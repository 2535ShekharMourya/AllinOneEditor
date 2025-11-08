package ja.burhanrashid52.photoeditor.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class FilterRecyclerView extends RecyclerView {
    public FilterRecyclerView(@NonNull Context context) {
        super(context);
    }

    public FilterRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return false;
    }

    public boolean onCustomTouchEvent(MotionEvent e) {
        return super.onTouchEvent(e);
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        velocityX *= 0;
        return super.fling(0, velocityY);
    }
}
