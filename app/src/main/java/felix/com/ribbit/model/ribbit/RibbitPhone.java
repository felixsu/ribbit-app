package felix.com.ribbit.model.ribbit;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import felix.com.ribbit.listener.RibbitResultListener;
import felix.com.ribbit.model.base.RibbitBase;
import felix.com.ribbit.model.firebase.PhoneData;
import felix.com.ribbit.model.wrapper.PhoneWrapper;
import felix.com.ribbit.model.wrapper.UserWrapper;

/**
 * Created by fsoewito on 3/19/2016.
 */
public class RibbitPhone extends RibbitBase {

    private static final Firebase FIREBASE_PHONE = new Firebase(RIBBIT_DATA + "/phoneNumber");

    public static Firebase getFirebasePhone() {
        return FIREBASE_PHONE;
    }


}
