package ja.burhanrashid52.photoeditor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class CustomFilterImageView extends androidx.appcompat.widget.AppCompatImageView {

    private int color;

    public CustomFilterImageView(Context context) {
        super(context);
    }

    public CustomFilterImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomFilterImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
