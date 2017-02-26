package com.chen.maptest.MyView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by chen on 17-1-20.
 */

public class CropImageLayout extends RelativeLayout {

    public final static int ORIENTATION_UP = 0;
    public final static int ORIENTATION_LEFT = 1;
    public final static int ORIENTATION_DOWN = 2;
    public final static int ORIENTATION_RIGHT = 3;

    private CropImageEdge mCropImageEdge = null;
    private ImageView mCropImageBack = null;
    private Context mContext;
    private Bitmap mBitmap;

    public CropImageLayout(Context context) {
        super(context);
        init(context);
    }

    public CropImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CropImageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;

//        mCropImageBack = new CropImageBack(mContext);
//        addView(mCropImageBack, new RelativeLayout.LayoutParams(
//                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mCropImageBack = new ImageView(mContext);
        addView(mCropImageBack,new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        mCropImageEdge = new CropImageEdge(mContext);
        addView(mCropImageEdge, new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

//    public void setCropImage(Bitmap bitmap) {
//        mBitmap = bitmap;
//        mCropImageBack.setCropImageBitmap(bitmap);
//    }

    public void setCropImage(String filename){
        Glide.with(mContext).load(filename).into(mCropImageBack);
        mCropImageEdge.setBorderBound(new RectF(0,0,1080,1820));
    }

    public String getCroppedImage(int currentOrientation) {
        return mCropImageEdge.getCropImage(currentOrientation);
    }


    private class CropImageBack extends SurfaceView {

        private final static String TAG = "CropImageBack";
        private Bitmap mBitmap;
        private Paint p;
        private float scalar;

        public CropImageBack(Context context) {
            super(context);
            init(context);
        }

        public CropImageBack(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(context);
        }

        public CropImageBack(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init(context);
        }

        private void init(Context context) {
            this.getHolder().addCallback(new CropImageHolderCallback(this));
            p = new Paint();
        }

        public void setCropImageBitmap(Bitmap bitmap) {
            mBitmap = bitmap;
        }

        public float getScalar() {
            return scalar;
        }


        private class CropImageHolderCallback implements SurfaceHolder.Callback {

            SurfaceView mSurfaceView = null;

            public CropImageHolderCallback(SurfaceView surfaceView) {
                mSurfaceView = surfaceView;
            }

            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {

                int w = mBitmap.getWidth();
                int h = mBitmap.getHeight();

                int lw = getWidth();
                int lh = getHeight();

                float br = w * 1.0f / h;
                float lr = lw * 1.0f / lh;


                if (br > lr)
                    scalar = lw * 1.0f / w; //图像较宽
                else
                    scalar = lh * 1.0f / h; //图像较长

                Matrix matrix = new Matrix();
                float[] matrixValues = new float[9];
                matrix.getValues(matrixValues);
                matrixValues[Matrix.MSCALE_X] = scalar;
                matrixValues[Matrix.MSCALE_Y] = scalar;
                matrix.setValues(matrixValues);

                Bitmap newBitmap = Bitmap.createBitmap(mBitmap, 0, 0, w, h, matrix, true);

                int topoffset = (lh - newBitmap.getHeight()) / 2;
                int lopoffset = (lw - newBitmap.getWidth()) / 2;

                Canvas c = surfaceHolder.lockCanvas();
                c.drawBitmap(newBitmap, lopoffset, topoffset, p);
                surfaceHolder.unlockCanvasAndPost(c);

                mCropImageEdge.setBorderBound(new RectF(lopoffset, topoffset, lopoffset + newBitmap.getWidth(), topoffset + newBitmap.getHeight()));
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        }

    }


    private class CropImageEdge extends ImageView {

        private final static String TAG = "CropImageEdge";

        private static final float BORDER_LINE_WIDTH = 6f;
        private static final float BORDER_CORNER_LENGTH = 50f;
        private static final float TOUCH_FIELD = 50f;


        private static final int POS_TOP_LEFT = 0;
        private static final int POS_TOP_RIGHT = 1;
        private static final int POS_BOTTOM_LEFT = 2;
        private static final int POS_BOTTOM_RIGHT = 3;
        private static final int POS_TOP = 4;
        private static final int POS_BOTTOM = 5;
        private static final int POS_LEFT = 6;
        private static final int POS_RIGHT = 7;
        private static final int POS_CENTER = 8;

        private Paint mBorderPaint;// 裁剪区边框
        private Paint mBgPaint;

        private RectF mBorderBound;
        private RectF mBitmapRect;


        float mborderMinWidth = 200f;
        float mborderMinHeight = 200f;

        private int touchPos;
        private PointF downPos;
        private RectF downRect;

        private Context mContext = null;

        public String getCropImage(int currentOrientation) {

            // Extract the scale and translation values. Note, we currently do not handle any other transformations (e.g. skew).
//            final float scaleX = mCropImageBack.getScalar();
//            final float scaleY = mCropImageBack.getScalar();

            final float scaleX = mCropImageBack.getScaleX();
            final float scaleY = mCropImageBack.getScaleY();

            // Calculate the top-left corner of the crop window relative to the ~original~ bitmap size.
            RectF borderBound = mCropImageEdge.mBorderBound;
            final float cropX = (borderBound.left - mBitmapRect.left) / scaleX;
            final float cropY = (borderBound.top - mBitmapRect.top) / scaleY;

            // Calculate the crop window size relative to the ~original~ bitmap size.
            // Make sure the right and bottom edges are not outside the ImageView bounds (this is just to address rounding discrepancies).
            final float cropWidth = Math.min((borderBound.right - borderBound.left) / scaleX, mBitmap.getWidth() - cropX);
            final float cropHeight = Math.min((borderBound.bottom - borderBound.top) / scaleY, mBitmap.getHeight() - cropY);

            Bitmap cropBitmap;

            Matrix matrix = new Matrix();
            switch (currentOrientation) {
                case ORIENTATION_UP:
                    break;
                case ORIENTATION_LEFT:
                    matrix.setRotate(-90);
                    break;
                case ORIENTATION_DOWN:
                    matrix.setRotate(-180);
                    break;
                case ORIENTATION_RIGHT:
                    matrix.setRotate(-270);
                    break;
                default:
                    break;
            }

            cropBitmap = Bitmap.createBitmap(mBitmap,
                    (int) cropX,
                    (int) cropY,
                    (int) cropWidth,
                    (int) cropHeight,
                    matrix, true);

            FileOutputStream out;
            String mFileName = UUID.randomUUID().toString() + ".jpg";

            try {
                out = mContext.openFileOutput(mFileName, Context.MODE_PRIVATE);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                cropBitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos);
                out.write(baos.toByteArray());
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String fullname = mContext.getFilesDir().getPath() + "/" + mFileName;
            return fullname;
        }

        public void setBorderBound(RectF bitmapRect) {
            mBitmapRect = bitmapRect;
            float left = bitmapRect.left + (bitmapRect.right - bitmapRect.left) * 0.2f;
            float top = bitmapRect.top + (bitmapRect.bottom - bitmapRect.top) * 0.2f;
            float right = bitmapRect.right - (bitmapRect.right - bitmapRect.left) * 0.2f;
            float bottom = bitmapRect.bottom - (bitmapRect.bottom - bitmapRect.top) * 0.2f;
            mBorderBound = new RectF(left, top, right, bottom);
        }

        public CropImageEdge(Context context) {
            super(context);
            init(context, null);
        }

        public CropImageEdge(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(context, attrs);
        }

        public CropImageEdge(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init(context, attrs);
        }

        private void init(Context context, AttributeSet attrs) {
            mContext = context;

            mBorderPaint = new Paint();
            mBorderPaint.setStyle(Paint.Style.STROKE);
            mBorderPaint.setColor(Color.parseColor("#AAFFFFFF"));
            mBorderPaint.setStrokeWidth(BORDER_LINE_WIDTH);

            mBgPaint = new Paint();
            mBgPaint.setColor(Color.parseColor("#B0000000"));
            mBgPaint.setAlpha(150);
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            setImageBitmap(Bitmap.createBitmap(right - left, bottom - top, Bitmap.Config.ARGB_8888));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Log.d(TAG, "onDraw");
            if (canvas != null) {
                drawBorder(canvas);
                drawBackground(canvas);
            }
        }


        @Override
        public boolean onTouchEvent(MotionEvent event) {
            // super.onTouchEvent(event);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downPos = new PointF(event.getX(), event.getY());
                    downRect = new RectF(mBorderBound);
//                getParent().requestDisallowInterceptTouchEvent(true);
                    touchPos = detectTouchPosition(event.getX(), event.getY());

                    Log.d(TAG, "detectTouchPosition  " + touchPos);
                    break;
                case MotionEvent.ACTION_MOVE:
                    onActionMove(event.getX(), event.getY());
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }

            return true;
        }


        private void drawBorder(Canvas canvas) {
            canvas.drawRect(mBorderBound.left, mBorderBound.top, mBorderBound.right, mBorderBound.bottom, mBorderPaint);
        }

        private void drawBackground(Canvas canvas) {

        /*-
          -------------------------------------
          |                top                |
          -------------------------------------
          |      |                    |       |<——————————mBmpBound
          |      |                    |       |
          | left |                    | right |
          |      |                    |       |
          |      |                  <─┼───────┼────mBorderBound
          -------------------------------------
          |              bottom               |
          -------------------------------------
         */

            float delta = BORDER_LINE_WIDTH / 2;
            float left = mBorderBound.left - delta;
            float top = mBorderBound.top - delta;
            float right = mBorderBound.right + delta;
            float bottom = mBorderBound.bottom + delta;

            // -------------------------------------------------------------------------------移动到上下两端会多出来阴影
            canvas.drawRect(mBitmapRect.left, mBitmapRect.top, mBitmapRect.right, top, mBgPaint);
            canvas.drawRect(mBitmapRect.left, bottom, mBitmapRect.right, mBitmapRect.bottom, mBgPaint);
            canvas.drawRect(mBitmapRect.left, top, left, bottom, mBgPaint);
            canvas.drawRect(right, top, mBitmapRect.right, bottom, mBgPaint);
        }

        private int detectTouchPosition(float x, float y) {
            if (x > mBorderBound.left + TOUCH_FIELD && x < mBorderBound.right - TOUCH_FIELD
                    && y > mBorderBound.top + TOUCH_FIELD && y < mBorderBound.bottom - TOUCH_FIELD)
                return POS_CENTER;

            if (x > mBorderBound.left + BORDER_CORNER_LENGTH && x < mBorderBound.right - BORDER_CORNER_LENGTH) {
                if (y > mBorderBound.top - TOUCH_FIELD && y < mBorderBound.top + TOUCH_FIELD)
                    return POS_TOP;
                if (y > mBorderBound.bottom - TOUCH_FIELD && y < mBorderBound.bottom + TOUCH_FIELD)
                    return POS_BOTTOM;
            }

            if (y > mBorderBound.top + BORDER_CORNER_LENGTH && y < mBorderBound.bottom - BORDER_CORNER_LENGTH) {
                if (x > mBorderBound.left - TOUCH_FIELD && x < mBorderBound.left + TOUCH_FIELD)
                    return POS_LEFT;
                if (x > mBorderBound.right - TOUCH_FIELD && x < mBorderBound.right + TOUCH_FIELD)
                    return POS_RIGHT;
            }

            // 前面的逻辑已经排除掉了几种情况 所以后面的 ┏ ┓ ┗ ┛ 边角就按照所占区域的方形来判断就可以了
            if (x > mBorderBound.left - TOUCH_FIELD && x < mBorderBound.left + BORDER_CORNER_LENGTH) {
                if (y > mBorderBound.top - TOUCH_FIELD && y < mBorderBound.top + BORDER_CORNER_LENGTH)
                    return POS_TOP_LEFT;
                if (y > mBorderBound.bottom - BORDER_CORNER_LENGTH && y < mBorderBound.bottom + TOUCH_FIELD)
                    return POS_BOTTOM_LEFT;
            }

            if (x > mBorderBound.right - BORDER_CORNER_LENGTH && x < mBorderBound.right + TOUCH_FIELD) {
                if (y > mBorderBound.top - TOUCH_FIELD && y < mBorderBound.top + BORDER_CORNER_LENGTH)
                    return POS_TOP_RIGHT;
                if (y > mBorderBound.bottom - BORDER_CORNER_LENGTH && y < mBorderBound.bottom + TOUCH_FIELD)
                    return POS_BOTTOM_RIGHT;
            }

            return -1;
        }

        private void onActionMove(float x, float y) {
            float deltaX = x - downPos.x;
            float deltaY = y - downPos.y;
            // 这里先不考虑裁剪框放最大的情况
            switch (touchPos) {
                case POS_CENTER:
                    if (downRect.left + deltaX < mBitmapRect.left)
                        deltaX = mBitmapRect.left - downRect.left;
                    if (downRect.top + deltaY < mBitmapRect.top)
                        deltaY = mBitmapRect.top - downRect.top;
                    if (downRect.right + deltaX > mBitmapRect.right)
                        deltaX = mBitmapRect.right - downRect.right;
                    if (downRect.bottom + deltaY > mBitmapRect.bottom)
                        deltaY = mBitmapRect.bottom - downRect.bottom;

                    mBorderBound.left = downRect.left + deltaX;
                    mBorderBound.top = downRect.top + deltaY;
                    mBorderBound.right = downRect.right + deltaX;
                    mBorderBound.bottom = downRect.bottom + deltaY;
                    break;

                case POS_TOP:
                    resetTop(deltaY);
                    break;
                case POS_BOTTOM:
                    resetBottom(deltaY);
                    break;
                case POS_LEFT:
                    resetLeft(deltaX);
                    break;
                case POS_RIGHT:
                    resetRight(deltaX);
                    break;
                case POS_TOP_LEFT:
                    resetTop(deltaY);
                    resetLeft(deltaX);
                    break;
                case POS_TOP_RIGHT:
                    resetTop(deltaY);
                    resetRight(deltaX);
                    break;
                case POS_BOTTOM_LEFT:
                    resetBottom(deltaY);
                    resetLeft(deltaX);
                    break;
                case POS_BOTTOM_RIGHT:
                    resetBottom(deltaY);
                    resetRight(deltaX);
                    break;
                default:

                    break;
            }
            invalidate();
        }


        private void resetLeft(float delta) {
            mBorderBound.left = downRect.left + delta;
            if (mBorderBound.left < mBitmapRect.left)
                mBorderBound.left = mBitmapRect.left;
            if (mBorderBound.right - mBorderBound.left < mborderMinWidth)
                mBorderBound.left = mBorderBound.right - mborderMinWidth;
        }

        private void resetTop(float delta) {
            mBorderBound.top = downRect.top + delta;
            if (mBorderBound.top < mBitmapRect.top)
                mBorderBound.top = mBitmapRect.top;
            if (mBorderBound.bottom - mBorderBound.top < mborderMinHeight)
                mBorderBound.top = mBorderBound.bottom - mborderMinHeight;
        }

        private void resetRight(float delta) {
            mBorderBound.right = downRect.right + delta;
            if (mBorderBound.right > mBitmapRect.right)
                mBorderBound.right = mBitmapRect.right;
            if (mBorderBound.right - mBorderBound.left < mborderMinWidth)
                mBorderBound.right = mBorderBound.left + mborderMinWidth;
        }

        private void resetBottom(float delta) {
            mBorderBound.bottom = downRect.bottom + delta;
            if (mBorderBound.bottom > mBitmapRect.bottom)
                mBorderBound.bottom = mBitmapRect.bottom;
            if (mBorderBound.bottom - mBorderBound.top < mborderMinHeight)
                mBorderBound.bottom = mBorderBound.top + mborderMinHeight;
        }
    }

}



