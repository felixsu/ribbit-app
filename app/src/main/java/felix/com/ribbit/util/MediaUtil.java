package felix.com.ribbit.util;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import felix.com.ribbit.constant.Constants;

/**
 * Created by fsoewito on 3/5/2016.
 */
public class MediaUtil {
    public static final int MEDIA_TYPE_IMAGE = 400;
    public static final int MEDIA_TYPE_VIDEO = 401;
    public static final int REQUEST_TAKE_PICTURE = 1001;
    public static final int REQUEST_PICK_PICTURE = 1101;
    public static final int MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final String TAG = MediaUtil.class.getName();

    public static File getMediaDir() throws IOException {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), Constants.APP_NAME);
        if (!mediaStorageDir.exists()) {
            if (mediaStorageDir.mkdirs()) {
                throw new IOException("failed to create apps directory");
            }
        }
        Log.i(TAG, "file created at " + mediaStorageDir.getPath());
        return mediaStorageDir;
    }

    public static Uri getOutputMediaFileUri(int mediaTypes) throws IOException {
        File mediaStorageDir = getMediaDir();
        File mediaFile;
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
        String path = mediaStorageDir.getPath() + File.separator;
        if (mediaTypes == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(path + "IMG_" + timeStamp + ".jpg");
        } else {
            throw new IOException("media not supported");
        }
        Uri result = Uri.fromFile(mediaFile);
        Log.d(TAG, "file created, filename : " + result);
        return result;
    }

    public static Uri getProfilePictureUri(String uid) throws IOException {
        if (uid == null) {
            throw new IOException("uid is null");
        }

        File mediaStorageDir = getMediaDir();
        File mediaFile;
        String path = mediaStorageDir.getPath() + File.separator;
        mediaFile = new File(path + uid + ".jpg");
        Log.d(TAG, "create file at " + mediaFile.getPath());
        Uri result = Uri.fromFile(mediaFile);
        return result;
    }

    public static boolean isProfilePictureAvailable(String uid) throws IOException {
        if (uid == null) {
            throw new IOException("uid is null");
        }
        Log.d(TAG, "finding picture with uid " + uid);
        File mediaStorageDir = getMediaDir();
        String path = mediaStorageDir.getPath() + File.separator + uid + ".jpg";

        File fPicture = new File(path);
        if (fPicture.exists()) {
            Log.d(TAG, "picture found");
            return true;
        } else {
            Log.i(TAG, "picture not found " + fPicture.getPath());
            return false;
        }
    }

}
