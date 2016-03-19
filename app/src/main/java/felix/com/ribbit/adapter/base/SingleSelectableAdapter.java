package felix.com.ribbit.adapter.base;

import android.content.Context;
import android.view.ViewGroup;

import java.util.List;

import felix.com.ribbit.view.base.BaseViewHolder;

/**
 * Created by fsoewito on 3/12/2016.
 */
public class SingleSelectableAdapter<T, V extends BaseViewHolder<T>> extends BaseViewAdapter<T,V> {
    public SingleSelectableAdapter(Context context, List<T> items) {
        super(context, items);
    }

    @Override
    public V onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    public void remove(int pos){
        mItems.remove(pos);
        notifyItemRemoved(pos);
    }

}
