package com.references.sqlproject.view.insert

import com.references.sqlproject.controller.DatabaseController
import javafx.collections.ObservableList
import javafx.event.EventTarget
import javafx.scene.text.Text
import tornadofx.*


class TempItem(var id: Int? = null,
               var ic: String? = null,
               var name: String? = null,
               var pp: Double? = null,
               var available: Int? = null)

class TempShop(var id: Int? = null,
               var name: String? = null,
               var address: String? = null,
               var tax: String? = null,
               var contact: String? = null)

class TempSale(var id: Int? = null,
               var itemId: Int? = null,
               var buyerId: Int? = null,
               var date: String? = null,
               var sp: Double? = null,
               var quantity: Int? = null)

class TempOrder(var id: Int? = null,
                var itemId: Int? = null,
                var np: Double? = null,
                var date: String? = null,
                var quantity: Int? = null)

fun <M, V : ItemViewModel<M>> sumElements(netSumText: Text,
                                          grossSumText: Text,
                                          elements: ObservableList<V>,
                                          summingFn: (V) -> Double) {
    val netSum = elements.sumByDouble {
        summingFn(it)
    }

    netSumText.text = "$netSum\n"
    grossSumText.text = "${netSum * 1.27}"
}

fun <M, V : ItemViewModel<M>> tryClosing(tableEditModel: TableViewEditModel<V>, frag: Fragment) {
    if (tableEditModel.items.all {
                !it.value.isDirty
            }) {
        frag.close()
    } else {
        confirm(owner = frag.currentWindow, title = "Warning",
                content = "You have not commited changes in your order.\nIf you click ok, these changes will be lost.",
                header = "Uncommitted changes!",
                actionFn = {
                    tableEditModel.rollback()
                    frag.close()
                })
    }
}

fun <M, V : ItemViewModel<M>> EventTarget.commitrollbackbar(controller: DatabaseController, tableEditModel: TableViewEditModel<V>) {
    menubar {
        menu("edit") {
            item("commit") {
                action {
                    controller.commitDirty(tableEditModel.items)
                }
            }
            item("rollback") {
                action {
                    tableEditModel.rollback()
                }
            }
        }
    }
}