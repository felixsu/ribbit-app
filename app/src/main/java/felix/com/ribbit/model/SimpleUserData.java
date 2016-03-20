package felix.com.ribbit.model;

import java.io.Serializable;

/**
 * Created by fsoewito on 3/20/2016.
 */
public class SimpleUserData extends RibbitObject implements Serializable {

    protected String name;
    protected String phoneNumber;
    protected String status;

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
}
