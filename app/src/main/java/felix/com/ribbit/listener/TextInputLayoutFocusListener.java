package felix.com.ribbit.listener;

import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;

/**
 * Created by fsoewito on 3/5/2016.
 */
public class TextInputLayoutFocusListener implements View.OnFocusChangeListener {
    private static final String TAG = TextInputLayoutFocusListener.class.getName();

    final TextInputLayout til;

    public TextInputLayoutFocusListener(TextInputLayout til) {
        this.til = til;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            Log.d(TAG, "get focus");
            if (til != null && til.isErrorEnabled()) {
                til.setError(null);
                til.setErrorEnabled(false);
            }
        } else {
            Log.d(TAG, "miss focus");
        }

    }
}
