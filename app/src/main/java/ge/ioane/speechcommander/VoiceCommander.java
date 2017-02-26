package ge.ioane.speechcommander;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by ioane5 on 2/26/17.
 */

public class VoiceCommander {

    private static final String TAG = VoiceCommander.class.getSimpleName();
    private Context mContext;
    private SpeechRecognizer speechRecognizer;

    public VoiceCommander(Context context) {
        mContext = context;

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(mContext);
        speechRecognizer.setRecognitionListener(new NativeSpeechListener());
    }

    private class NativeSpeechListener implements RecognitionListener {

        @Override
        public void onReadyForSpeech(Bundle params) {
            Log.d(TAG, "onReadyForSpeech() called with: params = [" + params + "]");
        }

        @Override
        public void onBeginningOfSpeech() {
            System.out.println("Speech beginning");
        }

        /**
         * The sound level in the audio stream has changed. There is no guarantee that this method will
         * be called.
         *
         * @param rmsdB the new RMS dB value
         */
        @Override
        public void onRmsChanged(float rmsdB) {
            Log.d(TAG, "onRmsChanged() called with: rmsdB = [" + rmsdB + "]");
        }

        /**
         * More sound has been received. The purpose of this function is to allow giving feedback to the
         * user regarding the captured audio. There is no guarantee that this method will be called.
         *
         * @param buffer a buffer containing a sequence of big-endian 16-bit integers representing a
         *               single channel audio stream. The sample rate is implementation dependent.
         */
        @Override
        public void onBufferReceived(byte[] buffer) {
            Log.d(TAG, "onBufferReceived() called with: buffer = [" + buffer + "]");
        }

        /**
         * Called after the user stops speaking.
         */
        @Override
        public void onEndOfSpeech() {
            Log.d(TAG, "onEndOfSpeech() called");
        }

        /**
         * A network or recognition error occurred.
         *
         * @param error code is defined in {@link SpeechRecognizer}
         */
        @Override
        public void onError(int error) {
            Log.d(TAG, "onError() called with: error = [" + error + "]");
        }

        /**
         * Called when recognition results are ready.
         *
         * @param results the recognition results. To retrieve the results in {@code
         *                ArrayList<String>} format use {@link Bundle#getStringArrayList(String)} with
         *                {@link SpeechRecognizer#RESULTS_RECOGNITION} as a parameter. A float array of
         *                confidence values might also be given in {@link SpeechRecognizer#CONFIDENCE_SCORES}.
         */
        @Override
        public void onResults(Bundle results) {
            StringBuilder sb = new StringBuilder();
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for (int i = 0; i < data.size(); i++) {
                sb.append(data.get(i));
                sb.append('\n');
            }
            Toast.makeText(mContext, "Speech " + sb.toString(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onResults() called with: results = [" + sb.toString() + "]");
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            Log.d(TAG, "onPartialResults() called with: partialResults = [" + partialResults + "]");
        }

        /**
         * Reserved for adding future events.
         *
         * @param eventType the type of the occurred event
         * @param params    a Bundle containing the passed parameters
         */
        @Override
        public void onEvent(int eventType, Bundle params) {
            Log.d(TAG, "onEvent() called with: eventType = [" + eventType + "], params = [" + params + "]");
        }
    }

    private void activateNativeSpeechRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, mContext.getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        speechRecognizer.startListening(intent);
    }


    public interface VoiceCommandCallbacks {
        /**
         * Your command here
         **/
        void onMyCommand(String extras);
    }
}
