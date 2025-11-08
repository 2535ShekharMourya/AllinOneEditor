package ja.burhanrashid52.photoeditor;

import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;

public class MultiTouchEvent extends MultiTouchListener {

    public MultiTouchEvent(@Nullable View deleteView, RelativeLayout parentView, ImageView photoEditImageView, boolean isTextPinchZoomable, OnPhotoEditorListener onPhotoEditorListener, Boolean bringTop, FrameLayout deleteArea, View view, ImageView deleteBin, Group deletegroup, ImageView deleteImage, TextView deleteText, OnDragListener onDragListener) {
        super(deleteView, parentView, photoEditImageView, isTextPinchZoomable, onPhotoEditorListener, bringTop, deleteArea, view, deleteBin, deletegroup, deleteImage, deleteText, onDragListener);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        super.onTouch(view, event);

        return true;
    }


}


