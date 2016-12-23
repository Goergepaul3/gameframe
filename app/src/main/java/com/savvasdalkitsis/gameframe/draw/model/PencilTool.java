package com.savvasdalkitsis.gameframe.draw.model;

import com.savvasdalkitsis.gameframe.grid.model.ColorGrid;

public class PencilTool implements DrawingTool {

    @Override
    public void drawOn(ColorGrid colorGrid, int column, int row, int color) {
        colorGrid.setColor(color, column, row);
    }

}