package ModelViews

import javafx.scene.Parent
import javafx.scene.Scene

class WindowsType {
    companion object Main {
        val  width: Double = 600.0
        val height: Double = 400.0

        fun create(parent: Parent) : Scene {
             return Scene(parent, width, height)
        }
    }
}