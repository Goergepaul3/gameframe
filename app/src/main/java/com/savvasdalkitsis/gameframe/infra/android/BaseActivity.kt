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
package com.savvasdalkitsis.gameframe.infra.android

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import butterknife.ButterKnife
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.savvasdalkitsis.gameframe.BuildConfig

import com.savvasdalkitsis.gameframe.R
import io.fabric.sdk.android.Fabric

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

abstract class BaseActivity : AppCompatActivity() {

    abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, Crashlytics(), Answers())
        }
        setContentView(layoutId)
        ButterKnife.bind(this)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        title = SpannableString(title).apply {
            setSpan(AbsoluteSizeSpan(resources.getDimensionPixelSize(R.dimen.toolbar_text_size)),
                    0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }
}
