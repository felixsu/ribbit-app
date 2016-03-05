package felix.com.ribbit.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;
import android.util.Log;

import felix.com.ribbit.receiver.MyReceiver;

public class MyService extends IntentService {
    public static final String TAG = MyService.class.getName();

    public static final int SERVICE_SUCCESS = 512;
    public static final int SERVICE_FAIL = 511;

    private static final String ACTION_FETCH = "felix.com.ribbit.action.fetch";

    // TODO: Rename parameters
    private static final String RECEIVER_NAME = "felix.com.ribbit.extra.RECEIVER_NAME";
    private static final String EXTRA_PREFERENCE_NAME = "felix.com.ribbit.extra.PREFERENCE_NAME";

    public MyService() {
        super(TAG);
    }

    public static void startActionFetch(Context context, MyReceiver receiver, String receiverName) {
        Log.d(TAG, "action fetch started");
        Intent intent = new Intent(context, MyService.class);
        intent.setAction(ACTION_FETCH);
        intent.putExtra(RECEIVER_NAME, receiverName);
        intent.putExtra(receiverName, receiver);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "intent is coming");
        if (intent != null) {
            Log.d(TAG, "intent not null");
            final String action = intent.getAction();
            if (ACTION_FETCH.equals(action)) {
                Log.d(TAG, "action equals to FETCH_ACTION");
                final String receiverName = intent.getStringExtra(RECEIVER_NAME);
                final ResultReceiver receiver = intent.getParcelableExtra(receiverName);
                handleActionFetch(receiver);
            }
        }
    }

    private void handleActionFetch(ResultReceiver receiver) {
        Log.d(TAG, "handle fetch action");
        if (receiver != null) {
            Log.d(TAG, "receiver not null");
            try {
                Thread.sleep(900l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            receiver.send(SERVICE_SUCCESS, null);
        } else {
            Log.w(TAG, "receiver null");
        }
    }
}
