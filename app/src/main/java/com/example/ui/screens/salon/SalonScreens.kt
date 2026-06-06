package com.example.ui.screens.salon

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.viewmodel.GheychiViewModel
import com.example.ui.screens.customer.sampleColors

@Composable
fun SalonOnboardingScreen(viewModel: GheychiViewModel) {
    val salonId by viewModel.currentSalonId.collectAsStateWithLifecycle()
    val salons by viewModel.allSalons.collectAsStateWithLifecycle()
    val salon = salons.find { it.id == salonId }
    val context = androidx.compose.ui.platform.LocalContext.current

    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("وضعیت ثبت‌نام سالن", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

            if (salon?.licenseImageUrl == null) {
                Text(
                    "برای شروع فعالیت و نمایش سالن در لیست، باید جواز کسب خود را آپلود کنید تا توسط مدیریت قیچی تأیید شود.",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground.copy(0.8f)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { 
                        viewModel.submitSalonLicense(salonId ?: -1)
                        android.widget.Toast.makeText(context, "جواز با موفقیت آپلود شد.", android.widget.Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Image, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("آپلود تصویر جواز کسب (شبیه‌سازی)", fontWeight = FontWeight.Bold)
                }
            } else {
                Text(
                    "جواز کسب شما باموفقیت آپلود شد.",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    "در حال حاضر حساب شما در انتظار تأیید توسط مدیریت است. لطفاً شکیبا باشید.",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground.copy(0.7f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalonRequestsScreen(viewModel: GheychiViewModel) {
    val salonId by viewModel.currentSalonId.collectAsStateWithLifecycle()
    val requests by viewModel.getSalonBookings(salonId ?: -1).collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("مدیریت نوبت‌ها", fontWeight = FontWeight.Bold) }) }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(requests) { request ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(20.dp).fillMaxWidth()) {
                        Text("مشتری: ${request.customerPhone}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("تاریخ: ${request.date} ساعت: ${request.time}", color = MaterialTheme.colorScheme.onSurface.copy(0.8f))
                        
                        if (request.selectedColorHex != null) {
                            val colorName = sampleColors.find { it.first == request.selectedColorHex }?.second ?: "نامشخص"
                            Spacer(Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("رنگ درخواستی: ")
                                Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(Color(android.graphics.Color.parseColor(request.selectedColorHex!!))))
                                Spacer(Modifier.width(8.dp))
                                Text(colorName, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("وضعیت فعلی:", fontSize = 14.sp)
                            Text(request.status, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("وضعیت بیعانه:", fontSize = 14.sp)
                            Text(request.depositStatus, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (request.status == "PENDING") {
                                Button(onClick = { viewModel.updateBookingStatus(request, "CONFIRMED") }, shape = RoundedCornerShape(12.dp)) {
                                    Text("تایید نوبت")
                                }
                                OutlinedButton(onClick = { viewModel.updateBookingStatus(request, "REJECTED") }, shape = RoundedCornerShape(12.dp)) {
                                    Text("رد")
                                }
                            }
                            if (request.depositStatus == "NO_DEPOSIT" && request.status != "REJECTED") {
                                TextButton(onClick = { viewModel.updateBookingStatus(request, request.status, "REQUESTED") }) {
                                    Text("درخواست بیعانه")
                                }
                            } else if (request.depositStatus == "REQUESTED") {
                                TextButton(onClick = { viewModel.updateBookingStatus(request, request.status, "CONFIRMED") }) {
                                    Text("تایید دستی بیعانه")
                                }
                            } else if (request.depositStatus == "RECEIPT_UPLOADED") {
                                Button(onClick = { viewModel.updateBookingStatus(request, request.status, "CONFIRMED") }, shape = RoundedCornerShape(12.dp)) {
                                    Text("تایید رسید ارسالی")
                                }
                            }
                        }
                    }
                }
            }
            if (requests.isEmpty()) {
                item {
                    Text("درخواستی وجود ندارد.", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onBackground.copy(0.6f))
                }
            }
        }
    }
}
