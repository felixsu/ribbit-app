package felix.com.ribbit.model.ribbit;

import android.content.SharedPreferences;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import felix.com.ribbit.listener.RibbitResultListener;
import felix.com.ribbit.model.base.RibbitBase;
import felix.com.ribbit.model.firebase.UserData;
import felix.com.ribbit.model.wrapper.UserWrapper;
import felix.com.ribbit.util.JsonUtil;

/**
 * Created by fsoewito on 3/19/2016.
 */
public class RibbitUser extends RibbitBase {

    private static final String TAG = RibbitUser.class.getName();

    private static final Firebase FIREBASE_USERS = new Firebase(RIBBIT_DATA + "/users");

    private static final String KEY_USER = "key-current-user";

    private static UserWrapper mCurrentUser;

    public static Firebase getFirebaseUsers() {
        return FIREBASE_USERS;
    }

    public static void init() {
        try {
            String currentUserJson = mSharedPref.getString(KEY_USER, null);
            if (currentUserJson != null) {
                mCurrentUser = JsonUtil.getObjectMapper().readValue(currentUserJson, UserWrapper.class);
            } else {
                mCurrentUser = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "failed to deserialize user", e);
        }
    }

    public static UserWrapper getCurrentUser() {
        return mCurrentUser;
    }

    public static void setCurrentUser(UserWrapper user) {
        mCurrentUser = user;
        if (mContext == null) {
            throw new IllegalStateException("context not initialized yet");
        }
        try {
            SharedPreferences.Editor editor = mSharedPref.edit();
            editor.putString(KEY_USER, JsonUtil.getObjectMapper().writeValueAsString(user)).apply();
        } catch (JsonProcessingException e) {
            Log.e(TAG, "failed to serialize user", e);
        }
    }

    public static void clearCurrentUser() {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(KEY_USER).apply();
    }

    //fresh login from sign up
    public static void firstLogin(final String email, final String password, final RibbitResultListener resultListener) {
        RibbitBase.getRoot().authWithPassword(email, password, new Firebase.AuthResultHandler() {

            @Override
            public void onAuthenticated(AuthData authData) {
                Log.d(TAG, "first time login success with uid" + authData.getUid());
                resultListener.onFinish();
                resultListener.onSuccess();
            }

            @Override
            public void onAuthenticationError(FirebaseError e) {
                Log.i(TAG, "first time login failed" + e.getMessage());
                deleteUser(email, password, resultListener);
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
        clearCurrentUser();
        mCurrentUser = null;
    }

    public static void deleteUser(String email, String password, RibbitResultListener resultListener) {
        Firebase root = getRoot();
        root.removeUser(email, password, new DeleteResultListener(resultListener));
    }

    private static class DeleteResultListener implements Firebase.ResultHandler {

        private final RibbitResultListener mResultListener;

        public DeleteResultListener(RibbitResultListener resultListener) {
            mResultListener = resultListener;
        }

        @Override
        public void onSuccess() {
            mResultListener.onFinish();
            mResultListener.onSuccess();
        }

        @Override
        public void onError(FirebaseError firebaseError) {
            mResultListener.onFinish();

            int errCode = firebaseError.getCode();
            if (errCode == FirebaseError.USER_DOES_NOT_EXIST) {
                mResultListener.onSuccess();
            } else {
                mResultListener.onError(firebaseError.toException(), firebaseError.getMessage());
            }
        }
    }

}
