package ch.bildspur.ledforest.controller

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.util.toFloat
import netP5.NetAddress
import oscP5.OscMessage
import oscP5.OscP5
import java.io.IOException
import java.net.InetAddress
import javax.jmdns.JmDNS
import javax.jmdns.ServiceInfo
import kotlin.concurrent.thread


/**
 * Created by cansik on 09.07.17.
 */
class OscController(internal var sketch: Sketch) {
    companion object {
        @JvmStatic val INCOMING_PORT = 9000
        @JvmStatic val OUTGOING_PORT = 8000
        @JvmStatic val VEZER_PORT = 1234
    }

    @Volatile
    var isSetup = false

    lateinit var apps: NetAddress
    lateinit var vezer: NetAddress
    lateinit var jmdns: JmDNS

    lateinit var osc: OscP5

    fun setup() {
        osc = OscP5(this, INCOMING_PORT)
        apps = NetAddress("255.255.255.255", OUTGOING_PORT)
        vezer = NetAddress("127.0.0.1", VEZER_PORT)

        thread {
            setupZeroConf()
        }

        isSetup = true
    }

    fun stop() {
        jmdns.unregisterAllServices()
    }

    private fun setupZeroConf() {
        try {
            println("setting up zero conf...")
            val address = InetAddress.getLocalHost()
            jmdns = JmDNS.create(address)
            jmdns.registerService(ServiceInfo.create("_osc._udp.", Sketch.NAME, INCOMING_PORT, ""))
            println("zero conf running!")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun oscEvent(msg: OscMessage) {
        when (msg.addrPattern()) {
            "/ledforest/remote/interaction" -> {
                sketch.remote.processCommand('x')
            }
        }

        updateOSCApp()
    }

    fun updateOSCApp() {
        sendMessage("/ledforest/remote/interaction", sketch.isInteractionOn.value.toFloat())
    }

    fun sendMessage(ip: NetAddress, address: String, value: Float) {
        val m = OscMessage(address)
        m.add(value)
        osc.send(m, ip)
    }

    fun sendMessage(address: String, value: Float) {
        val m = OscMessage(address)
        m.add(value)
        osc.send(m, apps)
    }

    fun sendMessage(address: String, value: String) {
        val m = OscMessage(address)
        m.add(value)
        osc.send(m, apps)
    }
}