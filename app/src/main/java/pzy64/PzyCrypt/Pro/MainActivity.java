package pzy64.PzyCrypt.Pro;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnLongClickListener {
    public static TextView cryCount, fileCount, errors, currFile;
    static Context c;
    static Button errsList;
    static EditText password;
    static Button enc, dec, vie;
    static CoordinatorLayout xxx;
    static short backCount = 0;
    static FloatingActionButton fab, cancel;
    static boolean cancelled = false, working = false;
    static List<String> errs;
    ArrayList<String> files;
    boolean shown = false;

    public MainActivity() {
        c = MainActivity.this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cryCount = (TextView) findViewById(R.id.enc_completed);
        fileCount = (TextView) findViewById(R.id.file_captured);
        errors = (TextView) findViewById(R.id.errs);
        currFile = (TextView) findViewById(R.id.currFileName);
        password = (EditText) findViewById(R.id.pass);
        errsList = (Button) findViewById(R.id.errsList);
        enc = (Button) findViewById(R.id.enc);
        dec = (Button) findViewById(R.id.dec);
        vie = (Button) findViewById(R.id.vie);
        xxx = (CoordinatorLayout) findViewById(R.id.xxx);
        cancel = (FloatingActionButton) findViewById(R.id.cancel_operation);
        fab = (FloatingActionButton) findViewById(R.id.open_files_fab);
        password.setEnabled(false);
        errsList.setEnabled(false);
        enc.setEnabled(false);
        dec.setEnabled(false);
        vie.setEnabled(false);
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.INTERNET,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        Snackbar.make(xxx,"Start by loading a file or folder !",Snackbar.LENGTH_LONG).show();
        if (!working)
            cancel.hide();
        setUp();
        files = new ArrayList<>();
        Intent shareIntent = getIntent();
        String action = shareIntent.getAction();
        if (action != null) {
            if (Intent.ACTION_SEND.contentEquals(action)) {
                files.add(MyContentProvider.getPath(this, (Uri) shareIntent.getParcelableExtra(Intent.EXTRA_STREAM)));
                files = checkAndChange(files);
                fileCount.setText(files.size() + "");
                password.setEnabled(true);
                enc.setEnabled(true);
                errsList.setEnabled(false);
                vie.setEnabled(true);
                dec.setEnabled(true);
            } else if (Intent.ACTION_SEND_MULTIPLE.contentEquals(action)) {
                new GatherFiles(shareIntent, this).execute();
            }
        }
        vie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shown) {
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    shown = false;

                } else {
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    shown = true;
                }
            }
        });
        enc.setOnLongClickListener(this);
        dec.setOnLongClickListener(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, LoadFile.class), 3);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(xxx, "Cancelling... please wait ", Snackbar.LENGTH_LONG).show();
                cancelled = true;
            }
        });
        errsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(3);
            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog d = new Dialog(MainActivity.this);
        d.setContentView(R.layout.err_dialog_ui);
        d.setTitle("ERRORS");
        ListView lv = (ListView) d.findViewById(R.id.errs);
        ArrayAdapter<String> st = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, errs);
        lv.setAdapter(st);
        return d;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    finish();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    @Override
    public boolean onLongClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.enc:
                cryCount.setText("0");
                errors.setText("0");
                if (password.getText().toString().contentEquals(""))
                    Snackbar.make(xxx, "Please enter password first!!", Snackbar.LENGTH_SHORT).show();
                else {
                    working = true;
                    Snackbar.make(xxx, "Encrypting files ... please wait ", Snackbar.LENGTH_LONG).show();
                    cancel.show();
                    intent = new Intent(MainActivity.this, EncryptServiceX.class);
                    intent.putExtra("PASSWORD", password.getText().toString());
                    intent.putExtra("LIST", files);
                    startService(intent);
                }
                break;
            case R.id.dec:

                cryCount.setText("0");
                errors.setText("0");
                if (password.getText().toString().contentEquals(""))
                    Snackbar.make(xxx, "Please enter password first!!", Snackbar.LENGTH_SHORT).show();
                else {
                    working = true;
                    Snackbar.make(xxx, "Decrypting files ... please wait ", Snackbar.LENGTH_LONG).show();
                    cancel.show();
                    intent = new Intent(MainActivity.this, DecryptServiceX.class);
                    intent.putExtra("PASSWORD", password.getText().toString());
                    intent.putExtra("LIST", files);
                    startService(intent);
                }
        }
        return true;
    }

    ArrayList<String> checkAndChange(ArrayList<String> str) {
        ArrayList<String> al = new ArrayList<>();
        for (String s : str) {
            File f = new File(s);
            char[] ss = f.getName().toCharArray();
            char[] nw = new char[ss.length];
            for (int i = 0; i < ss.length; i++) {
                if (ss[i] >= 32 && ss[i] <= 46 || ss[i] >= 48 && ss[i] <= 57 || ss[i] >= 59 && ss[i] <= 91 || ss[i] >= 97 && ss[i] <= 126)
                    nw[i] = ss[i];
                else
                    nw[i] = '_';
            }
            String x = f.getAbsolutePath().substring(0, f.getAbsolutePath().length() - nw.length) + String.copyValueOf(nw);
            f.renameTo(new File(x));
            al.add(x);
        }
        return al;
    }

    private void setUp() {
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        tb.setTitle(R.string.app_name);
        setSupportActionBar(tb);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            cryCount.setText("0");
            errors.setText("0");
            files = data.getExtras().getStringArrayList("DATA");
            fileCount.setText(files.size() + "");
            if (files.size() > 0) {
                password.setEnabled(true);
                vie.setEnabled(true);
                errsList.setEnabled(false);
                enc.setEnabled(true);
                dec.setEnabled(true);
            } else {
                password.setEnabled(false);
                vie.setEnabled(false);
                errsList.setEnabled(false);
                enc.setEnabled(false);
                dec.setEnabled(false);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.About:
                startActivity(new Intent(MainActivity.this, About.class));
                break;
            case R.id.Exit:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (backCount >= 1) {
            super.onBackPressed();
        }
        backCount++;
        Snackbar.make(xxx, "Press one more time to go back :)", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (files.size() > 0)
            fileCount.setText(files.size() + "");
        backCount = 0;
    }

    class GatherFiles extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context context;
        Intent intent;

        public GatherFiles(Intent in, Context c) {
            intent = in;
            context = c;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.this);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setIndeterminate(true);
            pd.setCanceledOnTouchOutside(false);
            pd.setMessage("loading files...");
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<Uri> l = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            for (Uri x : l)
                files.add(MyContentProvider.getPath(context, x));
            files = checkAndChange(files);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            fileCount.setText(files.size() + "");
            password.setEnabled(true);
            errsList.setEnabled(true);
            enc.setEnabled(true);
            vie.setEnabled(true);
            pd.dismiss();
            dec.setEnabled(true);
        }
    }
}
