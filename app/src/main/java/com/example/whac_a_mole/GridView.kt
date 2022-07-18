package com.example.whac_a_mole

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import kotlin.math.max
import kotlin.math.min
import kotlin.properties.Delegates

class GridView(
    context: Context,
    attributeSet: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : View(context, attributeSet, defStyleAttr, defStyleRes) {

    private var gridColor: Int by Delegates.notNull()
    private var gridRowCount: Int by Delegates.notNull()
    private var gridColumnCount: Int by Delegates.notNull()

    private var cellSize: Float = 0F
    private var cellPadding: Float = 0F

    private val gridRect = RectF(0F, 0F, 0F, 0F)

    private lateinit var gridPaint: Paint

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attributeSet,
        defStyleAttr,
        0
    )

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context) : this(context, null)

    init {
        if (attributeSet != null) initAttributes(attributeSet, defStyleAttr, defStyleRes)
        else initDefaultAttrs()

        initPaints()
    }

    private fun initAttributes(attributeSet: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val typedArray = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.GridView,
            defStyleAttr,
            defStyleRes
        )

        gridColor = typedArray.getColor(R.attr.gridColor, DEFAULT_GRID_COLOR)
        gridRowCount = typedArray.getInt(R.attr.gridRowCount, DEFAULT_GRID_ROW_COUNT)
        gridColumnCount = typedArray.getInt(R.attr.gridColumnCount, DEFAULT_GRID_COLUMN_COUNT)

        typedArray.recycle()
    }

    private fun initDefaultAttrs() {
        gridColor = DEFAULT_GRID_COLOR
        gridRowCount = DEFAULT_GRID_ROW_COUNT
        gridColumnCount = DEFAULT_GRID_COLUMN_COUNT
    }

    private fun initPaints() {
        gridPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        gridPaint.color = gridColor
        gridPaint.style = Paint.Style.STROKE
        gridPaint.strokeWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            3F,
            resources.displayMetrics
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val minHeight = suggestedMinimumHeight + paddingTop + paddingBottom

        val desiredCellSizeInPixels = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 50F, resources.displayMetrics
        ).toInt()

        val desiredWidth =
            max(minWidth, gridColumnCount * desiredCellSizeInPixels + paddingLeft + paddingRight)
        val desiredHeight =
            max(minHeight, gridRowCount * desiredCellSizeInPixels + paddingTop + paddingBottom)

        setMeasuredDimension(
            resolveSize(desiredWidth, widthMeasureSpec),
            resolveSize(desiredHeight, heightMeasureSpec)
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val safeWidth = width - paddingLeft - paddingRight
        val safeHeight = height - paddingTop - paddingBottom

        val cellWidth = safeWidth / gridColumnCount.toFloat()
        val cellHeight = safeHeight / gridRowCount.toFloat()

        cellSize = min(cellWidth, cellHeight)
        cellPadding = cellSize * 0.2F

        val gridWidth = cellSize * gridColumnCount
        val gridHeight = cellSize * gridRowCount

        gridRect.left = paddingLeft + (safeWidth - gridWidth) / 2
        gridRect.top = paddingTop + (safeHeight - gridHeight) / 2
        gridRect.right = gridRect.left + gridWidth
        gridRect.bottom = gridRect.top + gridHeight
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (canvas == null) return
        if (cellSize == 0F) return
        if (gridRect.width() <= 0) return
        if (gridRect.height() <= 0) return

        drawGrid(canvas)
    }

    private fun drawGrid(canvas: Canvas) {
        val xStart = gridRect.left
        val xEnd = gridRect.right
        for (i in 0..gridRowCount) {
            val y = gridRect.top + cellSize * i
            canvas.drawLine(xStart, y, xEnd, y, gridPaint)
        }

        val yStart = gridRect.top
        val yEnd = gridRect.bottom
        for (i in 0..gridColumnCount) {
            val x = gridRect.left + cellSize * i
            canvas.drawLine(x, yStart, x, yEnd, gridPaint)
        }
    }

    private companion object {
        const val DEFAULT_GRID_COLOR = Color.BLACK
        const val DEFAULT_GRID_ROW_COUNT = 3
        const val DEFAULT_GRID_COLUMN_COUNT = 3
    }
}