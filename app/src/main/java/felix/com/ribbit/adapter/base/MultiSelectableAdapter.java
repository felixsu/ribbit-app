package felix.com.ribbit.adapter.base;

import android.content.Context;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import felix.com.ribbit.view.base.BaseViewHolder;

/**
 * Created by fsoewito on 3/12/2016.
 */
public abstract class MultiSelectableAdapter<T, V extends BaseViewHolder<T>> extends BaseViewAdapter<T, V> {

    protected List<T> mItems;
    protected Map<Integer, T> mSelectedItems = new HashMap<>();

    public MultiSelectableAdapter(Context context, List<T> items) {
        super(context, items);
    }

    @Override
    public void onBindViewHolder(V holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.itemView.setActivated(mSelectedItems.containsKey(position));
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
}
