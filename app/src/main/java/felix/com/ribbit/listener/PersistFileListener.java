package felix.com.ribbit.listener;

import android.net.Uri;

/**
 * Created by fsoewito on 3/29/2016.
 */
public interface PersistFileListener {
    void onFinish(Throwable e, Uri uri);
}
