package felix.com.ribbit.model.firebase;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * Created by fsoewito on 3/19/2016.
 */

public class UserData extends RibbitObject implements Serializable {
    private static final String TAG = UserData.class.getName();

    protected String name;
    protected String phoneNumber;
    protected String status;

    @JsonIgnore(value = true)
    protected transient String mEmail;

    @JsonIgnore(value = true)
    protected transient String mPassword;

    protected String mUsername;

    public UserData() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public void prepareForSignUp(){
        mPassword = null;
        mUsername = null;
    }

}
