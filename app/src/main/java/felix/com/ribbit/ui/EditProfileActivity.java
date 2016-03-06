package felix.com.ribbit.ui;

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
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import felix.com.ribbit.R;
import felix.com.ribbit.constant.ParseConstants;
import felix.com.ribbit.util.MediaUtil;
import felix.com.ribbit.util.Util;

public class EditProfileActivity extends AppCompatActivity {

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
    @Bind(R.id.etc_progress_bar)
    protected ProgressBar mProgressBar;

    private ParseUser mCurrentUser;
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
        mCurrentUser = ParseUser.getCurrentUser();

        mEditNameButton.setImageDrawable(Util.setTint(
                getResources().getDrawable(R.drawable.ic_action_edit),
                getResources().getColor(R.color.colorAccent)));
        mEditProfilePictureButton.setImageDrawable(Util.setTint(
                getResources().getDrawable(R.drawable.ic_action_edit),
                getResources().getColor(R.color.colorAccent)));
        String name = (String) mCurrentUser.get(ParseConstants.KEY_NAME);
        if (name != null) {
            mNameField.setText(name);
        } else {
            mNameField.setText(R.string.default_profile_name);
        }

        String status = (String) mCurrentUser.get(ParseConstants.KEY_STATUS);
        if (status != null) {
            mStatusField.setText(status);
        } else {
            mStatusField.setText(R.string.default_profile_status);
        }

        String profilePicture = (String) mCurrentUser.get(ParseConstants.KEY_PROFILE_PICTURE);
        if (profilePicture != null) {
            mProfilePicture.setImageDrawable(Util.stringToDrawable(profilePicture, getResources()));
        } else {
            mProfilePicture.setImageDrawable(getResources().getDrawable(R.drawable.ic_user_default));
        }
        mEditPictureDialogListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DIALOG_PICK_PICTURE:
                        break;
                    case DIALOG_TAKE_PICTURE:
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        mMediaUri = MediaUtil.getOutputMediaFileUri(MediaUtil.MEDIA_TYPE_IMAGE, getString(R.string.title_activity_splash_screen));
                        if (mMediaUri == null) {
                            Toast.makeText(EditProfileActivity.this, R.string.media_storage_error, Toast.LENGTH_SHORT).show();
                        }
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                        startActivityForResult(takePictureIntent, MediaUtil.REQUEST_TAKE_PICTURE);
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
                nameText.setText((CharSequence) mCurrentUser.get(ParseConstants.KEY_NAME));

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

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Util.hideView(mProgressBar);
    }

    private void saveNewProfile() {
        String newPicture = Util.imageViewToString(mProfilePicture);
        String newName = mNameField.getText().toString();
        String newStatus = mStatusField.getText().toString();

        mCurrentUser.put(ParseConstants.KEY_NAME, newName);
        mCurrentUser.put(ParseConstants.KEY_PROFILE_PICTURE, newPicture);
        mCurrentUser.put(ParseConstants.KEY_STATUS, newStatus);

        Util.showView(mProgressBar);
        mCurrentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Util.hideView(mProgressBar);
                if (e == null) {
                    Toast.makeText(EditProfileActivity.this, R.string.positive_save_profile, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.e(TAG, "profile not saved", e);
                    Toast.makeText(EditProfileActivity.this, R.string.negative_save_profile, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
