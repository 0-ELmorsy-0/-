package com.example.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class MedicalService(
    val id: String,
    val title: String,
    val description: String,
    val longDescription: String,
    val basePrice: Double,
    val duration: String,
    val iconName: String, // string identifier for icons
    val imageUrl: String = "" // professional Unsplash image for dynamic UI Cards
)

data class Nurse(
    val id: String,
    val name: String,
    val rating: Double,
    val ratingCount: Int,
    val experienceYears: Int,
    val pricePerVisit: Double,
    val gender: String, // "MALE" or "FEMALE"
    val completedVisits: Int,
    val phone: String,
    val hospitalAffiliation: String,
    val lat: Double,
    val lng: Double,
    val homeDistrict: String
)

object AppData {
    var services = mutableListOf(
        MedicalService(
            id = "nursing",
            title = "تمريض منزلي",
            description = "رعاية تمريضية متكاملة لجميع الحالات الصحية بالمنزل",
            longDescription = "نوفر لك كادرًا تمريضيًا مرخصًا ومؤهلاً لتقديم الرعاية الطبية الشاملة بالمنزل، بما في ذلك قياس العلامات الحيوية، متابعة الأدوية ومستوى السكر، وتقديم الدعم الصحي اليومي للمرضى بعد العمليات وطريحي الفراش.",
            basePrice = 250.0,
            duration = "ساعة إلى ساعتين",
            iconName = "Healing",
            imageUrl = "https://images.unsplash.com/photo-1576765608535-5f04d1e3f289?auto=format&fit=crop&w=600&q=80"
        ),
        MedicalService(
            id = "elderly_care",
            title = "رعاية كبار السن",
            description = "رعاية شاملة ومرافقة لكبار السن صحياً ونفسياً",
            longDescription = "مرافقة صحية مخصصة لكبار السن لتلبية احتياجاتهم اليومية والطبية، وضمان سلامتها البدنية والنفسية، ومساعدتهم في التغذية، النظافة الشخصية، ممارسة التمارين البسيطة، ومواعيد الفحوصات الطبية.",
            basePrice = 350.0,
            duration = "زيارة 3 ساعات",
            iconName = "Accessibility",
            imageUrl = "https://images.unsplash.com/photo-1581578731548-c64695cc6952?auto=format&fit=crop&w=600&q=80"
        ),
        MedicalService(
            id = "injections",
            title = "حقن ومحاليل",
            description = "إعطاء كافة أنواع الحقن وتركيب المحاليل الطبية",
            longDescription = "خدمة سريعة ومعقمة لتركيب الكانيولا، إعطاء المحاليل الوريدية، والحقن تحت الجلد أو في العضل، والتأكد من مطابقة الجرعات للتعليمات الطبية مع اتخاذ أقصى درجات الوقاية السلامة.",
            basePrice = 120.0,
            duration = "زيارة سريعة (٣٠ دقيقة)",
            iconName = "Medication",
            imageUrl = "https://images.unsplash.com/photo-1516549655169-df83a0774514?auto=format&fit=crop&w=600&q=80"
        ),
        MedicalService(
            id = "wounds",
            title = "تغيير جروح",
            description = "العناية بالجروح المعقدة والغيار الجراحي وقرح الفراش",
            longDescription = "تنظيف وتطهير الجروح بعد العمليات الجراحية أو الإصابات، وتغيير الغيارات المعقمة بأحدث المواد الطبية لضمان سرعة الالتئام، وعلاج قرح الفراش والقدم السكري بمهارة فائقة وآمنة.",
            basePrice = 180.0,
            duration = "زيارة ساعة واحدة",
            iconName = "LocalHospital",
            imageUrl = "https://images.unsplash.com/photo-1584515979956-d9f6e5d09982?auto=format&fit=crop&w=600&q=80"
        ),
        MedicalService(
            id = "physiotherapy",
            title = "علاج طبيعي",
            description = "جلسات تأهيل وعلاج طبيعي حركي لتحسين الحركة",
            longDescription = "جلسات تأهيل حركي وعلاج طبيعي على يد أخصائيين متميزين لحالات الشلل، الجلطات، آلام الظهر والمفاصل، وتأهيل ما بعد العمليات الجراحية لرفع الكفاءة البدنية.",
            basePrice = 300.0,
            duration = "جلسة ساعة كاملة",
            iconName = "SelfImprovement",
            imageUrl = "https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?auto=format&fit=crop&w=600&q=80"
        ),
        MedicalService(
            id = "doctor_visit",
            title = "زيارة طبيب",
            description = "كشف طبي تخصصي في منزلك دون عناء الذهاب للمشفى",
            longDescription = "يتوجه طبيب أخصائي إلى منزلك لتقديم فحص إكلينيكي دقيق، تشخيص الحالة الصحية، تحديد بروتوكولات الأدوية، وحجز الفحوصات الإضافية المناسبة لحالة المريض بكل راحة وأمان.",
            basePrice = 500.0,
            duration = "زيارة ساعة كاملة",
            iconName = "SupervisorAccount",
            imageUrl = "https://images.unsplash.com/photo-1559839734-2b71ea197ec2?auto=format&fit=crop&w=600&q=80"
        )
    )

