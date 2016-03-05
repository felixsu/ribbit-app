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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;

import butterknife.Bind;
import butterknife.ButterKnife;
import felix.com.ribbit.R;
import felix.com.ribbit.constant.ParseConstants;
import felix.com.ribbit.exception.InputValidityException;
import felix.com.ribbit.listener.TextInputLayoutFocusListener;
import felix.com.ribbit.model.Validatable;
import felix.com.ribbit.ui.SignUpActivity;
import felix.com.ribbit.util.MediaUtil;
import felix.com.ribbit.util.Util;

public class UserDataFragment extends Fragment implements Validatable {
    private static final String TAG = UserDataFragment.class.getName();

    @Nullable
    @Bind(R.id.nameField)
    TextView mNameField;
    @Nullable
    @Bind(R.id.image_profile_picture)
    ImageView mProfilePicture;
    @Nullable
    @Bind(R.id.button_edit_profile_picture)
    ImageButton mEditProfilePictureButton;
    @Nullable
    @Bind(R.id.nameHolder)
    TextInputLayout mNameHolder;

    private ParseUser mCandidate;
    private SignUpActivity mActivity;
    private DialogInterface.OnClickListener mListener;
    private Uri mMediaUri;

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
        mCandidate = mActivity.getCandidate();
        if (mNameHolder != null && mNameField != null) {
            mNameField.setOnFocusChangeListener(new TextInputLayoutFocusListener(mNameHolder));
        }
        if (mProfilePicture != null && mEditProfilePictureButton != null) {
            mEditProfilePictureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setItems(R.array.edit_profile_pictures, mListener);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }
        mListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case MediaUtil.REQUEST_PICK_PICTURE:
                        //get from local
                        break;
                    case MediaUtil.REQUEST_TAKE_PICTURE:
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        mMediaUri = MediaUtil.getOutputMediaFileUri(MediaUtil.MEDIA_TYPE_IMAGE, getActivity().getString(R.string.title_activity_splash_screen));
                        if (mMediaUri == null) {
                            Toast.makeText(getActivity(), R.string.media_storage_error, Toast.LENGTH_SHORT).show();
                        }
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                        startActivityForResult(takePictureIntent, MediaUtil.REQUEST_TAKE_PICTURE);
                        break;
                    default:
                        break;
                }
            }
        };
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

        name = name.trim();

        if (name.isEmpty()) {
            errorMsg = getString(R.string.name_error);
            showError(mNameHolder, errorMsg);
            throw new InputValidityException(errorMsg);
        }

        mCandidate.put(ParseConstants.KEY_NAME, name);
        mCandidate.put(ParseConstants.KEY_PROFILE_PICTURE, Util.ImageViewToString(mProfilePicture));
        mCandidate.put(ParseConstants.KEY_STATUS, "Newbie in action");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(mMediaUri);
            mActivity.sendBroadcast(mediaScanIntent);
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i(TAG, "error on receiving intent result");
            Toast.makeText(mActivity, R.string.action_cancel, Toast.LENGTH_SHORT).show();
        } else {
            Log.i(TAG, "uncatched intent");
        }
    }
}