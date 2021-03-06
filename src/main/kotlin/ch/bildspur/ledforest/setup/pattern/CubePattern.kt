package ch.bildspur.ledforest.setup.pattern

import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.model.light.TubeTag
import ch.bildspur.ledforest.model.light.Universe
import ch.bildspur.ledforest.setup.SetupInformation
import processing.core.PApplet
import processing.core.PVector

class CubePattern : SquarePattern("Square with Cube") {

    var cubeSize = 2.5f

    override fun create(project: Project, info: SetupInformation) {
        super.create(project, info)
        addCube(project, info)
    }

    fun addCube(project: Project, info: SetupInformation) {
        // calculate addresses
        val addressPerLED = 3
        val tubeAddressSpace = (info.ledsPerTubeCount * addressPerLED)
        var tubeIndex = 0

        val topTag = TubeTag.CubeTop
        val bottomTag = TubeTag.CubeBottom

        // find free universe
        val node = project.nodes.first()
        val universeId = node.universes.map { it.id.value }.maxOrNull()!! + 1

        val universe1 = Universe(universeId)
        val universe2 = Universe(universeId + 1)

        node.universes.addAll(listOf(universe1, universe2))

        // calculate positions
        val dc = cubeSize
        val rc = cubeSize / 2f

        // ground
        project.tubes.add(Tube().init(universe1.id.value, (tubeAddressSpace * tubeIndex++), bottomTag, -rc, rc, dc, 180f))
        project.tubes.add(Tube().init(universe1.id.value, (tubeAddressSpace * tubeIndex++), bottomTag, rc, rc, dc, 180f))
        project.tubes.add(Tube().init(universe1.id.value, (tubeAddressSpace * tubeIndex++), bottomTag, rc, -rc, dc, 180f))
        project.tubes.add(Tube().init(universe1.id.value, (tubeAddressSpace * tubeIndex), bottomTag, -rc, -rc, dc, 180f))

        // top
        tubeIndex = 0
        project.tubes.add(Tube().init(universe2.id.value, (tubeAddressSpace * tubeIndex++), topTag, -rc, rc, dc, 90f))
        project.tubes.add(Tube().init(universe2.id.value, (tubeAddressSpace * tubeIndex++), topTag, rc, rc, dc, 0f, -90f))
        project.tubes.add(Tube().init(universe2.id.value, (tubeAddressSpace * tubeIndex++), topTag, rc, -rc, dc, -90f))
        project.tubes.add(Tube().init(universe2.id.value, (tubeAddressSpace * tubeIndex), topTag, -rc, -rc, dc, 0f, 90f))

    }

    private fun Tube.init(universe: Int, addressStart: Int, tag: TubeTag,
                          x: Float = 0f, y: Float = 0f, z: Float = 0f,
                          rx: Float = 0f, ry: Float = 0f, rz: Float = 0f): Tube {
        this.universe.value = universe
        this.addressStart.value = addressStart
        this.position.value = PVector(x, y, z)
        this.rotation.value = PVector(PApplet.radians(rx), PApplet.radians(ry), PApplet.radians(rz))
        this.tag.value = tag
        this.initLEDs()
        return this
    }
}