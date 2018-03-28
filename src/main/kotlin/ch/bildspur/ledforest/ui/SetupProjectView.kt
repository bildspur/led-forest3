package ch.bildspur.ledforest.ui

import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.setup.SetupInformation
import ch.bildspur.ledforest.setup.pattern.ClonePattern
import ch.bildspur.ledforest.setup.pattern.SquarePattern
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.scene.control.*
import javafx.stage.Stage

class SetupProjectView {
    lateinit var primaryStage: Stage
    lateinit var project: Project

    var info = SetupInformation()
    var initNewProject = false

    lateinit var statusLabel: Label

    lateinit var projectName: TextField
    lateinit var tubeCount: Spinner<Int>
    lateinit var areTubesInverted: CheckBox
    lateinit var ledsPerTubeCount: Spinner<Int>
    lateinit var tubesPerUniverseCount: Spinner<Int>
    lateinit var isUniverseAutoFill: CheckBox
    lateinit var universesPerNode: Spinner<Int>
    lateinit var space: Spinner<Int>
    lateinit var flipXY: CheckBox
    lateinit var clonePattern: ComboBox<ClonePattern>

    val clonePatterns = listOf(SquarePattern())

    data class PreCalculation(var ledCount: Int = 0, var tubeCount: Int = 0, var universeCount: Int = 0, var nodeCount: Int = 0)

    fun setupView() {
        // add patterns
        clonePattern.items.addAll(clonePatterns)

        // setup listeners
        projectName.setOnAction {
            info.projectName = projectName.text
            updateWindow()
        }

        tubeCount.valueProperty().addListener { _, _, newValue ->
            info.tubeCount = newValue
            updateWindow()
        }

        areTubesInverted.setOnAction {
            info.areTubesInverted = areTubesInverted.isSelected
            updateWindow()
        }

        ledsPerTubeCount.valueProperty().addListener { _, _, newValue ->
            info.ledsPerTubeCount = newValue
            updateWindow()
        }

        tubesPerUniverseCount.valueProperty().addListener { _, _, newValue ->
            info.tubesPerUniverseCount = newValue
            updateWindow()
        }

        isUniverseAutoFill.setOnAction {
            info.isUniverseAutoFill = isUniverseAutoFill.isSelected
            tubesPerUniverseCount.isDisable = isUniverseAutoFill.isSelected
            updateWindow()
        }

        universesPerNode.valueProperty().addListener { _, _, newValue ->
            info.universesPerNode = newValue
            updateWindow()
        }

        clonePattern.setOnAction {
            info.clonePattern = clonePattern.selectionModel.selectedItem
            updateWindow()
        }

        space.valueProperty().addListener { _, _, newValue ->
            info.space = newValue
            updateWindow()
        }

        flipXY.setOnAction {
            info.flipXY = flipXY.isSelected
            updateWindow()
        }

        // set initial values
        projectName.text = info.projectName
        tubeCount.valueFactory.value = info.tubeCount
        areTubesInverted.isSelected = info.areTubesInverted
        ledsPerTubeCount.valueFactory.value = info.ledsPerTubeCount
        tubesPerUniverseCount.valueFactory.value = info.tubesPerUniverseCount
        isUniverseAutoFill.isSelected = info.isUniverseAutoFill
        universesPerNode.valueFactory.value = info.universesPerNode
        clonePattern.selectionModel.select(info.clonePattern)
        space.valueFactory.value = info.space
        flipXY.isSelected = info.flipXY

        // set special unit values
        tubesPerUniverseCount.isDisable = isUniverseAutoFill.isSelected

        updateWindow()
    }

    fun onCancelClicked(e: ActionEvent) {
        closeWindow()
    }

    fun onCreateClicked(e: ActionEvent) {
        initNewProject = true
        closeWindow()
    }

    private fun updateWindow() {
        Platform.runLater({
            val c = runPreCalculation()
            statusLabel.text = "A: ${c.ledCount * 3} | LED: ${c.ledCount} | T: ${c.tubeCount} | U: ${c.universeCount} | N: ${c.nodeCount}"
        })
    }

    private fun runPreCalculation(): PreCalculation {
        val result = PreCalculation()
        val tempProject = Project()
        info.clonePattern.create(tempProject, info)

        // create output
        result.ledCount = tempProject.tubes.sumBy { it.leds.size }
        result.tubeCount = tempProject.tubes.size
        result.universeCount = tempProject.nodes.sumBy { it.universes.size }
        result.nodeCount = tempProject.nodes.size

        return result
    }

    private fun closeWindow() {
        primaryStage.close()
    }
}