package felix.com.ribbit.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import felix.com.ribbit.listener.PersistFileListener;
import felix.com.ribbit.util.BitmapUtils;
import felix.com.ribbit.util.MediaUtil;
import felix.com.ribbit.util.Util;

/**
 * Created by fsoewito on 3/29/2016.
 */
public class ImageLoader extends AsyncTask<String, Integer, Uri> {

    private static final String TAG = ImageLoader.class.getName();

    private final PersistFileListener mPersistFileListener;

    public ImageLoader(PersistFileListener persistFileListener) {
        mPersistFileListener = persistFileListener;
    }

    @Override
    protected Uri doInBackground(String... params) {
        final String uid = params[0];
        final String value = params[1];

        try {
            Uri imageUri = MediaUtil.getProfilePictureUri(uid);
            if (imageUri == null) {
                throw new IOException("uri not created");
            }

            byte[] profilePicture = Util.base64Decode(value);
            Bitmap bm = BitmapFactory.decodeByteArray(profilePicture, 0, profilePicture.length);
            BitmapUtils.writeBitmapToFile(bm, new File(imageUri.getPath()), 100);

            return imageUri;
        } catch (Exception e) {
            Log.d(TAG, e.getMessage(), e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(Uri uri) {
        mPersistFileListener.onFinish(uri);
    }
}
