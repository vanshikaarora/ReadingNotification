package vanshika.stickers.com.bobblenotification;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent;
                if (android.os.Build.VERSION.SDK_INT < 18) {
                    intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                } else {
                    intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                }
                try {
                    startActivity(intent);
                    Toast.makeText(MainActivity.this, "Please enable the Notification TTS Service.", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
