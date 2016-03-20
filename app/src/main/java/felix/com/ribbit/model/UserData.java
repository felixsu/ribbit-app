package felix.com.ribbit.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

import felix.com.ribbit.util.Util;

/**
 * Created by fsoewito on 3/19/2016.
 */

public class UserData extends SimpleUserData implements Serializable {
    private static final String TAG = UserData.class.getName();

    protected String mEmail;

    @JsonIgnore(value = true)
    protected transient String mPassword;

    protected String mUsername;

    protected String mPictureUri;

    protected String mPictureLocalUri;

    public UserData() {
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
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

}
