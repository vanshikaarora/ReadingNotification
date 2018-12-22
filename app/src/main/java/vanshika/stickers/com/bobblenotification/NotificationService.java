package vanshika.stickers.com.bobblenotification;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

public class NotificationService extends AccessibilityService {
    public static TextToSpeech textToSpeech = null;
    public static String name;
    private static Context context;
    @Override
    protected void onServiceConnected() {
            System.out.println("onServiceConnected");
            AccessibilityServiceInfo info = new AccessibilityServiceInfo();
            info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
            info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
            info.notificationTimeout = 100;
            info.packageNames = null;
            setServiceInfo(info);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.v("accessibilityevent25",event.getEventType()+"");
        if (event.getEventType()==AccessibilityEvent.TYPE_WINDOWS_CHANGED){
            Log.v("line27",AccessibilityEvent.CONTENT_CHANGE_TYPE_CONTENT_DESCRIPTION+"");
        }
        if (!(event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED)) {
            return;
        }
        context=getApplicationContext();
        name=event.getText().toString();
        name=name.substring(name.lastIndexOf(" "));
        if (event.getPackageName().toString().equals("com.gbwhatsapp3")){
            StringBuilder message = new StringBuilder();
            if (!event.getText().isEmpty()) {
                for (CharSequence subText : event.getText()) {
                    message.append(subText);
                }tts(getApplication(),message.toString());

            }

        }



    }

    @Override
    public void onInterrupt() {
        System.out.println("onInterrupt");
    }
    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }
    public static void tts(final Context c, final String txt) {

        if (textToSpeech == null) {
            textToSpeech = new TextToSpeech(c, new TextToSpeech.OnInitListener() {

                public void onInit(int arg0) {
                    if (arg0 == TextToSpeech.SUCCESS) {
                        //callIntent();
                        ttsOnInit(c, txt);
                    }
                }
            });
            //http://developer.android.com/reference/android/speech/tts/TextToSpeech.html
            if (android.os.Build.VERSION.SDK_INT > 15) {
                textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {

                    @Override
                    public void onStart(String arg0) {
                        //do nothing
                    }

                    @Override
                    public void onDone(String arg0) {
                        try {
                            textToSpeech.shutdown();
                            textToSpeech = null;
                            //callIntent();
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onError(String arg0) {
                        try {
                            textToSpeech.shutdown();
                            textToSpeech = null;
                        } catch (Exception e) {
                        }
                    }
                });
               //callIntent();
            } else {
                textToSpeech.setOnUtteranceCompletedListener(new TextToSpeech.OnUtteranceCompletedListener() {

                    public void onUtteranceCompleted(String utteranceId) {
                        try {
                            textToSpeech.shutdown();
                            textToSpeech = null;
                            //callIntent();
                        } catch (Exception e) {
                        }
                    }
                });
            }
        } else {
            ttsOnInit(c, txt);
        }
       /* final Handler h =new Handler();

        Runnable r = new Runnable() {

            public void run() {

                if (!textToSpeech.isSpeaking()) {
                    callIntent();
                }

                h.postDelayed(this, 1000);
            }
        };

        h.postDelayed(r, 10000);*/
    }

    private static void callIntent() {
        Intent intent = new Intent(context, Confirmation.class);
        intent.putExtra("name",name);
        context.startActivity(intent);
    }

    private static void ttsOnInit(Context c, String txt) {
        if (textToSpeech != null) {
            textToSpeech.speak(txt, TextToSpeech.QUEUE_ADD, null);
        } else {
            tts(c, txt);
        }
        final Handler h =new Handler();

        Runnable r = new Runnable() {

            public void run() {

                if (!textToSpeech.isSpeaking()) {
                    callIntent();
                }

                h.postDelayed(this, 1000);
            }
        };

        h.postDelayed(r, 1000);

    }

}
