package felix.com.ribbit.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import felix.com.ribbit.R;
import felix.com.ribbit.exception.InputValidityException;
import felix.com.ribbit.listener.TextInputLayoutFocusListener;
import felix.com.ribbit.model.Validatable;
import felix.com.ribbit.model.firebase.UserData;
import felix.com.ribbit.model.wrapper.UserWrapper;
import felix.com.ribbit.ui.SignUpActivity;
import felix.com.ribbit.util.Util;

public class AccountDataFragment extends Fragment implements Validatable {
    private static final String TAG = AccountDataFragment.class.getName();

    private static final int LEN_USERNAME_MIN = 4;
    private static final int LEN_USERNAME_MAX = 20;
    private static final int LEN_PASSWORD_MIN = 8;
    private static final int LEN_PASSWORD_MAX = 20;

    @Bind(R.id.field_email)
    TextView mUsernameField;
    @Bind(R.id.field_password)
    TextView mPasswordField;
    @Bind(R.id.emailField)
    TextView mEmailField;

    @Bind(R.id.holder_email)
    TextInputLayout mUsernameHolder;
    @Bind(R.id.holder_password)
    TextInputLayout mPasswordHolder;

    @Bind(R.id.emailHolder)
    TextInputLayout mEmailHolder;

    private UserWrapper mCandidate;
    private UserData mUserData;

    private SignUpActivity mActivity;

    public AccountDataFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return initView(inflater, container);
    }

    private View initView(LayoutInflater inflater, ViewGroup container) {
        View rootView = inflater.inflate(R.layout.fragment_sign_up_1, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        mActivity = (SignUpActivity) getActivity();
        mCandidate = mActivity.getUserWrapper();
        mUserData = mCandidate.getData();

        if ((mEmailHolder != null) && (mEmailField != null)) {
            mEmailField.setOnFocusChangeListener(new TextInputLayoutFocusListener(mEmailHolder));
        }
        if (mUsernameHolder != null && mUsernameField != null) {
            mUsernameField.setOnFocusChangeListener(new TextInputLayoutFocusListener(mUsernameHolder));
        }
        if (mPasswordHolder != null && mPasswordField != null) {
            mPasswordField.setOnFocusChangeListener(new TextInputLayoutFocusListener(mPasswordHolder));
        }
    }

    @Override
    public void validate() throws InputValidityException {
        if (mUsernameField == null || mPasswordField == null || mEmailField == null
                || mUsernameHolder == null || mPasswordHolder == null || mEmailHolder == null) {
            throw new IllegalStateException("field can not be null");
        }
        String username = mUsernameField.getText().toString();
        String password = mPasswordField.getText().toString();
        String email = mEmailField.getText().toString();
        String errorMsg;

        username = username.trim();
        password = password.trim();
        email = email.trim();

        if (!Util.isValidEmail(email)) {
            errorMsg = getString(R.string.email_error);
            showError(mEmailHolder, errorMsg);
            throw new InputValidityException(errorMsg);
        }
        if (username.isEmpty() || username.length() < LEN_USERNAME_MIN || username.length() > LEN_USERNAME_MAX) {
            errorMsg = getString(R.string.username_error);
            showError(mUsernameHolder, errorMsg);
            throw new InputValidityException(errorMsg);
        }
        if (password.isEmpty() || password.length() < LEN_PASSWORD_MIN || password.length() > LEN_PASSWORD_MAX) {
            errorMsg = getString(R.string.password_error);
            showError(mPasswordHolder, errorMsg);
            throw new InputValidityException(errorMsg);
        }

        mUserData.setUsername(username);
        mUserData.setPassword(password);
        mUserData.setEmail(email);
    }

    public void showError(TextInputLayout v, String error) {
        v.setErrorEnabled(true);
        v.setError(error);
    }

}