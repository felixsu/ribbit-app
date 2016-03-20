package felix.com.ribbit.listener;

/**
 * Created by fsoewito on 3/19/2016.
 */
public interface RibbitListener {

    void onFinish();

    void onSuccess();

    void onError(Throwable e);
}
