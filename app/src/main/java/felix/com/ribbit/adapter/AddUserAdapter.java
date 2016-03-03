package felix.com.ribbit.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import felix.com.ribbit.R;
import felix.com.ribbit.listener.ItemClickListener;
import felix.com.ribbit.listener.ItemLongClickListener;

/**
 * Created by fsoewito on 11/21/2015.
 *
 */
public class AddUserAdapter extends RecyclerView.Adapter<AddUserAdapter.ParseUserViewHolder>{
    protected Context mContext;
    private ItemClickListener mItemClickListener;
    private ItemLongClickListener mItemLongClickListener;

    protected List<ParseUser> mParseUsers;
    protected HashMap<Integer, ParseUser> mSelectedItems= new HashMap<>();
    boolean hideEmail=false;

    public AddUserAdapter(Context context, List<ParseUser> parseUsers, Boolean hide) {
        mContext = context;
        mParseUsers = parseUsers;
        hideEmail=hide;
    }

    @Override
    public ParseUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_add_friend_item, parent, false);
        return new ParseUserViewHolder(view, mItemClickListener, mItemLongClickListener);
    }

    @Override
    public void onBindViewHolder(ParseUserViewHolder holder, int position) {
        holder.bindParseUser(mParseUsers.get(position));
        holder.itemView.setActivated(mSelectedItems.containsKey(position));
    }

    @Override
    public int getItemCount() {
        return mParseUsers.size();
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public void setItemLongClickListener(ItemLongClickListener itemLongClickListener) {
        mItemLongClickListener = itemLongClickListener;
    }

    public void toggleSelection(int pos) {
        if (mSelectedItems.containsKey(pos)) {
            mSelectedItems.remove(pos);
        } else {
            mSelectedItems.put(pos, mParseUsers.get(pos));
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        mSelectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedCounts() {
        return mSelectedItems.size();
    }

    public List<ParseUser> getSelectedItems() {
        List<ParseUser> result = new ArrayList<>(mSelectedItems.size());

        Iterator<Integer> iterator = mSelectedItems.keySet().iterator();
        if (iterator.hasNext()){
            result.add(mSelectedItems.get(iterator.next()));
        }
        return result;
    }

    public class ParseUserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        TextView mNameLabel;
        TextView mEmailLabel;

        ItemClickListener mItemClickListener;
        ItemLongClickListener mItemLongClickListener;

        public ParseUserViewHolder(View itemView, ItemClickListener itemClickListener,
                                   ItemLongClickListener itemLongClickListener) {
            super(itemView);

            mNameLabel = (TextView) itemView.findViewById(R.id.label_name);
            mEmailLabel = (TextView) itemView.findViewById(R.id.label_email);

            mItemClickListener = itemClickListener;
            mItemLongClickListener = itemLongClickListener;

            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);
        }

        public void bindParseUser(ParseUser parseUser) {
            mNameLabel.setText(parseUser.getUsername());
            if(!hideEmail){
                mEmailLabel.setVisibility(View.GONE);
            }
            mEmailLabel.setText(parseUser.getEmail());
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getLayoutPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mItemLongClickListener != null) {
                mItemLongClickListener.OnLongClick(v, getLayoutPosition());
            }
            return true;
        }
    }

}
