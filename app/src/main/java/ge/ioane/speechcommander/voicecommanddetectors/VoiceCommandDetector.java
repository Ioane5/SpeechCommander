package ge.ioane.speechcommander.voicecommanddetectors;

import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * This class handles Voice Command detection and give user callback with {@link VoiceCommandCallback}
 * Created by ioane5 on 2/26/17.
 */
public class VoiceCommandDetector implements SpeechListener.SpeechCallback, VoiceDetectorActivator.KeywordListener {

    private static final String TAG = VoiceCommandDetector.class.getSimpleName();

    private Context mContext;
    private VoiceCommandCallback mCommandCallback;

    private VoiceDetectorActivator mActivator;
    private SpeechRecognizer mSpeechRecognizer;

    public VoiceCommandDetector(@NonNull Context context, @NonNull VoiceCommandCallback commandCallback) {
        mContext = context;
        mCommandCallback = commandCallback;

        mActivator = new VoiceDetectorActivator("okay watson", mContext, this);
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(mContext);
        mSpeechRecognizer.setRecognitionListener(new SpeechListener(this));
    }

    private void activateNativeSpeechRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, mContext.getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        mSpeechRecognizer.startListening(intent);
    }

    @Override
    public void onSpeechFinished(String result) {
        Log.d(TAG, "onSpeechFinished() called with: result = [" + result + "]");
        // TODO parse and call correct command method with params.
        mActivator.startListening();
    }

    @Override
    public void onKeywordDetected() {
        Log.d(TAG, "onKeywordDetected() called");
        activateNativeSpeechRecognition();
        mActivator.stopListening();
    }

    public void onDestroy() {
        mSpeechRecognizer.destroy();
        // TODO add destroy handling
    }

    /**
     * User should add voice command callbacks here.
     */
    public interface VoiceCommandCallback {
        /**
         * Your command here
         **/
        void onMyCommand(String extras);
    }
}
