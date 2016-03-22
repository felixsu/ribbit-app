package felix.com.ribbit.model.firebase;

import felix.com.ribbit.util.Util;

/**
 * Created by fsoewito on 3/21/2016.
 */
public class PhoneData extends RibbitObject {

    protected String mUid;
    protected String mName;
    protected String mStatus;
    protected String mPictureUri;
    protected String mPictureLocalUri;

    public PhoneData(String uid, String name, String status, String pictureUri, String pictureLocalUri) {
        mUid = uid;
        mName = name;
        mStatus = status;
        mPictureUri = pictureUri;
        mPictureLocalUri = pictureLocalUri;
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

    public String getPictureUri() {
        if (mPictureUri != null) {
            return new String(Util.base64Decode(mPictureUri));
        } else {
            return null;
        }
    }

    public void setPictureUri(String pictureUri) {
        byte[] uriBytes = pictureUri.getBytes();
        String uriString = Util.base64Encode(uriBytes, uriBytes.length);
        mPictureUri = uriString;
    }

    public String getPictureLocalUri() {
        if (mPictureLocalUri != null) {
            return new String(Util.base64Decode(mPictureLocalUri));
        } else {
            return null;
        }
    }

    public void setPictureLocalUri(String pictureLocalUri) {
        byte[] uriBytes = pictureLocalUri.getBytes();
        String uriString = Util.base64Encode(uriBytes, uriBytes.length);
        mPictureLocalUri = uriString;
    }

    public static class Builder {
        private String mUid;
        private String mName;
        private String mStatus;
        private String mPictureUri;
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

        public Builder setPictureUri(String pictureUri) {
            mPictureUri = pictureUri;
            return this;
        }

        public Builder setPictureLocalUri(String pictureLocalUri) {
            mPictureLocalUri = pictureLocalUri;
            return this;
        }

        public PhoneData createSimpleUserData() {
            return new PhoneData(mUid, mName, mStatus, mPictureUri, mPictureLocalUri);
        }
    }
}
