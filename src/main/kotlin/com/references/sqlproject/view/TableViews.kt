package com.references.sqlproject.view

import com.references.sqlproject.model.ItemModel
import com.references.sqlproject.model.OrderModel
import com.references.sqlproject.model.SaleModel
import com.references.sqlproject.model.ShopModel
import javafx.collections.ObservableList
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
        column("itemId", OrderModel::idOfItem).makeEditable().contentWidth(useAsMin = true, useAsMax = true)
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

fun EventTarget.saleview(items: ObservableList<SaleModel>, op: TableView<SaleModel>.() -> Unit = {}) {

    tableview(items) {

        column("#", SaleModel::id).contentWidth(useAsMin = true, useAsMax = false)
        column("itemId", SaleModel::itemId).makeEditable().contentWidth(useAsMin = true, useAsMax = false)
        readonlyColumn("itemName", SaleModel::itemName).weightedWidth(1.0)
        column("buyerId", SaleModel::buyerId).makeEditable().contentWidth(useAsMin = true, useAsMax = false)
        readonlyColumn("shop name", SaleModel::shopName).weightedWidth(1.0)
        column("date", SaleModel::date).makeEditable().fixedWidth(120)
        column("selling price", SaleModel::sPrice).makeEditable().weightedWidth(0.5)
        column("quantity", SaleModel::quantity).makeEditable().weightedWidth(0.5)

        smartResize()
        enableDirtyTracking()
        selectionModel.selectionMode = SelectionMode.MULTIPLE
        selectOnDrag()

        op()
    }

}