package felix.com.ribbit.model.wrapper;

import felix.com.ribbit.model.base.RibbitBase;
import felix.com.ribbit.model.firebase.RibbitObject;

/**
 * Created by fsoewito on 3/20/2016.
 */
public class RibbitWrapper<T extends RibbitObject> {
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
}
