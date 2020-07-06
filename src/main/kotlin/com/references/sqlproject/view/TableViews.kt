package com.references.sqlproject.view

import com.references.sqlproject.controller.DatabaseController
import com.references.sqlproject.model.*
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.scene.control.SelectionMode
import javafx.scene.control.TableView
import tornadofx.*

fun EventTarget.itemview(items: ObservableList<ItemModel>, op: TableView<ItemModel>.() -> Unit = {}) {

    tableview(items) {

        column("#", ItemModel::id).contentWidth(useAsMin = true, useAsMax = false)
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

fun EventTarget.shopview(items: ObservableList<ShopModel>, op: TableView<ShopModel>.() -> Unit = {}) {

    tableview(items) {
        column("#", ShopModel::id).contentWidth(useAsMin = true, useAsMax = false)
        column("name", ShopModel::name).makeEditable().remainingWidth()
        column("address", ShopModel::address).makeEditable().weightedWidth(0.5)
        column("tax", ShopModel::tax).makeEditable().weightedWidth(0.5)
        column("contact", ShopModel::contact).makeEditable().weightedWidth(0.5)
        column("last sale", ShopModel::lastSaleDate).fixedWidth(120)

        smartResize()
        enableDirtyTracking()
        selectionModel.selectionMode = SelectionMode.MULTIPLE
        selectOnDrag()

        op()
    }

}

fun EventTarget.orderview(items: ObservableList<OrderModel>, op: TableView<OrderModel>.() -> Unit = {}): TableView<OrderModel> {

    return tableview(items) {

        column("#", OrderModel::id).contentWidth(useAsMin = true, useAsMax = false)
        column("itemId", OrderModel::idOfItem).makeEditable().contentWidth(useAsMin = true, useAsMax = false)
        readonlyColumn("itemName", OrderModel::itemName).weightedWidth(1)
        column("netPrice", OrderModel::netPrice).makeEditable().weightedWidth(0.5)
        column("date", OrderModel::date).makeEditable().fixedWidth(120)
        column("quantity", OrderModel::quantity).makeEditable().weightedWidth(0.5)

        smartResize()
        enableDirtyTracking()
        selectionModel.selectionMode = SelectionMode.MULTIPLE
        selectOnDrag()

        op()
    }

}

fun EventTarget.saleview(items: ObservableList<SaleModel>, controller: DatabaseController, op: TableView<SaleModel>.() -> Unit = {}) {

    tableview(items) {

        column("#", SaleModel::id).contentWidth(useAsMin = true, useAsMax = false)
        column("itemId", SaleModel::itemId).makeEditable().contentWidth(useAsMin = true, useAsMax = false)
        readonlyColumn("itemName", SaleModel::itemName).weightedWidth(1.0)
        column("buyerId", SaleModel::buyerId).makeEditable().contentWidth(useAsMin = true, useAsMax = false)
        readonlyColumn("shop name", SaleModel::shopName).weightedWidth(1.0)
        column("date", SaleModel::date).cellFragment(fragment = DateEditor::class).fixedWidth(120)
        column("selling price", SaleModel::sPrice).makeEditable().weightedWidth(0.5)
        val quantityCol = column("quantity", SaleModel::quantity).makeEditable().weightedWidth(0.5)

        val defEditCommit = quantityCol.onEditCommit
        quantityCol.onEditCommit = EventHandler {
            defEditCommit.handle(it)
            controller.soldItemChange(it.rowValue, it.newValue - it.oldValue)
        }

        smartResize()
        enableDirtyTracking()
        selectionModel.selectionMode = SelectionMode.MULTIPLE
        selectOnDrag()

        op()
    }

}

private val dateFormat = Regex("^((19|2[0-9])[0-9]{2})-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])\$")

class DateEditor : TableCellFragment<Sale, String>() {
    val model = SaleModel().bindToRowItem(this)


    override val root = stackpane {
        textfield(model.date) {
            removeWhen(editingProperty.not())
            validator {
                if (model.date.value.matches(dateFormat)) null else error("Invalid number")
            }
            // Call cell.commitEdit() only if validation passes
            action {
                if (model.commit()) {
                    cell?.commitEdit(model.date.value)
                }
            }
        }
        // Label is visible when not in edit mode, and always shows committed value (itemProperty)
        label(itemProperty) {
            removeWhen(editingProperty)
        }
    }

    // Make sure we rollback our model to avoid showing the last failed edit
    override fun startEdit() {
        model.rollback()
    }

}
