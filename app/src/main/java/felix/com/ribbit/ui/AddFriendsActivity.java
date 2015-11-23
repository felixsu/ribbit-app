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

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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

    protected List<ParseUser> mUsers;
    protected ParseUser[] mParseUsers = new ParseUser[]{};
    private int mState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_friends);
        ButterKnife.bind(this);

        initView();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();

        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setTitle(TITLE);
        }
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
                    mParseUsers = new ParseUser[mUsers.size()];
                    int i = 0;
                    for (ParseUser user : users) {
                        mParseUsers[i] = user;
                        i++;
                    }

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
    public void onBackPressed() {
        if (mState == STATE_SELECT){
            mAdapter.clearSelections();
            toggleActionBar();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        mView = getWindow().getDecorView().getRootView();
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private void toggleLoadingScreen() {
        if (mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }


    private void initData() {
        mAdapter =
                new ParseUserAdapter(AddFriendsActivity.this, mParseUsers);
        mAdapter.setItemLongClickListener(this);
        mAdapter.setItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(AddFriendsActivity.this);
        layoutManager.scrollToPosition(0);
        mRecyclerView.setLayoutManager(layoutManager);

        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(this, null);
        mRecyclerView.addItemDecoration(decoration);

    }

    @Override
    public void onItemClick(View view, int index) {
        ParseUser parseUser = mParseUsers[index];
        if (mState == STATE_SELECT){
            mAdapter.toggleSelection(index);
            mActionBar.setTitle(String.format("%d selected", mAdapter.getSelectedCounts()));
        }
    }

    @Override
    public void OnLongClick(View view, int index) {
        ParseUser parseUser = mParseUsers[index];
        if (mState == STATE_IDLE){
            mAdapter.toggleSelection(index);
            toggleActionBar();
        }
    }

    private void toggleActionBar(){
        mState = (mState == STATE_IDLE?STATE_SELECT:STATE_IDLE);
        if (mState == STATE_IDLE){
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setTitle(TITLE);
        }else{
            mActionBar.setDisplayHomeAsUpEnabled(false);
            mActionBar.setTitle(String.format("%d selected", mAdapter.getSelectedCounts()));
        }

        supportInvalidateOptionsMenu();
    }
}
