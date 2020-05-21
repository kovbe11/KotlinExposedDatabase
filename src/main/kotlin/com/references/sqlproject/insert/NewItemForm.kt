package com.references.sqlproject.insert

import com.references.sqlproject.DB
import com.references.sqlproject.DatabaseController
import com.references.sqlproject.Item
import com.references.sqlproject.models.ItemModel
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.*
import java.util.*

class NewItemForm(val controller: DatabaseController) : Fragment("Add new Items") {

    val model = TempItem()
    val validators = LinkedList<ValidationContext.Validator<String>>()

    override val root = form {
        fieldset {
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
                    item = controller.insertItem(model)
                }

                controller.items.add(itemModel)
            }
        }
    }

}