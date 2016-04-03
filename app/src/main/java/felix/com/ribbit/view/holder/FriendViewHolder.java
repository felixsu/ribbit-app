package felix.com.ribbit.view.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import felix.com.ribbit.R;
import felix.com.ribbit.listener.ItemClickListener;
import felix.com.ribbit.listener.ItemLongClickListener;
import felix.com.ribbit.model.wrapper.FriendWrapper;
import felix.com.ribbit.transformation.CircleTransform;
import felix.com.ribbit.view.base.BaseViewHolder;

/**
 * Created by fsoewito on 3/11/2016.
 */
public class FriendViewHolder extends BaseViewHolder<FriendWrapper> {

    private final Context mContext;
    private TextView mNameLabel;
    private TextView mStatusLabel;
    private ImageView mProfileImage;

    public FriendViewHolder(View itemView, ItemClickListener itemClickListener, ItemLongClickListener itemLongClickListener, Context context) {
        super(itemView, itemClickListener, itemLongClickListener);
        mContext = context;
        initView();
    }

    @Override
    public void bindView(FriendWrapper object) {
        mNameLabel.setText(object.getData().getName());
        mStatusLabel.setText(object.getData().getStatus());
        Picasso.with(mContext).load(R.drawable.ic_user_default).transform(new CircleTransform()).into(mProfileImage);
    }

    private void initView() {
        mNameLabel = (TextView) itemView.findViewById(R.id.text_name);
        mStatusLabel = (TextView) itemView.findViewById(R.id.field_status);
        mProfileImage = (ImageView) itemView.findViewById(R.id.image_profile);
    }


}
