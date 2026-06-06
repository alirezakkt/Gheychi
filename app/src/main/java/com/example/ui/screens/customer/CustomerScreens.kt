package com.example.ui.screens.customer

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.BookingRequest
import com.example.data.Salon
import com.example.data.SalonService
import com.example.viewmodel.GheychiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalonListScreen(viewModel: GheychiViewModel, onSalonSelected: (Int) -> Unit) {
    val allSalons by viewModel.allSalons.collectAsStateWithLifecycle()
    val approvedSalons = allSalons.filter { it.isApproved }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("سالن‌های برگزیده", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(approvedSalons) { salon ->
                Card(
                    onClick = { onSalonSelected(salon.id) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(salon.name, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(salon.address, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalonDetailScreen(viewModel: GheychiViewModel, salonId: Int, onBookingRequested: () -> Unit) {
    val salons by viewModel.allSalons.collectAsStateWithLifecycle()
    val salon = salons.find { it.id == salonId }
    val services by viewModel.getServices(salonId).collectAsStateWithLifecycle()

    var showBookingSheet by remember { mutableStateOf(false) }
    var selectedService by remember { mutableStateOf<SalonService?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(salon?.name ?: "سالن", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (salon != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.width(8.dp))
                                Text(salon.address, color = MaterialTheme.colorScheme.onSurface)
                            }
                            Spacer(Modifier.height(12.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccessTime, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.width(8.dp))
                                Text(salon.defaultWorkingHours, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("خدمات لاین", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(services) { service ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(service.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Spacer(Modifier.height(6.dp))
                                Text("مدت زمان: ${service.durationMinutes} دقیقه", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.7f))
                                Text("قیمت: ${service.price}", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                                if (service.requiresDeposit) {
                                    Spacer(Modifier.height(6.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(14.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("نیازمند بیعانه", fontSize = 12.sp, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Button(
                                onClick = { 
                                    selectedService = service
                                    showBookingSheet = true
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("رزرو", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    }
                }
            }
        }

        if (showBookingSheet && selectedService != null) {
            ModalBottomSheet(
                onDismissRequest = { showBookingSheet = false },
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                BookingSheetContent(
                    service = selectedService!!,
                    onBook = { date, time, phone, colorHex ->
                        viewModel.submitBookingRequest(
                            salonId = salonId,
                            serviceId = selectedService!!.id,
                            phone = phone,
                            name = "مشتری",
                            date = date,
                            time = time,
                            notes = "",
                            colorHex = colorHex
                        )
                        showBookingSheet = false
                        onBookingRequested()
                    }
                )
            }
        }
    }
}

val sampleColors = listOf(
    "#F5D0A9" to "بلوند روشن",
    "#C49A6C" to "کاراملی",
    "#7D5333" to "شکلاتی",
    "#593122" to "فندقی تیره",
    "#2F1B12" to "مشکی پرکلاغی",
    "#913831" to "شرابی",
    "#D8C1BD" to "مرواریدی"
)

@Composable
fun BookingSheetContent(service: SalonService, onBook: (String, String, String, String?) -> Unit) {
    var date by remember { mutableStateOf("1402/10/05") }
    var time by remember { mutableStateOf("16:00") }
    var phone by remember { mutableStateOf("") }
    var selectedColorHex by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.padding(24.dp).fillMaxWidth()) {
        Text("ثبت درخواست نوبت", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = MaterialTheme.colorScheme.primary)
        Text(service.name, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.7f))
        Spacer(Modifier.height(24.dp))
        
        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("تاریخ (مثال: 1402/10/05)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = time,
            onValueChange = { time = it },
            label = { Text("ساعت (مثال: 16:00)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("شماره موبایل ارتباطی") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        if (service.isColorService) {
            Spacer(Modifier.height(24.dp))
            Text("انتخاب تناژ رنگ مو (اختیاری)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(16.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(horizontal = 4.dp)) {
                items(sampleColors) { (hex, name) ->
                    val isSelected = selectedColorHex == hex
                    val scale by animateFloatAsState(if (isSelected) 1.2f else 1f)
                    val shadow by animateFloatAsState(if (isSelected) 8f else 2f)

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            modifier = Modifier
                                .size(56.dp)
                                .scale(scale)
                                .clickable { selectedColorHex = if (isSelected) null else hex },
                            shape = CircleShape,
                            color = Color(android.graphics.Color.parseColor(hex)),
                            shadowElevation = shadow.dp,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(3.dp, MaterialTheme.colorScheme.primary) else null
                        ) {}
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = name,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(0.7f)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))
        
        Button(
            onClick = { onBook(date, time, phone, selectedColorHex) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = phone.isNotBlank(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("تایید و ارسال درخواست", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(viewModel: GheychiViewModel) {
    val allSalons by viewModel.allSalons.collectAsStateWithLifecycle()
    val salonMap = allSalons.associateBy { it.id }
    val myBookings by viewModel.getSalonBookings(viewModel.currentSalonId.value ?: -1).collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تاریخچه نوبت‌ها", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        LazyColumn(contentPadding = padding, modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            items(myBookings) { booking ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(salonMap[booking.salonId]?.name ?: "سالن ناشناس", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("زمان مراجعه: ${booking.date} - ${booking.time}", color = MaterialTheme.colorScheme.onSurface.copy(0.8f))
                        
                        if (booking.selectedColorHex != null) {
                            val colorName = sampleColors.find { it.first == booking.selectedColorHex }?.second ?: "رنگ سفارشی"
                            Spacer(Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("رنگ انتخابی: ", fontSize = 14.sp)
                                Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(Color(android.graphics.Color.parseColor(booking.selectedColorHex!!))))
                                Spacer(Modifier.width(8.dp))
                                Text(colorName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("وضعیت نوبت:", fontSize = 14.sp)
                            Text(booking.status, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        
                        if (booking.depositStatus != "NO_DEPOSIT") {
                            Spacer(Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("بیعانه:", fontSize = 14.sp)
                                Text(booking.depositStatus, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                            }
                        }
                        
                        if (booking.depositStatus == "REQUESTED") {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    android.widget.Toast.makeText(context, "در حال شبیه‌سازی آپلود تصویر...", android.widget.Toast.LENGTH_SHORT).show()
                                    viewModel.updateBookingStatus(booking, booking.status, "RECEIPT_UPLOADED")
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("آپلود تصویر رسید (شبیه‌سازی)")
                            }
                        }
                    }
                }
            }
            if (myBookings.isEmpty()) {
                item {
                    Text("هنوز رزروی ثبت نکرده‌اید.", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onBackground.copy(0.6f))
                }
            }
        }
    }
}
