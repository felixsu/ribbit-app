package felix.com.ribbit.view;

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
 * todo
 * increase performance on inflating layout (generate thumbnails use many resource)
 */
public class AddFriendViewHolder extends BaseViewHolder<PhoneWrapper> {
    private TextView mNameLabel;
    private ImageView mProfileImage;

    public AddFriendViewHolder(View itemView, ItemClickListener itemClickListener, ItemLongClickListener itemLongClickListener) {
        super(itemView, itemClickListener, itemLongClickListener);
        initView();
    }

    @Override
    public void bindView(PhoneWrapper phoneData) {
        mNameLabel.setText(phoneData.getData().getName());
    }

    private void initView() {
        mNameLabel = (TextView) itemView.findViewById(R.id.label_name);
        mProfileImage = (ImageView) itemView.findViewById(R.id.image_profile);
    }
}
