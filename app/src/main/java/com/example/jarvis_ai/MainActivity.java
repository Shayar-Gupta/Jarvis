package com.example.jarvis_ai;
import static com.example.jarvis_ai.Functions.wishMe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private SpeechRecognizer recognizer;
    private TextView textView;
    private MediaPlayer player;
    private TextToSpeech tts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Dexter
        .withContext(this) // 'this' refers to the context of your Activity
        .withPermission(Manifest.permission.RECORD_AUDIO)
        .withListener(new PermissionListener() {
            @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                // Permission granted, you can proceed with your logic here
            }
            @Override public void onPermissionDenied(PermissionDeniedResponse response) {
                // Permission denied, handle this situation (e.g., show a message to the user)
                System.exit(0);
            }
            @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                // This is called if the user needs to be shown a rationale for the permission
                // You can show a rationale dialog and call 'token.continuePermissionRequest()' to continue the request
                token.continuePermissionRequest();
            }
        }).check();
        initTextToSpeech();
        findbyid();
        result();
    }

    private void initTextToSpeech(){
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(tts.getEngines().size() == 0) {
                    Toast.makeText(MainActivity.this, "Engine is not available", Toast.LENGTH_SHORT).show();
                }
                else{
                    String s = wishMe();
                    speak("Hi, I'm Jarvis AI." + s);
                }
            }
        });
    }

    private void speak(String msg){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(msg, TextToSpeech.QUEUE_FLUSH, null, null);
        }
        else{
            tts.speak(msg, TextToSpeech.QUEUE_FLUSH,null);
        }
        }
    private void findbyid() {
        textView = (TextView) findViewById(R.id.textView);
    }

    private void result(){
        if(SpeechRecognizer.isRecognitionAvailable(this)){
            recognizer = SpeechRecognizer.createSpeechRecognizer(this);
            recognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle bundle) {

                }

                @Override
                public void onBeginningOfSpeech() {

                }

                @Override
                public void onRmsChanged(float v) {

                }

                @Override
                public void onBufferReceived(byte[] bytes) {

                }

                @Override
                public void onEndOfSpeech() {

                }

                @Override
                public void onError(int i) {

                }

                @Override
                public void onResults(Bundle bundle) {
                    ArrayList<String> result = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    Toast.makeText(MainActivity.this," " + result.get(0), Toast.LENGTH_SHORT).show();
                    textView.setText(result.get(0));
                    response(result.get(0));
                }

                @Override
                public void onPartialResults(Bundle bundle) {

                }

                @Override
                public void onEvent(int i, Bundle bundle) {

                }
            });
        }
    }

    private void response(String msg){
        String msgs = msg.toLowerCase(Locale.ROOT);
        if(msgs.contains("hello")){
            speak("Hello Sir, Jarvis at your service. Please tell Me how can i help you");
        }
        else if(msg.contains("time")){
            Date date = new Date();
            String time = DateUtils.formatDateTime(this,date.getTime(),DateUtils.FORMAT_SHOW_TIME);
            speak(time);
        }
        else if(msgs.contains("date")){
            SimpleDateFormat dt = new SimpleDateFormat("dd mm yyyy");
            Calendar cal = Calendar.getInstance();
            String todays_Date = dt.format(cal.getTime());
            speak("Today's date is" + todays_Date);
        }
        else if(msgs.contains("google")){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
            startActivity(intent);
        }
        else if(msgs.contains("youtube")){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com"));
            startActivity(intent);
        }
        else if(msgs.contains("instagram")){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com"));
            startActivity(intent);
        }
        else if(msgs.contains("whatsapp")){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.whatsapp.com"));
            startActivity(intent);
        }
        //hey there
        else if(msgs.contains("search")){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + msgs.replace("search"," ")));
            startActivity(intent);
        }
        else if(msgs.contains("remember")){
            speak("okay sir, I will remember it for you");
            writeToFile(msgs.replace("jarvis remember that", " "));
        }
        else if(msgs.contains("know")){
            String data = readToFile();
            speak("Yes sir, you told me to remember that " + data);
        }
        else if(msgs.contains("play")){
            play();
        }
        else if(msgs.contains("pause")){
            pause();
        }
        else if(msgs.contains("stop")){
            stop();
        }
        else{
            speak("Sorry Sir, try using below keyword");
        }
    }

    private void stop() {
        stopPlayer();
    }

    private void pause() {
        if(player != null){
            player.pause();
        }
    }

    private void play() {
        if(player == null){
            player = MediaPlayer.create(this, R.raw.song);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlayer();
                }
            });
        }
        player.start();
    }

    private void stopPlayer() {
        if(player!= null){
            player.release();
            player = null;
            Toast.makeText(this, "Media Player Released", Toast.LENGTH_SHORT).show();
        }
    }

    private String readToFile() {
        String ret = " ";
        try{
            InputStream inputStream = openFileInput("data.txt");
            if(inputStream != null){
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String Receivestr = " ";
                StringBuilder stringBuilder = new StringBuilder();

                while((Receivestr = bufferedReader.readLine()) != null){
                    stringBuilder.append("\n").append(Receivestr);
                }
                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch(FileNotFoundException e){
            Log.e("Exception","File not found" + e.toString());

        }
        catch (IOException e){
            Log.e("Exception", "cannot read file" + e.toString());
        }
        return ret;
    }

    private void writeToFile(String data) {
        try{
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("data.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e){
            Log.e("Exception","File Write Failed" +e.toString());
        }
    }

    public void startRecording(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);

        recognizer.startListening(intent);
    }
}