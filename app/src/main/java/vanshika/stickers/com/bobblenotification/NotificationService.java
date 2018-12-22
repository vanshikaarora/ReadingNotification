package vanshika.stickers.com.bobblenotification;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class NotificationService extends AccessibilityService {
    public static TextToSpeech textToSpeech = null;
    public static String name,message;
    private static Context context;
    @Override
    protected void onServiceConnected() {
            System.out.println("onServiceConnected");
            AccessibilityServiceInfo info = new AccessibilityServiceInfo();
            info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
            //info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
            info.eventTypes=AccessibilityEvent.TYPES_ALL_MASK;
            info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
            info.notificationTimeout = 100;
            info.packageNames = null;
            setServiceInfo(info);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.v("accessibilityevent25",event.getEventType()+"");
        final AccessibilityNodeInfo source = event.getSource();
        /*if (source == null) {
            return;
        }*/
        if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            context=getApplicationContext();
            if (event.getPackageName().toString().equals("com.gbwhatsapp3")){
                StringBuilder message = new StringBuilder();
                if (!event.getText().isEmpty()) {
                    for (CharSequence subText : event.getText()) {
                        message.append(subText);
                    }
                    if (message.toString().contains("Message from")){
                        name=message.toString().substring(13);
                        tts(getApplication(),message.toString());
                        final Handler h =new Handler();

                        Runnable r = new Runnable() {

                            public void run() {
                                callIntent();
                            }
                        };

                        h.postDelayed(r, 3000);
                    }
                }
            }
        }
        if (event.getEventType()==AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED){
            if ((source.getPackageName().equals("com.whatsapp") || source.getPackageName().equals("com.gbwhatsapp3"))) {
                if (event.getEventType()==AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED){
                    AccessibilityNodeInfo currentNode=getRootInActiveWindow();
                    if (currentNode!=null && currentNode.getClassName().equals("android.widget.FrameLayout") && currentNode.getChild(2)!=null && currentNode.getChild(2).getClassName().equals("android.widget.TextView") && currentNode.getChild(2).getContentDescription().equals("Search")) {
                        currentNode.getChild(2).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        Bundle arguments = new Bundle();
                        arguments.putCharSequence(AccessibilityNodeInfo
                                .ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, name);//+91 91492 45760
                        AccessibilityNodeInfo editTextNode = getRootInActiveWindow();
                        if (editTextNode != null && editTextNode.getClassName().equals("android.widget.FrameLayout") && editTextNode.getChild(0) != null && editTextNode.getChild(0).getClassName().equals("android.support.v7.widget.an") && editTextNode.getChild(0).getChild(0) != null && editTextNode.getChild(0).getChild(0).getClassName().equals("android.widget.EditText"))
                            editTextNode.getChild(0).getChild(0).performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                    }
                        AccessibilityNodeInfo root = getRootInActiveWindow();
                        if (root!=null && root.getClassName().equals("android.widget.FrameLayout") && root.getChild(2)!=null && root.getChild(2).getClassName().equals("android.widget.ListView")){
                            if (root.getChild(2).getChild(1)!=null && root.getChild(2).getChild(1).getClassName().equals("android.widget.RelativeLayout")) {
                                root.getChild(2).getChild(1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                if (getRootInActiveWindow()!=null && getRootInActiveWindow().getChild(4)!=null && getRootInActiveWindow().getChild(4).getClassName().equals("android.widget.ImageButton") && getRootInActiveWindow().getChild(4).getContentDescription().equals("Send")){
                                    getRootInActiveWindow().getChild(4).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                }
                            }
                        }


                }
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
                            callIntent();
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

    }

}
