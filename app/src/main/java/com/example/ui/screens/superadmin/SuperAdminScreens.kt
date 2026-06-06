package com.example.ui.screens.superadmin

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.viewmodel.GheychiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperAdminScreens(viewModel: GheychiViewModel) {
    val salons by viewModel.allSalons.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        topBar = { TopAppBar(title = { Text("مدیریت سالن‌ها (سوپر ادمین)", fontWeight = FontWeight.Bold) }) }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(salons) { salon ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(20.dp).fillMaxWidth()) {
                        Text("سالن: ${salon.name}", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(8.dp))
                        Text("شماره تماس: ${salon.phone}", color = MaterialTheme.colorScheme.onSurface.copy(0.8f))
                        Spacer(Modifier.height(4.dp))
                        Text("مدارک ارسالی (جواز): ${if (salon.licenseImageUrl != null) "آپلود شده" else "ندارد"}", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Text("وضعیت تأیید: ${if (salon.isApproved) "تأیید شده / فعال" else "در انتظار تأیید"}")

                        Spacer(modifier = Modifier.height(20.dp))
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (!salon.isApproved) {
                                Button(
                                    onClick = { 
                                        viewModel.updateSalonApprovalStatus(salon.id, true)
                                        Toast.makeText(context, "سالن با موفقیت تأیید شد", Toast.LENGTH_SHORT).show()
                                    },
                                    enabled = salon.licenseImageUrl != null,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("تأیید مدارک و فعال‌سازی روی نقشه")
                                }
                                if (salon.licenseImageUrl == null) {
                                    Text("ابتدا باید جواز آپلود شود", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                                }
                            } else {
                                OutlinedButton(
                                    onClick = { viewModel.updateSalonApprovalStatus(salon.id, false) },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("مسدودسازی / لغو تأیید")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
