package felix.com.ribbit.model.ribbit;

import android.content.SharedPreferences;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import felix.com.ribbit.listener.RibbitValueListener;
import felix.com.ribbit.model.base.RibbitBase;
import felix.com.ribbit.model.firebase.FriendData;
import felix.com.ribbit.model.wrapper.FriendWrapper;
import felix.com.ribbit.util.JsonUtil;

/**
 * Created by fsoewito on 3/22/2016.
 */
public class RibbitFriend extends RibbitBase {

    protected static final Firebase FIREBASE_FRIENDS = new Firebase(RIBBIT_DATA + "/friends");
    private static final String TAG = RibbitFriend.class.getName();
    private static final String KEY_FRIEND = "key-friend";
    private static FriendWrapper[] mFriends;

    public static Firebase getFirebaseFriend() {
        return FIREBASE_FRIENDS;
    }

    public static void init() {
        try {
            String currentUserJson = mSharedPref.getString(KEY_FRIEND, null);
            if (currentUserJson != null) {
                mFriends = JsonUtil.getObjectMapper().readValue(currentUserJson, FriendWrapper[].class);
            } else {
                mFriends = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "failed to deserialize candidate", e);
        }
    }

    public static void persist(FriendWrapper[] friends) {
        try {
            String candidateJson = JsonUtil.toJson(friends);
            if (candidateJson != null) {
                Log.d(TAG, "friends serialization finish successfully");
                mFriends = friends;
                mSharedPref.edit().putString(KEY_FRIEND, candidateJson).apply();
            } else {
                mFriends = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "failed to serialize candidate", e);
        }
    }

    public static void clearFriendData() {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(KEY_FRIEND).apply();
        mFriends= null;
    }

    public static FriendWrapper[] getLocalFriends() {
        return mFriends;
    }

    public static void getAll(String selfUid, RibbitValueListener<FriendWrapper> valueListener) {
        Firebase firebase = FIREBASE_FRIENDS.child("/" + selfUid);
        firebase.addListenerForSingleValueEvent(new FriendValueEventListenerImpl(valueListener));
    }


    private static class FriendValueEventListenerImpl implements ValueEventListener {

        private final RibbitValueListener<FriendWrapper> mValueListener;

        public FriendValueEventListenerImpl(RibbitValueListener<FriendWrapper> valueListener) {
            mValueListener = valueListener;
        }


        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot != null) {
                Map<String, Map<String, String>> m = dataSnapshot.getValue(HashMap.class);
                FriendWrapper[] w = new FriendWrapper[m.size()];
                int i = 0;
                for (Map.Entry<String, Map<String, String>> e : m.entrySet()) {
                    w[i] = new FriendWrapper();
                    w[i].setId(e.getKey());
                    w[i].setData(JsonUtil.getObjectMapper().convertValue(e.getValue(), FriendData.class));
                    i++;
                }
                mFriends = w;
                mValueListener.onFinish();
                mValueListener.onSuccess(mFriends);
            } else {
                onCancelled(new FirebaseError(96, "no data received"));
            }
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
            mValueListener.onFinish();
            mValueListener.onError(firebaseError.toException(), firebaseError.getMessage());
        }
    }

}
