package ja.burhanrashid52.photoeditor;

import android.content.Context;
import android.graphics.Rect;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;


public class CameraMultiTouchListener implements OnTouchListener {

    private static final int INVALID_POINTER_ID = -1;
    public static  boolean IMAGE_DELETE_OPTION = false;
    private final GestureDetector mGestureListener;
    private final boolean isRotateEnabled = true;
    private final boolean isTranslateEnabled = true;
    private final boolean isScaleEnabled = true;
    private final float minimumScale = 0.1f;
    private final float maximumScale = 10.0f;
    private int mActivePointerId = INVALID_POINTER_ID;
    private float mPrevX, mPrevY, mPrevRawX, mPrevRawY;
    private final ScaleGestureDetector mScaleGestureDetector;

    private final int[] location = new int[2];
    private final Rect outRect;
    private final View deleteView;
    private final ImageView photoEditImageView;
    private final RelativeLayout parentView;

    private OnMultiTouchListener onMultiTouchListener;
    private OnGestureControl mOnGestureControl;
    private final boolean mIsTextPinchZoomable;
    private final OnPhotoEditorListener mOnPhotoEditorListener;
    private final boolean toTop;
    private final FrameLayout deleteAreaView;
    private final ImageView deletebinView;

    boolean animateVie=false;
    private final View viewBeingDrag;
    private final Group deleteGroup;
    private final ImageView deleteImageView;
    private final TextView deleteTextView;

    public CameraMultiTouchListener(@Nullable View deleteView, RelativeLayout parentView,
                                    ImageView photoEditImageView, boolean isTextPinchZoomable,
                                    OnPhotoEditorListener onPhotoEditorListener, Boolean bringTop, FrameLayout deleteArea, View view, ImageView deleteBin, Group deletegroup, ImageView deleteImage, TextView deleteText) {
        mIsTextPinchZoomable = isTextPinchZoomable;
        mScaleGestureDetector = new ScaleGestureDetector(new ScaleGestureListener());
        mGestureListener = new GestureDetector(new GestureListener());
        this.deleteView = deleteView;
        this.parentView = parentView;
        this.photoEditImageView = photoEditImageView;
        this.mOnPhotoEditorListener = onPhotoEditorListener;
        this.toTop=bringTop;
        this.deleteAreaView=deleteArea;
        this.deletebinView=deleteBin;
        this.viewBeingDrag=view;
        this.deleteGroup=deletegroup;
        this.deleteImageView=deleteImage;
        this.deleteTextView=deleteText;
        if (deleteView != null) {
            outRect = new Rect(deleteView.getLeft(), deleteView.getTop(),
                    deleteView.getRight(), deleteView.getBottom());
        } else {
            outRect = new Rect(0, 0, 0, 0);
        }
    }

    private static float adjustAngle(float degrees) {
        if (degrees > 180.0f) {
            degrees -= 360.0f;
        } else if (degrees < -180.0f) {
            degrees += 360.0f;
        }

        return degrees;
    }

    private static void move(View view, TransformInfo info) {


        //computeRenderOffset(view, info.pivotX, info.pivotY);
       // adjustTranslation(view, info.deltaX, info.deltaY);
        Log.i("Debugmove","move:"+view.getX()+"y:"+view.getY());
        float scale = view.getScaleX() * info.deltaScale;
        scale = Math.max(info.minimumScale, Math.min(info.maximumScale, scale));
        view.setScaleX(scale);
        view.setScaleY(scale);


        float rotation = adjustAngle(view.getRotation() + info.deltaAngle);
        view.setRotation(rotation);

    }

    private static void adjustTranslation(View view, float deltaX, float deltaY) {
        float[] deltaVector = {deltaX, deltaY};
        view.getMatrix().mapVectors(deltaVector);
        view.setTranslationX(view.getTranslationX() + deltaVector[0]);
        view.setTranslationY(view.getTranslationY() + deltaVector[1]);
    }

