package ch.bildspur.ledforest.artnet.recorder

data class ArtNetSample(var timestamp: Long,
                        var subnet: Int,
                        var universe: Int,
                        var sequenceId: Int,
                        var data: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ArtNetSample

        if (timestamp != other.timestamp) return false
        if (subnet != other.subnet) return false
        if (universe != other.universe) return false
        if (sequenceId != other.sequenceId) return false
        return data.contentEquals(other.data)
    }

    override fun hashCode(): Int {
        var result = timestamp.hashCode()
        result = 31 * result + subnet
        result = 31 * result + universe
        result = 31 * result + sequenceId
        result = 31 * result + data.contentHashCode()
        return result
    }

}