const fs = require('fs');
const file = 'app/src/main/java/com/example/ui/screens/ELmorsyAppContent.kt';
let code = fs.readFileSync(file, 'utf8');

const newScreens = `

// ==========================================
// FULL SCREENS EXTRACTED FROM PROFILE
// ==========================================

@Composable
fun EditMedicalProfileScreen(viewModel: ELmorsyViewModel) {
    var editName by remember { mutableStateOf(viewModel.userName.value) }
    var editPhone by remember { mutableStateOf(viewModel.userPhone.value) }
    var editAddress by remember { mutableStateOf(viewModel.userAddress.value) }
    var editAge by remember { mutableStateOf(viewModel.patientAge.value) }
    var editBloodType by remember { mutableStateOf(viewModel.patientBloodType.value) }
    var editWeight by remember { mutableStateOf(viewModel.patientWeight.value) }
    var editGender by remember { mutableStateOf(viewModel.patientGender.value) }
    var editConditions by remember { mutableStateOf(viewModel.patientConditions.value) }
    var editAllergies by remember { mutableStateOf(viewModel.patientAllergies.value) }
    var editEmergencyName by remember { mutableStateOf(viewModel.emergencyContactName.value) }
    var editEmergencyPhone by remember { mutableStateOf(viewModel.emergencyContactPhone.value) }
    
    Scaffold(
        topBar = {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp).padding(top = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.navigateTo("profile") }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                }
                Text("تعديل الملف الطبي", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Button(onClick = { 
                    viewModel.updateProfileData(editName, editPhone, editAddress)
                    viewModel.updateMedicalProfile(
                        editAge, editBloodType, editWeight, editGender, editConditions, editAllergies, editEmergencyName, editEmergencyPhone,
                        viewModel.recentHeartRate.value, viewModel.recentBloodPressure.value, viewModel.recentBloodSugar.value
                    )
                    viewModel.navigateTo("profile")
                }) {
                    Text("حفظ")
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp)) {
            OutlinedTextField(value = editName, onValueChange = { editName = it }, label = { Text("الاسم") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = editPhone, onValueChange = { editPhone = it }, label = { Text("رقم الهاتف") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = editAddress, onValueChange = { editAddress = it }, label = { Text("العنوان") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(16.dp))
            Text("البيانات الطبية", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = editAge, onValueChange = { editAge = it }, label = { Text("العمر") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = editBloodType, onValueChange = { editBloodType = it }, label = { Text("فصيلة الدم") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = editWeight, onValueChange = { editWeight = it }, label = { Text("الوزن") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = editGender, onValueChange = { editGender = it }, label = { Text("النوع") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = editConditions, onValueChange = { editConditions = it }, label = { Text("الأمراض المزمنة") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = editAllergies, onValueChange = { editAllergies = it }, label = { Text("الحساسية") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(16.dp))
            Text("جهة اتصال للطوارئ", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = editEmergencyName, onValueChange = { editEmergencyName = it }, label = { Text("الاسم") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = editEmergencyPhone, onValueChange = { editEmergencyPhone = it }, label = { Text("التليفون") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun WalletScreen(viewModel: ELmorsyViewModel) {
    val balance by viewModel.walletBalance.collectAsState()
    val transactions by viewModel.walletTransactions.collectAsState()
    var rechargeAmount by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp).padding(top = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.navigateTo("profile") }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                }
                Text("المحفظة والرصيد", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MedicalBlue)) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("الرصيد المتاح", color = Color.White)
                    Text("$balance ج.م", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(24.dp))
            Text("شحن الرصيد", fontWeight = FontWeight.Bold)
            OutlinedTextField(value = rechargeAmount, onValueChange = { rechargeAmount = it }, label = { Text("المبلغ") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))
            Button(onClick = { 
                val amount = rechargeAmount.toDoubleOrNull() ?: 0.0
                if(amount > 0) {
                    viewModel.rechargeWallet(amount, "CREDIT_CARD")
                    rechargeAmount = ""
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("شحن")
            }
            Spacer(Modifier.height(24.dp))
            Text("سجل المعاملات", fontWeight = FontWeight.Bold)
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(transactions) { t ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(t.description)
                            Text("\${t.amount} ج.م", color = if (t.type == "CREDIT") Color.Green else Color.Red)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MedicalDocsScreen(viewModel: ELmorsyViewModel) {
    val documents by viewModel.medicalDocuments.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var docName by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp).padding(top = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.navigateTo("profile") }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                }
                Text("الوصفات والتحاليل", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            if (documents.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("لا توجد ملفات مرفوعة", color = Color.Gray)
                }
            } else {
                LazyColumn {
                    items(documents) { doc ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(doc.name)
                                IconButton(onClick = { viewModel.deleteMedicalDocument(doc.id) }) {
                                    Icon(Icons.Default.Delete, null, tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("رفع ملف جديد") },
            text = {
                OutlinedTextField(value = docName, onValueChange = { docName = it }, label = { Text("اسم الملف") })
            },
            confirmButton = {
                Button(onClick = { 
                    viewModel.uploadMedicalDocument(docName)
                    showAddDialog = false
                    docName = ""
                }) { Text("رفع") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("إلغاء") }
            }
        )
    }
}

@Composable
fun VitalsDashboardScreen(viewModel: ELmorsyViewModel) {
    var newVitalType by remember { mutableStateOf("HEART") }
    var newValue by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp).padding(top = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.navigateTo("profile") }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                }
                Text("لوحة القياسات الحيوية", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
            Text("القياسات الأخيرة", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                VitalCard("الضغط", viewModel.recentBloodPressure.value.ifBlank { "--" }, Icons.Default.FavoriteBorder, Modifier.weight(1f))
                Spacer(Modifier.width(8.dp))
                VitalCard("النبض", viewModel.recentHeartRate.value.ifBlank { "--" }, Icons.Default.Favorite, Modifier.weight(1f))
                Spacer(Modifier.width(8.dp))
                VitalCard("السكر", viewModel.recentBloodSugar.value.ifBlank { "--" }, Icons.Default.WaterDrop, Modifier.weight(1f))
            }
            Spacer(Modifier.height(32.dp))
            Text("إضافة قياس جديد", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                listOf("HEART" to "النبض", "PRESSURE" to "الضغط", "SUGAR" to "السكر").forEach { (type, label) ->
                    FilterChip(
                        selected = newVitalType == type,
                        onClick = { newVitalType = type },
                        label = { Text(label) }
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = newValue, onValueChange = { newValue = it }, label = { Text("القيمة") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(16.dp))
            Button(onClick = { 
                viewModel.logVitalSign(newVitalType, newValue, "")
                viewModel.updateMedicalProfile(
                    viewModel.patientAge.value, viewModel.patientBloodType.value, viewModel.patientWeight.value, viewModel.patientGender.value, viewModel.patientConditions.value, viewModel.patientAllergies.value, viewModel.emergencyContactName.value, viewModel.emergencyContactPhone.value,
                    if(newVitalType == "HEART") newValue else viewModel.recentHeartRate.value,
                    if(newVitalType == "PRESSURE") newValue else viewModel.recentBloodPressure.value,
                    if(newVitalType == "SUGAR") newValue else viewModel.recentBloodSugar.value
                )
                newValue = ""
            }, modifier = Modifier.fillMaxWidth()) {
                Text("تسجيل القياس")
            }
        }
    }
}

@Composable
fun VitalCard(title: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB))) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = MedicalBlue)
            Spacer(Modifier.height(8.dp))
            Text(title, color = Color.Gray, fontSize = 12.sp)
            Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
fun MedicationRemindersScreen(viewModel: ELmorsyViewModel) {
    val reminders by viewModel.medicationReminders.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var medName by remember { mutableStateOf("") }
    var medDose by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp).padding(top = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.navigateTo("profile") }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                }
                Text("التذكير بالأدوية", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            if (reminders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("لا توجد مذكرات للأدوية", color = Color.Gray)
                }
            } else {
                LazyColumn {
                    items(reminders) { rem ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text(rem.medicationName, fontWeight = FontWeight.Bold)
                                    Text("الجرعة: \${rem.dosage} - الوقت: \${rem.time}", color = Color.Gray, fontSize = 12.sp)
                                }
                                IconButton(onClick = { viewModel.deleteMedicationReminder(rem.id) }) {
                                    Icon(Icons.Default.Delete, null, tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("إضافة دواء جديد") },
            text = {
                Column {
                    OutlinedTextField(value = medName, onValueChange = { medName = it }, label = { Text("اسم الدواء") })
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = medDose, onValueChange = { medDose = it }, label = { Text("الجرعة") })
                }
            },
            confirmButton = {
                Button(onClick = { 
                    viewModel.addMedicationReminder(medName, medDose, "12:00")
                    showAddDialog = false
                    medName = ""
                    medDose = ""
                }) { Text("حفظ") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("إلغاء") }
            }
        )
    }
}

@Composable
fun SupportChatScreen(viewModel: ELmorsyViewModel) {
    var message by remember { mutableStateOf("") }
    val chatMessages = remember { mutableStateListOf<Pair<String, Boolean>>(
        "مرحباً بك! فريق الدعم الطبي معك كير جاهز للرد على استفساراتك." to false
    ) }
    
    Scaffold(
        topBar = {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp).padding(top = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.navigateTo("profile") }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                }
                Text("الدعم الطبي اللحظي", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("اكتب رسالتك...") }
                    )
                    Spacer(Modifier.width(8.dp))
                    IconButton(onClick = { 
                        if(message.isNotBlank()) {
                            chatMessages.add(message to true)
                            message = ""
                        }
                    }, modifier = Modifier.background(MedicalBlue, CircleShape)) {
                        Icon(Icons.Default.Send, null, tint = Color.White)
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            items(chatMessages) { (msg, isUser) ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = if (isUser) MedicalBlue else Color(0xFFF1F5F9)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Text(msg, modifier = Modifier.padding(12.dp), color = if (isUser) Color.White else Color.Black)
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

`;

code += newScreens;
fs.writeFileSync(file, code);

console.log("Appended new screens.");
