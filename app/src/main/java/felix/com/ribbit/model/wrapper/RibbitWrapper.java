package felix.com.ribbit.model.wrapper;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.io.Serializable;

import felix.com.ribbit.listener.RibbitResultListener;
import felix.com.ribbit.model.firebase.RibbitObject;

/**
 * Created by fsoewito on 3/20/2016.
 */
public abstract class RibbitWrapper<T extends RibbitObject> implements Serializable {
    protected String mId;
    protected T mData;

    public T getData() {
        return mData;
    }

    public void setData(T data) {
        mData = data;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public void store(RibbitResultListener resultListener) {
        Firebase fb = getFirebase().child("/" + mId);
        mData.updateDate();
        fb.setValue(mData, new StoreResultListener(resultListener));
    }

    public abstract Firebase getFirebase();

    private class StoreResultListener implements Firebase.CompletionListener {

        final private RibbitResultListener mResultListener;

        public StoreResultListener(RibbitResultListener resultListener) {
            mResultListener = resultListener;
        }

        @Override
        public void onComplete(FirebaseError e, Firebase firebase) {
            mResultListener.onFinish();
            if (e == null) {
                mResultListener.onSuccess();
            } else {
                mResultListener.onError(e.toException(), e.getMessage());
            }
        }
    }
}
