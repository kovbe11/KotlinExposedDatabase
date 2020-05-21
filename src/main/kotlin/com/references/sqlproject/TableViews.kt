package com.references.sqlproject

import javafx.collections.ObservableList
import javafx.event.EventTarget
import javafx.scene.control.SelectionMode
import javafx.scene.control.TableView
import tornadofx.*

fun EventTarget.itemview(items: ObservableList<ItemModel>, op: TableView<ItemModel>.() -> Unit = {}) {
    tableview(items) {
        column("#", ItemModel::id).fixedWidth(40)
        column("ic", ItemModel::ic).makeEditable().weightedWidth(1.0)
        column("name", ItemModel::name).makeEditable().remainingWidth()
        column("purchase price", ItemModel::pPrice).makeEditable().weightedWidth(0.7)
        column("available", ItemModel::available).makeEditable().weightedWidth(0.7)
        smartResize()
        enableDirtyTracking()
        selectionModel.selectionMode = SelectionMode.MULTIPLE
        selectOnDrag()
        op()
    }
}