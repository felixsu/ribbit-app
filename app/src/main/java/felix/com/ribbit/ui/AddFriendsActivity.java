package felix.com.ribbit.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import felix.com.ribbit.R;
import felix.com.ribbit.adapter.AddFriendAdapter;
import felix.com.ribbit.constant.ParseConstants;
import felix.com.ribbit.decoration.DividerItemDecoration;
import felix.com.ribbit.listener.ItemClickListener;
import felix.com.ribbit.listener.ItemLongClickListener;
import felix.com.ribbit.util.Util;

public class AddFriendsActivity extends AppCompatActivity
        implements ItemClickListener, ItemLongClickListener {

    private static final String TAG = AddFriendsActivity.class.getSimpleName();
    private static final int MAX_FRIEND = 1000;
    private static final int STATE_IDLE = 0;
    private static final int STATE_SELECT = 1;
    private static final String TITLE = "Hold to add";

    @Bind(R.id.recyclerView)
    protected RecyclerView mRecyclerView;

    @Bind(R.id.etc_progress_bar)
    protected ProgressBar mProgressBar;

    protected ActionBar mActionBar;
    protected AddFriendAdapter mAdapter;
    protected View mView;
    protected List<ParseUser> mUsers;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    private int mState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        ButterKnife.bind(this);
        initView();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByAscending(ParseConstants.KEY_USERNAME);
        query.setLimit(MAX_FRIEND);
        toggleLoadingScreen();
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                toggleLoadingScreen();
                if (e == null) {
                    mUsers = users;
                    initData();
                } else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AddFriendsActivity.this);
                    dialogBuilder.setMessage(e.getMessage())
                            .setTitle(R.string.editFriendsErrorTitle)
                            .setPositiveButton(android.R.string.ok, null);

                    AlertDialog dialog = dialogBuilder.create();
                    dialog.show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mState == STATE_SELECT) {
            getMenuInflater().inflate(R.menu.menu_state_add_friends, menu);
            menu.getItem(0).setIcon(Util.setTint(
                            getResources().getDrawable(R.drawable.ic_action_add),
                            getResources().getColor(R.color.colorAccent))
            );
            return true;
        }
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            addFriend();
            initData();
            toggleActionBar();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addFriend() {
        final List<ParseUser> friendsSelected = mAdapter.getSelectedItems();
        Log.d(TAG, "number of selected friend = {}" + friendsSelected.size());
        for (ParseUser friend : friendsSelected) {
            mFriendsRelation.add(friend);
        }
        mCurrentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(AddFriendsActivity.this, "failed to add friends", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, e.getMessage());
                } else{
                    mAdapter.clearSelections();
                    mAdapter.remove(friendsSelected);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (mState == STATE_SELECT) {
            mAdapter.clearSelections();
            toggleActionBar();
        } else {
            super.onBackPressed();
        }
    }

    private void initView() {
        Log.d(TAG, "entering initView()");
        mView = getWindow().getDecorView().getRootView();
        mProgressBar.setVisibility(View.INVISIBLE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();

        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setTitle(TITLE);
        }
        Log.d(TAG, "leaving initView()");
    }

    private void toggleLoadingScreen() {
        Log.d(TAG, "toggling loading screen");
        if (mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }


    private void initData() {
        //filter friends data
        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                Log.d(TAG, String.format("Num of user : %d", mUsers.size()));
                Log.d(TAG, String.format("Num of friend : %d", friends.size()));
                if (e == null) {
                    refreshFriendsCandidate(friends);
                } else {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }

    @Override
    public void onItemClick(View view, int index) {
        if (mState == STATE_SELECT) {
            mAdapter.toggleSelection(index);
            mActionBar.setTitle(String.format("%d selected", mAdapter.getSelectedCounts()));
        }
    }

    @Override
    public void OnLongClick(View view, int index) {
        if (mState == STATE_IDLE) {
            mAdapter.toggleSelection(index);
            toggleActionBar();
        }
    }

    private void toggleActionBar() {
        mState = (mState == STATE_IDLE ? STATE_SELECT : STATE_IDLE);
        if (mState == STATE_IDLE) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setTitle(TITLE);
        } else {
            mActionBar.setDisplayHomeAsUpEnabled(false);
            mActionBar.setTitle(String.format("%d selected", mAdapter.getSelectedCounts()));
        }
        supportInvalidateOptionsMenu();
    }

    private void refreshFriendsCandidate(List<ParseUser> friends) {
        List<ParseUser> filteredList = new ArrayList<ParseUser>();
        for (ParseUser user : mUsers) {
            boolean found = false;
            if (user.getObjectId().equals(mCurrentUser.getObjectId())) {
                found = true;
            }
            if (!found) {
                for (ParseUser friend : friends) {
                    if (user.getObjectId().equals(friend.getObjectId())) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                filteredList.add(user);
            }
        }

        mUsers = filteredList;
        mAdapter = new AddFriendAdapter(AddFriendsActivity.this, mUsers);
        mAdapter.setItemLongClickListener(AddFriendsActivity.this);
        mAdapter.setItemClickListener(AddFriendsActivity.this);
        mRecyclerView.setAdapter(mAdapter);

        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(AddFriendsActivity.this);
        layoutManager.scrollToPosition(0);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, null));
    }
}
