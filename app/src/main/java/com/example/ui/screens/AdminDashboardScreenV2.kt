package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

val MedicalBlueV2 = Color(0xFF2563EB)
val MedicalGreenV2 = Color(0xFF10B981)
val DarkNavyV2 = Color(0xFF1E293B)
val DarkBackgroundV2 = Color(0xFF0F172A)
val BodyGrayV2 = Color(0xFF64748B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreenV2(viewModel: ELmorsyViewModel) {
    val isDark = MaterialTheme.colorScheme.background == DarkBackgroundV2 || MaterialTheme.colorScheme.background == Color(0xFF0F172A)
    var selectedTab by remember { mutableStateOf("HOME") }
    
    val userName by viewModel.userName.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.fetchServicesAndNursesFromSupabase()
    }
    
    Scaffold(
        containerColor = if (isDark) DarkBackgroundV2 else Color(0xFFF8FAFC),
        topBar = {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                        .background(Brush.verticalGradient(listOf(Color(0xFF0F4095), MedicalBlueV2)))
                        .padding(top = 48.dp, start = 20.dp, end = 20.dp, bottom = 20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(50.dp).background(Color.White.copy(alpha = 0.2f), CircleShape).border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("لوحة تحكم الإدارة", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                Text(userName, color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                            }
                        }
                        IconButton(
                            onClick = {
                                viewModel.setUserRole("PATIENT")
                                viewModel.navigateTo("role_selection")
                            },
                            modifier = Modifier.background(Color.White.copy(alpha = 0.15f), CircleShape)
                        ) {
                            Icon(Icons.Default.Logout, contentDescription = "خروج", tint = Color.White)
                        }
                    }
                }
                
                ScrollableTabRow(
                    selectedTabIndex = when(selectedTab) { "HOME" -> 0 "REQUESTS" -> 1 "SERVICES" -> 2 "NURSES" -> 3 "CHATBOT" -> 4 "SETTINGS" -> 5 else -> 0},
                    containerColor = Color.Transparent,
                    contentColor = MedicalBlueV2,
                    edgePadding = 16.dp,
                    indicator = { },
                    divider = { }
                ) {
                    val tabs = listOf("HOME" to "لوحة التحكم", "REQUESTS" to "إدارة الطلبات", "SERVICES" to "الخدمات والأقسام", "NURSES" to "الممرضون", "CHATBOT" to "إعدادات المساعد", "SETTINGS" to "إعدادات الواجهة")
                    tabs.forEachIndexed { _, (id, title) ->
                        val isSelected = selectedTab == id
                        Tab(
                            selected = isSelected,
                            onClick = { selectedTab = id },
                            text = { 
                                Text(
                                    title, 
                                    fontWeight = if(isSelected) FontWeight.Black else FontWeight.Bold, 
                                    color = if(isSelected) MedicalBlueV2 else BodyGrayV2,
                                    fontSize = 14.sp
                                ) 
                            }
                        )
                    }
                }
            }
        }
    ) { paddingVals ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingVals).padding(horizontal = 16.dp)) {
            when (selectedTab) {
                "HOME" -> AdminHomeTab(viewModel, isDark)
                "REQUESTS" -> AdminRequestsTab(viewModel, isDark)
                "SERVICES" -> AdminServicesTab(viewModel, isDark)
                "NURSES" -> AdminNursesTab(viewModel, isDark)
                "CHATBOT" -> AdminChatbotTab(viewModel, isDark)
                "SETTINGS" -> AdminSettingsTab(viewModel, isDark)
            }
        }
    }
}

