package felix.com.ribbit.watcher;

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by fsoewito on 3/5/2016.
 */
public class CustomTextWatcher implements TextWatcher {
    TextInputLayout v;

    public CustomTextWatcher(TextInputLayout v) {
        this.v = v;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (v.isErrorEnabled()) {
            v.setError(null);
            v.setErrorEnabled(false);
        }
    }
}
