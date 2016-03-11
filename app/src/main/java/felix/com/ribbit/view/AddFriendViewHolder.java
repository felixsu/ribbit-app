package felix.com.ribbit.view;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseUser;

import felix.com.ribbit.R;
import felix.com.ribbit.constant.ParseConstants;
import felix.com.ribbit.listener.ItemClickListener;
import felix.com.ribbit.listener.ItemLongClickListener;
import felix.com.ribbit.util.Util;
import felix.com.ribbit.view.base.BaseViewHolder;

/**
 * Created by fsoewito on 3/11/2016.
 * todo
 * increase performance on inflating layout (generate thumbnails use many resource)
 */
public class AddFriendViewHolder extends BaseViewHolder<ParseUser> {
    private TextView mNameLabel;
    private ImageView mProfileImage;

    public AddFriendViewHolder(View itemView, ItemClickListener itemClickListener, ItemLongClickListener itemLongClickListener) {
        super(itemView, itemClickListener, itemLongClickListener);
        initView();
    }

    @Override
    public void bindView(ParseUser object) {
        mNameLabel.setText(object.getUsername());
        Drawable d = Util.stringToDrawable((String) object.get(ParseConstants.KEY_PROFILE_PICTURE));
        mProfileImage.setImageDrawable(d);
    }

    private void initView() {
        mNameLabel = (TextView) itemView.findViewById(R.id.label_name);
        mProfileImage = (ImageView) itemView.findViewById(R.id.image_profile);
    }
}
