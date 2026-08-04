package com.example.nearby

import android.content.Context
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class NearbyDevice(val endpointId: String, val name: String, val isConnected: Boolean = false)

class NearbyManager(private val context: Context) {
    private val connectionsClient: ConnectionsClient = Nearby.getConnectionsClient(context)
    private val SERVICE_ID = "com.aistudio.ezatalsarra.p2pmsh"
    private val myName = "User_${(1000..9999).random()}"

    private val _discoveredDevices = MutableStateFlow<List<NearbyDevice>>(emptyList())
    val discoveredDevices: StateFlow<List<NearbyDevice>> = _discoveredDevices.asStateFlow()

    private val _connectedDevices = MutableStateFlow<List<NearbyDevice>>(emptyList())
    val connectedDevices: StateFlow<List<NearbyDevice>> = _connectedDevices.asStateFlow()

    private val _incomingMessages = MutableStateFlow<Pair<String, String>?>(null)
    val incomingMessages: StateFlow<Pair<String, String>?> = _incomingMessages.asStateFlow()

    fun startAdvertising() {
        val options = AdvertisingOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build()
        connectionsClient.startAdvertising(myName, SERVICE_ID, connectionLifecycleCallback, options)
            .addOnSuccessListener { Log.d("NearbyManager", "Advertising started") }
            .addOnFailureListener { e -> Log.e("NearbyManager", "Advertising failed", e) }
    }

    fun startDiscovery() {
        _discoveredDevices.value = emptyList()
        val options = DiscoveryOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build()
        connectionsClient.startDiscovery(SERVICE_ID, endpointDiscoveryCallback, options)
            .addOnSuccessListener { Log.d("NearbyManager", "Discovery started") }
            .addOnFailureListener { e -> Log.e("NearbyManager", "Discovery failed", e) }
    }

    fun stopAll() {
        connectionsClient.stopAdvertising()
        connectionsClient.stopDiscovery()
        connectionsClient.stopAllEndpoints()
        _discoveredDevices.value = emptyList()
        _connectedDevices.value = emptyList()
    }

    fun connectToDevice(endpointId: String) {
        connectionsClient.requestConnection(myName, endpointId, connectionLifecycleCallback)
            .addOnSuccessListener { Log.d("NearbyManager", "Connection requested") }
            .addOnFailureListener { e -> Log.e("NearbyManager", "Connection request failed", e) }
    }

    fun sendMessage(endpointId: String, message: String) {
        connectionsClient.sendPayload(endpointId, Payload.fromBytes(message.toByteArray()))
    }

    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            val device = NearbyDevice(endpointId, info.endpointName)
            if (_discoveredDevices.value.none { it.endpointId == endpointId }) {
                _discoveredDevices.value = _discoveredDevices.value + device
            }
        }

        override fun onEndpointLost(endpointId: String) {
            _discoveredDevices.value = _discoveredDevices.value.filter { it.endpointId != endpointId }
        }
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            connectionsClient.acceptConnection(endpointId, payloadCallback)
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            if (result.status.isSuccess) {
                val deviceName = _discoveredDevices.value.find { it.endpointId == endpointId }?.name ?: "Unknown"
                val device = NearbyDevice(endpointId, deviceName, true)
                _connectedDevices.value = _connectedDevices.value + device
            }
        }

        override fun onDisconnected(endpointId: String) {
            _connectedDevices.value = _connectedDevices.value.filter { it.endpointId != endpointId }
        }
    }

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            if (payload.type == Payload.Type.BYTES) {
                val message = String(payload.asBytes()!!)
                _incomingMessages.value = Pair(endpointId, message)
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {}
    }
}
