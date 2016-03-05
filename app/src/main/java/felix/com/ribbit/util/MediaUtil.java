package felix.com.ribbit.util;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by fsoewito on 3/5/2016.
 */
public class MediaUtil {
    private static final String TAG = MediaUtil.class.getName();

    public static final int MEDIA_TYPE_IMAGE = 400;
    public static final int MEDIA_TYPE_VIDEO = 401;

    public static final int REQUEST_TAKE_PICTURE = 1001;
    public static final int REQUEST_PICK_PICTURE = 1101;


    public static Uri getOutputMediaFileUri(int mediaTypes, String appName) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appName);
        if (!mediaStorageDir.exists()) {
            if (mediaStorageDir.mkdirs()) {
                Log.e(TAG, "failed to create apps directory");
                return null;
            }
        }
        File mediaFile;
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
        String path = mediaStorageDir.getPath() + File.separator;
        if (mediaTypes == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(path + "IMG_" + timeStamp + ".jpg");
        } else {
            Log.i(TAG, "media not supported");
            return null;
        }
        Uri result = Uri.fromFile(mediaFile);
        Log.d(TAG, "file created, filename : " + result);
        return result;
    }

}
