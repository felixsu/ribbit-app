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

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;

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
import felix.com.ribbit.model.ribbit.RibbitPhone;
import felix.com.ribbit.model.ribbit.RibbitUser;
import felix.com.ribbit.model.wrapper.PhoneWrapper;
import felix.com.ribbit.model.wrapper.UserWrapper;

public class AddFriendsActivity extends AppCompatActivity
        implements ItemClickListener, ItemLongClickListener {

    private static final String TAG = AddFriendsActivity.class.getSimpleName();
    private static final int INDEX_OPEN = 0;
    private static final int INDEX_ADD_FRIEND = 1;

    @Bind(R.id.wrapper_container_friend_candidate)
    protected MaterialRefreshLayout mWrapperCandidate;

    @Bind(R.id.container_friend_candidate)
    protected RecyclerView mContainerCandidate;

    private List<PhoneWrapper> mListCandidate;

    private AddFriendAdapter mAdapter;

    private UserWrapper mCurrentUser;

    private RibbitValueListener<PhoneWrapper> mCandidateListener = new RibbitValueListener<PhoneWrapper>() {
        @Override
        public void onFinish() {
            mWrapperCandidate.finishRefresh();
        }

        @Override
        public void onSuccess(PhoneWrapper[] data) {
            List<PhoneWrapper> c = new ArrayList<>();
            for (PhoneWrapper phoneWrapper : data) {
                c.add(phoneWrapper);
            }
            AddFriendAdapter adapter = new AddFriendAdapter(AddFriendsActivity.this, c);
            adapter.setItemClickListener(AddFriendsActivity.this);
            adapter.setItemLongClickListener(AddFriendsActivity.this);
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
    private MaterialRefreshListener mRefreshListener = new MaterialRefreshListener() {
        @Override
        public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
            RibbitPhone.getAll(mCandidateListener);
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
        mWrapperCandidate.setMaterialRefreshListener(mRefreshListener);
    }

    private void setupContainerView() {
        mContainerCandidate.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mContainerCandidate.setAdapter(mAdapter);
        mContainerCandidate.addItemDecoration(new DividerItemDecoration(this, null));
    }

    private void setupBaseData() {
        mCurrentUser = RibbitUser.getCurrentUser();

        PhoneWrapper[] localCandidates = RibbitPhone.getLocalCandidates();
        mListCandidate = new ArrayList<>();
        if (localCandidates != null) {
            for (PhoneWrapper phoneWrapper : localCandidates) {
                mListCandidate.add(phoneWrapper);
            }
        }
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

    private void showProfile(int pos) {
        Toast.makeText(AddFriendsActivity.this, "[DEBUG] show profile : " + pos, Toast.LENGTH_SHORT).show();
    }

    private void addFriend(final int pos) {
        Toast.makeText(AddFriendsActivity.this, "[DEBUG] add friend : " + pos, Toast.LENGTH_SHORT).show();
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


}
