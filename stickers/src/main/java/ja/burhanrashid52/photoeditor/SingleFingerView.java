package ja.burhanrashid52.photoeditor;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class SingleFingerView extends LinearLayout {
    private final ImageView mView;
    private final ImageView mPushView;
    public ImageView mDeleteView;
    public FrameLayout textViewFrameLayout;
    public FrameLayout deleateArea;
    public ImageView deletebin;
    public TextView mTextView;

    private final float _1dp;
    private boolean mCenterInParent = true;
    private Drawable mImageDrawable, mPushImageDrawable, mDeleteImageDrawable;
    private float mImageHeight, mImageWidth, mPushImageHeight, mPushImageWidth,mDeleteImageHeight, mDeleteImageWidth;
    private String mText;
    private int mLeft = 0, mTop = 0;

    private final int mRight = 0;
    private final int mBottom = 0;



    public SingleFingerView(Context context) {

        this(context, null, 0,null);
    }

    public SingleFingerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0,null);
    }

    public SingleFingerView(Context context, AttributeSet attrs, int defStyle,Bitmap bitmap) {
        super(context, attrs, defStyle);
        this._1dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, context.getResources().getDisplayMetrics());
        this.parseAttr(context, attrs);
        View mRoot = View.inflate(context, R.layout.test_image_view, null);
        addView(mRoot, -1, -1);
        mPushView = mRoot.findViewById(R.id.push_view);
        mDeleteView= mRoot.findViewById(R.id.delete_view);
        mView = mRoot.findViewById(R.id.view);
        textViewFrameLayout = mRoot.findViewById(R.id.textview);
        mTextView= mRoot.findViewById(R.id.textview2);

        mView.setImageBitmap(bitmap);
