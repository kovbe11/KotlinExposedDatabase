package com.references.sqlproject.view.insert

import com.references.sqlproject.controller.DatabaseController
import com.references.sqlproject.model.ItemModel
import com.references.sqlproject.view.idField
import com.references.sqlproject.view.priceField
import com.references.sqlproject.view.quantityField
import com.references.sqlproject.view.stringField
import tornadofx.*
import java.util.*

class NewItemForm(val controller: DatabaseController) : Fragment("Add new Items") {

    val model = TempItem()
    private val validators = LinkedList<ValidationContext.Validator<String>>()

    override val root = form {
        fieldset {
            val validator1 = idField("id", model::id, finder = controller::findItemById)
            validators.add(validator1)

            val validator2 = stringField("internet code", model::ic)
            validators.add(validator2)

            val validator3 = stringField("name", model::name)
            validators.add(validator3)

            val validator4 = priceField("purchase price", model::pp)
            validators.add(validator4)

            val validator5 = quantityField("available", model::available)
            validators.add(validator5)
        }

        button("Add") {
            isDefaultButton = true

            action {
                if (!validators.all { it.isValid }) {
                    return@action
                }

                val itemModel = ItemModel().apply {
                    item = controller.insertItem(model)
                }

                controller.items.add(itemModel)
                validators.forEach { it.validate() }
            }
        }
    }

}