package com.references.sqlproject

import javafx.application.Application
import javafx.stage.Stage
import tornadofx.App
import tornadofx.importStylesheet

class MyApp : App(MainView::class, Styles::class) {
    init {
        importStylesheet(Styles::class)
    }

    override fun start(stage: Stage) {
        super.start(stage)
        stage.isMaximized = true
    }
}



fun main(args: Array<String>) {
    Application.launch(MyApp::class.java, *args)
}
