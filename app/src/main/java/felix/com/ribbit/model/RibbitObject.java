package felix.com.ribbit.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by fsoewito on 3/20/2016.
 */
public class RibbitObject implements Serializable {

    protected Long mCreatedAt;
    protected Long mUpdatedAt;

    public Long getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(Long createdAt) {
        mCreatedAt = createdAt;
    }

    public Long getUpdatedAt() {
        return mUpdatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        mUpdatedAt = updatedAt;
    }

    public void updateDate() {
        if (mCreatedAt == null) {
            mCreatedAt = new Date().getTime();
        }
        mUpdatedAt = new Date().getTime();
    }
}
