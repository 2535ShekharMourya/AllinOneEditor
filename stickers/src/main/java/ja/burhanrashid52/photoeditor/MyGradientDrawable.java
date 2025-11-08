package ja.burhanrashid52.photoeditor;

import android.graphics.drawable.GradientDrawable;

public class MyGradientDrawable extends GradientDrawable {

    public MyGradientDrawable(int fromColor, int toColor) {
        super(Orientation.TOP_BOTTOM, new int[]{fromColor, toColor});

        setCornerRadius(0);
        setGradientType(LINEAR_GRADIENT);
        setGradientRadius(90);
    }
}
