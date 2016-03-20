package felix.com.ribbit.model;

/**
 * Created by fsoewito on 3/20/2016.
 */
public class RibbitWrapper<T extends RibbitObject> {
    protected String mUid;
    protected T mData;


    public T getData() {
        return mData;
    }

    public void setData(T data) {
        mData = data;
    }

    public String getUid() {
        return mUid;
    }

    public void setUid(String uid) {
        mUid = uid;
    }
}
