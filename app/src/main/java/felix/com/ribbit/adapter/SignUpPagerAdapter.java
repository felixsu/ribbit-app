package felix.com.ribbit.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

import felix.com.ribbit.fragment.SignUpFragment;

public class SignUpPagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = SignUpPagerAdapter.class.getName();

    private static final int PAGE_NUMBER = 3;
    private Map<Integer, Fragment> mIdMapper = new HashMap<>();

    public SignUpPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        mIdMapper.put(position, fragment);
        Log.d(TAG, "fragment instantiated " + position);
        return fragment;
    }

    @Override
    public Fragment getItem(int position) {
        return SignUpFragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
        return PAGE_NUMBER;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "SECTION 1";
            case 1:
                return "SECTION 2";
            case 2:
                return "SECTION 3";
        }
        return null;
    }

    public Fragment getFragment(int position) {
        if (mIdMapper != null) {
            return mIdMapper.get(position);
        } else {
            return null;
        }
    }

}