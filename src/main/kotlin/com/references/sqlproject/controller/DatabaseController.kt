package com.references.sqlproject.controller

import com.references.sqlproject.model.*
import com.references.sqlproject.model.DB.db
import com.references.sqlproject.view.MainView
import com.references.sqlproject.view.insert.TempItem
import com.references.sqlproject.view.insert.TempOrder
import com.references.sqlproject.view.insert.TempSale
import com.references.sqlproject.view.insert.TempShop
import javafx.collections.ObservableList
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.Controller
import tornadofx.ItemViewModel
import tornadofx.TableColumnDirtyState
import tornadofx.asObservable
import java.sql.SQLException


//minden ami adatbázissal kommunikál az alkalmazásból az ide kerül

class DatabaseController : Controller() {

    //itt lehet érdemes lenne egy loading részt betenni, és async betölteni a 2 2 független táblát,
    //de így is csak 1-2 másodperc a betöltés 10ezres nagyságrendekkel


    //mivel baromi költséges lenne az adatbázistól elkérni a nevet minden rendezésnél stb, így megéri egy mappinget csinálni.
    //próbáltam observablevaluera kötni de az addListenerbe rakott kód nem hívódik meg, csak inicializáláskor
    //így nem egységbe zárt a dolog ugyan, de nem találtam meg a hibát a gondolatmenetemben hogy működjön az observable pattern


    val item: MutableMap<Int, String> = HashMap()
    val items: ObservableList<ItemModel> by lazy {
        transaction(db) {
            Item.all().map {
                item[it.id.value] = it.name
                ItemModel().apply {
                    item = it
                }
            }.asObservable()
        }
    }

    val shop: MutableMap<Int, String> = HashMap()
    val shops: ObservableList<ShopModel> by lazy {
        transaction(db) {
            Shop.all().map {
                shop[it.id.value] = it.name
                ShopModel().apply {
                    item = it
                }
            }.asObservable()
        }
    }

    val orders: ObservableList<OrderModel> by lazy {
        transaction(db) {
            Order.all().map {
                OrderModel().apply {
                    item = it
                }
            }.asObservable()
        }
    }

    val sales: ObservableList<SaleModel> by lazy {
        transaction(db) {
            Sale.all().map {
                SaleModel().apply {
                    item = it
                }
            }.asObservable()
        }
    }

    fun delete(model: Any) {
        when (model) {
            is ItemModel -> delete(model)
            is ShopModel -> delete(model)
            is OrderModel -> delete(model)
            is SaleModel -> delete(model)
            else -> throw IllegalArgumentException()
        }
    }


    private fun delete(model: ItemModel) {

        //SQLITE nem szereti az exposedot, alapból ki van kapcsolva az fk constraint
        //és van egy issue a githubon ami ezt tárgyalja, és sajnos nincs rá megoldás
        //szóval ellenőrzök inkább kézzel.

        transaction(db) {
            if (!Order.find { Orders.itemId eq model.item.id }.empty()) {
                throw ForeignKeyViolation("There is an order referencing this item.")
            }
            if (!Sale.find { Sales.itemId eq model.item.id }.empty()) {
                throw ForeignKeyViolation("There is a sale referencing this item.")
            }
        }

        transaction(db) {
            model.item.delete()
        }
        items.remove(model)
    }

    private fun delete(model: SaleModel) {
        transaction(db) {
            model.item.delete()
        }
        sales.remove(model)
    }

    private fun delete(model: OrderModel) {
        transaction(db) {
            model.item.delete()
        }
        orders.remove(model)
    }

    private fun delete(model: ShopModel) {
        transaction(db) {
            if (!Sale.find { Sales.buyerId eq model.item.id }.empty()) {
                throw ForeignKeyViolation("There is a sale referencing this shop.")
            }
        }

        transaction(db) {
            model.item.delete()
        }
        shops.remove(model)
    }

    fun insertItem(itemInfo: TempItem): Item {
        val ret = transaction(db) {
            Item.new(itemInfo.id) {
                ic = itemInfo.ic!!
                name = itemInfo.name!!
                pPrice = itemInfo.pp!!
                available = itemInfo.available!!
            }
        }
        item[ret.id.value] = ret.name
        return ret
    }

    fun insertShop(shopInfo: TempShop): Shop {
        val ret = transaction(db) {
            Shop.new(shopInfo.id) {
                name = shopInfo.name!!
                address = shopInfo.address
                tax = shopInfo.tax
                contact = shopInfo.contact
            }
        }
        shop[ret.id.value] = ret.name
        return ret
    }

    fun insertOrder(orderInfo: TempOrder): Order {
        return transaction(db) {
            Order.new(orderInfo.id) {
                itemId = Item[orderInfo.itemId!!].id
                netPrice = orderInfo.np!!
                date = orderInfo.date!!
                quantity = orderInfo.quantity!!
            }
        }
    }

    fun insertSale(saleInfo: TempSale): Sale {
        return transaction(db) {
            Sale.new(saleInfo.id) {
                itemId = Item[saleInfo.itemId!!].id
                buyerId = Shop[saleInfo.buyerId!!].id
                sPrice = saleInfo.sp!!
                date = saleInfo.date!!
                quantity = saleInfo.quantity!!
            }
        }
    }

    fun <M, V : ItemViewModel<M>> commitDirty(dirtyMapping: Map<V, TableColumnDirtyState<V>>) {
        var flag = false

        transaction(db) {
            dirtyMapping.filter {
                it.value.isDirty
            }.forEach {
                try {
                    this.commit()
                    it.value.commit()
                } catch (ex: SQLException) {
                    find(MainView::class).registerError("${it.key}")
                    flag = true
                }
            }
        }
        if (flag) {
            find(MainView::class).showErrors()
        }
    }

}

class ForeignKeyViolation(s: String) : Throwable(s)
