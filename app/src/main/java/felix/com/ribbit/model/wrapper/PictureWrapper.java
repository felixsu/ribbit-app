package felix.com.ribbit.model.wrapper;

import android.net.Uri;
import android.util.Log;

import com.firebase.client.Firebase;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import felix.com.ribbit.model.firebase.PictureData;
import felix.com.ribbit.model.ribbit.RibbitPicture;
import felix.com.ribbit.util.Util;

/**
 * Created by fsoewito on 3/24/2016.
 */
public class PictureWrapper extends RibbitWrapper<PictureData> {
    private static final String TAG = PictureWrapper.class.getName();

    public PictureWrapper(String id, Uri uri) {
        mId = id;
        InputStream is = null;
        try {
            if (uri != null) {
                File profPict = new File(uri.getPath());
                is = new FileInputStream(profPict);

                int len = is.available();
                byte[] byteObject = new byte[len];

                int readLen = is.read(byteObject, 0, len);
                Log.i(TAG, "available " + len + " read " + readLen);

                mData = new PictureData();
                mData.setPicture(Util.base64Encode(byteObject, readLen));
            }
        } catch (IOException e) {
            Log.e(TAG, "error wrapping bitmap");
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.e(TAG, "error wrapping bitmap", e);
                }
            }
        }
    }

    @Override
    public Firebase getFirebase() {
        return RibbitPicture.getFirebasePicture();
    }
}
