package felix.com.ribbit.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import felix.com.ribbit.R;
import felix.com.ribbit.constant.Constants;
import felix.com.ribbit.listener.RibbitResultListener;
import felix.com.ribbit.model.firebase.UserData;
import felix.com.ribbit.model.ribbit.RibbitUser;
import felix.com.ribbit.model.wrapper.PhoneWrapper;
import felix.com.ribbit.model.wrapper.PictureWrapper;
import felix.com.ribbit.model.wrapper.UserWrapper;
import felix.com.ribbit.util.MediaUtil;
import felix.com.ribbit.util.Util;

public class EditProfileActivity extends AppCompatActivity implements RibbitResultListener {

    private static final String TAG = EditProfileActivity.class.getName();
    private static final int DIALOG_PICK_PICTURE = 0;
    private static final int DIALOG_TAKE_PICTURE = 1;

    @Bind(R.id.image_profile_picture)
    protected ImageView mImageProfilePicture;
    @Bind(R.id.text_name)
    protected TextView mTextName;
    @Bind(R.id.field_status)
    protected EditText mStatusField;
    @Bind(R.id.button_edit_name)
    protected ImageView mEditNameButton;

    @Bind(R.id.button_edit_profile_picture)
    protected ImageView mEditProfilePictureButton;

    @Bind(R.id.progress_bar_global)
    protected ProgressBar mProgressBarGlobal;

    @Bind(R.id.wrapper_progress_bar_global)
    protected RelativeLayout mWrapperProgressBarGlobal;

    @Bind(R.id.progress_profile_picture)
    protected ProgressBar mProgressBarProfilePicture;

    @Bind(R.id.wrapper_progress_profile_picture)
    protected RelativeLayout mWrapperProgressBarProfilePicture;

    protected EditText mFieldDialogName;

    private UserWrapper mCurrentUser;
    private UserData mUserData;

