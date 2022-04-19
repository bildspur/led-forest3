package ch.bildspur.ledforest.ui.control.scene.control

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.event.EventHandler
import javafx.scene.Camera
import javafx.scene.Node
import javafx.scene.SubScene
import javafx.scene.shape.Sphere
import javafx.scene.transform.Rotate
import javafx.scene.transform.Rotate.X_AXIS
import javafx.scene.transform.Rotate.Y_AXIS
import javafx.scene.transform.Translate
import tornadofx.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

class OrbitControls internal constructor(
    camera: Camera, canvas: SubScene,
    initialAzimuth: Double = 0.0,
    initialLatitude: Double = PI / 6,
    initialDistance: Double = 300.0
) {

    /**
     * Azimuth angle in radians
     */
    val azimuthProperty: SimpleDoubleProperty = SimpleDoubleProperty(initialAzimuth)
    var azimuth: Double by azimuthProperty

    /**
     * Zenith angle in radians
     */
    val zenithProperty: SimpleDoubleProperty = SimpleDoubleProperty(PI / 2 - initialLatitude)
    var zenith: Double by zenithProperty

    var latitude: Double
        get() = PI / 2 - zenithProperty.get()
        set(value) = zenithProperty.set(PI / 2 - value)


    private val baseTranslate = Translate(0.0, 0.0, 0.0)

    var x: Double by baseTranslate.xProperty()
    var y: Double by baseTranslate.yProperty()
    var z: Double by baseTranslate.zProperty()

    private val distanceProperty: SimpleDoubleProperty = SimpleDoubleProperty(initialDistance)

    private val distanceTranslation = Translate().apply {
        zProperty().bind(-distanceProperty)
    }

    var distance: Double by distanceProperty

    private val centering = Translate().apply {
        xProperty().bind(-canvas.widthProperty() / 2)
        yProperty().bind(-canvas.heightProperty() / 2)
    }

    private val yUpRotation = Rotate(180.0, X_AXIS)

    private val azimuthRotation = Rotate().apply {
        axis = Y_AXIS
        angleProperty().bind(azimuthProperty * (180.0 / PI))
    }

    private val zenithRotation = Rotate().apply {
        axisProperty().bind(objectBinding(azimuthProperty) {
            azimuthRotation.inverseTransform(X_AXIS)
        })
        angleProperty().bind(-zenithProperty * (180.0 / PI))
    }

    private val inProgressProperty = SimpleBooleanProperty(false)


    val centerMarker: Node by lazy {
        Sphere(10.0).also {
            it.transforms.setAll(baseTranslate)
            it.visibleProperty().bind(inProgressProperty)
        }
    }

    init {
        camera.transforms.setAll(
            baseTranslate,
            yUpRotation,
            azimuthRotation,
            zenithRotation,
            distanceTranslation,
            centering,
        )

        canvas.apply {
            handleMouse()
        }
    }

    private fun Node.handleMouse() {

        var mousePosX = 0.0
        var mousePosY = 0.0
        var mouseOldX: Double
        var mouseOldY: Double
        var mouseDeltaX: Double
        var mouseDeltaY: Double

        onMousePressed = EventHandler { me ->
            mousePosX = me.sceneX
            mousePosY = me.sceneY
            mouseOldX = me.sceneX
            mouseOldY = me.sceneY
            inProgressProperty.set(true)
        }

        onMouseDragged = EventHandler { me ->
            mouseOldX = mousePosX
            mouseOldY = mousePosY
            mousePosX = me.sceneX
            mousePosY = me.sceneY
            mouseDeltaX = mouseOldX - mousePosX
            mouseDeltaY = mouseOldY - mousePosY

            val modifier = when {
                me.isControlDown -> CONTROL_MULTIPLIER
                me.isShiftDown -> SHIFT_MULTIPLIER
                else -> 1.0
            }

            if (me.isPrimaryButtonDown) {
                azimuth = (azimuth - mouseDeltaX * MOUSE_SPEED * modifier * ROTATION_SPEED)
                zenith = (zenith - mouseDeltaY * MOUSE_SPEED * modifier * ROTATION_SPEED).coerceIn(-PI / 2, PI / 2)
            } else if (me.isSecondaryButtonDown) {
                x += MOUSE_SPEED * modifier * TRACK_SPEED * (mouseDeltaX * cos(azimuth) - mouseDeltaY * sin(azimuth))
                z += MOUSE_SPEED * modifier * TRACK_SPEED * (mouseDeltaX * sin(azimuth) + mouseDeltaY * cos(azimuth))
            }
        }

        onMouseReleased = EventHandler {
            inProgressProperty.set(false)
        }

        onScroll = EventHandler { event ->
            distance = max(-100.0, distance - MOUSE_SPEED * event.deltaY * RESIZE_SPEED)
        }
    }

    companion object {
        private const val CONTROL_MULTIPLIER = 0.1
        private const val SHIFT_MULTIPLIER = 10.0
        private const val MOUSE_SPEED = 0.1
        private const val ROTATION_SPEED = 0.02
        private const val TRACK_SPEED = 20.0
        private const val RESIZE_SPEED = 10.0
    }
}
