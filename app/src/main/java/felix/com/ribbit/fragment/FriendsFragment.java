package felix.com.ribbit.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import felix.com.ribbit.R;
import felix.com.ribbit.adapter.FriendsAdapter;
import felix.com.ribbit.adapter.ParseUserAdapter;
import felix.com.ribbit.constant.ParseConstants;
import felix.com.ribbit.decoration.DividerItemDecoration;
import felix.com.ribbit.dto.FriendsDto;

/**
 * Created by fsoewito on 11/20/2015.
 *
 */
public class FriendsFragment extends Fragment {
    private static final String TAG = FriendsFragment.class.getSimpleName();
    FriendsAdapter adapter;
    public FriendsDto friendList;
    protected List<ParseUser> mUsers;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    ParseUserAdapter mAdapter;
    RecyclerView mRecyclerView;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_edit_friends, container, false);
        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
        ButterKnife.bind(getActivity());
        mRecyclerView=(RecyclerView)view.findViewById(R.id.recyclerViewFriends);
        return view;
    }

    private void initData() {
        lockScreen();
        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                Log.i(TAG, String.format("Num of friend : %d", friends.size()));
                if(friends.size()>0) {
                    FriendListLayout();
                }
                else{
                    emptyFriendListLayout();
                }
                if (e == null) {
                    for (int i = 0; i < friends.size(); i++) {
                        ParseUser user = friends.get(i);
                        ParseUser friend=new ParseUser();
                        friend.setUsername(user.getUsername());
                        friend.setEmail(user.getEmail());
                        mUsers.add(friend);
                    }
                    mAdapter = new ParseUserAdapter(getContext(), friends);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                    layoutManager.scrollToPosition(0);
                    mRecyclerView.setLayoutManager(layoutManager);
                    RecyclerView.ItemDecoration decoration = new DividerItemDecoration(getContext(), null);
                    mRecyclerView.addItemDecoration(decoration);
                    mRecyclerView.setAdapter(mAdapter);
                    unlockScreen();
                } else {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }

    private void emptyFriendListLayout() {
        view.findViewById(R.id.noFriendList).setVisibility(View.VISIBLE);
    }
    private void FriendListLayout() {
        view.findViewById(R.id.noFriendList).setVisibility(View.GONE);
    }
    private void lockScreen(){
        view.findViewById(R.id.progressBarFriendList).setVisibility(View.VISIBLE);

    }

    private void unlockScreen(){
        view.findViewById(R.id.progressBarFriendList).setVisibility(View.GONE);

    }

    @Override
    public void onResume() {

        super.onResume();

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByAscending(ParseConstants.KEY_USERNAME);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null) {
                    mUsers = users;
                    initData();
                } else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                    dialogBuilder.setMessage(e.getMessage())
                            .setTitle(R.string.editFriendsErrorTitle)
                            .setPositiveButton(android.R.string.ok, null);

                    AlertDialog dialog = dialogBuilder.create();
                    dialog.show();
                }
            }
        });
    }
}
