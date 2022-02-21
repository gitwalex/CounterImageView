package com.gerwalex.counterimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class CounterImageView extends ViewGroup {
    private static final int BUTTONSIZE = 96;
    private final DecoView decoView;
    private final int desiredSize;
    private final ImageView imageView;

    public CounterImageView(Context context) {
        this(context, null);
    }

    public CounterImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.style.CounterImageViewStyle);
    }

    public CounterImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        imageView = new ImageView(context, attrs);
        decoView = new DecoView(context, attrs);
        if (isInEditMode()) {
            imageView.setImageResource(R.drawable.img);
        } else {
            RequestCreator request;
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CounterImageView, 0,
                    R.style.CounterImageViewStyle);
            try {
                int padding =
                        (int) a.getDimension(R.styleable.CounterImageView_imagePadding, decoView.getDefaultLineWidth());
                imageView.setPadding(padding, padding, padding, padding);
                int file = a.getResourceId(R.styleable.CounterImageView_filename, -1);
                if (file != -1) {
                    request = Picasso.get().load(file);
                } else {
                    String filename = a.getString(R.styleable.CounterImageView_filename);
                    if (filename != null) {
                        request = Picasso.get().load(filename);
                    } else {
                        request = Picasso.get().load(R.drawable.img);
                    }
                }
                Drawable placeholder = a.getDrawable(R.styleable.CounterImageView_placeholder);
                if (placeholder != null) {
                    request.placeholder(placeholder);
                }
                int onError = a.getResourceId(R.styleable.CounterImageView_onError, -1);
                if (onError != -1) {
                    request.error(onError);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            } finally {
                a.recycle();
            }
            request.transform(new CropCircleTransformation());
            request.into(imageView);
        }
        addView(imageView);
        addView(decoView);
        desiredSize = (int) (getResources().getDisplayMetrics().density * BUTTONSIZE);
    }

    public DecoView getDecoView() {
        return decoView;
    }

    private int measureDimension(int desiredSize, int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        return specMode == MeasureSpec.UNSPECIFIED ? desiredSize : specSize;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int size = getWidth();
        imageView.layout(0, 0, size, size);
        decoView.layout(0, 0, size, size);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = Math.min(measureDimension(desiredSize, widthMeasureSpec),
                measureDimension(desiredSize, heightMeasureSpec));
        int childMeasureSec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
        imageView.measure(childMeasureSec, childMeasureSec);
        decoView.measure(childMeasureSec, childMeasureSec);
        //MUST CALL THIS
        setMeasuredDimension(size, size);
    }
}