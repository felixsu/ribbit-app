package felix.com.ribbit.model.wrapper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import felix.com.ribbit.listener.RibbitResultListener;
import felix.com.ribbit.model.firebase.PhoneData;
import felix.com.ribbit.model.firebase.UserData;
import felix.com.ribbit.model.ribbit.RibbitPhone;

/**
 * Created by fsoewito on 3/21/2016.
 */
public class PhoneWrapper extends RibbitWrapper<PhoneData> {

    public PhoneWrapper() {
    }

    public PhoneWrapper(UserWrapper userWrapper){
        String uid = userWrapper.getId();
        UserData userData = userWrapper.getData();

        String name = userData.getName();
        String phoneNumber = userData.getPhoneNumber();
        String status = userData.getStatus();

        mId = phoneNumber;
        mData = new PhoneData.Builder()
                .setName(name)
                .setStatus(status)
                .setUid(uid)
                .createSimpleUserData();
    }

    @Override
    @JsonIgnore()
    public Firebase getFirebase() {
        return RibbitPhone.getFirebasePhone();
    }
}
