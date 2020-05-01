package com.references.sqlproject

import javafx.application.Application
import tornadofx.App
import tornadofx.importStylesheet

class MyApp : App(DatabaseView::class, Styles::class){
    init {
        importStylesheet(Styles::class)
    }
}



fun main(args: Array<String>) {
    Application.launch(MyApp::class.java, *args)
}
