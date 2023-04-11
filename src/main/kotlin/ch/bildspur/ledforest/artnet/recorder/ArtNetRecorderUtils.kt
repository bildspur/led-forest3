package ch.bildspur.ledforest.artnet.recorder

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

fun ByteArray.compress(): ByteArray {
    var result = byteArrayOf()
    try {
        ByteArrayOutputStream(this.size).use { bos ->
            GZIPOutputStream(bos).use { gzipOS ->
                gzipOS.write(this)
                gzipOS.close()
                result = bos.toByteArray()
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return result
}

fun ByteArray.uncompress(): ByteArray {
    var result = byteArrayOf()
    try {
        ByteArrayInputStream(this).use { bis ->
            ByteArrayOutputStream().use { bos ->
                GZIPInputStream(bis).use { gzipIS ->
                    val buffer = ByteArray(1024)
                    var len: Int
                    while (gzipIS.read(buffer).also { len = it } != -1) {
                        bos.write(buffer, 0, len)
                    }
                    result = bos.toByteArray()
                }
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return result
}

/**
 * Reads byte array out of byte buffer
 */
fun ByteBuffer.getBytes(length : Int) : ByteArray {
    val subpart = this.slice().limit(length)
    subpart.order(ByteOrder.LITTLE_ENDIAN)
    val data = ByteArray(subpart.remaining())
    subpart.get(data)
    this.position(this.position() + length);
    return data
}

fun Boolean.toFlag(position : Int) : Int  {
    return (if(this) 1 else 0) shl position
}

fun Int.getFlag(position: Int) : Boolean {
    // todo: check if this is corrct?! (b & (1 << bitNumber)) != 0;
    return this shr position and 0xF == 1
}