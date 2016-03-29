package felix.com.ribbit.view.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import felix.com.ribbit.R;
import felix.com.ribbit.listener.ItemClickListener;
import felix.com.ribbit.listener.ItemLongClickListener;
import felix.com.ribbit.model.wrapper.PhoneWrapper;
import felix.com.ribbit.view.base.BaseViewHolder;

/**
 * Created by fsoewito on 3/11/2016.
 */
public class FriendViewHolder extends BaseViewHolder<PhoneWrapper> {
    private TextView mNameLabel;
    private TextView mStatusLabel;
    private ImageView mProfileImage;

    public FriendViewHolder(View itemView, ItemClickListener itemClickListener, ItemLongClickListener itemLongClickListener) {
        super(itemView, itemClickListener, itemLongClickListener);
        initView();
    }

    @Override
    public void bindView(PhoneWrapper object) {
        mNameLabel.setText(object.getData().getName());
        mStatusLabel.setText(object.getData().getStatus());
    }

    private void initView() {
        mNameLabel = (TextView) itemView.findViewById(R.id.text_name);
        mStatusLabel = (TextView) itemView.findViewById(R.id.field_status);
        mProfileImage = (ImageView) itemView.findViewById(R.id.image_profile);
    }


}
