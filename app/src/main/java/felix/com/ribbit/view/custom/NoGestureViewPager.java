package felix.com.ribbit.view.custom;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by fsoewito on 3/5/2016.
 */
public class NoGestureViewPager extends ViewPager {

    public NoGestureViewPager(Context context) {
        super(context);
    }

    public NoGestureViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
}
