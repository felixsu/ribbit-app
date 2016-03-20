package felix.com.ribbit.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import felix.com.ribbit.R;
import felix.com.ribbit.adapter.ChatListAdapter;
import felix.com.ribbit.model.Message;

/**
 * Created by rrsari on 01/03/2016.
 */
public class ChatActivity extends AppCompatActivity {
    static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;
    private static String USER_ID_KEY="user_id";
    private static String RECIPIENT_ID="recipient_id";
    private static String BODY_KEY="body";
    Button btnSend;
    EditText etMessage;
    TextView tvChat;
    ListView lvChat;
    ArrayList<Message> mMessages;
    ChatListAdapter mAdapter;
    boolean mFirstLoad;
    String clientId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);
        clientId=getIntent().getExtras().getString("client_id");
        setTitle(getIntent().getExtras().getString("user_name"));
        refreshMessages();
        setupMessagePosting();


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    void setupMessagePosting() {
        mMessages = new ArrayList<>();
        etMessage = (EditText) findViewById(R.id.etMessage);
        btnSend = (Button) findViewById(R.id.btnSendMessage);
        lvChat = (ListView) findViewById(R.id.lvChat);
//        final String userId = ParseUser.getCurrentUser().getObjectId();
//        lvChat.setTranscriptMode(1);
//        mFirstLoad = true;
//        mAdapter = new ChatListAdapter(ChatActivity.this, userId, mMessages);
//        lvChat.setAdapter(mAdapter);
//        // When send button is clicked, create message object on Parse
//        btnSend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String data = etMessage.getText().toString();
//                ParseObject message = ParseObject.create("Message");
//                message.put(USER_ID_KEY, ParseUser.getCurrentUser().getObjectId());
//                message.put(BODY_KEY, data);
//                message.put(RECIPIENT_ID,clientId);
//                Log.d("ChatActivity", data);
//                message.saveInBackground(new SaveCallback() {
//                    @Override
//                    public void done(ParseException e) {
//                       /* Toast.makeText(ChatActivity.this, "Successfully created message on Parse",
//                                Toast.LENGTH_SHORT).show();*/
//                        refreshMessages();
//
//
//                    }
//                });
//
//                etMessage.setText(null);
//            }
//        });
    }

    void refreshMessages() {
    }

}
