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

public class PhoneFragment extends Fragment implements Validatable {
    private static final String TAG = PhoneFragment.class.getName();

    @Bind(R.id.phoneNumberField)
    TextView mPhoneNumberField;
    @Bind(R.id.phoneNumberHolder)
    TextInputLayout mPhoneNumberHolder;

    private UserWrapper mCandidate;
    private UserData mUserData;
    private SignUpActivity mActivity;

    public PhoneFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return initView(inflater, container);
    }

    private View initView(LayoutInflater inflater, ViewGroup container) {
        View rootView = inflater.inflate(R.layout.fragment_sign_up_2, container, false);
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
        mPhoneNumberField.setOnFocusChangeListener(new TextInputLayoutFocusListener(mPhoneNumberHolder));

    }

    public void showError(TextInputLayout v, String error) {
        v.setErrorEnabled(true);
        v.setError(error);
    }

    @Override
    public void validate() throws InputValidityException {
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

        mUserData.setPhoneNumber(phoneNumber);
    }
}