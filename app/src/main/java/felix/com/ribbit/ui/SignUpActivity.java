package felix.com.ribbit.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.commit451.inkpageindicator.InkPageIndicator;

import butterknife.Bind;
import butterknife.ButterKnife;
import felix.com.ribbit.R;
import felix.com.ribbit.adapter.SignUpPagerAdapter;
import felix.com.ribbit.exception.InputValidityException;
import felix.com.ribbit.listener.RibbitListener;
import felix.com.ribbit.model.UserWrapper;
import felix.com.ribbit.model.Validatable;
import felix.com.ribbit.util.Util;

public class SignUpActivity extends AppCompatActivity implements RibbitListener {

    public static final String TAG = SignUpActivity.class.getName();

    @Bind(R.id.button_next)
    Button mButtonNext;
    @Bind(R.id.button_prev)
    Button mButtonPrev;
    @Bind(R.id.indicator)
    InkPageIndicator mIndicator;

    //etc section
    @Bind(R.id.progress_bar)
    ProgressBar mProgressBar;

    private SignUpPagerAdapter mSignUpPagerAdapter;
    private ViewPager mViewPager;
    private int mCurrentPage = 0;
    private UserWrapper mCandidate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    @Override
    public void onBackPressed() {
        if (mCurrentPage != 0) {
            goToPrevPage();
        } else {
            super.onBackPressed();
        }
    }

    private void initView() {
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mProgressBar.setVisibility(View.INVISIBLE);
        mSignUpPagerAdapter = new SignUpPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSignUpPagerAdapter);
        mIndicator.setViewPager(mViewPager);
        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNextPage();
            }
        });

        mButtonPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPrevPage();
            }
        });

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
        mCandidate = new UserWrapper();

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
                doFinalize();
            }

        } catch (InputValidityException e) {
            Log.i(TAG, "input error");
        }
    }

    public void doFinalize() {
        hideKeyboard();
        Util.showView(mProgressBar);
        mCandidate.signUp(this);
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public UserWrapper getCandidate() {
        return mCandidate;
    }


    @Override
    public void onFinish() {
        Util.hideView(mProgressBar);
    }

    @Override
    public void onSuccess() {
        Toast.makeText(this, "success to create user " + mCandidate.getUid(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    @Override
    public void onError(Throwable e) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SignUpActivity.this);
        dialogBuilder.setMessage(e.getMessage())
                .setTitle(R.string.signUpErrorTitle)
                .setPositiveButton(android.R.string.ok, null);

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }
}
