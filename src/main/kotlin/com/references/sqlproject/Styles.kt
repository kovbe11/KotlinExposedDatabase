package com.references.sqlproject

import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.cssclass
import tornadofx.px


//todo
class Styles : Stylesheet() {
    companion object {
        val heading by cssclass()
    }

    init {

        root{
            padding = box(5.px)
        }
    }
}
