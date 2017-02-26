package ge.ioane.speechcommander.voicecommanddetectors;


import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

/**
 * This activates Voice Command detector by listening to keyword continuously
 * Created by ioane5 on 2/26/17.
 */
class VoiceDetectorActivator {

    private static final String TAG = VoiceDetectorActivator.class.getSimpleName();
    private static final String KWS_SEARCH = "wakeup";

    private Context mContext;
    private KeywordListener mKeywordListener;
    private SpeechRecognizer mRecognizer;
    private String mKeyphrase = null;

    VoiceDetectorActivator(String keyphrase, Context context, KeywordListener keywordListener) {
        mKeyphrase = keyphrase;
        mContext = context;
        mKeywordListener = keywordListener;

        runRecognizerSetup();
    }

    void stopListening() {
        Log.d(TAG, "stopListening() called");
        mRecognizer.cancel();
    }

    void startListening() {
        Log.d(TAG, "startListening() called");
        // Start listening for keyword
        mRecognizer.startListening(KWS_SEARCH);
    }

    void onDestroy() {
        if (mRecognizer != null) {
            mRecognizer.cancel();
            mRecognizer.shutdown();
        }
    }

    private void runRecognizerSetup() {
        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task
        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(mContext);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                    Log.e(TAG, "onPostExecute: Failed to initialize");
                } else {
                    startListening();
                }
            }
        }.execute();
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them
        mRecognizer = SpeechRecognizerSetup.defaultSetup()
                // TODO test if really needed
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                .setKeywordThreshold(1e-40f)
                .setRawLogDir(assetsDir) // To disable logging of raw audio comment out this call (takes a lot of space on the device)
                .getRecognizer();

        RecognitionListener mRecognitionListener = new RecognizerListener();
        mRecognizer.addListener(mRecognitionListener);

        /** In your application you might not need to add all those searches.
         * They are added here for demonstration. You can leave just one.
         */

        // Create keyword-activation search.
        mRecognizer.addKeyphraseSearch(KWS_SEARCH, mKeyphrase);
    }

    private class RecognizerListener implements RecognitionListener {

        @Override
        public void onBeginningOfSpeech() {
            Log.d(TAG, "onBeginningOfSpeech() called");
        }

        @Override
        public void onEndOfSpeech() {
            Log.d(TAG, "onEndOfSpeech() called " + mRecognizer.getSearchName());
            if (!mRecognizer.getSearchName().equals(KWS_SEARCH)) {
                mRecognizer.stop();
                startListening();
            }
        }

        @Override
        public void onPartialResult(Hypothesis hypothesis) {
            Log.d(TAG, "onPartialResult() called with: hypothesis = [" + hypothesis + "]");
            if (hypothesis == null)
                return;
            Log.d(TAG, "onPartialResult: hypothesis " + hypothesis.getHypstr());
            if (TextUtils.equals(hypothesis.getHypstr(), mKeyphrase)) {
                mRecognizer.stop();
            }
        }

        @Override
        public void onResult(Hypothesis hypothesis) {
            Log.d(TAG, "onResult() called with: hypothesis = [" + hypothesis + "]");
            if (hypothesis != null && TextUtils.equals(hypothesis.getHypstr(), mKeyphrase)) {
                mKeywordListener.onKeywordDetected();
                mRecognizer.stop();
            }
        }

        @Override
        public void onError(Exception e) {
            Log.e(TAG, "onError: ", e);
        }

        @Override
        public void onTimeout() {
            startListening();
        }
    }

    interface KeywordListener {
        void onKeywordDetected();
    }
}
