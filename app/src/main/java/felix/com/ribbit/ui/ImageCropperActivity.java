package felix.com.ribbit.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import com.fenchtose.nocropper.CropperView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import felix.com.ribbit.R;
import felix.com.ribbit.util.BitmapUtils;
import felix.com.ribbit.util.MediaUtil;

public class ImageCropperActivity extends AppCompatActivity {
    private static final String TAG = ImageCropperActivity.class.getName();

    private static final float SCALE = 1280f;
    CropperView mCropperView;
    ImageView mButtonSnap;
    ImageView mButtonRotate;

    private Uri mUri;
    private Bitmap mBitmap;
    private boolean isSnappedToCenter = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_cropper);
        initView();

    }

    @Override
    protected void onStart() {
        super.onStart();
        processIntent();
        initData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_cropper, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_done) {
            cropImage();
            callParent();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mCropperView = (CropperView) findViewById(R.id.view_crop);
        mButtonRotate = (ImageView) findViewById(R.id.rotate_button);
        mButtonSnap = (ImageView) findViewById(R.id.snap_button);
    }

    private void initData() {
        loadImage();
        mButtonSnap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snapImage();
            }
        });

        mButtonRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotateImage();
            }
        });
    }

    private void loadImage() {
        InputStream is = null;
        try {
            is = getContentResolver().openInputStream(mUri);
            int fileSize = is.available();

            Log.d(TAG, "file size " + fileSize);

            mBitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            Log.e(TAG, "error open input stream", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.d(TAG, "error occured");
                }
            }
        }

        Log.i(TAG, "bitmap: " + mBitmap.getWidth() + " " + mBitmap.getHeight());

        int maxP = Math.max(mBitmap.getWidth(), mBitmap.getHeight());
        float scale1280 = (float) maxP / SCALE;

        if (mCropperView.getWidth() != 0) {
            mCropperView.setMaxZoom(mCropperView.getWidth() * 2 / SCALE);
        } else {

            ViewTreeObserver vto = mCropperView.getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mCropperView.getViewTreeObserver().removeOnPreDrawListener(this);
                    mCropperView.setMaxZoom(mCropperView.getWidth() * 2 / SCALE);
                    return true;
                }
            });

        }

        mBitmap = Bitmap.createScaledBitmap(mBitmap, (int) (mBitmap.getWidth() / scale1280),
                (int) (mBitmap.getHeight() / scale1280), true);
        Log.i(TAG, "cropped image width : " + mBitmap.getWidth() + " height : " + mBitmap.getHeight());
        mCropperView.setImageBitmap(mBitmap);
    }

    private void callParent() {
        Intent result = new Intent();
        result.setData(mUri);
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    private void cropImage() {

        Bitmap bitmap = mCropperView.getCroppedBitmap();

        if (bitmap != null) {
            try {
                Uri uri = MediaUtil.getOutputMediaFileUri(MediaUtil.MEDIA_TYPE_IMAGE);
                BitmapUtils.writeBitmapToFile(bitmap, new File(uri.getPath()), 100);
                mUri = uri;
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    private void rotateImage() {
        if (mBitmap == null) {
            Log.e(TAG, "bitmap is not loaded yet");
            return;
        }

        mBitmap = BitmapUtils.rotateBitmap(mBitmap, 90);
        mCropperView.setImageBitmap(mBitmap);
    }

    private void snapImage() {
        if (isSnappedToCenter) {
            mCropperView.cropToCenter();
        } else {
            mCropperView.fitToCenter();
        }
        isSnappedToCenter = !isSnappedToCenter;
    }

    private void processIntent() {
        Intent receivedIntent = getIntent();
        if (receivedIntent == null) {
            Log.e(TAG, "no uri data received, back to caller");
            Toast.makeText(this, "no uri received, back to caller activity", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            mUri = receivedIntent.getData();
            Log.d(TAG, "uri received");
        }
    }

}
