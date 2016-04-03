package felix.com.ribbit.model.firebase;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by fsoewito on 3/21/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PhoneData extends RibbitObject implements Serializable {

    protected String mUid;
    protected String mName;
    protected String mStatus;

    public PhoneData() {
    }

    public PhoneData(String uid, String name, String status) {
        mUid = uid;
        mName = name;
        mStatus = status;
    }

    public String getUid() {
        return mUid;
    }

    public void setUid(String uid) {
        mUid = uid;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public static class Builder {
        private String mUid;
        private String mName;
        private String mStatus;
        private String mThumbnail;
        private String mPictureLocalUri;

        public Builder setUid(String uid) {
            mUid = uid;
            return this;
        }

        public Builder setName(String name) {
            mName = name;
            return this;
        }

        public Builder setStatus(String status) {
            mStatus = status;
            return this;
        }

        public PhoneData createSimpleUserData() {
            return new PhoneData(mUid, mName, mStatus);
        }
    }
}
