package com.references.sqlproject

import com.references.sqlproject.view.MainView
import com.references.sqlproject.view.Styles
import javafx.application.Application
import javafx.stage.Stage
import tornadofx.App
import tornadofx.find
import tornadofx.importStylesheet

class MyApp : App(MainView::class, Styles::class) {
    init {
        importStylesheet(Styles::class)
    }

    override fun start(stage: Stage) {
        stage.isMaximized = true
        super.start(stage)
        stage.setOnCloseRequest {
            find(MainView::class).tryClosing()
            it.consume()
        }
    }
}



fun main(args: Array<String>) {
    Application.launch(MyApp::class.java, *args)
}
