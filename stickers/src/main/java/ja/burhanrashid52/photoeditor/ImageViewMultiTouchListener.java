package ja.burhanrashid52.photoeditor;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.RequiresApi;


public class ImageViewMultiTouchListener implements View.OnTouchListener {
    public boolean isEnable = false;
    PhotoEditor photoEditor;

    float firstFingerX;
    float firstFingerY;
    float secondFingerX;
    float secondFingerY;
    private final boolean isRotateEnabled = true;
    private final boolean isScaleEnabled = true;
    private final float minimumScale = 0.5f;
    private final float maximumScale = 4.0f;
    private final boolean mIsTextPinchZoomable=true;
    private final ScaleGestureDetector mScaleGestureDetector= new ScaleGestureDetector(new ScaleGestureListener());
    private float anchorX = 0F;

    public ImageViewMultiTouchListener(PhotoEditor photoEditor) {
        this.photoEditor = photoEditor;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        try{
            mScaleGestureDetector.onTouchEvent(v, event);
            switch (event.getAction() & MotionEvent.ACTION_MASK){
                case MotionEvent.ACTION_DOWN:
                    isEnable = true;
//                    Log.d("status","ACTION_DOWN");

                    Log.e("asdfasdf","************");
                    Log.e("asdfasdf","view.getX: " + v.getX());
                    Log.e("asdfasdf","view.getY: " + v.getY());
                    Log.e("asdfasdf","event.getX: " + event.getX());
                    Log.e("asdfasdf","event.getY: " + event.getY());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        Log.e("asdfasdf", "event.getRawX: " + event.getRawX());
                        Log.e("asdfasdf", "event.getRawY: " + event.getRawY());
                    }
                    Log.e("asdfasdf","************");

                    anchorX = event.getX();

                    firstFingerX=getX_SDK(v,event,0);
                    firstFingerY= getY_SDK(v,event,0);

                    secondFingerX= getX_SDK(v,event,0);
                    secondFingerY= getY_SDK(v,event,0);

                    break;
                case MotionEvent.ACTION_UP:

//                    Log.d("status","ACTION_UP");
                    break;
                case MotionEvent.ACTION_POINTER_UP:
//                    Log.d("status","ACTION_POINTER_UP");
                    break;

                case  MotionEvent.ACTION_POINTER_DOWN:
                    isEnable = false;
                    photoEditor.resetList();
                    Log.d("status","ACTION_POINTER_DOWN");
                    secondFingerX= getX_SDK(v,event,1);
                    secondFingerY= getY_SDK(v,event,1);

                    break;
                case  MotionEvent.ACTION_MOVE:
//                    Log.d("status","ACTION_MOVE");
                    if (event.getPointerCount()==2){
                        float x1= (getX_SDK(v,event,0) - firstFingerX);
                        float x2= (getX_SDK(v,event,1) - secondFingerX);
                        if ((x1 < 0 && x2 < 0) || (x1>0 && x2 > 0)){
                            float dx = (x1 + x2) / 2;
                            v.setX(v.getX() + dx);
                        }
                        float y1= (getY_SDK(v,event,0) - firstFingerY);
                        float y2= (getY_SDK(v,event,1) - secondFingerY);
                        if ((y1 < 0 && y2 < 0) || (y1>0 && y2 > 0)){
                            float dy = (y1 + y2) / 2;
                            v.setY(v.getY() + dy);
                        }

//                        Log.d("status","firstFingerX:"+firstFingerX+",secondFingerX:"+secondFingerX+",x1:"+x1+",x2:"+x2);
                        secondFingerX= getX_SDK(v,event,1);
                        secondFingerY= getY_SDK(v,event,1);
                    }
                    firstFingerX=getX_SDK(v,event,0);
                    firstFingerY= getY_SDK(v,event,0);

                    break;
            }
        }catch (Exception e){
            Log.d("status"," e:"+e.getLocalizedMessage());
        }
        if (isEnable && photoEditor.listView!=null){
//            Log.d("status","view scale:"+v.getScaleX()+",getX:"+event.getX()+",getY:"+event.getY()+",m getScaleX:"+
//                    event.getX()*v.getScaleX()+",m getScaleY:"+event.getY()*v.getScaleY());
            event.setLocation(event.getX()*v.getScaleX(),event.getY()*v.getScaleY());
            photoEditor.listView.onCustomTouchEvent(event);
        }
        return true;
    }
    private class ScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        private float mPivotX;
        private float mPivotY;
        private final Vector2D mPrevSpanVector = new Vector2D();

        @Override
        public boolean onScaleBegin(View view, ScaleGestureDetector detector) {
            mPivotX = detector.getFocusX();
            mPivotY = detector.getFocusY();
            mPrevSpanVector.set(detector.getCurrentSpanVector());
            return mIsTextPinchZoomable;
        }

        @Override
        public boolean onScale(View view, ScaleGestureDetector detector) {
            TransformInfo info = new TransformInfo();
            info.deltaScale = isScaleEnabled ? detector.getScaleFactor() : 1.0f;
            info.deltaAngle = isRotateEnabled ? Vector2D.getAngle(mPrevSpanVector, detector.getCurrentSpanVector()) : 0.0f;
            info.deltaX = /*isTranslateEnabled ? detector.getFocusX() - mPivotX :*/ 1.0f;
            info.deltaY = /*isTranslateEnabled ? detector.getFocusY() - mPivotY :*/ 1.0f;
            info.pivotX = mPivotX;
            info.pivotY = mPivotY;
            info.minimumScale = minimumScale;
            info.maximumScale = maximumScale;
            move(view, info);
            return !mIsTextPinchZoomable;
        }

    }
    private static void move(View view, TransformInfo info) {
        Log.i("Debugmove","move:"+view.getX()+"y:"+view.getY());
        float scale = view.getScaleX() * info.deltaScale;
        scale = Math.max(info.minimumScale, Math.min(info.maximumScale, scale));
        view.setScaleX(scale);
        view.setScaleY(scale);


        float rotation = adjustAngle(view.getRotation() + info.deltaAngle);
        view.setRotation(rotation);

    }
    private static float adjustAngle(float degrees) {
        if (degrees > 180.0f) {
            degrees -= 360.0f;
        } else if (degrees < -180.0f) {
            degrees += 360.0f;
        }

        return degrees;
    }
    private class TransformInfo {
        float deltaX;
        float deltaY;
        float deltaScale;
        float deltaAngle;
        float pivotX;
        float pivotY;
        float minimumScale;
        float maximumScale;
    }
    interface SwipeCustomListener {
        void onSwipeLeft();
        void onSwipeRight();
    }


    public float getX_SDK(View v, MotionEvent event, int pointerIndex){

        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return event.getRawX(pointerIndex);
            } else {
                return event.getX() + 16;
            }
        } catch (Exception e){
            return event.getXPrecision() + 16;
        }

//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            return event.getRawX(pointerIndex);
//        } else {
//            return (event.getX() + 17);
//        }
    }

    @SuppressLint("NewApi")
    public float getY_SDK(View v, MotionEvent event, int pointerIndex){

        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return event.getRawY(pointerIndex);
            } else {
                return event.getY() + 133;
            }
        } catch (Exception e){
            return event.getY() + 133;
        }
    }
}
