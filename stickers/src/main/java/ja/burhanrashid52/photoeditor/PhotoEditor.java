package ja.burhanrashid52.photoeditor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.os.Build;
import android.text.Layout;
import android.text.TextUtils;
import android.text.style.UpdateLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.util.Util;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ja.burhanrashid52.photoeditor.widget.FilterRecyclerView;


public class PhotoEditor implements BrushViewChangeListener {

    private static final String TAG = "PhotoEditor";
    private static final String TAG_STICKER = "sticker";
    private final LayoutInflater mLayoutInflater;
    private final Context context;
    private final PhotoEditorView parentView;
    private final ImageView imageView;
    private final View deleteView;
    private final BrushDrawingView brushDrawingView;
    private final List<View> addedViews;
    private final List<View> redoViews;
    private OnPhotoEditorListener mOnPhotoEditorListener;
    private final boolean isTextPinchZoomable;
    private final Typeface mDefaultTextTypeface;
    private final Typeface mDefaultEmojiTypeface;
    private final boolean toTop;

    public View getLastAddedView() {
        if (addedViews.size() > 0)
            return addedViews.get(addedViews.size() - 1);
        return null;
    }


    protected PhotoEditor(Builder builder) {
        this.context = builder.context;
        this.parentView = builder.parentView;
        this.imageView = builder.imageView;
        this.deleteView = builder.deleteView;
        this.brushDrawingView = builder.brushDrawingView;
        this.isTextPinchZoomable = builder.isTextPinchZoomable;
        this.mDefaultTextTypeface = builder.textTypeface;
        this.mDefaultEmojiTypeface = builder.emojiTypeface;
        this.toTop = builder.toTop;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        brushDrawingView.setBrushViewChangeListener(this);
        addedViews = new ArrayList<>();
        redoViews = new ArrayList<>();
    }

