package felix.com.ribbit.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.commit451.inkpageindicator.InkPageIndicator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.Bind;
import butterknife.ButterKnife;
import felix.com.ribbit.R;
import felix.com.ribbit.adapter.SignUpPagerAdapter;
import felix.com.ribbit.exception.InputValidityException;
import felix.com.ribbit.listener.RibbitResultListener;
import felix.com.ribbit.model.Validatable;
import felix.com.ribbit.model.ribbit.RibbitUser;
import felix.com.ribbit.model.wrapper.PhoneWrapper;
import felix.com.ribbit.model.wrapper.PictureWrapper;
import felix.com.ribbit.model.wrapper.UserWrapper;
import felix.com.ribbit.util.MediaUtil;
import felix.com.ribbit.util.Util;

public class SignUpActivity extends AppCompatActivity {

    public static final String TAG = SignUpActivity.class.getName();

    @Bind(R.id.button_next)
    TextView mButtonNext;
    @Bind(R.id.button_prev)
    TextView mButtonPrev;
    @Bind(R.id.indicator)
    InkPageIndicator mIndicator;

    //etc section
    @Bind(R.id.progress_bar_global)
    ProgressBar mProgressBarGlobal;
    @Bind(R.id.wrapper_progress_bar_global)
    RelativeLayout mWrapperProgressBarGlobal;

    @Bind(R.id.text_loading)
    TextView mTextLoading;

    private SignUpPagerAdapter mSignUpPagerAdapter;
    private ViewPager mViewPager;
    private int mCurrentPage = 0;
    private UserWrapper mUserWrapper;
    private Uri mProfilePictureUri;

    private int mCompleteSignUpTask = 0;
    private int mSuccessSignUpTask = 0;
    private boolean mUpdateState = false;

