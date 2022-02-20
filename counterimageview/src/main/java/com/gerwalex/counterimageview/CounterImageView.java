package com.gerwalex.counterimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.BindingAdapter;

import com.hookedonplay.decoviewlib.DecoView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class CounterImageView extends FrameLayout {
    private final DecoView decoView;
    private String filename;

    @BindingAdapter(value = {"file"})
    public static void setValue(CounterImageView view, String filename) {
        view.setValue(filename);
    }

    public CounterImageView(Context context) {
        this(context, null);
    }

    public CounterImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CounterImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        ImageView imageView = new ImageView(context, attrs);
        imageView.setPadding(30, 30, 30, 30);
        decoView = new DecoView(context, attrs);
        RequestCreator request;
        if (isInEditMode()) {
            imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.img, context.getTheme()));
        } else {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CounterImageView);
            try {
                int file = a.getResourceId(R.styleable.CounterImageView_filename, -1);
                if (file != -1) {
                    request = Picasso.get().load(file);
                } else {
                    filename = a.getString(R.styleable.CounterImageView_filename);
                    request = Picasso.get().load(new File(filename));
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
    }

    public DecoView getDecoView() {
        return decoView;
    }

    private void setValue(String filename) {
        this.filename = filename;
    }
}
