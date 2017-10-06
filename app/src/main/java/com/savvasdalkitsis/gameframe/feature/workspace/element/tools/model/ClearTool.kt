package com.savvasdalkitsis.gameframe.feature.workspace.element.tools.model

import android.graphics.Color
import com.savvasdalkitsis.gameframe.feature.workspace.element.grid.model.DEFAULT_BACKGROUND_COLOR
import com.savvasdalkitsis.gameframe.feature.workspace.element.layer.model.Layer

class ClearTool : AbstractDrawingTool() {

    override fun drawOn(layer: Layer, startColumn: Int, startRow: Int, column: Int, row: Int, color: Int) {
        layer.colorGrid.fill(when {
            layer.isBackground -> DEFAULT_BACKGROUND_COLOR
            else -> Color.TRANSPARENT
        })
    }
}