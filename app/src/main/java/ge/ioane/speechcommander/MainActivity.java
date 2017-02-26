package ge.ioane.speechcommander;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import ge.ioane.speechcommander.voicecommanddetectors.VoiceCommandDetector;

public class MainActivity extends AppCompatActivity implements VoiceCommandDetector.VoiceCommandCallback {

    private static final String TAG = MainActivity.class.getSimpleName();

    private VoiceCommandDetector mCommandDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCommandDetector = new VoiceCommandDetector(this, this);
    }

    /**
     * Your command here
     *
     * @param extras
     */
    @Override
    public void onMyCommand(String extras) {
        Log.d(TAG, "onMyCommand() called with: extras = [" + extras + "]");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCommandDetector.onDestroy();
    }
}
