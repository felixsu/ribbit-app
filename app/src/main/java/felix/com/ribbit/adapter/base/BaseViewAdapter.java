package felix.com.ribbit.adapter.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import felix.com.ribbit.listener.ItemClickListener;
import felix.com.ribbit.listener.ItemLongClickListener;
import felix.com.ribbit.view.base.BaseViewHolder;

/**
 * Created by fsoewito on 3/11/2016.
 */
public abstract class BaseViewAdapter<T, V extends BaseViewHolder> extends RecyclerView.Adapter<V> {
    protected Context mContext;
    protected ItemClickListener mItemClickListener;
    protected ItemLongClickListener mItemLongClickListener;

    protected List<T> mItems;
    protected Map<Integer, T> mSelectedItems = new HashMap<>();

    public BaseViewAdapter(Context context, List<T> items) {
        mContext = context;
        mItems = items;
    }

    @Override
    public void onBindViewHolder(V holder, int position) {
        holder.bindView(mItems.get(position));
        holder.itemView.setActivated(mSelectedItems.containsKey(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
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
            mSelectedItems.put(pos, mItems.get(pos));
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

    public List<T> getSelectedItems() {
        List<T> result = new ArrayList<>(mSelectedItems.size());

        for (Map.Entry<Integer, T> entry : mSelectedItems.entrySet()) {
            result.add(entry.getValue());
        }
        return result;
    }

    public abstract void remove(List<T> friends);
//    {
//        List<T> newList = new ArrayList<>();
//
//        for (T oldElement : mItems){
//            boolean found = false;
//            for (T friend : friends){
//                if (friend.getObjectId().equals(oldElement.getObjectId())){
//                    found = true;
//                    break;
//                }
//            }
//            if (!found){
//                newList.add(oldElement);
//            }
//        }
//        mParseUsers = newList;
//        notifyDataSetChanged();
//    }

}
