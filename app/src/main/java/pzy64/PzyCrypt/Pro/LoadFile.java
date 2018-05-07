package pzy64.PzyCrypt.Pro;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LoadFile extends AppCompatActivity {
    static short backCount = 0;
    LinearLayout xxx;
    ListView listView;
    FloatingActionButton fab;
    Files fs;
    Button back;
    TextView loc;
    File currentFolder;
    List<File> selectedItems;
    ArrayList<String> allSelectedItems;
    ArrayAdapter<String> arrayAdapter;
    int CheckedCnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_file);


        xxx = (LinearLayout) findViewById(R.id.xxx);
        listView = (ListView) findViewById(R.id.lv_loadfiles);
        back = (Button) findViewById(R.id.goback);
        loc = (TextView) findViewById(R.id.currentfolder);
        fs = new Files();
        fab = (FloatingActionButton) findViewById(R.id.fab_get_selected_files);
        currentFolder = Environment.getExternalStorageDirectory();
        selectedItems = new ArrayList<>();
        fab.hide();
        allSelectedItems = new ArrayList<>();
        Snackbar.make(xxx,"Long press to Select file or folder",Snackbar.LENGTH_INDEFINITE).show();
        Listit(currentFolder);
        CheckedCnt = 0;
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab.hide();
                selectedItems.clear();
                currentFolder = getOneMinusFile(currentFolder);
                Listit(currentFolder);
            }
        });
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listView.setItemChecked(position, false);
                if (fs.getFileAt(position).isDirectory()) {
                    selectedItems.clear();
                    fab.hide();
                    currentFolder = fs.getFileAt(position);
                    Listit(currentFolder);
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (!listView.isItemChecked(position)) {
                    listView.setItemChecked(position, true);
                    selectedItems.add(fs.getFileAt(position));
                } else {
                    listView.setItemChecked(position, false);
                    selectedItems.remove(fs.getFileAt(position));
                }
                CheckedCnt = listView.getCheckedItemCount();
                if (CheckedCnt > 0)
                    fab.show();
                else fab.hide();
                return true;
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File[] t = new File[selectedItems.size()];
                selectedItems.toArray(t);
                new Parallel_Loader(t).execute();
            }
        });
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

    void Listit(File f) {
        if (f.isDirectory()) {
            File[] xf = f.listFiles();
            if (xf != null) {
                loc.setText(f.getAbsolutePath());
                fs.setFiles(xf);
                arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, fs.getNames());
                listView.setAdapter(arrayAdapter);
            } else
                Snackbar.make(xxx, "Unable to open !!", Snackbar.LENGTH_SHORT).show();
        }
    }

    int findPath(String s) {
        int i, j;
        for (i = s.length() - 1, j = 0; j < s.length(); --i, j++) {
            if (s.charAt(i) == '/' && i != 0)
                return j + 1;
            if (i == 0) {
                return s.length() - 1;
            }
        }
        return s.length() - 1;
    }

    File getOneMinusFile(File f) {
        String path = f.getAbsolutePath();
        String res = path.substring(0, path.length() - findPath(path));
        return new File(res);
    }

    void RecurseThrough(File[] p) {
        for (File x : p) {
            if (x.isDirectory())
                RecurseThrough(x.listFiles());
            else if (x.isFile())
                allSelectedItems.add(x.getAbsolutePath());
        }
    }

    @Override
    public void onBackPressed() {
        if (!(currentFolder.getAbsolutePath().contentEquals(Environment.getExternalStorageDirectory().getAbsolutePath()) || "/".contentEquals(currentFolder.getAbsolutePath()))) {
            fab.hide();
            selectedItems.clear();
            currentFolder = getOneMinusFile(currentFolder);
            Listit(currentFolder);
        } else {
            if (backCount >= 1) {
                super.onBackPressed();
            }
            if (backCount == 0)
                Snackbar.make(xxx, "Press one more time to go back :)", Snackbar.LENGTH_SHORT).show();
            backCount++;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        backCount = 0;
    }

    class Files {
        List<File> files;
        List<String> names;

        public Files() {
            files = new ArrayList<>();
            names = new ArrayList<>();
        }

        public File[] getFiles() {
            File[] tmp = new File[files.size()];
            files.toArray(tmp);
            return tmp;
        }

        void setFiles(File[] f) {
            clear();
            for (File s : f) {
                files.add(s);
                names.add(s.getName());
            }
            sort();
        }

        public String[] getNames() {
            String t[] = new String[names.size()];
            names.toArray(t);
            return t;
        }

        void clear() {
            files.clear();
            names.clear();
        }

        void sort() {
            int sz = files.size();
            File tf;
            int i = 0, j;
            List<File> x = new ArrayList<>();
            File[] fs = new File[sz];

            for (File f : files) {
                fs[i] = f;
                i++;
            }

            for (i = 0; i < sz - 1; i++)
                for (j = 0; j < sz - i - 1; j++)
                    if (fs[j].getAbsolutePath().toLowerCase().compareTo(fs[j + 1].getAbsolutePath().toLowerCase()) > 0) {
                        tf = fs[j];
                        fs[j] = fs[j + 1];
                        fs[j + 1] = tf;
                    }

            for (i = 0; i < sz; i++)
                if (fs[i].isDirectory())
                    x.add(fs[i]);
            for (i = 0; i < sz; i++)
                if (fs[i].isFile())
                    x.add(fs[i]);

            clear();
            for (File s : x) {
                files.add(s);
                names.add(s.getName());
            }
        }

        File getFileAt(int pos) {
            return files.get(pos);
        }
    }

    class Parallel_Loader extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        File[] lst;

        public Parallel_Loader(File[] p) {
            lst = p;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(LoadFile.this);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setIndeterminate(true);
            pd.setCanceledOnTouchOutside(false);
            pd.setMessage("loading files...");
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            RecurseThrough(lst);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pd.dismiss();
            Bundle b = new Bundle();
            ArrayList<String> xx = checkAndChange(allSelectedItems);
            for (String a : xx)
                Log.d("MY STR", a);
            b.putStringArrayList("DATA", xx);
            Intent i = new Intent();
            i.putExtras(b);
            setResult(RESULT_OK, i);
            finish();
        }
    }

}
