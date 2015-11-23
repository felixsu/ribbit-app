package felix.com.ribbit.ui;

import android.app.AlertDialog;
import android.net.Uri;
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

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import felix.com.ribbit.R;
import felix.com.ribbit.adapter.ParseUserAdapter;
import felix.com.ribbit.constant.ParseConstants;
import felix.com.ribbit.decoration.DividerItemDecoration;
import felix.com.ribbit.listener.ItemClickListener;
import felix.com.ribbit.listener.ItemLongClickListener;

public class AddFriendsActivity extends AppCompatActivity
        implements ItemClickListener, ItemLongClickListener {

    private static final String TAG = AddFriendsActivity.class.getSimpleName();
    private static final int MAX_FRIEND = 1000;
    private static final int STATE_IDLE = 0;
    private static final int STATE_SELECT = 1;
    private static final String TITLE = "Hold to add";

    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;

    ActionBar mActionBar;
    ParseUserAdapter mAdapter;
    View mView;
    private int mState;

    protected List<ParseUser> mUsers;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_friends);
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
        List<ParseUser> friendsSelected = mAdapter.getSelectedItems();
        for (ParseUser friend: friendsSelected){
            mFriendsRelation.add(friend);
            mCurrentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            });
        }
        mAdapter.clearSelections();
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
        mView = getWindow().getDecorView().getRootView();
        mProgressBar.setVisibility(View.INVISIBLE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();

        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setTitle(TITLE);
        }
    }

    private void toggleLoadingScreen() {
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
                Log.i(TAG, String.format("Num of user : %d", mUsers.size()));
                Log.i(TAG, String.format("Num of friend : %d", friends.size()));
                if (e == null) {
                    for (int i = 0; i < mUsers.size(); i++) {
                        ParseUser user = mUsers.get(i);
                        for (ParseUser friend : friends) {
                            if (user.getObjectId().equals(friend.getObjectId())
                                    || user.getObjectId().equals(mCurrentUser.getObjectId())) {
                                mUsers.remove(i);
                                i--;
                                break;
                            }
                        }
                    }
                    mAdapter = new ParseUserAdapter(AddFriendsActivity.this, mUsers);
                    mAdapter.setItemLongClickListener(AddFriendsActivity.this);
                    mAdapter.setItemClickListener(AddFriendsActivity.this);
                    mRecyclerView.setAdapter(mAdapter);

                    RecyclerView.LayoutManager layoutManager =
                            new LinearLayoutManager(AddFriendsActivity.this);
                    layoutManager.scrollToPosition(0);
                    mRecyclerView.setLayoutManager(layoutManager);

                    RecyclerView.ItemDecoration decoration =
                            new DividerItemDecoration(AddFriendsActivity.this, null);
                    mRecyclerView.addItemDecoration(decoration);
                } else {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }

    @Override
    public void onItemClick(View view, int index) {
        ParseUser parseUser = mUsers.get(index);
        if (mState == STATE_SELECT) {
            mAdapter.toggleSelection(index);
            mActionBar.setTitle(String.format("%d selected", mAdapter.getSelectedCounts()));
        }
    }

    @Override
    public void OnLongClick(View view, int index) {
        ParseUser parseUser = mUsers.get(index);
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
}
