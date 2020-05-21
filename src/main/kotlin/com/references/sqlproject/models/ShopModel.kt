package com.references.sqlproject.models

import com.references.sqlproject.Shop

class ShopModel : Model<Shop>() {
    val id = bind(Shop::id)
    val name = bind(Shop::name)
    val address = bind(Shop::address)
    val tax = bind(Shop::tax)
    val contact = bind(Shop::contact)
    val lastSaleDate = bind(Shop::lastSaleDate)


    override fun contains(subString: String): Boolean {
        return id.value.value.toString().contentEquals(subString) ||
                name.value.contains(subString) ||
                if (address.value == null) {
                    false
                } else {
                    address.value.contains(subString)
                } ||
                if (tax.value == null) {
                    false
                } else {
                    tax.value.contains(subString)
                } ||
                if (contact.value == null) {
                    false
                } else {
                    contact.value.contains(subString)
                } ||
                lastSaleDate.value.contains(subString)
    }

    override fun toString(): String {
        return item.name
    }
}