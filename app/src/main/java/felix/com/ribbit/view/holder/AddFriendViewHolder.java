package felix.com.ribbit.view.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

    private RelativeLayout mContainerEmpty;
    private TextView mTextEmpty;
    private View mViewSeparator;


    public AddFriendViewHolder(View itemView, ItemClickListener itemClickListener, ItemLongClickListener itemLongClickListener, boolean isEmpty) {
        super(itemView, itemClickListener, itemLongClickListener);
        initView(isEmpty);
    }

    @Override
    public void bindView(PhoneWrapper phoneData) {
        if (phoneData.getData() != null) {
            mNameLabel.setText(phoneData.getData().getName());
        }
    }

    private void initView(boolean isEmpty) {
        mNameLabel = (TextView) itemView.findViewById(R.id.label_name);
        mProfileImage = (ImageView) itemView.findViewById(R.id.image_profile);

        mViewSeparator = itemView.findViewById(R.id.view_empty_separator);
        mTextEmpty = (TextView) itemView.findViewById(R.id.text_empty_add_friend);
        mContainerEmpty = (RelativeLayout) itemView.findViewById(R.id.container_empty_add_friend);

        if (isEmpty) {
            mContainerEmpty.setVisibility(View.VISIBLE);
            mViewSeparator.setVisibility(View.VISIBLE);
            mTextEmpty.setVisibility(View.VISIBLE);

            mNameLabel.setVisibility(View.GONE);
            mProfileImage.setVisibility(View.GONE);
        } else {
            mContainerEmpty.setVisibility(View.GONE);
            mViewSeparator.setVisibility(View.GONE);
            mTextEmpty.setVisibility(View.GONE);

            mNameLabel.setVisibility(View.VISIBLE);
            mProfileImage.setVisibility(View.VISIBLE);
        }
    }
}
