package com.gerwalex.counterimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class CounterImageView extends ViewGroup {
    private static final int BUTTONSIZE = 96;

    private final DecoView decoView;
    private final int desiredSize;
    private final ImageView imageView;

    @BindingAdapter(value = {"image"})
    public static void loadImage(CounterImageView view, String filename) {
        view.loadImage(filename);
    }

    public CounterImageView(Context context) {
        this(context, null);
    }

    public CounterImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.style.CounterImageViewStyle);
    }

    public CounterImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        imageView = new ImageView(context, attrs);
        decoView = new DecoView(context, attrs);
        addView(imageView);
        addView(decoView);
        desiredSize = (int) (getResources().getDisplayMetrics().density * BUTTONSIZE);
        TypedArray a = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.CounterImageView, 0, R.style.CounterImageViewStyle);
        try {
            int padding =
                    (int) a.getDimension(R.styleable.CounterImageView_imagePadding, decoView.getDefaultLineWidth());
            imageView.setPadding(padding, padding, padding, padding);
            if (isInEditMode()) {
                imageView.setImageResource(R.drawable.img);
            } else {
                int drawableRes = a.getResourceId(R.styleable.CounterImageView_placeholder, -1);
                if (drawableRes != -1) {
                    loadImage(drawableRes);
                }
            }
        } finally {
            a.recycle();
        }
    }

    public DecoView getDecoView() {
        return decoView;
    }

    public void loadImage(@DrawableRes int resourceId) {
        RequestCreator request = Picasso.get().load(resourceId);
        request.transform(new CropCircleTransformation());
        request.into(imageView);
    }

    public void loadImage(@Nullable String filename) {
        if (filename != null) {
            loadImage(new File(filename));
        }
    }

    public void loadImage(@Nullable Uri uri) {
        if (uri != null) {
            RequestCreator request = Picasso.get().load(uri);
            request.transform(new CropCircleTransformation());
            request.into(imageView);
        }
    }

    public void loadImage(@Nullable File file) {
        if (file != null) {
            RequestCreator request = Picasso.get().load(file);
            request.transform(new CropCircleTransformation());
            request.into(imageView);
            invalidate();
        }
    }

    private int measureDimension(int desiredSize, int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            return specSize;
        }
        return specMode == MeasureSpec.AT_MOST ? specSize : desiredSize;
    }

    /**
     * Zentrieren der ImageView und DecoView innerhalb der ViewGroup
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int w = r - l;
        int h = b - t;
        int size = Math.min(w, h);
        int wInset = (w - size) / 2;
        int hInset = (h - size) / 2;
        imageView.layout(l + wInset, t + hInset, w - wInset, h - hInset);
        decoView.layout(l + wInset, t + hInset, w - wInset, h - hInset);
        Log.d("gerwalex", String.format("onLayout: w:%1d h:%2d, wInset %3d, hInset: %4d", w, h, wInset, hInset));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = measureDimension(desiredSize, widthMeasureSpec);
        int h = measureDimension(desiredSize, heightMeasureSpec);
        //MUST CALL THIS
        setMeasuredDimension(w, h);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //        imageView.layout(0, 0, w, h);
        //        decoView.layout(0, 0, w, h);
        Log.d("gerwalex", String.format("onSizeChanged: w:%1d h:%2d", w, h));
    }
}