package com.bonlala.widget.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;

import com.bonlala.widget.R;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;

/**
 * 圆形ImageView，可设置最多两个宽度不同且颜色不同的圆形边框。 设置颜色在xml布局文件中由自定义属性配置参数指定
 */
public class RoundImageView extends androidx.appcompat.widget.AppCompatImageView { //com.jkcq.gym.view.RoundImageView

    public static final String TAG = RoundImageView.class.getSimpleName();

    private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;

    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int COLORDRAWABLE_DIMENSION = 2;

    /**
     * 默认外边框直径大小
     */
    private static final int DEFAULT_BORDER_WIDTH = 0;
    /**
     * 默认外边框颜色
     */
    private static final int DEFAULT_BORDER_COLOR = Color.BLACK;

    /**
     * 默认不显示外边框
     */
    private static final boolean DEFAULT_BORDER_OVERLAY = false;

    private final RectF mDrawableRect = new RectF();
    private final RectF mBorderRect = new RectF();

    private final Matrix mShaderMatrix = new Matrix();
    private final Paint mBitmapPaint = new Paint();
    private final Paint mBorderPaint = new Paint();

    private int mBorderColor = DEFAULT_BORDER_COLOR;
    private int mBorderWidth = DEFAULT_BORDER_WIDTH;

    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;
    private int mBitmapWidth;
    private int mBitmapHeight;


    private float mDrawableRadius;
    private float mBorderRadius;

    private ColorFilter mColorFilter;

    private boolean mReady;
    private boolean mSetupPending;
    private boolean mBorderOverlay;

    public RoundImageView(Context context) {
        super(context);

        init(context);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.Commonroundedimageview);
        mBorderWidth = a.getDimensionPixelSize(
                R.styleable.Commonroundedimageview_border_thickness, DEFAULT_BORDER_WIDTH);
        mBorderColor = a
                .getColor(R.styleable.Commonroundedimageview_border_outside_color,
                        DEFAULT_BORDER_COLOR);
        a.recycle();

