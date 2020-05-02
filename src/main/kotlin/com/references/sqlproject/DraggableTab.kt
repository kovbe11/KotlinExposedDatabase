package com.references.sqlproject

import javafx.event.EventHandler
import javafx.scene.control.Label
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.input.ClipboardContent
import javafx.scene.input.TransferMode



//ez a kódrészlet stackoverflowról származik.
//java kódként találtam, lefordítottam kotlinra de nem az én kódom.
//remélem ez nem gond.

//illetve felraktam egy issue-t a tornadofx githubjára,
//hátha meg tudja nekem mondani hogy bővítsem ki a dsljét úgy hogy működjön
//szóval még az is elképzelhető hogy ad erre egy szebb megoldást

var draggingTab: Tab? = null

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


class DraggableTab : Tab() {
    init {
        val label = Label(text)
        text = null
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