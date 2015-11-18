package felix.com.ribbit;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Created by fsoewito on 11/18/2015.
 */
public class RibbitApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "xblroS8M8SPINp3F9XCuQqn577f9ajERIJzuyAjw", "GMRJvzwzMOgplW4l4meKCjhRO9QykvTx43bZi8IF");

    }
}