    private Uri mMediaUri = null;
    private int mCompleteSignUpTask = 0;
    private int mSuccessSignUpTask = 0;
    private boolean mUpdateState = false;
    private DialogInterface.OnClickListener mDialogEditPictureListener = new DialogInterface.OnClickListener() {
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
    private View.OnClickListener mButtonEditProfilePictureListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
            builder.setItems(R.array.edit_profile_pictures, mDialogEditPictureListener);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    };
    private DialogInterface.OnClickListener mEditNameDialogPositiveListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            mTextName.setText(mFieldDialogName.getText().toString());
            dialog.dismiss();
        }
    };
    private DialogInterface.OnClickListener mEditNameDialogNegativeListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    };
    private View.OnClickListener mButtonEditNameListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mFieldDialogName.setText(mUserData.getName());

            AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
            builder
                    .setView(mFieldDialogName)
                    .setPositiveButton("Ok", mEditNameDialogPositiveListener)
                    .setNegativeButton("cancel", mEditNameDialogNegativeListener)
                    .show();
        }
    };
    private RibbitResultListener mUserDataResultListener = new RibbitResultListener() {
        @Override
        public void onFinish() {
            incrementCompleteTask();
        }

        @Override
        public void onSuccess() {
            incrementSuccessTask();
            checkResult();
        }

        @Override
        public void onError(Throwable e, String message) {
            checkResult();
        }
    };
    private RibbitResultListener mPhoneDataResultListener = new RibbitResultListener() {
        @Override
        public void onFinish() {
            incrementCompleteTask();
        }

        @Override
        public void onSuccess() {
            incrementSuccessTask();
            checkResult();
        }

        @Override
        public void onError(Throwable e, String message) {
            checkResult();
        }
    };
    private RibbitResultListener mPictureDataResultListener = new RibbitResultListener() {
        @Override
        public void onFinish() {
            Util.hideView(mProgressBarProfilePicture);
            Util.hideView(mWrapperProgressBarProfilePicture);
            enableButtonClick();
            deActivateUpdateState();
        }

        @Override
        public void onSuccess() {
            Log.d(TAG, "profile picture updated successfully in back end");
            Toast.makeText(EditProfileActivity.this, "Profile Picture Changed!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(Throwable e, String message) {
            Log.e(TAG, "failed to update profile picture", e);
            Toast.makeText(EditProfileActivity.this, "Failed to store profile picture change!", Toast.LENGTH_SHORT).show();
        }
    };
    private Callback mPicassoCallback = new Callback() {
        @Override
        public void onSuccess() {
            Toast.makeText(EditProfileActivity.this, "picture changed successfully", Toast.LENGTH_SHORT).show();
            storePicture();
        }

        @Override
        public void onError() {
            Toast.makeText(EditProfileActivity.this, "error fill profile picture", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
        initBasicView();
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
            if (!mUpdateState) {
                activateUpdateState();
                saveNewProfile();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mUpdateState) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "entering on activity result");
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == Constants.REQUEST_CROP) {
                //todo fill image view using image from prev activity
                Log.d(TAG, "entering result from crop picture");
                if (data == null) {
                    Log.i(TAG, "no data received");
                    Toast.makeText(this, "no data", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "data received");
                    mMediaUri = data.getData();
                    Log.i(TAG, "Media URI : " + mMediaUri);
                }
                Picasso.with(this).load(mMediaUri).into(mImageProfilePicture, mPicassoCallback);
            } else if (requestCode == MediaUtil.REQUEST_PICK_PICTURE) {

                Log.d(TAG, "entering result from pick picture");
                if (data == null) {
                    Log.i(TAG, "no data received");
                    Toast.makeText(this, "no data", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "data received");
                    mMediaUri = data.getData();
                    Log.i(TAG, "Media URI : " + mMediaUri);
                    startCroppingActivity();
                }

            } else if (requestCode == MediaUtil.REQUEST_TAKE_PICTURE) {
                Log.d(TAG, "entering result from take picture");
                notifyMediaForNewItem();
                startCroppingActivity();

            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i(TAG, "error on receiving intent result");
            Toast.makeText(this, R.string.action_cancel, Toast.LENGTH_SHORT).show();
        } else {
            Log.i(TAG, "un-caught intent");
        }
    }

    private void initData() {
        initCurrentUser();
        fillName();
        fillStatus();
        fillProfilePicture();
        coloringView();

        mEditProfilePictureButton.setOnClickListener(mButtonEditProfilePictureListener);
        mEditNameButton.setOnClickListener(mButtonEditNameListener);
    }

    private void fillProfilePicture() {
        String uid = mCurrentUser.getId();
        if (uid != null) {
            try {
                boolean isPictureAvailable = MediaUtil.isProfilePictureAvailable(uid);
                if (isPictureAvailable) {
                    Uri profilePictureUri = MediaUtil.getProfilePictureUri(uid);
                    Picasso.with(this).load(profilePictureUri).into(mImageProfilePicture);
                }
            } catch (IOException e) {
                Log.e(TAG, "failed load profile picture", e);
            }
        }
    }

    private void coloringView() {
        mEditNameButton.setImageDrawable(Util.setTint(
                getResources().getDrawable(R.drawable.ic_action_edit),
                getResources().getColor(R.color.colorAccent)));
        mEditProfilePictureButton.setImageDrawable(Util.setTint(
                getResources().getDrawable(R.drawable.ic_action_edit),
                getResources().getColor(R.color.colorAccent)));
    }

    private void initCurrentUser() {
        mCurrentUser = RibbitUser.getCurrentUser();
        mUserData = mCurrentUser.getData();
    }

    private void fillStatus() {
        String status = mUserData.getStatus();
        if (status != null) {
            mStatusField.setText(status);
        } else {
            mStatusField.setText(R.string.default_profile_status);
        }
    }

    private void fillName() {
        String name = mUserData.getName();
        if (name != null) {
            mTextName.setText(name);
        } else {
            mTextName.setText(R.string.default_profile_name);
        }
    }

    private void resetTask() {
        mCompleteSignUpTask = 0;
        mSuccessSignUpTask = 0;
    }

    private void incrementSuccessTask() {
        mSuccessSignUpTask++;
    }

    private void incrementCompleteTask() {
        mCompleteSignUpTask++;
    }

    private synchronized void checkResult() {
        if (mCompleteSignUpTask == 2) {
            hideProgressBar();
            enableButtonClick();
            deActivateUpdateState();
            if (mSuccessSignUpTask < 2) {
                resetTask();
            }
        }
        if (mSuccessSignUpTask == 2) {
            Log.d(TAG, "user " + mCurrentUser.getId() + " stored successfully");
            RibbitUser.setCurrentUser(mCurrentUser);
            finish();
        }
    }

    private void enableButtonClick() {
        Util.enableOnClickListener(mEditNameButton, mButtonEditNameListener);
        Util.enableOnClickListener(mEditProfilePictureButton, mButtonEditProfilePictureListener);
    }

    private void disableButtonClick() {
        Util.disableOnClickListener(mEditNameButton);
        Util.disableOnClickListener(mEditProfilePictureButton);
    }

    private void hideProgressBar() {
        Util.hideView(mProgressBarGlobal);
        Util.hideView(mWrapperProgressBarProfilePicture);
    }

    private void startCroppingActivity() {
        Intent cropIntent = new Intent(this, ImageCropperActivity.class);
        cropIntent.setData(mMediaUri);
        cropIntent.putExtra(Constants.KEY_INTENT_UID, mCurrentUser.getId());
        startActivityForResult(cropIntent, Constants.REQUEST_CROP);
    }

    private void notifyMediaForNewItem() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(mMediaUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void initBasicView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Util.hideView(mProgressBarGlobal);
        Util.hideView(mWrapperProgressBarGlobal);
        Util.hideView(mProgressBarProfilePicture);
        Util.hideView(mWrapperProgressBarProfilePicture);
        mFieldDialogName = new EditText(this);
    }

    private void saveNewProfile() {
        String newName = mTextName.getText().toString();
        String newStatus = mStatusField.getText().toString();

        mUserData.setName(newName);
        mUserData.setStatus(newStatus);

        Util.showView(mProgressBarGlobal);
        Util.showView(mWrapperProgressBarGlobal);
        disableButtonClick();
        mCurrentUser.store(mUserDataResultListener);

        PhoneWrapper phoneWrapper = new PhoneWrapper(mCurrentUser);
        phoneWrapper.store(mPhoneDataResultListener);
    }

    @Override
    public void onFinish() {
        Util.hideView(mProgressBarGlobal);
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

    private void storePicture() {
        if (mMediaUri != null) {
            Util.showView(mWrapperProgressBarProfilePicture);
            Util.showView(mProgressBarProfilePicture);
            disableButtonClick();
            activateUpdateState();
            PictureWrapper pictureWrapper = new PictureWrapper(mCurrentUser.getId(), mMediaUri);
            pictureWrapper.store(mPictureDataResultListener);
        }
    }

    private void activateUpdateState() {
        mUpdateState = true;
    }

    private void deActivateUpdateState() {
        mUpdateState = false;
    }


}
