package felix.com.ribbit.listener;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.File;
import java.io.IOException;

import felix.com.ribbit.model.firebase.PictureData;
import felix.com.ribbit.util.BitmapUtils;
import felix.com.ribbit.util.MediaUtil;
import felix.com.ribbit.util.Util;

public class PictureValueListener implements ValueEventListener {

    private final String mUid;
    private final PersistFileListener mPersistFileListener;

    public PictureValueListener(String uid, PersistFileListener persistFileListener) {
        mUid = uid;
        mPersistFileListener = persistFileListener;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        String pictureJson = dataSnapshot.getValue(PictureData.class).getPicture();
        try {
            Uri imageUri = MediaUtil.getProfilePictureUri(mUid);
            if (imageUri == null) {
                throw new IOException("uri not created");
            }
            byte[] profilePicture = Util.base64Decode(pictureJson);
            Bitmap bm = BitmapFactory.decodeByteArray(profilePicture, 0, profilePicture.length);
            BitmapUtils.writeBitmapToFile(bm, new File(imageUri.getPath()), 100);
            mPersistFileListener.onFinish(null, imageUri);
        } catch (IOException e) {
            mPersistFileListener.onFinish(e, null);
        }
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {
        mPersistFileListener.onFinish(firebaseError.toException(), null);
    }
}