package ch.bildspur.ledforest.artnet.recorder

import java.nio.ByteBuffer
import java.nio.ByteOrder


class ArtNetBuffer(var comment: String = "") {
    // meta
    var speed = 1.0f

    // data
    val samples = mutableListOf<ArtNetSample>()

    fun clear() {
        samples.clear()
        speed = 1.0f
        comment = ""
    }

    fun write(compressed: Boolean = false): ByteBuffer {
        // write payload
        val payloadLength = (samples.size * (4 * Int.SIZE_BYTES + Long.SIZE_BYTES)) + samples.sumOf { it.data.size }
        val payload = ByteBuffer.allocate(payloadLength)
        payload.order(ByteOrder.LITTLE_ENDIAN)

        samples.forEach {
            payload.putLong(it.timestamp)
            payload.putInt(it.subnet)
            payload.putInt(it.universe)
            payload.putInt(it.sequenceId)
            payload.putInt(it.data.size)
            payload.put(it.data)
        }

        payload.position(0)
        var payloadRaw = payload.getBytes(payload.limit())

        // compression
        if (compressed) {
            payloadRaw = payloadRaw.compress()
        }

        // write header
        val rawComment = comment.toByteArray(Charsets.UTF_8)
        val headerLength = Int.SIZE_BYTES + Int.SIZE_BYTES + Int.SIZE_BYTES + 4 + Int.SIZE_BYTES + rawComment.size

        val data = ByteBuffer.allocate(headerLength + payloadRaw.size)
        data.order(ByteOrder.LITTLE_ENDIAN)

        val flags = compressed.toFlag(0)

        data.putInt(flags)
        data.putInt(samples.size)
        data.putInt(payloadLength)
        data.putFloat(speed)
        data.putInt(rawComment.size)
        data.put(rawComment)

        data.put(payloadRaw)

        return data
    }

    fun read(data: ByteBuffer) {
        data.order(ByteOrder.LITTLE_ENDIAN)
        data.position(0)

        // read header
        val flags = data.int
        val compressed = flags.getFlag(0)

        var sampleCount = data.int
        sampleCount = if (sampleCount < 0) Int.MAX_VALUE else sampleCount

        val payloadLength = data.int

        this.speed = data.float
        this.comment = data.getBytes(data.int).toString(Charsets.UTF_8)

        // extract payload
        var payloadRaw = data.getBytes(data.limit() - data.position())

        if (compressed)
            payloadRaw = payloadRaw.uncompress()

        val payload = ByteBuffer.wrap(payloadRaw)
        payload.order(ByteOrder.LITTLE_ENDIAN)

        var count = 0
        while (count < sampleCount && payload.hasRemaining()) {
            val timestamp = payload.long
            val subnet = payload.int
            val universe = payload.int
            val sequenceId = payload.int
            val length = payload.int
            val packetData = payload.getBytes(length)

            samples.add(ArtNetSample(timestamp, subnet, universe, sequenceId, packetData))
            count++
        }
    }
}