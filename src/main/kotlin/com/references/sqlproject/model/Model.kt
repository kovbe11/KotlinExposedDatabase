package com.references.sqlproject.model

import org.jetbrains.exposed.dao.IntEntity
import tornadofx.ItemViewModel


abstract class Model<T : IntEntity> : ItemViewModel<T>() {
    abstract fun contains(subString: String): Boolean
}