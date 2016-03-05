package felix.com.ribbit.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseAnalytics;
import com.parse.ParseUser;

import felix.com.ribbit.R;
import felix.com.ribbit.receiver.MyReceiver;
import felix.com.ribbit.receiver.Receiver;
import felix.com.ribbit.service.MyService;
import felix.com.ribbit.util.Util;

public class SplashActivity extends AppCompatActivity implements Receiver {
    public static final String TAG = SplashActivity.class.getName();

    private ImageView mSplashIcon;
    private TextView mSplashLabel;

    MyReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "enter on create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        initField();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "enter on resume");
        super.onResume();
        mSplashLabel.setText(getString(R.string.app_name));
        if (mReceiver.getReceiver() == null) {
            mReceiver.setReceiver(this);
        }
        MyService.startActionFetch(this, mReceiver, TAG);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "enter on pause");
        super.onPause();
        if (mReceiver.getReceiver() != null) {
            mReceiver.setReceiver(null);
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        Log.d(TAG, "enter on receiver receiver");

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            navigateToLogin();
        } else {
            Log.i(TAG, String.format("User %s is login", currentUser.getUsername()));
            navigateToMainActivity();
        }
    }

    public void initField() {
        mSplashLabel = (TextView) findViewById(R.id.label_splash_message);
        mSplashIcon = (ImageView) findViewById(R.id.image_splash);

        Drawable splashIcon = Util.setTint(
                getResources().getDrawable(R.drawable.ic_chat_icon),
                getResources().getColor(R.color.white));
        mSplashIcon.setImageDrawable(splashIcon);
        mReceiver = new MyReceiver(new Handler());
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
