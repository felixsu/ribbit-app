package felix.com.ribbit.ui;

import android.app.Activity;
import android.os.Bundle;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import felix.com.ribbit.R;
import felix.com.ribbit.adapter.ChatListAdapter;
import felix.com.ribbit.model.Message;

/**
 * Created by rrsari on 01/03/2016.
 */
public class ChatActivity extends AppCompatActivity {
    Button btnSend;
    EditText etMessage;
    TextView tvChat;
    private static String USER_ID_KEY="user_id";
    private static String BODY_KEY="body";
    static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;
    ListView lvChat;
    ArrayList<Message> mMessages;
    ChatListAdapter mAdapter;
    boolean mFirstLoad;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);
        setTitle(getIntent().getExtras().getString("user_name"));
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
        final String userId = ParseUser.getCurrentUser().getObjectId();
        lvChat.setTranscriptMode(1);
        mFirstLoad = true;
        mAdapter = new ChatListAdapter(ChatActivity.this, userId, mMessages);
        lvChat.setAdapter(mAdapter);
        // When send button is clicked, create message object on Parse
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = etMessage.getText().toString();
                ParseObject message = ParseObject.create("Message");
                message.put(USER_ID_KEY, ParseUser.getCurrentUser().getObjectId());
                message.put(BODY_KEY, data);
                Log.d("ChatActivity", data);
                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Toast.makeText(ChatActivity.this, "Successfully created message on Parse",
                                Toast.LENGTH_SHORT).show();
                        refreshMessages();


                    }
                });

                etMessage.setText(null);
            }
        });
    }

    void refreshMessages() {
        // Construct query to execute
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        // Configure limit and sort order
        query.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);
        query.orderByAscending("createdAt");
        // Execute query to fetch all messages from Parse asynchronously
        // This is equivalent to a SELECT query with SQL
        query.findInBackground(new FindCallback<Message>() {
            public void done(List<Message> messages, ParseException e) {
                if (e == null) {
                    mMessages.clear();
                    mMessages.addAll(messages);
                    mAdapter.notifyDataSetChanged(); // update adapter
                    // Scroll to the bottom of the list on initial load
                    if (mFirstLoad) {
                        lvChat.setSelection(mAdapter.getCount() - 1);
                        mFirstLoad = false;
                    }
                } else {
                    Log.e("message", "Error Loading Messages" + e);
                }
            }
        });
    }

}
