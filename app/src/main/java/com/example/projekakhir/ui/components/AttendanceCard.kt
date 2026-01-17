package com.example.projekakhir.ui.components


import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.projekakhir.data.model.Attendance
import com.example.projekakhir.utils.DateUtils
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Place // Map Icon
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton


@Composable
fun AttendanceCard(
    item: Attendance,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    onViewMap: (() -> Unit)? = null // New callback
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = item.name)
            Text(text = "NIM: ${item.nim}")
            Text(text = "Status: ${item.status}")
            Text(text = "Lokasi: ${item.location}")
            Text(text = "Waktu: ${DateUtils.formatTimestamp(item.timestamp)}")
            
            if (onEdit != null || onDelete != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (onEdit != null) {
                        IconButton(onClick = onEdit) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }
                    if (onViewMap != null && item.location.contains(",")) {
                        IconButton(onClick = onViewMap) {
                            Icon(imageVector = Icons.Default.Place, contentDescription = "View Map")
                        }
                    }
                    if (onDelete != null) {
                        IconButton(onClick = onDelete) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            }
        }
    }
}