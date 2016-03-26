package felix.com.ribbit.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import felix.com.ribbit.R;
import felix.com.ribbit.adapter.base.SingleSelectableAdapter;
import felix.com.ribbit.model.wrapper.PhoneWrapper;
import felix.com.ribbit.view.holder.FriendViewHolder;

/**
 * Created by fsoewito on 3/11/2016.
 */
public class FriendAdapter extends SingleSelectableAdapter<PhoneWrapper, FriendViewHolder> {
    public FriendAdapter(Context context, @NonNull List<PhoneWrapper> items) {
        super(context, items);
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_edit_friend_item, parent, false);
        return new FriendViewHolder(view, mItemClickListener, mItemLongClickListener);
    }
}