    private static void computeRenderOffset(View view, float pivotX, float pivotY) {
        if (view.getPivotX() == pivotX && view.getPivotY() == pivotY) {
            return;
        }

        float[] prevPoint = {1.0f, 1.0f};
        view.getMatrix().mapPoints(prevPoint);

        view.setPivotX(pivotX);
        view.setPivotY(pivotY);

        float[] currPoint = {1.0f, 1.0f};
        view.getMatrix().mapPoints(currPoint);

        float offsetX = currPoint[0] - prevPoint[0];
        float offsetY = currPoint[1] - prevPoint[1];

        view.setTranslationX(view.getTranslationX() - offsetX);
        view.setTranslationY(view.getTranslationY() - offsetY);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(view, event);
        mGestureListener.onTouchEvent(event);

        if (!isTranslateEnabled) {
            return true;
        }

        int action = event.getAction();

        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        switch (action & event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                boolean temp=(viewBeingDrag.getTag()!=null && viewBeingDrag.getTag().equals("image")&&IMAGE_DELETE_OPTION);

                mActivePointerId = event.getPointerId(0);
                if (mActivePointerId!=0){
                    Log.i("Debugmove","not 0 so removing..."+mActivePointerId);

                    return false;
                }
                deleteGroup.setVisibility(temp?View.INVISIBLE:View.VISIBLE);
                Log.i("Debugmove","ACTION_DOWN mActivePointerId:"+mActivePointerId);
                DisplayMetrics displayMetrics = new DisplayMetrics();
                /*RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                viewBeingDrag.setLayoutParams(params);*/
                //viewBeingDrag.setPadding(40,40,40,40);

                mPrevX = event.getX();
                mPrevY = event.getY();
                mPrevRawX = event.getRawX();
                mPrevRawY = event.getRawY();

                if (deleteView != null) {
                    deleteView.setVisibility(temp?View.INVISIBLE:View.VISIBLE);
                }
                if (toTop){
                    view.bringToFront();
                }


                firePhotoEditorSDKListener(view, true);
                break;
            case MotionEvent.ACTION_MOVE:
                //below code will detect if moving view is overlapping with delete area view

                float centreX=view.getX() + view.getWidth()  / 2;
                float  centreY=view.getY() + view.getHeight() / 2;
                Log.i("sticker","Touch ACTION_MOVE"+view.getX()+"y:"+view.getY()+"centerX"+centreX+" centerY"+centreY);
                if (centreX >deleteAreaView.getX()-100 && centreX<deleteAreaView.getX()+200 && centreY>deleteAreaView.getY() && centreY<deleteAreaView.getY()+600){
                    float xminus=deleteAreaView.getX()-100;
                    float xmax=deleteAreaView.getX()+200;

                    float yminus=deleteAreaView.getY();
                    float ymax=deleteAreaView.getY()+600;

                    if (!animateVie){
                        if (viewBeingDrag.getTag()!=null && viewBeingDrag.getTag().equals("image")&&IMAGE_DELETE_OPTION){

                        }else {
                            Vibrator vibe = (Vibrator) deletebinView.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibe.vibrate(50);
                            deleteAreaView.setPadding(20,20,20,20);
                            deletebinView.setImageDrawable(ContextCompat.getDrawable(deleteAreaView.getContext(),R.drawable.ic_image_bin_openn));

                            animateVie=true;
                            if (viewBeingDrag.getTag()!=null && viewBeingDrag.getTag().equals("image")){
                                viewBeingDrag.setVisibility(View.INVISIBLE);
                                deleteImageView.setVisibility(View.GONE);
                                deleteTextView.setVisibility(View.GONE);
                                deleteGroup.setVisibility(View.GONE);

                                ImageView asdf= viewBeingDrag.findViewById(R.id.imgPhotoEditorImage);
                                if (asdf!=null){
                                    deleteImageView.setImageDrawable(asdf.getDrawable());
                                }else{
                                    Log.i("sticker","asdf is null");
                                }
                                Log.e("shaurabh", "onTouch:  image if part"+IMAGE_DELETE_OPTION );

                                Log.i("sticker","imageView is being move1");
                            }else if (viewBeingDrag.getTag()!=null && viewBeingDrag.getTag().equals("text")||viewBeingDrag.getTag().equals("sticker")){
                                Log.i("sticker","TextView is being move1");
                                viewBeingDrag.setVisibility(View.INVISIBLE);
                                deleteImageView.setVisibility(View.INVISIBLE);
                                deleteTextView.setVisibility(View.VISIBLE);
                                TextView asdf= viewBeingDrag.findViewById(R.id.tvPhotoEditorText);
                                if (asdf!=null){
                                    if (asdf.getText().length()>12){
                                        deleteTextView.setTextSize(8);

                                    }else{
                                        deleteTextView.setTextSize(16);
                                    }
                                    deleteTextView.setTextColor(asdf.getTextColors());
                                    deleteTextView.setText(asdf.getText());
                                    deleteTextView.setTypeface(asdf.getTypeface());
                                }else{
                                    Log.i("sticker","asdf is null");
                                }
                            }

                        }

                    }


                    Log.i("sticker","DELETE THIS VIEW."+xminus+"-"+xmax+" y:"+yminus+"-"+ymax+"\n"+view.getX()+" "+view.getY());
                }else{
                    if (animateVie){
                        deleteAreaView.setPadding(0,0,0,0);
                        deletebinView.setImageDrawable(ContextCompat.getDrawable(deleteAreaView.getContext(),R.drawable.ic_image_bin_close));
                        animateVie=false;

                    }
                    float xminus=deleteAreaView.getX()-100;
                    float xmax=deleteAreaView.getX()+200;

                    float yminus=deleteAreaView.getY();
                    float ymax=deleteAreaView.getY()+600;
                    if (viewBeingDrag.getTag()!=null && viewBeingDrag.getTag().equals("image")){
                        viewBeingDrag.setVisibility(View.VISIBLE);
                        deleteImageView.setVisibility(View.GONE);
                        deleteTextView.setVisibility(View.GONE);
                        deleteGroup.setVisibility(View.GONE);



                        Log.e("shaurabh", "onTouch:  image else part "+IMAGE_DELETE_OPTION );
                        /*ImageView asdf=(ImageView) viewBeingDrag.findViewById(R.id.imgPhotoEditorImage);
                        if (asdf!=null){
                            deleteImageView.setImageBitmap(asdf.getDrawingCache());
                        }else{
                            Log.i("sticker","asdf is null");
                        }*/

                       // Log.i("sticker","imageView is being move1");
                    }else if (viewBeingDrag.getTag()!=null && viewBeingDrag.getTag().equals("text")||viewBeingDrag.getTag().equals("sticker")){
                        Log.i("sticker","TextView is being move1");
                        viewBeingDrag.setVisibility(View.VISIBLE);
                        deleteImageView.setVisibility(View.INVISIBLE);
                        deleteTextView.setVisibility(View.INVISIBLE);
                        /*TextView asdf=(TextView) viewBeingDrag.findViewById(R.id.tvPhotoEditorText);
                        if (asdf!=null){
                            deleteTextView.setText(asdf.getText());
                        }else{
                            Log.i("sticker","asdf is null");
                        }*/
                    }

                    Log.i("sticker","not overlapping,should be between x:"+xminus+"-"+xmax+" y:"+yminus+"-"+ymax+"\n"+view.getX()+" "+view.getY());
                }
                //above code will detect if moving view is overlapping with delete area view


                int pointerIndexMove = event.findPointerIndex(mActivePointerId);

                Log.i("Debugmove","MOVE x:"+view.getX()+"y:"+view.getY());
                if (pointerIndexMove != -1) {
                    float currX = event.getX(pointerIndexMove);
                    float currY = event.getY(pointerIndexMove);
                    if (!mScaleGestureDetector.isInProgress()) {
                        adjustTranslation(view, currX - mPrevX, currY - mPrevY);
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                mActivePointerId = INVALID_POINTER_ID;
                Log.i("Debugmove","ACTION_CANCEL mActivePointerId:"+mActivePointerId);

                break;
            case MotionEvent.ACTION_UP:
                if(viewBeingDrag.getTag().equals("image")&&IMAGE_DELETE_OPTION){
                    break;
                }else {
                    mActivePointerId = INVALID_POINTER_ID;
                    deleteGroup.setVisibility(View.GONE);
                    deleteTextView.setVisibility(View.GONE);
                    deleteImageView.setVisibility(View.GONE);
                    deleteTextView.setText("");
                    deleteImageView.setImageDrawable(null);

                    float centreX2=view.getX() + view.getWidth()  / 2;
                    float  centreY2=view.getY() + view.getHeight() / 2;
                    // viewBeingDrag.setPadding(0,0,0,0);

                    if (centreX2 >deleteAreaView.getX()-100 && centreX2<deleteAreaView.getX()+100 && centreY2>deleteAreaView.getY() && centreY2<deleteAreaView.getY()+400){
                        deletebinView.setImageDrawable(ContextCompat.getDrawable(deleteAreaView.getContext(),R.drawable.ic_image_bin_close));

                        Log.i("sticker","DELETE THIS VIEW.");
                        // view.findViewById(R.id.delete_view).performClick();

                        ImageView closeButton=viewBeingDrag.findViewById(R.id.imgPhotoEditorImage);
                        ImageView closeButton2=viewBeingDrag.findViewById(R.id.imgPhotoEditorClose);

                        if (closeButton2!=null){
                            closeButton2.performClick();

                            Log.i("sticker","closeButton2 button is not null.");

                        }else if (closeButton!=null){
                            Log.i("sticker","closeButton button not null.");

                            closeButton.performClick();
                        } else{
                            Log.i("sticker","both button is null.");

                        }


                    }else{
                        Log.i("sticker","not overlapping,");
                    }
                    Log.i("Debugmove","ACTION_UP mActivePointerId:"+mActivePointerId);

                    if (deleteView != null && isViewInBounds(deleteView, x, y)) {
                        if (onMultiTouchListener != null)
                            onMultiTouchListener.onRemoveViewListener(view);
                    } /*else if (!isViewInBounds(photoEditImageView, x, y)) {
                    view.animate().translationY(0).translationY(0);
                }*/
                    if (deleteView != null) {
                        deleteView.setVisibility(View.GONE);
                    }
                    photoEditImageView.setPadding(0,0,0,0);

                    firePhotoEditorSDKListener(view, false);
                }

                break;
            case MotionEvent.ACTION_POINTER_UP:
                Log.i("Debugmove","ACTION_POINTER_UP mActivePointerId:"+mActivePointerId);

                int pointerIndexPointerUp = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                int pointerId = event.getPointerId(pointerIndexPointerUp);
                Log.i("Debugmove","ACTION_POINTER_UP after calculation mActivePointerId:"+mActivePointerId+"pointerIndexPointerUp"+pointerIndexPointerUp);

                if (pointerId == mActivePointerId) {
                    int newPointerIndex = pointerIndexPointerUp == 0 ? 1 : 0;
                    mPrevX = event.getX(newPointerIndex);
                    mPrevY = event.getY(newPointerIndex);
                    mActivePointerId = event.getPointerId(newPointerIndex);
                    Log.i("Debugmove","ACTION_POINTER_UP after calculation2 mActivePointerId:"+mActivePointerId+"newPointerIndex"+newPointerIndex);

                }
                break;
        }
        return true;
    }

    private void firePhotoEditorSDKListener(View view, boolean isStart) {
        Object viewTag = view.getTag();
        if (mOnPhotoEditorListener != null && viewTag != null && viewTag instanceof ViewType) {
            if (isStart)
                mOnPhotoEditorListener.onStartViewChangeListener(((ViewType) view.getTag()));
            else
                mOnPhotoEditorListener.onStopViewChangeListener(((ViewType) view.getTag()));
        }
    }

    private boolean isViewInBounds(View view, int x, int y) {
        view.getDrawingRect(outRect);
        view.getLocationOnScreen(location);
        outRect.offset(location[0], location[1]);
        return outRect.contains(x, y);
    }

    void setOnMultiTouchListener(OnMultiTouchListener onMultiTouchListener) {
        this.onMultiTouchListener = onMultiTouchListener;
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

     /*   private class TransformInfo {
            float deltaX;
            float deltaY;
            float deltaScale;
            float deltaAngle;
            float pivotX;
            float pivotY;
            float minimumScale;
            float maximumScale;
        }*/
       /* override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
            mScaleFactor *= scaleGestureDetector.scaleFactor
            mScaleFactor = Math.max(0.1f,
                    Math.min(mScaleFactor, 10.0f))
            mImageView?.setScaleX(mScaleFactor)
            mImageView?.setScaleY(mScaleFactor)

       *//* mScaleFactor *= scaleGestureDetector.scaleFactor
        mScaleFactor = mScaleFactor.coerceIn(0.1f, 5.0f)

        mImageView?.scaleX = mScaleFactor
        mImageView?.scaleY = mScaleFactor

        return super.onScale(scaleGestureDetector)*//*
            return true
        }*/
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

    interface OnMultiTouchListener {
        void onEditTextClickListener(String text, int colorCode);

        void onRemoveViewListener(View removedView);
    }

    interface OnGestureControl {
        void onClick();

        void onLongClick();
    }

    void setOnGestureControl(OnGestureControl onGestureControl) {
        mOnGestureControl = onGestureControl;
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (mOnGestureControl != null) {
                mOnGestureControl.onClick();
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            if (mOnGestureControl != null) {
                mOnGestureControl.onLongClick();
            }
        }
    }
}
