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
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import felix.com.ribbit.R;
import felix.com.ribbit.listener.RibbitResultListener;
import felix.com.ribbit.model.ribbit.RibbitUser;
import felix.com.ribbit.util.Util;

public class LoginActivity extends AppCompatActivity {
    @Bind(R.id.button_sign_up)
    TextView mSignUpButton;

    @Bind(R.id.field_email)
    EditText mFieldEmail;

    @Bind(R.id.field_password)
    EditText mFieldPassword;

    @Bind(R.id.button_login)
    Button mLoginButton;

    @Bind(R.id.progress_bar_global)
    ProgressBar mProgressBarGlobal;

    @Bind(R.id.wrapper_progress_bar_global)
    RelativeLayout mWrapperProgressBarGlobal;

    @Bind(R.id.text_loading)
    TextView mTextLoading;

    boolean mUpdateState = false;
    View mView;
    private View.OnClickListener mSignUpListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        }
    };
    private RibbitResultListener mLoginResultListener = new RibbitResultListener() {
        @Override
        public void onFinish() {
            disableLoading();
            enableButtonClick();
        }

        @Override
        public void onSuccess() {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        @Override
        public void onError(Throwable e, String message) {
            enableButtonClick();

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
            dialogBuilder.setMessage(e.getMessage())
                    .setTitle(R.string.loginErrorTitle)
                    .setPositiveButton(android.R.string.ok, null);

            AlertDialog dialog = dialogBuilder.create();
            dialog.show();
        }
    };
    private View.OnClickListener mLoginListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String email = mFieldEmail.getText().toString();
            String password = mFieldPassword.getText().toString();

            email = email.trim().toLowerCase();
            password = password.trim();


            if (email.isEmpty() || password.isEmpty()) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                dialogBuilder.setMessage(R.string.loginErrorMessage)
                        .setTitle(R.string.loginErrorTitle)
                        .setPositiveButton(android.R.string.ok, null);

                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
            } else {
                Util.hideKeyboard(LoginActivity.this);
                Util.showView(mProgressBarGlobal);
                disableButtonClick();
                enableLoading();
                RibbitUser.login(email, password, mLoginResultListener);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();

        enableButtonClick();
        disableLoading();
    }

    private void initView(){
        mView = getWindow().getDecorView().getRootView();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void disableButtonClick() {
        Util.disableOnClickListener(mLoginButton);
        Util.disableOnClickListener(mSignUpButton);
    }

    private void enableButtonClick() {
        Util.enableOnClickListener(mLoginButton, mLoginListener);
        Util.enableOnClickListener(mSignUpButton, mSignUpListener);
    }

    private void enableLoading() {
        mUpdateState = true;
        Util.showView(mTextLoading);
        Util.showView(mProgressBarGlobal);
        Util.showView(mWrapperProgressBarGlobal);
    }

    private void disableLoading() {
        mUpdateState = false;
        Util.hideView(mWrapperProgressBarGlobal);
        Util.hideView(mProgressBarGlobal);
        Util.hideView(mTextLoading);
    }
}
