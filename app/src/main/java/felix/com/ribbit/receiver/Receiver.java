package felix.com.ribbit.receiver;

import android.os.Bundle;

public interface Receiver {
    void onReceiveResult(int resultCode, Bundle resultData);
}