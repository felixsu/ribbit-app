package felix.com.ribbit.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import felix.com.ribbit.R;
import felix.com.ribbit.fragment.FriendsFragment;
import felix.com.ribbit.fragment.InboxFragment;

/**
 * Created by fsoewito on 11/20/2015.
 *
 */
public class MainActivityPageAdapter extends FragmentPagerAdapter {

    protected Context mContext;

    public MainActivityPageAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0 :
                return new InboxFragment();
            case 1 :
                return new FriendsFragment();
        }
        return new InboxFragment();
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.inboxTitle);
            case 1:
                return mContext.getString(R.string.friendsTitle);
        }
        return null;
    }
}