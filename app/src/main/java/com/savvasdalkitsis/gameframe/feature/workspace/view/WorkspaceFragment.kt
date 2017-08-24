package com.savvasdalkitsis.gameframe.feature.workspace.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.design.internal.NavigationMenu
import android.support.v4.widget.DrawerLayout
import android.util.Log
import android.view.*
import butterknife.OnClick
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.ColorChooserDialog
import com.savvasdalkitsis.gameframe.R
import com.savvasdalkitsis.gameframe.feature.history.usecase.HistoryUseCase
import com.savvasdalkitsis.gameframe.feature.home.view.HomeActivity
import com.savvasdalkitsis.gameframe.feature.workspace.element.grid.model.Grid
import com.savvasdalkitsis.gameframe.feature.workspace.element.grid.view.GridTouchedListener
import com.savvasdalkitsis.gameframe.feature.workspace.element.palette.model.Palette
import com.savvasdalkitsis.gameframe.feature.workspace.element.palette.view.AddNewPaletteSelectedListener
import com.savvasdalkitsis.gameframe.feature.workspace.element.palette.view.AddPaletteView
import com.savvasdalkitsis.gameframe.feature.workspace.element.palette.view.PaletteView
import com.savvasdalkitsis.gameframe.feature.workspace.element.swatch.view.SwatchSelectedListener
import com.savvasdalkitsis.gameframe.feature.workspace.element.swatch.view.SwatchView
import com.savvasdalkitsis.gameframe.feature.workspace.element.tools.model.Tools
import com.savvasdalkitsis.gameframe.feature.workspace.element.tools.view.ToolSelectedListener
import com.savvasdalkitsis.gameframe.feature.workspace.model.WorkspaceModel
import com.savvasdalkitsis.gameframe.feature.workspace.presenter.WorkspacePresenter
import com.savvasdalkitsis.gameframe.infra.view.BaseFragment
import com.savvasdalkitsis.gameframe.infra.view.FragmentSelectedListener
import com.savvasdalkitsis.gameframe.infra.view.Snackbars
import com.savvasdalkitsis.gameframe.injector.presenter.PresenterInjector.drawPresenter
import com.savvasdalkitsis.gameframe.injector.usecase.UseCaseInjector.blendUseCase
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import io.github.yavski.fabspeeddial.CustomFabSpeedDial
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_workspace.*

class WorkspaceFragment : BaseFragment(), FragmentSelectedListener, SwatchSelectedListener, GridTouchedListener, WorkspaceView, ColorChooserDialog.ColorCallback, ToolSelectedListener {

    lateinit var fab: CustomFabSpeedDial
    private val presenter = drawPresenter()
    private val blendUseCase = blendUseCase()
    private var swatchToModify: SwatchView? = null
    private val modelHistory = HistoryUseCase(WorkspaceModel())
    private var selected: Boolean = false
    private var activeSwatch: SwatchView? = null
    private var displayLayoutBorders = true

    override val layoutId: Int
        get() = R.layout.fragment_workspace

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.bindView(this)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fab = activity.findViewById(R.id.view_fab_workspace)
        setFabState()
        view_draw_layers.bind(modelHistory)
        view_draw_palettes.bind(modelHistory)
        modelHistory.observe().subscribeOn(AndroidSchedulers.mainThread()).subscribe { this.render(it) }
        view_draw_tools.setOnToolSelectedListener(this)
        view_draw_tools_current.bind(Tools.defaultTool())

