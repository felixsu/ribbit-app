package felix.com.ribbit.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import felix.com.ribbit.R;
import felix.com.ribbit.constant.Constants;
import felix.com.ribbit.exception.InputValidityException;
import felix.com.ribbit.listener.TextInputLayoutFocusListener;
import felix.com.ribbit.model.Validatable;
import felix.com.ribbit.model.firebase.UserData;
import felix.com.ribbit.model.wrapper.UserWrapper;
import felix.com.ribbit.ui.ImageCropperActivity;
import felix.com.ribbit.ui.SignUpActivity;
import felix.com.ribbit.util.MediaUtil;

public class UserDataFragment extends Fragment implements Validatable {

    private static final String TAG = UserDataFragment.class.getName();

    private static final int DIALOG_PICK_PICTURE = 0;
    private static final int DIALOG_TAKE_PICTURE = 1;

    @Bind(R.id.nameField)
    TextView mNameField;
    @Bind(R.id.image_profile_picture)
    ImageView mImageProfilePicture;
    @Bind(R.id.button_edit_profile_picture)
    ImageView mEditProfilePictureButton;
    @Bind(R.id.nameHolder)
    TextInputLayout mNameHolder;

    private UserWrapper mUserWrapper;
    private UserData mUserData;

    private SignUpActivity mActivity;

    private Uri mMediaUri;
    private DialogInterface.OnClickListener mListener = new DialogInterface.OnClickListener() {
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
                        Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private View.OnClickListener mProfilePictureClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setItems(R.array.edit_profile_pictures, mListener);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    };
    private Callback mPicassoCallback = new Callback() {
        @Override
        public void onSuccess() {
            mEditProfilePictureButton.setVisibility(View.INVISIBLE);
            mImageProfilePicture.setOnClickListener(mProfilePictureClickListener);
        }

        @Override
        public void onError() {
            Toast.makeText(mActivity, "error fill profile picture", Toast.LENGTH_SHORT).show();
        }
    };

    public UserDataFragment() {
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return initView(inflater, container);
    }

    private View initView(LayoutInflater inflater, ViewGroup container) {
        View rootView = inflater.inflate(R.layout.fragment_sign_up_3, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        mActivity = (SignUpActivity) getActivity();
        mUserWrapper = mActivity.getUserWrapper();
        mUserData = mUserWrapper.getData();
        mNameField.setOnFocusChangeListener(new TextInputLayoutFocusListener(mNameHolder));
        mEditProfilePictureButton.setOnClickListener(mProfilePictureClickListener);
    }

    public void showError(TextInputLayout v, String error) {
        v.setErrorEnabled(true);
        v.setError(error);
    }

    @Override
    public void validate() throws InputValidityException {
        if (mNameField == null || mNameHolder == null) {
            throw new IllegalStateException("field can not be null");
        }

        String name = mNameField.getText().toString();
        String errorMsg;

        if (name.isEmpty()) {
            errorMsg = getString(R.string.name_error);
            showError(mNameHolder, errorMsg);
            throw new InputValidityException(errorMsg);
        }

        mUserData.setName(name);
        mUserData.setStatus("Newbie in action");
        mActivity.setProfilePictureUri(mMediaUri);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "entering on activity result");
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == Constants.REQUEST_CROP) {
                //todo fill image view using image from prev activity
                Log.d(TAG, "entering result from crop picture");
                if (data == null) {
                    Log.i(TAG, "no data received");
                    Toast.makeText(mActivity, "no data", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "data received");
                    mMediaUri = data.getData();
                    Log.i(TAG, "Media URI : " + mMediaUri);
                }
                Picasso.with(mActivity).load(mMediaUri).into(mImageProfilePicture, mPicassoCallback);
            } else if (requestCode == MediaUtil.REQUEST_PICK_PICTURE) {

                Log.d(TAG, "entering result from pick picture");
                if (data == null) {
                    Log.i(TAG, "no data received");
                    Toast.makeText(mActivity, "no data", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(mActivity, R.string.action_cancel, Toast.LENGTH_SHORT).show();
        } else {
            Log.i(TAG, "un-catched intent");
        }
    }

    private void startCroppingActivity() {
        Intent cropIntent = new Intent(mActivity, ImageCropperActivity.class);
        cropIntent.setData(mMediaUri);
        startActivityForResult(cropIntent, Constants.REQUEST_CROP);
    }

    private void notifyMediaForNewItem() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(mMediaUri);
        mActivity.sendBroadcast(mediaScanIntent);
    }
}