package felix.com.ribbit.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseUser;

import butterknife.Bind;
import butterknife.ButterKnife;
import felix.com.ribbit.R;
import felix.com.ribbit.constant.ParseConstants;
import felix.com.ribbit.util.Util;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = EditProfileActivity.class.getName();

    @Bind(R.id.image_profile_picture)
    protected ImageView mProfilePicture;
    @Bind(R.id.label_name)
    protected TextView mNameField;
    @Bind(R.id.label_status)
    protected EditText mStatusField;
    @Bind(R.id.button_edit_name)
    protected ImageButton mEditNameButton;
    @Bind(R.id.button_edit_profile_picture)
    protected ImageButton mEditProfilePictureButton;

    private ParseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
        initView();
        initData();
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
            mNameField.setText("USER");
        }

        String status = (String) mCurrentUser.get(ParseConstants.KEY_STATUS);
        if (status != null) {
            mStatusField.setText(status);
        } else {
            mStatusField.setText("i don't like share my doubt");
        }




    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
