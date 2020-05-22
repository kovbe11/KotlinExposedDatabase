package com.references.sqlproject.view.insert

import com.references.sqlproject.controller.DatabaseController
import com.references.sqlproject.model.DB
import com.references.sqlproject.model.Item
import com.references.sqlproject.model.ShopModel
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.*
import java.util.*

class NewShopForm(val controller: DatabaseController) : Fragment("Adding new Shops") {

    val model = TempShop()
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

        }

        button("Add") {
            isDefaultButton = true

            action {
                if (!validators.all { it.isValid }) {
                    return@action
                }

                val shopModel = ShopModel().apply {
                    item = controller.insertShop(model)
                }

                controller.shops.add(shopModel)
            }
        }
    }
}