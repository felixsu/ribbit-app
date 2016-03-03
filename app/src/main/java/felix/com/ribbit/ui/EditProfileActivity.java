package felix.com.ribbit.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseUser;

import butterknife.Bind;
import butterknife.ButterKnife;
import felix.com.ribbit.R;
import felix.com.ribbit.constant.ParseConstants;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = EditProfileActivity.class.getName();

    @Bind(R.id.image_profile_picture)
    protected ImageView mProfilePicture;
    @Bind(R.id.label_name)
    protected TextView mNameField;
    @Bind(R.id.label_status)
    protected EditText mStatusField;

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
        String name = (String) mCurrentUser.get(ParseConstants.KEY_NAME);
        Log.d(TAG, name);
        mNameField.setText(name);
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
