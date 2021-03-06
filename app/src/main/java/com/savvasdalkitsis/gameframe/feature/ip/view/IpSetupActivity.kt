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
package com.savvasdalkitsis.gameframe.feature.ip.view

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewPropertyAnimator
import butterknife.OnClick
import com.savvasdalkitsis.gameframe.R
import com.savvasdalkitsis.gameframe.infra.android.BaseActivity
import com.savvasdalkitsis.gameframe.infra.android.Snackbars
import com.savvasdalkitsis.gameframe.injector.presenter.PresenterInjector.ipSetupPresenter
import com.savvasdalkitsis.gameframe.feature.ip.model.IpAddress
import kotlinx.android.synthetic.main.activity_ip_setup.*
import kotlinx.android.synthetic.main.view_ip_text_view.*

class IpSetupActivity : BaseActivity(), IpSetupView {

    private val presenter = ipSetupPresenter()

    override val layoutId: Int
        get() = R.layout.activity_ip_setup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        view_ip_text_view.setOnIpChangedListener(object : IpChangedListener {
            override fun onIpChangedListener(ipAddress: IpAddress) {
                if (view_ip_text_view.isEnabled) {
                    showFab(view_setup, ipAddress.isValid())
                }
            }
        })
        presenter.bindView(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return false
    }

    override fun onStop() {
        super.onStop()
        presenter.unbind()
    }

    override fun displayIpAddress(ipAddress: IpAddress) {
        view_ip_text_view.bind(ipAddress)
    }

    @OnClick(R.id.view_setup)
    fun setup() {
        presenter.setup(view_ip_text_view.ipAddress)
    }

    @OnClick(R.id.view_cancel_discover)
    fun cancelDiscover() {
        presenter.cancelDiscover()
    }

    @OnClick(R.id.view_discover)
    fun discover() {
        presenter.discoverIp()
    }

    override fun errorDiscoveringIpAddress(throwable: Throwable) {
        Log.e(IpSetupActivity::class.java.name, "Could not load ip address", throwable)
        Snackbars.error(view_setup_content, R.string.operation_failed)
    }

    override fun addressSaved(ipAddress: IpAddress) {
        finish()
    }

    override fun displayDiscovering() {
        view_setup_title.setText(R.string.trying_to_find_game_frame)
        scale(view_discover, 0f)
        alpha(view_discover_title, 0f)
        view_ip_text_view.isEnabled = false
        animate(view_ip_text_view)
                .translationY(resources.getDimensionPixelSize(R.dimen.ip_view_offset).toFloat())
                .start()

        showFab(view_setup, false)
        scale(view_fab_progress, 1f)
        view_setup_fab_container.setActiveFab(1)
        showFab(view_cancel_discover, true)
    }

    override fun ipAddressDiscovered(ipAddress: IpAddress) {
        Snackbars.success(view_setup_content, R.string.game_frame_ip_found)
    }

    override fun tryingAddress(ipAddress: IpAddress) {
        view_ip_text_view.bind(ipAddress)
    }

    override fun displayIdleView() {
        view_setup_title.setText(R.string.enter_ip_address)
        scale(view_discover, 1f)
        alpha(view_discover_title, 1f)
        view_ip_text_view.isEnabled = true
        animate(view_ip_text_view)
                .translationY(0f)
                .start()
        view_setup_fab_container.setActiveFab(0)
        showFab(view_cancel_discover, false)
        scale(view_fab_progress, 0f)
        showFab(view_setup, true)
    }

    private fun scale(view: View, value: Float) {
        animate(view).scaleX(value).scaleY(value).start()
    }

    private fun alpha(view: View, value: Float) {
        animate(view).alpha(value).start()
    }

    private fun animate(view: View): ViewPropertyAnimator {
        view.clearAnimation()
        return view.animate().setDuration(200)
    }

    private fun showFab(fab: View, show: Boolean) {
        val scale = (if (show) 1 else 0).toFloat()
        val rotation = (if (show) 0 else 45).toFloat()
        fab.isEnabled = show
        fab.isClickable = show
        animate(fab)
                .rotation(rotation)
                .scaleX(scale)
                .scaleY(scale)
                .start()
    }
}
