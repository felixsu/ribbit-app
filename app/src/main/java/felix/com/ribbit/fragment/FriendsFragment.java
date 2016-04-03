package felix.com.ribbit.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import felix.com.ribbit.R;
import felix.com.ribbit.adapter.FriendAdapter;
import felix.com.ribbit.decoration.DividerItemDecoration;
import felix.com.ribbit.listener.ItemClickListener;
import felix.com.ribbit.listener.ItemLongClickListener;
import felix.com.ribbit.model.ribbit.RibbitFriend;
import felix.com.ribbit.model.ribbit.RibbitUser;
import felix.com.ribbit.model.wrapper.FriendWrapper;
import felix.com.ribbit.model.wrapper.UserWrapper;
import felix.com.ribbit.view.custom.EmptyRecyclerView;

/**
 * Created by fsoewito on 11/20/2015.
 */
public class FriendsFragment extends Fragment {
    private static final String TAG = FriendsFragment.class.getSimpleName();

    @Bind(R.id.container_friend)
    EmptyRecyclerView mContainerFriend;

    private Activity mActivity;
    private UserWrapper mCurrentUser;
    private List<FriendWrapper> mFriendList;
    private FriendAdapter mAdapter;
    private boolean mUpdating;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_friends, container, false);
        mCurrentUser = RibbitUser.getCurrentUser();
        ButterKnife.bind(this, view);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private void initView(View view) {
        mContainerFriend.setEmptyView(view.findViewById(R.id.container_empty_friend));
    }

    private void initData() {
        mActivity = getActivity();
        mCurrentUser = RibbitUser.getCurrentUser();
        mUpdating = false;
        setupFriends();
        setupAdapter();
        setupContainer();
    }

    private void setupAdapter() {
        mAdapter = new FriendAdapter(getActivity(), mFriendList);
        mAdapter.setItemLongClickListener(new ItemLongClickListenerImpl());
        mAdapter.setItemClickListener(new ItemClickListenerImpl());
    }

    private void setupFriends() {
        mFriendList = new ArrayList<>();
        FriendWrapper[] friends = RibbitFriend.getLocalFriends();
        if ((friends != null) && (friends.length > 0)) {
            for (FriendWrapper friendWrapper : friends) {
                mFriendList.add(friendWrapper);
            }
        }
    }

    private void setupContainer() {
        mContainerFriend.setAdapter(mAdapter);
        mContainerFriend.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        mContainerFriend.addItemDecoration(new DividerItemDecoration(mActivity, null));
    }

    private void unlockScreen() {
        mUpdating = false;
    }

    private void lockScreen() {
        mUpdating = true;
    }

    private class ItemLongClickListenerImpl implements ItemLongClickListener {

        @Override
        public void OnLongClick(View view, int index) {
            Toast.makeText(mActivity, "Long click on " + index, Toast.LENGTH_SHORT).show();
        }
    }

    private class ItemClickListenerImpl implements ItemClickListener {

        @Override
        public void onItemClick(View view, int index) {
            Toast.makeText(mActivity, "Click on " + index, Toast.LENGTH_SHORT).show();
        }
    }
}
