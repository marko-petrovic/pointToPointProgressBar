package com.dualquo.te.pointtopointprogressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

public class PointToPointProgressBarView extends View {
    /**
     * Represents how many points on the bar we can set. <br>
     * <br>
     * If you need more points, simply add them as enums both in here and in attrs
     */
    public enum Point {
        ONE(1),
        TWO(2),
        THREE(3),
        FOUR(4);

        private int pointValue;

        Point(int pointValue) {
            this.pointValue = pointValue;
        }

        public int getPoint() {
            return pointValue;
        }
    }

    private final static String STATE_CENTER_OF_THE_FIRST_POINT = "STATE_CENTER_OF_THE_FIRST_POINT";
    private final static String STATE_CENTER_OF_THE_LAST_POINT = "STATE_CENTER_OF_THE_LAST_POINT";
    private final static String STATE_KEY_VALUE = "STATE_KEY_VALUE";

    private final static float DEFAULT_POINT_SIZE = 12f;
    private final static float DEFAULT_BAR_LINE_THICKNESS = 4f;
    private final static float DEFAULT_SPACING_OFFSET = 4f;

    private float pointRadius;
    private float pointSize;
    private float barLineThickness;
    private float pointToPointWidth;
    private float pointToPointHeight;
    private float nextPointToPointWidth;
    private float centerOfTheFirstPoint;
    private float centerOfTheLastPoint;
    private float spacingOffset;

    private int maximumNumberOfPoints;
    private int currentPointNumber;
    private int backgroundColor;
    private int foregroundColor;

    private Paint backgroundPaint;
    private Paint foregroundPaint;

    public PointToPointProgressBarView(Context context) {
        this(context, null, 0);
    }

