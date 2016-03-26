package felix.com.ribbit.listener;

import felix.com.ribbit.model.wrapper.RibbitWrapper;

/**
 * Created by fsoewito on 3/27/2016.
 */
public interface RibbitValueListener<T extends RibbitWrapper> {

    void onFinish();

    void onSuccess(T[] data);

    void onError(Throwable e, String message);
}
