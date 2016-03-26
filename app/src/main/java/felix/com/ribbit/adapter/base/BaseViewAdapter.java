package felix.com.ribbit.adapter.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import felix.com.ribbit.listener.ItemClickListener;
import felix.com.ribbit.listener.ItemLongClickListener;
import felix.com.ribbit.view.base.BaseViewHolder;

/**
 * Created by fsoewito on 3/11/2016.
 */
public abstract class BaseViewAdapter<T, V extends BaseViewHolder<T>> extends RecyclerView.Adapter<V> {
    protected Context mContext;
    protected ItemClickListener mItemClickListener;
    protected ItemLongClickListener mItemLongClickListener;

    protected List<T> mItems;

    public BaseViewAdapter(Context context, @NonNull List<T> items) {
        mContext = context;
        mItems = items;
    }

    @Override
    public void onBindViewHolder(V holder, int position) {
        holder.bindView(mItems.get(position));
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

    public T getItem(int pos){
        return mItems.get(pos);
    }
}
