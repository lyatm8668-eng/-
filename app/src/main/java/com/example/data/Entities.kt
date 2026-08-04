package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val senderId: String,
    val receiverId: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSelfDestruct: Boolean = false,
    val isGhost: Boolean = false
)

@Entity(tableName = "devices")
data class Device(
    @PrimaryKey val endpointId: String,
    val name: String,
    val lastSeen: Long = System.currentTimeMillis()
)