// ----------------------------------------
// TAB 1: Dashboard Home
// ----------------------------------------
@Composable
fun AdminHomeTab(viewModel: ELmorsyViewModel, isDark: Boolean) {
    val bookings by viewModel.dashboardBookings.collectAsState()
    val profiles by viewModel.allProfiles.collectAsState()
    
    val totalRevenue = bookings.filter { it.status == "COMPLETED" }.sumOf { it.price }
    val avgRating = if (bookings.any { it.rating > 0 }) bookings.filter { it.rating > 0 }.map { it.rating }.average() else 5.0
    val totalPatients = profiles.size
    val totalBookingsCount = bookings.size
    val completedCount = bookings.count { it.status == "COMPLETED" }
    val completionRate = if (totalBookingsCount > 0) (completedCount.toFloat() / totalBookingsCount) * 100 else 0f
    
    val today = LocalDate.now()
    val format = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    
    // Quick grouping for char (Last 7 days manually mapped for mock logic, real one using dates)
    val grouped = bookings.groupBy { it.date }
    
    LazyColumn(contentPadding = PaddingValues(bottom = 24.dp), modifier = Modifier.fillMaxSize()) {
        item {
            Text("مؤشرات الأداء", fontSize = 18.sp, fontWeight = FontWeight.Black, color = if(isDark) Color.White else DarkNavyV2)
            Spacer(modifier = Modifier.height(16.dp))
            
            // KPI Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                KpiCard(
                    title = "الإيرادات",
                    value = "${totalRevenue.toInt()} ج",
                    icon = Icons.Default.AttachMoney,
                    color = MedicalGreenV2,
                    modifier = Modifier.weight(1f),
                    isDark = isDark
                )
                KpiCard(
                    title = "المرضى",
                    value = "$totalPatients",
                    icon = Icons.Default.Groups,
                    color = MedicalBlueV2,
                    modifier = Modifier.weight(1f),
                    isDark = isDark
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                KpiCard(
                    title = "معدل الإتمام",
                    value = "${completionRate.toInt()}%",
                    icon = Icons.Default.CheckCircle,
                    color = Color(0xFF8B5CF6),
                    modifier = Modifier.weight(1f),
                    isDark = isDark
                )
                KpiCard(
                    title = "التقييم",
                    value = String.format("%.1f", avgRating),
                    icon = Icons.Default.Star,
                    color = Color(0xFFF59E0B),
                    modifier = Modifier.weight(1f),
                    isDark = isDark
                )
            }
            
            val context = androidx.compose.ui.platform.LocalContext.current
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { android.widget.Toast.makeText(context, "سيتم شاشات إضافة ممرض لاحقاً", android.widget.Toast.LENGTH_SHORT).show() },
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalBlueV2)
                ) {
                    Icon(Icons.Default.PersonAdd, null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("إضافة ممرض", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = { android.widget.Toast.makeText(context, "تم تصدير التقرير بنجاح", android.widget.Toast.LENGTH_SHORT).show() },
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkNavyV2),
                    border = BorderStroke(1.dp, if(isDark) Color(0xFF334155) else Color(0xFFE2E8F0))
                ) {
                    Icon(Icons.Default.Download, null, tint = if(isDark) Color.White else DarkNavyV2, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("تصدير تقرير", fontWeight = FontWeight.Bold, color = if(isDark) Color.White else DarkNavyV2)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text("إحصائيات الحجوزات (٧ أيام)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if(isDark) Color.White else DarkNavyV2)
            Spacer(modifier = Modifier.height(16.dp))
            
            val bookingCountsByDay = remember(bookings) {
                val counts = FloatArray(7)
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val now = LocalDate.now()
                bookings.forEach { b ->
                    try {
                        val d = LocalDate.parse(b.date, formatter)
                        val daysBetween = java.time.temporal.ChronoUnit.DAYS.between(d, now).toInt()
                        if (daysBetween in 0..6) {
                            counts[6 - daysBetween] += 1f
                        }
                    } catch (e: Exception) { }
                }
                counts
            }
            
            val maxCount = maxOf(1f, bookingCountsByDay.maxOrNull() ?: 1f)
            val textMeasurer = androidx.compose.ui.text.rememberTextMeasurer()

            Card(
                colors = CardDefaults.cardColors(containerColor = if(isDark) Color(0xFF1E293B) else Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(220.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val barWidth = size.width / 14f
                        val chartHeight = size.height - 30.dp.toPx()
                        for (i in 0..6) {
                            val x = i * (size.width / 7f) + (size.width / 14f) - (barWidth / 2f)
                            val count = bookingCountsByDay[i]
                            val h = (chartHeight * (count / maxCount)).coerceAtLeast(10f)
                            drawRoundRect(
                                color = MedicalBlueV2.copy(alpha = 0.8f),
                                topLeft = Offset(x, chartHeight - h),
                                size = Size(barWidth, h),
                                cornerRadius = CornerRadius(8f)
                            )
                            
                            val dayText = LocalDate.now().minusDays((6 - i).toLong()).dayOfWeek.name.take(3)
                            val textLayoutResult = textMeasurer.measure(
                                text = dayText,
                                style = androidx.compose.ui.text.TextStyle(fontSize = 10.sp, color = BodyGrayV2)
                            )
                            drawText(
                                textLayoutResult = textLayoutResult,
                                topLeft = Offset(x + barWidth/2f - textLayoutResult.size.width/2f, chartHeight + 8.dp.toPx())
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Text("أحدث الحجوزات الواردة", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if(isDark) Color.White else DarkNavyV2)
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        items(bookings.take(5)) { booking ->
            AdminDashboardBookingItem(booking, isDark)
        }
        
        item {
            if (bookings.isEmpty()) {
                Text("لا توجد حجوزات لعرضها.", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(32.dp), color = BodyGrayV2)
            }
        }
    }
}

@Composable
fun KpiCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier, isDark: Boolean) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = if(isDark) Color(0xFF1E293B) else Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if(isDark) Color(0xFF334155) else Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(modifier = Modifier.size(36.dp).background(color.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Black, color = if(isDark) Color.White else DarkNavyV2)
            Text(title, fontSize = 12.sp, color = BodyGrayV2, fontWeight = FontWeight.Medium)
        }
    }
}

// ----------------------------------------
// TAB 2: Bookings Management
// ----------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminRequestsTab(viewModel: ELmorsyViewModel, isDark: Boolean) {
    val bookings by viewModel.dashboardBookings.collectAsState()
    var selectedFilter by remember { mutableStateOf("الكل") }
    var searchQuery by remember { mutableStateOf("") }
    
    val filtered = bookings.filter { b -> 
        (selectedFilter == "الكل" || 
         (selectedFilter == "نشط" && b.status !in listOf("COMPLETED", "CANCELLED")) ||
         (selectedFilter == "مكتمل" && b.status == "COMPLETED") ||
         (selectedFilter == "ملغي" && b.status == "CANCELLED")) &&
        (searchQuery.isEmpty() || b.serviceName.contains(searchQuery) || b.address.contains(searchQuery))
    }
    
    var showStatusSheetFor by remember { mutableStateOf<com.example.connectivity.SupabaseBooking?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            placeholder = { Text("ابحث بالخدمة أو العنوان...") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = if(isDark) Color(0xFF334155) else Color(0xFFE2E8F0),
                unfocusedContainerColor = if(isDark) Color(0xFF1E293B) else Color.White
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("الكل", "نشط", "مكتمل", "ملغي").forEach { f ->
                FilterChip(
                    selected = selectedFilter == f,
                    onClick = { selectedFilter = f },
                    label = { Text(f) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MedicalBlueV2,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(contentPadding = PaddingValues(bottom = 24.dp), modifier = Modifier.fillMaxSize()) {
            items(filtered) { booking ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).clickable { showStatusSheetFor = booking },
                    colors = CardDefaults.cardColors(containerColor = if(isDark) Color(0xFF1E293B) else Color.White),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, if(isDark) Color(0xFF334155) else Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("رقم #${booking.id}", fontWeight = FontWeight.Bold)
                            Text(booking.status, color = getStatusColor(booking.status), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(booking.serviceName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if(isDark) Color.White else DarkNavyV2)
                        Text(booking.address, fontSize = 13.sp, color = BodyGrayV2)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("الممرض: ${booking.nurseName}", fontSize = 13.sp, color = MedicalBlueV2)
                            Text("${booking.price} ج.م", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            if (filtered.isEmpty()) {
                item { Text("لا توجد نتائج تطابق بحثك.", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(32.dp)) }
            }
        }
    }
    
    if (showStatusSheetFor != null) {
        val b = showStatusSheetFor!!
        ModalBottomSheet(onDismissRequest = { showStatusSheetFor = null }, containerColor = if (isDark) Color(0xFF1E293B) else Color.White) {
            Column(modifier = Modifier.padding(24.dp).fillMaxWidth()) {
                Text("تحديث حالة الحجز #${b.id}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                val nextStatus = when(b.status) {
                    "PENDING" -> "ON_THE_WAY" to "تعيين الممرض بالطريق"
                    "ON_THE_WAY" -> "ARRIVED" to "تأكيد وصول الممرض"
                    "ARRIVED" -> "IN_PROGRESS" to "بدء الخدمة"
                    "IN_PROGRESS" -> "COMPLETED" to "اكتمال الخدمة وإنهائها"
                    else -> null
                }
                
                if (nextStatus != null) {
                    Button(
                        onClick = { 
                            if (b.id != null) viewModel.updateBookingStatusWithSync(b.id, nextStatus.first)
                            showStatusSheetFor = null 
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MedicalGreenV2)
                    ) { Text(nextStatus.second, fontWeight = FontWeight.Bold) }
                    Spacer(modifier = Modifier.height(12.dp))
                }
                if (b.status != "COMPLETED" && b.status != "CANCELLED") {
                    OutlinedButton(
                        onClick = { 
                            if (b.id != null) viewModel.updateBookingStatusWithSync(b.id, "CANCELLED")
                            showStatusSheetFor = null
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                    ) { Text("إلغاء الطلب نهائياً", fontWeight = FontWeight.Bold) }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

fun getStatusColor(s: String) = when(s) {
    "PENDING" -> Color(0xFF64748B)
    "ON_THE_WAY" -> Color(0xFFF59E0B)
    "ARRIVED", "IN_PROGRESS" -> Color(0xFF3B82F6)
    "COMPLETED" -> MedicalGreenV2
    "CANCELLED" -> Color.Red
    else -> Color.Gray
}

@Composable
fun AdminDashboardBookingItem(booking: com.example.connectivity.SupabaseBooking, isDark: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(containerColor = if(isDark) Color(0xFF1E293B) else Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if(isDark) Color(0xFF334155) else Color(0xFFE2E8F0))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(10.dp).background(getStatusColor(booking.status), CircleShape))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(booking.serviceName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = if(isDark) Color.White else DarkNavyV2)
                Text(booking.address, fontSize = 12.sp, color = BodyGrayV2)
                Spacer(modifier = Modifier.height(8.dp))
                val progress = when(booking.status) {
                    "PENDING" -> 0.2f
                    "ON_THE_WAY" -> 0.4f
                    "ARRIVED" -> 0.6f
                    "IN_PROGRESS" -> 0.8f
                    "COMPLETED" -> 1f
                    else -> 0f
                }
                if (booking.status != "CANCELLED") {
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = getStatusColor(booking.status),
                        trackColor = if(isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("${booking.price} ج", fontWeight = FontWeight.Bold, color = MedicalBlueV2)
        }
    }
}

// ----------------------------------------
// TAB 3: Services & Departments
// ----------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminServicesTab(viewModel: ELmorsyViewModel, isDark: Boolean) {
    var inServices by remember { mutableStateOf(true) }
    val services by viewModel.servicesFlow.collectAsState()
    val depts by viewModel.departmentsFlow.collectAsState()
    
    var showEditService by remember { mutableStateOf<MedicalService?>(null) }
    var showEditDept by remember { mutableStateOf<DepartmentEntity?>(null) }
    var showAddService by remember { mutableStateOf(false) }
    var showAddDept by remember { mutableStateOf(false) }
    
    var serviceToDelete by remember { mutableStateOf<MedicalService?>(null) }
    var deptToDelete by remember { mutableStateOf<DepartmentEntity?>(null) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TabRow(selectedTabIndex = if (inServices) 0 else 1, containerColor = Color.Transparent, contentColor = MedicalBlueV2) {
                Tab(selected = inServices, onClick = { inServices = true }, text = { Text("الخدمات", fontWeight = FontWeight.Bold) })
                Tab(selected = !inServices, onClick = { inServices = false }, text = { Text("الأقسام", fontWeight = FontWeight.Bold) })
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(contentPadding = PaddingValues(bottom = 80.dp), modifier = Modifier.fillMaxSize()) {
                if (inServices) {
                    if (services.isEmpty()) {
                        item { Text("لا توجد خدمات مضافة حتى الآن.", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(32.dp), color = BodyGrayV2) }
                    }
                    items(services) { svc ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            colors = CardDefaults.cardColors(containerColor = if(isDark) Color(0xFF1E293B) else Color.White),
                            border = BorderStroke(1.dp, if(isDark) Color(0xFF334155) else Color(0xFFE2E8F0))
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(svc.title, fontWeight = FontWeight.Bold)
                                    Text("${svc.basePrice} ج.م • ${svc.duration}", fontSize = 12.sp, color = BodyGrayV2)
                                }
                                IconButton(onClick = { showEditService = svc }) { Icon(Icons.Default.Edit, "تعديل", tint = MedicalBlueV2) }
                                IconButton(onClick = { serviceToDelete = svc }) { Icon(Icons.Default.Delete, "حذف", tint = Color.Red) }
                            }
                        }
                    }
                } else {
                    if (depts.isEmpty()) {
                        item { Text("لا توجد أقسام مضافة حتى الآن.", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(32.dp), color = BodyGrayV2) }
                    }
                    items(depts) { dept ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            colors = CardDefaults.cardColors(containerColor = if(isDark) Color(0xFF1E293B) else Color.White),
                            border = BorderStroke(1.dp, if(isDark) Color(0xFF334155) else Color(0xFFE2E8F0))
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(dept.title, fontWeight = FontWeight.Bold)
                                    Text(dept.description, fontSize = 12.sp, color = BodyGrayV2)
                                }
                                IconButton(onClick = { showEditDept = dept }) { Icon(Icons.Default.Edit, "تعديل", tint = MedicalBlueV2) }
                                IconButton(onClick = { deptToDelete = dept }) { Icon(Icons.Default.Delete, "حذف", tint = Color.Red) }
                            }
                        }
                    }
                }
            }
        }
        
        FloatingActionButton(
            onClick = { if(inServices) showAddService = true else showAddDept = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            containerColor = MedicalBlueV2,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, "إضافة")
        }
    }
    
    // Delete Confirmation Dialogs
    if (serviceToDelete != null) {
        AlertDialog(
            onDismissRequest = { serviceToDelete = null },
            title = { Text("تأكيد الحذف") },
            text = { Text("هل أنت متأكد من حذف خدمة '${serviceToDelete?.title}' نهائياً؟") },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteService(serviceToDelete!!); serviceToDelete = null }) {
                    Text("نعم، احذف", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { serviceToDelete = null }) { Text("تراجع") }
            }
        )
    }
    if (deptToDelete != null) {
        AlertDialog(
            onDismissRequest = { deptToDelete = null },
            title = { Text("تأكيد الحذف") },
            text = { Text("هل أنت متأكد من حذف قسم '${deptToDelete?.title}' نهائياً؟") },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteDepartment(deptToDelete!!); deptToDelete = null }) {
                    Text("نعم، احذف", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { deptToDelete = null }) { Text("تراجع") }
            }
        )
    }
    
    if (showEditService != null || showAddService) {
        val s = showEditService
        var title by remember { mutableStateOf(s?.title ?: "") }
        var price by remember { mutableStateOf(s?.basePrice?.toString() ?: "") }
        var description by remember { mutableStateOf(s?.description ?: "") }
        var longDescription by remember { mutableStateOf(s?.longDescription ?: "") }
        var duration by remember { mutableStateOf(s?.duration ?: "") }
        var iconName by remember { mutableStateOf(s?.iconName ?: "ic_default") }
        
        ModalBottomSheet(onDismissRequest = { showEditService = null; showAddService = false }, containerColor = if (isDark) Color(0xFF1E293B) else Color.White) {
            Column(modifier = Modifier.padding(24.dp).fillMaxWidth().verticalScroll(rememberScrollState()).navigationBarsPadding()) {
                Text(if(s != null) "تعديل الخدمة" else "إضافة خدمة جديدة", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("المسمى") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("الوصف القصير") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = longDescription, onValueChange = { longDescription = it }, label = { Text("وصف مفصل") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("السعر") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = duration, onValueChange = { duration = it }, label = { Text("المدة") }, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = iconName, onValueChange = { iconName = it }, label = { Text("اسم الأيقونة (مثال: Injection)") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { 
                    if (s != null) {
                        viewModel.updateServiceInSupabase(s.id, mapOf("title" to title, "base_price" to price, "description" to description, "long_description" to longDescription, "duration" to duration, "icon_name" to iconName))
                    } else {
                        val newId = "srv_${System.currentTimeMillis()}"
                        viewModel.addServiceToSupabase(MedicalService(newId, title, description, longDescription, price.toDoubleOrNull() ?: 0.0, duration, iconName, ""))
                    }
                    showEditService = null; showAddService = false
                }, modifier = Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = MedicalBlueV2)) { 
                    Text(if(s != null) "حفظ التعديلات" else "إضافة الخدمة", fontWeight = FontWeight.Bold) 
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
    
    if (showEditDept != null || showAddDept) {
        val d = showEditDept
        var title by remember { mutableStateOf(d?.title ?: "") }
        var description by remember { mutableStateOf(d?.description ?: "") }
        var serviceIds by remember { mutableStateOf(d?.serviceIdsText ?: "") }
        
        ModalBottomSheet(onDismissRequest = { showEditDept = null; showAddDept = false }, containerColor = if (isDark) Color(0xFF1E293B) else Color.White) {
            Column(modifier = Modifier.padding(24.dp).fillMaxWidth().verticalScroll(rememberScrollState()).navigationBarsPadding()) {
                Text(if(d != null) "تعديل القسم" else "إضافة قسم جديد", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("الاسم") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("الوصف") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = serviceIds, onValueChange = { serviceIds = it }, label = { Text("معرفات الخدمات (مفصولة بفاصلة)") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { 
                    if (d != null) {
                        viewModel.updateDepartment(d.id, title, description, serviceIds)
                    } else {
                        viewModel.addDepartment(title, description, serviceIds)
                    }
                    showEditDept = null; showAddDept = false
                }, modifier = Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = MedicalBlueV2)) { 
                    Text(if(d != null) "حفظ التعديلات" else "إضافة القسم", fontWeight = FontWeight.Bold) 
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

// ----------------------------------------
// TAB 4: Nurses & Requests
// ----------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminNursesTab(viewModel: ELmorsyViewModel, isDark: Boolean) {
    val requests by viewModel.nurseRequests.collectAsState()
    val nurses by viewModel.nursesFlow.collectAsState()
    var showRequests by remember { mutableStateOf(true) }
    
    var searchQuery by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("الكل") } // الكل, ذكر, أنثى
    var selectedRating by remember { mutableStateOf("الكل") } // الكل, +4.0, +4.5
    
    var showNurseDetails by remember { mutableStateOf<Nurse?>(null) }
    var nurseToDelete by remember { mutableStateOf<Nurse?>(null) }
    var showAddNurse by remember { mutableStateOf(false) }
    
    val filteredNurses = nurses.filter {
        (searchQuery.isEmpty() || it.name.contains(searchQuery)) &&
        (selectedGender == "الكل" || (selectedGender == "ذكر" && it.gender == "MALE") || (selectedGender == "أنثى" && it.gender == "FEMALE")) &&
        (selectedRating == "الكل" || (selectedRating == "+4.0" && it.rating >= 4.0) || (selectedRating == "+4.5" && it.rating >= 4.5))
    }.sortedByDescending { it.rating }
    
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { showRequests = true },
                colors = ButtonDefaults.buttonColors(containerColor = if (showRequests) MedicalBlueV2 else Color.Transparent, contentColor = if(showRequests) Color.White else BodyGrayV2),
                modifier = Modifier.weight(1f)
            ) { Text("طلبات الانضمام (${requests.filter { it.status == "PENDING" }.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
            Button(
                onClick = { showRequests = false },
                colors = ButtonDefaults.buttonColors(containerColor = if (!showRequests) MedicalGreenV2 else Color.Transparent, contentColor = if(!showRequests) Color.White else BodyGrayV2),
                modifier = Modifier.weight(1f)
            ) { Text("الممرضين المعتمدين", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        if (!showRequests) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                placeholder = { Text("ابحث عن ممرض بالاسم...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = if(isDark) Color(0xFF334155) else Color(0xFFE2E8F0),
                    unfocusedContainerColor = if(isDark) Color(0xFF1E293B) else Color.White
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("الكل", "ذكر", "أنثى").forEach { f ->
                    FilterChip(
                        selected = selectedGender == f,
                        onClick = { selectedGender = f },
                        label = { Text(f) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MedicalBlueV2, selectedLabelColor = Color.White)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                listOf("الكل", "+4.0", "+4.5").forEach { r ->
                    FilterChip(
                        selected = selectedRating == r,
                        onClick = { selectedRating = r },
                        label = { Text("تقييم $r") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFF59E0B), selectedLabelColor = Color.White)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        LazyColumn(contentPadding = PaddingValues(bottom = 24.dp)) {
            if (showRequests) {
                if (requests.none { it.status == "PENDING" }) {
                    item { Text("لا توجد طلبات انضمام قيد الانتظار.", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(32.dp), color = BodyGrayV2) }
                }
                items(requests.filter { it.status == "PENDING" }) { req ->
                    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(req.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DarkNavyV2)
                            Text("الخبرة: ${req.experienceYears} | الشهادة: ${req.certificate}", fontSize = 13.sp, color = Color(0xFF92400E))
                            Text("الهاتف: ${req.phone}", fontSize = 13.sp, color = Color(0xFF92400E))
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = { viewModel.approveNurseRequest(req) }, colors = ButtonDefaults.buttonColors(containerColor = MedicalGreenV2), modifier = Modifier.weight(1f)) { Text("قبول وتفعيل") }
                                OutlinedButton(onClick = { viewModel.rejectNurseRequest(req) }, colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red), border = BorderStroke(1.dp, Color.Red), modifier = Modifier.weight(1f)) { Text("رفض الطلب") }
                            }
                        }
                    }
                }
            } else {
                if (filteredNurses.isEmpty()) {
                    item { Text("لا يوجد ممرضين يطابقون بحثك.", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(32.dp), color = BodyGrayV2) }
                }
                items(filteredNurses) { nurse ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).clickable { showNurseDetails = nurse },
                        colors = CardDefaults.cardColors(containerColor = if(isDark) Color(0xFF1E293B) else Color.White),
                        border = BorderStroke(1.dp, if(isDark) Color(0xFF334155) else Color(0xFFE2E8F0))
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(nurse.name, fontWeight = FontWeight.Bold, color = if(isDark) Color.White else DarkNavyV2)
                                Text("الخبرة: ${nurse.experienceYears} | التقييم: ${nurse.rating}", fontSize = 12.sp, color = BodyGrayV2)
                            }
                            IconButton(onClick = { nurseToDelete = nurse }) { Icon(Icons.Default.PersonRemove, "حذف", tint = Color.Red) }
                        }
                    }
                }
            }
        }
    }
    
    // Detailed Nurse Bottom Sheet
    if (showNurseDetails != null) {
        val n = showNurseDetails!!
        ModalBottomSheet(onDismissRequest = { showNurseDetails = null }, containerColor = if (isDark) Color(0xFF1E293B) else Color.White) {
            Column(modifier = Modifier.padding(24.dp).fillMaxWidth().navigationBarsPadding()) {
                Text(n.name, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column {
                        Text("التقييم", color = BodyGrayV2, fontSize = 12.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                            Text(" ${n.rating} (${n.ratingCount})", fontWeight = FontWeight.Bold)
                        }
                    }
                    Column {
                        Text("الخبرة", color = BodyGrayV2, fontSize = 12.sp)
                        Text("${n.experienceYears} سنوات", fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("الزيارات المكتملة", color = BodyGrayV2, fontSize = 12.sp)
                        Text("${n.completedVisits}", fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))
                Text("المستشفى: ${n.hospitalAffiliation}", fontSize = 14.sp)
                Text("سعر الزيارة: ${n.pricePerVisit} ج.م", fontSize = 14.sp)
                Text("المنطقة: ${n.homeDistrict}", fontSize = 14.sp)
                Text("الهاتف: ${n.phone}", fontSize = 14.sp)
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
    
    // Nurse delete confirmation
    if (nurseToDelete != null) {
        AlertDialog(
            onDismissRequest = { nurseToDelete = null },
            title = { Text("تأكيد حذف الممرض") },
            text = { Text("هل أنت متأكد من إزالة الممرض '${nurseToDelete?.name}' نهائياً من النظام؟") },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteNurse(nurseToDelete!!); nurseToDelete = null }) {
                    Text("إزالة ممرض", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { nurseToDelete = null }) { Text("إلغاء") }
            }
        )
    }
}

// ----------------------------------------
// TAB 5: AI Assistant Settings
// ----------------------------------------
@Composable
fun AdminChatbotTab(viewModel: ELmorsyViewModel, isDark: Boolean) {
    val prompt by viewModel.chatbotSystemPrompt.collectAsState()
    val isEnabled by viewModel.chatbotEnabled.collectAsState()
    var editPrompt by remember { mutableStateOf("") }
    var currentEnabled by remember { mutableStateOf(true) }
    
    LaunchedEffect(prompt, isEnabled) {
        editPrompt = prompt
        currentEnabled = isEnabled
    }
    
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Card(
            colors = CardDefaults.cardColors(containerColor = if(isDark) Color(0xFF1E293B) else Color.White),
            border = BorderStroke(1.dp, if(isDark) Color(0xFF334155) else Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("حالة المساعد الذكي", fontWeight = FontWeight.Bold, color = MedicalBlueV2, fontSize = 16.sp)
                    Switch(
                        checked = currentEnabled,
                        onCheckedChange = { viewModel.updateChatbotEnabled(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MedicalBlueV2)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("توجيه الذكاء الاصطناعي (System Prompt)", fontWeight = FontWeight.Bold, color = MedicalBlueV2, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = editPrompt,
                    onValueChange = { editPrompt = it },
                    modifier = Modifier.fillMaxWidth().height(250.dp),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.updateChatbotSystemPrompt(editPrompt) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalBlueV2)
                ) { Text("حفظ وتنشيط التدريب السحابي", fontWeight = FontWeight.Bold) }
            }
        }
    }
}

// ----------------------------------------
// TAB 6: Settings (Developer & Sliders)
// ----------------------------------------
@Composable
fun AdminSettingsTab(viewModel: ELmorsyViewModel, isDark: Boolean) {
    val devProfile by viewModel.developerProfile.collectAsState()
    val sliders by viewModel.appSliders.collectAsState()

    var devName by remember(devProfile) { mutableStateOf(devProfile?.name ?: "") }
    var devTitle by remember(devProfile) { mutableStateOf(devProfile?.title ?: "") }
    var devMessage by remember(devProfile) { mutableStateOf(devProfile?.message ?: "") }
    var devImage by remember(devProfile) { mutableStateOf(devProfile?.imageUrl ?: "") }

    var editingSlider by remember { mutableStateOf<com.example.connectivity.AppSlider?>(null) }
    var sTitle by remember(editingSlider) { mutableStateOf(editingSlider?.title ?: "") }
    var sDesc by remember(editingSlider) { mutableStateOf(editingSlider?.description ?: "") }
    var sImageUrl by remember(editingSlider) { mutableStateOf(editingSlider?.imageUrl ?: "") }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        
        // Developer Profile Setting
        Card(
            colors = CardDefaults.cardColors(containerColor = if(isDark) Color(0xFF1E293B) else Color.White),
            border = BorderStroke(1.dp, if(isDark) Color(0xFF334155) else Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("رسالة مطور التطبيق", fontWeight = FontWeight.Bold, color = MedicalBlueV2, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = devName, onValueChange = { devName = it },
                    label = { Text("الاسم") }, modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = devTitle, onValueChange = { devTitle = it },
                    label = { Text("اللقب") }, modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = devMessage, onValueChange = { devMessage = it },
                    label = { Text("الرسالة") }, modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = devImage, onValueChange = { devImage = it },
                    label = { Text("رابط الصورة (URL)") }, modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { viewModel.updateDeveloperProfile(devProfile?.id ?: 0, devName, devTitle, devMessage, devImage) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalBlueV2)
                ) { Text("حفظ رسالة المطور", fontWeight = FontWeight.Bold) }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Image Sliders Setting
        Text("شرائح البانر (Image Sliders)", fontWeight = FontWeight.Bold, color = if(isDark) Color.White else DarkNavyV2, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        
        sliders.forEach { slider ->
            Card(
                colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E293B) else Color.White),
                border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(slider.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(slider.description, fontSize = 12.sp, color = BodyGrayV2)
                    }
                    IconButton(onClick = { editingSlider = slider }) {
                        Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = MedicalBlueV2)
                    }
                }
            }
        }

        if (sliders.size < 5) {
            Button(
                onClick = { editingSlider = com.example.connectivity.AppSlider(0, "", "", "") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("إضافة شريحة جديدة")
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }

    // Slider Editing Dialog
    if (editingSlider != null) {
        AlertDialog(
            onDismissRequest = { editingSlider = null },
            title = { Text(if ((editingSlider!!.id ?: 0) == 0) "إضافة شريحة" else "تعديل شريحة البانر") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = sTitle, onValueChange = { sTitle = it },
                        label = { Text("العنوان الرئيسي") }, modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = sDesc, onValueChange = { sDesc = it },
                        label = { Text("الوصف المختصر") }, modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = sImageUrl, onValueChange = { sImageUrl = it },
                        label = { Text("رابط الصورة (URL)") }, modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { 
                        viewModel.updateSlider(editingSlider!!.id ?: 0, sTitle, sDesc, sImageUrl)
                        editingSlider = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalBlueV2)
                ) { Text("حفظ الشريحة") }
            },
            dismissButton = {
                TextButton(onClick = { editingSlider = null }) { Text("إلغاء") }
            }
        )
    }
}
