package felix.com.ribbit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import felix.com.ribbit.R;
import felix.com.ribbit.adapter.base.BaseViewAdapter;
import felix.com.ribbit.view.AddFriendViewHolder;

/**
 * Created by fsoewito on 11/21/2015.
 */
public class AddFriendAdapter extends BaseViewAdapter<ParseUser, AddFriendViewHolder> {

    public AddFriendAdapter(Context context, List<ParseUser> items) {
        super(context, items);
    }

    @Override
    public AddFriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_add_friend_item, parent, false);
        return new AddFriendViewHolder(view, mItemClickListener, mItemLongClickListener);
    }

    public void remove(List<ParseUser> friends) {
        List<ParseUser> newList = new ArrayList<>();

        for (ParseUser oldElement : mItems) {
            boolean found = false;
            for (ParseUser friend : friends) {
                if (friend.getObjectId().equals(oldElement.getObjectId())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                newList.add(oldElement);
            }
        }
        mItems = newList;
        notifyDataSetChanged();
    }

}
