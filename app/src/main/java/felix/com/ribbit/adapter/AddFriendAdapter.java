package felix.com.ribbit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import felix.com.ribbit.R;
import felix.com.ribbit.adapter.base.SingleSelectableAdapter;
import felix.com.ribbit.model.wrapper.PhoneWrapper;
import felix.com.ribbit.view.AddFriendViewHolder;

/**
 * Created by fsoewito on 11/21/2015.
 */
public class AddFriendAdapter extends SingleSelectableAdapter<PhoneWrapper, AddFriendViewHolder> {

    public AddFriendAdapter(Context context, List<PhoneWrapper> items) {
        super(context, items);
    }

    @Override
    public AddFriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_add_friend_item, parent, false);
        return new AddFriendViewHolder(view, mItemClickListener, mItemLongClickListener);
    }

}
