package pzy64.PzyCrypt.Pro;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by prafuldev on 6/5/16.
 */
public class DecryptServiceX extends Service {
    static int cryCnt = 0, errCnt = 0;
    static Context x;

    static {
        System.loadLibrary("gnustl_shared");
        System.loadLibrary("pzy64_pzycryptpro");
    }

    ArrayList<String> files, errs;
    String password;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        files = intent.getStringArrayListExtra("LIST");
        password = intent.getStringExtra("PASSWORD");
        errs = new ArrayList<>();
        new CollectiveTask().execute();
        x = DecryptServiceX.this;
        return START_STICKY;
    }

    private native int Decrypt(String i, String o, String pass);

    @Override
    public void onDestroy() {
        errs.clear();
        files.clear();
        password = null;
        cryCnt = 0;
        errCnt = 0;
        super.onDestroy();
    }

    class ParallelDecrypt extends Thread {
        String in, pass;

        public ParallelDecrypt(String i, String p) {
            super();
            in = i;
            pass = p;
        }

        @Override
        public void run() {
            Intent i = new Intent().setAction("crypto.dec.FILECOUNT");
            i.putExtra("FILECOUNT", files.size() + "");
            sendBroadcast(i);
            String nf = in.concat(".pzy");
            if (Decrypt(in, nf, pass) == 0) {
                errs.add(in);
                new File(nf).delete();
                Intent y = new Intent().setAction("crypto.dec.Fail.REC");
                y.putExtra("ENC_ERR_CNT", "" + ++errCnt);
                sendBroadcast(y);
            } else {
                Intent x = new Intent().setAction("crypto.dec.REC");
                x.putExtra("ENC_CNT", "" + ++cryCnt);
                sendBroadcast(x);
                File f = new File(in);
                File fx = new File(nf);
                f.delete();
                fx.renameTo(f);
            }


        }
    }

    class CollectiveTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MainActivity.errsList.setEnabled(false);
            MainActivity.enc.setEnabled(false);
            MainActivity.dec.setEnabled(false);
            MainActivity.password.setEnabled(false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            ParallelDecrypt a = null;
            int NOTIF_ID = 2;
            PendingIntent pendingIntent;
            NotificationCompat.Builder builder;
            NotificationManager manager;
            pendingIntent = PendingIntent.getActivity(DecryptServiceX.this, NOTIF_ID, new Intent(DecryptServiceX.this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
            builder = new NotificationCompat.Builder(DecryptServiceX.this);
            builder.setSmallIcon(R.drawable.notifi);
            for (int i = 0; i < files.size(); i++) {
                if (!MainActivity.cancelled) {
                    if (i < files.size())
                        if (files.get(i) != null) {
                            Intent y = new Intent().setAction("crypto.dec.currFILE");
                            y.putExtra("ENC_FILENM", new File(files.get(i)).getName());
                            sendBroadcast(y);
                            builder.setContentTitle("Decrypting ..");
                            builder.setContentText(new File(files.get(i)).getName());
                            builder.setContentIntent(pendingIntent);
                            builder.setProgress(files.size(), i, false);
                            Notification notification = builder.build();
                            notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
                            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            manager.notify(NOTIF_ID, notification);
                            a = new ParallelDecrypt(files.get(i), password);
                            a.run();
                        }
                    try {
                        if (a != null)
                            a.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    builder.setContentTitle("Cancelled ..");
                    builder.setContentIntent(pendingIntent);
                    Notification notification = builder.build();
                    manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.notify(NOTIF_ID, notification);
                    Intent y = new Intent().setAction("crypto.dec.CANCEL");
                    y.putExtra("ENC_CANCEL", true);
                    sendBroadcast(y);
                    stopSelf();
                    break;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            cryCnt = 0;
            errCnt = 0;
            files.clear();
            MainActivity.errsList.setEnabled(false);
            MainActivity.enc.setEnabled(false);
            MainActivity.dec.setEnabled(false);
            MainActivity.password.setEnabled(false);
            if (!MainActivity.cancelled) {
                int NOTIF_ID = 2;
                PendingIntent pendingIntent;
                NotificationCompat.Builder builder;
                NotificationManager manager;
                pendingIntent = PendingIntent.getActivity(DecryptServiceX.this, NOTIF_ID, new Intent(DecryptServiceX.this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
                builder = new NotificationCompat.Builder(DecryptServiceX.this);
                builder.setSmallIcon(R.drawable.notifi);
                builder.setContentTitle("Completed ..");
                MediaPlayer player = MediaPlayer.create(DecryptServiceX.this, R.raw.completed);
                player.start();
                builder.setContentIntent(pendingIntent);
                Notification notification = builder.build();
                manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(NOTIF_ID, notification);
                Intent y = new Intent().setAction("crypto.dec.COMPLETE");
                y.putStringArrayListExtra("ENC_ERR_LIST", errs);
                y.putExtra("ENC_COMPLETE", true);
                sendBroadcast(y);

            }
            stopSelf();
        }
    }
}
