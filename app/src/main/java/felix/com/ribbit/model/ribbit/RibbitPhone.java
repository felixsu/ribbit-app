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
import felix.com.ribbit.model.firebase.PhoneData;
import felix.com.ribbit.model.wrapper.PhoneWrapper;
import felix.com.ribbit.util.JsonUtil;

/**
 * Created by fsoewito on 3/19/2016.
 */
public class RibbitPhone extends RibbitBase {

    private static final String TAG = RibbitPhone.class.getName();

    private static final Firebase FIREBASE_PHONE = new Firebase(RIBBIT_DATA + "/phoneNumber");

    private static final String KEY_CANDIDATE = "key-candidate";

    private static PhoneWrapper[] mCandidates;

    public static Firebase getFirebasePhone() {
        return FIREBASE_PHONE;
    }

    public static void init() {
        try {
            String currentUserJson = mSharedPref.getString(KEY_CANDIDATE, null);
            if (currentUserJson != null) {
                mCandidates = JsonUtil.getObjectMapper().readValue(currentUserJson, PhoneWrapper[].class);
            } else {
                mCandidates = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "failed to deserialize candidate", e);
        }
    }

    public static void persist(PhoneWrapper[] candidates) {
        try {
            String candidateJson = JsonUtil.toJson(candidates);
            if (candidateJson != null) {
                Log.d(TAG, "candidates serialization finish successfully");
                mCandidates = candidates;
                mSharedPref.edit().putString(KEY_CANDIDATE, candidateJson).apply();
            } else {
                mCandidates = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "failed to serialize candidate", e);
        }
    }

    public static void clearCandidateData() {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(KEY_CANDIDATE).apply();
        mCandidates = null;
    }

    public static PhoneWrapper[] getLocalCandidates() {
        return mCandidates;
    }

    public static void getAll(String selfUid, RibbitValueListener<PhoneWrapper> valueListener) {
        FIREBASE_PHONE.addListenerForSingleValueEvent(new PhoneValueEventListenerImpl(selfUid, valueListener));
    }

    private static class PhoneValueEventListenerImpl implements ValueEventListener {

        private final RibbitValueListener<PhoneWrapper> mValueListener;
        private final String mSelfPhoneNumber;

        private PhoneValueEventListenerImpl(String selfPhoneNumber, RibbitValueListener<PhoneWrapper> valueListener) {
            mSelfPhoneNumber = selfPhoneNumber;
            mValueListener = valueListener;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if (dataSnapshot != null) {
                Map<String, Map<String, String>> m = dataSnapshot.getValue(HashMap.class);
                PhoneWrapper[] w = new PhoneWrapper[m.size() - 1];
                int i = 0;
                for (Map.Entry<String, Map<String, String>> e : m.entrySet()) {
                    if (!e.getKey().equals(mSelfPhoneNumber)) {
                        w[i] = new PhoneWrapper();
                        w[i].setId(e.getKey());
                        w[i].setData(JsonUtil.getObjectMapper().convertValue(e.getValue(), PhoneData.class));
                        i++;
                    }
                }
                mCandidates = w;
                mValueListener.onFinish();
                mValueListener.onSuccess(mCandidates);
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
