package com.references.sqlproject.model

class ItemModel : Model<Item>() {
    val id = bind(Item::id)
    val ic = bind(Item::ic)
    val name = bind(Item::name)
    val pPrice = bind(Item::pPrice)
    val available = bind(Item::available)
    override fun contains(subString: String): Boolean {
        return id.value.value.toString().contentEquals(subString) ||
                name.value.contains(subString) ||
                ic.value.contains(subString) ||
                pPrice.value.toString().contentEquals(subString) ||
                available.value.toString().contentEquals(subString)
    }

    override fun toString(): String {
        return item.name
    }
}