    private View.OnClickListener mPrevListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            goToPrevPage();
        }
    };
    private RibbitResultListener mDeleteUserListener = new RibbitResultListener() {
        @Override
        public void onFinish() {

        }

        @Override
        public void onSuccess() {
            Toast.makeText(SignUpActivity.this, "undone previous operation completed successfully", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(Throwable e, String message) {
            Toast.makeText(SignUpActivity.this, "undone previous operation not completed", Toast.LENGTH_SHORT).show();
        }
    };
    private View.OnClickListener mNextListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            goToNextPage();
        }
    };
    //store main data listener
    private RibbitResultListener mStoreDataListener = new RibbitResultListener() {
        @Override
        public void onFinish() {
            incrementCompleteTask();
        }

        @Override
        public void onSuccess() {
            Log.d(TAG, "store primary data finish successfully");
            incrementSuccessTask();
            checkResult();
        }

        @Override
        public void onError(Throwable e, String message) {
            showErrorSignUpDialog(e.getMessage());
            checkResult();
        }
    };
    //store phone data listener
    private RibbitResultListener mStorePhoneDataListener = new RibbitResultListener() {
        @Override
        public void onFinish() {
            incrementCompleteTask();
        }

        @Override
        public void onSuccess() {
            Log.d(TAG, "store phone data finish successfully");
            incrementSuccessTask();
            checkResult();
        }

        @Override
        public void onError(Throwable e, String message) {
            showErrorSignUpDialog(e.getMessage());
            checkResult();
        }
    };
    //store picture data listener
    private RibbitResultListener mStorePictureDataListener = new RibbitResultListener() {
        @Override
        public void onFinish() {
            incrementCompleteTask();
        }

        @Override
        public void onSuccess() {
            Log.d(TAG, "store picture finish successfully");
            incrementSuccessTask();
            checkResult();
        }

        @Override
        public void onError(Throwable e, String message) {
            showErrorSignUpDialog(e.getMessage());
            checkResult();
        }
    };
    //section sign-up and login process, delete on failed
    private RibbitResultListener mBasicSignUpListener = new RibbitResultListener() {
        @Override
        public void onFinish() {
            Util.showTextView(mTextLoading, "setup user data");
        }

        @Override
        public void onSuccess() {
            storeUserData();
            storeAdditionalData();
            storePicture();
        }

        @Override
        public void onError(Throwable e, String message) {
            hideProgressBar();
            setButtonOnClickListener();
            performDeleteUser();
            showErrorSignUpDialog(e.getMessage());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    @Override
    public void onBackPressed() {
        if (!mUpdateState) {
            if (mCurrentPage != 0) {
                goToPrevPage();
            } else {
                super.onBackPressed();
            }
        }
    }

    private void initView() {
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        hideProgressBar();

        mSignUpPagerAdapter = new SignUpPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSignUpPagerAdapter);
        mIndicator.setViewPager(mViewPager);

        setButtonOnClickListener();

        mButtonPrev.setVisibility(View.INVISIBLE);
    }

    private void goToPrevPage() {
        mCurrentPage = mViewPager.getCurrentItem();
        int totalPage = mSignUpPagerAdapter.getCount();
        Log.d(TAG, "Current Item : " + mCurrentPage);
        Log.d(TAG, "Total Item : " + totalPage);
        if (mCurrentPage != 0) {
            if (mCurrentPage == 1) {
                mButtonPrev.setVisibility(View.INVISIBLE);
            }
            mCurrentPage--;
            mButtonNext.setText(R.string.text_next);
            mViewPager.setCurrentItem(mCurrentPage, true);
        }
    }

    private void initData() {
        mUserWrapper = new UserWrapper();

    }

    private void goToNextPage() {
        try {
            mCurrentPage = mViewPager.getCurrentItem();
            Validatable validatable = (Validatable) mSignUpPagerAdapter.getFragment(mCurrentPage);
            validatable.validate();
            int totalPage = mSignUpPagerAdapter.getCount();
            Log.d(TAG, "Current Item : " + mCurrentPage);
            Log.d(TAG, "Total Item : " + totalPage);
            mButtonPrev.setVisibility(View.VISIBLE);
            if (mCurrentPage != totalPage - 1) {
                mViewPager.setCurrentItem(mCurrentPage, true);
                //on second page
                if (mCurrentPage == totalPage - 2) {
                    mButtonNext.setText(R.string.finish);
                }
                mCurrentPage++;
                mViewPager.setCurrentItem(mCurrentPage, true);
            } else {
                signUpUser();
            }

        } catch (InputValidityException e) {
            Log.i(TAG, "input error");
        }
    }

    public void signUpUser() {
        Util.hideKeyboard(this);
        Util.showView(mProgressBarGlobal);
        Util.showView(mWrapperProgressBarGlobal);
        removeButtonOnClickListener();
        activateUpdateState();
        mUserWrapper.signUp(mBasicSignUpListener);
    }

    private void removeButtonOnClickListener() {
        Util.disableOnClickListener(mButtonNext);
        Util.disableOnClickListener(mButtonPrev);
    }

    private void setButtonOnClickListener() {
        Util.enableOnClickListener(mButtonNext, mNextListener);
        Util.enableOnClickListener(mButtonPrev, mPrevListener);
    }

    public UserWrapper getUserWrapper() {
        return mUserWrapper;
    }

    public void setProfilePictureUri(Uri profilePictureUri) {
        mProfilePictureUri = profilePictureUri;
    }

    private void showErrorSignUpDialog(String message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SignUpActivity.this);
        dialogBuilder.setMessage(message)
                .setTitle(R.string.signUpErrorTitle)
                .setPositiveButton(android.R.string.ok, null);

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    private void hideProgressBar() {
        Util.hideView(mTextLoading);
        Util.hideView(mProgressBarGlobal);
        Util.hideView(mWrapperProgressBarGlobal);
    }

    private void startMainActivity() {
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void storeUserData() {
        mUserWrapper.store(mStoreDataListener);
    }

    private void storeAdditionalData() {
        PhoneWrapper phoneWrapper = new PhoneWrapper(mUserWrapper);
        phoneWrapper.store(mStorePhoneDataListener);
    }

    private void storePicture() {
        Bitmap rawPicture;

        try {
            if (mProfilePictureUri != null) {
                Log.d(TAG, "entering replace old picture with new one " + mUserWrapper.getId());
                InputStream is = getContentResolver().openInputStream(mProfilePictureUri);
                rawPicture = BitmapFactory.decodeStream(is);
                if (is != null) {
                    is.close();
                }

                File oldFile = new File(mProfilePictureUri.getPath());
                boolean deleted = oldFile.delete();
                Log.i(TAG, "delete " + deleted);

                Uri newUri = MediaUtil.getProfilePictureUri(mUserWrapper.getId());
                if (newUri == null) {
                    throw new IOException("failed get new uri");
                }
                mProfilePictureUri = newUri;
                File outputFile = new File(newUri.getPath());

                FileOutputStream os = new FileOutputStream(outputFile);
                rawPicture.compress(Bitmap.CompressFormat.JPEG, 80, os);
                os.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "error read/write file", e);
        }
        PictureWrapper pictureWrapper = new PictureWrapper(mUserWrapper.getId(), mProfilePictureUri);
        pictureWrapper.store(mStorePictureDataListener);
    }

    private void resetTask() {
        mCompleteSignUpTask = 0;
        mSuccessSignUpTask = 0;
    }

    private void incrementSuccessTask() {
        mSuccessSignUpTask++;
    }

    private void incrementCompleteTask() {
        mCompleteSignUpTask++;
    }

    private void performDeleteUser() {
        String userEmail = mUserWrapper.getData().getEmail();
        String password = mUserWrapper.getData().getPassword();
        RibbitUser.deleteUser(userEmail, password, mDeleteUserListener);
    }

    private synchronized void checkResult() {
        if (mCompleteSignUpTask == 3) {
            hideProgressBar();
            setButtonOnClickListener();
            deActivateUpdateState();
            if (mSuccessSignUpTask < 3) {
                resetTask();
            }
        }
        if (mSuccessSignUpTask == 3) {
            Log.d(TAG, "user " + mUserWrapper.getId() + " stored successfully");
            RibbitUser.setCurrentUser(mUserWrapper);
            startMainActivity();
        }
    }

    private void activateUpdateState() {
        mUpdateState = true;
    }

    private void deActivateUpdateState() {
        mUpdateState = false;
    }
}
