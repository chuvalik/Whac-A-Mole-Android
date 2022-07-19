package com.example.whac_a_mole

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import com.example.whac_a_mole.presentation.game_field.model.Cell
import kotlin.math.max
import kotlin.math.min
import kotlin.properties.Delegates

typealias OnCellActionListener = (row: Int, column: Int, field: GameField) -> Unit

class GameFieldView(
    context: Context,
    attributeSet: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : View(context, attributeSet, defStyleAttr, defStyleRes) {

    var gameField: GameField? = null
        set(value) {
            field?.listener?.remove(listener) // when the new field is set, remove old from list
            field = value
            value?.listener?.add(listener)
            updateViewSizes() // update size ->
            requestLayout() // calc new layout size ->
            invalidate() // redraw ->
        }

    private var moleColor: Int by Delegates.notNull()
    private var holeColor: Int by Delegates.notNull()
    private var gridColor: Int by Delegates.notNull()

    private lateinit var molePaint: Paint
    private lateinit var holePaint: Paint
    private lateinit var gridPaint: Paint

    private var cellSize: Float = 0f
    private var cellPadding: Float = 0f

    private val fieldRect = RectF(0F, 0F, 0F, 0F)
    private val cellRect = RectF(0F, 0F, 0F, 0F)

    private val listener: OnFieldChangedListener = { invalidate() }

    var actionListener: OnCellActionListener? = null

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attributeSet,
        defStyleAttr,
        R.style.DefaultGameFieldStyle
    )

    constructor(context: Context, attributeSet: AttributeSet?) : this(
        context,
        attributeSet,
        R.attr.gameField
    )

    constructor(context: Context) : this(context, null)

    init {
        if (attributeSet != null) initAttributes(attributeSet, defStyleAttr, defStyleRes)
        else initDefaultColors()
        initPaints()
    }

    private fun initAttributes(attributeSet: AttributeSet, defStyleAttr: Int, defStyleRes: Int) {
        val typedArray = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.GameFieldView,
            defStyleAttr,
            defStyleRes
        )

        moleColor = typedArray.getColor(R.styleable.GameFieldView_moleColor, DEFAULT_MOLE_COLOR)
        holeColor = typedArray.getColor(R.styleable.GameFieldView_holeColor, DEFAULT_HOLE_COLOR)
        gridColor = typedArray.getColor(R.styleable.GameFieldView_gridColor, DEFAULT_GRID_COLOR)

        typedArray.recycle()
    }

    private fun initDefaultColors() {
        moleColor = DEFAULT_MOLE_COLOR
        holeColor = DEFAULT_HOLE_COLOR
        gridColor = DEFAULT_GRID_COLOR
    }

    private fun initPaints() {
        molePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        molePaint.color = moleColor
        molePaint.style = Paint.Style.FILL

        holePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        holePaint.color = holeColor
        holePaint.style = Paint.Style.FILL

        gridPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        gridPaint.color = gridColor
        gridPaint.style = Paint.Style.STROKE
        gridPaint.strokeWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            3F,
            resources.displayMetrics
        )
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        gameField?.listener?.add(listener)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        gameField?.listener?.remove(listener)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val minHeight = suggestedMinimumHeight + paddingTop + paddingBottom

        val desiredCellSizeInPixels = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 100F, resources.displayMetrics
        ).toInt()

        val rows = gameField?.rows ?: 0
        val columns = gameField?.columns ?: 0

        val desiredWidth =
            max(minWidth, columns * desiredCellSizeInPixels + paddingLeft + paddingRight)
        val desiredHeight =
            max(minHeight, rows * desiredCellSizeInPixels + paddingTop + paddingBottom)

        setMeasuredDimension(
            resolveSize(desiredWidth, widthMeasureSpec),
            resolveSize(desiredHeight, heightMeasureSpec)
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateViewSizes()
    }

    private fun updateViewSizes() {
        val field = this.gameField ?: return

        val safeWidth = width - paddingLeft - paddingRight
        val safeHeight = height - paddingTop - paddingBottom

        val cellWidth = safeWidth / field.columns.toFloat()
        val cellHeight = safeHeight / field.rows.toFloat()

        cellSize = min(cellWidth, cellHeight) // because field cell is square
        cellPadding = cellWidth * 0.2f

        val fieldWidth = cellSize * field.columns
        val fieldHeight = cellSize * field.rows

        fieldRect.left = paddingLeft + (safeWidth - fieldWidth) / 2
        fieldRect.top = paddingTop + (safeHeight - fieldHeight) / 2
        fieldRect.right = fieldRect.left + fieldWidth
        fieldRect.bottom = fieldRect.top + fieldHeight
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (canvas == null) return
        if (gameField == null) return
        if (cellSize == 0F) return
        if (fieldRect.width() <= 0) return
        if (fieldRect.height() <= 0) return


        drawHole(canvas)
        drawGrid(canvas)
        drawMole(canvas)
    }

    private fun drawGrid(canvas: Canvas) {
        val field = this.gameField ?: return

        val xStart = fieldRect.left
        val xEnd = fieldRect.right

        for (i in 0..field.rows) {
            val y = fieldRect.top + cellSize * i
            canvas.drawLine(xStart, y, xEnd, y, gridPaint)
        }

        val yStart = fieldRect.top

        val yEnd = fieldRect.bottom

        for (i in 0..field.rows) {
            val x = fieldRect.left + cellSize * i
            canvas.drawLine(x, yStart, x, yEnd, gridPaint)
        }
    }

    private fun drawHole(canvas: Canvas) {
        val field = this.gameField ?: return
        for (row in 0 until field.rows) {
            for (column in 0 until field.columns) {
                val cell = getCellRect(row, column)
                canvas.drawCircle(
                    cell.centerX(),
                    cell.centerY(),
                    cell.width() / 2,
                    holePaint
                )
            }
        }
    }

    private fun drawMole(canvas: Canvas) {
        val field = this.gameField ?: return
        for (row in 0 until field.rows) {
            for (column in 0 until field.columns) {
                when (field.getCell(row, column)) {
                    Cell.MOLE -> {
                        val cell = getCellRect(row, column)
                        canvas.drawCircle(
                            cell.centerX(),
                            cell.centerY(),
                            cell.width() / 4,
                            molePaint
                        )
                    }
                    else -> Unit
                }

            }
        }
    }

    private fun getCellRect(row: Int, column: Int): RectF {
        cellRect.left = fieldRect.left + column * cellSize + cellPadding
        cellRect.top = fieldRect.top + row * cellSize + cellPadding
        cellRect.right = cellRect.left + cellSize - cellPadding * 2
        cellRect.bottom = cellRect.top + cellSize - cellPadding * 2

        return cellRect
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val field = this.gameField ?: return false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                return true
            }
            MotionEvent.ACTION_UP -> {
                val row = getRow(event)
                val column = getColumn(event)
                if (row >= 0 && column >= 0 && row < field.rows && column < field.columns) {
                    actionListener?.invoke(row, column, field)
                    return true
                }
                return false
            }
        }

        return false
    }

    private fun getRow(event: MotionEvent): Int {
        return ((event.y - fieldRect.top) / cellSize).toInt()
    }

    private fun getColumn(event: MotionEvent): Int {
        return ((event.x - fieldRect.left) / cellSize).toInt()
    }

    private companion object {
        const val DEFAULT_MOLE_COLOR = Color.WHITE
        const val DEFAULT_HOLE_COLOR = Color.BLACK
        const val DEFAULT_GRID_COLOR = Color.GREEN
    }
}