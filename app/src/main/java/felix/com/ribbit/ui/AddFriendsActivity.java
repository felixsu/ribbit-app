package felix.com.ribbit.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import felix.com.ribbit.R;
import felix.com.ribbit.adapter.AddFriendAdapter;
import felix.com.ribbit.listener.ItemClickListener;
import felix.com.ribbit.listener.ItemLongClickListener;
import felix.com.ribbit.model.ribbit.RibbitUser;
import felix.com.ribbit.model.firebase.UserData;
import felix.com.ribbit.model.wrapper.UserWrapper;

public class AddFriendsActivity extends AppCompatActivity
        implements ItemClickListener, ItemLongClickListener {

    private static final String TAG = AddFriendsActivity.class.getSimpleName();
    private static final int MAX_FRIEND = 1000;
    private static final int INDEX_OPEN = 0;
    private static final int INDEX_ADD_FRIEND = 1;

    @Bind(R.id.recyclerView)
    protected RecyclerView mRecyclerView;

    @Bind(R.id.progress_bar)
    protected ProgressBar mProgressBar;

    private ActionBar mActionBar;
    private AddFriendAdapter mAdapter;
    private View mView;
    private List<UserData> mUserDatas;
    //private ParseRelation<ParseUser> mFriendsRelation;
    private UserWrapper mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        ButterKnife.bind(this);
        initView();

        mCurrentUser = RibbitUser.getCurrentUser();
        //mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        ParseQuery<ParseUser> query = ParseUser.getQuery();
//        query.orderByAscending(ParseConstants.KEY_USERNAME);
//        query.setLimit(MAX_FRIEND);
//        toggleLoadingScreen();
//        query.findInBackground(new FindCallback<ParseUser>() {
//            @Override
//            public void done(List<ParseUser> users, ParseException e) {
//                toggleLoadingScreen();
//                if (e == null) {
//                    mUserDatas = users;
//                    initData();
//                } else {
//                    Log.e(TAG, e.getMessage());
//                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AddFriendsActivity.this);
//                    dialogBuilder.setMessage(e.getMessage())
//                            .setTitle(R.string.editFriendsErrorTitle)
//                            .setPositiveButton(android.R.string.ok, null);
//
//                    AlertDialog dialog = dialogBuilder.create();
//                    dialog.show();
//                }
//            }
//        });
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
//        ParseUser candidate = mAdapter.getItem(pos);
//        mFriendsRelation.add(candidate);
//        mCurrentUser.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                if (e != null) {
//                    Toast.makeText(AddFriendsActivity.this, "failed to add friends", Toast.LENGTH_SHORT).show();
//                    Log.e(TAG, e.getMessage());
//                } else {
//                    mAdapter.remove(pos);
//                }
//            }
//        });

    }

    @Override
    public void onBackPressed() {
            super.onBackPressed();
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
//        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
//            @Override
//            public void done(List<ParseUser> friends, ParseException e) {
//                Log.d(TAG, String.format("Num of user : %d", mUserDatas.size()));
//                Log.d(TAG, String.format("Num of friend : %d", friends.size()));
//                if (e == null) {
//                    refreshFriendsCandidate(friends);
//                } else {
//                    Log.e(TAG, e.getMessage());
//                }
//            }
//        });
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

//    private void refreshFriendsCandidate(List<ParseUser> friends) {
//        List<ParseUser> filteredList = new ArrayList<>();
//        for (ParseUser user : mUserDatas) {
//            boolean found = false;
//            if (user.getObjectId().equals(mCurrentUser.getObjectId())) {
//                found = true;
//            }
//            if (!found) {
//                for (ParseUser friend : friends) {
//                    if (user.getObjectId().equals(friend.getObjectId())) {
//                        found = true;
//                        break;
//                    }
//                }
//            }
//            if (!found) {
//                filteredList.add(user);
//            }
//        }
//
//        mUserDatas = filteredList;
//        mAdapter = new AddFriendAdapter(AddFriendsActivity.this, mUserDatas);
//        mAdapter.setItemLongClickListener(AddFriendsActivity.this);
//        mAdapter.setItemClickListener(AddFriendsActivity.this);
//        mRecyclerView.setAdapter(mAdapter);
//
//        RecyclerView.LayoutManager layoutManager =
//                new LinearLayoutManager(AddFriendsActivity.this);
//        layoutManager.scrollToPosition(0);
//        mRecyclerView.setLayoutManager(layoutManager);
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, null));
//    }

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
