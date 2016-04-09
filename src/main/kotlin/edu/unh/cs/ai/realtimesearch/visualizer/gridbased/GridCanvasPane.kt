package edu.unh.cs.ai.realtimesearch.visualizer.gridbased

import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.layout.Pane
import javafx.scene.paint.Color

/**
 * A canvas which displays the grid specified in the provided {@link MapInfo}.  All cells except start cells are
 * displayed automatically.
 *
 * @author Mike Bogochow (mgp36@unh.edu)
 * @since April 8, 2016
 */
class GridCanvasPane(val mapInfo: MapInfo, val tileSize: Double) : Pane() {
    val canvas: Canvas = Canvas()
    val gridWidth = mapInfo.columnCount * tileSize
    val gridHeight = mapInfo.rowCount * tileSize

    init {
        children.add(canvas)
    }

    companion object {
        val ZERO = GridCanvasPane(MapInfo.ZERO, 0.0)
    }

    override fun layoutChildren() {
        val top = snappedTopInset()
        val right = snappedRightInset()
        val bottom = snappedBottomInset()
        val left = snappedLeftInset()
        val layoutWidth = width - left - right
        val layoutHeight = height - top - bottom
        canvas.layoutX = left
        canvas.layoutY = top

        if (layoutWidth != canvas.width || layoutHeight != canvas.height) {
            canvas.width = layoutWidth
            canvas.height = layoutHeight
            val g: GraphicsContext = canvas.graphicsContext2D

            // Add row lines
            g.stroke = Color.WHITE
            g.lineWidth = 0.1
            for (row in 1..mapInfo.rowCount) {
                val yPosition = row * tileSize
                g.strokeLine(0.0, yPosition, gridWidth, yPosition)
            }

            // Add column lines
            for (column in 1..mapInfo.columnCount) {
                val xPosition = column * tileSize
                g.strokeLine(xPosition, 0.0, xPosition, gridHeight)
            }

            // Add blocked cells
            g.fill = Color.BLACK
            for (cell in mapInfo.blockedCells) {
                g.fillRect(cell.x.toDouble() * tileSize, cell.y.toDouble() * tileSize, tileSize, tileSize)
            }

            // Add goal cells
            g.fill = Color.BLUE
            val radius = tileSize / 10.0
            val diameter = radius * 2
            for (cell in mapInfo.endCells) {
                val dirtyLocX = cell.x * tileSize + tileSize / 2.0 - radius
                val dirtyLocY = cell.y * tileSize + tileSize / 2.0 - radius

                g.fillOval(dirtyLocX, dirtyLocY, diameter, diameter)
            }
        }
    }
}