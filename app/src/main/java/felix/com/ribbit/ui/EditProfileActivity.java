package felix.com.ribbit.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import butterknife.Bind;
import butterknife.ButterKnife;
import felix.com.ribbit.R;
import felix.com.ribbit.listener.RibbitResultListener;
import felix.com.ribbit.model.firebase.UserData;
import felix.com.ribbit.model.ribbit.RibbitUser;
import felix.com.ribbit.model.wrapper.UserWrapper;
import felix.com.ribbit.util.MediaUtil;
import felix.com.ribbit.util.Util;

public class EditProfileActivity extends AppCompatActivity implements RibbitResultListener {

    public static final int MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final String TAG = EditProfileActivity.class.getName();
    private static final int DIALOG_PICK_PICTURE = 0;
    private static final int DIALOG_TAKE_PICTURE = 1;
    @Bind(R.id.image_profile_picture)
    protected ImageView mProfilePicture;
    @Bind(R.id.label_name)
    protected TextView mNameField;
    @Bind(R.id.label_status)
    protected EditText mStatusField;
    @Bind(R.id.button_edit_name)
    protected ImageView mEditNameButton;
    @Bind(R.id.button_edit_profile_picture)
    protected ImageView mEditProfilePictureButton;
    @Bind(R.id.progress_bar)
    protected ProgressBar mProgressBar;

    private UserWrapper mCurrentUser;
    private UserData mUserData;

    private Uri mMediaUri;
    private DialogInterface.OnClickListener mEditPictureDialogListener;
    private DialogInterface.OnClickListener mEditNameDialogPositiveListener;
    private DialogInterface.OnClickListener mEditNameDialogNegativeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        menu.getItem(0).setIcon(Util.setTint(
                        getResources().getDrawable(R.drawable.ic_action_done),
                        getResources().getColor(R.color.colorAccent))
        );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            saveNewProfile();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initData() {
        mCurrentUser = RibbitUser.getCurrentUser();
        mUserData = mCurrentUser.getData();

        mEditNameButton.setImageDrawable(Util.setTint(
                getResources().getDrawable(R.drawable.ic_action_edit),
                getResources().getColor(R.color.colorAccent)));
        mEditProfilePictureButton.setImageDrawable(Util.setTint(
                getResources().getDrawable(R.drawable.ic_action_edit),
                getResources().getColor(R.color.colorAccent)));
        String name = mUserData.getName();
        if (name != null) {
            mNameField.setText(name);
        } else {
            mNameField.setText(R.string.default_profile_name);
        }

        String status = mUserData.getStatus();
        if (status != null) {
            mStatusField.setText(status);
        } else {
            mStatusField.setText(R.string.default_profile_status);
        }

        mEditPictureDialogListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DIALOG_PICK_PICTURE:
                        Log.d(TAG, "entering pick picture");
                        Intent pickPictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        pickPictureIntent.setType("image/*");
                        startActivityForResult(pickPictureIntent, MediaUtil.REQUEST_PICK_PICTURE);
                        break;
                    case DIALOG_TAKE_PICTURE:
                        try {
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            mMediaUri = MediaUtil.getOutputMediaFileUri(MediaUtil.MEDIA_TYPE_IMAGE);

                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                            startActivityForResult(takePictureIntent, MediaUtil.REQUEST_TAKE_PICTURE);
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage(), e);
                            Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    default:
                        break;
                }
            }
        };

        mEditProfilePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
                builder.setItems(R.array.edit_profile_pictures, mEditPictureDialogListener);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        final EditText nameText = new EditText(this);
        mEditNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameText.setText(mUserData.getName());

                AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
                builder
                        .setView(nameText)
                        .setPositiveButton("Ok", mEditNameDialogPositiveListener)
                        .setNegativeButton("cancel", mEditNameDialogNegativeListener)
                        .show();
            }
        });

        mEditNameDialogPositiveListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mNameField.setText(nameText.getText().toString());
                dialog.dismiss();
            }
        };

        mEditNameDialogNegativeListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "entering on activity result");
        if (resultCode == RESULT_OK) {
            InputStream inputStream = null;
            try {
                if (requestCode == MediaUtil.REQUEST_PICK_PICTURE) {
                    Log.d(TAG, "entering on pick picture");
                    if (data == null) {
                        Log.i(TAG, "no data received");
                    } else {
                        Log.d(TAG, "data received");
                        mMediaUri = data.getData();
                        Log.i(TAG, "Media URI : " + mMediaUri);
                    }
                } else {
                    Log.d(TAG, "entering on take picture");
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaScanIntent.setData(mMediaUri);
                    sendBroadcast(mediaScanIntent);

                }
                Log.d(TAG, "so our media URI is : " + mMediaUri);
                int fileSize = 0;
                inputStream = getContentResolver().openInputStream(mMediaUri);
                fileSize = inputStream.available();
                if (fileSize > MAX_FILE_SIZE) {
                    Toast.makeText(EditProfileActivity.this, "File too large (max 10MB)", Toast.LENGTH_SHORT).show();
                    throw new IOException("Files too large");
                }

                //prepare for compress -- thinking to move it into async task
                ByteArrayOutputStream compressedOutputStream = new ByteArrayOutputStream();
                Bitmap original = BitmapFactory.decodeStream(inputStream);
                original.compress(Bitmap.CompressFormat.JPEG, 80, compressedOutputStream);

                Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(compressedOutputStream.toByteArray()));
                Log.d(TAG, "first compressing result : " + decoded.getWidth() + " x " + decoded.getHeight() + " and " + decoded.getByteCount());

                Bitmap compressed = Bitmap.createScaledBitmap(decoded, 160, 160, false);
                Log.d(TAG, "first compressing result : " + compressed.getWidth() + " x " + compressed.getHeight() + " and " + compressed.getByteCount());

                mProfilePicture.setImageBitmap(compressed);

            } catch (FileNotFoundException e) {
                Log.i(TAG, "File not found, can not open file", e);
                Toast.makeText(EditProfileActivity.this, "File not found, can not open file", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Log.i(TAG, "Error open file", e);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        Log.e(TAG, "error occured", e);
                    }
                }
            }
        } else if (resultCode == RESULT_CANCELED) {
            Log.i(TAG, "error on receiving intent result");
            Toast.makeText(this, R.string.action_cancel, Toast.LENGTH_SHORT).show();
        } else {
            Log.i(TAG, "uncatched intent");

        }

    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Util.hideView(mProgressBar);
    }

    private void saveNewProfile() {
        String newName = mNameField.getText().toString();
        String newStatus = mStatusField.getText().toString();

        mUserData.setName(newName);
        mUserData.setStatus(newStatus);

        Util.showView(mProgressBar);
        mCurrentUser.store(this);
    }

    @Override
    public void onFinish() {
        Util.hideView(mProgressBar);
    }

    @Override
    public void onSuccess() {
        Toast.makeText(EditProfileActivity.this, R.string.positive_save_profile, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onError(Throwable e, String message) {
        Log.e(TAG, "profile not saved", e);
        Toast.makeText(EditProfileActivity.this, R.string.negative_save_profile, Toast.LENGTH_SHORT).show();
    }
}