    /**
     * This will add image on {@link PhotoEditorView} which you drag,rotate and scale using pinch
     * if {@link PhotoEditor.Builder#setPinchTextScalable(boolean)} enabled
     *
     * @param desiredImage bitmap image you want to add
     */
    public void addImage(Bitmap desiredImage, FrameLayout deleteArea, ImageView deleteBin, Group deleteGroup, ImageView deleteImage, TextView deleteText, OnDragListener onDragListener) {
        Log.i(TAG, "addImage");
        brushDrawingView.setBrushDrawingMode(false);
        // final View imageRootView = getLayout(ViewType.IMAGE);
       /* ((SingleFingerView) imageRootView.findViewById(R.id.tiv)).setmImageDrawable(desiredImage);
        ((SingleFingerView) imageRootView.findViewById(R.id.tiv)).passDeleteArea(deleteArea,deleteBin);
        ((SingleFingerView) imageRootView.findViewById(R.id.tiv)).mDeleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("sticker","tap on delete singleview");
                viewUndo(imageRootView,ViewType.STICKER);
            }
        });*/
        final View imageRootView = getLayout(ViewType.IMAGE);
        imageRootView.setTag("image");
        final ImageView imageView = imageRootView.findViewById(R.id.imgPhotoEditorImage);
        final FrameLayout frmBorder = imageRootView.findViewById(R.id.frmBorder);
        final ImageView imgClose = imageRootView.findViewById(R.id.imgPhotoEditorClose);

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewUndo(imageRootView, ViewType.IMAGE);
            }
        });
        imageView.setImageBitmap(desiredImage);

        MultiTouchListener multiTouchListener = getMultiTouchListener(deleteArea, imageRootView, deleteBin, deleteGroup, deleteImage, deleteText, onDragListener);
        multiTouchListener.setOnGestureControl(new MultiTouchListener.OnGestureControl() {
            @Override
            public void onClick() {
              /*  boolean isBackgroundVisible = frmBorder.getTag() != null && (boolean) frmBorder.getTag();
                frmBorder.setBackgroundResource(isBackgroundVisible ? 0 : R.drawable.rounded_border_tv);
                imgClose.setVisibility(isBackgroundVisible ? View.GONE : View.VISIBLE);
                frmBorder.setTag(!isBackgroundVisible);*/
            }

            @Override
            public void onLongClick() {

            }
        });

        imageRootView.setOnTouchListener(multiTouchListener);

        addViewToParent(imageRootView, ViewType.IMAGE);

    }

    public void addCameraImage(Bitmap desiredImage, FrameLayout deleteArea, ImageView deleteBin, Group deleteGroup, ImageView deleteImage, TextView deleteText) {
        Log.i(TAG, "addImage");
        brushDrawingView.setBrushDrawingMode(false);
        // final View imageRootView = getLayout(ViewType.IMAGE);
       /* ((SingleFingerView) imageRootView.findViewById(R.id.tiv)).setmImageDrawable(desiredImage);
        ((SingleFingerView) imageRootView.findViewById(R.id.tiv)).passDeleteArea(deleteArea,deleteBin);
        ((SingleFingerView) imageRootView.findViewById(R.id.tiv)).mDeleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("sticker","tap on delete singleview");
                viewUndo(imageRootView,ViewType.STICKER);
            }
        });*/
        final View imageRootView = getLayout(ViewType.IMAGE);
        imageRootView.setTag("image");
        final ImageView imageView = imageRootView.findViewById(R.id.imgPhotoEditorImage);
        final FrameLayout frmBorder = imageRootView.findViewById(R.id.frmBorder);
        final ImageView imgClose = imageRootView.findViewById(R.id.imgPhotoEditorClose);

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewUndo(imageRootView, ViewType.IMAGE);
            }
        });
        imageView.setImageBitmap(desiredImage);

        CameraMultiTouchListener multiTouchListener = getCameraMultiTouchListener(deleteArea, imageRootView, deleteBin, deleteGroup, deleteImage, deleteText);
        multiTouchListener.setOnGestureControl(new CameraMultiTouchListener.OnGestureControl() {
            @Override
            public void onClick() {
              /*  boolean isBackgroundVisible = frmBorder.getTag() != null && (boolean) frmBorder.getTag();
                frmBorder.setBackgroundResource(isBackgroundVisible ? 0 : R.drawable.rounded_border_tv);
                imgClose.setVisibility(isBackgroundVisible ? View.GONE : View.VISIBLE);
                frmBorder.setTag(!isBackgroundVisible);*/
            }

            @Override
            public void onLongClick() {

            }
        });

        imageRootView.setOnTouchListener(multiTouchListener);

        addViewToParent(imageRootView, ViewType.IMAGE);

    }


    public void addCameraImage(String tag, Bitmap desiredImage, FrameLayout deleteArea, ImageView deleteBin, Group deleteGroup, ImageView deleteImage, TextView deleteText) {
        Log.i(TAG, "addImage");
        brushDrawingView.setBrushDrawingMode(false);
        // final View imageRootView = getLayout(ViewType.IMAGE);
       /* ((SingleFingerView) imageRootView.findViewById(R.id.tiv)).setmImageDrawable(desiredImage);
        ((SingleFingerView) imageRootView.findViewById(R.id.tiv)).passDeleteArea(deleteArea,deleteBin);
        ((SingleFingerView) imageRootView.findViewById(R.id.tiv)).mDeleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("sticker","tap on delete singleview");
                viewUndo(imageRootView,ViewType.STICKER);
            }
        });*/
        final View imageRootView = getLayout(ViewType.IMAGE);
        imageRootView.setTag(tag);
        final ImageView imageView = imageRootView.findViewById(R.id.imgPhotoEditorImage);
        final FrameLayout frmBorder = imageRootView.findViewById(R.id.frmBorder);
        final ImageView imgClose = imageRootView.findViewById(R.id.imgPhotoEditorClose);

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewUndo(imageRootView, ViewType.IMAGE);
            }
        });
        imageView.setImageBitmap(desiredImage);

        CameraMultiTouchListener multiTouchListener = getCameraMultiTouchListener(deleteArea, imageRootView, deleteBin, deleteGroup, deleteImage, deleteText);
        multiTouchListener.setOnGestureControl(new CameraMultiTouchListener.OnGestureControl() {
            @Override
            public void onClick() {
              /*  boolean isBackgroundVisible = frmBorder.getTag() != null && (boolean) frmBorder.getTag();
                frmBorder.setBackgroundResource(isBackgroundVisible ? 0 : R.drawable.rounded_border_tv);
                imgClose.setVisibility(isBackgroundVisible ? View.GONE : View.VISIBLE);
                frmBorder.setTag(!isBackgroundVisible);*/
            }

            @Override
            public void onLongClick() {

            }
        });

        imageRootView.setOnTouchListener(multiTouchListener);

        addViewToParent(imageRootView, ViewType.IMAGE);

    }

    public interface OnSwipeListener {
        void onSwipeLeft();

        void onSwipeRight();
    }

    ImageView customFilterImageView;

    public FilterRecyclerView processFilter(List<FilterNew> list) {
        final View filterView = getLayout(ViewType.FILTER_VIEW);
        addViewToParent(filterView, ViewType.FILTER_VIEW);
        list.add(0, new Gson().fromJson("{ \"name\": \"\", \"type\": \"transparent\", \"startColor\": \"#e83342\", \"midColor\": \"#e7fad4\", \"endColor\": \"#3ce833\" }", FilterNew.class));

        ArrayList colorList = new ArrayList();
        colorList.addAll(list);
        colorList.addAll(list);

        ImageFilterAdapter imageFilterAdapter = new ImageFilterAdapter(colorList);
        FilterRecyclerView recyclerView = filterView.findViewById(R.id.filter_list);
        recyclerView.setAdapter(imageFilterAdapter);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        recyclerView.scrollToPosition(colorList.size() / 2);
        return recyclerView;
    }

    public FilterRecyclerView listView;
    public TextView filterText;

    public void addCameraImageNoBorderWithGesture(Bitmap desiredImage, List<FilterNew> list, TextView filterText) {
        this.filterText = filterText;
        Log.i(TAG, "addImageNoBorderWithGesture");
//        ImageViewMultiTouchListener imageViewMultiTouchListener = new ImageViewMultiTouchListener(this);
        MultiTouchListenerNew imageViewMultiTouchListener = new MultiTouchListenerNew(this);

        final View emptyView = getLayout(ViewType.NO_BORDER_CAMERA);
        FrameLayout frameLayout = emptyView.findViewById(R.id.frmBorder);
        frameLayout.removeAllViews();
        addViewToParent(emptyView, ViewType.IMAGE);

        final View imageRootView = getLayout(ViewType.NO_BORDER_CAMERA);
        customFilterImageView = imageRootView.findViewById(R.id.imgPhotoEditorImage);
        imageRootView.setTag("image");
        customFilterImageView.setImageBitmap(desiredImage);
        imageRootView.setOnTouchListener(imageViewMultiTouchListener);

        addViewToParent(imageRootView, ViewType.IMAGE);
        listView = processFilter(list);

        customFilterImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.e("status", " add-DefaultFullImage INSIDE imageView width: " + customFilterImageView.getWidth()
                        + " height: " + customFilterImageView.getHeight());

                if (customFilterImageView.getWidth() > 0 && customFilterImageView.getHeight() > 0 &&
                        customFilterImageView.getHeight() < getScreenHeight() && customFilterImageView.getHeight() > customFilterImageView.getWidth()) {
                    customFilterImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    float scale = (float) getScreenHeight() / (float) customFilterImageView.getHeight();
                    customFilterImageView.getLayoutParams().height = getScreenHeight();
                    customFilterImageView.getLayoutParams().width = (int) (customFilterImageView.getLayoutParams().width * scale);
                    Log.e("status", " scale :" + scale + ",height:" + customFilterImageView.getLayoutParams().height +
                            ",getScreenHeight:" + getScreenHeight());
                }
            }
        });


        if (listView != null && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
            listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    switch (newState) {
                        case RecyclerView.SCROLL_STATE_IDLE:
                            updateRecycleList();
                            break;
                        case RecyclerView.SCROLL_STATE_DRAGGING:
                            Log.d("status", "Scrolling now");
                            break;
                        case RecyclerView.SCROLL_STATE_SETTLING:
                            break;
                    }
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                }
            });
        }

        frameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                listView.onCustomTouchEvent(motionEvent);
                return true;
            }
        });
    }

    public void updateRecycleList() {
        if (listView.getAdapter() instanceof ImageFilterAdapter && filterText != null) {
            ImageFilterAdapter adapter = (ImageFilterAdapter) listView.getAdapter();
            final int offset = listView.computeHorizontalScrollOffset();
            if (offset % customFilterImageView.getWidth() == 0) {
                int currentItemPos = offset / customFilterImageView.getWidth();
                if (currentItemPos == adapter.colorList.size() - 1 || currentItemPos == 0) {
                    listView.scrollToPosition(adapter.colorList.size() / 2);
                    currentItemPos = adapter.colorList.size() / 2;
                }
                if (adapter.colorList.size() > currentItemPos && currentItemPos >= 0 &&
                        adapter.currentItemPos != currentItemPos) {
                    adapter.currentItemPos = currentItemPos;
                    String text = adapter.colorList.get(currentItemPos).getName();
                    filterText.setText(text + "");
                    filterText.setVisibility(View.VISIBLE);
                    filterText.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            filterText.setVisibility(View.GONE);
                        }
                    }, 2000);
                }
            }

            Log.d("status", "The RecyclerView is not scrolling currentItemPos:" + (offset % customFilterImageView.getWidth()));
        }

    }

    public void resetList() {
        if (listView.getAdapter() instanceof ImageFilterAdapter && filterText != null) {
            ImageFilterAdapter adapter = (ImageFilterAdapter) listView.getAdapter();

            LinearLayoutManager layoutManager = (LinearLayoutManager) listView.getLayoutManager();
            int currentItemPos = (layoutManager.findFirstVisibleItemPosition() + layoutManager.findLastVisibleItemPosition()) / 2;

            if (currentItemPos == adapter.colorList.size() - 1 || currentItemPos == 0) {
                listView.scrollToPosition(adapter.colorList.size() / 2);
                currentItemPos = adapter.colorList.size() / 2;
            }
//            if (adapter.colorList.size()>currentItemPos && currentItemPos >= 0 &&
//                    adapter.currentItemPos != currentItemPos) {
            listView.smoothScrollToPosition(currentItemPos);
            adapter.currentItemPos = currentItemPos;
            String text = adapter.colorList.get(currentItemPos).getName();
            filterText.setText(text + "");
            filterText.setVisibility(View.VISIBLE);
            filterText.postDelayed(new Runnable() {
                @Override
                public void run() {
                    filterText.setVisibility(View.GONE);
                }
            }, 2000);
//            }
            Log.d("status", " first:" + layoutManager.findFirstVisibleItemPosition() + ",second:" +
                    layoutManager.findLastVisibleItemPosition());
        }

    }


    private void moveImageToCoordinate(FrameLayout frameLayout, ImageView imageView, int targetX, int targetY) {
        int frameLayoutWidth = frameLayout.getWidth();
        int frameLayoutHeight = frameLayout.getHeight();

        int imageWidth = imageView.getWidth();
        int imageHeight = imageView.getHeight();

        // Calculate the translation values to move the image
        int translationX = targetX - (frameLayoutWidth / 2) + (imageWidth / 2);
        int translationY = targetY - (frameLayoutHeight / 2) + (imageHeight / 2);

        // Apply the translation to the image view
        imageView.setX(translationX);
        imageView.setY(translationY);
    }


    public void resizeAndAdjustPosition(View imageRootView, Rect rect, ImagesDetails imagesDetails, AlertDialog progressDialog, Integer frame_layout_height, Integer frame_layout_width) {
        final ImageView imageView = imageRootView.findViewById(R.id.imgPhotoEditorImage);
        float biasHeightPercent = 0.5f;
        float biasWidthPercent = 0.5f;
        float width = 0.5f;
        float height = 0.5f;
        final ConstraintLayout childConstraint = imageRootView.findViewById(R.id.constraint);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) childConstraint.getLayoutParams();
        params.dimensionRatio = imagesDetails.getImg_width()+":" +imagesDetails.getImg_height();
        childConstraint.setLayoutParams(params);
        final ConstraintLayout constraintLayout = childConstraint;
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        if (imagesDetails.getWidth()!=null) {
             float biasHeight = (float) (frame_layout_height - imagesDetails.getHeight_in_px());
             float biasWidth = (float) (frame_layout_width- imagesDetails.getWidth_in_px());
             biasHeightPercent = (float) (imagesDetails.getTop_in_px() / biasHeight);
             biasWidthPercent = (float) (imagesDetails.getLeft_in_px() / biasWidth);
             width = (float) (imagesDetails.getWidth()/100);
             height = (float) (imagesDetails.getHeight()/100);
        }



        constraintSet.constrainPercentHeight(imageView.getId(), height);
        constraintSet.constrainPercentWidth(imageView.getId(), width);
        constraintSet.setVerticalBias(imageView.getId(), biasHeightPercent);
        constraintSet.setHorizontalBias(imageView.getId(), biasWidthPercent);
        constraintSet.applyTo(constraintLayout);

        Bitmap desired = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        BitMapWorker bitMapWorker = new BitMapWorker(context);
        if (imagesDetails.getType().equals("circle")) {
            desired = bitMapWorker.makeCircularBitmap(desired);
        }
        imageView.setImageBitmap(desired);
        imageView.requestLayout();



    }

    public void addImageNoBorderWithGesture(Bitmap desiredImage, FrameLayout deleteArea, ImageView deleteBin,
                                            Group deleteGroup, ImageView deleteImage, TextView deleteText,

                                            OnDragListener onDragListener) {
        Log.i(TAG, "addImageNoBorderWithGesture");


        final View imageRootView = getLayout(ViewType.NO_BORDER);
        final ImageView imageView = imageRootView.findViewById(R.id.imgPhotoEditorImage);
        imageRootView.setTag("image");
        final RelativeLayout frmBorder = imageRootView.findViewById(R.id.frmBorder);

        MultiTouchEvent multiTouchListener = getMultiTouchEvent(deleteArea, imageRootView, deleteBin, deleteGroup, deleteImage, deleteText, onDragListener);
        multiTouchListener.setOnGestureControl(new MultiTouchEvent.OnGestureControl() {
            @Override
            public void onClick() {

            }

            @Override
            public void onLongClick() {

            }
        });

        imageRootView.setOnTouchListener(multiTouchListener);

        FrameLayout parentFrm = imageRootView.findViewById(R.id.parentFrm);
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.e("SELECTED IMAGE", "selected image layout");

                return false;
            }
        });
        parentFrm.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.e("SELECTED IMAGE", "selected frame layout");
                return false;
            }
        });

        frmBorder.post(new Runnable() {
            @Override
            public void run() {
                //TODO: Resize Bitmap
                Bitmap desired = desiredImage;

                imageView.setImageBitmap(desired);
            }
        });

        addViewToParent(imageRootView, ViewType.IMAGE);

    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public void destroyObserver(boolean isAi) {
        final View imageRootView = getLayout(!isAi ? ViewType.FILLPARENT : ViewType.FILLAIPARENT);
        imageRootView.setTag("image");
        final ImageView imageView = imageRootView.findViewById(R.id.imgPhotoEditorImage);
        imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.e("asdfasdf", "destroyObserver removing listener ");
                imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    public interface ImageListener {
        void imageCreated(ImageView imageView);
    }

    /**
     * This will add image on {@link PhotoEditorView} which you drag,rotate and scale using pinch
     * if {@link PhotoEditor.Builder#setPinchTextScalable(boolean)} enabled
     *
     * @param desiredImage bitmap image you want to add
     */
    public void addFullImage(String desiredImage, boolean isAi, ImageListener imageListener, View view) {

        final View imageRootView = getLayout(!isAi ? ViewType.FILLPARENT : ViewType.FILLAIPARENT);
        imageRootView.setTag("image");
        addViewToFullParent(imageRootView, ViewType.IMAGE, imageListener, desiredImage, view);
    }

    public void addFullImageQuotes(String desiredImage, boolean isAi,int position) {

        Log.i(TAG,"addFullImage");
        Log.e("asdfasdf", "addFullImage isAI: " + !isAi);
        final View imageRootView = getLayout(!isAi?ViewType.FILLPARENT:ViewType.FILLAIPARENT);
        imageRootView.setTag("image");
        final ImageView imageView = imageRootView.findViewById(R.id.imgPhotoEditorImage);
        Log.e("asdfasdf"," addFullImage imageView width: " + imageView.getWidth() + " height: " + imageView.getHeight());
        imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                if(imageView.getWidth()>0){
                    mOnPhotoEditorListener.update(imageView.getWidth(),imageView.getHeight(),"success",position);
                }
                Log.e("asdfasdf"," addFullImage INSIDE imageView width: " + imageView.getWidth() + " height: " + imageView.getHeight());
                Glide.with(imageView).
                        load(desiredImage)
                        .apply(new RequestOptions().override(imageView.getWidth(), imageView.getHeight())).
                        listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                //  res.dismiss();
                                return false;
                            }
                        }).into(imageView);

                if (imageView.getWidth()>0 && imageView.getHeight()>0){
                    Log.e("asdfasdf"," addFullImage REMOVING LISTENER");
                    imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });


        addViewToFullParent(imageRootView, ViewType.IMAGE);

    }


    public void addDefaultFullImage(int desiredImage, boolean isAi, ImageListener imageListener, AlertDialog res) {

        Log.i(TAG, "addFullImage");
        Log.e("asdfasdf", "add-DefaultFullImage isAI: " + !isAi);
        final View imageRootView = getLayout(!isAi ? ViewType.FILLPARENT : ViewType.FILLAIPARENT);
        imageRootView.setTag("image");
        final ImageView imageView = imageRootView.findViewById(R.id.imgPhotoEditorImage);

        Log.e("asdfasdf", " add-DefaultFullImage imageView width: " + imageView.getWidth() + " height: " + imageView.getHeight());

        imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.e("asdfasdf", " add-DefaultFullImage INSIDE imageView width: " + imageView.getWidth() + " height: " + imageView.getHeight());

                imageListener.imageCreated(imageView);
                Glide.with(imageView).
                        load(desiredImage)
                        .apply(new RequestOptions().override(imageView.getWidth(), imageView.getHeight())).
                        listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                res.dismiss();
                                return false;
                            }
                        }).
                        into(imageView);
                if (imageView.getWidth() > 0 && imageView.getHeight() > 0) {
                    Log.e("asdfasdf", " add-DefaultFullImage REMOVING LISTENER");
                    imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });

        addViewToFullParent(imageRootView, ViewType.IMAGE);

    }

    public void addLoading(int desiredImage, boolean isAi, ImageListener imageListener) {

        Log.i(TAG, "addFullImage");
        final View imageRootView = getLayout(!isAi ? ViewType.FILLPARENT : ViewType.FILLAIPARENT);
        imageRootView.setTag("image");
        final ImageView imageView = imageRootView.findViewById(R.id.imgPhotoEditorImage);
        Glide.with(imageView).
                applyDefaultRequestOptions(new RequestOptions().override(200, 200)).
                load(desiredImage).
                into(imageView);

        addViewToFullParent(imageRootView, ViewType.IMAGE);

    }

    /**
     * This add the text on the {@link PhotoEditorView} with provided parameters
     * by default {@link TextView#setText(int)} will be 18sp
     *
     * @param text              text to display
     * @param colorCodeTextView text color to be displayed
     */
    @SuppressLint("ClickableViewAccessibility")
    public void addText(String text, final int colorCodeTextView, FrameLayout deleteArea, ImageView deletebin, Group deleteGroup, ImageView deleteImage, TextView deleteText, OnDragListener onDragListener) {
        addText(null, text, colorCodeTextView, deleteArea, deletebin, deleteGroup, deleteImage, deleteText, onDragListener);
    }

    /**
     * This add the text on the {@link PhotoEditorView} with provided parameters
     * by default {@link TextView#setText(int)} will be 18sp
     *
     * @param textTypeface      typeface for custom font in the text
     * @param text              text to display
     * @param colorCodeTextView text color to be displayed
     */
    @SuppressLint("ClickableViewAccessibility")
    public void addText(@Nullable Typeface textTypeface, String text, final int colorCodeTextView, FrameLayout deleteArea, ImageView deleteBin, Group deleteGroup, ImageView deleteImage, TextView deleteText, OnDragListener onDragListener) {
        final TextStyleBuilder styleBuilder = new TextStyleBuilder();

        styleBuilder.withTextColor(colorCodeTextView);
        if (textTypeface != null) {
            styleBuilder.withTextFont(textTypeface);
        }

        addText(text, styleBuilder, deleteArea, deleteBin, deleteGroup, deleteImage, deleteText, onDragListener);
    }

    /**
     * This add the text on the {@link PhotoEditorView} with provided parameters
     * by default {@link TextView#setText(int)} will be 18sp
     *
     * @param text         text to display
     * @param styleBuilder text style builder with your style
     */
    @SuppressLint("ClickableViewAccessibility")
    public void addText(String text, @Nullable TextStyleBuilder styleBuilder, FrameLayout deleteArea, ImageView deletebin, Group deleteGroup, ImageView deleteImage, TextView deleteText, OnDragListener onDragListener) {
        brushDrawingView.setBrushDrawingMode(false);
        final View textRootView = getLayout(ViewType.TEXT);
        //SingleFingerView singleFingerView=(SingleFingerView) textRootView.findViewById(R.id.tiv);
        final TextView textInputTv = textRootView.findViewById(R.id.tvPhotoEditorText);
        final ImageView imgClose = textRootView.findViewById(R.id.imgPhotoEditorClose);
        final FrameLayout frmBorder = textRootView.findViewById(R.id.frmBorder);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewUndo(textRootView, ViewType.TEXT);
            }
        });
        textRootView.setTag("text");
        //singleFingerView.mTextView.setText(text);
        //singleFingerView.setTextToTextView(text);
        textInputTv.setText(text);
        Log.i("sticker", "after setting text" + textInputTv.getText().toString());
        if (styleBuilder != null)
            styleBuilder.applyStyle(textInputTv);

        MultiTouchListener multiTouchListener = getMultiTouchListener(deleteArea, textRootView, deletebin, deleteGroup, deleteImage, deleteText, onDragListener);
        multiTouchListener.setOnGestureControl(new MultiTouchListener.OnGestureControl() {
            @Override
            public void onClick() {
                String textInput = textInputTv.getText().toString();
                int currentTextColor = textInputTv.getCurrentTextColor();

                Typeface typeface = textInputTv.getTypeface();
                TextData textData = new TextData();
                textData.setTypeface(typeface);


                if (mOnPhotoEditorListener != null) {
                    Log.i(TAG, "onLongClick mOnPhotoEditorListener != null");

                    mOnPhotoEditorListener.onEditTextChangeListener(textRootView, textInput, currentTextColor, textInputTv.getGravity(), textData);
                } else {
                    Log.i(TAG, "onLongClick mOnPhotoEditorListener null");

                }
            }

            @Override
            public void onLongClick() {
                Log.i(TAG, "onLongClick");
               /* String textInput = textInputTv.getText().toString();
                int currentTextColor = textInputTv.getCurrentTextColor();

                Typeface typeface=textInputTv.getTypeface();
                TextData textData=new TextData();
                textData.setTypeface(typeface);



                if (mOnPhotoEditorListener != null) {
                    Log.i(TAG,"onLongClick mOnPhotoEditorListener != null");

                    mOnPhotoEditorListener.onEditTextChangeListener(textRootView, textInput, currentTextColor,textInputTv.getGravity(),textData);
                }else{
                    Log.i(TAG,"onLongClick mOnPhotoEditorListener null");

                }*/
            }
        });

        textRootView.setOnTouchListener(multiTouchListener);
        addViewToParent(textRootView, ViewType.TEXT);
    }

    public void addCameraText(String text, @Nullable TextStyleBuilder styleBuilder, FrameLayout deleteArea, ImageView deletebin, Group deleteGroup, ImageView deleteImage, TextView deleteText) {
        brushDrawingView.setBrushDrawingMode(false);
        final View textRootView = getLayout(ViewType.TEXT);
        //SingleFingerView singleFingerView=(SingleFingerView) textRootView.findViewById(R.id.tiv);
        final TextView textInputTv = textRootView.findViewById(R.id.tvPhotoEditorText);
        final ImageView imgClose = textRootView.findViewById(R.id.imgPhotoEditorClose);
        final FrameLayout frmBorder = textRootView.findViewById(R.id.frmBorder);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewUndo(textRootView, ViewType.TEXT);
            }
        });
        textRootView.setTag("text");
        //singleFingerView.mTextView.setText(text);
        //singleFingerView.setTextToTextView(text);
        textInputTv.setText(text);
        Log.i("sticker", "after setting text" + textInputTv.getText().toString());
        if (styleBuilder != null)
            styleBuilder.applyStyle(textInputTv);

        CameraMultiTouchListener multiTouchListener = getCameraMultiTouchListener(deleteArea, textRootView, deletebin, deleteGroup, deleteImage, deleteText);
        multiTouchListener.setOnGestureControl(new CameraMultiTouchListener.OnGestureControl() {
            @Override
            public void onClick() {
                String textInput = textInputTv.getText().toString();
                int currentTextColor = textInputTv.getCurrentTextColor();

                Typeface typeface = textInputTv.getTypeface();
                TextData textData = new TextData();
                textData.setTypeface(typeface);


                if (mOnPhotoEditorListener != null) {
                    Log.i(TAG, "onLongClick mOnPhotoEditorListener != null");

                    mOnPhotoEditorListener.onEditTextChangeListener(textRootView, textInput, currentTextColor, textInputTv.getGravity(), textData);
                } else {
                    Log.i(TAG, "onLongClick mOnPhotoEditorListener null");

                }
            }

            @Override
            public void onLongClick() {
                Log.i(TAG, "onLongClick");
               /* String textInput = textInputTv.getText().toString();
                int currentTextColor = textInputTv.getCurrentTextColor();

                Typeface typeface=textInputTv.getTypeface();
                TextData textData=new TextData();
                textData.setTypeface(typeface);



                if (mOnPhotoEditorListener != null) {
                    Log.i(TAG,"onLongClick mOnPhotoEditorListener != null");

                    mOnPhotoEditorListener.onEditTextChangeListener(textRootView, textInput, currentTextColor,textInputTv.getGravity(),textData);
                }else{
                    Log.i(TAG,"onLongClick mOnPhotoEditorListener null");

                }*/
            }
        });

        textRootView.setOnTouchListener(multiTouchListener);
        addViewToParent(textRootView, ViewType.TEXT);
    }

    /**
     * This will update text and color on provided view
     *
     * @param view      view on which you want update
     * @param inputText text to update {@link TextView}
     * @param colorCode color to update on {@link TextView}
     */
    public void editText(@NonNull View view, String inputText, @NonNull int colorCode, Typeface typeface) {
        editText(view, null, inputText, colorCode, typeface);
    }

    /**
     * This will update the text and color on provided view
     *
     * @param view         root view where text view is a child
     * @param textTypeface update typeface for custom font in the text
     * @param inputText    text to update {@link TextView}
     * @param colorCode    color to update on {@link TextView}
     */
    public void editText(@NonNull View view, @Nullable Typeface textTypeface, String inputText, @NonNull int colorCode, Typeface typeface) {
        final TextStyleBuilder styleBuilder = new TextStyleBuilder();
        styleBuilder.withTextColor(colorCode);
        if (textTypeface != null) {
            styleBuilder.withTextFont(textTypeface);
        }

        editText(view, inputText, styleBuilder, textTypeface);
    }

    /**
     * This will update the text and color on provided view
     *
     * @param view         root view where text view is a child
     * @param inputText    text to update {@link TextView}
     * @param styleBuilder style to apply on {@link TextView}
     */
    public void editText(@NonNull View view, String inputText, @Nullable TextStyleBuilder styleBuilder, Typeface typeface) {
        TextView inputTextView = view.findViewById(R.id.tvPhotoEditorText);
        if (inputTextView != null && addedViews.contains(view) && !TextUtils.isEmpty(inputText)) {
            inputTextView.setText(inputText);
            if (styleBuilder != null)
                styleBuilder.applyStyle(inputTextView);
            //inputTextView.setTypeface(typeface);
            parentView.updateViewLayout(view, view.getLayoutParams());
            int i = addedViews.indexOf(view);
            if (i > -1) addedViews.set(i, view);
        }
    }

    /**
     * Adds emoji to the {@link PhotoEditorView} which you drag,rotate and scale using pinch
     * if {@link PhotoEditor.Builder#setPinchTextScalable(boolean)} enabled
     *
     * @param emojiName unicode in form of string to display emoji
     */
    public void addEmoji(String emojiName, FrameLayout deleteArea, ImageView deletebin, Group deleteGroup, ImageView deleteImage, TextView deleteText, OnDragListener onDragListener) {
        addEmoji(null, emojiName, deleteArea, deletebin, deleteGroup, deleteImage, deleteText, onDragListener);
    }

    public void addCameraEmoji(String emojiName, FrameLayout deleteArea, ImageView deletebin, Group deleteGroup, ImageView deleteImage, TextView deleteText) {
        addCameraEmoji(null, emojiName, deleteArea, deletebin, deleteGroup, deleteImage, deleteText);
    }

    /**
     * Adds emoji to the {@link PhotoEditorView} which you drag,rotate and scale using pinch
     * if {@link PhotoEditor.Builder#setPinchTextScalable(boolean)} enabled
     *
     * @param emojiTypeface typeface for custom font to show emoji unicode in specific font
     * @param emojiName     unicode in form of string to display emoji
     */
    public void addEmoji(Typeface emojiTypeface, String emojiName, FrameLayout deleteArea, ImageView deletebin, Group deleteGroup, ImageView deleteImage, TextView deleteText, OnDragListener onDragListener) {
        brushDrawingView.setBrushDrawingMode(false);
        final View emojiRootView = getLayout(ViewType.EMOJI);
        /*SingleFingerView singleFingerView=((SingleFingerView) emojiRootView.findViewById(R.id.tiv));
        singleFingerView.mDeleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("sticker","tap on delete singleview");
                viewUndo(emojiRootView,ViewType.EMOJI);
            }
        });*/

        /*if (singleFingerView.mTextView != null) {
            Log.i(TAG_STICKER,"setting layerType 1");
            singleFingerView.mTextView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }*/
        emojiRootView.setTag("text");


        final TextView emojiTextView = emojiRootView.findViewById(R.id.tvPhotoEditorText);
        final FrameLayout frmBorder = emojiRootView.findViewById(R.id.frmBorder);
        final ImageView imgClose = emojiRootView.findViewById(R.id.imgPhotoEditorClose);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewUndo(emojiRootView, ViewType.EMOJI);
            }
        });
        if (emojiTypeface != null) {
            emojiTextView.setTypeface(emojiTypeface);
            Log.i(TAG_STICKER, "setting emojiTypeface" + emojiTypeface + "emojiname:" + emojiName);
        }
        Log.i(TAG_STICKER, "setting emojiTypeface" + emojiTypeface + "emojiname:" + emojiName);
        emojiTextView.setTextSize(56);
        emojiTextView.setText(emojiName);


    /*    if (emojiTypeface != null) {
            singleFingerView.mTextView.setTypeface(emojiTypeface);
            Log.i(TAG_STICKER,"setting emojiTypeface"+emojiTypeface+"emojiname:"+emojiName);
        }
        Log.i(TAG_STICKER,"setting emojiTypeface"+emojiTypeface+"emojiname:"+emojiName);
        singleFingerView.mTextView.setTextSize(56);
        singleFingerView.setTextToTextView(emojiName);
        singleFingerView.passDeleteAreaToTextView(deleteArea);*/

        MultiTouchListener multiTouchListener = getMultiTouchListener(deleteArea, emojiRootView, deletebin, deleteGroup, deleteImage, deleteText, onDragListener);
        multiTouchListener.setOnGestureControl(new MultiTouchListener.OnGestureControl() {
            @Override
            public void onClick() {
               /* boolean isBackgroundVisible = frmBorder.getTag() != null && (boolean) frmBorder.getTag();
                frmBorder.setBackgroundResource(isBackgroundVisible ? 0 : R.drawable.rounded_border_tv);
                imgClose.setVisibility(isBackgroundVisible ? View.GONE : View.VISIBLE);
                frmBorder.setTag(!isBackgroundVisible);*/
            }

            @Override
            public void onLongClick() {
            }
        });

        emojiRootView.setOnTouchListener(multiTouchListener);
        addViewToParent(emojiRootView, ViewType.EMOJI);
        //addViewToBottomParent(emojiRootView, ViewType.EMOJI);
    }

    public void addCameraEmoji(Typeface emojiTypeface, String emojiName, FrameLayout deleteArea, ImageView deletebin, Group deleteGroup, ImageView deleteImage, TextView deleteText) {
        brushDrawingView.setBrushDrawingMode(false);
        final View emojiRootView = getLayout(ViewType.EMOJI);
        /*SingleFingerView singleFingerView=((SingleFingerView) emojiRootView.findViewById(R.id.tiv));
        singleFingerView.mDeleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("sticker","tap on delete singleview");
                viewUndo(emojiRootView,ViewType.EMOJI);
            }
        });*/

        /*if (singleFingerView.mTextView != null) {
            Log.i(TAG_STICKER,"setting layerType 1");
            singleFingerView.mTextView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }*/
        emojiRootView.setTag("text");


        final TextView emojiTextView = emojiRootView.findViewById(R.id.tvPhotoEditorText);
        final FrameLayout frmBorder = emojiRootView.findViewById(R.id.frmBorder);
        final ImageView imgClose = emojiRootView.findViewById(R.id.imgPhotoEditorClose);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewUndo(emojiRootView, ViewType.EMOJI);
            }
        });
        if (emojiTypeface != null) {
            emojiTextView.setTypeface(emojiTypeface);
            Log.i(TAG_STICKER, "setting emojiTypeface" + emojiTypeface + "emojiname:" + emojiName);
        }
        Log.i(TAG_STICKER, "setting emojiTypeface" + emojiTypeface + "emojiname:" + emojiName);
        emojiTextView.setTextSize(56);
        emojiTextView.setText(emojiName);


    /*    if (emojiTypeface != null) {
            singleFingerView.mTextView.setTypeface(emojiTypeface);
            Log.i(TAG_STICKER,"setting emojiTypeface"+emojiTypeface+"emojiname:"+emojiName);
        }
        Log.i(TAG_STICKER,"setting emojiTypeface"+emojiTypeface+"emojiname:"+emojiName);
        singleFingerView.mTextView.setTextSize(56);
        singleFingerView.setTextToTextView(emojiName);
        singleFingerView.passDeleteAreaToTextView(deleteArea);*/

        CameraMultiTouchListener multiTouchListener = getCameraMultiTouchListener(deleteArea, emojiRootView, deletebin, deleteGroup, deleteImage, deleteText);
        multiTouchListener.setOnGestureControl(new CameraMultiTouchListener.OnGestureControl() {
            @Override
            public void onClick() {
               /* boolean isBackgroundVisible = frmBorder.getTag() != null && (boolean) frmBorder.getTag();
                frmBorder.setBackgroundResource(isBackgroundVisible ? 0 : R.drawable.rounded_border_tv);
                imgClose.setVisibility(isBackgroundVisible ? View.GONE : View.VISIBLE);
                frmBorder.setTag(!isBackgroundVisible);*/
            }

            @Override
            public void onLongClick() {
            }
        });

        emojiRootView.setOnTouchListener(multiTouchListener);
        addViewToParent(emojiRootView, ViewType.EMOJI);
        //addViewToBottomParent(emojiRootView, ViewType.EMOJI);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void addWatermark() {
        brushDrawingView.setBrushDrawingMode(false);
        final View emojiRootView = getLayout(ViewType.WATERMARK);
        addViewToBottomParent(emojiRootView, ViewType.EMOJI);

    }


    /**
     * Add to root view from image,emoji and text to our parent view
     *
     * @param rootView rootview of image,text and emoji
     */
    private void addViewToParent(View rootView, ViewType viewType) {
        Log.i("Movedebug", "addviewtoParent");
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        parentView.addView(rootView, params);
        addedViews.add(rootView);
        if (mOnPhotoEditorListener != null)
            mOnPhotoEditorListener.onAddViewListener(viewType, addedViews.size());
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void addViewToBottomParent(View rootView, ViewType viewType) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);

        parentView.addView(rootView, params);
        addedViews.add(rootView);
        if (mOnPhotoEditorListener != null)
            mOnPhotoEditorListener.onAddViewListener(viewType, addedViews.size());
    }

    private void addViewToFullParent(View rootView, ViewType viewType) {
        Log.i(TAG, "addFullImage -- > addViewToFullParent");
        // parentView.getSource().setAdjustViewBounds(true);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        parentView.addView(rootView, params);

        addedViews.add(rootView);
        if (mOnPhotoEditorListener != null)
            mOnPhotoEditorListener.onAddViewListener(viewType, addedViews.size());

    }

    int i = 0;

    private void addViewToFullParent(View rootView, ViewType viewType, ImageListener imageListener, String desiredImage, View view) {
        Log.i(TAG, "addFullImage -- > addViewToFullParent");

        // parentView.getSource().setAdjustViewBounds(true);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        parentView.addView(rootView, params);

        addedViews.add(rootView);

        parentView.post(new Runnable() {
            @Override
            public void run() {
                final ImageView imageView = rootView.findViewById(R.id.imgPhotoEditorImage);


                    Glide.with(context)
                            .asBitmap()
                            .load(desiredImage)
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {

                                    imageView.setImageBitmap(bitmap);
                                    Log.d("imageDetail",bitmap.getWidth() + " : " + bitmap.getHeight());
                                    imageListener.imageCreated(imageView);

                                    if (mOnPhotoEditorListener != null)
                                        mOnPhotoEditorListener.onAddViewListener(viewType, addedViews.size());
                                }
                            });


            }
        });

    }

    /**
     * Create a new instance and scalable touchview
     *
     * @return scalable multitouch listener
     */
    @NonNull
    synchronized private MultiTouchEvent getMultiTouchEvent(FrameLayout deleteArea, View viewBeingDrag, ImageView deletebin, Group deleteGroup, ImageView deleteImage, TextView deleteText, OnDragListener onDragListener) {

        Log.i("Debugmove", "new getMultiTouchListener");
        MultiTouchEvent multiTouchListener = new MultiTouchEvent(
                deleteView,
                parentView,
                this.imageView,
                isTextPinchZoomable,
                mOnPhotoEditorListener, toTop, deleteArea, viewBeingDrag, deletebin, deleteGroup, deleteImage, deleteText, onDragListener);

        //multiTouchListener.setOnMultiTouchListener(this);

        return multiTouchListener;
    }

    /**
     * Create a new instance and scalable touchview
     *
     * @return scalable multitouch listener
     */
    @NonNull
    synchronized private MultiTouchListener getMultiTouchListener(FrameLayout deleteArea, View viewBeingDrag, ImageView deletebin, Group deleteGroup, ImageView deleteImage, TextView deleteText, OnDragListener onDragListener) {

        Log.i("Debugmove", "new getMultiTouchListener");
        MultiTouchListener multiTouchListener = new MultiTouchListener(
                deleteView,
                parentView,
                this.imageView,
                isTextPinchZoomable,
                mOnPhotoEditorListener, toTop, deleteArea, viewBeingDrag, deletebin, deleteGroup, deleteImage, deleteText, onDragListener);

        //multiTouchListener.setOnMultiTouchListener(this);

        return multiTouchListener;
    }

    synchronized private CameraMultiTouchListener getCameraMultiTouchListener(FrameLayout deleteArea, View viewBeingDrag, ImageView deletebin, Group deleteGroup, ImageView deleteImage, TextView deleteText) {

        Log.i("Debugmove", "new getMultiTouchListener");
        CameraMultiTouchListener multiTouchListener = new CameraMultiTouchListener(
                deleteView,
                parentView,
                this.imageView,
                isTextPinchZoomable,
                mOnPhotoEditorListener, toTop, deleteArea, viewBeingDrag, deletebin, deleteGroup, deleteImage, deleteText);

        //multiTouchListener.setOnMultiTouchListener(this);

        return multiTouchListener;
    }

    /**
     * Get root view by its type i.e image,text and emoji
     *
     * @param viewType image,text or emoji
     * @return rootview
     */
    private View getLayout(final ViewType viewType) {
        View rootView = null;
        switch (viewType) {
            case TEXT:
                rootView = mLayoutInflater.inflate(R.layout.view_photo_editor_text, null);
                TextView txtText = rootView.findViewById(R.id.tvPhotoEditorText);
                if (txtText != null && mDefaultTextTypeface != null) {
                    txtText.setGravity(Gravity.CENTER);
                    if (mDefaultEmojiTypeface != null) {
                        txtText.setTypeface(mDefaultTextTypeface);
                    }
                }
                break;
            case IMAGE:
                rootView = mLayoutInflater.inflate(R.layout.view_photo_editor_image, null);
                break;
            case EMPTY_VIEW:
                rootView = mLayoutInflater.inflate(R.layout.empty_view, null);
                break;
            case STICKER:
                rootView = mLayoutInflater.inflate(R.layout.new_sticker_item, null);
                break;
            case NO_BORDER:
                rootView = mLayoutInflater.inflate(R.layout.view_photo_editor_noborder, null);
                break;
            case NO_BORDER_CAMERA:
                rootView = mLayoutInflater.inflate(R.layout.view_photo_editor_noborder_camera, null);
                break;
            case FILLPARENT:
                rootView = mLayoutInflater.inflate(R.layout.view_photo_editor_noborder_fillparent, null);
                break;
            case FILLAIPARENT:
                rootView = mLayoutInflater.inflate(R.layout.view_photo_editor_noborder_fillparent1, null);
                break;
            case WATERMARK:
                rootView = mLayoutInflater.inflate(R.layout.watermark_item, null);
                break;
            case FILTER_VIEW:
                rootView = mLayoutInflater.inflate(R.layout.filter_view_container, null);
                break;
            case EMOJI:
                rootView = mLayoutInflater.inflate(R.layout.view_photo_editor_text, null);
                TextView txtTextEmoji = rootView.findViewById(R.id.tvPhotoEditorText);
                if (txtTextEmoji != null) {
                    if (mDefaultEmojiTypeface != null) {
                        Log.i(TAG_STICKER, "mDefaultEmojiTypeface" + mDefaultEmojiTypeface);
                        txtTextEmoji.setTypeface(mDefaultEmojiTypeface);
                    }
                    txtTextEmoji.setGravity(Gravity.CENTER);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) // KITKAT
                    {
                        txtTextEmoji.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    }
                    //txtTextEmoji.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                }
                break;
        }

        if (rootView != null) {
            //We are setting tag as ViewType to identify what type of the view it is
            //when we remove the view from stack i.e onRemoveViewListener(ViewType viewType, int numberOfAddedViews);
            rootView.setTag(viewType);
            /*final ImageView imgClose = rootView.findViewById(R.id.imgPhotoEditorClose);
            final View finalRootView = rootView;
            if (imgClose != null) {
                imgClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewUndo(finalRootView, viewType);
                    }
                });
            }*/
        }
        return rootView;
    }

    /**
     * Enable/Disable drawing mode to draw on {@link PhotoEditorView}
     *
     * @param brushDrawingMode true if mode is enabled
     */
    public void setBrushDrawingMode(boolean brushDrawingMode) {
        if (brushDrawingView != null)
            brushDrawingView.setBrushDrawingMode(brushDrawingMode);
    }

    /**
     * @return true is brush mode is enabled
     */
    public Boolean getBrushDrawableMode() {
        return brushDrawingView != null && brushDrawingView.getBrushDrawingMode();
    }

    /**
     * set the size of bursh user want to paint on canvas i.e {@link BrushDrawingView}
     *
     * @param size size of brush
     */
    public void setBrushSize(float size) {
        if (brushDrawingView != null)
            brushDrawingView.setBrushSize(size);
    }

    /**
     * set opacity/transparency of brush while painting on {@link BrushDrawingView}
     *
     * @param opacity opacity is in form of percentage
     */
    public void setOpacity(@IntRange(from = 0, to = 100) int opacity) {
        if (brushDrawingView != null) {
            opacity = (int) ((opacity / 100.0) * 255.0);
            brushDrawingView.setOpacity(opacity);
        }
    }

    /**
     * set brush color which user want to paint
     *
     * @param color color value for paint
     */
    public void setBrushColor(@ColorInt int color) {
        if (brushDrawingView != null)
            brushDrawingView.setBrushColor(color);
    }

    /**
     * set the eraser size
     * <br></br>
     * <b>Note :</b> Eraser size is different from the normal brush size
     *
     * @param brushEraserSize size of eraser
     */
    public void setBrushEraserSize(float brushEraserSize) {
        if (brushDrawingView != null)
            brushDrawingView.setBrushEraserSize(brushEraserSize);
    }

    void setBrushEraserColor(@ColorInt int color) {
        if (brushDrawingView != null)
            brushDrawingView.setBrushEraserColor(color);
    }

    /**
     * @return provide the size of eraser
     * @see PhotoEditor#setBrushEraserSize(float)
     */
    public float getEraserSize() {
        return brushDrawingView != null ? brushDrawingView.getEraserSize() : 0;
    }

    /**
     * @return provide the size of eraser
     * @see PhotoEditor#setBrushSize(float)
     */
    public float getBrushSize() {
        if (brushDrawingView != null)
            return brushDrawingView.getBrushSize();
        return 0;
    }

    /**
     * @return provide the size of eraser
     * @see PhotoEditor#setBrushColor(int)
     */
    public int getBrushColor() {
        if (brushDrawingView != null)
            return brushDrawingView.getBrushColor();
        return 0;
    }

    /**
     * <p>
     * Its enables eraser mode after that whenever user drags on screen this will erase the existing
     * paint
     * <br>
     * <b>Note</b> : This eraser will work on paint views only
     * <p>
     */
    public void brushEraser() {
        if (brushDrawingView != null)
            brushDrawingView.brushEraser();
    }

    private void viewUndo(View removedView, ViewType viewType) {
        if (addedViews.size() > 0) {
            if (addedViews.contains(removedView)) {
                parentView.removeView(removedView);
                addedViews.remove(removedView);
                redoViews.add(removedView);
                if (mOnPhotoEditorListener != null) {
                    mOnPhotoEditorListener.onRemoveViewListener(viewType, addedViews.size());
                }
            }
        }
    }

    /**
     * Undo the last operation perform on the {@link PhotoEditor}
     *
     * @return true if there nothing more to undo
     */
    public boolean undo() {
        if (addedViews.size() > 0) {
            Log.i("undoRedo", ">0");
            View removeView = addedViews.get(addedViews.size() - 1);
            if (removeView instanceof BrushDrawingView) {
                return brushDrawingView != null && brushDrawingView.undo();
            } else {
                Log.i("undoRedo", "no brushview");

                addedViews.remove(addedViews.size() - 1);
                parentView.removeView(removeView);
                redoViews.add(removeView);
            }
            if (mOnPhotoEditorListener != null) {
                Object viewTag = removeView.getTag();
                if (viewTag != null && viewTag instanceof ViewType) {
                    Log.i("undoRedo", " viewTag instanceof ViewType");

                    mOnPhotoEditorListener.onRemoveViewListener(((ViewType) viewTag), addedViews.size());
                }
            }
        }
        return addedViews.size() != 0;
    }


    public boolean undoWatermark() {
        if (addedViews.size() > 0) {
            View removeView = addedViews.get(addedViews.size() - 1);
            if (removeView instanceof BrushDrawingView) {
                return brushDrawingView != null && brushDrawingView.undo();
            } else {
                addedViews.remove(addedViews.size() - 1);
                parentView.removeView(removeView);
                //redoViews.add(removeView);
            }
            if (mOnPhotoEditorListener != null) {
                Object viewTag = removeView.getTag();
                if (viewTag != null && viewTag instanceof ViewType) {
                    mOnPhotoEditorListener.onRemoveViewListener(((ViewType) viewTag), addedViews.size());
                }
            }
        }
        return addedViews.size() != 0;
    }

    /**
     * Redo the last operation perform on the {@link PhotoEditor}
     *
     * @return true if there nothing more to redo
     */
    public boolean redo() {
        if (redoViews.size() > 0) {
            View redoView = redoViews.get(redoViews.size() - 1);
            if (redoView instanceof BrushDrawingView) {
                return brushDrawingView != null && brushDrawingView.redo();
            } else {
                redoViews.remove(redoViews.size() - 1);
                parentView.addView(redoView);
                addedViews.add(redoView);
            }
            Object viewTag = redoView.getTag();
            if (mOnPhotoEditorListener != null && viewTag != null && viewTag instanceof ViewType) {
                mOnPhotoEditorListener.onAddViewListener(((ViewType) viewTag), addedViews.size());
            }
        }
        return redoViews.size() != 0;
    }

    private void clearBrushAllViews() {
        if (brushDrawingView != null)
            brushDrawingView.clearAll();
    }

    /**
     * Removes all the edited operations performed {@link PhotoEditorView}
     * This will also clear the undo and redo stack
     */
    public void clearAllViews() {
        for (int i = 0; i < addedViews.size(); i++) {
            parentView.removeView(addedViews.get(i));
        }
        if (addedViews.contains(brushDrawingView)) {
            parentView.addView(brushDrawingView);
        }
        addedViews.clear();
        redoViews.clear();
        clearBrushAllViews();
    }

    public void reDrawAllviews() {
        ArrayList<View> dupaddedViews = new ArrayList<>(addedViews);
        for (int i = 0; i < addedViews.size(); i++) {
            parentView.removeView(addedViews.get(i));
        }
        addedViews.clear();
        for (int i = 0; i < dupaddedViews.size(); i++) {
            parentView.addView(dupaddedViews.get(i));
            addedViews.add(dupaddedViews.get(i));
        }

        redoViews.clear();
        clearBrushAllViews();
    }

    /**
     * Remove all helper boxes from views
     */
    @UiThread
    public void clearHelperBox() {
        for (int i = 0; i < parentView.getChildCount(); i++) {
            View childAt = parentView.getChildAt(i);
            FrameLayout frmBorder = childAt.findViewById(R.id.frmBorder);
            if (frmBorder != null) {
                frmBorder.setBackgroundResource(0);
            }
            ImageView imgClose = childAt.findViewById(R.id.imgPhotoEditorClose);
            if (imgClose != null) {
                imgClose.setVisibility(View.GONE);
            }
           /* SingleFingerView singleFingerView=childAt.findViewById(R.id.tiv);
            singleFingerView.hideResizebutton();*/

            ImageView push_view = childAt.findViewById(R.id.push_view);
            if (push_view != null) {
                push_view.setVisibility(View.GONE);
            }

        }
    }

    /**
     * Setup of custom effect using effect type and set parameters values
     *
     * @param customEffect {@link CustomEffect.Builder#setParameter(String, Object)}
     */
    public void setFilterEffect(CustomEffect customEffect) {
        parentView.setFilterEffect(customEffect);
    }

    /**
     * Set pre-define filter available
     *
     * @param filterType type of filter want to apply {@link PhotoEditor}
     */
    public void setFilterEffect(PhotoFilter filterType) {
        parentView.setFilterEffect(filterType);
    }

    /**
     * A callback to save the edited image asynchronously
     */
    public interface OnSaveListener {

        /**
         * Call when edited image is saved successfully on given path
         *
         * @param imagePath path on which image is saved
         */
        void onSuccess(@NonNull String imagePath);

        /**
         * Call when failed to saved image on given path
         *
         * @param exception exception thrown while saving image
         */
        void onFailure(@NonNull Exception exception);
    }

    /**
     * Save the edited image on given path
     *
     * @param imagePath      path on which image to be saved
     * @param onSaveListener callback for saving image
     * @see OnSaveListener
     */
    @RequiresPermission(allOf = {Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void saveAsFile(@NonNull final String imagePath, @NonNull final OnSaveListener onSaveListener) {
        saveAsFile(imagePath, new SaveSettings.Builder().build(), onSaveListener);
    }

    /**
     * Save the edited image on given path
     *
     * @param imagePath      path on which image to be saved
     * @param saveSettings   builder for multiple save options {@link SaveSettings}
     * @param onSaveListener callback for saving image
     * @see OnSaveListener
     */
    @SuppressLint("StaticFieldLeak")
    @RequiresPermission(allOf = {Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void saveAsFile(@NonNull final String imagePath,
                           @NonNull final SaveSettings saveSettings,
                           @NonNull final OnSaveListener onSaveListener) {
        Log.d(TAG, "Image Path: " + imagePath);
        parentView.saveFilter(new OnSaveBitmap() {
            @Override
            public void onBitmapReady(Bitmap saveBitmap) {
                new AsyncTask<String, String, Exception>() {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        clearHelperBox();
                        brushDrawingView.destroyDrawingCache();
                    }

                    @SuppressLint("MissingPermission")
                    @Override
                    protected Exception doInBackground(String... strings) {
                        // Create a media file name
                        File file = new File(imagePath);
                        try {
                            FileOutputStream out = new FileOutputStream(file, false);
                            if (parentView != null) {
                                Bitmap capturedBitmap = saveSettings.isTransparencyEnabled()
                                        ? BitmapUtil.removeTransparency(captureView(parentView))
                                        : captureView(parentView);
                                capturedBitmap.compress(saveSettings.getCompressFormat(), saveSettings.getCompressQuality(), out);
                            }
                            out.flush();
                            out.close();
                            Log.d(TAG, "Filed Saved Successfully");
                            return null;
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d(TAG, "Failed to save File");
                            return e;
                        }
                    }

                    @Override
                    protected void onPostExecute(Exception e) {
                        super.onPostExecute(e);
                        if (e == null) {
                            //Clear all views if its enabled in save settings
                            if (saveSettings.isClearViewsEnabled()) clearAllViews();
                            onSaveListener.onSuccess(imagePath);
                        } else {
                            onSaveListener.onFailure(e);
                        }
                    }

                }.execute();
            }

            @Override
            public void onFailure(Exception e) {
                onSaveListener.onFailure(e);
            }
        });
    }

    /**
     * Save the edited image as bitmap
     *
     * @param onSaveBitmap callback for saving image as bitmap
     * @see OnSaveBitmap
     */
    @SuppressLint("StaticFieldLeak")
    public void saveAsBitmap(@NonNull final OnSaveBitmap onSaveBitmap) {
        saveAsBitmap(new SaveSettings.Builder().build(), onSaveBitmap);
    }

    /**
     * Save the edited image as bitmap
     *
     * @param saveSettings builder for multiple save options {@link SaveSettings}
     * @param onSaveBitmap callback for saving image as bitmap
     * @see OnSaveBitmap
     */
    @SuppressLint("StaticFieldLeak")
    public void saveAsBitmap(@NonNull final SaveSettings saveSettings,
                             @NonNull final OnSaveBitmap onSaveBitmap) {
        parentView.saveFilter(new OnSaveBitmap() {
            @Override
            public void onBitmapReady(Bitmap saveBitmap) {
                new AsyncTask<String, String, Bitmap>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        clearHelperBox();
                        brushDrawingView.destroyDrawingCache();
                    }

                    @Override
                    protected Bitmap doInBackground(String... strings) {
                        if (parentView != null) {
                            return saveSettings.isTransparencyEnabled() ?
                                    BitmapUtil.removeTransparency(captureView(parentView))
                                    : captureView(parentView);
                        } else {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        super.onPostExecute(bitmap);
                        if (bitmap != null) {
                            if (saveSettings.isClearViewsEnabled()) clearAllViews();
                            onSaveBitmap.onBitmapReady(bitmap);
                        } else {
                            onSaveBitmap.onFailure(new Exception("Failed to load the bitmap"));
                        }
                    }

                }.execute();
            }

            @Override
            public void onFailure(Exception e) {
                onSaveBitmap.onFailure(e);
            }
        });
    }

    private static String convertEmoji(String emoji) {
        String returnedEmoji;
        try {
            int convertEmojiToInt = Integer.parseInt(emoji.substring(2), 16);
            returnedEmoji = new String(Character.toChars(convertEmojiToInt));
        } catch (NumberFormatException e) {
            returnedEmoji = "";
        }
        return returnedEmoji;
    }

    private Bitmap captureView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(
                view.getWidth(),
                view.getHeight(),
                Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    /**
     * Callback on editing operation perform on {@link PhotoEditorView}
     *
     * @param onPhotoEditorListener {@link OnPhotoEditorListener}
     */
    public void setOnPhotoEditorListener(@NonNull OnPhotoEditorListener onPhotoEditorListener) {
        this.mOnPhotoEditorListener = onPhotoEditorListener;
    }

    /**
     * Check if any changes made need to save
     *
     * @return true if nothing is there to change
     */
    public boolean isCacheEmpty() {
        Log.i("EditImage", "isCacheEmpty addedViews size: " + addedViews.size());
        return addedViews.size() == 0 && redoViews.size() == 0;

    }

    public boolean isCacheEmptyForSecondLayer() {
        Log.i("EditImage", "isCacheEmptyForSecondLayer addedViews size: " + addedViews.size());
        return addedViews.size() == 1 && redoViews.size() == 0;

    }

    public boolean isCacheEmptyForThirdLayer() {
        Log.i("EditImage", "isCacheEmptyForThirdLayer addedViews size: " + addedViews.size());
        return addedViews.size() == 0;

    }


    @Override
    public void onViewAdd(BrushDrawingView brushDrawingView) {
        if (redoViews.size() > 0) {
            redoViews.remove(redoViews.size() - 1);
        }
        addedViews.add(brushDrawingView);
        if (mOnPhotoEditorListener != null) {
            mOnPhotoEditorListener.onAddViewListener(ViewType.BRUSH_DRAWING, addedViews.size());
        }
    }

    @Override
    public void onViewRemoved(BrushDrawingView brushDrawingView) {
        if (addedViews.size() > 0) {
            View removeView = addedViews.remove(addedViews.size() - 1);
            if (!(removeView instanceof BrushDrawingView)) {
                parentView.removeView(removeView);
            }
            redoViews.add(removeView);
        }
        if (mOnPhotoEditorListener != null) {
            mOnPhotoEditorListener.onRemoveViewListener(ViewType.BRUSH_DRAWING, addedViews.size());
        }
    }

    @Override
    public void onStartDrawing() {
        if (mOnPhotoEditorListener != null) {
            mOnPhotoEditorListener.onStartViewChangeListener(ViewType.BRUSH_DRAWING);
        }
    }

    @Override
    public void onStopDrawing() {
        if (mOnPhotoEditorListener != null) {
            mOnPhotoEditorListener.onStopViewChangeListener(ViewType.BRUSH_DRAWING);
        }
    }


    /**
     * Builder pattern to define {@link PhotoEditor} Instance
     */
    public static class Builder {

        private final Context context;
        private final PhotoEditorView parentView;
        private final ImageView imageView;
        private View deleteView;
        private final BrushDrawingView brushDrawingView;
        private Typeface textTypeface;
        private Typeface emojiTypeface;
        private final boolean toTop;
        //By Default pinch zoom on text is enabled
        private boolean isTextPinchZoomable = true;

        /**
         * Building a PhotoEditor which requires a Context and PhotoEditorView
         * which we have setup in our xml layout
         *
         * @param context         context
         * @param photoEditorView {@link PhotoEditorView}
         */
        public Builder(Context context, PhotoEditorView photoEditorView, boolean bringTop) {
            this.context = context;
            parentView = photoEditorView;
            imageView = photoEditorView.getSource();
            brushDrawingView = photoEditorView.getBrushDrawingView();
            this.toTop = bringTop;
        }

        Builder setDeleteView(View deleteView) {
            this.deleteView = deleteView;
            return this;
        }

        /**
         * set default text font to be added on image
         *
         * @param textTypeface typeface for custom font
         * @return {@link Builder} instant to build {@link PhotoEditor}
         */
        public Builder setDefaultTextTypeface(Typeface textTypeface) {
            this.textTypeface = textTypeface;
            return this;
        }

        /**
         * set default font specific to add emojis
         *
         * @param emojiTypeface typeface for custom font
         * @return {@link Builder} instant to build {@link PhotoEditor}
         */
        public Builder setDefaultEmojiTypeface(Typeface emojiTypeface) {
            this.emojiTypeface = emojiTypeface;
            return this;
        }

        /**
         * set false to disable pinch to zoom on text insertion.By deafult its true
         *
         * @param isTextPinchZoomable flag to make pinch to zoom
         * @return {@link Builder} instant to build {@link PhotoEditor}
         */
        public Builder setPinchTextScalable(boolean isTextPinchZoomable) {
            this.isTextPinchZoomable = isTextPinchZoomable;
            return this;
        }

        /**
         * @return build PhotoEditor instance
         */
        public PhotoEditor build() {
            return new PhotoEditor(this);
        }
    }

    /**
     * Provide the list of emoji in form of unicode string
     *
     * @param context context
     * @return list of emoji unicode
     */
    public static ArrayList<String> getEmojis(Context context) {
        ArrayList<String> convertedEmojiList = new ArrayList<>();
        String[] emojiList = context.getResources().getStringArray(R.array.brochill_emoji);
        for (String emojiUnicode : emojiList) {
            convertedEmojiList.add(convertEmoji(emojiUnicode));
        }
        return convertedEmojiList;
    }
}
