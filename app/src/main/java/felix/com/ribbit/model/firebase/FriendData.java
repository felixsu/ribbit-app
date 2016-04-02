package felix.com.ribbit.model.firebase;

/**
 * Created by fsoewito on 3/22/2016.
 */
public class FriendData extends RibbitObject {

    private String mPhoneNumber;
    private String mEmail;
    private String mName;
    private String mStatus;

    public FriendData() {
    }

    public FriendData(UserData userData){
        mPhoneNumber = userData.getPhoneNumber();
        mEmail = userData.getEmail();
        mName = userData.getName();
        mStatus = userData.getStatus();
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
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
}
