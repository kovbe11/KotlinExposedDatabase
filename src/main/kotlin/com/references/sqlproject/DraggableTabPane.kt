package com.references.sqlproject

import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.scene.control.Label
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.input.ClipboardContent
import javafx.scene.input.TransferMode
import tornadofx.attachTo
import tornadofx.tag


//a kód ezen része stackoverflowról származik, java kódként találtam valami hasonlót.


//dsl bővítés:

fun EventTarget.draggabletabpane(op: DraggableTabPane.() -> Unit = {}) =
        DraggableTabPane().attachTo(this, op)

fun DraggableTabPane.draggabletab(text: String? = null,
                                  tag: Any? = null,
                                  op: Tab.() -> Unit = {})
        : DraggableTab {
    val tab = DraggableTab(text)
    tab.tag = tag
    tabs.add(tab)
    return tab.also(op)
}

//a kód ezen része stackoverflowról származik, java kódként találtam valami hasonlót.

var draggingTab: DraggableTab? = null

class DraggableTabPane : TabPane() {
    init {
        onDragDropped =
                EventHandler { event ->
                    val dragboard = event.dragboard
                    if (dragboard?.string == "tab") {
                        if (draggingTab?.tabPane != this) {
                            draggingTab!!.tabPane!!.tabs!!.remove(draggingTab)
                            tabs.add(draggingTab)
                            selectionModel.select(draggingTab)
                            event.isDropCompleted = true
                            draggingTab = null
                            event.consume()
                        }
                    }
                }

        onDragOver =
                EventHandler { event ->
                    val dragboard = event.dragboard
                    if (dragboard?.string == "tab") {
                        if (draggingTab?.tabPane != this) {
                            event.acceptTransferModes(TransferMode.MOVE)
                            event.consume()
                        }
                    }

                }
    }


}


class DraggableTab(tabName: String?) : Tab() {
    init {
        val label = Label(tabName)
        label.onDragDetected =
                EventHandler { event ->
                    val dragboard = label.startDragAndDrop(TransferMode.MOVE)
                    val clipboardContent = ClipboardContent()
                    clipboardContent.putString("tab")
                    dragboard.setContent(clipboardContent)
                    draggingTab = this
                    event.consume()
                }
        graphic = label
    }
}