        view_draw_sliding_up_panel.addPanelSlideListener(object : SlidingUpPanelLayout.SimplePanelSlideListener() {
            override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {
                val scale = if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) 0f else 1f
                fab.animate().scaleY(scale).scaleX(scale).start()
            }
        })
        view_draw_drawer.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerOpened(drawerView: View?) {
                setFabState()
            }

            override fun onDrawerClosed(drawerView: View?) {
                setFabState()
            }
        })
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view_draw_led_grid_view.setOnGridTouchedListener(this)
        val paletteView = view_draw_drawer.findViewById<PaletteView>(R.id.view_draw_palette)
        modelHistory.observe().subscribeOn(AndroidSchedulers.mainThread()).subscribe { paletteView.bind(it.selectedPalette) }
        paletteView.setOnSwatchSelectedListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        if (selected) {
            inflater.inflate(R.menu.menu_workspace, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.menu_borders -> {
            displayLayoutBorders = !displayLayoutBorders
            render(modelHistory.present)
            true
        }
        R.id.menu_undo -> {
            modelHistory.stepBackInTime()
            true
        }
        R.id.menu_redo -> {
            modelHistory.stepForwardInTime()
            true
        }
        else -> false
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        modelHistory.hasPast()
                .compose(bindMenuItem(menu, R.id.menu_undo))
                .subscribe()
        modelHistory.hasFuture()
                .compose(bindMenuItem(menu, R.id.menu_redo))
                .subscribe()
        val item = menu.findItem(R.id.menu_borders)
        item?.setIcon(if (displayLayoutBorders)
            R.drawable.ic_border_outer_white_48px
        else
            R.drawable.ic_border_clear_white_48px)
    }

    override fun onFragmentSelected() {
        selected = true
        fab.visibility = View.VISIBLE
        invalidateOptionsMenu()
    }

    override fun onFragmentUnselected() {
        selected = false
        fab.visibility = View.GONE
        invalidateOptionsMenu()
    }

    override fun onSwatchSelected(swatchView: SwatchView) {
        this.activeSwatch = swatchView
    }

    override fun onSwatchLongPressed(swatch: SwatchView) {
        swatchToModify = swatch
        ColorChooserDialog.Builder(context as HomeActivity, R.string.change_color)
                .show()
    }

    override fun onColorSelection(dialog: ColorChooserDialog, @ColorInt selectedColor: Int) {
        swatchToModify?.let {
            if (it.color != selectedColor) {
                with(modelHistory) {
                    progressTimeWithoutAnnouncing()
                    present.selectedPalette.changeColor(it.index, selectedColor)
                    collapsePresentWithPastIfTheSame()
                    announcePresent()
                }
            }
        }
    }

    override fun onGridTouchStarted() {
        modelHistory.progressTime()
    }

    override fun onGridTouch(startColumn: Int, startRow: Int, column: Int, row: Int) {
        activeSwatch?.let {
            view_draw_tools_current.drawingTool?.drawOn(modelHistory.present.selectedLayer, startColumn, startRow, column, row, it.color)
            render(modelHistory.present)
        }
    }

    override fun onGridTouchFinished() {
        view_draw_tools_current.drawingTool?.finishStroke(modelHistory.present.selectedLayer)
        modelHistory.collapsePresentWithPastIfTheSame()
    }

    override fun onToolSelected(tool: Tools) {
        view_draw_tools_current.bind(tool)
        view_draw_sliding_up_panel.panelState = SlidingUpPanelLayout.PanelState.HIDDEN
    }

    override fun askForFileName(name: Function1<String, Unit>) {
        MaterialDialog.Builder(activity)
                .input(R.string.name_of_drawing, 0, false) { _, input -> name.invoke(input.toString()) }
                .title(R.string.enter_name_for_drawing)
                .positiveText(R.string.upload)
                .negativeText(android.R.string.cancel)
                .build()
                .show()
    }

    override fun fileUploaded() {
        Snackbars.success(coordinator(), R.string.success).show()
        stopFabProgress()
    }

    override fun failedToUpload(e: Throwable) {
        Log.e(WorkspacePresenter::class.java.name, "Error uploading to game frame", e)
        Snackbars.error(coordinator(), R.string.operation_failed).show()
        stopFabProgress()
    }

    override fun displayUploading() {
        startFabProgress()
    }

    override fun drawingAlreadyExists(name: String, colorGrid: Grid, e: Throwable) {
        Log.e(WorkspacePresenter::class.java.name, "Drawing already exists", e)
        Snackbars.actionError(coordinator(), R.string.already_exists, R.string.replace,
                { presenter.replaceDrawing(name, colorGrid) }).show()
        stopFabProgress()
    }

    override fun failedToDelete(e: Throwable) {
        Log.e(WorkspacePresenter::class.java.name, "Failed to delete drawing", e)
        Snackbars.error(coordinator(), R.string.operation_failed).show()
    }

    @SuppressLint("RtlHardcoded")
    @OnClick(R.id.view_draw_open_layers)
    fun openLayers() {
        view_draw_drawer.openDrawer(Gravity.RIGHT)
    }

    @SuppressLint("RtlHardcoded")
    @OnClick(R.id.view_draw_open_palette)
    fun openPalette() {
        view_draw_drawer.openDrawer(Gravity.LEFT)
    }

    @OnClick(R.id.view_draw_tools_change, R.id.view_draw_tools_current)
    fun changeTool() {
        view_draw_sliding_up_panel.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
    }

    @SuppressLint("RtlHardcoded")
    private fun setFabState() = with(fab) {
        when {
            view_draw_drawer.isDrawerOpen(Gravity.RIGHT) -> {
                setMenuListener(addNewLayerOperation())
                setImageResource(R.drawable.ic_add_white_48px)
            }
            view_draw_drawer.isDrawerOpen(Gravity.LEFT) -> {
                setMenuListener(addNewPaletteOperation())
                setImageResource(R.drawable.ic_add_white_48px)
            }
            else -> {
                setMenuListener(standardOperation())
                setImageResource(R.drawable.ic_import_export_white_48px)
            }
        }
    }

    private fun addNewPaletteOperation() = object : SimpleMenuListenerAdapter() {
        override fun onPrepareMenu(navigationMenu: NavigationMenu?): Boolean {
            AddPaletteView.show(context, view_draw_drawer, object : AddNewPaletteSelectedListener {
                override fun onAddNewPalletSelected(palette: Palette) {
                    view_draw_palettes.addNewPalette(palette)
                }
            })
            return false
        }
    }

    private fun addNewLayerOperation() = object : SimpleMenuListenerAdapter() {
        override fun onPrepareMenu(navigationMenu: NavigationMenu?): Boolean {
            view_draw_layers.addNewLayer()
            return false
        }
    }

    private fun standardOperation() = object : SimpleMenuListenerAdapter() {
        override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
            R.id.operation_upload -> {
                presenter.upload(view_draw_led_grid_view.colorGrid)
                true
            }
            else -> false
        }
    }

    private fun startFabProgress() {
        fab.startProgress()
    }

    private fun stopFabProgress() {
        fab.stopProgress(R.drawable.ic_import_export_white_48px)
    }

    private fun coordinator() = activity.findViewById<View>(R.id.view_coordinator)

    private fun render(model: WorkspaceModel) {
        model.layers.forEach { blendUseCase.renderOn(it, view_draw_led_grid_view) }
        val selected = model.layers.firstOrNull { it.isSelected }

        if (selected != null && displayLayoutBorders) {
            val colorGrid = selected.colorGrid
            view_draw_led_grid_view.displayBoundaries(colorGrid.columnTranslation, colorGrid.rowTranslation)
        } else {
            view_draw_led_grid_view.clearBoundaries()
        }
        view_draw_led_grid_view.invalidate()
        invalidateOptionsMenu()
    }

    private fun invalidateOptionsMenu() {
        activity.invalidateOptionsMenu()
    }

    private fun bindMenuItem(menu: Menu, menuId: Int) = SingleTransformer<Boolean, Boolean> { timeline -> timeline
            .doOnSuccess { hasMoments ->
                menu.findItem(menuId)?.let { item ->
                    item.icon.alpha = if (hasMoments) 255 else 125
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
    }
}
