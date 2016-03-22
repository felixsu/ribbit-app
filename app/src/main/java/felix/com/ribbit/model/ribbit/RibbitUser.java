package felix.com.ribbit.model.ribbit;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import felix.com.ribbit.listener.RibbitResultListener;
import felix.com.ribbit.model.base.RibbitBase;
import felix.com.ribbit.model.firebase.UserData;
import felix.com.ribbit.model.wrapper.UserWrapper;
import felix.com.ribbit.util.Util;

/**
 * Created by fsoewito on 3/19/2016.
 */
public class RibbitUser extends RibbitBase {

    private static final String TAG = RibbitUser.class.getName();

    private static final Firebase FIREBASE_USERS = new Firebase(RIBBIT_DATA + "/users");

    private static final String MASTER_DATA = "ribbit-app-master";
    private static final String KEY_USER = "key-current-user";


    private static Context mContext;
    private static SharedPreferences mSharedPref;
    private static UserWrapper mCurrentUser;

    public static Firebase getFirebaseUsers() {
        return FIREBASE_USERS;
    }

    public static void setAndroidContext(Context context) {
        mContext = context;
        mSharedPref = context.getSharedPreferences(MASTER_DATA, Context.MODE_PRIVATE);

        try {
            String currentUserJson = mSharedPref.getString(KEY_USER, null);
            if (currentUserJson != null) {
                mCurrentUser = Util.getMapperInstance().readValue(currentUserJson, UserWrapper.class);
            } else {
                mCurrentUser = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "failed to deserialize user");
        }
    }

    public static UserWrapper getCurrentUser() {
        return mCurrentUser;
    }

    public static void setCurrentUser(UserWrapper user) {
        if (mContext == null) {
            throw new IllegalStateException("context not initialized yet");
        }
        try {
            SharedPreferences.Editor editor = mSharedPref.edit();
            editor.putString(KEY_USER, new ObjectMapper().writeValueAsString(user)).apply();
        } catch (JsonProcessingException e) {
            Log.e(TAG, "failed to serialize user", e);
        }
    }

    public static void clearCurrentUser() {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(KEY_USER).apply();
    }

    //fresh login from sign up
    public static void firstLogin(final String email, final String password, final RibbitResultListener listener) {
        RibbitBase.getRoot().authWithPassword(email, password, new Firebase.AuthResultHandler() {

            @Override
            public void onAuthenticated(AuthData authData) {
                Log.d(TAG, "first time login success with uid" + authData.getUid());
                listener.onFinish();
                listener.onSuccess();
            }

            @Override
            public void onAuthenticationError(FirebaseError e) {
                Log.i(TAG, "first time login failed" + e.getMessage());
                listener.onFinish();
                listener.onError(e.toException(), e.getMessage());
            }
        });
    }

    public static void login(final String email, final String password, final RibbitResultListener listener) {
        RibbitBase.getRoot().authWithPassword(email, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                final String uid = authData.getUid();

                Firebase firebaseUser = getFirebaseUsers().child("/" + uid);
                firebaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot result) {
                        UserData u = result.getValue(UserData.class);
                        if (mCurrentUser == null) {
                            mCurrentUser = new UserWrapper(uid, u);
                        } else {
                            mCurrentUser.setId(uid);
                            mCurrentUser.setData(u);
                        }
                        setCurrentUser(mCurrentUser);
                        listener.onFinish();
                        listener.onSuccess();
                    }

                    @Override
                    public void onCancelled(FirebaseError e) {
                        listener.onFinish();
                        listener.onError(e.toException(), e.getMessage());
                    }
                });
            }

            @Override
            public void onAuthenticationError(FirebaseError e) {
                Log.e(TAG, "login error " + e.getCode());
                listener.onFinish();
                listener.onError(e.toException(), e.getMessage());
            }
        });
    }

    public static void logout() {
        RibbitBase.getRoot().unauth();
    }


}
