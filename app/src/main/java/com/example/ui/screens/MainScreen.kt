package com.example.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.R
import com.example.nearby.NearbyManager

@Composable
fun MainScreen(navController: NavController, nearbyManager: NearbyManager) {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf(
        Triple(stringResource(R.string.chats_tab), Icons.Default.Chat, 0),
        Triple(stringResource(R.string.status_tab), Icons.Default.Star, 1),
        Triple(stringResource(R.string.radar_tab), Icons.Default.Radar, 2),
        Triple(stringResource(R.string.settings_tab), Icons.Default.Settings, 3)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(item.second, contentDescription = item.first) },
                        label = { Text(item.first) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index }
                    )
                }
            }
        }
    ) { innerPadding ->
        Modifier.padding(innerPadding)
        when (selectedItem) {
            0 -> ChatsScreen(navController, nearbyManager)
            1 -> StatusScreen()
            2 -> RadarScreen(nearbyManager)
            3 -> SettingsScreen()
        }
    }
}
