package felix.com.ribbit.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseUser;

import butterknife.Bind;
import butterknife.ButterKnife;
import felix.com.ribbit.R;
import felix.com.ribbit.constant.ParseConstants;
import felix.com.ribbit.exception.InputValidityException;
import felix.com.ribbit.listener.TextInputLayoutFocusListener;
import felix.com.ribbit.ui.SignUpActivity;
import felix.com.ribbit.util.Util;

public class SignUpFragment extends Fragment {

    private static final String TAG = SignUpActivity.class.getName();

    private static final int LEN_USERNAME_MIN = 4;
    private static final int LEN_USERNAME_MAX = 20;
    private static final int LEN_PASSWORD_MIN = 8;
    private static final int LEN_PASSWORD_MAX = 20;

    //field section
    @Nullable
    @Bind(R.id.usernameField)
    TextView mUsernameField;
    @Nullable
    @Bind(R.id.passwordField)
    TextView mPasswordField;
    @Nullable
    @Bind(R.id.emailField)
    TextView mEmailField;
    @Nullable
    @Bind(R.id.phoneNumberField)
    TextView mPhoneNumberField;
    @Nullable
    @Bind(R.id.nameField)
    TextView mNameField;
    @Nullable
    @Bind(R.id.image_profile_picture)
    ImageView mProfilePicture;
    @Nullable
    @Bind(R.id.button_edit_profile_picture)
    ImageButton mEditProfilePictureButton;

    //text input layout section
    @Nullable
    @Bind(R.id.usernameHolder)
    TextInputLayout mUsernameHolder;
    @Nullable
    @Bind(R.id.passwordHolder)
    TextInputLayout mPasswordHolder;

    @Nullable
    @Bind(R.id.emailHolder)
    TextInputLayout mEmailHolder;
    @Nullable
    @Bind(R.id.nameHolder)
    TextInputLayout mNameHolder;
    @Nullable
    @Bind(R.id.phoneNumberHolder)
    TextInputLayout mPhoneNumberHolder;

    private Integer mSection;
    private ParseUser mCandidate;
    private SignUpActivity mActivity;
    private View mView;
    private DialogInterface.OnClickListener mListener;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public SignUpFragment() {
    }

    public static SignUpFragment newInstance(int sectionNumber) {
        SignUpFragment fragment = new SignUpFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mSection = getArguments().getInt(ARG_SECTION_NUMBER);
        mView = initView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private View initView(LayoutInflater inflater, ViewGroup container,
                          Bundle savedInstanceState) {
        View rootView;
        switch (mSection) {
            case 1:
                rootView = inflater.inflate(R.layout.fragment_sign_up_1, container, false);
                break;
            case 2:
                rootView = inflater.inflate(R.layout.fragment_sign_up_2, container, false);
                break;
            case 3:
                rootView = inflater.inflate(R.layout.fragment_sign_up_3, container, false);
                break;
            default:
                rootView = inflater.inflate(R.layout.fragment_sign_up_3, container, false);
        }
        return rootView;
    }

    private void initData() {
        mActivity = (SignUpActivity) getActivity();
        mCandidate = mActivity.getCandidate();

        if (mEmailHolder != null && mEmailField != null) {
            mEmailField.setOnFocusChangeListener(new TextInputLayoutFocusListener(mEmailHolder));
        }
        if (mUsernameHolder != null && mUsernameField != null) {
            mUsernameField.setOnFocusChangeListener(new TextInputLayoutFocusListener(mUsernameHolder));
        }
        if (mPasswordHolder != null && mPasswordField != null) {
            mPasswordField.setOnFocusChangeListener(new TextInputLayoutFocusListener(mPasswordHolder));
        }
        if (mPhoneNumberHolder != null && mPhoneNumberField != null) {
            mPhoneNumberField.setOnFocusChangeListener(new TextInputLayoutFocusListener(mPhoneNumberHolder));
        }
        if (mNameHolder != null && mNameField != null) {
            mNameField.setOnFocusChangeListener(new TextInputLayoutFocusListener(mNameHolder));
        }
        if (mProfilePicture != null && mEditProfilePictureButton != null) {
            mListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            break;
                        case 1:
                            break;
                        default:
                            break;
                    }
                }
            };
            mEditProfilePictureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setItems(R.array.edit_profile_pictures, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });


        }
    }

    public void validateInput() throws InputValidityException {
        try {
            switch (mSection) {
                case 1:
                    validateFirstPage();
                    break;
                case 2:
                    validateSecondPage();
                    break;
                case 3:
                    validateFinalPage();
                    break;
                default:
                    break;
            }
        } catch (InputValidityException e) {
            Log.i(TAG, e.getMessage());
            throw new InputValidityException(e.getMessage());
        }
    }

    public void validateFirstPage() throws InputValidityException {
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

        mCandidate.setUsername(username);
        mCandidate.setPassword(password);
        mCandidate.setEmail(email);
    }

    public void validateSecondPage() throws InputValidityException {
        if (mPhoneNumberField == null || mPhoneNumberHolder == null) {
            throw new IllegalStateException("field can not be null");
        }

        String phoneNumber = mPhoneNumberField.getText().toString();
        String errorMsg;

        phoneNumber = phoneNumber.trim();

        if (phoneNumber.isEmpty()) {
            errorMsg = getString(R.string.phone_number_error);
            showError(mPhoneNumberHolder, errorMsg);
            throw new InputValidityException(errorMsg);
        }

        mCandidate.put(ParseConstants.KEY_PHONE_NUMBER, phoneNumber);
    }

    public void validateFinalPage() throws InputValidityException {
        if (mNameField == null || mNameHolder == null) {
            throw new IllegalStateException("field can not be null");
        }

        String name = mNameField.getText().toString();
        String errorMsg;

        name = name.trim();

        if (name.isEmpty()) {
            errorMsg = getString(R.string.name_error);
            showError(mNameHolder, errorMsg);
            throw new InputValidityException(errorMsg);
        }

        mCandidate.put(ParseConstants.KEY_NAME, name);
        mCandidate.put(ParseConstants.KEY_PROFILE_PICTURE, Util.ImageViewToString(mProfilePicture));
        mCandidate.put(ParseConstants.KEY_STATUS, "Newbie in action");

    }

    private void showError(TextInputLayout v, String error) {
        v.setErrorEnabled(true);
        v.setError(error);
    }
}