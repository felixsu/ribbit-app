package felix.com.ribbit.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import felix.com.ribbit.R;
import felix.com.ribbit.adapter.AddFriendAdapter;
import felix.com.ribbit.listener.ItemClickListener;
import felix.com.ribbit.listener.ItemLongClickListener;
import felix.com.ribbit.model.ribbit.RibbitUser;
import felix.com.ribbit.model.wrapper.PhoneWrapper;
import felix.com.ribbit.model.wrapper.UserWrapper;
import felix.com.ribbit.view.EmptyRecyclerView;

public class AddFriendsActivity extends AppCompatActivity
        implements ItemClickListener, ItemLongClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = AddFriendsActivity.class.getSimpleName();
    private static final int MAX_FRIEND = 1000;
    private static final int INDEX_OPEN = 0;
    private static final int INDEX_ADD_FRIEND = 1;

    @Bind(R.id.wrapper_container_friend_candidate)
    protected SwipeRefreshLayout mWrapperCandidate;

    @Bind(R.id.container_friend_candidate)
    protected EmptyRecyclerView mContainerCandidate;

    @Bind(R.id.text_empty_view)
    protected TextView mTextEmptyView;

    private List<PhoneWrapper> mListCandidate;

    private AddFriendAdapter mAdapter;

    private UserWrapper mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void addFriend(final int pos) {


    }

    @Override
    public void onBackPressed() {
            super.onBackPressed();
    }

    private void initView() {
        Log.d(TAG, "entering initView()");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Log.d(TAG, "leaving initView()");
    }

    private void initData() {
        setupWrapper();
        setupBaseData();
        setupAdapter();
        setupContainerView();
    }

    private void setupWrapper() {
        mWrapperCandidate.setOnRefreshListener(this);
    }

    private void setupContainerView() {
        mContainerCandidate.setEmptyView(mTextEmptyView);
        mContainerCandidate.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mContainerCandidate.setAdapter(mAdapter);
    }

    private void setupBaseData() {
        mCurrentUser = RibbitUser.getCurrentUser();
        mListCandidate = new ArrayList<>();
    }

    private void setupAdapter() {
        mAdapter = new AddFriendAdapter(this, mListCandidate);
        mAdapter.setItemClickListener(this);
        mAdapter.setItemLongClickListener(this);
    }

    @Override
    public void onItemClick(View view, int index) {
        Toast.makeText(this, "[DEBUG] on click : " + index, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnLongClick(View view, int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddFriendsActivity.this);
        builder.setItems(R.array.add_friend_dialog, new MyDialogOnClickListener(index));
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onRefresh() {
        mWrapperCandidate.setRefreshing(false);
    }


    public class MyDialogOnClickListener implements DialogInterface.OnClickListener {
        private int pos;

        public MyDialogOnClickListener(int pos) {
            this.pos = pos;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case INDEX_OPEN :
                    Toast.makeText(AddFriendsActivity.this, "[DEBUG] on click : ", Toast.LENGTH_SHORT).show();
                    break;
                case INDEX_ADD_FRIEND :
                    addFriend(pos);
                    break;
                default:
                    break;
            }
        }
    }
}
