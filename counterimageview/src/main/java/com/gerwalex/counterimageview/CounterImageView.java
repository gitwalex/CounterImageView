package com.gerwalex.counterimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class CounterImageView extends FrameLayout {
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
                int padding = (int) a.getDimension(R.styleable.CounterImageView_imagePadding, 0);
                imageView.setPadding(getPaddingLeft() + padding, getPaddingTop() + padding, getPaddingRight() + padding,
                        getPaddingBottom() + padding);
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

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int size = getWidth();
        imageView.layout(0, 0, size, size);
        decoView.layout(0, 0, size, size);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec) - getPaddingStart() - getPaddingEnd();
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec) - getPaddingBottom() - getPaddingTop();
        int width = desiredSize;
        int height = desiredSize;
        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = widthSize;
        }
        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = heightSize;
        }
        int size = Math.min(width, height);
        int childMeasureSec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
        imageView.measure(childMeasureSec, childMeasureSec);
        decoView.measure(childMeasureSec, childMeasureSec);
        //MUST CALL THIS
        setMeasuredDimension(size, size);
    }
}