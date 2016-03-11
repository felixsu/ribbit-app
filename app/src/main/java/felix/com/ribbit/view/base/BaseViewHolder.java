package felix.com.ribbit.view.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import felix.com.ribbit.listener.ItemClickListener;
import felix.com.ribbit.listener.ItemLongClickListener;

/**
 * Created by fsoewito on 3/11/2016.
 */
public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    protected ItemClickListener mItemClickListener;
    protected ItemLongClickListener mItemLongClickListener;

    public BaseViewHolder(View itemView, ItemClickListener itemClickListener,
                          ItemLongClickListener itemLongClickListener) {
        super(itemView);
        this.mItemClickListener = itemClickListener;
        this.mItemLongClickListener = itemLongClickListener;

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
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
            return true;
        } else {
            return false;
        }
    }

    public abstract void bindView(T object);
}
