package felix.com.ribbit.model.wrapper;

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

    public PhoneWrapper(UserWrapper userWrapper){
        String uid = userWrapper.getId();
        UserData userData = userWrapper.getData();

        String name = userData.getName();
        String phoneNumber = userData.getPhoneNumber();
        String status = userData.getStatus();
        String pictureUri = userData.getPictureUri();
        String pictureLocalUri = userData.getPictureLocalUri();

        mId = phoneNumber;
        mData = new PhoneData.Builder()
                .setName(name)
                .setPictureLocalUri(pictureLocalUri)
                .setStatus(status)
                .setPictureUri(pictureUri)
                .setUid(uid)
                .createSimpleUserData();
    }

    public void store(RibbitResultListener resultListener){
        Firebase firebasePhone = RibbitPhone.getFirebasePhone().child("/" + mId);
        firebasePhone.setValue(mData, new StoreResultListener(resultListener));
    }

    private class StoreResultListener implements Firebase.CompletionListener {

        final private RibbitResultListener mResultListener;

        public StoreResultListener(RibbitResultListener resultListener) {
            mResultListener = resultListener;
        }

        @Override
        public void onComplete(FirebaseError e, Firebase firebase) {
            mResultListener.onFinish();
            if (e == null){
                mResultListener.onSuccess();
            }else{
                mResultListener.onError(e.toException(), e.getMessage());
            }
        }
    }
}
