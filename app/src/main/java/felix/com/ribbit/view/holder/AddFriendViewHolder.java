package felix.com.ribbit.view.holder;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import felix.com.ribbit.R;
import felix.com.ribbit.listener.ItemClickListener;
import felix.com.ribbit.listener.ItemLongClickListener;
import felix.com.ribbit.listener.PersistFileListener;
import felix.com.ribbit.listener.PictureValueListener;
import felix.com.ribbit.model.ribbit.RibbitPicture;
import felix.com.ribbit.model.wrapper.PhoneWrapper;
import felix.com.ribbit.transformation.CircleTransform;
import felix.com.ribbit.util.MediaUtil;
import felix.com.ribbit.view.base.BaseViewHolder;

/**
 * Created by fsoewito on 3/11/2016.
 */
public class AddFriendViewHolder extends BaseViewHolder<PhoneWrapper> {
    private static final String TAG = AddFriendViewHolder.class.getName();

    private Context mContext;

    private TextView mNameLabel;
    private ImageView mProfileImage;

    private RelativeLayout mContainerEmpty;
    private TextView mTextEmpty;
    private View mViewSeparator;
    private PersistFileListener mPersistFileListener = new PersistFileListener() {
        @Override
        public void onFinish(Throwable e, Uri uri) {
            if (e == null) {
                if (uri != null) {
                    Picasso.with(mContext).load(uri).into(mProfileImage);
                }
            } else {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    };

    public AddFriendViewHolder(View itemView,
                               ItemClickListener itemClickListener, ItemLongClickListener itemLongClickListener, Context context) {
        super(itemView, itemClickListener, itemLongClickListener);
        initView();
        mContext = context;
    }

    @Override
    public void bindView(PhoneWrapper phoneData) {
        Log.d(TAG, "binding " + phoneData.getId());
        if (phoneData.getData() != null) {
            mNameLabel.setText(phoneData.getData().getName());
            String uid = phoneData.getData().getUid();

            try {
                Uri imageUri = MediaUtil.getProfilePictureUri(uid);
                if (imageUri == null) {
                    throw new IOException("uri not created");
                }

                File f = new File(imageUri.getPath());
                if (f.exists()) {
                    Picasso.with(mContext).load(imageUri).transform(new CircleTransform()).into(mProfileImage);
                } else {
                    Firebase pictureFirebase = RibbitPicture.getFirebasePicture().child(uid);
                    pictureFirebase.addListenerForSingleValueEvent(new PictureValueListener(uid, mPersistFileListener));
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
                mProfileImage.setImageResource(R.drawable.ic_user_default);
            }
        }
    }

    private void initView() {
        mNameLabel = (TextView) itemView.findViewById(R.id.text_name);
        mProfileImage = (ImageView) itemView.findViewById(R.id.image_profile);
    }
}
