package com.references.sqlproject

import javafx.collections.ObservableList
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.Controller
import tornadofx.ItemViewModel
import tornadofx.TableColumnDirtyState
import tornadofx.asObservable


typealias ItemDirtyStateMapping = Map.Entry<ItemModel, TableColumnDirtyState<ItemModel>>
typealias ShopDirtyStateMapping = Map.Entry<ShopModel, TableColumnDirtyState<ShopModel>>
typealias OrderDirtyStateMapping = Map.Entry<OrderModel, TableColumnDirtyState<OrderModel>>
typealias SaleDirtyStateMapping = Map.Entry<SaleModel, TableColumnDirtyState<SaleModel>>



//minden ami adatbázissal kommunikál az alkalmazásból az ide kerül
class DatabaseController : Controller() {


    val items: ObservableList<ItemModel> by lazy {
        transaction(DB.db) {
            Item.all().map {
                ItemModel().apply {
                    item = it
                }
            }.asObservable()
        }
    }

    val shops: ObservableList<ShopModel> by lazy {
        transaction(DB.db) {
            Shop.all().map {
                ShopModel().apply {
                    item = it
                }
            }.asObservable()
        }
    }

    val orders: ObservableList<OrderModel> by lazy{
        transaction(DB.db) {
            Order.all().map {
                OrderModel().apply {
                    item = it
                }
            }.asObservable()
        }
    }

    val sales: ObservableList<SaleModel> by lazy {
        transaction(DB.db) {
            Sale.all().map {
                SaleModel().apply {
                    item = it
                }
            }.asObservable()
        }
    }

    fun delete(model: ItemModel){
        transaction(DB.db) {
            model.item.delete()
        }
        items.remove(model)
    }

    fun delete(model: SaleModel){
        transaction(DB.db) {
            model.item.delete()
        }
        sales.remove(model)
    }

    fun delete(model: OrderModel){
        transaction(DB.db) {
            model.item.delete()
        }
        orders.remove(model)
    }

    fun delete(model: ShopModel){
        transaction(DB.db) {
            model.item.delete()
        }
        shops.remove(model)
    }

    //itt akárhogy próbálkoztam nem sikerült találnom egy olyan typealiast
    //amivel csak egyszer kell implementálni.. Ha te ki tudod találni kérlek írj rám
    //mert valószínű hogy valamit nem értek és azért nem sikerült.

    fun commitDirtyItems(dirtyMapping: Sequence<ItemDirtyStateMapping>){
        transaction(DB.db) {
            dirtyMapping.filter { it.value.isDirty }.forEach {
                it.key.commit()
                it.value.commit()
            }
        }
    }

    fun commitDirtyShops(dirtyMapping: Sequence<ShopDirtyStateMapping>){
        transaction(DB.db) {
            dirtyMapping.filter { it.value.isDirty }.forEach {
                it.key.commit()
                it.value.commit()
            }
        }
    }
    fun commitDirtyOrders(dirtyMapping: Sequence<OrderDirtyStateMapping>){
        transaction(DB.db) {
            dirtyMapping.filter { it.value.isDirty }.forEach {
                it.key.commit()
                it.value.commit()
            }
        }
    }
    fun commitDirtySales(dirtyMapping: Sequence<SaleDirtyStateMapping>){
        transaction(DB.db) {
            dirtyMapping.filter { it.value.isDirty }.forEach {
                it.key.commit()
                it.value.commit()
            }
        }
    }

}