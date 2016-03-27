package felix.com.ribbit.application;

import android.app.Application;

import com.firebase.client.Firebase;

import felix.com.ribbit.model.ribbit.RibbitGlobal;

/**
 * Created by fsoewito on 11/18/2015.
 *
 */
public class RibbitApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(this);

        RibbitGlobal.setAndroidContext(this);
    }
}
