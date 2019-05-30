package com.netatmo.ylu.myapplication;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class HeaderView extends LinearLayout {
    ImageView imageView;
    private ScaleGestureDetector scaleGestureDetector;
    private ScaleGestureDetector.OnScaleGestureListener listener;
    private float factor = 1.0f;
    boolean isZooming;
    private float lastX;
    private float lastY;

    public HeaderView(Context context) {
        super(context);
        init(context);
    }

    public HeaderView(Context context,  @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.view_header, this);
        imageView = findViewById(R.id.image);
        listener = new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {

                factor *= detector.getScaleFactor();
                if(factor < 1.0f){
                    return false;
                }
                if(factor < imageView.getScaleX()){
                    float newPivotX = imageView.getPivotX();
                    float newPivotY = imageView.getPivotY();
                    float newTransX = imageView.getTranslationX();
                    float newTransY = imageView.getTranslationY();
                    if(imageView.getPivotX() * (1 - imageView.getScaleX()) + imageView.getTranslationX()>=0){
                        newPivotX = 0;
                        newTransX = 0;
                    } else if((imageView.getWidth() - imageView.getPivotX()) * (imageView.getScaleX()-1) + imageView.getTranslationX() <= 0){
                        newPivotX = imageView.getWidth();
                        newTransX = 0;
                    }
                    if(imageView.getPivotY() * (1-imageView.getScaleY()) + imageView.getTranslationY() >= 0){
                        newPivotY = 0;
                        newTransY = 0;
                    } else if((imageView.getHeight() - imageView.getPivotY()) * (imageView.getScaleY()-1) + imageView.getTranslationY() <= 0){
                        newPivotY = getHeight();
                        newTransY = 0;
                    }
                    imageView.setTranslationX(newTransX);
                    imageView.setTranslationY(newTransY);
                    imageView.setPivotX(newPivotX);
                    imageView.setPivotY(newPivotY);
                }
                imageView.setScaleX(factor);
                imageView.setScaleY(factor);

                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                isZooming = true;

                float newX = detector.getFocusX();
                float newY = detector.getFocusY();
                imageView.setTranslationX(imageView.getTranslationX() + (imageView.getPivotX() - newX) * (1 - imageView.getScaleX()));
                imageView.setTranslationY(imageView.getTranslationY() + (imageView.getPivotY() - newY) * (1 - imageView.getScaleY()));
                imageView.setPivotX(newX);
                imageView.setPivotY(newY);
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                isZooming = false;
            }
        };
        scaleGestureDetector = new ScaleGestureDetector(context, listener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        if(!isZooming && imageView.getScaleX() > 1){
            switch (event.getActionMasked()){
                case MotionEvent.ACTION_DOWN:
                    lastX = event.getX();
                    lastY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float limitLeft = imageView.getPivotX() * (imageView.getScaleX()  -1) - imageView.getTranslationX();
                    float limitRight = (imageView.getWidth() - imageView.getPivotX()) * (imageView.getScaleX()  -1)  + imageView.getTranslationX();
                    float limitTop = imageView.getPivotY() * ( imageView.getScaleY() - 1) - imageView.getTranslationY();
                    float limitBottom = (imageView.getHeight() - imageView.getPivotY()) * (imageView.getScaleY() - 1) + imageView.getTranslationY();
                    float deltaX = event.getX() - lastX;
                    float deltaY = event.getY() - lastY;
                    deltaX = Math.max( - limitRight, Math.min(limitLeft , deltaX));
                    deltaY = Math.max( - limitBottom, Math.min(limitTop, deltaY));


                    imageView.setTranslationX(deltaX + imageView.getTranslationX());
                    imageView.setTranslationY(deltaY + imageView.getTranslationY());
                    lastX = event.getX();
                    lastY = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
        }
        return true;
    }

    public void reset(){
        imageView.setTranslationY(0f);
        imageView.setTranslationX(0f);
        imageView.setScaleY(1f);
        imageView.setScaleX(1f);
        imageView.setPivotX((float) imageView.getWidth()/2);
        imageView.setPivotY((float) imageView.getHeight()/2);
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        float height = (float) width / 16 *9;
        heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
