package com.gathersg.user.classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gathersg.user.R;

public class CircularImageView extends AppCompatImageView {

    private final Paint paint = new Paint();

    public CircularImageView(Context context) {
        super(context);
        init();
    }

    public CircularImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircularImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint.setAntiAlias(true);
        paint.setShader(createImageShader());
    }

    private BitmapShader createImageShader() {
        BitmapShader shader = new BitmapShader(
                getBitmapFromDrawable(),
                Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP
        );
        return shader;
    }

    private Bitmap getBitmapFromDrawable() {
        // Use Glide to load the image into a Bitmap
        RequestOptions requestOptions = new RequestOptions()
                .centerCrop()
                .circleCrop()
                .placeholder(R.drawable.default_icon); // Ensures circular cropping

        Glide.with(getContext())
                .asBitmap()
                .load(R.drawable.default_icon) // Set a default image resource
                .apply(requestOptions)
                .into(this); // Loads the image directly into the CircularImageView

        // Dummy placeholder
        return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int radius = Math.min(getWidth(), getHeight()) / 2;
        canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, radius, paint);
    }
}
