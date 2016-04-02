package felix.com.ribbit.model.ribbit;

import android.content.Context;

import felix.com.ribbit.model.base.RibbitBase;

/**
 * Created by fsoewito on 3/27/2016.
 */
public class RibbitGlobal extends RibbitBase {

    public static void setAndroidContext(Context context) {
        mContext = context;
        mSharedPref = context.getSharedPreferences(MASTER_DATA, Context.MODE_PRIVATE);
        RibbitUser.init();
        RibbitPhone.init();
        RibbitFriend.init();
    }
}
