package com.references.sqlproject

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.StringProperty
import javafx.event.EventHandler
import javafx.scene.control.SelectionMode
import javafx.scene.control.TabPane
import javafx.scene.input.KeyCode
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.*
import java.util.*

class DatabaseView : View("Database") {

    private val controller: DatabaseController by inject()

    private var itemTable: TableViewEditModel<ItemModel> by singleAssign()
    private var shopTable: TableViewEditModel<ShopModel> by singleAssign()
    private var orderTable: TableViewEditModel<OrderModel> by singleAssign()
    private var saleTable: TableViewEditModel<SaleModel> by singleAssign()

    private var filter: StringProperty by singleAssign()
    private val filterTarget: SimpleObjectProperty<FilterTargetType> = SimpleObjectProperty(FilterTargetType.All)

    val items = SortedFilteredList(controller.items)
    val shops = SortedFilteredList(controller.shops)
    val orders = SortedFilteredList(controller.orders)
    val sales = SortedFilteredList(controller.sales)

    override val root = vbox {
        menubar {
            menu("File") {
                menu("New..") {
                    item("Item") {
                        action {
                            newItem()
                        }
                    }
                    item("Shop") {
                        action {
                            newShop()
                        }
                    }
                    item("Order") {
                        action {
                            newOrder()
                        }
                    }
                    item("Sale") {
                        action {
                            newSale()
                        }
                    }
                }
                item("export to csv")
            }
            menu("Edit") {
                menu("Commit..") {
                    item("Items") {
                        action {
                            controller.commitDirtyItems(itemTable.items.asSequence())
                        }
                    }
                    item("Shops") {
                        action {
                            controller.commitDirtyShops(shopTable.items.asSequence())
                        }
                    }
                    item("Orders") {
                        action {
                            controller.commitDirtyOrders(orderTable.items.asSequence())
                        }
                    }
                    item("Sales") {
                        action {
                            controller.commitDirtySales(saleTable.items.asSequence())
                        }
                    }
                }
                item("Rollback") {
                    action {
                        itemTable.rollback()
                        shopTable.rollback()
                        orderTable.rollback()
                        saleTable.rollback()
                    }
                }
            }
        }
        hbox {
            textfield {
                filter = textProperty()
            }
            combobox(filterTarget, FilterTargetType.values().asList().asObservable()) {

            }
        }
        splitpane {
            setDividerPosition(0, 0.5)
            draggabletabpane {
                draggabletab("Items") {
                    tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

                    tableview<ItemModel> {

                        val filterer = this@DatabaseView.items.bindTo(this)
                        filterer.filterWhen(filter) { filter, item ->
                            item.contains(filter) || !filterTarget.value.isEnabled(FilterTargetType.Items)
                        }

                        filterer.filterWhen(filterTarget) { _, item ->
                            item.contains(filter.get()) || !filterTarget.value.isEnabled(FilterTargetType.Items)
                        }


                        smartResize()

                        itemTable = editModel
                        enableDirtyTracking()
                        selectionModel.selectionMode = SelectionMode.MULTIPLE


                        //TODO: miért nem tudom ezt duplikáció nélkül kihozni a többire is??
                        onKeyPressed = EventHandler {
                            if (it.code == KeyCode.DELETE) {
                                val selected = selectionModel.selectedCells
                                var msg = selected.joinToString(limit = 3, truncated = "\nand ${selected.size - 3} more") {
                                    items[it.row].name.value
                                }

                                confirm("Are you sure you want to delete this item?", "Selected item is: $msg", actionFn = {
                                    selectionModel.selectedItems.forEach {
                                        controller.delete(it)
                                    }
                                })
                            }
                        }

                        column("#", ItemModel::id).fixedWidth(40)
                        column("ic", ItemModel::ic).makeEditable().weightedWidth(1.0)
                        column("name", ItemModel::name).makeEditable().remainingWidth()
                        column("purchase price", ItemModel::pPrice).makeEditable().weightedWidth(1.0)
                        column("available", ItemModel::available).makeEditable().weightedWidth(1.0)
                        smartResize()
                    }
                }
                draggabletab("Shops") {
                    tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

                    tableview<ShopModel> {
                        val filterer = shops.bindTo(this)
                        filterer.filterWhen(filter) { filter, item ->
                            item.contains(filter) || !filterTarget.value.isEnabled(FilterTargetType.Shops)
                        }

                        filterer.filterWhen(filterTarget) { _, item ->
                            item.contains(filter.get()) || !filterTarget.value.isEnabled(FilterTargetType.Shops)
                        }
                        shopTable = editModel

                        enableDirtyTracking()
                        selectionModel.selectionMode = SelectionMode.MULTIPLE

                        column("#", ShopModel::id).fixedWidth(40)
                        column("name", ShopModel::name).makeEditable().remainingWidth()
                        column("address", ShopModel::address).makeEditable().weightedWidth(0.5)
                        column("tax", ShopModel::tax).makeEditable().weightedWidth(0.5)
                        column("contact", ShopModel::contact).makeEditable().weightedWidth(0.5)
                        column("last sale", ShopModel::lastSaleDate).fixedWidth(90)
                        smartResize()
                    }
                }
            }
            draggabletabpane {
                draggabletab("Orders") {
                    tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

                    tableview<OrderModel> {
                        val filterer = orders.bindTo(this)
                        filterer.filterWhen(filter) { filter, item ->
                            item.contains(filter) || !filterTarget.value.isEnabled(FilterTargetType.Orders)
                        }

                        filterer.filterWhen(filterTarget) { _, item ->
                            item.contains(filter.get()) || !filterTarget.value.isEnabled(FilterTargetType.Orders)
                        }
                        orderTable = editModel


                        enableDirtyTracking()

                        selectionModel.selectionMode = SelectionMode.MULTIPLE
                        column("#", OrderModel::id).fixedWidth(40)
                        column("itemId", OrderModel::itemId).makeEditable().weightedWidth(0.3)
                        readonlyColumn("itemName", OrderModel::itemName).weightedWidth(1)
                        column("netPrice", OrderModel::netPrice).makeEditable().weightedWidth(0.5)
                        column("date", OrderModel::date).makeEditable().fixedWidth(90)
                        column("quantity", OrderModel::quantity).makeEditable().weightedWidth(0.5)
                        smartResize()
                    }
                }
                draggabletab("Sales") {
                    tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

                    tableview<SaleModel> {
                        val filterer = sales.bindTo(this)
                        filterer.filterWhen(filter) { filter, item ->
                            item.contains(filter) || !filterTarget.value.isEnabled(FilterTargetType.Sales)
                        }

                        filterer.filterWhen(filterTarget) { _, item ->
                            item.contains(filter.get()) || !filterTarget.value.isEnabled(FilterTargetType.Sales)
                        }
                        saleTable = editModel

                        enableDirtyTracking()

                        selectionModel.selectionMode = SelectionMode.MULTIPLE
                        column("#", SaleModel::id).fixedWidth(40)
                        column("itemId", SaleModel::itemId).makeEditable().weightedWidth(0.3)
                        readonlyColumn("itemName", SaleModel::itemName).weightedWidth(1.0)
                        column("buyerId", SaleModel::buyerId).makeEditable().weightedWidth(0.3)
                        readonlyColumn("shop name", SaleModel::shopName).weightedWidth(1.0)
                        column("date", SaleModel::date).makeEditable()
                        column("selling price", SaleModel::sPrice).makeEditable().weightedWidth(0.5)
                        column("quantity", SaleModel::quantity).makeEditable().weightedWidth(0.5)
                        smartResize()
                    }
                }
            }
        }
    }

