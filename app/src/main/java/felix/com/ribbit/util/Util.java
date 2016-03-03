package felix.com.ribbit.util;

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
}
