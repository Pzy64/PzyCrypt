package pzy64.PzyCrypt.Pro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.design.widget.Snackbar;

import java.util.ArrayList;

/**
 * Created by prafuldev on 6/5/16.
 */
public class DecryptReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getStringExtra("ENC_FILENM") != null)
            MainActivity.currFile.setText(intent.getStringExtra("ENC_FILENM"));
        if (intent.getStringExtra("ENC_CNT") != null) {
            MainActivity.cryCount.setText(intent.getStringExtra("ENC_CNT"));
            MainActivity.cancel.show();
        }
        if (intent.getStringExtra("ENC_ERR_CNT") != null)
            MainActivity.errors.setText(intent.getStringExtra("ENC_ERR_CNT"));
        if (intent.getBooleanExtra("ENC_COMPLETE", false)) {
            Snackbar.make(MainActivity.xxx, "Completed !!", Snackbar.LENGTH_LONG).show();
            MainActivity.errsList.setEnabled(true);
            MainActivity.cancel.hide();
            MainActivity.working = false;
        }
        if (intent.getBooleanExtra("ENC_CANCEL", false)) {
            Snackbar.make(MainActivity.xxx, "Decryption cancelled by user !!", Snackbar.LENGTH_LONG).show();
            MainActivity.cancelled = false;
            MainActivity.cancel.hide();
            MediaPlayer m = MediaPlayer.create(DecryptServiceX.x, R.raw.err);
            m.start();
        }
        if (intent.getStringExtra("FILECOUNT") != null) {
            MainActivity.fileCount.setText(intent.getStringExtra("FILECOUNT"));
        }
        if (intent.getStringArrayListExtra("ENC_ERR_LIST") != null) {
            MainActivity.errs = new ArrayList<>();
            MainActivity.errs = intent.getStringArrayListExtra("ENC_ERR_LIST");
        }
    }
}
