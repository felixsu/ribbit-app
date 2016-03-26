package felix.com.ribbit.application;

import android.app.Application;

import com.firebase.client.Firebase;

import felix.com.ribbit.model.ribbit.RibbitGlobal;
import io.filepicker.Filepicker;

/**
 * Created by fsoewito on 11/18/2015.
 *
 */
public class RibbitApplication extends Application {

    private static final String FILE_PICKER_API_KEY = "AeExLGVTSkmKDB6x0atUsz";
    private static final String FILE_PICKER_APP_NAME = "Ribbit-App";

    @Override
    public void onCreate() {
        super.onCreate();

        Filepicker.setKey(FILE_PICKER_API_KEY);
        Filepicker.setAppName(FILE_PICKER_APP_NAME);

        Firebase.setAndroidContext(this);

        RibbitGlobal.setAndroidContext(this);
    }
}
