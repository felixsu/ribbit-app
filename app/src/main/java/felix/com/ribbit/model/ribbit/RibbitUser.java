package felix.com.ribbit.model.ribbit;

import android.content.SharedPreferences;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import felix.com.ribbit.listener.RibbitResultListener;
import felix.com.ribbit.listener.RibbitValueListener;
import felix.com.ribbit.model.base.RibbitBase;
import felix.com.ribbit.model.firebase.FriendData;
import felix.com.ribbit.model.firebase.UserData;
import felix.com.ribbit.model.wrapper.FriendWrapper;
import felix.com.ribbit.model.wrapper.UserWrapper;
import felix.com.ribbit.util.JsonUtil;

/**
 * Created by fsoewito on 3/19/2016.
 */
public class RibbitUser extends RibbitBase {

    private static final String TAG = RibbitUser.class.getName();

    private static final Firebase FIREBASE_USERS = new Firebase(RIBBIT_DATA + "/users");

    private static final String KEY_USER = "key-current-user";
    private static final int NOT_LOGGED_IN = 2;
    private static final int LOGGED_IN = 0;

    private static UserWrapper mCurrentUser;
    private static FriendWrapper[] mFriends;
    private static int mLoginState;
    private static int mLoginStateTry;

    public static Firebase getFirebaseUsers() {
        return FIREBASE_USERS;
    }

    public static void init() {
        try {
            String currentUserJson = mSharedPref.getString(KEY_USER, null);
            if (currentUserJson != null) {
                mCurrentUser = JsonUtil.getObjectMapper().readValue(currentUserJson, UserWrapper.class);
                mLoginState = LOGGED_IN;
            } else {
                mCurrentUser = null;
                mLoginState = NOT_LOGGED_IN;
            }
        } catch (Exception e) {
            Log.e(TAG, "failed to deserialize user", e);
        }
    }

    @JsonIgnore
    public static void getUser(String uid, RibbitValueListener<UserWrapper> valueListener) {
        if ((uid != null) && (uid.length() != mCurrentUser.getId().length())) {
            throw new IllegalStateException("requested user id not valid : " + uid);
        }
        Firebase firebase = RibbitUser.getFirebaseUsers().child("/" + uid);
        firebase.addListenerForSingleValueEvent(new UserValueListener(valueListener));
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
        mCurrentUser = null;
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

    public static void login(final String email, final String password, final RibbitResultListener resultListener) {
        mLoginState = NOT_LOGGED_IN;
        mLoginStateTry = 0;
        RibbitBase.getRoot().authWithPassword(email, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                final String uid = authData.getUid();

                Firebase firebaseUser = getFirebaseUsers().child("/" + uid);
                firebaseUser.addListenerForSingleValueEvent(new LoginUserValueListener(uid, resultListener));

                Firebase firebaseFriend = RibbitFriend.getFirebaseFriend().child("/" + uid);
                firebaseFriend.addListenerForSingleValueEvent(new FriendValueListener(resultListener));
            }

            @Override
            public void onAuthenticationError(FirebaseError e) {
                Log.e(TAG, "login error " + e.getCode());
                resultListener.onFinish();
                resultListener.onError(e.toException(), e.getMessage());
            }
        });
    }

    public static void logout() {
        RibbitBase.getRoot().unauth();
        clearCurrentUser();
        RibbitPhone.clearCandidateData();
        RibbitFriend.clearFriendData();

    }

    public static void deleteUser(String email, String password, RibbitResultListener resultListener) {
        Firebase root = getRoot();
        root.removeUser(email, password, new DeleteResultListener(resultListener));
    }

    private static synchronized void checkResult(boolean isSuccess, RibbitResultListener resultListener) {
        Log.i(TAG, "check result called " + mLoginStateTry);
        mLoginStateTry++;
        if (isSuccess) {
            mLoginState--;
        }
        if (mLoginStateTry == 2 && mLoginState == LOGGED_IN) {
            RibbitFriend.persist(mFriends);
            setCurrentUser(mCurrentUser);
            resultListener.onFinish();
            resultListener.onSuccess();
        } else if (mLoginStateTry == 2) {
            mLoginStateTry = 0;
            mLoginState = NOT_LOGGED_IN;
            resultListener.onFinish();
            resultListener.onError(new RuntimeException("login error"), "failed to complete login sequence");
        }
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

    private static class UserValueListener implements ValueEventListener {

        private final RibbitValueListener<UserWrapper> mValueListener;

        public UserValueListener(RibbitValueListener<UserWrapper> valueListener) {
            mValueListener = valueListener;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue() != null) {
                UserWrapper userWrapper = new UserWrapper();
                userWrapper.setId(dataSnapshot.getKey());
                userWrapper.setData(dataSnapshot.getValue(UserData.class));
                mValueListener.onFinish();
                mValueListener.onSuccess(new UserWrapper[]{userWrapper});
            } else {
                Log.i(TAG, "user not found " + dataSnapshot.getKey());
                onCancelled(new FirebaseError(96, "user not found : " + dataSnapshot.getKey()));
            }
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
            mValueListener.onFinish();
            mValueListener.onError(firebaseError.toException(), firebaseError.getMessage());
        }
    }

    private static class LoginUserValueListener implements ValueEventListener {

        private final String mUid;
        private final RibbitResultListener mResultListener;

        public LoginUserValueListener(String uid, RibbitResultListener resultListener) {
            mUid = uid;
            mResultListener = resultListener;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            UserData u = dataSnapshot.getValue(UserData.class);
            if (mCurrentUser == null) {
                mCurrentUser = new UserWrapper(mUid, u);
            } else {
                mCurrentUser.setId(mUid);
                mCurrentUser.setData(u);
            }
            checkResult(true, mResultListener);
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
            checkResult(false, mResultListener);
        }
    }

    private static class FriendValueListener implements ValueEventListener {

        private final RibbitResultListener mResultListener;

        public FriendValueListener(RibbitResultListener resultListener) {
            mResultListener = resultListener;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue() != null) {
                try {
                    HashMap<String, Map<String, String>> map = dataSnapshot.getValue(HashMap.class);
                    mFriends = new FriendWrapper[map.size()];
                    int i = 0;
                    for (Map.Entry<String, Map<String, String>> entry : map.entrySet()) {
                        FriendWrapper friendWrapper = new FriendWrapper();
                        friendWrapper.setId(entry.getKey());
                        friendWrapper.setData(JsonUtil.getObjectMapper().convertValue(entry.getValue(), FriendData.class));
                        mFriends[i] = friendWrapper;
                        i++;
                    }
                    checkResult(true, mResultListener);
                } catch (Exception e) {
                    checkResult(false, mResultListener);
                }
            } else {
                checkResult(true, mResultListener);
            }
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
            checkResult(false, mResultListener);
        }
    }

}
