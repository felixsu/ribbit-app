package felix.com.ribbit.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.yalantis.phoenix.PullToRefreshView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import felix.com.ribbit.R;
import felix.com.ribbit.adapter.AddFriendAdapter;
import felix.com.ribbit.listener.ItemClickListener;
import felix.com.ribbit.listener.ItemLongClickListener;
import felix.com.ribbit.listener.RibbitValueListener;
import felix.com.ribbit.model.ribbit.RibbitPhone;
import felix.com.ribbit.model.ribbit.RibbitUser;
import felix.com.ribbit.model.wrapper.PhoneWrapper;
import felix.com.ribbit.model.wrapper.UserWrapper;

public class AddFriendsActivity extends AppCompatActivity
        implements ItemClickListener, ItemLongClickListener, PullToRefreshView.OnRefreshListener {

    private static final String TAG = AddFriendsActivity.class.getSimpleName();
    private static final int MAX_FRIEND = 1000;
    private static final int INDEX_OPEN = 0;
    private static final int INDEX_ADD_FRIEND = 1;

    @Bind(R.id.wrapper_container_friend_candidate)
    protected PullToRefreshView mWrapperCandidate;

    @Bind(R.id.container_friend_candidate)
    protected RecyclerView mContainerCandidate;

    private List<PhoneWrapper> mListCandidate;

    private AddFriendAdapter mAdapter;

    private UserWrapper mCurrentUser;
    private RibbitValueListener<PhoneWrapper> mCandidateListener = new RibbitValueListener<PhoneWrapper>() {
        @Override
        public void onFinish() {
            mWrapperCandidate.setRefreshing(false);
        }

        @Override
        public void onSuccess(PhoneWrapper[] data) {
            List<PhoneWrapper> c = Arrays.asList(data);
            AddFriendAdapter adapter = new AddFriendAdapter(AddFriendsActivity.this, c);
            adapter.setItemClickListener(AddFriendsActivity.this);
            adapter.setItemLongClickListener(AddFriendsActivity.this);
            mAdapter = adapter;
            mContainerCandidate.setAdapter(mAdapter);
            mContainerCandidate.invalidate();
        }

        @Override
        public void onError(Throwable e, String message) {

        }
    };

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
        final int REFRESH_DELAY = 2000;
        RibbitPhone.getAll(mCandidateListener);
        mWrapperCandidate.postDelayed(new Runnable() {
            @Override
            public void run() {
                mWrapperCandidate.setRefreshing(false);
            }
        }, REFRESH_DELAY);
        //mWrapperCandidate.setRefreshing(false);
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
