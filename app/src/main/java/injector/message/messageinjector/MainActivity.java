package injector.message.messageinjector;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    String defaultSmsApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.b_inject).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT < 19) {
                    sendSms();
                } else if (Telephony.Sms.getDefaultSmsPackage(MainActivity.this).equals(MainActivity.this.getPackageName())) {
                    sendSms();
                    setDefaultSmsAppBack();
                } else  {
                    setDefaultSmsApp();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 1) {
            sendSms();
            setDefaultSmsAppBack();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    void sendSms() {
        ContentValues contentValues = new ContentValues();
        String phone = ((EditText) findViewById(R.id.et_phone)).getText().toString();
        String message = ((EditText) findViewById(R.id.et_message)).getText().toString();
        contentValues.put("address", phone);
        contentValues.put("body", message + "\n" + System.currentTimeMillis());
        contentValues.put("date", System.currentTimeMillis());
        getContentResolver().insert(Uri.parse("content://sms/"), contentValues);
    }

    void setDefaultSmsAppBack() {
        Intent intent = new Intent("android.provider.Telephony.ACTION_CHANGE_DEFAULT");
        intent.putExtra("package", this.defaultSmsApp);
        startActivityForResult(intent, 4);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void setDefaultSmsApp() {
        defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(this);
        Log.e("TAG", defaultSmsApp);
        Intent intent = new Intent("android.provider.Telephony.ACTION_CHANGE_DEFAULT");
        intent.putExtra("package", getPackageName());
        startActivityForResult(intent, 1);
    }

}