    var nurses = mutableListOf(
        Nurse(
            id = "nurse_1",
            name = "م. أحمد الشافعي",
            rating = 4.9,
            ratingCount = 142,
            experienceYears = 7,
            pricePerVisit = 250.0,
            gender = "MALE",
            completedVisits = 412,
            phone = "٠١٠١٢٣٤٥٦٧٨",
            hospitalAffiliation = "مستشفى القصر العيني",
            lat = 30.0152,
            lng = 31.4116,
            homeDistrict = "التجمع الخامس"
        ),
        Nurse(
            id = "nurse_2",
            name = "م. فاطمة الزهراء",
            rating = 4.8,
            ratingCount = 98,
            experienceYears = 5,
            pricePerVisit = 280.0,
            gender = "FEMALE",
            completedVisits = 320,
            phone = "٠١٢٣٤٥٦٧٨٩٠",
            hospitalAffiliation = "مستشفيات جامعة عين شمس",
            lat = 30.0398,
            lng = 31.2132,
            homeDistrict = "الدقي"
        ),
        Nurse(
            id = "nurse_3",
            name = "م. محمد الصاوي",
            rating = 4.9,
            ratingCount = 215,
            experienceYears = 9,
            pricePerVisit = 300.0,
            gender = "MALE",
            completedVisits = 580,
            phone = "٠١١٢٢٣٣٤٤٥٥",
            hospitalAffiliation = "مستشفى مبرة المعادي",
            lat = 29.9634,
            lng = 31.2501,
            homeDistrict = "المعادي"
        ),
        Nurse(
            id = "nurse_4",
            name = "م. سارة الجمال",
            rating = 4.7,
            ratingCount = 76,
            experienceYears = 4,
            pricePerVisit = 230.0,
            gender = "FEMALE",
            completedVisits = 190,
            phone = "٠١٥٥٦٦٧٧٨٨٩",
            hospitalAffiliation = "المستشفى الإيطالي بالقاهرة",
            lat = 30.1034,
            lng = 31.3323,
            homeDistrict = "مصر الجديدة"
        ),
        Nurse(
            id = "nurse_5",
            name = "م. محمود رضوان",
            rating = 4.85,
            ratingCount = 112,
            experienceYears = 8,
            pricePerVisit = 270.0,
            gender = "MALE",
            completedVisits = 280,
            phone = "٠١٠٩٩٨٨٧٧٦٦",
            hospitalAffiliation = "مستشفى تبارك للأطفال ومتابعة البالغين",
            lat = 30.0543,
            lng = 31.2012,
            homeDistrict = "المهندسين"
        ),
        Nurse(
            id = "nurse_6",
            name = "م. ياسمين طاهر",
            rating = 4.9,
            ratingCount = 156,
            experienceYears = 6,
            pricePerVisit = 260.0,
            gender = "FEMALE",
            completedVisits = 310,
            phone = "٠١٢٨٨٧٧٦٦٥٥",
            hospitalAffiliation = "مستشفى دار الدفاع الجوي برابعة",
            lat = 30.0614,
            lng = 31.3371,
            homeDistrict = "مدينة نصر"
        )
    )

    val timeSlots = listOf(
        "٠٩:٠٠ ص - ١١:٠٠ ص",
        "١١:٠٠ ص - ٠١:٠٠ م",
        "٠١:٠٠ م - ٠٣:٠٠ م",
        "٠٣:٠٠ م - ٠٥:٠٠ م",
        "٠٥:٠٠ م - ٠٧:٠٠ م",
        "٠٧:٠٠ م - ٠٩:٠٠ م",
        "٠٩:٠٠ م - ١١:٠٠ م"
    )
    
    val availableDates = listOf(
        "اليوم، الثلاثاء ٣ يونيو",
        "غداً، الأربعاء ٤ يونيو",
        "الخميس، ٥ يونيو",
        "الجمعة، ٦ يونيو",
        "السبت، ٧ يونيو",
        "الأحد، ٨ يونيو"
    )
}

data class ChatMessage(
    val id: String,
    val sender: String, // "PATIENT" or "NURSE" or "DOCTOR"
    val content: String,
    val timestamp: String,
    val isSystem: Boolean = false
)

data class InAppNotification(
    val id: String,
    val title: String,
    val description: String,
    val time: String,
    val type: String, // "CONFIRMATION", "ARRIVED", "EN_ROUTE", "UPDATE", "ALERT"
    val isRead: Boolean = false
)

data class MedicalDocument(
    val id: String,
    val name: String,
    val date: String,
    val size: String = "٢.٤ ميجابايت"
)

data class MedicationReminder(
    val id: String,
    val medName: String,
    val dosage: String,
    val time: String, // e.g. "٠٨:٠٠ م"
    val isTaken: Boolean = false,
    val daysOfWeek: String = "يومياً"
)

data class WalletTransaction(
    val id: String,
    val title: String,
    val amount: Double,
    val isCredit: Boolean, // true for recharge/refund, false for deduction
    val date: String,
    val paymentMethodSymbol: String // 💸, 💳, 📱
)

data class VitalLog(
    val id: String,
    val type: String, // "HEART", "PRESSURE", "SUGAR"
    val value: String,
    val timestamp: String,
    val note: String = ""
)

// Extension function for nurse expert badges
fun Nurse.getExpertBadges(): List<String> {
    return when (id) {
        "nurse_1" -> listOf("رعاية كبار السن 👴", "سريع الاستجابة ⚡", "رعاية مكثفة 🏥")
        "nurse_2" -> listOf("العناية بالجروح 🩹", "رعاية الأطفال 👶", "ملاك الرحمة 🤍")
        "nurse_3" -> listOf("حقن وريرية 💉", "أخصائي محاليل 🩹", "استشاري مرافقة 🛡️")
        "nurse_4" -> listOf("رعاية كبار السن 👴", "ملاك الرحمة 🤍", "حقن تحت الجلد 💉")
        "nurse_5" -> listOf("رعاية أطفال 👶", "تركيب الكانيولا 🩹", "سريع الاستجابة ⚡")
        "nurse_6" -> listOf("القدم السكري 🦶", "تغيير معقم 🩺", "مساعد طبي 🛡️")
        else -> listOf("ممرض معتمد 🩺", "سريع الاستجابة ⚡")
    }
}


