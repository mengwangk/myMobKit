package com.mymobkit.ui.viewer;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.mymobkit.ui.adapter.PhotoGalleryAdapter;

import java.util.HashMap;
import java.util.Map;

import static com.mymobkit.common.LogUtils.makeLogTag;
import static com.mymobkit.common.LogUtils.LOGE;

/**
 * Gallery view using horizontal scroll view.
 */
public class PhotoGalleryView extends HorizontalScrollView implements OnClickListener {

    private static final String TAG = makeLogTag(PhotoGalleryView.class);

    /**
     */
    public interface CurrentImageChangeListener {
        void onCurrentImageChanged(int position, View viewIndicator);
    }

    /**
     */
    public interface OnItemClickListener {
        void onClick(View view, int pos);
    }

    private CurrentImageChangeListener imageChangeListener;

    private OnItemClickListener itemClickListener;

    private LinearLayout container;

    private int childWidth;

    private int childHeight;

    private int currentIndex;

    private int firstIndex;

    private View firstView;

    private PhotoGalleryAdapter photoGalleryAdapter;

    private int countOneScreen;

    private int screenWidth;

    /**
     */
    private Map<View, Integer> viewPos = new HashMap<View, Integer>();

    public PhotoGalleryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        screenWidth = outMetrics.widthPixels;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        container = (LinearLayout) getChildAt(0);
    }

    /**
     */
    protected void loadNextImage() {
        try {
            if (currentIndex == photoGalleryAdapter.getCount() - 1) {
                return;
            }
            scrollTo(0, 0);
            viewPos.remove(container.getChildAt(0));
            container.removeViewAt(0);

            View view = photoGalleryAdapter.getView(++currentIndex, null, container);
            view.setOnClickListener(this);
            container.addView(view);
            viewPos.put(view, currentIndex);

            firstIndex++;
            if (imageChangeListener != null) {
                notifyCurrentImageChanged();
            }
        } catch (Exception ex) {
            LOGE(TAG, "[loadNextImage] Unable to load next image", ex);
        }
    }

    /**
     */
    protected void loadPreImage() {
        if (firstIndex == 0)
            return;
        int index = currentIndex - countOneScreen;
        if (index >= 0) {
//			container = (LinearLayout) getChildAt(0);
            int oldViewPos = container.getChildCount() - 1;
            viewPos.remove(container.getChildAt(oldViewPos));
            container.removeViewAt(oldViewPos);

            View view = photoGalleryAdapter.getView(index, null, container);
            viewPos.put(view, index);
            container.addView(view, 0);
            view.setOnClickListener(this);
            scrollTo(childWidth, 0);
            currentIndex--;
            firstIndex--;
            if (imageChangeListener != null) {
                notifyCurrentImageChanged();
            }
        }
    }

    /**
     *
     */
    public void notifyCurrentImageChanged() {
        //for (int i = 0; i < container.getChildCount(); i++) {
        //    container.getChildAt(i).setBackgroundColor(Color.WHITE);
        //}
        //imageChangeListener.onCurrentImageChanged(firstIndex, container.getChildAt(0));
    }

    /**
     * @param adapter
     */
    public void initDatas(PhotoGalleryAdapter adapter) {
        photoGalleryAdapter = adapter;
        container = (LinearLayout) getChildAt(0);
        // final View view = adapter.getView(0, null, container);
        final View view = adapter.getView(0, null, container);
        container.addView(view);

        if (childWidth == 0 && childHeight == 0) {
            int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            view.measure(w, h);
            childHeight = view.getMeasuredHeight();
            childWidth = view.getMeasuredWidth();
            childHeight = view.getMeasuredHeight();
            countOneScreen = (screenWidth / childWidth == 0) ? screenWidth / childWidth + 1 : screenWidth / childWidth + 2;
        }
        if (countOneScreen > adapter.getCount()) {
            countOneScreen = adapter.getCount();
        }
        initFirstScreenChildren(countOneScreen);
    }

    /**
     *
     * @param countOneScreen
     */
    public void initFirstScreenChildren(int countOneScreen) {
        container = (LinearLayout) getChildAt(0);
        container.removeAllViews();
        viewPos.clear();

        for (int i = 0; i < countOneScreen; i++) {
            View view = photoGalleryAdapter.getView(i, null, container);
            view.setOnClickListener(this);
            container.addView(view);
            viewPos.put(view, i);
            currentIndex = i;
        }

        if (imageChangeListener != null) {
            notifyCurrentImageChanged();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_MOVE:

                int scrollX = getScrollX();
                if (scrollX >= childWidth) {
                    loadNextImage();
                }
                if (scrollX == 0) {
                    loadPreImage();
                }
                break;
        }
        return super.onTouchEvent(motionEvent);
    }

    @Override
    public void onClick(View v) {
        if (itemClickListener != null) {
            for (int i = 0; i < container.getChildCount(); i++) {
                container.getChildAt(i).setBackgroundColor(Color.WHITE);
            }
            itemClickListener.onClick(v, viewPos.get(v));
        }
    }

    public void setOnItemClickListener(OnItemClickListener onClickListener) {
        this.itemClickListener = onClickListener;
    }

    public void setCurrentImageChangeListener(CurrentImageChangeListener listener) {
        this.imageChangeListener = listener;
    }
}
