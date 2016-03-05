package felix.com.ribbit.util;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Base64;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

/**
 * Created by fsoewito on 3/3/2016.
 */
public class Util {
    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static Drawable setTint(Drawable d, int color) {
        Drawable wrappedDrawable = DrawableCompat.wrap(d);
        DrawableCompat.setTint(wrappedDrawable, color);
        return wrappedDrawable;
    }

    public static String base64Encode(byte[] input, int len) {
        return Base64.encodeToString(input, 0, len, Base64.CRLF);
    }

    public static String ImageViewToString(ImageView v) {
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache();
        Bitmap bm = v.getDrawingCache();

        ByteArrayOutputStream o = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, o);
        byte[] b = o.toByteArray();

        return base64Encode(b, b.length);

    }
}