//        mPushView.setOnTouchListener(new PushBtnTouchListener(mView));
        if (deleateArea!=null){
            Log.i("sticker","delete areaview not null");
        }else{
            Log.i("sticker","delete areaview is null");

        }
        initForSingleFingerView();
    }
    public void setmImageDrawable(Bitmap bitmap){
        mView.setImageBitmap(bitmap);
        textViewFrameLayout.setVisibility(View.GONE);
        mView.setVisibility(View.VISIBLE);
        mPushView.setOnTouchListener(new PushBtnTouchListener(mView));
    }
    public void setTextToTextView(String emojiName){
        Log.i("sticker","before setting emojiname:"+ mTextView.getText().toString());
        mText=emojiName;

        mTextView.setText(emojiName);
        Log.i("sticker","after setting emojiname:"+ mTextView.getText().toString());

    }
    public  void passDeleteArea(FrameLayout deleteview,ImageView deleteBin){
        deleateArea=deleteview;
        this.deletebin=deleteBin;
        if (deleateArea!=null){
            Log.i("sticker","delete areaview not null");
        }else{
            Log.i("sticker","delete areaview is null");

        }
        mView.setOnTouchListener(new ViewOnTouchListener(mPushView,deleateArea,deleteBin));



    }

    public  void passDeleteAreaToTextView(FrameLayout deleteview){
        deleateArea=deleteview;
        if (deleateArea!=null){
            Log.i("sticker","delete areaview not null");
        }else{
            Log.i("sticker","delete areaview is null");

        }
        textViewFrameLayout.setOnTouchListener(new ViewOnTouchListener(mPushView,deleateArea,deletebin));
        mPushView.setOnTouchListener(new PushBtnTouchListener(textViewFrameLayout));

    }
    public void hideResizebutton(){
        mPushView.setBackgroundColor(Color.TRANSPARENT);
    }




    private void parseAttr(Context context, AttributeSet attrs) {
        if (null == attrs) return;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SingleFingerView);
        if (a != null) {
            int n = a.getIndexCount();
            for (int i = 0; i < n; i++) {
                int attr = a.getIndex(i);
                if (attr == R.styleable.SingleFingerView_centerInParent) {
                    this.mCenterInParent = a.getBoolean(attr, true);
                } else if (attr == R.styleable.SingleFingerView_image) {
                    this.mImageDrawable = a.getDrawable(attr);
                } else if (attr == R.styleable.SingleFingerView_image_height) {
                    this.mImageHeight = a.getDimension(attr, 200 * _1dp);
                } else if (attr == R.styleable.SingleFingerView_image_width) {
                    this.mImageWidth = a.getDimension(attr, 200 * _1dp);
                }
                else if (attr==R.styleable.SingleFingerView_text_for_textview){
                    this.mText=a.getString(attr);
                }
                else if (attr == R.styleable.SingleFingerView_push_image) {
                    this.mPushImageDrawable = a.getDrawable(attr);
                }
                else if (attr == R.styleable.SingleFingerView_delete_image) {
                    this.mDeleteImageDrawable = a.getDrawable(attr);
                }else if (attr == R.styleable.SingleFingerView_push_image_width) {
                    this.mPushImageWidth = a.getDimension(attr, 50 * _1dp);
                } else if (attr == R.styleable.SingleFingerView_push_image_height) {
                    this.mPushImageHeight = a.getDimension(attr, 50 * _1dp);
                }

                else if (attr == R.styleable.SingleFingerView_delete_image_width) {
                    this.mDeleteImageWidth = a.getDimension(attr, 50 * _1dp);
                } else if (attr == R.styleable.SingleFingerView_delete_image_height) {
                    this.mDeleteImageHeight = a.getDimension(attr, 50 * _1dp);
                }
                else if (attr == R.styleable.SingleFingerView_left) {
                    this.mLeft = (int) a.getDimension(attr, 0 * _1dp);
                } else if (attr == R.styleable.SingleFingerView_top) {
                    this.mTop = (int) a.getDimension(attr, 0 * _1dp);
                }
            }
        }
    }

    private void initForSingleFingerView() {
       /* ViewTreeObserver vto2 = mView.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                FrameLayout.LayoutParams viewLP = (FrameLayout.LayoutParams) mView.getLayoutParams();
                FrameLayout.LayoutParams pushViewLP = (FrameLayout.LayoutParams) mPushView.getLayoutParams();
                pushViewLP.width = (int) mPushImageWidth;
                pushViewLP.height = (int) mPushImageHeight;
                pushViewLP.leftMargin = (viewLP.leftMargin + mView.getWidth()) - mPushView.getWidth() / 2;
                pushViewLP.topMargin = (viewLP.topMargin + mView.getHeight()) - mPushView.getWidth() / 2;
                mPushView.setLayoutParams(pushViewLP);
            }
        });*/
    }

    private void setViewToAttr(int pWidth, int pHeight) {
        if (null != mImageDrawable) {
            this.mView.setBackgroundDrawable(mImageDrawable);
        }
        if (null != mPushImageDrawable) {
            this.mPushView.setBackgroundDrawable(mPushImageDrawable);
        }
        if (null != mDeleteImageDrawable) {
            this.mDeleteView.setBackgroundDrawable(mDeleteImageDrawable);
        }
        if (null!=mText){
            Log.i("sticker","mText is not null "+mText);
            this.mTextView.setText(mText);
        }else{
            Log.i("sticker","mText is null");
        }
        FrameLayout.LayoutParams viewLP = (FrameLayout.LayoutParams) this.mView.getLayoutParams();
        viewLP.width = (int) mImageWidth;
        viewLP.height = (int) mImageHeight;
        int left = 0, top = 0;
        if (mCenterInParent) {
            left = pWidth / 2 - viewLP.width / 2;
            top = pHeight / 2 - viewLP.height / 2;
        } else {
            if (mLeft > 0) left = mLeft;
            if (mTop > 0) top = mTop;

        }
        viewLP.leftMargin = left;
        viewLP.topMargin = top;
        this.mView.setLayoutParams(viewLP);




        FrameLayout.LayoutParams pushViewLP = (FrameLayout.LayoutParams) mPushView.getLayoutParams();
        pushViewLP.width = (int) mPushImageWidth;
        pushViewLP.height = (int) mPushImageHeight;
        pushViewLP.leftMargin = (int) (viewLP.leftMargin + mImageWidth - mPushImageWidth / 2);
        pushViewLP.topMargin = (int) (viewLP.topMargin + mImageHeight - mPushImageHeight / 2);
        mPushView.setLayoutParams(pushViewLP);

        FrameLayout.LayoutParams deleteViewLP = (FrameLayout.LayoutParams) mDeleteView.getLayoutParams();
        deleteViewLP.width = (int) mDeleteImageWidth;
        deleteViewLP.height = (int) mDeleteImageHeight;
        deleteViewLP.leftMargin = (int) (viewLP.leftMargin + mImageWidth - mDeleteImageWidth / 2);
        deleteViewLP.topMargin = (int) (viewLP.topMargin + mImageHeight - mDeleteImageHeight / 2);
        mDeleteView.setLayoutParams(deleteViewLP);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setParamsForView(widthMeasureSpec, heightMeasureSpec);
    }

    private boolean hasSetParamsForView = false;

    private void setParamsForView(int widthMeasureSpec, int heightMeasureSpec) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (null != layoutParams && !hasSetParamsForView) {
            System.out.println("AAAAAAAAAAAAAAAAAAA setParamsForView");
            hasSetParamsForView = true;
            int width;
            if ((getLayoutParams().width == LayoutParams.MATCH_PARENT)) {
                width = MeasureSpec.getSize(widthMeasureSpec);
            } else {
                width = getLayoutParams().width;
            }
            int height;
            if ((getLayoutParams().height == LayoutParams.MATCH_PARENT)) {
                height = MeasureSpec.getSize(heightMeasureSpec);
            } else {
                height = getLayoutParams().height;
            }
            setViewToAttr(width, height);
        }
    }
}
