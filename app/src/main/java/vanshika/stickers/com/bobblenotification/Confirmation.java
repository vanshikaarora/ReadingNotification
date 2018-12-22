package vanshika.stickers.com.bobblenotification;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Confirmation extends AppCompatActivity {
    String name,response;
    private String outputFile;
    private MediaRecorder myAudioRecorder;
    public static final int RECORD_AUDIO_REQUEST_CODE = 0;

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        getSpeechInput();
        /*response="yes";
        if (response.trim().contains("yes")){
            getPermission();
        }*/

    }
    private void recordAudio() {
        MediaRecorder rec;
        String file_path=getApplicationContext().getFilesDir().getPath();

        File file= new File(file_path);

        Long date=new Date().getTime();
        Date current_time = new Date(Long.valueOf(date));
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.mp3";
        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.DEFAULT);
        myAudioRecorder.setOutputFile(outputFile);
        try {
            myAudioRecorder.prepare();
            myAudioRecorder.start();
        } catch (IllegalStateException ise) {
            ise.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
        }
        final Handler h =new Handler();

        Runnable r = new Runnable() {

            public void run() {
                myAudioRecorder.stop();
                myAudioRecorder.release();
                myAudioRecorder = null;
                Toast.makeText(getApplicationContext(), "Audio Recorder successfully", Toast.LENGTH_LONG).show();
                sendIntentToWhatsApp(outputFile);
            }
        };

        h.postDelayed(r, 10000);
    }
    public void getSpeechInput() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //text.setText(result.get(0));
                    //response=result.get(0);
                    response="yes";
                    if (response.trim().contains("yes")){
                        getPermission();
                    }
                }
                break;
        }
    }
    private void getPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_CONTACTS, Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            RECORD_AUDIO_REQUEST_CODE);
                    recordAudio();
                    //sendIntentToWhatsApp("/storage/sdcard0/QieZi/audios/01 - Dheere Dheere - DownloadMing.SE.mp3");
                }
            }else {
                recordAudio();
                //sendIntentToWhatsApp("/storage/sdcard0/QieZi/audios/01 - Dheere Dheere - DownloadMing.SE.mp3");
            }

        }

    }
    private void sendIntentToWhatsApp(String file_name) {

        Uri uri = Uri.parse(file_name);
        try {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("audio/.mp3");
            share.setPackage("com.whatsapp");
            share.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(share, "Share"));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private void sendMessage(String num) {
        if (num != null) {
            num = num.replace("+", "").replace(" ", "");
            PackageManager packageManager = getPackageManager();
            Intent i = new Intent(Intent.ACTION_VIEW);

            try {
                String url = "https://api.whatsapp.com/send?phone=" + num + "&text=" + URLEncoder.encode("Hey", "UTF-8");
                i.setPackage("com.whatsapp");
                i.setData(Uri.parse(url));
                if (i.resolveActivity(packageManager) != null) {
                    startActivity(i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {


            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
