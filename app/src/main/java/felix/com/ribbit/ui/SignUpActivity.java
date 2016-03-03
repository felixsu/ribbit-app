package felix.com.ribbit.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import felix.com.ribbit.R;
import felix.com.ribbit.constant.ParseConstants;
import felix.com.ribbit.exception.InputValidityException;
import felix.com.ribbit.util.Util;

public class SignUpActivity extends AppCompatActivity implements OnClickListener {

    public static final String TAG = SignUpActivity.class.getName();

    private static final int LEN_USERNAME_MIN = 4;
    private static final int LEN_USERNAME_MAX = 20;
    private static final int LEN_PASSWORD_MIN = 8;
    private static final int LEN_PASSWORD_MAX = 20;

    //field section
    @Bind(R.id.usernameField)
    TextView mUsernameField;
    @Bind(R.id.passwordField)
    TextView mPasswordField;
    @Bind(R.id.emailField)
    TextView mEmailField;

    @Bind(R.id.nameField)
    TextView mnameField;
    @Bind(R.id.phoneNumberField)
    TextView mPhoneNumberField;

    //text input layout section
    @Bind(R.id.usernameHolder)
    TextInputLayout mUsernameHolder;
    @Bind(R.id.passwordHolder)
    TextInputLayout mPasswordHolder;
    @Bind(R.id.emailHolder)
    TextInputLayout mEmailHolder;
    @Bind(R.id.nameHolder)
    TextInputLayout mNameHolder;
    @Bind(R.id.phoneNumberHolder)
    TextInputLayout mPhoneNumberHolder;

    //etc section
    @Bind(R.id.signUpButton)
    Button mSignUpButton;

    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;

    View mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        initView();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initView() {
        mView = getWindow().getDecorView().getRootView();
        mProgressBar.setVisibility(View.INVISIBLE);
        mSignUpButton.setOnClickListener(this);
    }

    private void toggleLoadingScreen() {
        if (mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mView.setAlpha(0.8f);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mView.setAlpha(0f);
        }
    }

    private LocalUser validateInput() throws InputValidityException {
        String username = mUsernameField.getText().toString();
        String password = mPasswordField.getText().toString();
        String email = mEmailField.getText().toString();
        String name = mnameField.getText().toString();
        String phoneNumber = mPhoneNumberField.getText().toString();
        String errorMsg;

        username = username.trim();
        password = password.trim();
        email = email.trim();
        name = name.trim();
        phoneNumber = phoneNumber.trim();

        if (username.isEmpty() || username.length() < LEN_USERNAME_MIN || username.length() > LEN_USERNAME_MAX) {
            errorMsg = getString(R.string.username_error);
            mUsernameHolder.setError(errorMsg);
            throw new InputValidityException(errorMsg);
        }

        if (password.isEmpty() || password.length() < LEN_PASSWORD_MIN || password.length() > LEN_PASSWORD_MAX) {
            errorMsg = getString(R.string.password_error);
            mPasswordHolder.setError(errorMsg);
            throw new InputValidityException(errorMsg);
        }

        if (!Util.isValidEmail(email)) {
            errorMsg = getString(R.string.email_error);
            mPasswordHolder.setError(errorMsg);
            throw new InputValidityException(errorMsg);
        }

        if (name.isEmpty()) {
            errorMsg = getString(R.string.name_error);
            mPasswordHolder.setError(errorMsg);
            throw new InputValidityException(errorMsg);
        }

        if (phoneNumber.isEmpty()) {
            errorMsg = getString(R.string.phone_number_error);
            mPasswordHolder.setError(errorMsg);
            throw new InputValidityException(errorMsg);
        }

        return new LocalUser(name, username, password, email, phoneNumber);
    }

    @Override
    public void onClick(View v) {
        LocalUser u;

        hideKeyboard();
        try {
            u = validateInput();
        } catch (InputValidityException e) {
            Log.i(TAG, e.getMessage());
            return;
        }
        ParseUser newUser = new ParseUser();
        newUser.setEmail(u.getEmail());
        newUser.setPassword(u.getPassword());
        newUser.setUsername(u.getUsername());
        newUser.put(ParseConstants.KEY_NAME, u.getName());
        newUser.put(ParseConstants.KEY_PHONE_NUMBER, u.getPhoneNumber());
        toggleLoadingScreen();
        newUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                toggleLoadingScreen();
                if (e == null) {
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
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

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private static class LocalUser {

        String name;
        String username;
        String password;
        String email;
        String phoneNumber;

        public LocalUser(String name, String username, String password, String email, String phoneNumber) {
            this.name = name;
            this.username = username;
            this.password = password;
            this.email = email;
            this.phoneNumber = phoneNumber;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
    }
}
