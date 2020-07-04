package com.references.sqlproject.view

import javafx.event.EventTarget
import tornadofx.ChangeListener
import tornadofx.ValidationContext
import tornadofx.field
import tornadofx.textfield
import kotlin.reflect.KMutableProperty0

typealias StringValidator = ValidationContext.Validator<String>

fun <M> EventTarget.idField(text: String, idProp: KMutableProperty0<Int?>,
                            isRequired: Boolean = false,
                            mustFind: Boolean = false,
                            notFoundMsg: String = "",
                            finder: (Int?) -> (M?) = { null }): StringValidator {
    var ret: StringValidator? = null

    val mustNotFind = !mustFind

    field(text) {
        val textField = textfield {
            textProperty().addListener(ChangeListener { _, _, newValue ->
                idProp.set(newValue.toIntOrNull())
            })
        }

        ret = ValidationContext().addValidator(textField, textField.textProperty()) {
            val m = finder(idProp.get())
            when {
                isRequired && it.isNullOrBlank() -> error("This field is required")
                mustFind && m == null -> {
                    error(notFoundMsg)
                }
                mustNotFind && m != null -> {
                    error("This id is taken!")
                }
                else -> null
            }
        }

    }

    return ret ?: throw IllegalStateException("unknown error")
}

fun EventTarget.priceField(text: String, priceProp: KMutableProperty0<Double?>)
        : StringValidator {
    var ret: StringValidator? = null

    field(text) {
        val textField = textfield {
            textProperty().addListener(ChangeListener { _, _, newValue ->
                priceProp.set(newValue.toDoubleOrNull())
            })
        }

        ret = ValidationContext().addValidator(textField, textField.textProperty()) {
            val p = priceProp.get()
            when {
                p == null -> error("This field requires a number!")
                p < 0 -> error("Must be bigger than 0!")
                else -> null
            }
        }

    }
    ret!!.validate()
    return ret!!
}

fun EventTarget.quantityField(text: String, quantityProp: KMutableProperty0<Int?>)
        : StringValidator {
    var ret: StringValidator? = null

    field(text) {
        val textField = textfield {
            textProperty().addListener(ChangeListener { _, _, newValue ->
                quantityProp.set(newValue.toIntOrNull())
            })
        }

        ret = ValidationContext().addValidator(textField, textField.textProperty()) {
            val p = quantityProp.get()
            when {
                p == null -> error("This field requires a number!")
                p < 0 -> error("Must be bigger than 0!")
                else -> null
            }
        }

    }
    ret!!.validate()
    return ret!!
}

private val dateFormat = Regex("^((19|2[0-9])[0-9]{2})-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])\$")


fun EventTarget.dateField(stringProp: KMutableProperty0<String?>): StringValidator {
    var ret: StringValidator? = null

    field("Date") {
        val textField = textfield {
            textProperty().addListener(ChangeListener { _, _, newValue ->
                stringProp.set(newValue)
            })
        }

        ret = ValidationContext().addValidator(textField, textField.textProperty()) {
            when {
                it.isNullOrBlank() -> error("This field is required!")
                !it.matches(dateFormat) -> error("Invalid date format!")
                else -> null
            }
        }
    }

    ret!!.validate()
    return ret!!
}

fun EventTarget.stringField(text: String,
                            stringProp: KMutableProperty0<String?>,
                            isRequired: Boolean = true,
                            failMsg: String = "This field is required"): StringValidator {
    var ret: StringValidator? = null

    field(text) {
        val textField = textfield {
            textProperty().addListener(ChangeListener { _, _, newValue ->
                stringProp.set(newValue)
            })
        }

        ret = ValidationContext().addValidator(textField, textField.textProperty()) {
            if (isRequired) {
                if (it.isNullOrBlank()) error(failMsg) else null
            } else null
        }
    }

    ret!!.validate()
    return ret!!
}