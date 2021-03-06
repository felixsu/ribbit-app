package felix.com.ribbit.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by fsoewito on 3/3/2016.
 */
public class Util {
    private final static ObjectMapper mapper = new ObjectMapper();

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static Drawable tintDrawable(Drawable d, int color) {
        Drawable wrappedDrawable = DrawableCompat.wrap(d);
        DrawableCompat.setTint(wrappedDrawable, color);
        return wrappedDrawable;
    }

    public static String base64Encode(byte[] input, int len) {
        return Base64.encodeToString(input, 0, len, Base64.CRLF);
    }

    public static byte[] base64Decode(String input) {
        return Base64.decode(input, Base64.CRLF);
    }

    public static void hideView(View v) {
        v.setVisibility(View.INVISIBLE);
    }

    public static void showView(View v) {
        v.setVisibility(View.VISIBLE);
    }

    public static void showTextView(TextView v, String text){
        if (v.getVisibility() != View.VISIBLE) {
            v.setVisibility(View.VISIBLE);
        }
        v.setText(text);
    }

    public static void disableOnClickListener(View view) {
        view.setOnClickListener(null);
    }

    public static void enableOnClickListener(View view, View.OnClickListener onClickListener) {
        view.setOnClickListener(onClickListener);
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