        init(context);
    }

    private void init(Context context) {
        super.setScaleType(SCALE_TYPE);
        mReady = true;

        if (mSetupPending) {
            setup();
            mSetupPending = false;
        }
    }

    @Override
    public ScaleType getScaleType() {
        return SCALE_TYPE;
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (scaleType != SCALE_TYPE) {
            throw new IllegalArgumentException(String.format("ScaleType %s not supported.", scaleType));
        }
    }

    @Override
    public void setAdjustViewBounds(boolean adjustViewBounds) {
        if (adjustViewBounds) {
            throw new IllegalArgumentException("adjustViewBounds not supported.");
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() == null) {
            return;
        }
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, mDrawableRadius, mBitmapPaint);
        if (mBorderWidth != 0) {
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, mBorderRadius, mBorderPaint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setup();
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderColor(int borderColor) {
        if (borderColor == mBorderColor) {
            return;
        }

        mBorderColor = borderColor;
        mBorderPaint.setColor(mBorderColor);
        invalidate();
    }

    public void setBorderColorResource(@ColorRes int borderColorRes) {
        setBorderColor(getContext().getResources().getColor(borderColorRes));
    }

    public int getBorderWidth() {
        return mBorderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        if (borderWidth == mBorderWidth) {
            return;
        }

        mBorderWidth = borderWidth;
        setup();
    }

    public boolean isBorderOverlay() {
        return mBorderOverlay;
    }

    public void setBorderOverlay(boolean borderOverlay) {
        if (borderOverlay == mBorderOverlay) {
            return;
        }

        mBorderOverlay = borderOverlay;
        setup();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = bm;
        setup();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        mBitmap = getBitmapFromDrawable(drawable);
        setup();
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        super.setImageResource(resId);
        mBitmap = getBitmapFromDrawable(getDrawable());
        setup();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        mBitmap = getBitmapFromDrawable(getDrawable());
        setup();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        if (cf == mColorFilter) {
            return;
        }

        mColorFilter = cf;
        mBitmapPaint.setColorFilter(mColorFilter);
        invalidate();
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;

            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private void setup() {
        if (!mReady) {
            mSetupPending = true;
            return;
        }

        if (mBitmap == null) {
            return;
        }

        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setShader(mBitmapShader);

        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);

        mBitmapHeight = mBitmap.getHeight();
        mBitmapWidth = mBitmap.getWidth();

        mBorderRect.set(0, 0, getWidth(), getHeight());
        mBorderRadius = Math.min((mBorderRect.height() - mBorderWidth) / 2, (mBorderRect.width() - mBorderWidth) / 2);

        mDrawableRect.set(mBorderRect);
        if (!mBorderOverlay) {
            mDrawableRect.inset(mBorderWidth, mBorderWidth);
        }
        mDrawableRadius = Math.min(mDrawableRect.height() / 2, mDrawableRect.width() / 2);

        updateShaderMatrix();
        invalidate();
    }

    private void updateShaderMatrix() {
        float scale;
        float dx = 0;
        float dy = 0;

        mShaderMatrix.set(null);

        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
            scale = mDrawableRect.height() / (float) mBitmapHeight;
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
        } else {
            scale = mDrawableRect.width() / (float) mBitmapWidth;
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
        }

        mShaderMatrix.setScale(scale, scale);
        mShaderMatrix.postTranslate((int) (dx + 0.5f) + mDrawableRect.left, (int) (dy + 0.5f) + mDrawableRect.top);

        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }

    /**
     * 默认图片ID
     */
    private int defaultImageResId;

    private int errorResId;

    public void setDefaultImage(int defaultImageResId) {
        this.defaultImageResId = defaultImageResId;
    }

    public void setErrorResId(int errorResId) {
        this.errorResId = errorResId;
    }

    public void setImageUrl(String url) {
        setImageUrl(getContext(), url);
    }

    public void setImageUrl(Context context, String url) {
        setImageUrl(context, url, getId());
    }

    public void setImageUrl(Context context, String url, int defaultImageResId, int errorResId) {
        this.defaultImageResId = defaultImageResId;
        this.errorResId = errorResId;
        //setImageUrl(context, url, getId(), true);
    }

    public void setImageUrl(Context context, String url, int key) {
        //setImageUrl(context, url, key, true);
    }

    /*public void setImageUrl(Context context, String url, int key, boolean needCache) {
        if (null == context || !Util.isOnMainThread()) {
            return;
        }
        if (TextUtils.isEmpty(url)) {
            Logger.d(TAG, "url is null.");
            if (defaultImageResId != 0) {
                setImageResource(defaultImageResId);
            }
            return;
        }

        if ((key >>> 24) < 2) {
            key = R.string.app_name;
        }

        this.setTag(key, url);

        DrawableRequestBuilder requestBuilder = Glide.with(context)
                .load(url)//目标URL
                .placeholder(defaultImageResId) //占位图片
                .error(defaultImageResId) //图片获取失败时默认显示的图片
                .centerCrop()
                .crossFade()
                .dontAnimate();
//                .progressListener(new GlideRequestListener());

        if (needCache) {
            requestBuilder.diskCacheStrategy(DiskCacheStrategy.ALL);
        } else {
            requestBuilder.diskCacheStrategy(DiskCacheStrategy.NONE);
        }

        requestBuilder.into(this);
    }

    public void setImageUrl(Context context, String url, int key, boolean needCache, String signature) {
        if (null == context || !Util.isOnMainThread()) {
            return;
        }
        if (TextUtils.isEmpty(url)) {
            Logger.d(TAG, "url is null.");
            if (defaultImageResId != 0) {
                setImageResource(defaultImageResId);
            }
            return;
        }

        if ((key >>> 24) < 2) {
            key = R.string.app_name;
        }

        try {
            this.setTag(key, url);
            DrawableTypeRequest<String> typeRequest = Glide.with(context).load(url);
            if (url.endsWith(".gif")) {
                typeRequest.asGif()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE);
            } else {
                typeRequest.diskCacheStrategy(DiskCacheStrategy.ALL);//缓存全尺寸图片，也缓存其他尺寸图片
            }

            DrawableRequestBuilder requestBuilder = typeRequest
                    .placeholder(defaultImageResId) //占位图片
                    .error(defaultImageResId) //图片获取失败时默认显示的图片
                    .centerCrop()
                    .crossFade()
                    .dontAnimate();
//                    .progressListener(new GlideRequestListener());

            if (needCache) {
                requestBuilder.diskCacheStrategy(DiskCacheStrategy.ALL);
            } else {
                requestBuilder.diskCacheStrategy(DiskCacheStrategy.NONE);
            }

            if (!TextUtils.isEmpty(signature)) {
                requestBuilder.signature(new StringSignature(signature));
            }

            requestBuilder.into(this);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Logger.d(TAG, "setImageUrl error. " + e.getMessage());
        }
    }*/
}
