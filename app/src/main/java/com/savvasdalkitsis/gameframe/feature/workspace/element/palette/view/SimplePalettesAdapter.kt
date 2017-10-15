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
package com.savvasdalkitsis.gameframe.feature.workspace.element.palette.view

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

import com.savvasdalkitsis.gameframe.feature.workspace.element.palette.model.Palette


internal class SimplePalettesAdapter(private val palettes: Array<Palette>) : RecyclerView.Adapter<PaletteViewHolder>() {

    private var onAddNewPaletteSelectedListener: AddNewPaletteSelectedListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            PaletteViewHolder(parent, false)

    override fun onBindViewHolder(holder: PaletteViewHolder, position: Int) = with(holder) {
        clearListeners()
        bind(palettes[position])
        setOnClickListener { onAddNewPaletteSelectedListener?.onAddNewPalletSelected(palettes[position]) }
    }

    override fun getItemCount() = palettes.size

    fun setOnAddNewPaletteSelectedListener(onAddNewPaletteSelectedListener: AddNewPaletteSelectedListener) {
        this.onAddNewPaletteSelectedListener = onAddNewPaletteSelectedListener
    }

}
