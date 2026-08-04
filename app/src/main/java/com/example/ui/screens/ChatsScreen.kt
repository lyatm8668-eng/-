package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.R
import com.example.nearby.NearbyManager

@Composable
fun ChatsScreen(navController: NavController, nearbyManager: NearbyManager) {
    val connectedDevices by nearbyManager.connectedDevices.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.chats_tab),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
        if (connectedDevices.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text(
                    "لا توجد محادثات نشطة. اذهب إلى الرادار للاتصال بالأجهزة القريبة.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn {
                items(connectedDevices) { device ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate("chat/${device.endpointId}/${device.name}") }
                            .padding(16.dp)
                    ) {
                        Text(text = device.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(text = "متصل الآن", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                    }
                    Divider()
                }
            }
        }
    }
}
