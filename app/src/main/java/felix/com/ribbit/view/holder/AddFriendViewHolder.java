package felix.com.ribbit.view.holder;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.squareup.picasso.Picasso;

import felix.com.ribbit.R;
import felix.com.ribbit.listener.ItemClickListener;
import felix.com.ribbit.listener.ItemLongClickListener;
import felix.com.ribbit.listener.PersistFileListener;
import felix.com.ribbit.model.firebase.PictureData;
import felix.com.ribbit.model.ribbit.RibbitPicture;
import felix.com.ribbit.model.wrapper.PhoneWrapper;
import felix.com.ribbit.task.ImageLoader;
import felix.com.ribbit.view.base.BaseViewHolder;

/**
 * Created by fsoewito on 3/11/2016.
 * todo
 * increase performance on inflating layout (generate thumbnails use many resource)
 */
public class AddFriendViewHolder extends BaseViewHolder<PhoneWrapper> implements PersistFileListener {
    private static final String TAG = AddFriendViewHolder.class.getName();


    private String mPictureJson;
    private Context mContext;

    private TextView mNameLabel;
    private ImageView mProfileImage;

    private RelativeLayout mContainerEmpty;
    private TextView mTextEmpty;
    private View mViewSeparator;


    public AddFriendViewHolder(View itemView,
                               ItemClickListener itemClickListener, ItemLongClickListener itemLongClickListener, boolean isEmpty,
                               Context context) {
        super(itemView, itemClickListener, itemLongClickListener);
        initView(isEmpty);
        mContext = context;
    }

    @Override
    public void bindView(PhoneWrapper phoneData) {
        if (phoneData.getData() != null) {
            mNameLabel.setText(phoneData.getData().getName());
        }

        String uid = phoneData.getData().getUid();

        Firebase pictureFirebase = RibbitPicture.getFirebasePicture().child(uid);
        pictureFirebase.addListenerForSingleValueEvent(new PictureValueListener(uid));
    }

    private void initView(boolean isEmpty) {
        mNameLabel = (TextView) itemView.findViewById(R.id.text_name);
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

    @Override
    public void onFinish(Uri uri) {
        Picasso.with(mContext).load(uri).into(mProfileImage);
    }

    private class PictureValueListener implements ValueEventListener {

        private final String mUid;

        public PictureValueListener(String uid) {
            mUid = uid;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mPictureJson = dataSnapshot.getValue(PictureData.class).getPicture();
            Log.d(TAG, mPictureJson);
            new ImageLoader(AddFriendViewHolder.this).execute(mUid, mPictureJson);
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
            Log.e(TAG, firebaseError.getMessage());
        }
    }
}
