package felix.com.ribbit;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends AppCompatActivity {

    @Bind(R.id.usernameField)
    TextView mUsernameField;

    @Bind(R.id.passwordField)
    TextView mPasswordField;

    @Bind(R.id.emailField)
    TextView mEmailField;

    @Bind(R.id.signUpButton)
    Button mSignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_sign_up);

        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setVisibility(View.GONE);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(progressBar);

    }

    @Override
    public void setSupportProgressBarIndeterminateVisibility(boolean visible) {
        getSupportActionBar().getCustomView().setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @OnClick(R.id.signUpButton)
    void onSignUpClick() {
        String userName = mUsernameField.getText().toString();
        String password = mPasswordField.getText().toString();
        String email = mEmailField.getText().toString();

        userName = userName.trim();
        password = password.trim();
        email = email.trim();

        if (userName.isEmpty() || password.isEmpty() || email.isEmpty()) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setMessage(R.string.signUpErrorMessage)
                    .setTitle(R.string.signUpErrorTitle)
                    .setPositiveButton(android.R.string.ok, null);

            AlertDialog dialog = dialogBuilder.create();
            dialog.show();
        } else {
            ParseUser newUser = new ParseUser();
            newUser.setEmail(email);
            newUser.setPassword(password);
            newUser.setUsername(userName);
            setSupportProgressBarIndeterminateVisibility(true);
            newUser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    setSupportProgressBarIndeterminateVisibility(false);
                    if (e == null){
                        Intent intent = new Intent (SignUpActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }else{
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SignUpActivity.this);
                        dialogBuilder.setMessage(e.getMessage())
                                .setTitle(R.string.signUpErrorTitle)
                                .setPositiveButton(android.R.string.ok, null);

                        AlertDialog dialog = dialogBuilder.create();
                        dialog.show();
                    }
                }
            });
        }
    }


}
