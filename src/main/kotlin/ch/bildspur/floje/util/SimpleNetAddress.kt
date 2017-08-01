package ch.bildspur.floje.util

import netP5.NetAddress
import java.net.InetAddress

/**
 * Created by cansik on 17.07.17.
 */
class SimpleNetAddress : NetAddress {
    constructor(p0: String?, p1: Int) : super(p0, p1)
    constructor(p0: NetAddress?) : super(p0)
    constructor(p0: InetAddress?, p1: Int) : super(p0, p1)


    fun updatePort(port: Int) {
        this.port = port
    }

    fun updateAddress(inetaddress: InetAddress) {
        this.inetaddress = inetaddress
        this.isValid = true
    }
}