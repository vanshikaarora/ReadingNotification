package vanshika.stickers.com.bobblenotification;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.Notification;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.RequiresApi;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class NotificationService extends AccessibilityService {
    public static TextToSpeech textToSpeech = null;
    public static String name,notificationMessage;
    private static Context context;
    private ArrayList<String> readMsgs;
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
            readMsgs=new ArrayList<>();
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

                        ExecutorService executor = Executors.newFixedThreadPool(1);
                            Runnable worker = new WorkerThread(event);
                            executor.execute(worker);
                        executor.shutdown();
                        while (!executor.isTerminated()) {
                        }
                        System.out.println("Finished all threads");



                    }


    try{

    if (event.getEventType()==AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED){
        if (source!=null && (source.getPackageName().equals("com.whatsapp") || source.getPackageName().equals("com.gbwhatsapp3")) && Confirmation.ACTION_SEND) {
            if (event.getEventType()==AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED){
                AccessibilityNodeInfo currentNode=getRootInActiveWindow();
                if (currentNode!=null && currentNode.getClassName().equals("android.widget.FrameLayout") && currentNode.getChild(2)!=null && currentNode.getChild(2).getClassName().equals("android.widget.TextView") && currentNode.getChild(2).getContentDescription().equals("Search")) {
                    currentNode.getChild(2).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    Bundle arguments = new Bundle();
                    arguments.putCharSequence(AccessibilityNodeInfo
                            .ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, name);
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
                            Confirmation.ACTION_SEND=false;
                        }
                    }
                }

            }
        }
    }
    }catch (NullPointerException e){
            Toast.makeText(context, "We have recieved an error from whatsapp end",Toast.LENGTH_SHORT).show();
        sendBroadcast(new Intent("xyz"));
    }
    catch (Exception e){
        Toast.makeText(context,"Sorry, There was an error in sending the message",Toast.LENGTH_SHORT).show();
        PackageInfo pinfo = null;
        try {
            pinfo = context.getPackageManager().getPackageInfo( "com.whatsapp", 0 );
        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
        }
        ((Activity)context).finish();
        String verName = pinfo.versionName;
        int verCode = pinfo.versionCode;
        sendBroadcast(new Intent("xyz"));

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
                        Log.v("147",arg0+"");
                        while (textToSpeech.isSpeaking()){
                            Log.v("line79",textToSpeech.isSpeaking()+"");
                        }
                        if (!textToSpeech.isSpeaking()){
                            callIntent();
                            Log.v("line83",textToSpeech.isSpeaking()+"");
                        }
                    }
                }
            });
            //http://developer.android.com/reference/android/speech/tts/TextToSpeech.html
            if (android.os.Build.VERSION.SDK_INT > 15) {Log.v("line152","y");
                textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {

                    @Override
                    public void onStart(String arg0) {
                        Log.v("157",arg0);
                    }

                    @Override
                    public void onDone(String arg0) {
                        try {
                            textToSpeech.shutdown();
                            textToSpeech = null;
                            Log.v("165",arg0);
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
            Log.v("195",txt);
        }


    }

    private static void callIntent() {
        Intent intent = new Intent(context, Confirmation.class);
        intent.putExtra("name",name);
        context.startActivity(intent);
    }

    private static void ttsOnInit(Context c, String txt) {
        if (textToSpeech != null) {Log.v("208",txt);
            textToSpeech.speak(txt, TextToSpeech.QUEUE_ADD, null);
            while (textToSpeech.isSpeaking()){
                Log.v("line79",textToSpeech.isSpeaking()+"");
            }
            if (!textToSpeech.isSpeaking()){
                callIntent();
                Log.v("line83",textToSpeech.isSpeaking()+"");
            }
        } else {
            tts(c, txt);Log.v("211",txt);
        }

    }
    public class WorkerThread implements Runnable {

        private AccessibilityEvent event;

        public WorkerThread(AccessibilityEvent s){

            this.event=s;
        }

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + " Start. Command = " + event);
            context = getApplicationContext();
            if (event.getPackageName().toString().equals("com.gbwhatsapp3")) {//
                StringBuilder message = new StringBuilder();
                if (!event.getText().isEmpty()) {
                    Parcelable data = event.getParcelableData();
                    Notification notification = (Notification) data;
                    Log.d("Tortuga", "ticker: " + notification.tickerText);
                    Log.d("Tortuga", "icon: " + notification.icon);
                    Log.d("Tortuga", "notification: " + event.getText());
                    for (CharSequence subText : event.getText()) {
                        message.append(subText);
                    }
                    if (message.toString().contains("Message from")) {
                        name = message.toString().substring(13);
                        notificationMessage = name + " Sent you ";

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            if (notification.extras.getCharSequenceArray("android.textLines") != null) {
                                String msgList = "";
                                for (int i = 0; i < notification.extras.getCharSequenceArray("android.textLines").length; i++) {
                                    String msg = Arrays.asList(notification.extras.getCharSequenceArray("android.textLines")).get(i).toString();
                                    String temp="";
                                    if (msg.contains(":")){
                                        temp=msg.substring(0,msg.indexOf(":"));int c=0;
                                        for(int j=0;j<temp.length();j++){
                                            if (temp.charAt(j)=='@') c++;
                                            if (c>=2)
                                                return;
                                        }
                                    }
                                    if(!(msg.indexOf(":")==msg.lastIndexOf(":"))){
                                        return;
                                    }
                                    if (msg.contains(":")){
                                        String t=msg.substring(0,msg.indexOf(":"));
                                        if (t.contains("Voice message (") && msg.lastIndexOf(")")==msg.length()-1){
                                            msgList="A voice message";
                                        } else
                                        if (isContact(msg.substring(0,msg.indexOf(":"))) || msg.substring(0,msg.indexOf(":")).trim().equals(name)){
                                            msgList = msgList + msg.substring(msg.indexOf(":"))      + " ";
                                        }
                                    }
                                    else {
                                        msgList = msgList + msg + " ";
                                    }
                                    if (readMsgs.contains(msg)) {
                                        msgList = "";
                                    } else {
                                        readMsgs.add(msg);
                                    }
                                }
                                notificationMessage = notificationMessage + msgList;
                            } else {
                                String msg = notification.extras.getString(Notification.EXTRA_TEXT);
                                readMsgs.add(msg);
                                notificationMessage = notificationMessage + msg;
                            }
                        }
                        if (name.contains("@")){
                            name=name.substring(name.indexOf("@")).trim();
                        }
                        KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                        if (myKM.inKeyguardRestrictedInputMode()) {
                            String info = "Please unlock the phone to reply";
                            notificationMessage = notificationMessage + info;
                        }
                        tts(getApplication(), notificationMessage);
                        System.out.println(Thread.currentThread().getName() + " End.");
                    }
                }
            }
        }

        private boolean isContact(String substring) {
            if (isPhone(substring.replace(" ","").replace("+","")) || inContactList(substring)){
                return true;
            }
                return false;
        }

        private boolean inContactList(String s) {
            final String[] projection = {
                    ContactsContract.Data.CONTACT_ID,
                    ContactsContract.Data.DISPLAY_NAME,
                    ContactsContract.Data.MIMETYPE,
                    "account_type",
                    ContactsContract.Data.DATA3,
            };

            final String selection = ContactsContract.Data.MIMETYPE + " =? and account_type=?";
            final String[] selectionArgs = {
                    "vnd.android.cursor.item/vnd.com.whatsapp.profile",
                    "com.whatsapp"
            };

            ContentResolver cr = getContentResolver();
            Cursor c = cr.query(
                    ContactsContract.Data.CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    null);
            while (c.moveToNext()) {
                String id = c.getString(c.getColumnIndex(ContactsContract.Data.CONTACT_ID));
                String number = c.getString(c.getColumnIndex(ContactsContract.Data.DATA3));
                String name1 = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                Log.v("WhatsApp", "name " + name1 + " - number - " + number);
                if (name1.equals(s)) {
                    //watssappNumber = number;
                    return true;
                    //Log.v("line80", name + " " + watssappNumber);
                }

            }
            Log.v("WhatsApp", "Total WhatsApp Contacts: " + c.getCount());
            c.close();
            return false;
        }

        private boolean isPhone(String s) {
            try{
                Integer.parseInt(s);
            }catch(NumberFormatException exception){
                return false;
            }
            return true;
        }


    }

}
