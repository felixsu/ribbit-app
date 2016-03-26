package felix.com.ribbit.model.ribbit;

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

    private static final String KEY_USER = "key-current-candidate";
    private static PhoneWrapper[] mCandidates;
    private static ValueEventListener mValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Log.d(TAG, "data changed");
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    };

    public static Firebase getFirebasePhone() {
        return FIREBASE_PHONE;
    }

    public static void init() {
        try {
            String currentUserJson = mSharedPref.getString(KEY_USER, null);
            if (currentUserJson != null) {
                mCandidates = JsonUtil.getObjectMapper().readValue(currentUserJson, PhoneWrapper[].class);
            } else {
                mCandidates = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "failed to deserialize user");
        }
    }

    public static void getAll(RibbitValueListener<PhoneWrapper> valueListener) {
        FIREBASE_PHONE.addListenerForSingleValueEvent(new PhoneValueEventListenerImpl(valueListener));
    }

    private static class PhoneValueEventListenerImpl implements ValueEventListener {

        private final RibbitValueListener<PhoneWrapper> mValueListener;

        private PhoneValueEventListenerImpl(RibbitValueListener<PhoneWrapper> valueListener) {
            mValueListener = valueListener;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if (dataSnapshot != null) {
                Map<String, Map<String, String>> m = dataSnapshot.getValue(HashMap.class);
                PhoneWrapper[] w = new PhoneWrapper[m.size()];
                int i = 0;
                for (Map.Entry<String, Map<String, String>> e : m.entrySet()) {
                    w[i] = new PhoneWrapper();
                    w[i].setId(e.getKey());
                    w[i].setData(JsonUtil.getObjectMapper().convertValue(e.getValue(), PhoneData.class));
                    i++;
                }
                mCandidates = w;
                mValueListener.onFinish();
                mValueListener.onSuccess(mCandidates);
            }

        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    }


}