    public PointToPointProgressBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PointToPointProgressBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initializePointToPointProgressBar(context, attrs, defStyle);
    }

    private void initializePointToPointProgressBar(Context context, AttributeSet attributeSet, int defStyle) {
        initDefaultValues(context);

        barLineThickness = convertDpToPixel(barLineThickness);
        spacingOffset = convertDpToPixel(spacingOffset);

        if (attributeSet != null) {
            final TypedArray styledAttributes = context
                    .obtainStyledAttributes(
                            attributeSet,
                            R.styleable.PointToPointProgressBar,
                            defStyle,
                            0
                    );

            backgroundColor = styledAttributes.getColor(
                    R.styleable.PointToPointProgressBar_pointToPointProgressBar_backgroundColor,
                    backgroundColor
            );

            foregroundColor = styledAttributes.getColor(
                    R.styleable.PointToPointProgressBar_pointToPointProgressBar_foregroundColor,
                    foregroundColor
            );

            currentPointNumber = styledAttributes.getInteger(
                    R.styleable.PointToPointProgressBar_pointToPointProgressBar_currentPointNumber,
                    currentPointNumber
            );

            maximumNumberOfPoints = styledAttributes.getInteger(
                    R.styleable.PointToPointProgressBar_pointToPointProgressBar_maximumNumberOfPoints,
                    maximumNumberOfPoints
            );

            pointSize = styledAttributes.getDimension(
                    R.styleable.PointToPointProgressBar_pointToPointProgressBar_pointSize,
                    pointSize
            );

            barLineThickness = styledAttributes.getDimension(
                    R.styleable.PointToPointProgressBar_pointToPointProgressBar_barLineThickness,
                    barLineThickness
            );

            pointRadius = pointSize / 2;

            validateBarLineThickness(barLineThickness);
            validatePointNumber(currentPointNumber);

            styledAttributes.recycle();
        }

        backgroundPaint = initPaint(barLineThickness, backgroundColor);
        foregroundPaint = initPaint(barLineThickness, foregroundColor);
    }

    private void initDefaultValues(Context context) {
        backgroundColor = ContextCompat.getColor(context, R.color.colorPrimary);
        foregroundColor = ContextCompat.getColor(context, R.color.colorAccent);

        pointSize = DEFAULT_POINT_SIZE;
        barLineThickness = DEFAULT_BAR_LINE_THICKNESS;
        spacingOffset = DEFAULT_SPACING_OFFSET;

        maximumNumberOfPoints = Point.FOUR.getPoint();
        currentPointNumber = Point.ONE.getPoint();
    }

    private Paint initPaint(float strokeWidth, int color) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(color);

        return paint;
    }

    /**
     * Sets current point on the bar line, then invalidates the view.
     *
     * @param currentPoint {@link Point} that is being set.
     */
    public void setCurrentPointNumber(Point currentPoint) {
        validatePointNumber(currentPoint.getPoint());
        this.currentPointNumber = currentPoint.getPoint();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPointToPointProgressBar(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = (int) (2 * pointRadius) + (int) (spacingOffset);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        setMeasuredDimension(width, height);

        pointToPointHeight = (int) (2 * pointRadius) + (int) (spacingOffset);
    }

    @Override
    protected void onSizeChanged(int width, int height, int previousWidth, int previousHeight) {
        super.onSizeChanged(width, height, previousWidth, previousHeight);

        int widthOfTheView = getWidth();
        pointToPointWidth = widthOfTheView / (maximumNumberOfPoints - 1);
        nextPointToPointWidth = pointToPointWidth / 2;
    }

    /**
     * Draws the whole bar with all the points and lines. Triggered in onDraw method. <br>
     * <br>
     * At first, bar lines with background color are drawn. Then points as circles, in order that
     * first we draw all points with background color, and then those that are to the left side of
     * the selected (current) point, including it. Then there come bar lines that are to the left
     * of the current point, painted with foreground color.
     *
     * @param canvas {@link Canvas} to draw on.
     */
    private void drawPointToPointProgressBar(Canvas canvas) {
        drawBarLines(canvas, backgroundPaint, maximumNumberOfPoints);
        drawCirclesForPoints(canvas, backgroundPaint, maximumNumberOfPoints);
        drawCirclesForPoints(canvas, foregroundPaint, currentPointNumber);
        drawBarLines(canvas, foregroundPaint, currentPointNumber);
    }

    /**
     * Draws bar lines.
     *
     * @param canvas              {@link Canvas} to draw on.
     * @param paintToDrawWith     {@link Paint} to draw with.
     * @param numberOfLinesToDraw how many lines to draw.
     */
    private void drawBarLines(Canvas canvas, Paint paintToDrawWith, int numberOfLinesToDraw) {
        float drawingSegmentStartPosition;
        float drawingSegmentStopPosition;
        float segmentStartPosition;
        float segmentStopPosition;

        segmentStartPosition = 0f;

        for (int i = 0; i < numberOfLinesToDraw - 1; i++) {

            nextPointToPointWidth += pointToPointWidth;
            segmentStopPosition = nextPointToPointWidth - (pointToPointWidth);

            drawingSegmentStartPosition = segmentStartPosition + pointRadius;
            drawingSegmentStopPosition = segmentStopPosition - pointRadius;

            canvas.drawLine(
                    drawingSegmentStartPosition,
                    pointToPointHeight / 2,
                    drawingSegmentStopPosition,
                    pointToPointHeight / 2,
                    paintToDrawWith
            );

            segmentStartPosition = segmentStopPosition;
        }

        nextPointToPointWidth = pointToPointWidth;
    }

    /**
     * Draws circles for points.
     *
     * @param canvas                {@link Canvas} to draw on.
     * @param paint                 {@link Paint} to draw with.
     * @param numberOfCirclesToDraw how many circles to draw.
     */
    private void drawCirclesForPoints(Canvas canvas, Paint paint, int numberOfCirclesToDraw) {
        float edgeOffset;

        for (int i = 0; i < numberOfCirclesToDraw; i++) {
            if (i == 0) {
                edgeOffset = pointRadius;
            } else if (i == maximumNumberOfPoints - 1) {
                edgeOffset = -1 * pointRadius;
            } else {
                edgeOffset = 0.0f;
            }

            canvas.drawCircle(
                    nextPointToPointWidth - pointToPointWidth + edgeOffset,
                    pointToPointHeight / 2,
                    pointRadius,
                    paint
            );

            nextPointToPointWidth += pointToPointWidth;
        }

        nextPointToPointWidth = pointToPointWidth;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();

        bundle.putParcelable(STATE_KEY_VALUE, super.onSaveInstanceState());
        bundle.putFloat(STATE_CENTER_OF_THE_LAST_POINT, this.centerOfTheLastPoint);
        bundle.putFloat(STATE_CENTER_OF_THE_FIRST_POINT, this.centerOfTheFirstPoint);

        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;

            this.centerOfTheLastPoint = bundle.getFloat(STATE_CENTER_OF_THE_LAST_POINT);
            this.centerOfTheFirstPoint = bundle.getFloat(STATE_CENTER_OF_THE_FIRST_POINT);

            state = bundle.getParcelable(STATE_KEY_VALUE);
        }
        super.onRestoreInstanceState(state);
    }

    private void validatePointNumber(int pointNumber) {
        if (pointNumber > maximumNumberOfPoints) {
            throw new IllegalStateException(
                    "Point number value " +
                            pointNumber +
                            " cannot be greater than " +
                            maximumNumberOfPoints
            );
        }
    }

    private void validateBarLineThickness(float lineThickness) {
        if (lineThickness > pointRadius) {
            barLineThickness = pointRadius;
        }
    }

    private float convertDpToPixel(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return dp * scale;
    }
}