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
package com.savvasdalkitsis.gameframe.feature.workspace.serialization

import com.google.gson.*
import com.savvasdalkitsis.gameframe.feature.composition.model.AvailableBlendMode
import com.savvasdalkitsis.gameframe.feature.composition.model.BlendMode
import java.lang.reflect.Type

class BlendModeAdapter: JsonSerializer<BlendMode>, JsonDeserializer<BlendMode> {

    override fun serialize(src: BlendMode, typeOfSrc: Type?, context: JsonSerializationContext?) =
            JsonPrimitive(AvailableBlendMode.from(src).name)

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?) =
            AvailableBlendMode.fromName(json.asString)
}