    fun newItem() {
        dialog {

            val model = TempItem()
            val validators = LinkedList<ValidationContext.Validator<String>>()

            form {
                field("id") {

                    //TODO: rájönni a bindolás szintaxisára
                    val textField = textfield {
                        textProperty().addListener(ChangeListener { _, _, newValue ->
                            model.id = newValue.toIntOrNull()
                        })
                    }

                    val validator = ValidationContext().addValidator(textField, textField.textProperty()) {
                        val item: Item? = transaction(DB.db) {
                            textField.text.toIntOrNull()?.let { Item.findById(it) }
                        }
                        when {
                            item != null -> {
                                error("There is already an item with this id")
                            }
                            else -> null
                        }

                    }

                    validators.add(validator)
                }

                field("internet code") {
                    val textField = textfield {
                        textProperty().addListener(ChangeListener { _, _, newValue ->
                            model.ic = newValue
                        })
                    }

                    val validator = ValidationContext().addValidator(textField, textField.textProperty()) {
                        if (it.isNullOrBlank()) error("The internet code field is required") else null
                    }
                    validator.validate()
                    validators.add(validator)
                }

                field("name") {
                    val textField = textfield {
                        textProperty().addListener(ChangeListener { _, _, newValue ->
                            model.name = newValue
                        })
                    }

                    val validator = ValidationContext().addValidator(textField, textField.textProperty()) {
                        if (it.isNullOrBlank()) error("The internet code field is required") else null
                    }
                    validator.validate()
                    validators.add(validator)
                }

                field("purchase price") {
                    val textField = textfield {
                        textProperty().addListener(ChangeListener { _, _, newValue ->
                            model.pp = newValue.toDoubleOrNull()
                        })
                    }

                    val validator = ValidationContext().addValidator(textField, textField.textProperty()) {
                        val pp = textField.text.toDoubleOrNull()

                        when {
                            pp == null -> error("purchase price is required")
                            pp < 0 -> error("purchase price must be > 0")
                            else -> null
                        }

                    }
                    validator.validate()
                    validators.add(validator)
                }

                field("available") {
                    val textField = textfield {
                        textProperty().addListener(ChangeListener { _, _, newValue ->
                            model.available = newValue.toIntOrNull()
                        })
                    }

                    val validator = ValidationContext().addValidator(textField, textField.textProperty()) {
                        val available = textField.text.toIntOrNull()

                        when {
                            available == null -> error("available is required")
                            available < 0 -> error("available must be > 0")
                            else -> null
                        }

                    }
                    validator.validate()
                    validators.add(validator)
                }
            }


            button("Add") {
                isDefaultButton = true

                action {
                    if (!validators.all { it.isValid }) {
                        return@action
                    }

                    val itemModel = ItemModel().apply {
                        item = transaction(DB.db) {
                            Item.new(model.id) {
                                ic = model.ic!!
                                name = model.name!!
                                pPrice = model.pp!!
                                available = model.available!!
                            }
                        }
                    }

                    controller.items.add(itemModel)
                }
            }
        }
    }

