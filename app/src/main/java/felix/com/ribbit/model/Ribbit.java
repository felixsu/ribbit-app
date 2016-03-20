package felix.com.ribbit.model;

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

import felix.com.ribbit.listener.RibbitListener;
import felix.com.ribbit.util.Util;

/**
 * Created by fsoewito on 3/19/2016.
 */
public class Ribbit extends RibbitBase {

    private static final String TAG = Ribbit.class.getName();

    private static final String MASTER_DATA = "ribbit-app-master";
    private static final String KEY_USER = "key-current-user";


    private static Context mContext;
    private static SharedPreferences mSharedPref;
    private static UserWrapper mCurrentUser;


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

    public static void login(final UserWrapper userWrapper, final RibbitListener listener) {
        UserData userData = userWrapper.getData();
        ROOT.authWithPassword(userData.getEmail(), userData.getPassword(), new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                setCurrentUser(userWrapper);
                listener.onFinish();
                listener.onSuccess();
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                listener.onFinish();
                listener.onError(firebaseError.toException());
            }
        });
    }

    public static void login(final String email, final String password, final RibbitListener listener) {
        ROOT.authWithPassword(email, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                final String uid = authData.getUid();

                Firebase firebaseUser = getUsers().child("/" + uid);
                firebaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot result) {
                        UserData u = result.getValue(UserData.class);
                        if (mCurrentUser == null) {
                            mCurrentUser = new UserWrapper(uid, u);
                        } else {
                            mCurrentUser.setUid(uid);
                            mCurrentUser.setData(u);
                        }
                        setCurrentUser(mCurrentUser);
                        listener.onFinish();
                        listener.onSuccess();
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        listener.onFinish();
                        listener.onError(firebaseError.toException());
                    }
                });
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                listener.onFinish();
                listener.onError(firebaseError.toException());
            }
        });
    }

    public static void logout() {
        ROOT.unauth();
    }


}
