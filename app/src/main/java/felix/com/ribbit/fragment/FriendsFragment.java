package felix.com.ribbit.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import felix.com.ribbit.R;
import felix.com.ribbit.adapter.FriendAdapter;
import felix.com.ribbit.listener.ItemClickListener;
import felix.com.ribbit.listener.ItemLongClickListener;
import felix.com.ribbit.model.Ribbit;
import felix.com.ribbit.model.UserWrapper;

/**
 * Created by fsoewito on 11/20/2015.
 */
public class FriendsFragment extends Fragment implements ItemClickListener, ItemLongClickListener {
    private static final String TAG = FriendsFragment.class.getSimpleName();

    protected UserWrapper mCurrentUser;
    FriendAdapter mAdapter;
    RecyclerView mRecyclerView;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edit_friends, container, false);
        mCurrentUser = Ribbit.getCurrentUser();
        if (mCurrentUser != null) {
        }
        ButterKnife.bind(getActivity());
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewFriends);
        return view;
    }

    private void initData() {
        lockScreen();
    }

    private void emptyFriendListLayout() {
        view.findViewById(R.id.noFriendList).setVisibility(View.VISIBLE);
    }

    private void FriendListLayout() {
        view.findViewById(R.id.noFriendList).setVisibility(View.GONE);
    }

    private void lockScreen() {
        view.findViewById(R.id.progressBarFriendList).setVisibility(View.VISIBLE);

    }

    private void unlockScreen() {
        view.findViewById(R.id.progressBarFriendList).setVisibility(View.GONE);

    }

    @Override
    public void onItemClick(View view, int index) {

    }

    @Override
    public void OnLongClick(View view, int index) {

    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
