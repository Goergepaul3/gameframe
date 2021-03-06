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
package com.savvasdalkitsis.gameframe.feature.workspace.element.tools.view

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

import com.savvasdalkitsis.gameframe.feature.workspace.element.tools.model.Tools

class ToolsAdapter : RecyclerView.Adapter<ToolsViewHolder>() {

    var toolSelectedListener: ToolSelectedListener? = null
        set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ToolsViewHolder(parent)

    override fun onBindViewHolder(holder: ToolsViewHolder, position: Int) = with(holder) {
        clearListeners()
        bind(Tools.values()[position])
        setOnClickListener { toolSelectedListener?.onToolSelected(Tools.values()[holder.adapterPosition]) }
    }

    override fun getItemCount() = Tools.values().size

}
