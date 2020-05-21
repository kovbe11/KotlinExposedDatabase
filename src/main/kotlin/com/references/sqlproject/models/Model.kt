package com.references.sqlproject.models

import org.jetbrains.exposed.dao.IntEntity
import tornadofx.ItemViewModel


abstract class Model<T : IntEntity> : ItemViewModel<T>() {
    abstract fun contains(subString: String): Boolean
}

