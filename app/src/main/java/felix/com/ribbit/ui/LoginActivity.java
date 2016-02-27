package felix.com.ribbit.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import felix.com.ribbit.R;

public class LoginActivity extends AppCompatActivity {
    @Bind(R.id.signUpLabel)
    TextView mSignUpText;

    @Bind(R.id.usernameField)
    TextView mUsernameField;

    @Bind(R.id.passwordField)
    TextView mPasswordField;

    @Bind(R.id.loginButton)
    Button mLoginButton;

    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;

    View mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        initView();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSignUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });


    }

    @OnClick(R.id.loginButton)
    void onClickLoginButton() {
        String username = mUsernameField.getText().toString();
        String password = mPasswordField.getText().toString();

        username = username.trim();
        password = password.trim();

        if (username.isEmpty() || password.isEmpty()) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setMessage(R.string.loginErrorMessage)
                    .setTitle(R.string.loginErrorTitle)
                    .setPositiveButton(android.R.string.ok, null);

            AlertDialog dialog = dialogBuilder.create();
            dialog.show();
        } else {
            toggleLoadingScreen();
            ParseUser.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    toggleLoadingScreen();
                    if (e == null){
                        Intent intent = new Intent (LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else{
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                        dialogBuilder.setMessage(e.getMessage())
                                .setTitle(R.string.loginErrorTitle)
                                .setPositiveButton(android.R.string.ok, null);

                        AlertDialog dialog = dialogBuilder.create();
                        dialog.show();
                    }
                }
            });
        }
    }

    private void initView(){
        mView = getWindow().getDecorView().getRootView();
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private void toggleLoadingScreen(){
        if (mProgressBar.getVisibility() == View.INVISIBLE){
            mProgressBar.setVisibility(View.VISIBLE);
        }else{
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }
}
