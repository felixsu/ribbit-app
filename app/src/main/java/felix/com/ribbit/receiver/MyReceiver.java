package felix.com.ribbit.receiver;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by fsoewito on 1/2/2016.
 */
@SuppressLint("ParcelCreator")
public class MyReceiver extends ResultReceiver {
    private Receiver mReceiver;

    public MyReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    public Receiver getReceiver() {
        return mReceiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }
}
