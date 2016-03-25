package felix.com.ribbit.model.ribbit;

import com.firebase.client.Firebase;

import felix.com.ribbit.model.base.RibbitBase;

/**
 * Created by fsoewito on 3/24/2016.
 */
public class RibbitPicture extends RibbitBase {

    protected static final Firebase FOREBASE_PICTURES = new Firebase(RIBBIT_DATA + "/pictures");

    public static Firebase getFirebasePicture() {
        return FOREBASE_PICTURES;
    }
}
