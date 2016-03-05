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

import com.commit451.inkpageindicator.InkPageIndicator;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import felix.com.ribbit.R;
import felix.com.ribbit.adapter.SignUpPagerAdapter;
import felix.com.ribbit.exception.InputValidityException;
import felix.com.ribbit.model.Validatable;

public class SignUpActivity extends AppCompatActivity {

    public static final String TAG = SignUpActivity.class.getName();

    @Bind(R.id.button_next)
    Button mNextButton;
    @Bind(R.id.button_prev)
    Button mPrevButton;
    @Bind(R.id.indicator)
    InkPageIndicator mIndicator;

    //etc section
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;

    private SignUpPagerAdapter mSignUpPagerAdapter;
    private ViewPager mViewPager;
    private int mCurrentPage = 0;
    private ParseUser mCandidate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
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
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNextPage();
            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPrevPage();
            }
        });

        mPrevButton.setVisibility(View.INVISIBLE);
    }

    private void goToPrevPage() {
        mCurrentPage = mViewPager.getCurrentItem();
        int totalPage = mSignUpPagerAdapter.getCount();
        Log.d(TAG, "Current Item : " + mCurrentPage);
        Log.d(TAG, "Total Item : " + totalPage);
        if (mCurrentPage != 0) {
            if (mCurrentPage == 1) {
                mPrevButton.setVisibility(View.INVISIBLE);
            }
            mCurrentPage--;
            mViewPager.setCurrentItem(mCurrentPage, true);
        }
    }

    private void initData() {
        mCandidate = new ParseUser();
    }

    private void goToNextPage() {
        try {
            mCurrentPage = mViewPager.getCurrentItem();
            Validatable validatable = (Validatable) mSignUpPagerAdapter.getFragment(mCurrentPage);
            validatable.validate();
            int totalPage = mSignUpPagerAdapter.getCount();
            Log.d(TAG, "Current Item : " + mCurrentPage);
            Log.d(TAG, "Total Item : " + totalPage);
            mPrevButton.setVisibility(View.VISIBLE);
            if (mCurrentPage != totalPage - 1) {
                mViewPager.setCurrentItem(mCurrentPage, true);
                //on second page
                if (mCurrentPage == totalPage - 2) {
                    mNextButton.setText(R.string.finish);
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

    private void toggleLoadingScreen() {
        if (mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void doFinalize() {
        hideKeyboard();
        toggleLoadingScreen();
        mCandidate.signUpInBackground(new SignUpCallback() {
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

    public ParseUser getCandidate() {
        return mCandidate;
    }


}
