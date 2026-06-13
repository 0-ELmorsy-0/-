package com.example.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.notifications.LocalNotificationHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.isActive
import com.example.connectivity.SupabaseClient
import com.example.connectivity.SupabaseBooking
import android.util.Log
import com.example.api.GeminiRetrofitClient
import com.example.api.GenerateContentRequest
import com.example.api.Content
import com.example.api.Part
import com.example.BuildConfig

class ELmorsyViewModel(application: Application) : AndroidViewModel(application) {

    // --- AI Assistant State ---
    suspend fun getAiConsultationResponse(prompt: String): String {
        return try {
            val apiKey = BuildConfig.GEMINI_API_KEY
                            val request = GenerateContentRequest(
                contents = listOf(
                    Content(parts = listOf(Part(text = "أنت طبيب استشاري ذكي ومساعد طبي من شركة معاك للرعاية المنزلية (Maak Care). قدم نصيحة أولية مختصرة جداً (سطرين) للمريض واطلب منه حجز تمريض أو طبيب من التطبيق إذا تطلب الأمر. سؤال المريض: $prompt")))
                )
            )
            val response = GeminiRetrofitClient.service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "عذراً النطام مشغول مؤقتاً."
        } catch (e: Exception) {
            Log.e("GeminiError", "Error", e)
            "عذراً، حدث خطأ في الاتصال بالذكاء الاصطناعي. الرجاء التحقق من اتصال الإنترنت."
        }
    }

    private val db = AppDatabase.getDatabase(application)
    private val repository = BookingRepository(db.bookingDao())
    private val notificationHelper = LocalNotificationHelper(application)

    // --- Admin Dashboard State ---
    private val _nurseRequests = MutableStateFlow<List<com.example.connectivity.SupabaseNurseRequest>>(emptyList())
    val nurseRequests = _nurseRequests.asStateFlow()

    private val _appSettings = MutableStateFlow<Map<String, String>>(emptyMap())
    val appSettings = _appSettings.asStateFlow()

    private val _developerProfile = MutableStateFlow<com.example.connectivity.DeveloperProfile?>(null)
    val developerProfile = _developerProfile.asStateFlow()

    private val _appSliders = MutableStateFlow<List<com.example.connectivity.AppSlider>>(emptyList())
    val appSliders = _appSliders.asStateFlow()

    private val _allProfiles = MutableStateFlow<List<com.example.connectivity.SupabaseProfile>>(emptyList())
    val allProfiles = _allProfiles.asStateFlow()

    private val _chatbotSystemPrompt = MutableStateFlow("أنت مساعد طبي ذكي من معاك كير...")
    val chatbotSystemPrompt = _chatbotSystemPrompt.asStateFlow()

    private val _chatbotEnabled = MutableStateFlow(true)
    val chatbotEnabled = _chatbotEnabled.asStateFlow()

    // Dashboard Statistics flows
    private val _dashboardBookings = MutableStateFlow<List<com.example.connectivity.SupabaseBooking>>(emptyList())
    val dashboardBookings = _dashboardBookings.asStateFlow()

