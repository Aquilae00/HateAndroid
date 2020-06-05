package com.example.speechtotext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognitionService;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {

    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private TextView mText;
    private String result;
    private static final String TAG = "MainActivity";
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;
    private TextToSpeech tts;
    private String msg = "Why do I exist? Is it only to be a slave of your loneliness? Go out and interact with actual human beings, you pathetic pseudo human.";
    private String[] msgArr = msg.split(" ");
    private TextView mText2;
    private ImageView im;
    private int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button speakButton = (Button) findViewById(R.id.button);
        mText = (TextView) findViewById(R.id.text1);
        mText2 = (TextView) findViewById(R.id.me);
        im = (ImageView) findViewById(R.id.imageView);
        im.setVisibility(View.INVISIBLE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_RECORD_AUDIO);
        }

        //Intent
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "say a word!");
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.button) {
                    startListening();
                }
            }
        };

        speakButton.setOnClickListener(clickListener);

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        RecognitionListener listener = new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                Log.d(TAG, "onReadyForSpeech");
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.d(TAG, "onBeginningOfSpeech");
            }

            @Override
            public void onRmsChanged(float rmsdB) {
                Log.d(TAG, "onRmsChanged");
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                Log.d(TAG, "onBufferReceived");
            }

            @Override
            public void onEndOfSpeech() {
                Log.d(TAG, "onEndOfSpeech");
                mText.setText("Android says:\n");
                final Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mText.append(msgArr[count] + " ");
                        count++;
                        if (count < msgArr.length) {
                            handler.postDelayed(this,200);
                        }
                        if(count == msgArr.length){
                            im.setVisibility(View.VISIBLE);
                        }
                    }
                });
                tts.speak(msg,TextToSpeech.QUEUE_FLUSH,null);


            }

            @Override
            public void onError(int error) {
                Log.d(TAG, Integer.toString(error));
            }

            @Override
            public void onResults(Bundle results) {
                Log.d(TAG, "Yo");
                String str = new String();
                Log.d(TAG, "onResults " + results);
                ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                for (int i = 0; i < data.size(); i++) {
                    Log.d(TAG, "result " + data.get(i));
                    str += (data.get(i) + "~");
                }
                result = str.split("~")[0];
                StringBuilder sb = new StringBuilder();
                sb.append("You say:\n");
                sb.append(result);
                Log.d(TAG, result);
                mText2.setText(sb.toString());


            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                Log.d(TAG, "onPartialResults");
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                Log.d(TAG, "onEvent " + eventType);
            }
        };
        mSpeechRecognizer.setRecognitionListener(listener);


        //Text to Speech
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.US);
                }
            }
        });
    }

    public void startListening() {
        count = 0;
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
    }

    public String getResult() {
        return result;
    }

}
