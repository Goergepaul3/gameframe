/**
 * Copyright 2017 Savvas Dalkitsis
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * 'Game Frame' is a registered trademark of LEDSEQ
 */
package com.savvasdalkitsis.gameframe.feature.workspace.element.tools.model

import com.savvasdalkitsis.gameframe.feature.workspace.element.grid.model.Grid

class FillOvalTool : OvalTool() {

    private val fillTool = FillTool()

    override fun drawOnScratch(scratch: Grid, startColumn: Int, startRow: Int, column: Int, row: Int, color: Int) {
        super.drawOnScratch(scratch, startColumn, startRow, column, row, color)

        val rx = Math.abs(startColumn - column) / 2
        val ry = Math.abs(startRow - row) / 2
        val cx = Math.min(startColumn, column) + rx
        val cy = Math.min(startRow, row) + ry
        fillTool.fill(scratch, cx, cy, color, scratch.getColor(column, row))
    }
}
