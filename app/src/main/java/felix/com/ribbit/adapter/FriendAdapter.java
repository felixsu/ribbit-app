package felix.com.ribbit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseUser;

import java.util.List;

import felix.com.ribbit.R;
import felix.com.ribbit.adapter.base.BaseViewAdapter;
import felix.com.ribbit.view.FriendViewHolder;

/**
 * Created by fsoewito on 3/11/2016.
 */
public class FriendAdapter extends BaseViewAdapter<ParseUser, FriendViewHolder> {
    public FriendAdapter(Context context, List<ParseUser> items) {
        super(context, items);
    }

    @Override
    public void remove(List<ParseUser> friends) {

    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_edit_friend_item, parent, false);
        return new FriendViewHolder(view, mItemClickListener, mItemLongClickListener);
    }
}
