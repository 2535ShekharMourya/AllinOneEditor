package ja.burhanrashid52.photoeditor;


import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;


class ViewOnTouchListener implements View.OnTouchListener {
    Point pushPoint;
    int lastImgLeft;
    int lastImgTop;
    boolean animateVie=false;
    FrameLayout.LayoutParams viewLP;
    FrameLayout.LayoutParams pushBtnLP;
    int lastPushBtnLeft;
    int lastPushBtnTop;
    private final View mPushView;
    private final FrameLayout mDeleteAreaView;
    private final ImageView deleteBin;

    ViewOnTouchListener(View mPushView,FrameLayout deleteArea,ImageView deleteBin) {
        this.mPushView = mPushView;
        this.mDeleteAreaView=deleteArea;
        this.deleteBin=deleteBin;

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                Log.i("sticker","Touch Down");
                if (null == viewLP) {
                    viewLP = (FrameLayout.LayoutParams) view.getLayoutParams();
                }
                if (null == pushBtnLP) {
                    pushBtnLP = (FrameLayout.LayoutParams) mPushView.getLayoutParams();
                }

                view.bringToFront();

                pushPoint = getRawPoint(event);
                lastImgLeft = viewLP.leftMargin;
                lastImgTop = viewLP.topMargin;
                lastPushBtnLeft = pushBtnLP.leftMargin;
                lastPushBtnTop = pushBtnLP.topMargin;




                break;
            case MotionEvent.ACTION_MOVE:
                float centreX=view.getX() + view.getWidth()  / 2;
                float  centreY=view.getY() + view.getHeight() / 2;
                Log.i("sticker","Touch ACTION_MOVE"+view.getX()+"y:"+view.getY()+"centerX"+centreX+" centerY"+centreY);
                if (centreX >mDeleteAreaView.getX()-100 && centreX<mDeleteAreaView.getX()+100 && centreY>mDeleteAreaView.getY() && centreY<mDeleteAreaView.getY()+400){
                    float xminus=mDeleteAreaView.getX()-100;
                    float xmax=mDeleteAreaView.getX()+100;

                    float yminus=mDeleteAreaView.getY();
                    float ymax=mDeleteAreaView.getY()+400;

                    if (!animateVie){
                        mDeleteAreaView.setPadding(20,20,20,20);
                        deleteBin.setImageDrawable(ContextCompat.getDrawable(mDeleteAreaView.getContext(),R.drawable.ic_image_bin_openn));

                        animateVie=true;
                    }


                    Log.i("sticker","DELETE THIS VIEW."+xminus+"-"+xmax+" y:"+yminus+"-"+ymax+"\n"+view.getX()+" "+view.getY());
                }else{
                    if (animateVie){
                        mDeleteAreaView.setPadding(0,0,0,0);

                        deleteBin.setImageDrawable(ContextCompat.getDrawable(mDeleteAreaView.getContext(),R.drawable.ic_image_bin_close));
                        animateVie=false;

                    }
                    float xminus=mDeleteAreaView.getX()-100;
                    float xmax=mDeleteAreaView.getX()+100;

                    float yminus=mDeleteAreaView.getY();
                    float ymax=mDeleteAreaView.getY()+400;

                    Log.i("sticker","not overlapping,should be between x:"+xminus+"-"+xmax+" y:"+yminus+"-"+ymax+"\n"+view.getX()+" "+view.getY());
                }
                Point newPoint = getRawPoint(event);
                float moveX = newPoint.x - pushPoint.x;
                float moveY = newPoint.y - pushPoint.y;

                viewLP.leftMargin = (int) (lastImgLeft + moveX);
                viewLP.topMargin = (int) (lastImgTop + moveY);
                view.setLayoutParams(viewLP);

                pushBtnLP.leftMargin = (int) (lastPushBtnLeft + moveX);
                pushBtnLP.topMargin = (int) (lastPushBtnTop + moveY);
                mPushView.setLayoutParams(pushBtnLP);
                break;
            case MotionEvent.ACTION_UP:
                float centreX2=view.getX() + view.getWidth()  / 2;
                float  centreY2=view.getY() + view.getHeight() / 2;
                if (centreX2 >mDeleteAreaView.getX()-100 && centreX2<mDeleteAreaView.getX()+100 && centreY2>mDeleteAreaView.getY() && centreY2<mDeleteAreaView.getY()+400){
                    deleteBin.setImageDrawable(ContextCompat.getDrawable(deleteBin.getContext(),R.drawable.ic_image_bin_close));

                    Log.i("sticker","DELETE THIS VIEW.");
                   // view.findViewById(R.id.delete_view).performClick();
                    FrameLayout singleFingerView= ((FrameLayout) mPushView.getParent());
                    singleFingerView.findViewById(R.id.delete_view).performClick();

                }else{
                    Log.i("sticker","not overlapping,");
                }

                Log.i("sticker","Touch ACTION_UP"+centreX2+"y:"+centreY2);
                break;

        }
        return false;
    }


    private Point getRawPoint(MotionEvent event) {
        return new Point((int) event.getRawX(), (int) event.getRawY());
    }
}
