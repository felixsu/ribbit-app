package felix.com.ribbit.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import felix.com.ribbit.R;
import felix.com.ribbit.adapter.AddFriendAdapter;
import felix.com.ribbit.decoration.DividerItemDecoration;
import felix.com.ribbit.listener.ItemClickListener;
import felix.com.ribbit.listener.ItemLongClickListener;
import felix.com.ribbit.listener.RibbitValueListener;
import felix.com.ribbit.model.ribbit.RibbitFriend;
import felix.com.ribbit.model.ribbit.RibbitPhone;
import felix.com.ribbit.model.ribbit.RibbitUser;
import felix.com.ribbit.model.wrapper.FriendWrapper;
import felix.com.ribbit.model.wrapper.PhoneWrapper;
import felix.com.ribbit.model.wrapper.UserWrapper;
import felix.com.ribbit.util.Util;
import felix.com.ribbit.view.custom.EmptyRecyclerView;

public class AddFriendsActivity extends AppCompatActivity {

    private static final String TAG = AddFriendsActivity.class.getSimpleName();
    private static final int INDEX_OPEN = 0;
    private static final int INDEX_ADD_FRIEND = 1;

    @Bind(R.id.container_friend_candidate)
    protected EmptyRecyclerView mContainerCandidate;

    private List<PhoneWrapper> mListCandidate;

    private AddFriendAdapter mAdapter;

    private UserWrapper mCurrentUser;

    private List<FriendWrapper> mListFriend;

    private RibbitValueListener<PhoneWrapper> mCandidateListener = new RibbitValueListener<PhoneWrapper>() {
        @Override
        public void onFinish() {
            Log.d(TAG, "candidate refresh finish");
            Toast.makeText(AddFriendsActivity.this, "refresh finish", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSuccess(PhoneWrapper[] data) {
            List<PhoneWrapper> c = new ArrayList<>();
            for (PhoneWrapper phoneWrapper : data) {
                boolean found = false;
                for (FriendWrapper friendWrapper : mListFriend){
                    if (friendWrapper.getId().equals(phoneWrapper.getData().getUid())){
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    c.add(phoneWrapper);
                }
            }
            AddFriendAdapter adapter = new AddFriendAdapter(AddFriendsActivity.this, c);
            adapter.setItemClickListener(mItemClickListener);
            adapter.setItemLongClickListener(mItemLongClickListener);
            mAdapter = adapter;
            mListCandidate = c;
            mContainerCandidate.setAdapter(mAdapter);
            mContainerCandidate.invalidate();
        }

        @Override
        public void onError(Throwable e, String message) {
            Log.e(TAG, message, e);
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
        getMenuInflater().inflate(R.menu.menu_add_friends, menu);
        updateMenuStyle(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            String selfPhoneNumber = mCurrentUser.getData().getPhoneNumber();
            Log.d(TAG, "refresh with current id " + selfPhoneNumber);
            RibbitPhone.getAll(selfPhoneNumber, mCandidateListener);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mListCandidate.size() != 0) {
            PhoneWrapper[] localCandidates = new PhoneWrapper[mListCandidate.size()];
            localCandidates = mListCandidate.toArray(localCandidates);
            Log.d(TAG, "candidate array size = " + localCandidates.length);
            RibbitPhone.persist(localCandidates);
        }
    }

    private void updateMenuStyle(Menu menu) {
        menu.getItem(0).setIcon(Util.tintDrawable(
                getResources().getDrawable(R.drawable.ic_action_refresh),
                getResources().getColor(R.color.colorAccent)));
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
        setupBaseData();
        setupAdapter();
        setupContainerView();
    }

    private void setupContainerView() {
        mContainerCandidate.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mContainerCandidate.setEmptyView(findViewById(R.id.container_empty_add_friend));
        mContainerCandidate.setAdapter(mAdapter);
        mContainerCandidate.addItemDecoration(new DividerItemDecoration(this, null));
    }

    private void setupBaseData() {
        mCurrentUser = RibbitUser.getCurrentUser();

        PhoneWrapper[] localCandidates = RibbitPhone.getLocalCandidates();
        FriendWrapper[] localFriends = RibbitFriend.getLocalFriends();

        mListCandidate = new ArrayList<>();
        if ((localCandidates != null) && (localCandidates.length > 0)) {
            for (PhoneWrapper phoneWrapper : localCandidates) {
                mListCandidate.add(phoneWrapper);
            }
        }

        mListFriend = new ArrayList<>();
        if ((localFriends != null) && (localFriends.length > 0)){
            for (FriendWrapper friendWrapper : localFriends){
                mListFriend.add(friendWrapper);
            }
        }
    }

    private void setupAdapter() {
        mAdapter = new AddFriendAdapter(this, mListCandidate);
        mAdapter.setItemClickListener(mItemClickListener);
        mAdapter.setItemLongClickListener(mItemLongClickListener);
    }

    private ItemClickListener mItemClickListener = new ItemClickListener() {
        @Override
        public void onItemClick(View view, int index) {
            showProfile(index);
        }
    };

    private ItemLongClickListener mItemLongClickListener = new ItemLongClickListener() {
        @Override
        public void OnLongClick(View view, int index) {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddFriendsActivity.this);
            builder.setItems(R.array.add_friend_dialog, new MyDialogOnClickListener(index));
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    };

    private void showProfile(int pos) {
        Log.i(TAG, "show profile " + pos);
    }

    private void addFriend(final int pos) {
        Log.i(TAG, "add friend " + pos);
        RibbitUser.getUser(mAdapter.getItem(pos).getData().getUid(), new AddFriendListener(pos));
    }

    public class MyDialogOnClickListener implements DialogInterface.OnClickListener {
        private int pos;

        public MyDialogOnClickListener(int pos) {
            this.pos = pos;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case INDEX_OPEN:
                    showProfile(pos);
                    break;
                case INDEX_ADD_FRIEND:
                    addFriend(pos);
                    break;
                default:
                    break;
            }
        }
    }

    private class AddFriendListener implements RibbitValueListener<UserWrapper> {

        private final int mPosition;

        private AddFriendListener(int position) {
            mPosition = position;
        }

        @Override
        public void onFinish() {
            Log.i(TAG, "querry user finished");
        }

        @Override
        public void onSuccess(UserWrapper[] data) {
            if (data != null && data.length > 0){
                UserWrapper retrievedUser = data[0];
                Log.i(TAG, "user found : " + retrievedUser.getId());

                FriendWrapper friendWrapper = new FriendWrapper(retrievedUser);
                mListFriend.add(friendWrapper);
                mAdapter.remove(mPosition);
            } else{
                Log.i(TAG, "user not found");
            }
        }

        @Override
        public void onError(Throwable e, String message) {
            Log.e(TAG, e.getMessage(), e);
        }
    }
}
