package felix.com.ribbit.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import felix.com.ribbit.R;
import felix.com.ribbit.listener.RibbitListener;
import felix.com.ribbit.model.Ribbit;
import felix.com.ribbit.util.Util;

public class LoginActivity extends AppCompatActivity implements RibbitListener {
    @Bind(R.id.button_sign_up)
    Button mSignUpButton;

    @Bind(R.id.field_email)
    EditText mFieldEmail;

    @Bind(R.id.field_password)
    EditText mFieldPassword;

    @Bind(R.id.button_login)
    Button mLoginButton;

    @Bind(R.id.progress_bar)
    ProgressBar mProgressBar;

    View mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initView();
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mFieldEmail.getText().toString();
                String password = mFieldPassword.getText().toString();

                email = email.trim();
                password = password.trim();

                if (email.isEmpty() || password.isEmpty()) {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                    dialogBuilder.setMessage(R.string.loginErrorMessage)
                            .setTitle(R.string.loginErrorTitle)
                            .setPositiveButton(android.R.string.ok, null);

                    AlertDialog dialog = dialogBuilder.create();
                    dialog.show();
                } else {
                    Util.showView(mProgressBar);
                    Ribbit.login(email, password, LoginActivity.this);
                }
            }
        });


    }

    private void initView(){
        mView = getWindow().getDecorView().getRootView();
        mProgressBar.setVisibility(View.INVISIBLE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onFinish() {
        Util.hideView(mProgressBar);
    }

    @Override
    public void onSuccess() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onError(Throwable e) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
        dialogBuilder.setMessage(e.getMessage())
                .setTitle(R.string.loginErrorTitle)
                .setPositiveButton(android.R.string.ok, null);

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }
}
