package felix.com.ribbit.view;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import felix.com.ribbit.R;
import felix.com.ribbit.listener.ItemClickListener;
import felix.com.ribbit.listener.ItemLongClickListener;
import felix.com.ribbit.model.firebase.UserData;
import felix.com.ribbit.util.Util;
import felix.com.ribbit.view.base.BaseViewHolder;

/**
 * Created by fsoewito on 3/11/2016.
 * todo
 * increase performance on inflating layout (generate thumbnails use many resource)
 */
public class AddFriendViewHolder extends BaseViewHolder<UserData> {
    private TextView mNameLabel;
    private ImageView mProfileImage;
    private Resources mRes;

    public AddFriendViewHolder(View itemView, ItemClickListener itemClickListener, ItemLongClickListener itemLongClickListener, Resources res) {
        super(itemView, itemClickListener, itemLongClickListener);
        mRes = res;
        initView();
    }

    @Override
    public void bindView(UserData userData) {
        mNameLabel.setText(userData.getUsername());
        Drawable d = Util.stringToDrawable(userData.getPictureLocalUri());
        mProfileImage.setImageDrawable(d);
    }

    private void initView() {
        mNameLabel = (TextView) itemView.findViewById(R.id.label_name);
        mProfileImage = (ImageView) itemView.findViewById(R.id.image_profile);
    }
}
