package ge.ioane.speechcommander.voicecommanddetectors;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;

/**
 * This class listens to user after being activated.
 * Created by ioane5 on 2/26/17.
 */
class SpeechListener implements RecognitionListener {

    private static final String TAG = SpeechListener.class.getSimpleName();

    private SpeechCallback mListener;

    SpeechListener(SpeechCallback listener) {
        mListener = listener;
    }

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
        mListener.onNoCommand();
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
    public void onResults(@NonNull Bundle results) {
        StringBuilder sb = new StringBuilder();
        ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        // TODO
        for (int i = 0; i < data.size(); i++) {
            sb.append(data.get(i));
            sb.append('\n');
        }
        mListener.onSpeechFinished(sb.toString());
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

    interface SpeechCallback {
        // TODO might change to support multiple data
        void onSpeechFinished(String result);
        void onNoCommand();
    }
}