    fun newShop() {
        dialog {

            val model = TempShop()
            val validators = LinkedList<ValidationContext.Validator<String>>()

            form {
                field("id") {

                    val textField = textfield {
                        textProperty().addListener(ChangeListener { _, _, newValue ->
                            model.id = newValue.toIntOrNull()
                        })
                    }

                    val validator = ValidationContext().addValidator(textField, textField.textProperty()) {
                        val item: Item? = transaction(DB.db) {
                            textField.text.toIntOrNull()?.let { Item.findById(it) }
                        }
                        when {
                            item != null -> {
                                error("There is already an item with this id")
                            }
                            else -> null
                        }

                    }

                    validators.add(validator)
                }

                field("name") {
                    val textField = textfield {
                        textProperty().addListener(ChangeListener { _, _, newValue ->
                            model.name = newValue
                        })
                    }

                    val validator = ValidationContext().addValidator(textField, textField.textProperty()) {
                        if (it.isNullOrBlank()) error("The name is required") else null
                    }
                    validator.validate()
                    validators.add(validator)
                }

                field("address") {
                    textfield {
                        textProperty().addListener(ChangeListener { _, _, newValue ->
                            model.address = newValue
                        })
                    }
                }

                field("tax") {
                    textfield {
                        textProperty().addListener(ChangeListener { _, _, newValue ->
                            model.tax = newValue
                        })
                    }
                }

                field("contact") {
                    textfield {
                        textProperty().addListener(ChangeListener { _, _, newValue ->
                            model.contact = newValue
                        })
                    }
                }


                button("Add") {
                    isDefaultButton = true

                    action {
                        if (!validators.all { it.isValid }) {
                            return@action
                        }

                        val shopModel = ShopModel().apply {
                            item = transaction(DB.db) {
                                Shop.new(model.id) {
                                    name = model.name!!
                                    address = model.address
                                    tax = model.tax
                                    contact = model.contact
                                }
                            }
                        }

                        controller.shops.add(shopModel)
                    }
                }
            }
        }
    }

    fun newOrder() {
        dialog("new Order") {

        }
    }

    fun newSale() {
        dialog("new Sales") {

            form {

            }


            button("Add") {
                isDefaultButton = true
            }
        }

    }

}

class TempItem(var id: Int? = null,
               var ic: String? = null,
               var name: String? = null,
               var pp: Double? = null,
               var available: Int? = null) {}

class TempShop(var id: Int? = null,
               var name: String? = null,
               var address: String? = null,
               var tax: String? = null,
               var contact: String? = null) {}

class TempSale(var id: Int? = null,
               var buyerId: Int? = null,
               var date: String? = null,
               var sp: Double? = null,
               var quantity: Int? = null) {}

class TempOrder(var id: Int? = null,
                var itemId: Int? = null,
                var np: Double? = null,
                var date: String? = null,
                var quantity: Int? = null) {}