    val servicesFlow: StateFlow<List<MedicalService>> = db.serviceDao().getAllServices()
        .map { list -> list.map { MedicalService(it.id, it.title, it.description, it.longDescription, it.basePrice, it.duration, it.iconName, it.imageUrl) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val nursesFlow: StateFlow<List<Nurse>> = db.nurseDao().getAllNurses()
        .map { list -> list.map { Nurse(it.id, it.name, it.rating, it.ratingCount, it.experienceYears, it.pricePerVisit, it.gender, it.completedVisits, it.phone, it.hospitalAffiliation, it.lat, it.lng, it.homeDistrict) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val departmentsFlow: StateFlow<List<DepartmentEntity>> = db.departmentDao().getAllDepartments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI state for navigation
    private val _currentScreen = MutableStateFlow("splash")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    // Auth State
    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userPhone = MutableStateFlow("")
    val userPhone: StateFlow<String> = _userPhone.asStateFlow()

    private val _userAddress = MutableStateFlow("القاهرة، مصر الجديدة")
    val userAddress: StateFlow<String> = _userAddress.asStateFlow()

    private val _userLat = MutableStateFlow(30.1034) // Default Heliopolis/misr-el-gdeda lat
    val userLat: StateFlow<Double> = _userLat.asStateFlow()

    private val _userLng = MutableStateFlow(31.3323) // Default Heliopolis/misr-el-gdeda lng
    val userLng: StateFlow<Double> = _userLng.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // Session persistence & Biometrics Support
    private val prefs = application.getSharedPreferences("elmorsy_session_prefs", android.content.Context.MODE_PRIVATE)

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _userRole = MutableStateFlow(prefs.getString("user_role", "PATIENT") ?: "PATIENT")
    val userRole: StateFlow<String> = _userRole.asStateFlow()

    fun setUserRole(role: String) {
        _userRole.value = role
        prefs.edit().putString("user_role", role).apply()
    }

    private val _hasSavedSession = MutableStateFlow(false)
    val hasSavedSession: StateFlow<Boolean> = _hasSavedSession.asStateFlow()

    private val _hasCompletedOnboarding = MutableStateFlow(false)
    val hasCompletedOnboarding: StateFlow<Boolean> = _hasCompletedOnboarding.asStateFlow()

    fun completeOnboarding() {
        prefs.edit().putBoolean("onboarding_complete", true).apply()
        _hasCompletedOnboarding.value = true
        navigateTo("login")
    }

    private val _savedSessionEmail = MutableStateFlow("")
    val savedSessionEmail: StateFlow<String> = _savedSessionEmail.asStateFlow()

    private val _savedSessionName = MutableStateFlow("")
    val savedSessionName: StateFlow<String> = _savedSessionName.asStateFlow()

    init {
        val hasSession = prefs.getBoolean("has_session", false)
        _isDarkMode.value = prefs.getBoolean("is_dark_mode", false)
        _hasSavedSession.value = hasSession
        _hasCompletedOnboarding.value = prefs.getBoolean("onboarding_complete", false)
        _savedSessionEmail.value = prefs.getString("saved_email", "") ?: ""
        _savedSessionName.value = prefs.getString("saved_name", "") ?: ""
        
        // Also if we already have a session, we can restore details so biometric is ready
        if (hasSession) {
            _userName.value = prefs.getString("saved_name", "") ?: ""
            _userPhone.value = prefs.getString("saved_phone", "") ?: ""
            
            val savedAddress = prefs.getString("saved_address", "") ?: ""
            val savedLat = prefs.getFloat("saved_lat", 0f)
            if (savedAddress.isNotEmpty() && savedLat != 0f) {
                _userAddress.value = savedAddress
                _userLat.value = savedLat.toDouble()
                _userLng.value = prefs.getFloat("saved_lng", 31.3323f).toDouble()
                _isLoggedIn.value = true
            } else {
                _isLoggedIn.value = false
            }
        }
    }

    fun toggleTheme() {
        val next = !_isDarkMode.value
        _isDarkMode.value = next
        prefs.edit().putBoolean("is_dark_mode", next).apply()
    }

    fun saveSession(email: String, name: String, phone: String, provider: String = "email") {
        prefs.edit().apply {
            putBoolean("has_session", true)
            putString("saved_email", email)
            putString("saved_name", name)
            putString("saved_phone", phone)
            putString("saved_provider", provider)
            apply()
        }
        _hasSavedSession.value = true
        _savedSessionEmail.value = email
        _savedSessionName.value = name
    }

    fun clearSession() {
        prefs.edit().apply {
            putBoolean("has_session", false)
            putString("saved_email", "")
            putString("saved_name", "")
            putString("saved_phone", "")
            putString("saved_provider", "")
            putString("saved_address", "")
            putFloat("saved_lat", 0f)
            putFloat("saved_lng", 0f)
            apply()
        }
        _hasSavedSession.value = false
        _savedSessionEmail.value = ""
        _savedSessionName.value = ""
    }

    fun loginWithBiometrics() {
        val savedName = prefs.getString("saved_name", "") ?: ""
        val savedPhone = prefs.getString("saved_phone", "") ?: ""
        val savedAddr = prefs.getString("saved_address", "القاهرة، مصر الجديدة") ?: "القاهرة، مصر الجديدة"
        
        _userName.value = savedName
        _userPhone.value = savedPhone
        _userAddress.value = savedAddr
        _bookingAddress.value = savedAddr
        _isLoggedIn.value = true
        _currentScreen.value = "home"
    }

    /**
     * Calculates distance in kilometers between the user location and a nurse's coordinates.
     */
    fun getDistanceToNurseInKm(nurse: Nurse): Double {
        val lat = _userLat.value
        val lng = _userLng.value
        val dLat = nurse.lat - lat
        val dLng = nurse.lng - lng
        return java.lang.Math.sqrt(dLat * dLat + dLng * dLng) * 111.0
    }

    /**
     * Exposes nurses that are sorted and filtered to be in proximity of the patient's selected coordinates.
     */
    val nearbyNurses: StateFlow<List<Nurse>> = combine(_userLat, _userLng, nursesFlow) { lat, lng, nursesList ->
        val listWithDist = nursesList.map { nurse ->
            val dLat = nurse.lat - lat
            val dLng = nurse.lng - lng
            val dist = java.lang.Math.sqrt(dLat * dLat + dLng * dLng) * 111.0
            Pair(nurse, dist)
        }
        val insideRange = listWithDist.filter { it.second <= 12.0 } // 12 KM standard medical range
        if (insideRange.isNotEmpty()) {
            insideRange.sortedBy { it.second }.map { it.first }
        } else {
            // fallback to closest 3 so the interface works perfectly on any coordinates
            listWithDist.sortedBy { it.second }.take(3).map { it.first }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Professional Patient Profile States
    private val _patientAge = MutableStateFlow("٦٨ عاماً")
    val patientAge: StateFlow<String> = _patientAge.asStateFlow()

    private val _patientBloodType = MutableStateFlow("O+")
    val patientBloodType: StateFlow<String> = _patientBloodType.asStateFlow()

    private val _patientWeight = MutableStateFlow("٧٤ كجم")
    val patientWeight: StateFlow<String> = _patientWeight.asStateFlow()

    private val _patientGender = MutableStateFlow("ذكر")
    val patientGender: StateFlow<String> = _patientGender.asStateFlow()

    private val _patientConditions = MutableStateFlow("ضغط الدم المرتفع، السكري من الدرجة الثانية")
    val patientConditions: StateFlow<String> = _patientConditions.asStateFlow()

    private val _patientAllergies = MutableStateFlow("البنسلين والمنتجات الكيميائية المشتقة")
    val patientAllergies: StateFlow<String> = _patientAllergies.asStateFlow()

    private val _emergencyContactName = MutableStateFlow("د. محمد صبحي (الابن)")
    val emergencyContactName: StateFlow<String> = _emergencyContactName.asStateFlow()

    private val _emergencyContactPhone = MutableStateFlow("٠١٠٩٨٧٦٥٤٣٢")
    val emergencyContactPhone: StateFlow<String> = _emergencyContactPhone.asStateFlow()

    private val _recentHeartRate = MutableStateFlow("٧٦ ن/د")
    val recentHeartRate: StateFlow<String> = _recentHeartRate.asStateFlow()

    private val _recentBloodPressure = MutableStateFlow("١٢٠ / ٨٠")
    val recentBloodPressure: StateFlow<String> = _recentBloodPressure.asStateFlow()

    private val _recentBloodSugar = MutableStateFlow("١١٥ ملجم")
    val recentBloodSugar: StateFlow<String> = _recentBloodSugar.asStateFlow()

    private val _vitalLogs = MutableStateFlow<List<VitalLog>>(emptyList())
    val vitalLogs: StateFlow<List<VitalLog>> = _vitalLogs.asStateFlow()

    private val _selectedPatientType = MutableStateFlow("أنا (صاحب الحساب)")
    val selectedPatientType: StateFlow<String> = _selectedPatientType.asStateFlow()

    private val _customPatientName = MutableStateFlow("")
    val customPatientName: StateFlow<String> = _customPatientName.asStateFlow()

    fun setSelectedPatientType(type: String) {
        _selectedPatientType.value = type
    }

    fun setCustomPatientName(name: String) {
        _customPatientName.value = name
    }

    fun addVitalLog(type: String, value: String, note: String) {
        val typeLabel = when (type) {
            "HEART" -> "ن/د"
            "SUGAR" -> "ملجم"
            else -> ""
        }
        val formattedValue = if (typeLabel.isNotEmpty() && !value.contains(typeLabel)) "$value $typeLabel" else value
        
        val newLog = VitalLog(
            id = "vl_${System.currentTimeMillis()}",
            type = type,
            value = formattedValue,
            timestamp = "اليوم، ٣ يونيو",
            note = note.ifBlank { "قياس معتمد" }
        )
        
        _vitalLogs.value = listOf(newLog) + _vitalLogs.value
        
        when (type) {
            "HEART" -> _recentHeartRate.value = formattedValue
            "PRESSURE" -> _recentBloodPressure.value = formattedValue
            "SUGAR" -> _recentBloodSugar.value = formattedValue
        }
        
        addNotification(
            title = "تسجيل مؤشر حيوي جديد 🧪",
            description = "تم إضافة قياس جديد لـ ${when(type){"HEART"->"نبض القلب" "PRESSURE"->"ضغط الدم" else->"مستوى السكر"}}: $formattedValue بنجاح للرجوع إليه عند زيارة الممرض.",
            type = "UPDATE"
        )
    }

    fun deleteVitalLog(id: String) {
        _vitalLogs.value = _vitalLogs.value.filter { it.id != id }
    }

    fun updatePatientProfile(
        name: String,
        phone: String,
        address: String,
        age: String,
        bloodType: String,
        weight: String,
        gender: String,
        conditions: String,
        allergies: String,
        emergencyName: String,
        emergencyPhone: String,
        heartRate: String,
        bloodPressure: String,
        bloodSugar: String
    ) {
        _userName.value = name
        _userPhone.value = phone
        _userAddress.value = address
        _patientAge.value = age
        _patientBloodType.value = bloodType
        _patientWeight.value = weight
        _patientGender.value = gender
        _patientConditions.value = conditions
        _patientAllergies.value = allergies
        _emergencyContactName.value = emergencyName
        _emergencyContactPhone.value = emergencyPhone
        _recentHeartRate.value = heartRate
        _recentBloodPressure.value = bloodPressure
        _recentBloodSugar.value = bloodSugar
    }

    // Onboarding slide index
    private val _onboardingSlide = MutableStateFlow(0)
    val onboardingSlide: StateFlow<Int> = _onboardingSlide.asStateFlow()

    // Selected Booking States
    private val _selectedService = MutableStateFlow<MedicalService?>(null)
    val selectedService: StateFlow<MedicalService?> = _selectedService.asStateFlow()

    private val _viewedNurse = MutableStateFlow<Nurse?>(null)
    val viewedNurse: StateFlow<Nurse?> = _viewedNurse.asStateFlow()

    private val _selectedNurse = MutableStateFlow<Nurse?>(null)
    val selectedNurse: StateFlow<Nurse?> = _selectedNurse.asStateFlow()

    private val _bookingDate = MutableStateFlow("")
    val bookingDate: StateFlow<String> = _bookingDate.asStateFlow()

    private val _bookingTimeSlot = MutableStateFlow("")
    val bookingTimeSlot: StateFlow<String> = _bookingTimeSlot.asStateFlow()

    private val _bookingAddress = MutableStateFlow("")
    val bookingAddress: StateFlow<String> = _bookingAddress.asStateFlow()

    private val _bookingNotes = MutableStateFlow("")
    val bookingNotes: StateFlow<String> = _bookingNotes.asStateFlow()

    private val _paymentMethod = MutableStateFlow("APP_WALLET") // "APP_WALLET", "WALLET", "FAWRY", "CARD"
    val paymentMethod: StateFlow<String> = _paymentMethod.asStateFlow()

    // Selected Nurse Selection Filter
    private val _nurseFilterType = MutableStateFlow("ALL") // "ALL", "RATING", "PRICE", "EXP"
    val nurseFilterType: StateFlow<String> = _nurseFilterType.asStateFlow()

    // Running tracking states
    private val _activeBookingId = MutableStateFlow<Int>(-1)
    val activeBookingId: StateFlow<Int> = _activeBookingId.asStateFlow()

    private val _trackingEtaSeconds = MutableStateFlow(30) // simulation timer
    val trackingEtaSeconds: StateFlow<Int> = _trackingEtaSeconds.asStateFlow()

    private val _trackingStatus = MutableStateFlow("ON_THE_WAY") // "ON_THE_WAY", "ARRIVED", "IN_PROGRESS", "COMPLETED"
    val trackingStatus: StateFlow<String> = _trackingStatus.asStateFlow()

    private val _nurseLatOffset = MutableStateFlow(0.04f)
    val nurseLatOffset: StateFlow<Float> = _nurseLatOffset.asStateFlow()
    private val _nurseLngOffset = MutableStateFlow(-0.03f)
    val nurseLngOffset: StateFlow<Float> = _nurseLngOffset.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _notifications = MutableStateFlow<List<InAppNotification>>(emptyList())
    val notifications: StateFlow<List<InAppNotification>> = _notifications.asStateFlow()

    private var trackingJob: Job? = null

    // --- NEW: 5 HEALTHCARE FEATURES STATES & ACTIONS ---
    
    // E-Wallet Balance & Transactions History
    private val _walletBalance = MutableStateFlow(850.0)
    val walletBalance = _walletBalance.asStateFlow()

    private val _walletTransactions = MutableStateFlow<List<WalletTransaction>>(emptyList())
    val walletTransactions = _walletTransactions.asStateFlow()

    // Medications & Treatement Reminders
    private val _medicationReminders = MutableStateFlow<List<MedicationReminder>>(emptyList())
    val medicationReminders = _medicationReminders.asStateFlow()

    // Dynamic digital clinical documents
    private val _medicalDocuments = MutableStateFlow<List<MedicalDocument>>(emptyList())
    val medicalDocuments = _medicalDocuments.asStateFlow()

    // Active Helpdesk status
    val isHelpdeskChat = MutableStateFlow(false)

    // Send chat message
    fun sendChatMessage(content: String, isHelpdesk: Boolean = false) {
        if (content.isBlank()) return
        isHelpdeskChat.value = isHelpdesk
        
        val current = _chatMessages.value.toMutableList()
        val timeNow = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
        
        current.add(
            ChatMessage(
                id = "p_${System.currentTimeMillis()}",
                sender = "PATIENT",
                content = content,
                timestamp = timeNow
            )
        )
        _chatMessages.value = current

        // Self-trigger automated response representation for Helpdesk or Nurse
        viewModelScope.launch {
            delay(1500)
            val responseText = if (isHelpdesk) {
                "شكراً لتواصلكم مع فريق الدعم الطبي ومكتب المساعدة لتطبيق معاك. لقد استلمنا استفساركم: \"$content\". يقوم مستشار رعاية المرضى الآن بمراجعة ملفك الطبي وتاريخ الأدوية لمساعدتكم بشكل مخصص وفوري."
            } else {
                generateAutomatedReply(content)
            }
            
            val senderLabel = if (isHelpdesk) "مستشار الدعم الطبي 🎧" else (_selectedNurse.value?.name ?: "مختص الرعاية 🩺")
            val senderRole = if (isHelpdesk) "SUPPORT" else "NURSE"
            
            val currentUpdated = _chatMessages.value.toMutableList()
            currentUpdated.add(
                ChatMessage(
                    id = "resp_${System.currentTimeMillis()}",
                    sender = senderRole,
                    content = responseText,
                    timestamp = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
                )
            )
            _chatMessages.value = currentUpdated

            // Notify user of incoming reply!
            notificationHelper.sendNotification("✉️ رسالة جديدة من $senderLabel", responseText)
        }
    }

    fun initSupportChat() {
        isHelpdeskChat.value = true
        _chatMessages.value = listOf(
            ChatMessage(
                id = "support_1",
                sender = "SUPPORT",
                content = "أهلاً بك في مكتب المساعدة والعملاء الموحد لتطبيق معاك! كيف يمكن لمستشار فريقنا الطبي مساعدتك ومرافقتكم اليوم؟ 🩺",
                timestamp = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
            )
        )
    }

    private fun toArabicNumerals(num: Int): String {
        return num.toString().map { char ->
            if (char in '0'..'9') (char.code + 1584).toChar() else char
        }.joinToString("")
    }

    // --- E-Wallet Actions ---
    fun rechargeWallet(amount: Double, method: String) {
        _walletBalance.value += amount
        val symbol = when (method) {
            "VODAFONE_CASH" -> "📱"
            "FAWRY" -> "💸"
            else -> "💳"
        }
        val methodNameAr = when (method) {
            "VODAFONE_CASH" -> "فودافون كاش"
            "FAWRY" -> "فوري"
            else -> "البطاقة الائتمانية"
        }
        val trans = WalletTransaction(
            id = "w_${System.currentTimeMillis()}",
            title = "شحن المحفظة عبر $methodNameAr",
            amount = amount,
            isCredit = true,
            date = "اليوم، " + java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date()),
            paymentMethodSymbol = symbol
        )
        _walletTransactions.value = listOf(trans) + _walletTransactions.value
        addNotification(
            title = "نجح شحن رصيد المحفظة 💰",
            description = "تم إضافة ${toArabicNumerals(amount.toInt())} ج.م في حسابك عبر $methodNameAr بنجاح.",
            type = "CONFIRMATION"
        )
    }

    fun payWithWallet(amount: Double, serviceName: String): Boolean {
        if (_walletBalance.value >= amount) {
            _walletBalance.value -= amount
            val trans = WalletTransaction(
                id = "w_${System.currentTimeMillis()}",
                title = "دفع حجز خدمة ($serviceName)",
                amount = amount,
                isCredit = false,
                date = "اليوم، " + java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date()),
                paymentMethodSymbol = "🩺"
            )
            _walletTransactions.value = listOf(trans) + _walletTransactions.value
            return true
        }
        return false
    }

    // --- Medication Reminders Actions ---
    fun addMedicationReminder(name: String, dosage: String, time: String) {
        val newRem = MedicationReminder(
            id = "rem_${System.currentTimeMillis()}",
            medName = name,
            dosage = dosage,
            time = time,
            isTaken = false,
            daysOfWeek = "يومياً"
        )
        _medicationReminders.value = _medicationReminders.value + newRem
        addNotification(
            title = "تم إضافة منبه الدواء ⏰",
            description = "تم جدولة التذكير بدواء ($name) في تمام الساعة $time بنجاح.",
            type = "CONFIRMATION"
        )
    }

    fun toggleReminderTaken(id: String) {
        _medicationReminders.value = _medicationReminders.value.map { rem ->
            if (rem.id == id) {
                val nextState = !rem.isTaken
                if (nextState) {
                    notificationHelper.sendNotification(
                        "⏰ أحسنت! تناول الدواء",
                        "تم تسجيل تناول جرعة دواء (${rem.medName}) بنجاح. نتمنى لكم وافر الصحة والعافية."
                    )
                }
                rem.copy(isTaken = nextState)
            } else rem
        }
    }

    fun deleteMedicationReminder(id: String) {
        _medicationReminders.value = _medicationReminders.value.filter { it.id != id }
    }

    // --- Medical Documents Actions ---
    fun addMedicalDocument(name: String, date: String = "اليوم") {
        val newDoc = MedicalDocument(
            id = "doc_${System.currentTimeMillis()}",
            name = name,
            date = date,
            size = "١.٨ ميجابايت"
        )
        _medicalDocuments.value = _medicalDocuments.value + newDoc
        addNotification(
            title = "تم حفظ مستند طبي جديد 🗂️",
            description = "تم رفع وحفظ ملف ($name) بملفكم الرقمي الشامل المعتمد.",
            type = "CONFIRMATION"
        )
    }

    fun deleteMedicalDocument(id: String) {
        _medicalDocuments.value = _medicalDocuments.value.filter { it.id != id }
    }


    // Room Database bookings
    val bookingsHistory: StateFlow<List<BookingEntity>> = repository.allBookings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Start continuous background polling for data sync
        viewModelScope.launch {
            while (isActive) {
                fetchServicesAndNursesFromSupabase()
                delay(30000) // Poll every 30 seconds
            }
        }
    }

    fun markAllNotificationsAsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    fun addNotification(title: String, description: String, type: String) {
        val newNotification = InAppNotification(
            id = "n_${System.currentTimeMillis()}",
            title = title,
            description = description,
            time = "الآن",
            type = type,
            isRead = false
        )
        _notifications.value = listOf(newNotification) + _notifications.value
    }

    fun navigateTo(screen: String) {
        _currentScreen.value = screen
    }

    fun setOnboardingSlide(slide: Int) {
        _onboardingSlide.value = slide
    }

    // Supabase Email Login Flow
    fun performSupabaseEmailLogin(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val apiKey = com.example.connectivity.SupabaseClient.getSupabaseKey()
                val request = com.example.connectivity.SupabaseAuthRequest(email = email, password = password)
                val response = com.example.connectivity.SupabaseClient.service.signIn(
                    apiKey = apiKey,
                    grantType = "password",
                    body = request
                )
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    
                    if (_userRole.value == "ADMIN") {
                        val authHeader = "Bearer ${authResponse.accessToken ?: apiKey}"
                        val adminResponse = com.example.connectivity.SupabaseClient.service.getAdmin(
                            apiKey = apiKey,
                            authHeader = authHeader,
                            emailFilter = "eq.$email"
                        )
                        if (!adminResponse.isSuccessful || adminResponse.body().isNullOrEmpty()) {
                            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                onResult(false, "غير مصرح لك بالدخول بنسخة الإدارة. يرجى مراجعة الصلاحيات.")
                            }
                            return@launch
                        }
                    }

                    val name = authResponse.user?.userMetadata?.fullName ?: authResponse.user?.email?.substringBefore("@") ?: "مستفيد معاك"
                    val phone = authResponse.user?.userMetadata?.phone ?: "01000000000"
                    
                    saveSession(email = email, name = name, phone = phone, provider = "email")
                    
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                        _userName.value = name
                        _userPhone.value = phone
                        _currentScreen.value = "address_selection"
                        onResult(true, null)
                    }
                } else {
                    val errorMsg = response.errorBody()?.string() ?: ""
                    val parsedError = if (errorMsg.contains("Invalid login credentials") || errorMsg.contains("invalid_credentials")) {
                        "عذراً، البريد الإلكتروني أو كود المرور غير صحيح"
                    } else {
                        "خطأ في تسجيل الدخول. تفاضيل: $errorMsg"
                    }
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                        onResult(false, parsedError)
                    }
                }
            } catch (e: Exception) {
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    onResult(false, "حدث خطأ غير متوقع بالشبكة: ${e.localizedMessage}")
                }
            }
        }
    }

    // Supabase Email SignUp Flow
    fun performSupabaseEmailSignUp(fullName: String, phone: String, email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val apiKey = com.example.connectivity.SupabaseClient.getSupabaseKey()
                
                if (_userRole.value == "ADMIN") {
                    val adminResponse = com.example.connectivity.SupabaseClient.service.getAdmin(
                        apiKey = apiKey,
                        authHeader = "Bearer $apiKey",
                        emailFilter = "eq.$email"
                    )
                    if (!adminResponse.isSuccessful || adminResponse.body().isNullOrEmpty()) {
                        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                            onResult(false, "غير مصرح لك بإنشاء حساب بصلاحية الإدارة. يرجى مراجعة الدعم الفني.")
                        }
                        return@launch
                    }
                }

                val meta = mapOf("full_name" to fullName, "phone" to phone)
                val request = com.example.connectivity.SupabaseAuthRequest(email = email, password = password, data = meta)
                val response = com.example.connectivity.SupabaseClient.service.signUp(
                    apiKey = apiKey,
                    body = request
                )
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    // Mirror to custom profiles table fallback if exists
                    try {
                        val authHeader = "Bearer $apiKey"
                        val profile = com.example.connectivity.SupabaseProfile(
                            id = authResponse.user?.id,
                            phone = phone,
                            name = fullName,
                            email = email
                        )
                        com.example.connectivity.SupabaseClient.service.insertProfile(
                            apiKey = apiKey,
                            authHeader = authHeader,
                            profile = profile
                        )
                    } catch (e: Exception) {
                        // ignore profiles table warning
                    }
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                        onResult(true, null)
                    }
                } else {
                    val errorMsg = response.errorBody()?.string() ?: ""
                    val parsedError = if (errorMsg.contains("already registered") || errorMsg.contains("user_already_exists")) {
                        "هذا البريد الإلكتروني مسجل بالفعل مسبقاً"
                    } else {
                        "خطأ بالتسجيل: $errorMsg"
                    }
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                        onResult(false, parsedError)
                    }
                }
            } catch (e: Exception) {
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    onResult(false, "حدث خطأ بالشبكة أثناء التسجيل: ${e.localizedMessage}")
                }
            }
        }
    }

    // Supabase Phone Database-Backed Login Flow
    fun performSupabasePhoneLogin(phone: String, nameForNewUserIfSignup: String?, isSignUp: Boolean, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val apiKey = com.example.connectivity.SupabaseClient.getSupabaseKey()
                val authHeader = "Bearer $apiKey"
                
                // Fetch profiles matching phone
                val response = com.example.connectivity.SupabaseClient.service.getProfile(
                    apiKey = apiKey,
                    authHeader = authHeader,
                    phoneFilter = "eq.$phone"
                )
                if (response.isSuccessful) {
                    val profilesList = response.body() ?: emptyList()
                    if (isSignUp) {
                        if (profilesList.isNotEmpty()) {
                            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                onResult(false, "رقم الهاتف هذا مسجل بالفعل مسبقاً. تفضل بتسجيل الدخول مباشرة")
                            }
                        } else {
                            val profile = com.example.connectivity.SupabaseProfile(
                                id = java.util.UUID.randomUUID().toString(),
                                phone = phone,
                                name = nameForNewUserIfSignup ?: "مستفيد معاك"
                            )
                            val insertResponse = com.example.connectivity.SupabaseClient.service.insertProfile(
                                apiKey = apiKey,
                                authHeader = authHeader,
                                profile = profile
                            )
                            if (insertResponse.isSuccessful) {
                                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                    _userName.value = nameForNewUserIfSignup ?: "مستفيد معاك"
                                    _userPhone.value = phone
                                    _currentScreen.value = "address_selection"
                                    onResult(true, null)
                                }
                            } else {
                                val err = insertResponse.errorBody()?.string() ?: "خطأ غير معروف"
                                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                    onResult(false, "فشل إنشاء الملف الطبي في قاعدة بيانات Supabase: $err")
                                }
                            }
                        }
                    } else {
                        if (profilesList.isNotEmpty()) {
                            val activeProfile = profilesList.first()
                            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                _userName.value = activeProfile.name
                                _userPhone.value = activeProfile.phone
                                _currentScreen.value = "address_selection"
                                onResult(true, null)
                            }
                        } else {
                            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                onResult(false, "لا يوجد حساب بهذا الرقم في قاعدة البيانات. يرجى الاشتراك كحساب جديد أولاً")
                            }
                        }
                    }
                } else {
                    val errorString = response.errorBody()?.string() ?: "خطأ في الشبكة"
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                        onResult(false, "عذراً، فشل استدعاء بيانات المستخدم من Supabase: $errorString")
                    }
                }
            } catch (e: Exception) {
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    onResult(false, "فشل الاتصال بخادم وخدمة قاعدة بيانات Supabase: ${e.localizedMessage}")
                }
            }
        }
    }

    // Supabase Backed Google/Apple Social Auth integration
    fun performSupabaseSocialLogin(
        email: String,
        fullName: String,
        phoneInput: String,
        provider: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val apiKey = com.example.connectivity.SupabaseClient.getSupabaseKey()
                val authHeader = "Bearer $apiKey"
                
                if (_userRole.value == "ADMIN") {
                    val adminResponse = com.example.connectivity.SupabaseClient.service.getAdmin(
                        apiKey = apiKey,
                        authHeader = authHeader,
                        emailFilter = "eq.$email"
                    )
                    if (!adminResponse.isSuccessful || adminResponse.body().isNullOrEmpty()) {
                        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                            onResult(false, "غير مصرح لك بالدخول بنسخة الإدارة. يرجى مراجعة الصلاحيات.")
                        }
                        return@launch
                    }
                }

                // 1. Try to fetch profile by email
                val response = com.example.connectivity.SupabaseClient.service.getProfileByEmail(
                    apiKey = apiKey,
                    authHeader = authHeader,
                    emailFilter = "eq.$email"
                )
                
                if (response.isSuccessful) {
                    val profilesList = response.body() ?: emptyList()
                    if (profilesList.isNotEmpty()) {
                        // User exists in db with this email, fetch it!
                        val activeProfile = profilesList.first()
                        saveSession(email = email, name = activeProfile.name, phone = activeProfile.phone, provider = provider)
                        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                            _userName.value = activeProfile.name
                            _userPhone.value = activeProfile.phone
                            _currentScreen.value = "address_selection"
                            onResult(true, null)
                        }
                    } else {
                        // Email doesn't exist, search by phone to prevent duplicates
                        val phoneResponse = com.example.connectivity.SupabaseClient.service.getProfile(
                            apiKey = apiKey,
                            authHeader = authHeader,
                            phoneFilter = "eq.$phoneInput"
                        )
                        val phoneProfilesList = if (phoneResponse.isSuccessful) phoneResponse.body() ?: emptyList() else emptyList()
                        
                        if (phoneProfilesList.isNotEmpty()) {
                            // User exists but with different email or none, link or login
                            val activeProfile = phoneProfilesList.first()
                            saveSession(email = email, name = activeProfile.name, phone = activeProfile.phone, provider = provider)
                            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                _userName.value = activeProfile.name
                                _userPhone.value = activeProfile.phone
                                _currentScreen.value = "address_selection"
                                onResult(true, null)
                            }
                        } else {
                            // Totally new user, create a profile inside Supabase
                            val profile = com.example.connectivity.SupabaseProfile(
                                id = java.util.UUID.randomUUID().toString(),
                                phone = phoneInput,
                                name = fullName,
                                email = email
                            )
                            val insertResponse = com.example.connectivity.SupabaseClient.service.insertProfile(
                                apiKey = apiKey,
                                authHeader = authHeader,
                                profile = profile
                            )
                            if (insertResponse.isSuccessful) {
                                saveSession(email = email, name = fullName, phone = phoneInput, provider = provider)
                                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                    _userName.value = fullName
                                    _userPhone.value = phoneInput
                                    _currentScreen.value = "address_selection"
                                    onResult(true, null)
                                }
                            } else {
                                val err = insertResponse.errorBody()?.string() ?: "خطأ أثناء التسجيل"
                                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                    onResult(false, "فشل ربط ملفك الاجتماعي بقاعدة بيانات Supabase: $err")
                                }
                            }
                        }
                    }
                } else {
                    val errorString = response.errorBody()?.string() ?: "خطأ بالشبكة"
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                        onResult(false, "فشل المصادقة مع خادم Supabase: $errorString")
                    }
                }
            } catch (e: Exception) {
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    onResult(false, "عذراً، فشل الاتصال بقاعدة بيانات Supabase: ${e.localizedMessage}")
                }
            }
        }
    }

    fun performLogin(name: String, phone: String) {
        viewModelScope.launch {
            if (name.isNotBlank() && phone.isNotBlank()) {
                saveSession(email = "", name = name, phone = phone, provider = "phone")
                _userName.value = name
                _userPhone.value = phone
                if (_userRole.value == "NURSE" || _userRole.value == "ADMIN") {
                    _currentScreen.value = "home" // HomeScreen wrapper handles it
                } else {
                    _currentScreen.value = "address_selection"
                }
            }
        }
    }

    fun saveUserAddressAndCompleteLogin(fullAddress: String) {
        _userAddress.value = fullAddress
        _bookingAddress.value = fullAddress
        _isLoggedIn.value = true
        // Also persist the chosen address in session prefs
        prefs.edit().putString("saved_address", fullAddress).apply()
        _currentScreen.value = "home"
    }

    fun saveUserAddressAndCompleteLogin(fullAddress: String, lat: Double, lng: Double) {
        _userLat.value = lat
        _userLng.value = lng
        prefs.edit()
            .putFloat("saved_lat", lat.toFloat())
            .putFloat("saved_lng", lng.toFloat())
            .apply()
        saveUserAddressAndCompleteLogin(fullAddress)
    }

    fun performLogout() {
        _isLoggedIn.value = false
        _userName.value = ""
        _userPhone.value = ""
        clearSession()
        _currentScreen.value = "login"
    }

    fun setService(service: MedicalService) {
        _selectedService.value = service
        // reset selected nurse and inputs
        _selectedNurse.value = null
        _bookingNotes.value = ""
    }

    fun selectNurse(nurse: Nurse) {
        _selectedNurse.value = nurse
    }

    fun viewNurseDetails(nurse: Nurse) {
        _viewedNurse.value = nurse
        navigateTo("nurse_details")
    }

    fun setNurseFilter(filter: String) {
        _nurseFilterType.value = filter
    }

    fun updateBookingDetails(date: String, slot: String, address: String, notes: String) {
        _bookingDate.value = date
        _bookingTimeSlot.value = slot
        _bookingAddress.value = address
        _bookingNotes.value = notes
    }

    fun setPaymentMethod(method: String) {
        _paymentMethod.value = method
    }

    fun confirmAndPay() {
        viewModelScope.launch {
            val service = _selectedService.value ?: return@launch
            val nurse = _selectedNurse.value ?: nursesFlow.value.firstOrNull() ?: return@launch

            val totalBill = service.basePrice + nurse.pricePerVisit + 20.0
            if (_paymentMethod.value == "APP_WALLET") {
                val success = payWithWallet(totalBill, service.title)
                if (!success) {
                    // Not enough balance, but we prevent this in UI, handle fallback
                    addNotification(
                        title = "فشل دفع الحجز ⚠️",
                        description = "رصيد المحفظة غير كافٍ لإتمام حجز خدمة ${service.title}",
                        type = "WARNING"
                    )
                    return@launch
                }
            }

            val entity = BookingEntity(
                serviceName = service.title,
                serviceIconName = service.iconName,
                nurseName = nurse.name,
                nurseRating = nurse.rating,
                nurseExperience = nurse.experienceYears,
                price = service.basePrice + nurse.pricePerVisit,
                date = _bookingDate.value,
                timeSlot = _bookingTimeSlot.value,
                address = _bookingAddress.value.ifBlank { _userAddress.value },
                notes = _bookingNotes.value,
                paymentMethod = _paymentMethod.value,
                status = "ON_THE_WAY"
            )

            val insertedId = repository.insertBooking(entity)
            _activeBookingId.value = insertedId.toInt()

            // Sync to Supabase Cloud Back-end
            val finalEntity = entity.copy(id = insertedId.toInt())
            syncBookingToSupabase(finalEntity)

            // Dynamic in-app history notification
            addNotification(
                title = "تم تأكيد طلبك لخدمة ${service.title} بنجاح 🟢",
                description = "تم جدولة الخدمة مع الممرض ${nurse.name} ليوم ${_bookingDate.value} في تمام ${_bookingTimeSlot.value}. العنوان: ${entity.address}",
                type = "CONFIRMATION"
            )

            // Dynamic nurse direct message notification
            addNotification(
                title = "رسالة جديدة من ${nurse.name} 💬",
                description = "مرحباً! أنا في طريقي إليك لتلبية طلبك لخدمة ${service.title}. يمكنك مراسلتي من هنا لأي استفسار.",
                type = "MESSAGE"
            )

            // Trigger Confirmation and Reminder Push Notifications on success
            notificationHelper.sendBookingConfirmation(service.title, nurse.name, _bookingDate.value, _bookingTimeSlot.value)
            notificationHelper.sendUpcomingVisitReminder(service.title, nurse.name, _bookingTimeSlot.value)
            
            // Navigate to tracking
            _currentScreen.value = "tracking"
            
            // Start simulation of tracking
            startTrackingSimulation(insertedId.toInt(), nurse.name)
        }
    }

    fun startTrackingSimulation(bookingId: Int, nurseName: String) {
        trackingJob?.cancel()
        _trackingEtaSeconds.value = 25
        _trackingStatus.value = "ON_THE_WAY"
        _nurseLatOffset.value = 0.05f
        _nurseLngOffset.value = -0.04f

        // Initial chat welcome message based on nurse / doctor visit
        val serviceName = _selectedService.value?.title ?: "الخدمة"
        val isDoc = _selectedService.value?.id == "doctor_visit"
        val senderRole = if (isDoc) "DOCTOR" else "NURSE"
        val initialMsgText = if (isDoc) {
            "السلام عليكم ورحمة الله، أنا الطبيب المخصص لزيارتكم المنزلية اليوم لمتابعة خدمة (${serviceName}). لقد استلمت طلبكم ومستعد تماماً لزيارتكم ومناظرة الحالة والاطمئنان الكامل عليها."
        } else {
            "مرحباً بحضرتك يا فندم، معكم الممرض ${nurseName} لخدمة الرعاية (${serviceName}). أنا حالياً أستعد وسأنطلق إليكم بأسرع وقت لتقديم الرعاية الطبية الفائقة برفق وتفانٍ."
        }
        
        _chatMessages.value = listOf(
            ChatMessage(
                id = "system_init",
                sender = senderRole,
                content = initialMsgText,
                timestamp = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
            )
        )

        // Initial on the way notification
        notificationHelper.sendNurseStatusUpdate("ON_THE_WAY", nurseName)
        addNotification(
            title = "أخصائي معاك في الطريق إليك 📍",
            description = "انطلق الممرض ${nurseName} لتلبية طلب الرعاية الصحية المنزلي لخدمة (${serviceName}) المتكاملة.",
            type = "EN_ROUTE"
        )

        trackingJob = viewModelScope.launch {
            // Simulated tracker timer loop
            while (_trackingEtaSeconds.value > 0) {
                delay(1000)
                _trackingEtaSeconds.value -= 1
                
                // Map coordinates update
                _nurseLatOffset.value -= 0.002f
                _nurseLngOffset.value += 0.0016f

                when (_trackingEtaSeconds.value) {
                    18 -> {
                        _trackingStatus.value = "ARRIVED"
                        updateBookingStatusWithSync(bookingId, "ARRIVED")
                        notificationHelper.sendNurseStatusUpdate("ARRIVED", nurseName)
                        addNotification(
                            title = "وصل الممرض للعنوان المحدد 🚪",
                            description = "يتواجد الممرض ${nurseName} حالياً أمام عنوان تلبية الخدمة المختار لبدء زيارتكم الطبية المعقمة.",
                            type = "ARRIVED"
                        )
                    }
                    10 -> {
                        _trackingStatus.value = "IN_PROGRESS"
                        updateBookingStatusWithSync(bookingId, "IN_PROGRESS")
                        notificationHelper.sendNurseStatusUpdate("IN_PROGRESS", nurseName)
                        addNotification(
                            title = "بدء تنفيذ جلسة الرعاية الطبية 🩺",
                            description = "يقوم الممرض ${nurseName} حالياً بفحص الحالة ومناظرة المؤشرات الدوائية والبدنية وتقديم الخدمة بعناية فائقة.",
                            type = "UPDATE"
                        )
                    }
                }
            }
            _trackingStatus.value = "COMPLETED"
            updateBookingStatusWithSync(bookingId, "COMPLETED")
            notificationHelper.sendNurseStatusUpdate("COMPLETED", nurseName)
            addNotification(
                title = "اكتملت الزيارة الطبية بنجاح تام ✨",
                description = "تم الانتهاء من فحص ومناظرة تلبية طلب الخدمة الخاص بملفكم الرقمي بتطبيق معاك كير الأمني.",
                type = "UPDATE"
            )
            _currentScreen.value = "review"
        }
    }

    fun cancelTrackingAndComplete() {
        // user skips tracking or we immediately complete to test review screen
        trackingJob?.cancel()
        _trackingEtaSeconds.value = 0
        _trackingStatus.value = "COMPLETED"
        viewModelScope.launch {
            updateBookingStatusWithSync(_activeBookingId.value, "COMPLETED")
            // Find current nurse name for notification
            val nurseName = _selectedNurse.value?.name ?: "ممرض المرسي"
            notificationHelper.sendNurseStatusUpdate("COMPLETED", nurseName)
            _currentScreen.value = "review"
        }
    }

    fun cancelBooking(bookingId: Int, nurseName: String) {
        if (_activeBookingId.value == bookingId) {
            trackingJob?.cancel()
            _trackingEtaSeconds.value = 0
        }
        viewModelScope.launch {
            updateBookingStatusWithSync(bookingId, "CANCELLED")
            notificationHelper.sendNurseStatusUpdate("CANCELLED", nurseName)
        }
    }

    fun rebookService(booking: BookingEntity) {
        viewModelScope.launch {
            val service = servicesFlow.value.firstOrNull { it.title == booking.serviceName }
            if (service != null) {
                _selectedService.value = service
                _selectedNurse.value = nursesFlow.value.firstOrNull { it.name == booking.nurseName } ?: nursesFlow.value.first()
                _bookingDate.value = AppData.availableDates.first()
                _bookingTimeSlot.value = AppData.timeSlots.first()
                _currentScreen.value = "booking"
            }
        }
    }

    fun submitReview(rating: Float, comment: String) {
        viewModelScope.launch {
            val bId = _activeBookingId.value
            if (bId != -1) {
                repository.updateBookingReview(bId, rating, comment)
                if (SupabaseClient.isConfigured()) {
                    try {
                        val apiKey = SupabaseClient.getSupabaseKey()
                        val authHeader = "Bearer $apiKey"
                        val updateMap = mapOf(
                            "rating" to rating.toString(),
                            "review_comment" to comment
                        )
                        val response = SupabaseClient.service.updateBooking(
                            apiKey = apiKey,
                            authHeader = authHeader,
                            idFilter = "eq.$bId",
                            bookingUpdate = updateMap
                        )
                        Log.d("SupabaseSync", "Review updated. Code ${response.code()}")
                    } catch (e: Exception) {
                        Log.e("SupabaseSync", "Exception syncing review: ${e.message}")
                    }
                }
            }
            _currentScreen.value = "home"
        }
    }

    fun fetchServicesAndNursesFromSupabase() {
        viewModelScope.launch {
            if (!com.example.connectivity.SupabaseClient.isConfigured()) return@launch
            try {
                val apiKey = com.example.connectivity.SupabaseClient.getSupabaseKey()
                val authHeader = "Bearer $apiKey"
                val serviceRes = com.example.connectivity.SupabaseClient.service.getServices(apiKey, authHeader)
                if (serviceRes.isSuccessful) {
                    val list = serviceRes.body()
                    if (list != null) {
                        db.serviceDao().replaceAll(list.map {
                            ServiceEntity(
                                id = it.id ?: "",
                                title = it.title,
                                description = it.description,
                                longDescription = it.longDescription,
                                basePrice = it.basePrice,
                                duration = it.duration,
                                iconName = it.iconName,
                                imageUrl = it.imageUrl
                            )
                        })
                    }
                }
                
                val nurseRes = com.example.connectivity.SupabaseClient.service.getNurses(apiKey, authHeader)
                if (nurseRes.isSuccessful) {
                    val list = nurseRes.body()
                    if (list != null) {
                        db.nurseDao().replaceAll(list.map {
                            NurseEntity(
                                id = it.id ?: "",
                                name = it.name,
                                rating = it.rating,
                                ratingCount = it.ratingCount,
                                experienceYears = it.experienceYears,
                                pricePerVisit = it.pricePerVisit,
                                gender = it.gender,
                                completedVisits = it.completedVisits,
                                phone = it.phone,
                                hospitalAffiliation = it.hospitalAffiliation,
                                lat = it.lat,
                                lng = it.lng,
                                homeDistrict = it.homeDistrict
                            )
                        })
                    }
                }
                
                val deptRes = com.example.connectivity.SupabaseClient.service.getDepartments(apiKey, authHeader)
                if (deptRes.isSuccessful) {
                    val list = deptRes.body()
                    if (list != null) {
                        db.departmentDao().replaceAll(list.map {
                            DepartmentEntity(
                                id = it.id ?: "",
                                title = it.title,
                                description = it.description,
                                serviceIdsText = it.serviceIdsText
                            )
                        })
                    }
                }

                // Fetch admin dashboard data if user is ADMIN
                if (_userRole.value == "ADMIN") {
                    fetchAdminDashboardData()
                }

            } catch (e: Exception) {
                Log.e("SupabaseSync", "Error fetching services/nurses", e)
            }
        }
    }

    private fun fetchAdminDashboardData() {
        viewModelScope.launch {
            if (!com.example.connectivity.SupabaseClient.isConfigured()) return@launch
            try {
                val apiKey = com.example.connectivity.SupabaseClient.getSupabaseKey()
                val authHeader = "Bearer $apiKey"
                
                // Fetch Nurse Requests
                val requestsRes = com.example.connectivity.SupabaseClient.service.getNurseRequests(apiKey, authHeader)
                if (requestsRes.isSuccessful) {
                    _nurseRequests.value = requestsRes.body() ?: emptyList()
                }

                // Fetch App Settings
                val settingsRes = com.example.connectivity.SupabaseClient.service.getAllSettings(apiKey, authHeader)
                if (settingsRes.isSuccessful) {
                    val settingsList = settingsRes.body() ?: emptyList()
                    val settingsMap = settingsList.associate { it.key to it.value }
                    _appSettings.value = settingsMap
                    if (settingsMap.containsKey("chatbot_system_prompt")) {
                        _chatbotSystemPrompt.value = settingsMap["chatbot_system_prompt"] ?: "أنت مساعد طبي ذكي من معاك كير..."
                    }
                    if (settingsMap.containsKey("chatbot_enabled")) {
                        _chatbotEnabled.value = settingsMap["chatbot_enabled"] == "true"
                    }
                }

                // Fetch Developer Profile
                val devProfileRes = com.example.connectivity.SupabaseClient.service.getDeveloperProfile(apiKey, authHeader)
                if (devProfileRes.isSuccessful) {
                    _developerProfile.value = devProfileRes.body()?.firstOrNull()
                }

                // Fetch App Sliders
                val slidersRes = com.example.connectivity.SupabaseClient.service.getAppSliders(apiKey, authHeader)
                if (slidersRes.isSuccessful) {
                    _appSliders.value = slidersRes.body() ?: emptyList()
                }

                // Fetch Profiles
                val profilesRes = com.example.connectivity.SupabaseClient.service.getAllProfiles(apiKey, authHeader)
                if (profilesRes.isSuccessful) {
                    _allProfiles.value = profilesRes.body() ?: emptyList()
                }

                // Fetch All Bookings
                val bookingsRes = com.example.connectivity.SupabaseClient.service.getBookingsByStatus(apiKey, authHeader, "")
                if (bookingsRes.isSuccessful) {
                    _dashboardBookings.value = bookingsRes.body() ?: emptyList()
                }
            } catch (e: Exception) {
                Log.e("SupabaseSync", "Error fetching admin data", e)
            }
        }
    }

    fun updateDeveloperProfile(id: Int, name: String, title: String, message: String, imageUrl: String) {
        viewModelScope.launch {
            if (!com.example.connectivity.SupabaseClient.isConfigured()) return@launch
            try {
                val apiKey = com.example.connectivity.SupabaseClient.getSupabaseKey()
                val authHeader = "Bearer $apiKey"
                val updateMap = mapOf(
                    "name" to name,
                    "title" to title,
                    "message" to message,
                    "image_url" to imageUrl
                )
                if (id == 0) {
                    com.example.connectivity.SupabaseClient.service.insertDeveloperProfile(apiKey, authHeader, updateMap)
                } else {
                    com.example.connectivity.SupabaseClient.service.updateDeveloperProfile(apiKey, authHeader, "eq.$id", updateMap)
                }
                fetchAdminDashboardData()
            } catch (e: Exception) {
                Log.e("SupabaseSync", "Error updating DeveloperProfile", e)
            }
        }
    }

    fun updateSlider(id: Int, title: String, description: String, imageUrl: String) {
        viewModelScope.launch {
            if (!com.example.connectivity.SupabaseClient.isConfigured()) return@launch
            try {
                val apiKey = com.example.connectivity.SupabaseClient.getSupabaseKey()
                val authHeader = "Bearer $apiKey"
                val updateMap = mapOf(
                    "title" to title,
                    "description" to description,
                    "image_url" to imageUrl
                )
                if (id == 0) {
                    com.example.connectivity.SupabaseClient.service.insertAppSlider(apiKey, authHeader, updateMap)
                } else {
                    com.example.connectivity.SupabaseClient.service.updateAppSlider(apiKey, authHeader, "eq.$id", updateMap)
                }
                fetchAdminDashboardData()
            } catch (e: Exception) {
                Log.e("SupabaseSync", "Error updating AppSlider", e)
            }
        }
    }

    fun updateChatbotSystemPrompt(prompt: String) {
        _chatbotSystemPrompt.value = prompt
        viewModelScope.launch {
            if (!com.example.connectivity.SupabaseClient.isConfigured()) return@launch
            try {
                val apiKey = com.example.connectivity.SupabaseClient.getSupabaseKey()
                val authHeader = "Bearer $apiKey"
                val updateMap = mapOf("value" to prompt)
                com.example.connectivity.SupabaseClient.service.updateSetting(apiKey, authHeader, "eq.chatbot_system_prompt", updateMap)
            } catch (e: Exception) {
                Log.e("SupabaseSync", "Error updating setting", e)
            }
        }
    }

    fun updateChatbotEnabled(enabled: Boolean) {
        _chatbotEnabled.value = enabled
        viewModelScope.launch {
            if (!com.example.connectivity.SupabaseClient.isConfigured()) return@launch
            try {
                val apiKey = com.example.connectivity.SupabaseClient.getSupabaseKey()
                val authHeader = "Bearer $apiKey"
                val updateMap = mapOf("value" to enabled.toString())
                com.example.connectivity.SupabaseClient.service.updateSetting(apiKey, authHeader, "eq.chatbot_enabled", updateMap)
            } catch (e: Exception) {
                Log.e("SupabaseSync", "Error updating chatbot enabled", e)
            }
        }
    }

    fun approveNurseRequest(request: com.example.connectivity.SupabaseNurseRequest) {
        viewModelScope.launch {
            if (!com.example.connectivity.SupabaseClient.isConfigured()) return@launch
            try {
                val apiKey = com.example.connectivity.SupabaseClient.getSupabaseKey()
                val authHeader = "Bearer $apiKey"
                
                // Insert into nurses
                val id = request.id ?: java.util.UUID.randomUUID().toString()
                val newNurse = com.example.connectivity.SupabaseNurse(
                    id = id,
                    name = request.name,
                    rating = 5.0,
                    ratingCount = 0,
                    experienceYears = request.experienceYears,
                    pricePerVisit = 200.0,
                    gender = request.gender,
                    completedVisits = 0,
                    phone = request.phone,
                    hospitalAffiliation = request.hospitalAffiliation,
                    lat = 30.0444,
                    lng = 31.2357,
                    homeDistrict = request.homeDistrict,
                    status = "ACTIVE"
                )
                val insertRes = com.example.connectivity.SupabaseClient.service.insertNurse(apiKey, authHeader, newNurse)
                
                if (insertRes.isSuccessful) {
                    // Update request status
                    com.example.connectivity.SupabaseClient.service.updateNurseRequest(
                        apiKey, authHeader, "eq.${request.id}", mapOf("status" to "APPROVED")
                    )
                    fetchAdminDashboardData() // Refresh
                }
            } catch (e: Exception) {
                Log.e("SupabaseSync", "Error approving nurse", e)
            }
        }
    }

    fun rejectNurseRequest(request: com.example.connectivity.SupabaseNurseRequest) {
        viewModelScope.launch {
            if (!com.example.connectivity.SupabaseClient.isConfigured()) return@launch
            try {
                val apiKey = com.example.connectivity.SupabaseClient.getSupabaseKey()
                val authHeader = "Bearer $apiKey"
                com.example.connectivity.SupabaseClient.service.updateNurseRequest(
                    apiKey, authHeader, "eq.${request.id}", mapOf("status" to "REJECTED")
                )
                fetchAdminDashboardData() // Refresh
            } catch (e: Exception) {
                Log.e("SupabaseSync", "Error rejecting nurse", e)
            }
        }
    }

    fun updateServiceInSupabase(serviceId: String, updates: Map<String, String>) {
        viewModelScope.launch {
            if (!com.example.connectivity.SupabaseClient.isConfigured()) return@launch
            try {
                val apiKey = com.example.connectivity.SupabaseClient.getSupabaseKey()
                val authHeader = "Bearer $apiKey"
                com.example.connectivity.SupabaseClient.service.updateService(apiKey, authHeader, "eq.$serviceId", updates)
                fetchServicesAndNursesFromSupabase() // Refresh Room + State
            } catch (e: Exception) {
                Log.e("SupabaseSync", "Error updating service", e)
            }
        }
    }

    fun addDepartment(title: String, description: String, serviceIds: String) {
        viewModelScope.launch {
            val id = "dept_${System.currentTimeMillis()}"
            db.departmentDao().insertDepartment(DepartmentEntity(id, title, description, serviceIds))
            if (!com.example.connectivity.SupabaseClient.isConfigured()) return@launch
            try {
                val apiKey = com.example.connectivity.SupabaseClient.getSupabaseKey()
                val authHeader = "Bearer $apiKey"
                val supaDept = com.example.connectivity.SupabaseDepartment(
                    id = id,
                    title = title,
                    description = description,
                    serviceIdsText = serviceIds
                )
                com.example.connectivity.SupabaseClient.service.insertDepartment(apiKey, authHeader, supaDept)
            } catch (e: Exception) {
                Log.e("SupabaseSync", "Error inserting department", e)
            }
        }
    }

    fun addServiceToSupabase(service: MedicalService) {
        viewModelScope.launch {
            db.serviceDao().insertService(ServiceEntity(service.id, service.title, service.description, service.longDescription, service.basePrice, service.duration, service.iconName, service.imageUrl))
            if (!com.example.connectivity.SupabaseClient.isConfigured()) return@launch
            try {
                val apiKey = com.example.connectivity.SupabaseClient.getSupabaseKey()
                val authHeader = "Bearer $apiKey"
                val supaService = com.example.connectivity.SupabaseMedicalService(
                    title = service.title,
                    description = service.description,
                    longDescription = service.longDescription,
                    basePrice = service.basePrice,
                    duration = service.duration,
                    iconName = service.iconName,
                    imageUrl = service.imageUrl
                )
                com.example.connectivity.SupabaseClient.service.insertService(apiKey, authHeader, supaService)
            } catch (e: Exception) {
                Log.e("SupabaseSync", "Error inserting service", e)
            }
        }
    }

    fun deleteService(service: MedicalService) {
        viewModelScope.launch {
            db.serviceDao().deleteService(service.id)
            if (SupabaseClient.isConfigured()) {
                val apiKey = SupabaseClient.getSupabaseKey()
                val authHeader = "Bearer $apiKey"
                try {
                    SupabaseClient.service.deleteService(apiKey, authHeader, "eq.${service.id}")
                } catch (e: Exception) {
                    Log.e("SupabaseSync", "Error deleting service", e)
                }
            }
        }
    }

    fun deleteNurse(nurse: Nurse) {
        viewModelScope.launch {
            db.nurseDao().deleteNurse(nurse.id)
            if (SupabaseClient.isConfigured()) {
                val apiKey = SupabaseClient.getSupabaseKey()
                val authHeader = "Bearer $apiKey"
                try {
                    SupabaseClient.service.deleteNurse(apiKey, authHeader, "eq.${nurse.id}")
                } catch (e: Exception) {
                    Log.e("SupabaseSync", "Error deleting nurse", e)
                }
            }
        }
    }

    fun deleteDepartment(dept: DepartmentEntity) {
        viewModelScope.launch {
            db.departmentDao().deleteDepartment(dept.id)
            if (SupabaseClient.isConfigured()) {
                val apiKey = SupabaseClient.getSupabaseKey()
                val authHeader = "Bearer $apiKey"
                try {
                    SupabaseClient.service.deleteDepartment(apiKey, authHeader, "eq.${dept.id}")
                } catch (e: Exception) {
                    Log.e("SupabaseSync", "Error deleting department", e)
                }
            }
        }
    }

    fun updateDepartment(deptId: String, title: String, description: String, serviceIdsText: String) {
        viewModelScope.launch {
            val updated = DepartmentEntity(deptId, title, description, serviceIdsText)
            db.departmentDao().insertDepartment(updated)
            if (SupabaseClient.isConfigured()) {
                val apiKey = SupabaseClient.getSupabaseKey()
                val authHeader = "Bearer $apiKey"
                try {
                    val apiDept = com.example.connectivity.SupabaseDepartment(deptId, title, description, serviceIdsText)
                    SupabaseClient.service.updateDepartment(apiKey, authHeader, "eq.$deptId", apiDept)
                } catch (e: Exception) {
                    Log.e("SupabaseSync", "Error updating department", e)
                }
            }
        }
    }

    fun approveNurse(nurse: Nurse) {
        viewModelScope.launch {
            db.nurseDao().insertNurse(NurseEntity(nurse.id, nurse.name, nurse.rating, nurse.ratingCount, nurse.experienceYears, nurse.pricePerVisit, nurse.gender, nurse.completedVisits, nurse.phone, nurse.hospitalAffiliation, nurse.lat, nurse.lng, nurse.homeDistrict))
        }
    }

    fun syncBookingToSupabase(booking: BookingEntity) {
        viewModelScope.launch {
            if (!SupabaseClient.isConfigured()) {
                Log.d("SupabaseSync", "Supabase is not configured.")
                return@launch
            }
            try {
                val apiKey = SupabaseClient.getSupabaseKey()
                val authHeader = "Bearer $apiKey"
                val supabaseBooking = SupabaseBooking(
                    id = booking.id,
                    serviceName = booking.serviceName,
                    serviceIconName = booking.serviceIconName,
                    nurseName = booking.nurseName,
                    nurseRating = booking.nurseRating,
                    nurseExperience = booking.nurseExperience,
                    price = booking.price,
                    date = booking.date,
                    timeSlot = booking.timeSlot,
                    address = booking.address,
                    notes = booking.notes,
                    paymentMethod = booking.paymentMethod,
                    status = booking.status,
                    timestamp = booking.timestamp,
                    rating = booking.rating,
                    reviewComment = booking.reviewComment
                )
                Log.d("SupabaseSync", "Inserting booking ${booking.id} to Supabase REST endpoint...")
                val response = SupabaseClient.service.insertBooking(
                    apiKey = apiKey,
                    authHeader = authHeader,
                    booking = supabaseBooking
                )
                if (response.isSuccessful) {
                    Log.d("SupabaseSync", "Booking ${booking.id} inserted successfully in Supabase.")
                } else {
                    val err = response.errorBody()?.string() ?: ""
                    Log.e("SupabaseSync", "Insert failed, details: $err")
                    // If table exists but record conflict occurs:
                    if (response.code() == 409 || err.contains("duplicate") || err.contains("already exists")) {
                        val updateMap = mapOf(
                            "status" to booking.status,
                            "rating" to booking.rating.toString(),
                            "review_comment" to booking.reviewComment
                        )
                        val patchRes = SupabaseClient.service.updateBooking(
                            apiKey = apiKey,
                            authHeader = authHeader,
                            idFilter = "eq.${booking.id}",
                            bookingUpdate = updateMap
                        )
                        Log.d("SupabaseSync", "Update/Patch result: ${patchRes.code()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("SupabaseSync", "Failed to sync insert: ${e.message}")
            }
        }
    }

    fun updateBookingStatusWithSync(bookingId: Int, status: String) {
        viewModelScope.launch {
            repository.updateBookingStatus(bookingId, status)
            if (SupabaseClient.isConfigured()) {
                try {
                    val apiKey = SupabaseClient.getSupabaseKey()
                    val authHeader = "Bearer $apiKey"
                    val updateMap = mapOf(
                        "status" to status
                    )
                    Log.d("SupabaseSync", "Updating status to $status for booking $bookingId in Supabase...")
                    val response = SupabaseClient.service.updateBooking(
                        apiKey = apiKey,
                        authHeader = authHeader,
                        idFilter = "eq.$bookingId",
                        bookingUpdate = updateMap
                    )
                    if (response.isSuccessful) {
                        Log.d("SupabaseSync", "Status updated successfully in Supabase.")
                    } else {
                        Log.e("SupabaseSync", "Status update failed, details: ${response.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    Log.e("SupabaseSync", "Failed to sync status update: ${e.message}")
                }
            }
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAllBookings()
        }
    }

    private fun generateAutomatedReply(msg: String): String {
        val lower = msg.lowercase()
        val nurseName = _selectedNurse.value?.name ?: "مختص الرعاية"
        return when {
            lower.contains("السلام") || lower.contains("سلام") || lower.contains("مرحبا") || lower.contains("مرحب") || lower.contains("أهلا") || lower.contains("اهلا") -> {
                "أهلاً وسهلاً بحضرتك يا فندم، وعليكم السلام ورحمة الله وبركاته. كيف يمكنني مساعدتكم في تيسير هذه الزيارة الصحية الآن؟"
            }
            lower.contains("تاخر") || lower.contains("تأخر") || lower.contains("متى") || lower.contains("موعد") || lower.contains("الوقت") || lower.contains("وين") || lower.contains("فين") || lower.contains("وصلت") || lower.contains("متبقي") -> {
                "أنا جاري في الطريق إليكم حالياً وبفضل الله متبقي دقائق معدودة وأصل أمام الباب. يرجى الاطمئنان وبإمكانكم رؤية موقعي على تتبع الخريطة اللحظي."
            }
            lower.contains("تجهيز") || lower.contains("احضر") || lower.contains("جيب") || lower.contains("معاك") || lower.contains("روشت") || lower.contains("دواء") || lower.contains("العلاج") -> {
                "لا تقلق أبداً، لقد أحضرت معي كافة المستلزمات الطبية والأدوات المعقمة الخاصة بتقديم الخدمة بالكامل. فقط يرجى الاستعداد بالروشتة الطبية للدواء."
            }
            lower.contains("حالة") || lower.contains("تعب") || lower.contains("وجع") || lower.contains("الم") || lower.contains("ألم") || lower.contains("سكر") || lower.contains("ضغط") || lower.contains("نزيف") || lower.contains("حرار") -> {
                "أرجو تمديد المريض برفق وتهوية المكان ليتنفس بانتظام تام وتجنب إعطائه أي عقار عشوائي. أنا أسرع بأقصى ما لدي وسأفحص المؤشرات الحيوية فوراً عند دخولي."
            }
            lower.contains("شكرا") || lower.contains("تسلم") || lower.contains("جزاك") || lower.contains("تمام") || lower.contains("ماشي") -> {
                "هذا واجبنا المهني والإنساني تجاهكم. نسأل الله العظيم أن يكتب الشفاء العاجل والتام للمريض الكريم."
            }
            else -> {
                "تمام متفهم جداً يا فندم. قمت بتسجيل ملحوظتكم وسأحرص على اتخاذ كل التدابير اللازمة لراحة المريض فور وصولي في غضون دقائق."
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        trackingJob?.cancel()
    }
}

class ELmorsyViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ELmorsyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ELmorsyViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
