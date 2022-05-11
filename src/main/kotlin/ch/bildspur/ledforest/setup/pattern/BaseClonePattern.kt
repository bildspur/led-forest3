package ch.bildspur.ledforest.setup.pattern

import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.DmxNode
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.model.light.Universe
import ch.bildspur.ledforest.setup.SetupInformation
import ch.bildspur.model.DataModel

abstract class BaseClonePattern(val name: String) : ClonePattern {
    override fun create(project: Project, info: SetupInformation) {
        val addressPerLED = 3
        val tubeAddressSpace = (info.ledsPerTubeCount * addressPerLED)

        var addressCount = 0
        var universeCount = 0

        // pre calculate max tubes per universe
        val maxTubePerUniverse = if (info.isUniverseAutoFill) {
            512 / tubeAddressSpace
        } else {
            info.tubesPerUniverseCount
        }

        val maxAddressPerUniverse = maxTubePerUniverse * tubeAddressSpace

        // create tubes
        for (i in 0 until info.tubeCount) {
            // check if new universe is needed
            if ((addressCount + tubeAddressSpace) > maxAddressPerUniverse) {
                addressCount = 0
                universeCount++
            }

            val tube = Tube()
            tube.addressStart.value = addressCount
            tube.universe.value = universeCount
            tube.ledCount.value = info.ledsPerTubeCount
            setupPosition(i, tube, info)
            project.tubes.add(tube)

            addressCount += tubeAddressSpace
        }

        var currentNode = DmxNode()
        var universeInNodeCount = 1
        project.nodes.add(currentNode)

        // create universes and nodes
        for (i in 0 until universeCount + 1) {
            if (universeInNodeCount > info.universesPerNode) {
                universeInNodeCount = 1

                currentNode = DmxNode()
                project.nodes.add(currentNode)
            }

            currentNode.universes.add(Universe(i))

            universeInNodeCount++
        }
    }

    abstract fun setupPosition(index: Int, tube: Tube, info: SetupInformation)

    override fun toString(): String {
        return name
    }
}