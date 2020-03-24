package com.example.tsukasa1260.speechrecognizer01;

import android.os.Build;
import android.os.Bundle;
import android.app.Activity;

import java.util.ArrayList;
import java.util.Locale;

import android.Manifest;
import android.content.pm.PackageManager;

import android.content.Intent;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * 音声入力（Input）と音声読み上げ（Output）のテスト。
 * マイクに入った音声を認識して，そのまま音声合成し，おうむ返しにスピーカ出力を試みる。
 * @author id:language_and_engineering
 *
 */
public class MainActivity extends Activity implements OnClickListener, TextToSpeech.OnInitListener
{
    // 音声入力用
    SpeechRecognizer sr;

    // 音声合成用
    TextToSpeech tts = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener( this );

        tts = new TextToSpeech(this, this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected  void onResume(){
        super.onResume();

        // RuntimePermissionの許可
        ArrayList<String> permissionList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }
        if(checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.RECORD_AUDIO);
        }

        if(!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            requestPermissions(permissions, 0);
        }
    }



    @Override
    public void onClick(View v)
    {
        // 音声認識APIに自作リスナをセット
        sr = SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(new MyRecognitionListener());

        // インテントを作成
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());


        // 入力言語のロケールを設定
        //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
        //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toString());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.JAPAN.toString());
        //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.CHINA.toString());

        // 音声認識APIにインテントを処理させる
        sr.startListening(intent);
    }



    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS) {
            // 音声合成の設定を行う

            float pitch = 1.0f; // 音の高低
            float rate = 1.0f; // 話すスピード
            Locale locale = Locale.JAPANESE; // 対象言語のロケール
            // ※ロケールの一覧表
            //   http://docs.oracle.com/javase/jp/1.5.0/api/java/util/Locale.html

            tts.setPitch(pitch);
            tts.setSpeechRate(rate);
            tts.setLanguage(locale);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if( tts != null )
        {
            // 破棄
            tts.shutdown();
        }
    }



    // 音声認識のリスナ
    class MyRecognitionListener implements RecognitionListener {

        @Override
        public void onBeginningOfSpeech() {
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
        }

        @Override
        public void onEndOfSpeech() {
        }

        @Override
        public void onError(int error) {
            Toast.makeText(getApplicationContext(), "エラー： " + error, Toast.LENGTH_LONG).show();
            // エラーコードの一覧表
            // http://developer.android.com/intl/ja/reference/android/speech/SpeechRecognizer.html#ERROR_AUDIO

            // 認識結果の候補が存在しなかった場合や，RECORD_AUDIOのパーミッションが不足している場合など
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
        }

        @Override
        public void onReadyForSpeech(Bundle params) {
            Toast.makeText(getApplicationContext(), "認識を開始します。", Toast.LENGTH_LONG).show();
        }


        @Override
        public void onResults(Bundle results) {
            // 結果を受け取る
            ArrayList<String> results_array = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            // 取得した文字列を結合
            String resultsString = "";

            /*for (int i = 0; i < results.size(); i++) {

                resultsString += results_array.get(i) + "。";
            }*/
            String s = results_array.get(0);

            // トーストで結果を表示
            Toast.makeText(getApplicationContext(), results_array.get(0), Toast.LENGTH_LONG).show();

            // 音声合成して発音
            if(tts.isSpeaking()) {
                tts.stop();
            }
            tts.speak(s, TextToSpeech.QUEUE_FLUSH, null);
        }


        @Override
        public void onRmsChanged(float rmsdB) {
        }

    }

}