package ModelViews

import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Alert

class WindowsType {
    companion object Main {
        val width: Double   = 800.0
        val height: Double  = 600.0

        fun create(parent: Parent) : Scene {
             return Scene(parent, width, height)
        }
    }
}

class Alerts {
    companion object simple {
        fun show(alertType: Alert.AlertType, title: String, contentText: String) {
            var alert = Alert(alertType)
            alert.setTitle(title)
            alert.setHeaderText(null)
            alert.setContentText(contentText)
            alert.showAndWait()
        }
    }
}