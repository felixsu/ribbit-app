package felix.com.ribbit.listener;

/**
 * Created by fsoewito on 3/19/2016.
 * interface to listen on firebase operation
 */


public interface RibbitResultListener {

    void onFinish();

    void onSuccess();

    void onError(Throwable e, String message);
}
