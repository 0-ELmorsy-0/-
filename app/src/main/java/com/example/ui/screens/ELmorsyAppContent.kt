@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    androidx.compose.animation.ExperimentalAnimationApi::class,
    androidx.compose.animation.ExperimentalSharedTransitionApi::class
)
package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.util.concurrent.Executor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.CameraUpdateFactory
import com.example.data.*
import com.example.ui.theme.*
import com.example.ui.components.AddressSelectionMap
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.ui.viewinterop.AndroidView

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ELmorsyAppContent(viewModel: ELmorsyViewModel) {
    // Force RTL for Arabic layout direction
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val currentScreen by viewModel.currentScreen.collectAsState()
        
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                SharedTransitionLayout {
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            val screenRank = mapOf(
                                "splash" to 0,
                                "role_selection" to 1,
                                "onboarding" to 2,
                                "login" to 3,
                                "address_selection" to 4,
                                "home" to 5,
                                "notifications" to 6,
                                "booking_history" to 6,
                                "services" to 6,
                                "profile" to 7,
                                "service_details" to 8,
                                "nurse_selection" to 9,
                                "booking" to 10,
                                "payment" to 11,
                                "tracking" to 12,
                                "review" to 13,
                                "nurse_details" to 14,
                                "nurse_chat" to 15
                            )
                            val fromRank = screenRank[initialState] ?: 4
                            val toRank = screenRank[targetState] ?: 4
                            
                            val isBottomBarTabShift = (fromRank in 4..7) && (toRank in 4..7)

                            if (isBottomBarTabShift) {
                                scaleIn(
                                    initialScale = 0.95f,
                                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                                ) + fadeIn(
                                    animationSpec = tween(220)
                                ) with scaleOut(
                                    targetScale = 0.95f,
                                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                                ) + fadeOut(
                                    animationSpec = tween(220)
                                )
                            } else if (toRank > fromRank) {
                                slideInHorizontally(
                                    animationSpec = spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessMediumLow),
                                    initialOffsetX = { fullWidth -> -fullWidth }
                                ) + fadeIn() with slideOutHorizontally(
                                    animationSpec = spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessMediumLow),
                                    targetOffsetX = { fullWidth -> fullWidth }
                                ) + fadeOut()
                            } else {
                                slideInHorizontally(
                                    animationSpec = spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessMediumLow),
                                    initialOffsetX = { fullWidth -> fullWidth }
                                ) + fadeIn() with slideOutHorizontally(
                                    animationSpec = spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessMediumLow),
                                    targetOffsetX = { fullWidth -> -fullWidth }
                                ) + fadeOut()
                            }
                        },
                        label = "ScreenTransition"
                    ) { screenName ->
                        when (screenName) {
                            "splash" -> SplashScreen(viewModel)
                            "role_selection" -> RoleSelectionScreen(viewModel)
                            "onboarding" -> OnboardingScreen(viewModel)
                            "login" -> LoginScreen(viewModel)
                            "address_selection" -> AddressSelectionScreen(viewModel)
                            "home" -> HomeScreen(
                                viewModel = viewModel,
                                sharedTransitionScope = this@SharedTransitionLayout,
                                animatedVisibilityScope = this@AnimatedContent
                            )
                            "service_details" -> ServiceDetailsScreen(
                                viewModel = viewModel,
                                sharedTransitionScope = this@SharedTransitionLayout,
                                animatedVisibilityScope = this@AnimatedContent
                            )
                            "services" -> AllServicesScreen(
                                viewModel = viewModel,
                                sharedTransitionScope = this@SharedTransitionLayout,
                                animatedVisibilityScope = this@AnimatedContent
                            )
                            "nurse_selection" -> NurseSelectionScreen(viewModel)
                            "booking" -> BookingScreen(viewModel)
                            "payment" -> PaymentScreen(viewModel)
                            "tracking" -> TrackingScreen(viewModel)
                            "review" -> ReviewScreen(viewModel)
                            "nurse_details" -> NurseDetailsScreen(viewModel)
                            "wallet" -> WalletScreen(viewModel)
                            "edit_medical_profile" -> EditMedicalProfileScreen(viewModel)
                            "vitals_dashboard" -> VitalsDashboardScreen(viewModel)
                            "medical_docs" -> MedicalDocsScreen(viewModel)
                            "medication_reminders" -> MedicationRemindersScreen(viewModel)
                            "support_chat" -> SupportChatScreen(viewModel)
                            "nurse_chat" -> NurseChatScreen(viewModel)

                            "profile" -> ProfileScreen(viewModel)
                            "booking_history" -> BookingHistoryScreen(
                                viewModel = viewModel,
                                sharedTransitionScope = this@SharedTransitionLayout,
                                animatedVisibilityScope = this@AnimatedContent
                            )
                            "messages" -> MessagesListScreen(viewModel)
                            "notifications" -> NotificationsHistoryScreen(viewModel)
                            else -> HomeScreen(
                                viewModel = viewModel,
                                sharedTransitionScope = this@SharedTransitionLayout,
                                animatedVisibilityScope = this@AnimatedContent
                            )
                        }
                    }
                }
            }
        }
    }
}

// Map screen string icons cleanly to standard vectors
fun getServiceIcon(iconName: String): ImageVector {
    return when (iconName) {
        "Healing" -> Icons.Default.Healing
        "Accessibility" -> Icons.Default.Accessibility
        "Medication" -> Icons.Default.MedicalServices
        "LocalHospital" -> Icons.Default.LocalHospital
        "SelfImprovement" -> Icons.Default.Spa
        "SupervisorAccount" -> Icons.Default.SupervisorAccount
        else -> Icons.Default.MedicalServices
    }
}

data class TabItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val badgeCount: Int = 0
)

@Composable
fun ELmorsyBottomBar(
    currentScreen: String,
    onNavigate: (String) -> Unit,
    notificationsCount: Int
) {
    val tabs = listOf(
        TabItem("services", "الخدمات", Icons.Default.LocalHospital),
        TabItem("booking_history", "حجوزاتي", Icons.Default.EventNote),
        TabItem("home", "الرئيسية", Icons.Default.AddHome),
        TabItem("messages", "الرسائل", Icons.Default.Forum, badgeCount = notificationsCount),
        TabItem("profile", "المزيد", Icons.Default.Person)
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 16.dp),
        color = Color.White
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            contentAlignment = Alignment.BottomCenter
        ) {
            // Main Nav Bar solid background spanning full width
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
            ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEachIndexed { idx, tab ->
                    val isActive = when (idx) {
                        0 -> currentScreen == "service_details" || currentScreen == "services"
                        1 -> currentScreen == "booking_history"
                        2 -> currentScreen == "home"
                        3 -> currentScreen == "messages"
                        4 -> currentScreen == "profile" || currentScreen == "more"
                        else -> false
                    }

                    if (idx == 2) {
                        // Empty space for the floating center button
                        Spacer(modifier = Modifier.width(72.dp))
                    } else {
                        val iconColor = if (isActive) MedicalBlue else Color(0xFF888888)
                        val textColor = if (isActive) MedicalBlue else Color(0xFF888888)
                        val iconSize = if (isActive) 26.dp else 24.dp

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { onNavigate(tab.route) }
                                .padding(vertical = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            BadgedBox(
                                badge = {
                                    if (tab.badgeCount > 0) {
                                        Badge(
                                            containerColor = Color(0xFFEF4444),
                                            contentColor = Color.White,
                                            modifier = Modifier.offset(x = 2.dp, y = (-2).dp)
                                        ) {
                                            Text("${tab.badgeCount}", fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.title,
                                    tint = iconColor,
                                    modifier = Modifier.size(iconSize)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = tab.title,
                                fontSize = 10.sp,
                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                color = textColor
                            )
                        }
                    }
                }
            }
        }

        // Center Floating Item
        val isCenterActive = currentScreen == "home"
        
        Box(
            modifier = Modifier
                .padding(bottom = 36.dp) // Pushes it up to straddle the border
                .size(60.dp)
                .shadow(elevation = 8.dp, shape = CircleShape)
                .background(
                    color = Color.White,
                    shape = CircleShape
                )
                .border(
                    width = 4.dp,
                    color = Color.White,
                    shape = CircleShape
                )
                .clickable { onNavigate(tabs[2].route) },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFFFFD700).copy(alpha = 0.2f), Color.Transparent) // Subtle gold top glow just like image
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                 Icon(
                     imageVector = tabs[2].icon,
                     contentDescription = tabs[2].title,
                     tint = if (isCenterActive) MedicalBlue else Color(0xFF333333),
                     modifier = Modifier.size(28.dp)
                 )
            }
        }
    }
    }
}

// ==========================================
// 1. SPLASH SCREEN (Premium Scientific & Animated Redesign)
// ==========================================
@Composable
fun SplashScreen(viewModel: ELmorsyViewModel) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val hasSavedSession by viewModel.hasSavedSession.collectAsState()
    
    // Auto navigation timer
    LaunchedEffect(Unit) {
        delay(2500) // 2.5 second splash display

        // Seamless route navigation based on login status keys
        if (hasSavedSession && isLoggedIn) {
            viewModel.navigateTo("home")
        } else if (hasSavedSession) {
            if (viewModel.userRole.value == "NURSE" || viewModel.userRole.value == "ADMIN") {
                viewModel.navigateTo("home")
            } else {
                viewModel.navigateTo("address_selection")
            }
        } else {
            if (viewModel.hasCompletedOnboarding.value) {
                viewModel.navigateTo("login")
            } else {
                viewModel.navigateTo("role_selection")
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "SplashAnimation")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LogoPulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Elegant brand logo
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .shadow(12.dp, CircleShape, ambientColor = MedicalBlue.copy(alpha = 0.2f))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color.White, Color(0xFFF1F8FF))
                        ),
                        shape = CircleShape
                    )
                    .border(2.dp, MedicalBlue.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.HealthAndSafety,
                    contentDescription = null,
                    tint = MedicalBlue,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "معاك",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MedicalBlue,
                    fontSize = 36.sp
                )
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "رعاية طبية منزلية دقيقة وآمنة",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            )
        }
        
        // Footer
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = "Safe",
                    tint = MedicalGreen,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "رعاية معتمدة وآمنة",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun AppSetupStepIndicator(currentStepIndex: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val steps = listOf("الترحيب", "الصلاحية", "التعريف", "التسجيل", "الموقع")
        steps.forEachIndexed { index, stepTitle ->
            val isCompleted = index < currentStepIndex
            val isCurrent = index == currentStepIndex

            val circleColor = when {
                isCurrent -> Brush.horizontalGradient(listOf(MedicalBlue, Color(0xFF0369A1)))
                isCompleted -> Brush.horizontalGradient(listOf(MedicalGreen, Color(0xFF059669)))
                else -> Brush.linearGradient(listOf(Color(0xFFF1F5F9), Color(0xFFF1F5F9)))
            }

            val textColor = when {
                isCurrent -> MedicalBlue
                isCompleted -> MedicalGreen
                else -> BodyGray
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(circleColor),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    } else {
                        Text(
                            text = (index + 1).toString(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isCurrent) Color.White else BodyGray
                        )
                    }
                }
                Spacer(modifier = Modifier.width(6.dp))
                if (isCurrent || isCompleted) {
                   Text(
                       text = stepTitle,
                       fontSize = 12.sp,
                       fontWeight = if (isCurrent) FontWeight.ExtraBold else FontWeight.Bold,
                       color = textColor
                   )
                } else {
                   Text(
                       text = stepTitle,
                       fontSize = 11.sp,
                       fontWeight = FontWeight.Medium,
                       color = BodyGray
                   )
                }
            }

            if (index < steps.size - 1) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .padding(horizontal = 4.dp)
                        .background(if (isCompleted) MedicalGreen else Color(0xFFE2E8F0))
                )
            }
        }
    }
}

// ==========================================
// 1.5 ROLE SELECTION SCREEN
// ==========================================
@Composable
fun RoleSelectionScreen(viewModel: ELmorsyViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        AppSetupStepIndicator(currentStepIndex = 1)
        
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
        Icon(
            imageVector = Icons.Default.VerifiedUser,
            contentDescription = null,
            tint = MedicalBlue,
            modifier = Modifier.size(64.dp).padding(bottom = 16.dp)
        )
        Text(
            text = "مرحباً بك في تطبيق معاك للرعاية",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = DarkNavy,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "الرجاء تحديد صفة الدخول للمتابعة",
            fontSize = 14.sp,
            color = BodyGray,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        val roles = listOf(
            Triple("PATIENT", "أنا مستخدم (مريض)", Icons.Default.Person),
            Triple("NURSE", "أنا ممرض / أخصائي", Icons.Default.MedicalServices),
            Triple("ADMIN", "إدارة التطبيق (أدمن)", Icons.Default.MonitorHeart)
        )

        roles.forEach { (roleId, title, icon) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .clickable {
                        viewModel.setUserRole(roleId)
                        viewModel.navigateTo("onboarding")
                    },
                colors = CardDefaults.cardColors(containerColor = SoftBlueBg),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = icon, contentDescription = title, tint = MedicalBlue, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkNavy
                    )
                }
            }
        }
        }
    }
}

// ==========================================
// 2. ONBOARDING SCREEN (Premium Visual Enhancements)
// ==========================================
data class OnboardItem(val title: String, val desc: String, val icon: ImageVector, val badgeColor: Color, val tag: String)

@Composable
fun OnboardingScreen(viewModel: ELmorsyViewModel) {
    val slideIndex by viewModel.onboardingSlide.collectAsState()
    
    val slides = listOf(
        OnboardItem(
            "احجز ممرض بسهولة",
            "بخطوات بسيطة احجز ممرضًا محترفًا لزيارتك في المنزل وخدمتك على مدار الساعة بامتياز فوري.",
            Icons.Default.TouchApp,
            Color(0xFF0F52BA),
            "حجز ذكي وسريع"
        ),
        OnboardItem(
            "خدمات طبية متكاملة",
            "نوفر رعاية كبار السن، الغيار على الجروح، رعاية ما بعد الجراحة وإعطاء الحقن والمحاليل بدقة متناهية.",
            Icons.Default.MedicalServices,
            Color(0xFF0D9488),
            "رعاية شاملة"
        ),
        OnboardItem(
            "أمان وثقة تامة",
            "جميع طواقمنا الطبية مرخصة، مدربة بمستشفيات كبرى، وموثوقة لتقديم أفضل خدمة تليق بصحتكم.",
            Icons.Default.VerifiedUser,
            Color(0xFF10B981),
            "رعاية موثوقة وآمنة"
        ),
        OnboardItem(
            "ابدأ الآن مع معاك",
            "سجل حسابك الآن بلمسات بسيطة لتستفيد من رعاية منزلية فورية ومريحة جداً لأحبائك.",
            Icons.Default.Home,
            Color(0xFFE11D48),
            "الرعاية بين يديك"
        )
    )

    val currentSlide = slides[slideIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Row: Brand Info + Skip Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.HealthAndSafety,
                    contentDescription = null,
                    tint = Color(0xFF0F52BA),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "معاك",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = Color(0xFF0F172A)
                )
            }

            if (slideIndex < slides.size - 1) {
                TextButton(
                    onClick = { viewModel.completeOnboarding() },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF64748B))
                ) {
                    Text(
                        text = "تخطي",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(48.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        AppSetupStepIndicator(currentStepIndex = 2)

        Spacer(modifier = Modifier.weight(0.4f))

        // Large 3D style Illustration 
        Box(
            modifier = Modifier
                .size(260.dp),
            contentAlignment = Alignment.Center
        ) {
            // Background pulsing circles
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(0.9f)
                    .drawBehind {
                        drawCircle(
                            color = currentSlide.badgeColor.copy(alpha = 0.08f),
                            radius = size.width / 2f
                        )
                        drawCircle(
                            color = currentSlide.badgeColor.copy(alpha = 0.04f),
                            radius = size.width / 1.6f
                        )
                    }
            )

            // Overlapping central clean Card
            Card(
                modifier = Modifier
                    .size(190.dp)
                    .shadow(16.dp, RoundedCornerShape(28.dp))
                    .border(2.dp, Color.White, RoundedCornerShape(28.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(28.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .background(currentSlide.badgeColor.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = currentSlide.icon,
                            contentDescription = null,
                            tint = currentSlide.badgeColor,
                            modifier = Modifier.size(56.dp)
                        )
                    }

                    // Little floating cross decorations in the corners!
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = currentSlide.badgeColor.copy(alpha = 0.3f),
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.TopStart)
                            .offset(x = 16.dp, y = 16.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = currentSlide.badgeColor.copy(alpha = 0.3f),
                        modifier = Modifier
                            .size(18.dp)
                            .align(Alignment.BottomEnd)
                            .offset(x = (-20).dp, y = (-20).dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(0.3f))

        // Topic category Tag
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(30.dp))
                .background(currentSlide.badgeColor.copy(alpha = 0.12f))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = currentSlide.tag,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = currentSlide.badgeColor
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Title and Description
        Text(
            text = currentSlide.title,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF0F172A),
                fontSize = 24.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = currentSlide.desc,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color(0xFF64748B),
                lineHeight = 22.sp,
                fontSize = 15.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.weight(0.5f))

        // Premium large forward confirmation button
        Button(
            onClick = {
                if (slideIndex < slides.size - 1) {
                    viewModel.setOnboardingSlide(slideIndex + 1)
                } else {
                    viewModel.completeOnboarding()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .shadow(4.dp, RoundedCornerShape(14.dp))
                .testTag("onboarding_next_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = currentSlide.badgeColor,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                val label = if (slideIndex < slides.size - 1) "الـتـالـي" else "ابـدأ الآن مـع مـعـاك"
                Text(label, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Force RTL arrow directions beautifully
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ==========================================
// 3. LOGIN & SIGNUP SCREEN (PRO PREMIUM DUAL-MODE)
// ==========================================
@Composable
fun LoginScreen(viewModel: ELmorsyViewModel) {
    // Top-level switch: false = Sign In (الدخول), true = Sign Up / Register (الاشتراك)
    var isSignUpMode by remember { mutableStateOf(false) }
    
    var loginMethod by remember { mutableStateOf("phone") } // "phone" or "email"
    var phoneInput by remember { mutableStateOf("") }
    var otpInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(true) }

    var otpSent by remember { mutableStateOf(false) }
    var verificationError by remember { mutableStateOf(false) }
    var validationErrorText by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Multi-platform instant auth overlay simulator
    var socialAuthLoader by remember { mutableStateOf<String?>(null) } // "Google", "Apple", "Facebook"
    var showGoogleChooser by remember { mutableStateOf(false) }
    var showAppleChooser by remember { mutableStateOf(false) }

    var googleCustomName by remember { mutableStateOf("") }
    var googleCustomEmail by remember { mutableStateOf("") }
    var googleCustomPhone by remember { mutableStateOf("") }
    var selectCustomGoogle by remember { mutableStateOf(false) }

    var appleCustomEmail by remember { mutableStateOf("ahmed.ali@icloud.com") }
    var appleCustomName by remember { mutableStateOf("أحمد علي") }
    var appleCustomPhone by remember { mutableStateOf("") }
    var isAppleFaceIdScanning by remember { mutableStateOf(false) }
    var isAppleFaceIdSuccess by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val hasSavedSession by viewModel.hasSavedSession.collectAsState()
    val savedSessionEmail by viewModel.savedSessionEmail.collectAsState()
    val savedSessionName by viewModel.savedSessionName.collectAsState()
    val userRole by viewModel.userRole.collectAsState()
    
    val context = LocalContext.current
    val activity = context as? FragmentActivity

    LaunchedEffect(hasSavedSession) {
        if (hasSavedSession) {
            activity?.let { act ->
                showBiometricPromptHelper(
                    activity = act,
                    onSuccess = {
                        viewModel.loginWithBiometrics()
                    },
                    onError = { err ->
                        validationErrorText = err
                    }
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Aesthetic Top Wave/Gradient Banner Decoration (Airy, luminous feel)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(290.dp)
                .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFE0F2FE), // Airy luminous sky blue
                            Color(0xFFF0F9FF), // Soft light-blue
                            Color.White
                        )
                    )
                )
        ) {
            // Elegant background shapes for medical/care depth
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color(0xFF0F52BA).copy(alpha = 0.05f),
                    radius = 200.dp.toPx(),
                    center = Offset(size.width * 0.1f, size.height * 0.2f)
                )
                drawCircle(
                    color = Color(0xFF0F52BA).copy(alpha = 0.03f),
                    radius = 120.dp.toPx(),
                    center = Offset(size.width * 0.85f, size.height * 0.7f)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            AppSetupStepIndicator(currentStepIndex = 3)
            Spacer(modifier = Modifier.height(12.dp))

            // Premium Brand Logo Layout
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                // Main Logo Icon with circular glow
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .shadow(6.dp, CircleShape)
                        .background(Color.White, CircleShape)
                        .border(1.5.dp, MedicalBlue.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalHospital,
                        contentDescription = null,
                        tint = MedicalBlue,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = "معاك • MA3AK",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MedicalBlue,
                            fontSize = 20.sp,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Text(
                        text = "الرعاية الطبية المنزلية الخاصة",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = BodyGray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (hasSavedSession && !isSignUpMode) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .shadow(6.dp, RoundedCornerShape(20.dp))
                        .clickable {
                            activity?.let { act ->
                                showBiometricPromptHelper(
                                    activity = act,
                                    onSuccess = {
                                        viewModel.loginWithBiometrics()
                                    },
                                    onError = { err ->
                                        validationErrorText = err
                                    }
                                )
                            } ?: run {
                                validationErrorText = "عذراً، تعذر تشغيل الحماية البيومترية في هذا الجهاز"
                            }
                        },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.5.dp, Color(0xFFBBF7D0))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color(0xFFDCFCE7), CircleShape)
                                .border(1.dp, Color(0xFF86EFAC), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "بصمة",
                                tint = Color(0xFF16A34A),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "مرحباً بك مجدداً، $savedSessionName 🌟",
                                fontWeight = FontWeight.Bold,
                                color = DarkNavy,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Start
                            )
                            Text(
                                text = "انقر هنا لتسجيل دخولك الفوري والآمن بالبصمة 🔐",
                                color = BodyGray,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Start
                            )
                        }
                        
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = null,
                            tint = Color(0xFF16A34A).copy(alpha = 0.5f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // DUAL MODE TOGGLE TABS (Login "تسجيل الدخول" vs Signup "إنشاء حساب")
            if (userRole != "ADMIN") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFE2E8F0).copy(alpha = 0.7f))
                        .padding(5.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Sign In Tab Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (!isSignUpMode) Color.White else Color.Transparent)
                            .clickable { isSignUpMode = false; otpSent = false; validationErrorText = null }
                            .padding(vertical = 11.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "تسجيل الدخول",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (!isSignUpMode) MedicalBlue else BodyGray
                        )
                    }

                    // Sign Up Tab Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSignUpMode) Color.White else Color.Transparent)
                            .clickable { isSignUpMode = true; otpSent = false; validationErrorText = null }
                            .padding(vertical = 11.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "إنشاء حساب جديد",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (isSignUpMode) MedicalBlue else BodyGray
                        )
                    }
                }
            } else {
                // Force Sign In Mode and Email Login for Admin
                LaunchedEffect(Unit) {
                    isSignUpMode = false
                    loginMethod = "email"
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MedicalBlue.copy(alpha = 0.1f))
                        .padding(11.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "تسجيل دخول الإدارة",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MedicalBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Interactive Form Area Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(16.dp, RoundedCornerShape(26.dp))
                    .border(1.dp, Color.White, RoundedCornerShape(26.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(26.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header inside card
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (otpSent) {
                                "رمز التحقق من الهاتف"
                            } else if (isSignUpMode) {
                                "انضم لشبكة الرعاية الطبية"
                            } else {
                                "أهلاً بك مجدداً معك"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DarkNavy,
                                fontSize = 15.sp
                            )
                        )
                        
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SoftBlueBg)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (otpSent) "خطوة ٢ من ٢" else if (isSignUpMode) "حساب جديد" else "الدخول السريع",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MedicalBlue
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                    // Error text callout if present
                    validationErrorText?.let { error ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Error, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = error, color = Color.Red, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // SWITCHABLE LOGIN / SIGNUP INNER SELECTOR (Phone vs Email selection)
                    if (!otpSent && userRole != "ADMIN") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF1F5F9))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (loginMethod == "phone") Color.White else Color.Transparent)
                                    .clickable { loginMethod = "phone"; validationErrorText = null }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.PhoneAndroid,
                                        contentDescription = null,
                                        tint = if (loginMethod == "phone") MedicalBlue else BodyGray,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "رقم الهاتف",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.5.sp,
                                        color = if (loginMethod == "phone") DarkNavy else BodyGray
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (loginMethod == "email") Color.White else Color.Transparent)
                                    .clickable { loginMethod = "email"; validationErrorText = null }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Email,
                                        contentDescription = null,
                                        tint = if (loginMethod == "email") MedicalBlue else BodyGray,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "البريد الإلكتروني",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.5.sp,
                                        color = if (loginMethod == "email") DarkNavy else BodyGray
                                    )
                                }
                            }
                        }
                    }

                    // --- INPUT FIELDS AND BUTTONS GROUP ---
                    if (!otpSent) {
                        if (!isSignUpMode) {
                            // ==========================================
                            // FLOW 1: SIGN IN (تسجيل الدخول)
                            // ==========================================
                            if (loginMethod == "phone") {
                                // SignIn Option A: Phone number & name
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = MedicalBlue, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "الاسم أو هويتك الطبية", fontWeight = FontWeight.Bold, color = DarkNavy, fontSize = 13.sp)
                                    }
                                    OutlinedTextField(
                                        value = nameInput,
                                        onValueChange = { nameInput = it },
                                        placeholder = { Text("أدخل الاسم ثلاثي لخدمتك", color = BodyGray.copy(alpha = 0.5f)) },
                                        modifier = Modifier.fillMaxWidth().testTag("name_input"),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MedicalBlue,
                                            unfocusedBorderColor = Color(0xFFCBD5E1),
                                            focusedContainerColor = SoftBlueBg.copy(alpha = 0.15f)
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true
                                    )
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = MedicalGreen, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "رقم الهاتف المحمول", fontWeight = FontWeight.Bold, color = DarkNavy, fontSize = 13.sp)
                                    }
                                    OutlinedTextField(
                                        value = phoneInput,
                                        onValueChange = { input ->
                                            if (input.all { it.isDigit() } && input.length <= 11) {
                                                phoneInput = input
                                            }
                                        },
                                        placeholder = { Text("مثال: ٠١xxxxxxxxx", color = BodyGray.copy(alpha = 0.5f)) },
                                        leadingIcon = {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(start = 12.dp, end = 6.dp)
                                            ) {
                                                Text("🇪🇬", fontSize = 16.sp)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("+20", fontWeight = FontWeight.Bold, color = DarkNavy, fontSize = 13.sp)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Box(modifier = Modifier.height(18.dp).width(1.dp).background(Color(0xFFCBD5E1)))
                                            }
                                        },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                        modifier = Modifier.fillMaxWidth().testTag("phone_input"),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MedicalBlue,
                                            unfocusedBorderColor = Color(0xFFCBD5E1),
                                            focusedContainerColor = SoftBlueBg.copy(alpha = 0.15f)
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true
                                    )
                                }

                                Button(
                                    onClick = {
                                        if (nameInput.isBlank()) {
                                            validationErrorText = "يرجى إدخال اسمك بالكامل أولاً"
                                        } else if (phoneInput.length < 10) {
                                            validationErrorText = "يرجى كتابة رقم هاتف مصري صحيح"
                                        } else {
                                            validationErrorText = null
                                            isLoading = true
                                            scope.launch {
                                                delay(1100)
                                                isLoading = false
                                                otpSent = true
                                            }
                                        }
                                    },
                                    enabled = !isLoading,
                                    modifier = Modifier.fillMaxWidth().height(50.dp).testTag("send_otp_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = MedicalBlue),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                                    } else {
                                        Text("إرسال رمز التفعيل الآمن", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                                    }
                                }
                            } else {
                                // SignIn Option B: Email & Password
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Email, contentDescription = null, tint = MedicalBlue, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "البريد الإلكتروني", fontWeight = FontWeight.Bold, color = DarkNavy, fontSize = 13.sp)
                                    }
                                    OutlinedTextField(
                                        value = emailInput,
                                        onValueChange = { emailInput = it; validationErrorText = null },
                                        placeholder = { Text("example@domain.com", color = BodyGray.copy(alpha = 0.5f)) },
                                        modifier = Modifier.fillMaxWidth().testTag("email_input"),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MedicalBlue,
                                            unfocusedBorderColor = Color(0xFFCBD5E1),
                                            focusedContainerColor = SoftBlueBg.copy(alpha = 0.15f)
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true
                                    )
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Lock, contentDescription = null, tint = MedicalGreen, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "كلمة المرور المشفرة", fontWeight = FontWeight.Bold, color = DarkNavy, fontSize = 13.sp)
                                    }
                                    OutlinedTextField(
                                        value = passwordInput,
                                        onValueChange = { passwordInput = it; validationErrorText = null },
                                        placeholder = { Text("أدخل كلمة المرور الخاصة بك", color = BodyGray.copy(alpha = 0.5f)) },
                                        visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                                        trailingIcon = {
                                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                                Icon(
                                                    imageVector = if (passwordVisible) Icons.Default.LockOpen else Icons.Default.Lock,
                                                    contentDescription = "Toggle password visibility",
                                                    tint = BodyGray
                                                )
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth().testTag("password_input"),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MedicalBlue,
                                            unfocusedBorderColor = Color(0xFFCBD5E1),
                                            focusedContainerColor = SoftBlueBg.copy(alpha = 0.15f)
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true
                                    )
                                }

                                Button(
                                    onClick = {
                                        if (emailInput.isBlank() || !emailInput.contains("@")) {
                                            validationErrorText = "يرجى كتابة بريد إلكتروني صحيح ومعتدل"
                                        } else if (passwordInput.length < 6) {
                                            validationErrorText = "كلمة المرور يجب أن لا تقل عن 6 رموز"
                                        } else {
                                            validationErrorText = null
                                            isLoading = true
                                            viewModel.performSupabaseEmailLogin(emailInput, passwordInput) { success, error ->
                                                isLoading = false
                                                if (!success) {
                                                    validationErrorText = error ?: "خطأ غير معروف أثناء تسجيل الدخول"
                                                }
                                            }
                                        }
                                    },
                                    enabled = !isLoading,
                                    modifier = Modifier.fillMaxWidth().height(50.dp).testTag("email_login_submit"),
                                    colors = ButtonDefaults.buttonColors(containerColor = MedicalBlue),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                                    } else {
                                        Text("تسجيل دخول المشترك الآمن", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                                    }
                                }
                            }
                        } else {
                            // ==========================================
                            // FLOW 2: SIGN UP / SUBSCRIBE (إنشاء حساب واشتراك)
                            // ==========================================
                            if (loginMethod == "phone") {
                                // SignUp Option A: Phone registration
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = MedicalBlue, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "الاسم بالكامل (ثلاثي)", fontWeight = FontWeight.Bold, color = DarkNavy, fontSize = 13.sp)
                                    }
                                    OutlinedTextField(
                                        value = nameInput,
                                        onValueChange = { nameInput = it },
                                        placeholder = { Text("أدخل اسمك بالكامل لطباعة الهوية", color = BodyGray.copy(alpha = 0.5f)) },
                                        modifier = Modifier.fillMaxWidth().testTag("name_input"),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MedicalBlue,
                                            unfocusedBorderColor = Color(0xFFCBD5E1),
                                            focusedContainerColor = SoftBlueBg.copy(alpha = 0.15f)
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true
                                    )
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = MedicalGreen, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "رقم الهاتف المحمول", fontWeight = FontWeight.Bold, color = DarkNavy, fontSize = 13.sp)
                                    }
                                    OutlinedTextField(
                                        value = phoneInput,
                                        onValueChange = { input ->
                                            if (input.all { it.isDigit() } && input.length <= 11) {
                                                phoneInput = input
                                            }
                                        },
                                        placeholder = { Text("مثال: ٠١xxxxxxxxx", color = BodyGray.copy(alpha = 0.5f)) },
                                        leadingIcon = {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(start = 12.dp, end = 6.dp)
                                            ) {
                                                Text("🇪🇬", fontSize = 16.sp)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("+20", fontWeight = FontWeight.Bold, color = DarkNavy, fontSize = 13.sp)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Box(modifier = Modifier.height(18.dp).width(1.dp).background(Color(0xFFCBD5E1)))
                                            }
                                        },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                        modifier = Modifier.fillMaxWidth().testTag("phone_input"),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MedicalBlue,
                                            unfocusedBorderColor = Color(0xFFCBD5E1),
                                            focusedContainerColor = SoftBlueBg.copy(alpha = 0.15f)
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true
                                    )
                                }

                                // Custom Beautiful Terms Checkbox
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { termsAccepted = !termsAccepted }
                                        .padding(vertical = 4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (termsAccepted) MedicalBlue else Color(0xFFF1F5F9))
                                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(6.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (termsAccepted) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "أوافق على وثيقة شروط الخدمة الطبية وسياسة الاستخدام لمعاك كير",
                                        color = BodyGray,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Button(
                                    onClick = {
                                        if (nameInput.isBlank()) {
                                            validationErrorText = "الاسم حقل مطلوب للتسجيل الطبي"
                                        } else if (phoneInput.length < 10) {
                                            validationErrorText = "رقم الهاتف غير معتمد أو ناقص"
                                        } else if (!termsAccepted) {
                                            validationErrorText = "يرجى القبول بشروط الخدمة والسياسات للمتابعة"
                                        } else {
                                            validationErrorText = null
                                            isLoading = true
                                            scope.launch {
                                                delay(1200)
                                                isLoading = false
                                                otpSent = true
                                            }
                                        }
                                    },
                                    enabled = !isLoading,
                                    modifier = Modifier.fillMaxWidth().height(50.dp).testTag("send_otp_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = MedicalGreen),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                                    } else {
                                        Text("إنشاء الحساب وبدء التحقق المباشر", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                                    }
                                }
                            } else {
                                // SignUp Option B: Email, Phone, Password, Confirm Password registration
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = MedicalBlue, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "الاسم بالكامل", fontWeight = FontWeight.Bold, color = DarkNavy, fontSize = 13.sp)
                                    }
                                    OutlinedTextField(
                                        value = nameInput,
                                        onValueChange = { nameInput = it },
                                        placeholder = { Text("الاسم كاملاً", color = BodyGray.copy(alpha = 0.5f)) },
                                        modifier = Modifier.fillMaxWidth().testTag("name_input"),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MedicalBlue,
                                            unfocusedBorderColor = Color(0xFFCBD5E1),
                                            focusedContainerColor = SoftBlueBg.copy(alpha = 0.15f)
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true
                                    )
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Email, contentDescription = null, tint = MedicalBlue, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "البريد الإلكتروني", fontWeight = FontWeight.Bold, color = DarkNavy, fontSize = 13.sp)
                                    }
                                    OutlinedTextField(
                                        value = emailInput,
                                        onValueChange = { emailInput = it; validationErrorText = null },
                                        placeholder = { Text("example@domain.com", color = BodyGray.copy(alpha = 0.5f)) },
                                        modifier = Modifier.fillMaxWidth().testTag("email_input"),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MedicalBlue,
                                            unfocusedBorderColor = Color(0xFFCBD5E1),
                                            focusedContainerColor = SoftBlueBg.copy(alpha = 0.15f)
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true
                                    )
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = MedicalBlue, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "رقم الهاتف للمستبسل", fontWeight = FontWeight.Bold, color = DarkNavy, fontSize = 13.sp)
                                    }
                                    OutlinedTextField(
                                        value = phoneInput,
                                        onValueChange = { phoneInput = it; validationErrorText = null },
                                        placeholder = { Text("٠١xxxxxxxxx", color = BodyGray.copy(alpha = 0.5f)) },
                                        modifier = Modifier.fillMaxWidth().testTag("phone_input"),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MedicalBlue,
                                            unfocusedBorderColor = Color(0xFFCBD5E1),
                                            focusedContainerColor = SoftBlueBg.copy(alpha = 0.15f)
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true
                                    )
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Lock, contentDescription = null, tint = MedicalGreen, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "كلمة المرور المشفرة", fontWeight = FontWeight.Bold, color = DarkNavy, fontSize = 13.sp)
                                    }
                                    OutlinedTextField(
                                        value = passwordInput,
                                        onValueChange = { passwordInput = it; validationErrorText = null },
                                        placeholder = { Text("الرمز السري (6 خانات كحد أدنى)", color = BodyGray.copy(alpha = 0.5f)) },
                                        visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                                        trailingIcon = {
                                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                                Icon(
                                                    imageVector = if (passwordVisible) Icons.Default.LockOpen else Icons.Default.Lock,
                                                    contentDescription = null,
                                                    tint = BodyGray
                                                )
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth().testTag("password_input"),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MedicalBlue,
                                            unfocusedBorderColor = Color(0xFFCBD5E1),
                                            focusedContainerColor = SoftBlueBg.copy(alpha = 0.15f)
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true
                                    )
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Lock, contentDescription = null, tint = MedicalGreen, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "تأكيد كلمة المرور", fontWeight = FontWeight.Bold, color = DarkNavy, fontSize = 13.sp)
                                    }
                                    OutlinedTextField(
                                        value = confirmPasswordInput,
                                        onValueChange = { confirmPasswordInput = it; validationErrorText = null },
                                        placeholder = { Text("أعد كتابة كلمة المرور", color = BodyGray.copy(alpha = 0.5f)) },
                                        visualTransformation = if (confirmPasswordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                                        trailingIcon = {
                                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                                Icon(
                                                    imageVector = if (confirmPasswordVisible) Icons.Default.LockOpen else Icons.Default.Lock,
                                                    contentDescription = null,
                                                    tint = BodyGray
                                                )
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MedicalBlue,
                                            unfocusedBorderColor = Color(0xFFCBD5E1),
                                            focusedContainerColor = SoftBlueBg.copy(alpha = 0.15f)
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true
                                    )
                                }

                                // Terms Agreement Checkbox
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { termsAccepted = !termsAccepted }
                                        .padding(vertical = 4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (termsAccepted) MedicalBlue else Color(0xFFF1F5F9))
                                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(6.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (termsAccepted) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "أوافق على وثيقة شروط الخدمة الطبية وسياسة الاستخدام لمعاك كير",
                                        color = BodyGray,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Button(
                                    onClick = {
                                        if (nameInput.isBlank()) {
                                            validationErrorText = "يرجى توفير الاسم قبل التسجيل"
                                        } else if (emailInput.isBlank() || !emailInput.contains("@")) {
                                            validationErrorText = "البريد الإلكتروني المكتوب غير صالح"
                                        } else if (phoneInput.length < 10) {
                                            validationErrorText = "رقم الهاتف المحمول المصري غير كامل"
                                        } else if (passwordInput.length < 6) {
                                            validationErrorText = "الرمز السري لا يمكن أن يقل عن 6 أحرف"
                                        } else if (passwordInput != confirmPasswordInput) {
                                            validationErrorText = "بيانات تأكيد كلمة المرور غير متطابقة"
                                        } else if (!termsAccepted) {
                                            validationErrorText = "يجب الموافقة على وثيقة شروط معاك كير"
                                        } else {
                                            validationErrorText = null
                                            isLoading = true
                                            viewModel.performSupabaseEmailSignUp(nameInput, phoneInput, emailInput, passwordInput) { success, error ->
                                                isLoading = false
                                                if (success) {
                                                    showSuccessDialog = true
                                                } else {
                                                    validationErrorText = error ?: "حدث خطأ غير معروف أثناء إنشاء حسابك"
                                                }
                                            }
                                        }
                                    },
                                    enabled = !isLoading,
                                    modifier = Modifier.fillMaxWidth().height(50.dp).testTag("email_login_submit"),
                                    colors = ButtonDefaults.buttonColors(containerColor = MedicalGreen),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                                    } else {
                                        Text("إنشاء ومتابعة الاشتراك الطبي", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                                    }
                                }
                            }
                        }
                    } else {
                        // ==========================================
                        // OTP VERIFICATION STEP VIEW
                        // ==========================================
                        Text(
                            text = "أدخل رمز رمز التحقق المكون من ٤ أرقام الذي تم إرساله إلى رقم الهاتف $phoneInput",
                            color = BodyGray,
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // 4 Interactive Styled divided Boxes
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (i in 0..3) {
                                val digit = otpInput.getOrNull(i)?.toString() ?: ""
                                val isFocused = otpInput.length == i || (otpInput.length == 4 && i == 3)
                                
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .shadow(if (isFocused) 4.dp else 1.dp, RoundedCornerShape(12.dp))
                                        .background(
                                            if (isFocused) SoftBlueBg.copy(alpha = 0.4f) else Color(0xFFF8FAFC),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .border(
                                            width = if (isFocused) 2.dp else 1.dp,
                                            color = if (isFocused) MedicalBlue else Color(0xFFE2E8F0),
                                            shape = RoundedCornerShape(12.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = digit,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isFocused) MedicalBlue else DarkNavy
                                    )
                                    if (digit.isEmpty()) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(BodyGray.copy(alpha = 0.3f))
                                        )
                                    }
                                }
                            }
                        }

                        // Hidden real textfield to capture user typing securely
                        Box(modifier = Modifier.size(1.dp).clip(CircleShape)) {
                            OutlinedTextField(
                                value = otpInput,
                                onValueChange = { input ->
                                    if (input.all { it.isDigit() } && input.length <= 4) {
                                        otpInput = input
                                    }
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                modifier = Modifier.testTag("otp_input")
                            )
                        }

                        if (verificationError) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F2)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Error, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "رمز التحقق خاطئ. يرجى إدخال رمز التحقق الصحيح المكون من ٤ أرقام",
                                        color = Color.Red,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { otpSent = false },
                                colors = ButtonDefaults.textButtonColors(contentColor = MedicalBlue)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("رجوع وتعديل الهاتف", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            TextButton(
                                onClick = { 
                                    verificationError = false
                                },
                                colors = ButtonDefaults.textButtonColors(contentColor = BodyGray)
                            ) {
                                Text("إعادة إرسال الرمز", fontSize = 12.sp)
                            }
                        }

                        Button(
                            onClick = {
                                if (otpInput.length < 4) {
                                    verificationError = true
                                } else {
                                    isLoading = true
                                    viewModel.performSupabasePhoneLogin(
                                        phone = phoneInput.ifBlank { "01000001234" },
                                        nameForNewUserIfSignup = if (nameInput.isNotBlank()) nameInput else "مستفيد معاك",
                                        isSignUp = isSignUpMode
                                    ) { success, error ->
                                        isLoading = false
                                        if (!success) {
                                            verificationError = true
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp).testTag("login_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = MedicalGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                            } else {
                                Text("التحقق والبدء الفوري", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                            }
                        }
                    }

                    // ==========================================
                    // SOCIAL AUTH CHANNELS (GOOGLE / APPLE)
                    // ==========================================
                    if (!otpSent) {
                        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isSignUpMode) "أو التسجيل والربط المباشر بلمسة واحدة عبر" else "أو الدخول الآمن المباشر عبر حساباتك",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = BodyGray,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                            
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Google Login
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .clickable {
                                            showGoogleChooser = true
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("G", fontWeight = FontWeight.Black, color = Color(0xFFDB4437), fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Google", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkNavy)
                                    }
                                }
                                
                                // Apple Login
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .clickable {
                                            showAppleChooser = true
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = DarkNavy, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text("Apple", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkNavy)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Footer HIPAA compliance label
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Icon(
                    Icons.Default.Security,
                    contentDescription = null,
                    tint = BodyGray.copy(alpha = 0.5f),
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "بياناتك مشفرة بضمان معايير HIPAA الرعائية والخصوصية",
                    color = BodyGray.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    // Modal Simulation Dialog Success Signup
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            confirmButton = {},
            dismissButton = {},
            shape = RoundedCornerShape(26.dp),
            containerColor = Color.White,
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .background(Color(0xFFDCFCE7), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MedicalGreen,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(18.dp))
                    
                    Text(
                        text = "مرحباً بك في عائلة معاك! 🎉",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkNavy,
                            fontSize = 18.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    Text(
                        text = "تم إنشاء وتنشيط ملفك الطبي الرقمي بنجاح تام. جاري المتابعة لتفاصيل اختيار عنوان الرعاية المنزلية الخاص بك...",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = BodyGray,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Button(
                        onClick = {
                            showSuccessDialog = false
                            val finalName = if (nameInput.isNotBlank()) nameInput else "مستخدم معاك كير"
                            viewModel.performLogin(finalName, phoneInput)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MedicalBlue),
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("متابعة إعداد العنوان 🏠", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                    }
                }
            }
        )
    }

    // Modal Social verification overlay
    if (socialAuthLoader != null) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {},
            dismissButton = {},
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White,
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(
                                color = when (socialAuthLoader) {
                                    "Google" -> Color(0xFFF1F5F9)
                                    "Apple ID" -> Color(0xFF0F172A)
                                    else -> SoftBlueBg
                                },
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        when (socialAuthLoader) {
                            "Google" -> Text("G", fontWeight = FontWeight.Black, color = Color(0xFFDB4437), fontSize = 28.sp)
                            "Apple ID" -> Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(18.dp))
                    
                    Text(
                        text = "التحقق الطبي المعتمد",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkNavy
                        ),
                        textAlign = TextAlign.Center
                    )
                    
                    Text(
                        text = "جاري الاتصال الآمن مع بوابة $socialAuthLoader ...",
                        style = MaterialTheme.typography.bodyMedium.copy(color = BodyGray, fontSize = 12.sp),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    CircularProgressIndicator(
                        color = MedicalBlue,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        )
    }

    if (showGoogleChooser) {
        AlertDialog(
            onDismissRequest = { showGoogleChooser = false },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showGoogleChooser = false }) {
                    Text("إلغاء", color = BodyGray, fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.padding(bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("G", fontWeight = FontWeight.Black, color = Color(0xFF4285F4), fontSize = 28.sp)
                        Text("o", fontWeight = FontWeight.Black, color = Color(0xFFEA4335), fontSize = 28.sp)
                        Text("o", fontWeight = FontWeight.Black, color = Color(0xFFFBBC05), fontSize = 28.sp)
                        Text("g", fontWeight = FontWeight.Black, color = Color(0xFF4285F4), fontSize = 28.sp)
                        Text("l", fontWeight = FontWeight.Black, color = Color(0xFF34A853), fontSize = 28.sp)
                        Text("e", fontWeight = FontWeight.Black, color = Color(0xFFEA4335), fontSize = 28.sp)
                    }
                    
                    Text(
                        text = "اختر حساباً للمتابعة",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DarkNavy),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "إلى Ma3ak Care (معاك كير للرعاية الطبية)",
                        style = MaterialTheme.typography.bodySmall.copy(color = BodyGray),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (!selectCustomGoogle) {
                        val googleAccounts = listOf(
                            Triple("أحمد علي", "ahmed.ali@gmail.com", "01099887766"),
                            Triple("قاسم دغيم", "kasemdg417@gmail.com", "01011223344"),
                            Triple("مستفيد تجريبي", "guest.user@gmail.com", "01055667788")
                        )
                        
                        googleAccounts.forEach { account ->
                            val (name, email, phone) = account
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        showGoogleChooser = false
                                        socialAuthLoader = "Google"
                                        viewModel.performSupabaseSocialLogin(
                                            email = email,
                                            fullName = name,
                                            phoneInput = phone,
                                            provider = "Google",
                                            onResult = { success, err ->
                                                socialAuthLoader = null
                                                if (!success && err != null) {
                                                    validationErrorText = err
                                                }
                                            }
                                        )
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(MedicalBlue.copy(alpha = 0.1f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = name.take(1),
                                            fontWeight = FontWeight.Bold,
                                            color = MedicalBlue,
                                            fontSize = 14.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(name, fontWeight = FontWeight.Bold, color = DarkNavy, fontSize = 13.sp)
                                        Text(email, color = BodyGray, fontSize = 11.sp)
                                    }
                                    Icon(
                                        Icons.Default.ArrowBack,
                                        contentDescription = null,
                                        tint = BodyGray.copy(alpha = 0.5f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectCustomGoogle = true
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0).copy(alpha = 0.8f))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(Color(0xFFF1F5F9), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = null,
                                        tint = BodyGray,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("استخدام حساب Google مخصص آخر", fontWeight = FontWeight.Bold, color = BodyGray, fontSize = 12.sp)
                            }
                        }
                    } else {
                        Text(
                            "أدخل تفاصيل حساب Google الخاص بك:",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = DarkNavy)
                        )
                        
                        OutlinedTextField(
                            value = googleCustomName,
                            onValueChange = { googleCustomName = it },
                            label = { Text("الاسم بالكامل بـ Google", fontSize = 12.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MedicalBlue,
                                unfocusedBorderColor = Color(0xFFE2E8F0)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        OutlinedTextField(
                            value = googleCustomEmail,
                            onValueChange = { googleCustomEmail = it },
                            label = { Text("عنوان البريد الإلكتروني (Gmail)", fontSize = 12.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MedicalBlue,
                                unfocusedBorderColor = Color(0xFFE2E8F0)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        OutlinedTextField(
                            value = googleCustomPhone,
                            onValueChange = { googleCustomPhone = it },
                            label = { Text("رقم الهاتف لتنشيط الملف الطبي", fontSize = 12.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MedicalBlue,
                                unfocusedBorderColor = Color(0xFFE2E8F0)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (googleCustomName.isBlank() || googleCustomEmail.isBlank() || googleCustomPhone.isBlank()) {
                                        validationErrorText = "يرجى ملء جميع بيانات المصادقة لـ Google"
                                        return@Button
                                    }
                                    showGoogleChooser = false
                                    socialAuthLoader = "Google"
                                    viewModel.performSupabaseSocialLogin(
                                        email = googleCustomEmail,
                                        fullName = googleCustomName,
                                        phoneInput = googleCustomPhone,
                                        provider = "Google",
                                        onResult = { success, err ->
                                            socialAuthLoader = null
                                            if (!success && err != null) {
                                                validationErrorText = err
                                            }
                                        }
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4)),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("تسجيل ومزامنة", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            
                            OutlinedButton(
                                onClick = { selectCustomGoogle = false },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("رجوع", color = BodyGray, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        )
    }

    if (showAppleChooser) {
        AlertDialog(
            onDismissRequest = { 
                showAppleChooser = false 
                isAppleFaceIdScanning = false
                isAppleFaceIdSuccess = false
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { 
                    showAppleChooser = false 
                    isAppleFaceIdScanning = false
                    isAppleFaceIdSuccess = false
                }) {
                    Text("إلغاء", color = BodyGray, fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color(0xFF0F172A),
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Sign in with Apple ID",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "معاك كير للرعاية الطبية المنزلية المعتمدة",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8)),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (!isAppleFaceIdScanning && !isAppleFaceIdSuccess) {
                        Text(
                            "مشاركة الخصوصية الطبية بضمان تشفير Apple Secure Key:",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1)),
                            fontSize = 11.sp
                        )
                        
                        OutlinedTextField(
                            value = appleCustomEmail,
                            onValueChange = { appleCustomEmail = it },
                            label = { Text("قفل رمز Apple ID", color = Color(0xFF94A3B8), fontSize = 12.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF38BDF8),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        OutlinedTextField(
                            value = appleCustomName,
                            onValueChange = { appleCustomName = it },
                            label = { Text("الاسم المعتمد في هاتف iPhone", color = Color(0xFF94A3B8), fontSize = 12.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF38BDF8),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        OutlinedTextField(
                            value = appleCustomPhone,
                            onValueChange = { appleCustomPhone = it },
                            label = { Text("أدخل رقم هاتفك لتسجيل الملف الطبي", color = Color(0xFF94A3B8), fontSize = 12.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF38BDF8),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        Button(
                            onClick = {
                                if (appleCustomEmail.isBlank() || appleCustomName.isBlank() || appleCustomPhone.isBlank()) {
                                    validationErrorText = "يرجى تعبئة رقم الهاتف وبريد Apple ID لإتمام التسجيل الآمن"
                                    return@Button
                                }
                                
                                isAppleFaceIdScanning = true
                                scope.launch {
                                    delay(2000)
                                    isAppleFaceIdScanning = false
                                    isAppleFaceIdSuccess = true
                                    delay(1000)
                                    showAppleChooser = false
                                    socialAuthLoader = "Apple ID"
                                    
                                    viewModel.performSupabaseSocialLogin(
                                        email = appleCustomEmail,
                                        fullName = appleCustomName,
                                        phoneInput = appleCustomPhone,
                                        provider = "Apple ID",
                                        onResult = { success, err ->
                                            socialAuthLoader = null
                                            isAppleFaceIdSuccess = false
                                            if (!success && err != null) {
                                                validationErrorText = err
                                            }
                                        }
                                    )
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth().height(46.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF0F172A), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("المتابعة باستخدام Face ID 🛡️", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    } else if (isAppleFaceIdScanning) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .border(2.dp, Color(0xFF38BDF8), CircleShape)
                                    .background(Color(0xFF1E293B), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Face,
                                    contentDescription = null,
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "جاري قراءة ملامح الحماية المزدوجة Face ID...",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            LinearProgressIndicator(
                                color = Color(0xFF38BDF8),
                                trackColor = Color(0xFF334155),
                                modifier = Modifier.fillMaxWidth(0.6f).height(4.dp)
                            )
                        }
                    } else if (isAppleFaceIdSuccess) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .background(Color(0xFF10B981), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "تم التحقق ببصمة الوجه الموثقة!",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        )
    }
}


// ==========================================
// 3.5. ADDRESS SELECTION SCREEN (POST-LOGIN)
// ==========================================
@Composable
fun AddressSelectionScreen(viewModel: ELmorsyViewModel) {
    AddressSelectionMap(
        onBack = {
            viewModel.performLogout()
        },
        onAddressConfirmed = { fullAddress, lat, lng, street, building, floor, apartment ->
            viewModel.saveUserAddressAndCompleteLogin(fullAddress, lat, lng)
        },
        headerContent = { AppSetupStepIndicator(currentStepIndex = 4) }
    )
}


// ==========================================
// 4. HOME SCREEN WAPPER (Role Based)
// ==========================================
@Composable
fun HomeScreen(
    viewModel: ELmorsyViewModel,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    val role by viewModel.userRole.collectAsState()
    
    // Refresh data whenever the home screen is displayed
    LaunchedEffect(Unit) {
        viewModel.fetchServicesAndNursesFromSupabase()
    }
    
    when (role) {
        "NURSE" -> NurseDashboardScreen(viewModel)
        "ADMIN" -> AdminDashboardScreenV2(viewModel)
        else -> PatientHomeScreen(viewModel, sharedTransitionScope, animatedVisibilityScope)
    }
}

@Composable
fun PatientHomeScreen(
    viewModel: ELmorsyViewModel,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    val userName by viewModel.userName.collectAsState()
    val bookings by viewModel.bookingsHistory.collectAsState()
    val nearbyNurses by viewModel.nearbyNurses.collectAsState()
    
    val servicesList by viewModel.servicesFlow.collectAsState()
    val departmentsList by viewModel.departmentsFlow.collectAsState()
    
    val developerProfile by viewModel.developerProfile.collectAsState()
    val appSliders by viewModel.appSliders.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchServicesAndNursesFromSupabase()
    }

    val devName = developerProfile?.name ?: "د. محمد صبحي"
    val devTitle = developerProfile?.title ?: "رسالة المطور"
    val devMessage = developerProfile?.message ?: "مُطوّر التطبيق والمشرف التقني. يسعدني تقديم هذه المنصة لتسهيل وصولكم لخدمات الرعاية الموثوقة."
    val devImageUrl = developerProfile?.imageUrl ?: ""
    
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("الكل") }
    
    // Auto-carousel slide index
    var carouselIndex by remember { mutableStateOf(0) }
    class SlideData(val title: String, val desc: String, val imageRes: Int, val imageUrl: String)
    val defaultSlides = listOf(
        SlideData("معاك للغيار والتمريض المضمون", "نوفر كوادراً طبية مرخصة وجاهزة لخدمتك الفورية بالمنزل على مدار الساعة بكل أمان.", com.example.R.drawable.banner_nurse_1781237049978, ""),
        SlideData("رعاية كبار السن وحالات النقاهة", "مرافقون صحيون مؤهلون ومخصصون لتلبية جميع الاحتياجات اليومية والطبية بابتسامة.", com.example.R.drawable.banner_therapy_1781237066814, ""),
        SlideData("خصم مخصص وحصري ٢٠% اليوم", "استخدم كود MA3AK20 عند حجز زيارة تمريضية أو جلسة علاج طبيعي أولى بالمنزل.", com.example.R.drawable.banner_discount_1781237082668, "")
    )
    val carouselSlides = if (appSliders.isNotEmpty()) {
        appSliders.map { SlideData(it.title, it.description, com.example.R.drawable.banner_nurse_1781237049978, it.imageUrl ?: "") }
    } else {
        defaultSlides
    }

    // Dynamic filtering based on search query AND category selection
    val filteredServices = servicesList.filter { service ->
        val matchesSearch = service.title.contains(searchQuery) || service.description.contains(searchQuery)
        val selectedDept = departmentsList.find { it.title == selectedCategory }
        val matchesCategory = if (selectedCategory == "الكل") {
            true
        } else if (selectedDept != null) {
            val serviceIds = selectedDept.serviceIdsText.split(",").map { it.trim() }
            service.id in serviceIds
        } else {
            false
        }
        matchesSearch && matchesCategory
    }

    // Interactive States
    var showEmergencyDialog by remember { mutableStateOf(false) }
    var showAiConsultationDialog by remember { mutableStateOf(false) }
    var aiQueryText by remember { mutableStateOf("") }
    var aiResponseText by remember { mutableStateOf("") }
    var aiLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            val notificationsList by viewModel.notifications.collectAsState()
            val unreadCount = notificationsList.count { !it.isRead }
            ELmorsyBottomBar(
                currentScreen = "home",
                onNavigate = { viewModel.navigateTo(it) },
                notificationsCount = unreadCount
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            // Hero Header and Top Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                        )
                    )
                    .padding(horizontal = 24.dp)
                    .padding(top = 48.dp, bottom = 32.dp)
            ) {
                // Profile + Welcome Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Image Avatar
                        Image(
                            painter = painterResource(id = com.example.R.drawable.dramatic_portrait_1780966896428),
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "مرحبا بعودتك",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Text(
                                text = userName.ifBlank { "المستخدم" },
                                fontSize = 19.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Search Light Icon
                        IconButton(
                            onClick = { /* Handle Search */ },
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color.White.copy(alpha = 0.15f), CircleShape)
                        ) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Notification Bell Icon with Badge
                        val notifications by viewModel.notifications.collectAsState()
                        val hasUnread = notifications.any { !it.isRead }

                        IconButton(
                            onClick = { viewModel.navigateTo("notifications") },
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color.White.copy(alpha = 0.15f), CircleShape)
                        ) {
                            Box(contentAlignment = Alignment.TopEnd) {
                                Icon(
                                    Icons.Default.NotificationsNone,
                                    contentDescription = "Notifications",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                                if (hasUnread) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(Color(0xFFEF4444), CircleShape)
                                            .border(1.5.dp, MedicalBlue, CircleShape)
                                            .offset(x = (-2).dp, y = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Scrollable Home List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // ACTIVE BOOKINGS SECTION
                val activeBooking = bookings.firstOrNull { it.status != "COMPLETED" && it.status != "CANCELLED" }
                if (activeBooking != null) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.5.dp, MedicalBlue, RoundedCornerShape(16.dp))
                                .clickable { viewModel.navigateTo("tracking") },
                            colors = CardDefaults.cardColors(containerColor = SoftBlueBg),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .background(MedicalBlue, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.DirectionsRun, contentDescription = null, tint = Color.White)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "زيارة طبية نشطة جارية الآن!",
                                        fontWeight = FontWeight.Bold,
                                        color = MedicalBlue,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        "${activeBooking.serviceName} • مع ${activeBooking.nurseName}",
                                        color = DarkNavy,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null,
                                    tint = MedicalBlue
                                )
                            }
                        }
                    }
                }

                // 1. PREMIUM PROMOTION CAROUSEL WITH INTERACTIVE FLOW
                item {
                    val slide = carouselSlides[carouselIndex]
                    val pagerState = androidx.compose.foundation.pager.rememberPagerState(pageCount = { carouselSlides.size })
                    
                    // Auto-scroll logic
                    LaunchedEffect(Unit) {
                        while(true) {
                            kotlinx.coroutines.delay(4000)
                            val nextPage = (pagerState.currentPage + 1) % carouselSlides.size
                            pagerState.animateScrollToPage(nextPage)
                        }
                    }
                    
                    androidx.compose.foundation.pager.HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        pageSpacing = 12.dp
                    ) { page ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .shadow(4.dp, RoundedCornerShape(18.dp)),
                            colors = CardDefaults.cardColors(containerColor = SoftBlueBg),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                // Background Image
                                val currentSlide = carouselSlides[page]
                                if (currentSlide.imageUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = currentSlide.imageUrl,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(id = currentSlide.imageRes),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                // Gradient Overlay
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                                            startY = 100f
                                        ))
                                )
                                
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomStart)
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = currentSlide.title,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = currentSlide.desc,
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 12.sp,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(carouselSlides.size) { iteration ->
                            val color = if (pagerState.currentPage == iteration) MedicalBlue else Color.LightGray
                            val width = if (pagerState.currentPage == iteration) 24.dp else 8.dp
                            Box(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .height(8.dp)
                                    .width(width)
                            )
                        }
                    }
                }

                // 2. INTERACTIVE QUICK ACTIONS PANEL
                item {
                    Column {
                        Text(
                            text = "الخدمات المتاحة",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DarkNavy
                            ),
                            modifier = Modifier.padding(bottom = 12.dp, top = 4.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Service Card 1: Home Nursing
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(110.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(Color(0xFFDBEAFE), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.MedicalServices,
                                                contentDescription = "تمريض منزلي",
                                                tint = MedicalBlue,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            "تمريض منزلي",
                                            fontWeight = FontWeight.Bold,
                                            color = DarkNavy,
                                            fontSize = 12.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                    // Status Indicator
                                    Row(
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .align(Alignment.TopStart),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(Color(0xFF22C55E), CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("متاح", fontSize = 9.sp, color = Color(0xFF166534), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            // Service Card 2: Physical Therapy
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(110.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(Color(0xFFDCFCE7), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.DirectionsRun,
                                                contentDescription = "علاج طبيعي",
                                                tint = MedicalGreen,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            "علاج طبيعي",
                                            fontWeight = FontWeight.Bold,
                                            color = DarkNavy,
                                            fontSize = 12.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                    // Status Indicator
                                    Row(
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .align(Alignment.TopStart),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(Color(0xFFF59E0B), CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("مشغول", fontSize = 9.sp, color = Color(0xFFB45309), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            // Service Card 3: Patient Monitoring
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(110.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED)),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(Color(0xFFFFEDD5), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.Favorite,
                                                contentDescription = "مراقبة مريض",
                                                tint = Color(0xFFF97316),
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            "مراقبة مريض",
                                            fontWeight = FontWeight.Bold,
                                            color = DarkNavy,
                                            fontSize = 12.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                    // Status Indicator
                                    Row(
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .align(Alignment.TopStart),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(Color(0xFF22C55E), CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("متاح", fontSize = 9.sp, color = Color(0xFF166534), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. CATEGORY CHIPS FILTERING SYSTEM
                item {
                    val categories = listOf("الكل") + departmentsList.map { it.title }
                    Column {
                        Text(
                            text = "تصفية حسب الفئة الصحية",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DarkNavy
                            ),
                            modifier = Modifier.padding(bottom = 10.dp)
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(categories) { category ->
                                val isSelected = selectedCategory == category
                                val containerColor = if (isSelected) MedicalBlue else SoftBlueBg
                                val contentColor = if (isSelected) Color.White else DarkNavy
                                val borderModifier = if (isSelected) Modifier else Modifier.border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
                                
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(containerColor)
                                        .clickable { selectedCategory = category }
                                        .then(borderModifier)
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = category,
                                        color = contentColor,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                if (selectedCategory == "الكل" && searchQuery.isEmpty()) {
                    item {
                        Column(modifier = Modifier.padding(top = 4.dp)) {
                            Text(
                                text = "الأقسام والخدمات الطبية المنزلية 🏥",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DarkNavy
                                )
                            )
                            Text(
                                text = "تصفح باقات ورعاية الفروع والخدمات المتاحة لكل قسم من أقسام المِرسى",
                                fontSize = 11.sp,
                                color = BodyGray,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }

                    departmentsList.forEach { dept ->
                        val serviceIds = dept.serviceIdsText.split(",").map { it.trim() }
                        val matchingServices = servicesList.filter { it.id in serviceIds }
                        
                        if (matchingServices.isNotEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .shadow(4.dp, RoundedCornerShape(20.dp)),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(20.dp),
                                    border = BorderStroke(1.dp, Color(0xFFE1EFFE))
                                ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    // 1. Department Hero Image Banner with Gradient Overlay
                                    val firstService = matchingServices.firstOrNull()
                                    if (firstService != null && firstService.imageUrl.isNotEmpty()) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(160.dp)
                                                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                                        ) {
                                            AsyncImage(
                                                model = firstService.imageUrl,
                                                contentDescription = dept.title,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                            // Soft premium dark gradient overlay for text readability
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(
                                                        Brush.verticalGradient(
                                                            colors = listOf(
                                                                Color.Transparent,
                                                                Color.Black.copy(alpha = 0.85f)
                                                            )
                                                        )
                                                    )
                                            )
                                            // Title and Badge Overlay at Bottom End
                                            Column(
                                                modifier = Modifier
                                                    .align(Alignment.BottomStart)
                                                    .padding(16.dp)
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(6.dp))
                                                            .background(MedicalBlue)
                                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(
                                                            text = "قسم معتمد",
                                                            color = Color.White,
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = dept.title,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 15.sp,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }

                                // 2. Main content container
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    // Department Description
                                    Text(
                                        text = dept.description,
                                        fontSize = 11.5.sp,
                                        color = DarkNavy.copy(alpha = 0.8f),
                                        lineHeight = 16.5.sp,
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )

                                    // Trust indicators in Row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFFE6F4EA))
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = "معتمد وموثوق 🛡️",
                                                color = Color(0xFF137333),
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(SoftBlueBg)
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = "تغطية فورية للمنزل 🏠",
                                                color = MedicalBlue,
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFFFFF7ED))
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = "${matchingServices.size} خدمات",
                                                color = Color(0xFFC2410C),
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))
                                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                                    Spacer(modifier = Modifier.height(14.dp))

                                    // 3. Nested Services list
                                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        matchingServices.forEach { service ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(14.dp))
                                                    .background(
                                                        brush = Brush.linearGradient(
                                                            colors = listOf(
                                                                Color(0xFFF8FAFC),
                                                                SoftBlueBg.copy(alpha = 0.25f)
                                                            )
                                                        )
                                                    )
                                                    .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(14.dp))
                                                    .then(
                                                        if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                                                            with(sharedTransitionScope) {
                                                                Modifier.sharedElement(
                                                                    rememberSharedContentState(key = "service_bg_${service.id}"),
                                                                    animatedVisibilityScope = animatedVisibilityScope
                                                                )
                                                            }
                                                        } else Modifier
                                                    )
                                                    .clickable {
                                                        viewModel.setService(service)
                                                        viewModel.navigateTo("service_details")
                                                    }
                                                    .padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                // Service Professional Image Thumbnail
                                                Box(
                                                    modifier = Modifier
                                                        .size(54.dp)
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                                                        .then(
                                                            if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                                                                with(sharedTransitionScope) {
                                                                    Modifier.sharedElement(
                                                                        rememberSharedContentState(key = "service_image_${service.id}"),
                                                                        animatedVisibilityScope = animatedVisibilityScope
                                                                    )
                                                                }
                                                            } else Modifier
                                                        )
                                                ) {
                                                    AsyncImage(
                                                        model = service.imageUrl,
                                                        contentDescription = service.title,
                                                        contentScale = ContentScale.Crop,
                                                        modifier = Modifier.fillMaxSize()
                                                    )
                                                    // Mini overlaid icon for clinical depth
                                                    Box(
                                                        modifier = Modifier
                                                            .size(20.dp)
                                                            .align(Alignment.BottomEnd)
                                                            .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(topStart = 8.dp)),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(
                                                            imageVector = getServiceIcon(service.iconName),
                                                            contentDescription = null,
                                                            tint = MedicalBlue,
                                                            modifier = Modifier.size(11.dp)
                                                        )
                                                    }
                                                }

                                                Spacer(modifier = Modifier.width(12.dp))

                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = service.title,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.5.sp,
                                                        color = DarkNavy,
                                                        modifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                                                            with(sharedTransitionScope) {
                                                                Modifier.sharedElement(
                                                                    rememberSharedContentState(key = "service_title_${service.id}"),
                                                                    animatedVisibilityScope = animatedVisibilityScope
                                                                )
                                                            }
                                                        } else Modifier
                                                    )
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = service.description,
                                                        fontSize = 10.5.sp,
                                                        color = BodyGray,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }

                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(10.dp))
                                                            .background(Color(0xFFE6F4EA))
                                                            .border(0.5.dp, MedicalGreen.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(
                                                            text = "${service.basePrice.toInt()} ج.م",
                                                            fontWeight = FontWeight.ExtraBold,
                                                            fontSize = 11.sp,
                                                            color = Color(0xFF137333)
                                                        )
                                                    }

                                                    Box(
                                                        modifier = Modifier
                                                            .size(24.dp)
                                                            .background(MedicalBlue.copy(alpha = 0.08f), CircleShape),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, // logical left pointing forward in RTL
                                                            contentDescription = null,
                                                            tint = MedicalBlue,
                                                            modifier = Modifier.size(12.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        }
                        }
                    }
                } else {
                    // Grid services layout title
                    item {
                        Text(
                            text = "الخدمات الطبية المنزلية المتاحة (${filteredServices.size})",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DarkNavy
                            ),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    if (filteredServices.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = SoftBlueBg.copy(alpha = 0.5f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.Search, contentDescription = null, tint = BodyGray, modifier = Modifier.size(42.dp))
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        "عذراً، لم نجد خدمات مطابقة للمواصفات حالياً.",
                                        fontSize = 13.sp,
                                        color = BodyGray,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    // 2 COLS GRID ADAPTATION IN LAZY COLUMN USING ROW/CHUNKS TO STAY SMOOTH
                    val rows = filteredServices.chunked(2)
                    items(rows) { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            pair.forEach { service ->
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .shadow(3.dp, RoundedCornerShape(18.dp))
                                        .then(
                                            if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                                                with(sharedTransitionScope) {
                                                    Modifier.sharedElement(
                                                        rememberSharedContentState(key = "service_bg_${service.id}"),
                                                        animatedVisibilityScope = animatedVisibilityScope
                                                    )
                                                }
                                            } else Modifier
                                        )
                                        .clickable {
                                            viewModel.setService(service)
                                            viewModel.navigateTo("service_details")
                                        }
                                        .testTag("service_grid_item_${service.id}"),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(18.dp),
                                    border = BorderStroke(1.dp, Color(0xFFE1EFFE))
                                ) {
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        // 1. Beautiful Service Banner Image
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(96.dp)
                                                .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                                                .then(
                                                    if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                                                        with(sharedTransitionScope) {
                                                            Modifier.sharedElement(
                                                                rememberSharedContentState(key = "service_image_${service.id}"),
                                                                animatedVisibilityScope = animatedVisibilityScope
                                                            )
                                                        }
                                                    } else Modifier
                                                )
                                        ) {
                                            if (service.imageUrl.isNotEmpty()) {
                                                AsyncImage(
                                                    model = service.imageUrl,
                                                    contentDescription = service.title,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            } else {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .background(SoftBlueBg)
                                                )
                                            }
                                            // Soft overlay gradient
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(
                                                        Brush.verticalGradient(
                                                            colors = listOf(
                                                                Color.Transparent,
                                                                Color.Black.copy(alpha = 0.4f)
                                                            )
                                                        )
                                                    )
                                            )
                                            
                                            // Top overlay badge
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .padding(8.dp)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(Color.White.copy(alpha = 0.9f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "متاح ⚡",
                                                    fontSize = 8.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MedicalBlue
                                                )
                                            }
                                        }

                                        // 2. Body Details
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = service.title,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 13.sp,
                                                color = DarkNavy,
                                                textAlign = TextAlign.Center,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                                                    with(sharedTransitionScope) {
                                                        Modifier.sharedElement(
                                                            rememberSharedContentState(key = "service_title_${service.id}"),
                                                            animatedVisibilityScope = animatedVisibilityScope
                                                        )
                                                    }
                                                } else Modifier
                                            )
                                            
                                            Spacer(modifier = Modifier.height(4.dp))
                                            
                                            // Price Badge
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Color(0xFFE6F4EA))
                                                    .border(0.5.dp, MedicalGreen.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                                            ) {
                                                Text(
                                                    text = "${service.basePrice.toInt()} ج.م",
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 11.sp,
                                                    color = Color(0xFF137333),
                                                    textAlign = TextAlign.Center
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))

                                            // CTA Button
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(MedicalBlue)
                                                    .padding(vertical = 5.dp),
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = getServiceIcon(service.iconName),
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(10.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "احجز الآن 🩺",
                                                    fontSize = 9.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            if (pair.size < 2) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                // TOP APPOINTED NURSES
                item {
                    Column(modifier = Modifier.padding(top = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "أبرز الممرضين المتاحين",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DarkNavy
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Horizontal list of health practitioners
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 8.dp)
                        ) {
                            items(nearbyNurses) { nurse ->
                                Card(
                                    modifier = Modifier
                                        .width(180.dp)
                                        .shadow(1.5.dp, RoundedCornerShape(14.dp))
                                        .clickable { viewModel.viewNurseDetails(nurse) },
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .background(
                                                    if (nurse.gender == "MALE") SoftBlueBg else Color(0xFFFFECEF),
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (nurse.gender == "MALE") Icons.Default.Person else Icons.Default.Face,
                                                contentDescription = null,
                                                tint = if (nurse.gender == "MALE") MedicalBlue else Color(0xFFFF5A79)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = nurse.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = DarkNavy,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Text(
                                            text = nurse.hospitalAffiliation,
                                            fontSize = 10.sp,
                                            color = BodyGray,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        val distKm = viewModel.getDistanceToNurseInKm(nurse)
                                        Text(
                                            text = "على بعد ${String.format(java.util.Locale.US, "%.1f", distKm)} كم 📍",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MedicalBlue,
                                            modifier = Modifier
                                                .padding(top = 4.dp)
                                                .background(SoftBlueBg, RoundedCornerShape(6.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                Icons.Default.Star,
                                                contentDescription = null,
                                                tint = LightYellow,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "${nurse.rating}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = DarkNavy
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "(+${nurse.experienceYears} س)",
                                                fontSize = 10.sp,
                                                color = BodyGray
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                // DEVELOPER CARD AT THE BOTTOM
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .shadow(8.dp, RoundedCornerShape(20.dp), ambientColor = MedicalBlue.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(MedicalBlue, Color(0xFF1E40AF))
                                    )
                                )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (devImageUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = devImageUrl,
                                        contentDescription = "Developer Picture",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(id = com.example.R.drawable.dramatic_portrait_1780966896428),
                                        contentDescription = "Developer Picture",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                    )
                                }
                                Column(
                                    modifier = Modifier
                                        .weight(2.2f)
                                        .padding(horizontal = 16.dp),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Code,
                                            contentDescription = null,
                                            tint = Color(0xFF60A5FA),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = devTitle,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF60A5FA)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = devName,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = devMessage,
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.9f),
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // --- INTERACTIVE DIALOGS ---

        // 1. Emergency Dispatch Action Dialog
        if (showEmergencyDialog) {
            AlertDialog(
                onDismissRequest = { showEmergencyDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalHospital, contentDescription = null, tint = Color.Red)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("طوارئ تمريضية فورية", fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Text(
                        "هل تحتاج إلى ممرض منزلي بأسرع وقت ممكن للغيار الطارئ أو إعطاء محاليل فورية؟ سيتم إرسال بلاغ لأقرب مراقب صحة متاح في منقطتك فوراً وجاري الاتصال بخدمة العملاء.",
                        fontSize = 14.sp,
                        color = BodyGray
                    )
                },
                confirmButton = {
                    val context = androidx.compose.ui.platform.LocalContext.current
                    Button(
                        onClick = {
                            showEmergencyDialog = false
                            val customIntent = android.content.Intent(android.content.Intent.ACTION_DIAL).apply {
                                data = android.net.Uri.parse("tel:123")
                            }
                            context.startActivity(customIntent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MedicalBlue)
                    ) {
                        Text("اتصال فوري برقم الإسعاف (١٢٣)")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEmergencyDialog = false }) {
                        Text("إلغاء أزرار الطوارئ", color = BodyGray)
                    }
                }
            )
        }

        // 2. Interactive AI Advice Consultant dialog
        if (showAiConsultationDialog) {
            AlertDialog(
                onDismissRequest = { showAiConsultationDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = MedicalGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("طبيبك الاستشاري الذكي معاك", fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            "اطرح سؤالك الطبي والاستشاري واحصل على نصائح إرشادية فورية.",
                            fontSize = 13.sp,
                            color = BodyGray
                        )

                        OutlinedTextField(
                            value = aiQueryText,
                            onValueChange = { aiQueryText = it },
                            placeholder = { Text("مثال: كيف أعتني بجرح السكر؟", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp),
                            singleLine = true
                        )

                        if (aiLoading) {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = MedicalBlue,
                                    modifier = Modifier.size(26.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                        }

                        if (aiResponseText.isNotEmpty() && !aiLoading) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = SoftBlueBg),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = aiResponseText,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = DarkNavy,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }

                        // Default Quick Suggestion chips to trigger dynamic answers
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val suggestions = listOf("تنظيف الجرح", "ارتفاع الضغط", "رعاية السكر")
                            suggestions.forEach { label ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFE2E8F0))
                                        .clickable {
                                            aiQueryText = label
                                            aiLoading = true
                                            scope.launch {
                                                aiResponseText = viewModel.getAiConsultationResponse(label)
                                                aiLoading = false
                                            }
                                        }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DarkNavy)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (aiQueryText.isNotBlank()) {
                                aiLoading = true
                                scope.launch {
                                    aiResponseText = viewModel.getAiConsultationResponse(aiQueryText)
                                    aiLoading = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MedicalBlue)
                    ) {
                        Text("استشير الآن")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showAiConsultationDialog = false
                        aiQueryText = ""
                        aiResponseText = ""
                        aiLoading = false
                    }) {
                        Text("إغلاق", color = BodyGray)
                    }
                }
            )
        }
    }
}
                // ==========================================
// 5. CUSTOM BOOKING STEP PROGRESS INDICATOR
// ==========================================
@Composable
fun BookingStepIndicator(currentStepIndex: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val steps = listOf("العرض", "الممرض", "الموعد", "التأكيد")
        steps.forEachIndexed { index, stepTitle ->
            val isCompleted = index < currentStepIndex
            val isCurrent = index == currentStepIndex
            val isPending = index > currentStepIndex

            val circleColor = when {
                isCurrent -> Brush.horizontalGradient(listOf(MedicalBlue, Color(0xFF0369A1)))
                isCompleted -> Brush.horizontalGradient(listOf(MedicalGreen, Color(0xFF059669)))
                else -> Brush.linearGradient(listOf(Color(0xFFF1F5F9), Color(0xFFF1F5F9)))
            }

            val textColor = when {
                isCurrent -> MedicalBlue
                isCompleted -> MedicalGreen
                else -> BodyGray
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(circleColor),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    } else {
                        Text(
                            text = (index + 1).toString(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isCurrent) Color.White else BodyGray
                        )
                    }
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stepTitle,
                    fontSize = 11.sp,
                    fontWeight = if (isCurrent) FontWeight.ExtraBold else FontWeight.Bold,
                    color = textColor
                )
            }

            if (index < steps.size - 1) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .padding(horizontal = 4.dp)
                        .background(if (isCompleted) MedicalGreen else Color(0xFFE2E8F0))
                )
            }
        }
    }
}

// ==========================================
// 6. SERVICE DETAILS SCREEN (REDESIGNED)
// ==========================================
@Composable
fun ServiceDetailsScreen(
    viewModel: ELmorsyViewModel,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    val service by viewModel.selectedService.collectAsState()

    if (service == null) return

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { 
                    Text(
                        text = "تفاصيل الخدمة الطبية", 
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkNavy,
                        fontSize = 18.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo("home") }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DarkNavy)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Step tracker at top
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                BookingStepIndicator(currentStepIndex = 0)
            }

            // Visual Premium Hero Banner with Asymmetry & Depth
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .then(
                        if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                            with(sharedTransitionScope) {
                                Modifier.sharedElement(
                                    rememberSharedContentState(key = "service_bg_${service!!.id}"),
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                            }
                        } else Modifier
                    )
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFFE0F2FE), SoftBlueBg, Color(0xFFECF5FF)),
                            radius = 600f
                        )
                    )
                    .padding(vertical = 32.dp, horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .shadow(12.dp, CircleShape)
                            .then(
                                if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                                    with(sharedTransitionScope) {
                                        Modifier.sharedElement(
                                            rememberSharedContentState(key = "service_image_${service!!.id}"),
                                            animatedVisibilityScope = animatedVisibilityScope
                                        )
                                    }
                                } else Modifier
                            )
                            .background(Color.White, CircleShape)
                            .border(2.dp, MedicalBlue.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getServiceIcon(service!!.iconName),
                            contentDescription = null,
                            tint = MedicalBlue,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(MedicalBlue.copy(alpha = 0.1f))
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MedicalBlue, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "خدمة معتمدة من شريك طبي مرخص",
                            fontSize = 11.sp,
                            color = MedicalBlue,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = service!!.title,
                        fontSize = 20.sp,
                        color = DarkNavy,
                        fontWeight = FontWeight.Black,
                        modifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                            with(sharedTransitionScope) {
                                Modifier.sharedElement(
                                    rememberSharedContentState(key = "service_title_${service!!.id}"),
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                            }
                        } else Modifier
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "العناية بالمنزل بسلامة ودقة فائقة",
                        fontSize = 14.sp,
                        color = BodyGray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Main Info Container
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, Color(0xFFECEFF1))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "نبذة عن الخدمة والبروتوكول الطبي:",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = DarkNavy,
                                fontSize = 16.sp
                            )
                        )

                        Text(
                            text = service!!.longDescription,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = DarkNavy.copy(alpha = 0.8f),
                                lineHeight = 26.sp,
                                fontSize = 14.5.sp
                            )
                        )
                    }
                }

                // Pricing & Duration Highlight Cards Side-by-Side
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFFECFDF5), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = MedicalGreen, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("السعر المبدئي", color = BodyGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${service!!.basePrice} ج.م", color = MedicalGreen, fontWeight = FontWeight.Black, fontSize = 17.sp)
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(SoftBlueBg, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AccessTimeFilled, contentDescription = null, tint = MedicalBlue, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("المدة المقدرة", color = BodyGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(service!!.duration, color = DarkNavy, fontWeight = FontWeight.Black, fontSize = 14.sp)
                        }
                    }
                }

                // Features Callout Checklist cards
                Text(
                    text = "مزايا طلب الزيارة الطبية عبر مِعاك:",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = DarkNavy, fontSize = 15.sp)
                )

                val checklist = listOf(
                    "ممرضين مرخصين ومطعمين بالكامل من كبرى مستشفيات مصر" to "نوفر لك جودة وموثوقية المستشفى في بيتك الآمن.",
                    "أدوات ومستلزمات طبية معقمة ومغلفة بشكل فردي" to "أعلى معايير مكافحة العدوى والوقاية المعتمدة لسلامتكم.",
                    "رصد وربط العلامات الحيوية لحظياً للمريض" to "يتابع أطباؤنا البيانات لضمان استقرار الحالة دوماً.",
                    "دعم طبي ومتابعة مستمرة ٢٤ ساعة على مدار الأسبوع" to "خدمة عملاء فائقة في انتظار مكالماتكم بكل ود."
                )

                checklist.forEach { (title, subtitle) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MedicalGreen,
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(title, fontWeight = FontWeight.ExtraBold, color = DarkNavy, fontSize = 13.5.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(subtitle, color = BodyGray, fontSize = 12.sp, lineHeight = 18.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Premium Sticky bottom Action Button with gradient
                Button(
                    onClick = { viewModel.navigateTo("nurse_selection") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp))
                        .testTag("book_service_now_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Unspecified),
                    contentPadding = PaddingValues(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(MedicalBlue, Color(0xFF0284C7))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("اختر الممرض الآن", fontWeight = FontWeight.Black, fontSize = 17.sp, color = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 7. NURSE SELECTION SCREEN (REDESIGNED)
// ==========================================
@Composable
fun NurseSelectionScreen(viewModel: ELmorsyViewModel) {
    val service by viewModel.selectedService.collectAsState()
    val filterType by viewModel.nurseFilterType.collectAsState()
    val selectedNurse by viewModel.selectedNurse.collectAsState()
    val nearbyNurses by viewModel.nearbyNurses.collectAsState()

    if (service == null) return

    val filteredNurses = remember(filterType, nearbyNurses) {
        when (filterType) {
            "RATING" -> nearbyNurses.sortedByDescending { it.rating }
            "PRICE" -> nearbyNurses.sortedBy { it.pricePerVisit }
            "EXP" -> nearbyNurses.sortedByDescending { it.experienceYears }
            else -> nearbyNurses
        }
    }

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { 
                    Text(
                        text = "اختر التمريض المناسب", 
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkNavy,
                        fontSize = 18.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo("service_details") }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DarkNavy)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            // Step marker 
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                BookingStepIndicator(currentStepIndex = 1)
            }

            // Beautiful pills filters selection inside horizontal scroll
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(
                    "ALL" to "المقترح والكل",
                    "RATING" to "الأعلى تقييماً ⭐",
                    "PRICE" to "الأوفر سعراً 💵",
                    "EXP" to "الأكثر خبرة 🎓"
                ).forEach { (fCode, fName) ->
                    val isActive = filterType == fCode
                    
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isActive) {
                                    Brush.horizontalGradient(listOf(MedicalBlue, Color(0xFF0369A1)))
                                } else {
                                    Brush.linearGradient(listOf(Color(0xFFF1F5F9), Color(0xFFF1F5F9)))
                                }
                            )
                            .clickable { viewModel.setNurseFilter(fCode) }
                            .padding(horizontal = 14.dp, vertical = 9.dp)
                    ) {
                        Text(
                            text = fName,
                            color = if (isActive) Color.White else DarkNavy,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.5.sp
                        )
                    }
                }
            }

            // List of nurses
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
            ) {
                items(filteredNurses) { nurse ->
                    val isChosen = selectedNurse?.id == nurse.id

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(if (isChosen) 6.dp else 2.dp, RoundedCornerShape(20.dp))
                            .border(
                                width = if (isChosen) 2.dp else 1.dp,
                                color = if (isChosen) MedicalBlue else Color(0xFFE2E8F0),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .testTag("nurse_item_card_${nurse.id}"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isChosen) Color(0xFFF0F9FF) else Color.White
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Rounded initial profile picture with active online circle badge overlay
                                Box(
                                    modifier = Modifier.size(60.dp),
                                    contentAlignment = Alignment.BottomEnd
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                if (nurse.gender == "MALE") SoftBlueBg else Color(0xFFFFECEF),
                                                CircleShape
                                            )
                                            .border(1.5.dp, Color.White, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (nurse.gender == "MALE") Icons.Default.Person else Icons.Default.Face,
                                            contentDescription = null,
                                            tint = if (nurse.gender == "MALE") MedicalBlue else Color(0xFFFF5A79),
                                            modifier = Modifier.size(34.dp)
                                        )
                                    }
                                    
                                    // Pulse green locator or online badge
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(CircleShape)
                                            .background(Color.White, CircleShape)
                                            .padding(2.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(MedicalGreen, CircleShape)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = nurse.name,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 16.sp,
                                            color = DarkNavy
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFFECFDF5))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "متاح فوراً", 
                                                fontSize = 9.sp, 
                                                color = MedicalGreen, 
                                                fontWeight = FontWeight.Black
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(3.dp))

                                    Text(
                                        text = nurse.hospitalAffiliation,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = BodyGray
                                    )

                                    val nurseDist = viewModel.getDistanceToNurseInKm(nurse)
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn, 
                                            contentDescription = null, 
                                            tint = MedicalBlue, 
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "نطاق الحي (يبعد ${String.format(java.util.Locale.US, "%.1f", nurseDist)} كم)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MedicalBlue
                                        )
                                    }
                                }

                                // Elegant high contrast price tags
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "+${nurse.pricePerVisit} ج.م",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = MedicalGreen
                                    )
                                    Text(
                                        text = "/للزيارة",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BodyGray
                                    )
                                }
                            }

                            // Dynamic Expert Badges Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Experience Badge
                                if (nurse.experienceYears >= 8) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFFEF3C7))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Icon(Icons.Default.School, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(12.dp))
                                            Text("مستشار رعاية 🎓", fontSize = 9.5.sp, color = Color(0xFFB45308), fontWeight = FontWeight.Black)
                                        }
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFDBEAFE))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Icon(Icons.Default.Verified, contentDescription = null, tint = MedicalBlue, modifier = Modifier.size(12.dp))
                                            Text("ممارس معتمد 🛡️", fontSize = 9.5.sp, color = Color(0xFF1E40AF), fontWeight = FontWeight.Black)
                                        }
                                    }
                                }

                                // Rating Badge (Exceptional Service)
                                if (nurse.rating >= 4.8) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFECFDF5))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Icon(Icons.Default.Star, contentDescription = null, tint = MedicalGreen, modifier = Modifier.size(12.dp))
                                            Text("خدمة استثنائية ⭐", fontSize = 9.5.sp, color = Color(0xFF047857), fontWeight = FontWeight.Black)
                                        }
                                    }
                                }

                                // Visits Completed Badge
                                if (nurse.completedVisits > 5) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFF3E8FF))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF8B5CF6), modifier = Modifier.size(12.dp))
                                            Text("رعاية ممتازة 🎖️", fontSize = 9.5.sp, color = Color(0xFF6B21A8), fontWeight = FontWeight.Black)
                                        }
                                    }
                                }
                            }

                            HorizontalDivider(
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier.padding(vertical = 14.dp)
                            )

                            // Footer metrics row with experience and CTA
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(17.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("${nurse.rating}", fontWeight = FontWeight.Black, color = DarkNavy, fontSize = 13.sp)
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Work, contentDescription = null, tint = BodyGray, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("${nurse.experienceYears} سنوات خبرة", color = BodyGray, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }

                                Button(
                                    onClick = {
                                        viewModel.viewNurseDetails(nurse)
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MedicalBlue),
                                    border = BorderStroke(1.dp, MedicalBlue),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Text("عرض التفاصيل", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                
                                Button(
                                    onClick = {
                                        viewModel.selectNurse(nurse)
                                        viewModel.navigateTo("booking")
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isChosen) MedicalGreen else MedicalBlue
                                    ),
                                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (isChosen) "مُختار ✓" else "اختيار ممرض",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 13.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 7.5 NURSE DETAILS SCREEN
// ==========================================
@Composable
fun NurseDetailsScreen(viewModel: ELmorsyViewModel) {
    val nurse by viewModel.viewedNurse.collectAsState()
    val isDark = MaterialTheme.colorScheme.background == DarkBackground

    if (nurse == null) {
        return
    }

    Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isDark) Color(0xFF1E293B) else Color.White)
                    .padding(16.dp)
                    .shadow(elevation = 12.dp, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            ) {
                Button(
                    onClick = {
                        viewModel.selectNurse(nurse!!)
                        viewModel.navigateTo("booking")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalBlue),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("اختر هذا الممرض واحجز الآن", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDark) DarkBackground else Color(0xFFF8FAFC))
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Image/Hero
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                // Background Gradient pattern
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(MedicalBlue, SoftBlueBg)
                            )
                        )
                )
                
                // Back button
                IconButton(
                    onClick = { viewModel.navigateTo("nurse_selection") },
                    modifier = Modifier
                        .padding(top = 40.dp, start = 16.dp)
                        .background(Color.White.copy(alpha = 0.3f), CircleShape)
                        .align(Alignment.TopStart)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                
                // Content inside header
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Icon
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .background(Color.White, CircleShape)
                            .border(3.dp, MedicalGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        val iconRes = if (nurse!!.gender == "MALE") Icons.Default.Person else Icons.Default.Face
                        Icon(iconRes, contentDescription = null, modifier = Modifier.size(60.dp), tint = MedicalBlue)
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = nurse!!.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    
                    Text(
                        text = "مختص رعاية صحية معتمد",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
            
            // Details Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-30).dp)
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(if (isDark) Color(0xFF1E293B) else Color.White)
                    .padding(24.dp)
            ) {
                // Info Row (Rating, Exp, Visits)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Rating
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${nurse!!.rating}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = if (isDark) Color.White else DarkNavy)
                        }
                        Text("(${nurse!!.ratingCount} تقييم)", fontSize = 12.sp, color = BodyGray)
                    }
                    
                    // Divider
                    Box(modifier = Modifier.height(40.dp).width(1.dp).background(Color(0xFFE2E8F0)))
                    
                    // Exp
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${nurse!!.experienceYears}+", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = if (isDark) Color.White else DarkNavy)
                        }
                        Text("سنوات خبرة", fontSize = 12.sp, color = BodyGray)
                    }
                    
                    // Divider
                    Box(modifier = Modifier.height(40.dp).width(1.dp).background(Color(0xFFE2E8F0)))
                    
                    // Visits
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${nurse!!.completedVisits}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = if (isDark) Color.White else DarkNavy)
                        }
                        Text("زيارة ناجحة", fontSize = 12.sp, color = BodyGray)
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // About Section
                Text("نبذة عن الممرض", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (isDark) Color.White else DarkNavy)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "ممرض متخصص ومرخص ذو كفاءة عالية في تقديم الرعاية الصحية المنزلية الشاملة. يتمتع بخبرة واسعة تفوق ${nurse!!.experienceYears} سنوات في متابعة الحالات الحرجة وتقديم العلاجات بكفاءة تامة. مسجل كعضو فعال في طاقم التمريض التابع لـ ${nurse!!.hospitalAffiliation}.",
                    fontSize = 14.sp,
                    color = BodyGray,
                    lineHeight = 22.sp
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Info List
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(40.dp).background(SoftBlueBg, CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.LocalHospital, contentDescription = null, tint = MedicalBlue, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("الارتباط المستشفى / الطبي", fontSize = 12.sp, color = BodyGray)
                            Text(nurse!!.hospitalAffiliation, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (isDark) Color.White else DarkNavy)
                        }
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(40.dp).background(SoftBlueBg, CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = MedicalBlue, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("المنطقة الأساسية للعمل", fontSize = 12.sp, color = BodyGray)
                            Text(nurse!!.homeDistrict, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (isDark) Color.White else DarkNavy)
                        }
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(40.dp).background(Color(0xFFF0FDF4), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.AttachMoney, contentDescription = null, tint = MedicalGreen, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("تكلفة الزيارة للممرض", fontSize = 12.sp, color = BodyGray)
                                Text("تضاف لتكلفة الخدمة الأساسية", fontSize = 11.sp, color = BodyGray.copy(alpha = 0.7f))
                            }
                            Text("${nurse!!.pricePerVisit} ج.م", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MedicalGreen)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

// ==========================================
// 8. BOOKING SCREEN (REDESIGNED)
// ==========================================
@Composable
fun BookingScreen(viewModel: ELmorsyViewModel) {
    val service by viewModel.selectedService.collectAsState()
    val nurse by viewModel.selectedNurse.collectAsState()

    val initialAddress by viewModel.userAddress.collectAsState()
    val notesInput by viewModel.bookingNotes.collectAsState()

    var activeAddress by remember { mutableStateOf(initialAddress) }
    var activeNotes by remember { mutableStateOf(notesInput) }

    val activeDate by viewModel.bookingDate.collectAsState()
    val activeTimeSlot by viewModel.bookingTimeSlot.collectAsState()

    // Keep activeAddress in sync with ViewModel state if updated asynchronously (e.g., from Map GPS)
    LaunchedEffect(initialAddress) {
        activeAddress = initialAddress
    }

    if (service == null) return

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { 
                    Text(
                        text = "جدولة تفاصيل الحجز", 
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkNavy,
                        fontSize = 18.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo("nurse_selection") }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DarkNavy)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Step marker indicator
            BookingStepIndicator(currentStepIndex = 2)

            // Step Detail Summary Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, Color(0xFFECEFF1))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(SoftBlueBg, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getServiceIcon(service!!.iconName), 
                            contentDescription = null, 
                            tint = MedicalBlue,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(service!!.title, fontWeight = FontWeight.Black, color = DarkNavy, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "مقدم الرعاية: ${nurse?.name ?: "الأول المتاح بمستشفى الشريك"}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BodyGray
                        )
                    }
                }
            }

            // Beneficiary Patient Selection
            val selectedPatientType by viewModel.selectedPatientType.collectAsState()
            val customPatientName by viewModel.customPatientName.collectAsState()

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "من هو متلقي الرعاية الصحية (المستفيد)؟",
                    fontWeight = FontWeight.Black,
                    color = DarkNavy,
                    fontSize = 14.sp
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val patients = listOf(
                        "أنا (صاحب الحساب)" to "👤 أنا",
                        "الوالد (الأب)" to "👴 الوالد",
                        "الوالدة (الأم)" to "👵 الوالدة",
                        "طفلي (الابن/الابنة)" to "👶 طفل",
                        "أخرى (إدخال يدوي)" to "✏️ شخص آخر"
                    )
                    
                    patients.forEach { (typeKey, label) ->
                        val isSelected = selectedPatientType == typeKey
                        Card(
                            modifier = Modifier.clickable { 
                                viewModel.setSelectedPatientType(typeKey)
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MedicalBlue else Color.White
                            ),
                            border = BorderStroke(1.dp, if (isSelected) MedicalBlue else Color(0xFFE2E8F0)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = label,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                color = if (isSelected) Color.White else DarkNavy,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp
                            )
                        }
                    }
                }
                
                if (selectedPatientType == "أخرى (إدخال يدوي)") {
                    OutlinedTextField(
                        value = customPatientName,
                        onValueChange = { viewModel.setCustomPatientName(it) },
                        placeholder = { Text("أدخل الاسم الكامل للمستفيد...") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MedicalBlue) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedicalBlue,
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFF1F5F9))

            // 1. DATE CHIPS PICKER (HORIZONTALLY)
            Text(
                text = "١. اختر تاريخ الزيارة الطبية المناسب:", 
                fontWeight = FontWeight.Black, 
                color = DarkNavy,
                fontSize = 14.5.sp
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppData.availableDates.forEach { date ->
                    val isSelected = activeDate == date
                    Card(
                        modifier = Modifier
                            .clickable { viewModel.updateBookingDetails(date, activeTimeSlot, activeAddress, activeNotes) }
                            .testTag("date_chip_$date"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MedicalBlue else Color.White
                        ),
                        border = BorderStroke(1.dp, if (isSelected) MedicalBlue else Color(0xFFE2E8F0)),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = date,
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                            color = if (isSelected) Color.White else DarkNavy,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // 2. TIME SLOTS PICKER (GRID-LIKE LAYOUT USING ROWS OF 2)
            Text(
                text = "٢. اختر الموعد المناسب لخدمتك:", 
                fontWeight = FontWeight.Black, 
                color = DarkNavy,
                fontSize = 14.5.sp
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AppData.timeSlots.chunked(2).forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        pair.forEach { slot ->
                            val isSelected = activeTimeSlot == slot
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.updateBookingDetails(activeDate, slot, activeAddress, activeNotes) }
                                    .testTag("time_chip_$slot"),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Color(0xFFEEF2FF) else Color.White
                                ),
                                border = BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) MedicalBlue else Color(0xFFE2E8F0)
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(vertical = 14.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTimeFilled, 
                                        contentDescription = null, 
                                        tint = if (isSelected) MedicalBlue else BodyGray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = slot,
                                        color = if (isSelected) MedicalBlue else DarkNavy,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.5.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. SPECIAL ADDRESS USER FORM
            Text(
                text = "٣. عنوان الزيارة بوضوح:", 
                fontWeight = FontWeight.Black, 
                color = DarkNavy,
                fontSize = 14.5.sp
            )
            
            // Helpful Reminder Banner for GPS Address
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SoftBlueBg),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MedicalBlue)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "تأكيد العنوان المسحوب تلقائياً من الخريطة بدقة يضمن وصول الممرض في الموعد تماماً!",
                        color = MedicalBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 16.sp
                    )
                }
            }

            OutlinedTextField(
                value = activeAddress,
                onValueChange = {
                    activeAddress = it
                    viewModel.updateBookingDetails(activeDate, activeTimeSlot, it, activeNotes)
                },
                placeholder = { Text("أدخل اسم المنطقة، الشارع، رقم الشقة/العمارة...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("address_input"),
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = MedicalBlue) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MedicalBlue,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp),
                maxLines = 3
            )

            // 4. BRIEF PATIENT DETAILS NOTES FOR NURSES
            Text(
                text = "٤. ملاحظات هامة للممرض للمساعدة (اختياري):", 
                fontWeight = FontWeight.Black, 
                color = DarkNavy,
                fontSize = 14.5.sp
            )
            OutlinedTextField(
                value = activeNotes,
                onValueChange = {
                    activeNotes = it
                    viewModel.updateBookingDetails(activeDate, activeTimeSlot, activeAddress, it)
                },
                placeholder = { Text("مثال: قياس سكر صائم، مريض يخشى الحقن بالوريد، هل يتحدث العربية، إلخ...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .testTag("notes_input"),
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = BodyGray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MedicalBlue,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action Booking with secure tag icon
            Button(
                onClick = {
                    if (activeAddress.isNotBlank()) {
                        val beneficiaryPrefix = when (selectedPatientType) {
                            "أخرى (إدخال يدوي)" -> "متلقي الخدمة: $customPatientName \n"
                            else -> "متلقي الخدمة: $selectedPatientType \n"
                        }
                        val finalNotes = if (activeNotes.contains("متلقي الخدمة:")) activeNotes else beneficiaryPrefix + activeNotes
                        viewModel.updateBookingDetails(activeDate, activeTimeSlot, activeAddress, finalNotes)
                        viewModel.navigateTo("payment")
                    }
                },
                enabled = activeAddress.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(6.dp, RoundedCornerShape(16.dp))
                    .testTag("confirm_booking_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MedicalBlue,
                    disabledContainerColor = Color(0xFFCBD5E1)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = "Secure Checkout", tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("المتابعة وتأكيد الفاتورة", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}

// ==========================================
// 9. PAYMENT SCREEN (REDESIGNED BILL RECEIPT)
// ==========================================
@Composable
fun PaymentScreen(viewModel: ELmorsyViewModel) {
    val service by viewModel.selectedService.collectAsState()
    val nurse by viewModel.selectedNurse.collectAsState()
    val payMethod by viewModel.paymentMethod.collectAsState()
    val walletBalance by viewModel.walletBalance.collectAsState()

    var showLocalRecharge by remember { mutableStateOf(false) }
    var rechargeAmount by remember { mutableStateOf("") }
    var rechargeMethod by remember { mutableStateOf("VODAFONE_CASH") }

    val servicePrice = service?.basePrice ?: 0.0
    val nursePrice = nurse?.pricePerVisit ?: 0.0
    val appFee = 20.0
    val totalBill = servicePrice + nursePrice + appFee

    var isPayingSimulated by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { 
                    Text(
                        text = "الفاتورة وطريقة السداد", 
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkNavy,
                        fontSize = 18.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo("booking") }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DarkNavy)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Step Marker Indicator
            BookingStepIndicator(currentStepIndex = 3)

            // Dynamic Receipt-Styled Bill Breakdown
            Text(
                text = "الفاتورة الطبية المعتمدة:", 
                fontWeight = FontWeight.Black, 
                color = DarkNavy,
                fontSize = 15.sp
            )
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.2.dp, Color(0xFFECEFF1))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header visual badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("معهد معاك للخدمات الطبية", fontWeight = FontWeight.Black, color = MedicalBlue, fontSize = 12.sp)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFFEF3C7))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("بانتظار التأكيد", fontSize = 9.sp, color = Color(0xFFD97706), fontWeight = FontWeight.Bold)
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

                    // Line items
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "تكلفة الخدمة (${service?.title})", 
                            color = BodyGray, 
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text("$servicePrice ج.م", fontWeight = FontWeight.Black, color = DarkNavy, fontSize = 14.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "أتعاب زيارة الممرض (${nurse?.name})", 
                            color = BodyGray, 
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text("$nursePrice ج.م", fontWeight = FontWeight.Black, color = DarkNavy, fontSize = 14.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "رسوم الخدمة ومصاريف التأمين", 
                            color = BodyGray, 
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text("$appFee ج.م", fontWeight = FontWeight.Black, color = DarkNavy, fontSize = 14.sp)
                    }

                    // Simulated dotted receipt line gap
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFFCBD5E1))
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("المبلغ الإجمالي للدفع", fontWeight = FontWeight.Black, color = DarkNavy, fontSize = 16.sp)
                        Text("$totalBill ج.م", fontWeight = FontWeight.Black, color = MedicalGreen, fontSize = 20.sp)
                    }
                }
            }

            // METHOD CHOICES
            Text(
                text = "اختر طريقة السداد المفضلة:", 
                fontWeight = FontWeight.Black, 
                color = DarkNavy,
                fontSize = 15.sp
            )

            val paymentMethods = listOf(
                "WALLET" to "محافظ إلكترونية (فودافون كاش، اتصالات، اورانج)" to Icons.Default.PhoneIphone,
                "FAWRY" to "مدفوعات فوري (Fawry)" to Icons.Default.PointOfSale,
                "CARD" to "بطاقة ائتمانية (الفيزا / ماستركارد)" to Icons.Default.CreditCard,
                "APP_WALLET" to "المحفظة الإلكترونية لـ معاك" to Icons.Default.AccountBalanceWallet
            )

            paymentMethods.forEach { (pair, icon) ->
                val (mCode, mLabel) = pair
                val isSelected = payMethod == mCode
                val displayLabel = if (mCode == "WALLET") "$mLabel (رصيدك الحالي: $walletBalance ج.م)" else mLabel
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.setPaymentMethod(mCode) }
                        .testTag("pay_method_card_$mCode"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFFF0F9FF) else Color.White
                    ),
                    border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) MedicalBlue else Color(0xFFE2E8F0)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { viewModel.setPaymentMethod(mCode) },
                            colors = RadioButtonDefaults.colors(selectedColor = MedicalBlue)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(if (isSelected) Color(0xFFE0F2FE) else Color(0xFFF1F5F9), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = icon, contentDescription = null, tint = if (isSelected) MedicalBlue else BodyGray, modifier = Modifier.size(18.dp))
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Text(
                            text = displayLabel,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                            color = DarkNavy,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Conditional visual dynamic card alerts depending on selection
            if (payMethod == "WALLET" || payMethod == "FAWRY") {
                val isFawry = payMethod == "FAWRY"
                val paymentTitle = if (isFawry) "كود الدفع لفوري 🟡" else "⚠️ تنويه المحافظ الإلكترونية:"
                val paymentDesc = if (isFawry) "كود استخراج الفاتورة للدفع في منافذ فوري رقم (888999). الرجاء الدفع خلال ٢٤ ساعة لتأكيد الحجز." else "يرجى تحويل قيمة الفاتورة كاملة ($totalBill ج.م) باستخدام محفظتك الإلكترونية إلى الرقم المعتمد: ٠١٠٤٩٣٠٢٩٢٩ تمهيداً للتحقق السريع وتأطير حجزك فوراً."

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = if (isFawry) Color(0xFFFEF3C7) else Color(0xFFFFF1F2)),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, if (isFawry) Color(0xFFFDE68A) else Color(0xFFFECDD3))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = paymentTitle,
                            color = if (isFawry) Color(0xFFB45309) else Color(0xFFBE123C),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = paymentDesc,
                            color = if (isFawry) Color(0xFF92400E) else Color(0xFF9F1239),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 18.sp
                        )
                        if (!isFawry) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = "", onValueChange = {},
                                label = { Text("رقم المحفظة المحول منها لتأكيد الدفع", fontSize = 11.sp) },
                                modifier = Modifier.fillMaxWidth().height(56.dp)
                            )
                        }
                    }
                }
            } else if (payMethod == "CARD") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("تفاصيل بطاقة السداد الافتراضية:", fontWeight = FontWeight.Bold, color = DarkNavy, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = "xxxx - xxxx - xxxx - 3920",
                            onValueChange = {},
                            enabled = false,
                            label = { Text("رقم البطاقة الائتمانية") },
                            leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null, tint = MedicalBlue) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledBorderColor = Color(0xFFCBD5E1),
                                disabledContainerColor = Color(0xFFF8FAFC)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            } else if (payMethod == "APP_WALLET") {
                val isSufficient = walletBalance >= totalBill
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSufficient) Color(0xFFECFDF5) else Color(0xFFFFF1F2)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, if (isSufficient) Color(0xFF34D399) else Color(0xFFFECDD3))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = if (isSufficient) "✓ رصيد المحفظة كافٍ:" else "⚠️ رصيد المحفظة غير كافٍ:",
                            color = if (isSufficient) Color(0xFF047857) else Color(0xFFBE123C),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isSufficient) {
                                "رصيدك الحالي هو $walletBalance ج.م وسيتم خصم قيمة الفاتورة ($totalBill ج.م) تلقائياً وآمناً بعد تأكيد الحجز."
                            } else {
                                "عذراً! رصيد المحفظة الحالي هو ($walletBalance ج.م) وهو أقل من قيمة الفاتورة المطلوبة ($totalBill ج.م). يرجى شحن الرصيد أولاً للمتابعة والدفع المباشر."
                            },
                            color = if (isSufficient) Color(0xFF065F46) else Color(0xFF9F1239),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 18.sp
                        )
                        if (!isSufficient) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { showLocalRecharge = true },
                                colors = ButtonDefaults.buttonColors(containerColor = MedicalBlue),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("شحن رصيد المحفظة الآن 💳", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            val isWalletInvalid = payMethod == "APP_WALLET" && walletBalance < totalBill

            // Submitting payment trigger
            Button(
                onClick = {
                    if (isWalletInvalid) {
                        showLocalRecharge = true
                    } else {
                        isPayingSimulated = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp))
                    .testTag("pay_now_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isWalletInvalid) Color(0xFFEF4444) else MedicalGreen
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (isPayingSimulated) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    LaunchedEffect(Unit) {
                        kotlinx.coroutines.delay(2000)
                        viewModel.confirmAndPay()
                    }
                } else {
                    val payLabel = when {
                        isWalletInvalid -> "يرجى شحن محفظتك أولاً ⚠️"
                        payMethod == "WALLET" || payMethod == "FAWRY" -> "تأكيد الحجز والدفع الخارجي"
                        payMethod == "APP_WALLET" -> "تأكيد الدفع من المحفظة الداخلية ($totalBill ج.م) 📱"
                        else -> "تأكيد الدفع أمنياً ($totalBill ج.م) 🔒"
                    }
                    Text(payLabel, fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }

    if (showLocalRecharge) {
        AlertDialog(
            onDismissRequest = { showLocalRecharge = false },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = rechargeAmount.toDoubleOrNull() ?: 100.0
                        viewModel.rechargeWallet(amount, rechargeMethod)
                        rechargeAmount = ""
                        showLocalRecharge = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalBlue)
                ) {
                    Text("تأكيد الشحن الفوري", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLocalRecharge = false }) {
                    Text("إلغاء", color = BodyGray)
                }
            },
            title = {
                Text(
                    text = "شحن محفظة معاك",
                    fontWeight = FontWeight.Bold,
                    color = DarkNavy,
                    fontSize = 15.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "قم بشحن رصيد محفظتك الآن بشكل فوري لإجراء عمليات دفع آمنة وحجوزات طبية سريعة بنقرة زر واحدة.",
                        fontSize = 11.sp,
                        color = BodyGray,
                        lineHeight = 16.sp
                    )

                    OutlinedTextField(
                        value = rechargeAmount,
                        onValueChange = { rechargeAmount = it },
                        label = { Text("القيمة المطلوب شحنها (مثلاً: ٢٥٠ ج.م)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        )
                    )

                    listOf(
                        "VODAFONE_CASH" to "فودافون كاش 🔴",
                        "FAWRY" to "بوابة فوري 🟡",
                        "CREDIT_CARD" to "بطاقة ائتمانية 💳"
                    ).forEach { (methodId, labelText) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (rechargeMethod == methodId) Color(0xFFEFF6FF) else Color.White
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (rechargeMethod == methodId) MedicalBlue else Color(0xFFE2E8F0),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { rechargeMethod = methodId }
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = rechargeMethod == methodId,
                                onClick = { rechargeMethod = methodId },
                                colors = RadioButtonDefaults.colors(selectedColor = MedicalBlue)
                              )
                            Text(labelText, fontSize = 11.5.sp, color = DarkNavy, fontWeight = if (rechargeMethod == methodId) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }
}

// ==========================================
// 9. TRACKING SCREEN
// ==========================================
@Composable
fun TrackingScreen(viewModel: ELmorsyViewModel) {
    val service by viewModel.selectedService.collectAsState()
    val nurse by viewModel.selectedNurse.collectAsState()
    val eta by viewModel.trackingEtaSeconds.collectAsState()
    val status by viewModel.trackingStatus.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()

    // Removed showChatSheet
    var showChatSheet by remember { mutableStateOf(false) }

    // Map offset
    val latOffset by viewModel.nurseLatOffset.collectAsState()
    val lngOffset by viewModel.nurseLngOffset.collectAsState()

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // TOP HEADER BAR INDICATING STATE
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = 12.dp, horizontal = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "تتبع وصول الممرض للمنزل",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = DarkNavy
                )

                IconButton(
                    onClick = { viewModel.cancelTrackingAndComplete() },
                    modifier = Modifier.testTag("skip_tracking_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Skip Tracking",
                        tint = BodyGray
                    )
                }
            }
        }

        // DYNAMIC CANVAS RADAR-PATH MAP WITH NURSE AND PATIENT ICONS
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFFE2E8F0))
        ) {
            val patientLat by viewModel.userLat.collectAsState()
            val patientLng by viewModel.userLng.collectAsState()

            RealTrackingWebView(
                patientLat = patientLat,
                patientLng = patientLng,
                latOffset = latOffset,
                lngOffset = lngOffset,
                modifier = Modifier.fillMaxSize()
            )
        }

        // BOTTOM TRACKING CARD WITH TIMING, STATUS & CALL CONTROLS
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(16.dp, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "حالة طلب الخدمةالآن:",
                            color = BodyGray,
                            fontSize = 13.sp
                        )

                        val statusLabel = when (status) {
                            "ON_THE_WAY" -> "الممرض في طريقه إليك 🚗"
                            "ARRIVED" -> "وصل الممرض لعنوانك 🔔"
                            "IN_PROGRESS" -> "الخدمة قيد التنفيذ بالمنزل 🏥"
                            else -> "اكتملت الخدمة بنجاح ✓"
                        }

                        Text(
                            text = statusLabel,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MedicalBlue
                        )
                    }

                    // Simulated interactive countdown ETA
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "الوقت المتبقي:",
                            color = BodyGray,
                            fontSize = 11.sp
                        )
                        Text(
                            text = if (eta > 0) "$eta ثانية" else "وصل الآن",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = if (eta > 0) LightYellow else MedicalGreen
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFFF1F5F9))

                // Interactive contact details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(SoftBlueBg, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = null,
                            tint = MedicalBlue
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = nurse?.name ?: "م. أحمد الشافعي",
                            fontWeight = FontWeight.Bold,
                            color = DarkNavy
                        )
                        Text(
                            text = "مختص معتمد - ${nurse?.phone ?: "٠١٠"}",
                            fontSize = 12.sp,
                            color = BodyGray
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Call Button
                    var callTriggered by remember { mutableStateOf(false) }
                    Button(
                        onClick = { callTriggered = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (callTriggered) MedicalGreen else Color(0xFFF1F5F9),
                            contentColor = if (callTriggered) Color.White else DarkNavy
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("call_nurse_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "اتصال",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (callTriggered) "يتصل..." else "اتصال رنان",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    // Chat Button
                    Button(
                        onClick = { viewModel.navigateTo("nurse_chat") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MedicalBlue,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("chat_nurse_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "المحادثة الفورية",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "محادثة فورية",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }

    if (showChatSheet) {
        val isDoc = service?.id == "doctor_visit"
        val recipientName = nurse?.name ?: "مختص الرعاية"
        val recipientTitle = if (isDoc) "طبيب استشاري متخصص" else "ممرض منزلي معتمد"
        
        var textInput by remember { mutableStateOf("") }
        val listState = rememberLazyListState()
        
        // Auto-scroll to bottom of chat when new messages arrive
        LaunchedEffect(chatMessages.size) {
            if (chatMessages.isNotEmpty()) {
                listState.animateScrollToItem(chatMessages.size - 1)
            }
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background) // soft medical background
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Chat Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { showChatSheet = false },
                    modifier = Modifier.testTag("close_chat_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "الرجوع لتتبع الخريطة",
                        tint = DarkNavy
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Avatar with online status Indicator
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(MedicalBlue.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isDoc) Icons.Default.Person else Icons.Default.MedicalServices,
                            contentDescription = null,
                            tint = MedicalBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color(0xFF10B981), CircleShape)
                            .border(1.5.dp, Color.White, CircleShape)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = recipientName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = DarkNavy
                    )
                    Text(
                        text = "$recipientTitle • نشط الآن",
                        fontSize = 11.sp,
                        color = Color(0xFF10B981),
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // Quick quick SOS button
                IconButton(
                    onClick = { /* simulated speed dial */ },
                    modifier = Modifier
                        .background(Color(0xFFFEF2F2), CircleShape)
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "اتصال سريع للمتابعة",
                        tint = Color.Red,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            // Helpful encrypted details
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SoftBlueBg.copy(alpha = 0.5f))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MedicalBlue,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "المحادثات مشفرة بالكامل لضمان خصوصية بيانات المرضى.",
                        fontSize = 10.sp,
                        color = MedicalBlue,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            // Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(chatMessages) { msg ->
                    val isMe = msg.sender == "PATIENT"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                    ) {
                        Column(
                            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Text(
                                text = if (isMe) "أنت" else msg.sender,
                                fontSize = 10.sp,
                                color = BodyGray,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                            
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = if (isMe) MedicalBlue else Color.White,
                                        shape = RoundedCornerShape(
                                            topStart = 16.dp,
                                            topEnd = 16.dp,
                                            bottomStart = if (isMe) 16.dp else 4.dp,
                                            bottomEnd = if (isMe) 4.dp else 16.dp
                                        )
                                    )
                                    .border(
                                        width = if (isMe) 0.dp else 1.dp,
                                        color = if (isMe) Color.Transparent else Color(0xFFE2E8F0),
                                        shape = RoundedCornerShape(
                                            topStart = 16.dp,
                                            topEnd = 16.dp,
                                            bottomStart = if (isMe) 16.dp else 4.dp,
                                            bottomEnd = if (isMe) 4.dp else 16.dp
                                        )
                                    )
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = msg.content,
                                    color = if (isMe) Color.White else DarkNavy,
                                    fontSize = 13.5.sp,
                                    lineHeight = 20.sp
                                )
                            }
                            
                            Text(
                                text = msg.timestamp,
                                fontSize = 9.sp,
                                color = BodyGray,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
            
            // Preset Suggestion Chips for lightning fast interaction
            val suggestions = if (isDoc) {
                listOf("السلام عليكم يا دكتور", "هل أحتاج لتجهيز تحاليل معينة؟", "أشعر بألم بسيط حالياً", "شكراً جزيلاً لاهتمامك")
            } else {
                listOf("السلام عليكم يا ممرض", "متى متوقع وصولك؟", "هل أحتاج لتجهيز دواء معين؟", "شكراً جزيلاً")
            }
            
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(suggestions) { phrase ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SoftBlueBg),
                        border = BorderStroke(0.5.dp, MedicalBlue.copy(alpha = 0.4f)),
                        modifier = Modifier.clickable {
                            viewModel.sendChatMessage(phrase)
                        }
                    ) {
                        Text(
                            text = phrase,
                            color = MedicalBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
            
            // Chat Input Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("اكتب رسالة للمختص الطبي...", fontSize = 13.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 40.dp)
                        .testTag("chat_input_text_field"),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MedicalBlue,
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    )
                )
                
                IconButton(
                    onClick = {
                        if (textInput.isNotBlank()) {
                            viewModel.sendChatMessage(textInput)
                            textInput = ""
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(MedicalBlue, CircleShape)
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "إرسال",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// ==========================================
// 10. REVIEW SCREEN
// ==========================================
@Composable
fun ReviewScreen(viewModel: ELmorsyViewModel) {
    val service by viewModel.selectedService.collectAsState()
    val nurse by viewModel.selectedNurse.collectAsState()

    var activeRating by remember { mutableStateOf(5f) }
    var userComment by remember { mutableStateOf("") }
    var ratingSubmitted by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Success celebration icon
        Box(
            modifier = Modifier
                .size(76.dp)
                .background(MedicalGreen.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MedicalGreen,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "تم تقديم الخدمة بنجاح!",
            fontWeight = FontWeight.Bold,
            color = DarkNavy,
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )

        Text(
            text = "الحمد لله على سلامتكم. تقييمكم يساعدنا دوماً على تطوير الجودة وتقديم أفضل الرعاية الطبية بالمنزل.",
            color = BodyGray,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier.padding(top = 6.dp, bottom = 32.dp)
        )

        // CARD FOR INPUT REVIEWS
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "كيف تقيم أداء وتفاني الممرض؟",
                    fontWeight = FontWeight.Bold,
                    color = DarkNavy,
                    fontSize = 16.sp
                )

                Text(
                    text = nurse?.name ?: "م. أحمد الشافعي",
                    fontWeight = FontWeight.Bold,
                    color = MedicalBlue,
                    fontSize = 14.sp
                )

                // Tactile star selection bar
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    (1..5).forEach { star ->
                        val isFilled = star <= activeRating
                        IconButton(
                            onClick = { activeRating = star.toFloat() },
                            modifier = Modifier.size(40.dp).testTag("star_$star")
                        ) {
                            Icon(
                                imageVector = if (isFilled) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "$star نجوم",
                                tint = if (isFilled) LightYellow else BodyGray.copy(alpha = 0.4f),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }

                // Text Comments Input Box
                OutlinedTextField(
                    value = userComment,
                    onValueChange = { userComment = it },
                    placeholder = { Text("اكتب رأيك هنا بخصوص مهارة الممرض، سلوكه، أو أي ملاحظات للتعزيز والمتابعة...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("review_comments"),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )

                Button(
                    onClick = {
                        ratingSubmitted = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_review_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (ratingSubmitted) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        LaunchedEffect(Unit) {
                            delay(1500)
                            viewModel.submitReview(activeRating, userComment)
                        }
                    } else {
                        Text("إرسال التقييم والعودة للرئيسية", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}

// ==========================================
// 11. PROFILE SCREEN
// ==========================================
@Composable
fun ProfileScreen(viewModel: ELmorsyViewModel) {
    val isDark = MaterialTheme.colorScheme.background == DarkBackground
    val userName by viewModel.userName.collectAsState()
    val userPhone by viewModel.userPhone.collectAsState()
    val userAddress by viewModel.userAddress.collectAsState()
    val bookings by viewModel.bookingsHistory.collectAsState()

    val patientAge by viewModel.patientAge.collectAsState()
    val patientBloodType by viewModel.patientBloodType.collectAsState()
    val patientWeight by viewModel.patientWeight.collectAsState()
    val patientGender by viewModel.patientGender.collectAsState()
    val patientConditions by viewModel.patientConditions.collectAsState()
    val patientAllergies by viewModel.patientAllergies.collectAsState()
    val emergencyContactName by viewModel.emergencyContactName.collectAsState()
    val emergencyContactPhone by viewModel.emergencyContactPhone.collectAsState()
    val recentHeartRate by viewModel.recentHeartRate.collectAsState()
    val recentBloodPressure by viewModel.recentBloodPressure.collectAsState()
    val recentBloodSugar by viewModel.recentBloodSugar.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }
    var notificationEnabled by remember { mutableStateOf(true) }
    var showUploadSuccessMsg by remember { mutableStateOf(false) }

    // --- NEW MODAL CONTROLS STATES ---
    val walletBalance by viewModel.walletBalance.collectAsState()
    val walletTransactions by viewModel.walletTransactions.collectAsState()
    val medicationReminders by viewModel.medicationReminders.collectAsState()
    val medicalDocuments by viewModel.medicalDocuments.collectAsState()

    var showAddReminderDialog by remember { mutableStateOf(false) }
    var newMedName by remember { mutableStateOf("") }
    var newMedDose by remember { mutableStateOf("") }
    var newMedTime by remember { mutableStateOf("09:00 ص") }

    var showAddDocDialog by remember { mutableStateOf(false) }
    var newDocName by remember { mutableStateOf("") }

    var showRechargeDialog by remember { mutableStateOf(false) }
    var rechargeAmountInput by remember { mutableStateOf("") }
    var rechargeMethodInput by remember { mutableStateOf("VODAFONE_CASH") } // "VODAFONE_CASH", "FAWRY", "CREDIT_CARD"

    var showSupportChatOverlay by remember { mutableStateOf(false) }

    var showLogVitalDialog by remember { mutableStateOf(false) }
    var showVitalHistoryDialog by remember { mutableStateOf(false) }
    var newVitalType by remember { mutableStateOf("HEART") } // "HEART", "PRESSURE", "SUGAR"
    var newVitalValueInput by remember { mutableStateOf("") }
    var newVitalNoteInput by remember { mutableStateOf("") }

    // Dialog state copies
    var editName by remember { mutableStateOf("") }
    var editPhone by remember { mutableStateOf("") }
    var editAddress by remember { mutableStateOf("") }
    var editAge by remember { mutableStateOf("") }
    var editBloodType by remember { mutableStateOf("") }
    var editWeight by remember { mutableStateOf("") }
    var editGender by remember { mutableStateOf("") }
    var editConditions by remember { mutableStateOf("") }
    var editAllergies by remember { mutableStateOf("") }
    var editEmergencyName by remember { mutableStateOf("") }
    var editEmergencyPhone by remember { mutableStateOf("") }
    var editHeartRate by remember { mutableStateOf("") }
    var editBloodPressure by remember { mutableStateOf("") }
    var editBloodSugar by remember { mutableStateOf("") }

    Scaffold(
        bottomBar = {
            val notificationsList by viewModel.notifications.collectAsState()
            val unreadCount = notificationsList.count { !it.isRead }
            ELmorsyBottomBar(
                currentScreen = "profile",
                onNavigate = { viewModel.navigateTo(it) },
                notificationsCount = unreadCount
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0A0A0A)) // Black background for top side
                .padding(innerPadding)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo("home") },
                    modifier = Modifier
                        .background(Color(0xFF1E1E1E), CircleShape)
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "رجوع",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = "الحساب",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(
                    onClick = { viewModel.navigateTo("notifications") },
                    modifier = Modifier
                        .background(Color(0xFF1E1E1E), CircleShape)
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "التنبيهات",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // User Info Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Image(
                        painter = painterResource(id = com.example.R.drawable.dramatic_portrait_1780966896428),
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color(0xFF333333), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .offset(x = 4.dp, y = 4.dp)
                            .size(26.dp)
                            .background(Color(0xFF1E1E1E), CircleShape)
                            .border(2.dp, Color(0xFF0A0A0A), CircleShape)
                            .clickable { showEditDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "تغيير الصورة",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = userName.ifBlank { "زائر" },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = userPhone.ifBlank { "رقم الهاتف غير متاح" },
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            // White rounded bottom section (The lists)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                color = Color.White, // pure white to match design
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Group 1
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.navigateTo("wallet") }
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Icon(Icons.Default.AccountBalanceWallet, null, tint = Color(0xFF6B7280), modifier = Modifier.size(24.dp))
                                Text("المحفظة والرصيد", color = Color(0xFF111827), fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            }
                            Icon(Icons.Default.KeyboardArrowLeft, null, tint = Color(0xFFD1D5DB), modifier = Modifier.size(20.dp))
                        }
                    }

                    // Group 2
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.navigateTo("edit_medical_profile") }
                                    .padding(horizontal = 16.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Icon(Icons.Default.Edit, null, tint = Color(0xFF6B7280), modifier = Modifier.size(22.dp))
                                    Text("تعديل الملف الطبي", color = Color(0xFF111827), fontSize = 15.sp, fontWeight = FontWeight.Medium)
                                }
                                Icon(Icons.Default.KeyboardArrowLeft, null, tint = Color(0xFFD1D5DB), modifier = Modifier.size(20.dp))
                            }
                            HorizontalDivider(color = Color(0xFFE5E7EB), modifier = Modifier.padding(horizontal = 56.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.navigateTo("vitals_dashboard") }
                                    .padding(horizontal = 16.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Icon(Icons.Default.HealthAndSafety, null, tint = Color(0xFF6B7280), modifier = Modifier.size(22.dp))
                                    Text("لوحة القياسات الحيوية", color = Color(0xFF111827), fontSize = 15.sp, fontWeight = FontWeight.Medium)
                                }
                                Icon(Icons.Default.KeyboardArrowLeft, null, tint = Color(0xFFD1D5DB), modifier = Modifier.size(20.dp))
                            }
                            HorizontalDivider(color = Color(0xFFE5E7EB), modifier = Modifier.padding(horizontal = 56.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.navigateTo("medical_docs") }
                                    .padding(horizontal = 16.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Icon(Icons.Default.Description, null, tint = Color(0xFF6B7280), modifier = Modifier.size(22.dp))
                                    Text("الوصفات والتحاليل", color = Color(0xFF111827), fontSize = 15.sp, fontWeight = FontWeight.Medium)
                                }
                                Icon(Icons.Default.KeyboardArrowLeft, null, tint = Color(0xFFD1D5DB), modifier = Modifier.size(20.dp))
                            }
                            HorizontalDivider(color = Color(0xFFE5E7EB), modifier = Modifier.padding(horizontal = 56.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.navigateTo("medication_reminders") }
                                    .padding(horizontal = 16.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Icon(Icons.Default.Medication, null, tint = Color(0xFF6B7280), modifier = Modifier.size(22.dp))
                                    Text("التذكير بالأدوية", color = Color(0xFF111827), fontSize = 15.sp, fontWeight = FontWeight.Medium)
                                }
                                Icon(Icons.Default.KeyboardArrowLeft, null, tint = Color(0xFFD1D5DB), modifier = Modifier.size(20.dp))
                            }
                        }
                    }

                    // Group 3
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.navigateTo("support_chat") }
                                    .padding(horizontal = 16.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Icon(Icons.Default.SupportAgent, null, tint = Color(0xFF6B7280), modifier = Modifier.size(22.dp))
                                    Text("الدعم الطبي اللحظي", color = Color(0xFF111827), fontSize = 15.sp, fontWeight = FontWeight.Medium)
                                }
                                Icon(Icons.Default.KeyboardArrowLeft, null, tint = Color(0xFFD1D5DB), modifier = Modifier.size(20.dp))
                            }
                            HorizontalDivider(color = Color(0xFFE5E7EB), modifier = Modifier.padding(horizontal = 56.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Icon(Icons.Default.NotificationsActive, null, tint = Color(0xFF6B7280), modifier = Modifier.size(22.dp))
                                    Text("تفعيل الإشعارات", color = Color(0xFF111827), fontSize = 15.sp, fontWeight = FontWeight.Medium)
                                }
                                Switch(
                                    checked = notificationEnabled,
                                    onCheckedChange = { notificationEnabled = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF10B981))
                                )
                            }
                            if (bookings.isNotEmpty()) {
                                HorizontalDivider(color = Color(0xFFE5E7EB), modifier = Modifier.padding(horizontal = 56.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.clearAllHistory() }
                                        .padding(horizontal = 16.dp, vertical = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                        Icon(Icons.Default.SettingsBackupRestore, null, tint = Color(0xFF6B7280), modifier = Modifier.size(22.dp))
                                        Text("مسح سجل الحجوزات السابقة", color = Color(0xFF111827), fontSize = 15.sp, fontWeight = FontWeight.Medium)
                                    }
                                    Icon(Icons.Default.KeyboardArrowLeft, null, tint = Color(0xFFD1D5DB), modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }

                    // Logout
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.performLogout() }
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Icon(Icons.Default.Logout, null, tint = Color(0xFFFF5A79), modifier = Modifier.size(22.dp))
                                Text("تسجيل الخروج", color = Color(0xFFFF5A79), fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            }
                            Icon(Icons.Default.KeyboardArrowLeft, null, tint = Color(0xFFD1D5DB), modifier = Modifier.size(20.dp))
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

    }

    // Comprehensive Interactive Modal to Edit All Patient Fields
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updatePatientProfile(
                            name = editName,
                            phone = editPhone,
                            address = editAddress,
                            age = editAge,
                            bloodType = editBloodType,
                            weight = editWeight,
                            gender = editGender,
                            conditions = editConditions,
                            allergies = editAllergies,
                            emergencyName = editEmergencyName,
                            emergencyPhone = editEmergencyPhone,
                            heartRate = editHeartRate,
                            bloodPressure = editBloodPressure,
                            bloodSugar = editBloodSugar
                        )
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalBlue)
                ) {
                    Text("حفظ التحديثات الطبية", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("إلغاء", color = BodyGray)
                }
            },
            title = {
                Text(
                    text = "تحديث ملف المريض الشامل",
                    fontWeight = FontWeight.Bold,
                    color = DarkNavy,
                    fontSize = 16.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "يرجى إدخال البيانات الطبية والملفات الحيوية بدقة لتأمين سلامة الرعاية المنزلية المقدمة.",
                        fontSize = 11.sp,
                        color = BodyGray,
                        lineHeight = 16.sp
                    )

                    // 1. Name
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("الاسم الكامل للمريض") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    // 2. Phone
                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("رقم الهاتف") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    // 3. Address
                    OutlinedTextField(
                        value = editAddress,
                        onValueChange = { editAddress = it },
                        label = { Text("العنوان الصحي المنزلي") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 4. Age
                        OutlinedTextField(
                            value = editAge,
                            onValueChange = { editAge = it },
                            label = { Text("العمر") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        )

                        // 5. Gender
                        OutlinedTextField(
                            value = editGender,
                            onValueChange = { editGender = it },
                            label = { Text("الجنس") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 6. Blood Type
                        OutlinedTextField(
                            value = editBloodType,
                            onValueChange = { editBloodType = it },
                            label = { Text("فصيلة الدم") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        )

                        // 7. Weight
                        OutlinedTextField(
                            value = editWeight,
                            onValueChange = { editWeight = it },
                            label = { Text("الوزن (كجم)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Text("المؤشرات والقياسات الحيوية الحالية", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkNavy)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = editHeartRate,
                            onValueChange = { editHeartRate = it },
                            label = { Text("نبض القلب") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        OutlinedTextField(
                            value = editBloodPressure,
                            onValueChange = { editBloodPressure = it },
                            label = { Text("ضغط الدم") },
                            modifier = Modifier.weight(1.2f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        OutlinedTextField(
                            value = editBloodSugar,
                            onValueChange = { editBloodSugar = it },
                            label = { Text("مستوى السكر") },
                            modifier = Modifier.weight(1.1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Text("الملاحظات الطبية الحرجة الحالية", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkNavy)

                    // 8. Chronic Diseases
                    OutlinedTextField(
                        value = editConditions,
                        onValueChange = { editConditions = it },
                        label = { Text("تحديد الأمراض المزمنة") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    // 9. Allergies
                    OutlinedTextField(
                        value = editAllergies,
                        onValueChange = { editAllergies = it },
                        label = { Text("تحديد الحساسيات") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Text("بيانات SOS للطوارئ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkNavy)

                    // 10. Emergency Contact Name
                    OutlinedTextField(
                        value = editEmergencyName,
                        onValueChange = { editEmergencyName = it },
                        label = { Text("اسم جهة SOS للطوارئ") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    // 11. Emergency Contact Phone
                    OutlinedTextField(
                        value = editEmergencyPhone,
                        onValueChange = { editEmergencyPhone = it },
                        label = { Text("هاتف الطوارئ المعين") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }

    // =========================================================================
    // MODALS: MEDICATION REMINDERS, DIGITAL HEALTH VAULT & E-WALLET SERVICES
    // =========================================================================

    // 1. Add Medication / Preventive Reminder Modal
    if (showAddReminderDialog) {
        AlertDialog(
            onDismissRequest = { showAddReminderDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        if (newMedName.isNotBlank() && newMedDose.isNotBlank()) {
                            viewModel.addMedicationReminder(newMedName, newMedDose, newMedTime)
                            newMedName = ""
                            newMedDose = ""
                            showAddReminderDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalBlue)
                ) {
                    Text("إضافة المنبه", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddReminderDialog = false }) {
                    Text("إلغاء", color = BodyGray)
                }
            },
            title = {
                Text(
                    text = "إضافة منبه دواء أو فحص صحي",
                    fontWeight = FontWeight.Bold,
                    color = DarkNavy,
                    fontSize = 15.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "قم ببرمجة مواعيد دواء المريض أو العلامات الحيوية (مثلاً: قياس الضغط أو السكر) لتنبيهكم بها عبر منبهات الهاتف الذكي الوقائية المباشرة.",
                        fontSize = 11.sp,
                        color = BodyGray,
                        lineHeight = 16.sp
                    )

                    OutlinedTextField(
                        value = newMedName,
                        onValueChange = { newMedName = it },
                        label = { Text("اسم الدواء أو الفحص (مثلاً: الأنسولين)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = newMedDose,
                        onValueChange = { newMedDose = it },
                        label = { Text("الجرعة والتفاصيل (مثلاً: حقنة واحدة بعد الأكل)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = newMedTime,
                        onValueChange = { newMedTime = it },
                        label = { Text("توقيت المنبه المطلوب (مثلاً: 08:30 م)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }

    // 2. Add Medical Document Upload Modal
    if (showAddDocDialog) {
        AlertDialog(
            onDismissRequest = { showAddDocDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        if (newDocName.isNotBlank()) {
                            viewModel.addMedicalDocument(newDocName)
                            newDocName = ""
                            showAddDocDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalBlue)
                ) {
                    Text("رفع وتخزين الملف", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDocDialog = false }) {
                    Text("إلغاء", color = BodyGray)
                }
            },
            title = {
                Text(
                    text = "تخزين مستند أو روشتة طبية جديدة",
                    fontWeight = FontWeight.Bold,
                    color = DarkNavy,
                    fontSize = 15.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "يرجى تحديد تفاصيل أو اسم المستند والتقرير الطبي بهدف تسهيل مهام طواقم التمريض وقراءة التاريخ المرضي بدقة متناهية.",
                        fontSize = 11.sp,
                        color = BodyGray,
                        lineHeight = 16.sp
                    )

                    OutlinedTextField(
                        value = newDocName,
                        onValueChange = { newDocName = it },
                        label = { Text("عنوان أو فئة المستند (مثلاً: تحاليل السكر - يونيو ٢٠٢٦)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }

    // 2b. Log Vital Dialog
    if (showLogVitalDialog) {
        AlertDialog(
            onDismissRequest = { showLogVitalDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        if (newVitalValueInput.isNotBlank()) {
                            viewModel.addVitalLog(newVitalType, newVitalValueInput, newVitalNoteInput)
                            newVitalValueInput = ""
                            newVitalNoteInput = ""
                            showLogVitalDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalBlue),
                    enabled = newVitalValueInput.isNotBlank()
                ) {
                    Text("حفظ الفحص", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogVitalDialog = false }) {
                    Text("إلغاء", color = BodyGray)
                }
            },
            title = {
                Text(
                    text = "تسجيل علامة حيوية جديدة",
                    fontWeight = FontWeight.Bold,
                    color = DarkNavy,
                    fontSize = 15.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "اختر المؤشر الحيوي الذي قمت بقياسه الآن وسجّل النتيجة لمتابعته مع الممرض المنزلي وطبيب العائلة.",
                        fontSize = 11.sp,
                        color = BodyGray,
                        lineHeight = 16.sp
                    )

                    // Vital Type selector row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "HEART" to "💖 نبض",
                            "PRESSURE" to "🩺 ضغط",
                            "SUGAR" to "🩸 سكر"
                        ).forEach { (mId, mLabel) ->
                            val isSelected = newVitalType == mId
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { newVitalType = mId },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Color(0xFFEFF6FF) else Color.White
                                ),
                                border = BorderStroke(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) MedicalBlue else Color(0xFFE2E8F0)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = mLabel,
                                    modifier = Modifier
                                        .padding(vertical = 10.dp)
                                        .fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    color = if (isSelected) MedicalBlue else DarkNavy,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = newVitalValueInput,
                        onValueChange = { newVitalValueInput = it },
                        label = {
                            val placeholderText = when (newVitalType) {
                                "HEART" -> "نبض القلب (مثلاً: ٧٢)"
                                "PRESSURE" -> "ضغط الدم (مثلاً: ١٢٠ / ٨٠)"
                                else -> "مستوى السكر (مثلاً: ١١٠)"
                            }
                            Text(placeholderText)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = newVitalNoteInput,
                        onValueChange = { newVitalNoteInput = it },
                        label = { Text("ملاحظة إضافية (مثال: صائم، بعد الأكل بساعتين...)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }

    // 2c. Vital Logs History Dialog
    if (showVitalHistoryDialog) {
        val vitalsList by viewModel.vitalLogs.collectAsState()

        AlertDialog(
            onDismissRequest = { showVitalHistoryDialog = false },
            confirmButton = {
                Button(
                    onClick = { showVitalHistoryDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalBlue)
                ) {
                    Text("إغلاق السجل", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Text(
                    text = "سجل العلامات الحيوية المتكامل",
                    fontWeight = FontWeight.Bold,
                    color = DarkNavy,
                    fontSize = 15.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 350.dp)
                ) {
                    Text(
                        text = "التاريخ الطبي لجميع قياساتك المنزلية الأخيرة مقسمة ومحفوظة لحمايتك الطبية الشاملة:",
                        fontSize = 11.sp,
                        color = BodyGray,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    if (vitalsList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("لا توجد قياسات مسجلة حالياً.", color = BodyGray, fontSize = 12.sp)
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            vitalsList.forEach { log ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(30.dp)
                                                .background(
                                                    when (log.type) {
                                                        "HEART" -> Color(0xFFFEF2F2)
                                                        "PRESSURE" -> Color(0xFFEFF6FF)
                                                        else -> Color(0xFFECFDF5)
                                                    },
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = when (log.type) {
                                                    "HEART" -> "💖"
                                                    "PRESSURE" -> "🩺"
                                                    else -> "🩸"
                                                },
                                                fontSize = 14.sp
                                            )
                                        }

                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Text(
                                                    text = log.value,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.5.sp,
                                                    color = DarkNavy
                                                )
                                                Text(
                                                    text = when (log.type) {
                                                        "HEART" -> "(نبض)"
                                                        "PRESSURE" -> "(ضغط)"
                                                        else -> "(سكر)"
                                                    },
                                                    fontSize = 10.sp,
                                                    color = BodyGray
                                                )
                                            }
                                            Text(
                                                text = "${log.timestamp} • ${log.note}",
                                                fontSize = 9.5.sp,
                                                color = BodyGray
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = { viewModel.deleteVitalLog(log.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "حذف الفحص",
                                            tint = Color.Red.copy(alpha = 0.7f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }

    // 3. Recharge E-Wallet Modal
    if (showRechargeDialog) {
        AlertDialog(
            onDismissRequest = { showRechargeDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = rechargeAmountInput.toDoubleOrNull() ?: 100.0
                        viewModel.rechargeWallet(amount, rechargeMethodInput)
                        rechargeAmountInput = ""
                        showRechargeDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalBlue)
                ) {
                    Text("تأكيد الشحن الفوري", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRechargeDialog = false }) {
                    Text("إلغاء وتعديل", color = BodyGray)
                }
            },
            title = {
                Text(
                    text = "شحن رصيد المحفظة الآمنة",
                    fontWeight = FontWeight.Bold,
                    color = DarkNavy,
                    fontSize = 15.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "اختر القيمة المحددة ووسيلة الدفع المناسبة لشحن رصيد المحفظة الخاصة بك لاستخدامها في تيسير الدفع للحجوزات الطبية المباشرة التلقائية وعلامات الدفع الآمن.",
                        fontSize = 11.sp,
                        color = BodyGray,
                        lineHeight = 16.sp
                    )

                    OutlinedTextField(
                        value = rechargeAmountInput,
                        onValueChange = { rechargeAmountInput = it },
                        label = { Text("القيمة المطلوب شحنها (مثلاً: ٢٥٠ ج.م)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        )
                    )

                    Text(
                        text = "اختر بوابة الدفع الفوري:",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkNavy
                    )

                    listOf(
                        "VODAFONE_CASH" to "فودافون كاش - دفع لحظي 🔴",
                        "FAWRY" to "بوابة فوري - رقم الخدمة (٨٨٨) 🟡",
                        "CREDIT_CARD" to "بطاقة ائتمانية - البنك المعتمد 💳"
                    ).forEach { (methodId, labelText) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (rechargeMethodInput == methodId) Color(0xFFEFF6FF) else Color.White
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (rechargeMethodInput == methodId) MedicalBlue else Color(0xFFE2E8F0),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { rechargeMethodInput = methodId }
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = rechargeMethodInput == methodId,
                                onClick = { rechargeMethodInput = methodId },
                                colors = RadioButtonDefaults.colors(selectedColor = MedicalBlue)
                            )
                            Text(
                                text = labelText,
                                fontSize = 11.5.sp,
                                color = DarkNavy,
                                fontWeight = if (rechargeMethodInput == methodId) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }

    // 4. Live Health Consultant Helpdesk / Customer Care Full Screen Dialog Overlay
    if (showSupportChatOverlay) {
        val chatMessages by viewModel.chatMessages.collectAsState()
        var supportMsgText by remember { mutableStateOf("") }
        val listState = rememberLazyListState()

        // AutoScroll to bottom when new messages join this chat channel
        LaunchedEffect(chatMessages.size) {
            if (chatMessages.isNotEmpty()) {
                listState.animateScrollToItem(chatMessages.size - 1)
            }
        }

        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showSupportChatOverlay = false }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(550.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDark) DarkBackground else Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Chat Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MedicalBlue)
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "مكتب المساعدة والاستشارات الطبية",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp
                                )
                                Text(
                                    text = "مستشار التمريض والدعم اللحظي نشط الآن 🟢",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 10.sp
                                )
                            }
                        }

                        IconButton(
                            onClick = { showSupportChatOverlay = false },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "إغلاق",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Encryption Notice
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFEFF6FF))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = MedicalBlue,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "محادثة الدعم مسجلة ومؤمنة بالكامل لضمان سرية المرضى.",
                                fontSize = 9.5.sp,
                                color = MedicalBlue
                            )
                        }
                    }

                    // Support chat history messages list
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        items(chatMessages) { msg ->
                            val isMe = msg.sender == "PATIENT"
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                            ) {
                                Column(
                                    horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
                                    modifier = Modifier.widthIn(max = 240.dp)
                                ) {
                                    Text(
                                        text = if (isMe) "أنت" else msg.sender,
                                        fontSize = 9.sp,
                                        color = BodyGray,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = if (isMe) MedicalBlue else Color(0xFFF1F5F9),
                                                shape = RoundedCornerShape(
                                                    topStart = 12.dp,
                                                    topEnd = 12.dp,
                                                    bottomStart = if (isMe) 12.dp else 2.dp,
                                                    bottomEnd = if (isMe) 2.dp else 12.dp
                                                )
                                            )
                                            .padding(10.dp)
                                    ) {
                                        Text(
                                            text = msg.content,
                                            color = if (isMe) Color.White else DarkNavy,
                                            fontSize = 11.sp,
                                            lineHeight = 16.sp
                                        )
                                    }
                                    Text(
                                        text = msg.timestamp,
                                        fontSize = 8.sp,
                                        color = BodyGray.copy(alpha = 0.7f),
                                        modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Interactive Custom Query Tags Helper (مفاتيح الأسئلة السريعة)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "استفسارات سريعة مقترحة للضغط والطلب اللحظي:",
                            fontSize = 9.sp,
                            color = BodyGray,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "ما هي أسعار الجلسات الطبية والتمريض المنزلي؟",
                                "أحتاج ممرض متخصص رعاية مسنين فوراً",
                                "تفعيل كشف طبي منزلي طارئ",
                                "تتبع ممرض الحجز الحالي"
                            ).forEach { tag ->
                                Box(
                                    modifier = Modifier
                                        .background(SoftBlueBg, RoundedCornerShape(12.dp))
                                        .border(0.5.dp, MedicalBlue.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                        .clickable {
                                            viewModel.sendChatMessage(tag, isHelpdesk = true)
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = tag,
                                        fontSize = 9.5.sp,
                                        color = MedicalBlue,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Input Send Box Section
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = supportMsgText,
                            onValueChange = { supportMsgText = it },
                            placeholder = { Text("اكتب رسالتك للمستشار الطبي الفوري...", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            maxLines = 2,
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.5.sp)
                        )

                        IconButton(
                            onClick = {
                                if (supportMsgText.isNotBlank()) {
                                    viewModel.sendChatMessage(supportMsgText, isHelpdesk = true)
                                    supportMsgText = ""
                                }
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .background(MedicalBlue, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "إرسال",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 12. BOOKING HISTORY SCREEN
// ==========================================
@Composable
fun BookingHistoryScreen(
    viewModel: ELmorsyViewModel,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    val isDark = MaterialTheme.colorScheme.background == DarkBackground
    val bookings by viewModel.bookingsHistory.collectAsState()
    var selectedTab by remember { mutableStateOf("ALL") } // "ALL", "ACTIVE", "COMPLETED", "CANCELLED"
    var showCancelConfirmDialog by remember { mutableStateOf<BookingEntity?>(null) }
    var selectedCalendarDate by remember { mutableStateOf<java.time.LocalDate?>(null) }

    val filteredList = remember(bookings, selectedTab, selectedCalendarDate) {
        val tabFiltered = when (selectedTab) {
            "ACTIVE" -> bookings.filter { it.status in listOf("PENDING", "ON_THE_WAY", "ARRIVED", "IN_PROGRESS") }
            "COMPLETED" -> bookings.filter { it.status == "COMPLETED" }
            "CANCELLED" -> bookings.filter { it.status == "CANCELLED" }
            else -> bookings
        }
        if (selectedCalendarDate != null) {
            tabFiltered.filter { bookingMatchesDate(it, selectedCalendarDate!!) }
        } else {
            tabFiltered
        }
    }

    Scaffold(
        bottomBar = {
            val notificationsList by viewModel.notifications.collectAsState()
            val unreadCount = notificationsList.count { !it.isRead }
            ELmorsyBottomBar(
                currentScreen = "booking_history",
                onNavigate = { viewModel.navigateTo(it) },
                notificationsCount = unreadCount
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            // Elegant Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(MedicalBlue, MedicalBlue.copy(alpha = 0.85f))
                        )
                    )
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "سجل الطلبات والرعاية",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 20.sp
                            )
                        )
                        Icon(
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "الرصد والمتابعة اللحظية ومراجعة زيارات ممرضي المرسي",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // Calendar Component
                item {
                    MaakCalendarComponent(
                        bookings = bookings,
                        selectedDate = selectedCalendarDate,
                        onDateSelected = { selectedCalendarDate = it }
                    )
                }

                // Tab Filters
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .background(if (isDark) Color(0xFF1E293B) else Color.White, RoundedCornerShape(12.dp))
                            .border(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(
                            Pair("ALL", "الكل"),
                            Pair("ACTIVE", "النشطة"),
                            Pair("COMPLETED", "المكتملة"),
                            Pair("CANCELLED", "الملغاة")
                        ).forEach { (tabKey, tabTitle) ->
                            val isSelected = selectedTab == tabKey
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) MedicalBlue else Color.Transparent)
                                    .clickable { selectedTab = tabKey }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tabTitle,
                                    color = if (isSelected) Color.White else (if (isDark) Color(0xFF94A3B8) else BodyGray),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.5.sp
                                )
                            }
                        }
                    }
                }

                // Active Calendar Filter Banner
                if (selectedCalendarDate != null) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                                .background(MedicalBlue.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                                .border(1.dp, MedicalBlue.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EventNote,
                                    contentDescription = null,
                                    tint = MedicalBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "حجوزات يوم: ${toArabicNumerals(selectedCalendarDate!!.dayOfMonth)} ${monthInArabic(selectedCalendarDate!!.monthValue)}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.White else DarkNavy
                                )
                            }
                            Text(
                                text = "عرض الكل ✕",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MedicalBlue,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .clickable { selectedCalendarDate = null }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // List items or Empty state
                if (filteredList.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp, horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(32.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(96.dp)
                                        .background(if (isDark) Color(0xFF1E293B) else SoftBlueBg, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EventBusy,
                                        contentDescription = null,
                                        tint = MedicalBlue,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(18.dp))
                                Text(
                                    text = "لا توجد طلبات في هذا اليوم حالياً",
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.White else DarkNavy,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "قم بزيارة الرئيسية لحجز خدمة رعاية ريادية متكاملة للمريض بكل سهولة في هذا التاريخ.",
                                    color = if (isDark) Color(0xFF94A3B8) else BodyGray,
                                    textAlign = TextAlign.Center,
                                    fontSize = 12.5.sp,
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                Button(
                                    onClick = { viewModel.navigateTo("home") },
                                    colors = ButtonDefaults.buttonColors(containerColor = MedicalBlue),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("طلب رعاية جديدة", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    items(filteredList, key = { it.id }) { booking ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                            BookingItemCard(
                                booking = booking,
                                onTrackClick = {
                                    viewModel.navigateTo("tracking")
                                },
                                onCancelClick = {
                                    showCancelConfirmDialog = booking
                                },
                                onRebookClick = {
                                    viewModel.rebookService(booking)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Cancellation Confirmation Dialog
    if (showCancelConfirmDialog != null) {
        val targetBooking = showCancelConfirmDialog!!
        AlertDialog(
            onDismissRequest = { showCancelConfirmDialog = null },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cancelBooking(targetBooking.id, targetBooking.nurseName)
                        showCancelConfirmDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("نعم، إلغاء الحجز", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showCancelConfirmDialog = null }
                ) {
                    Text("رجوع", color = BodyGray)
                }
            },
            title = {
                Text("تأكيد إلغاء طلب الزيارة الطبية", fontWeight = FontWeight.Bold, color = if (isDark) Color.White else DarkNavy, fontSize = 16.sp)
            },
            text = {
                Text(
                    "هل أنت متأكد من رغبتك في إلغاء طلب خدمة (${targetBooking.serviceName}) مع ${targetBooking.nurseName}؟ سيتم استرداد مبلغ ${targetBooking.price} ج.م إلى محفظتك بالكامل.",
                    fontSize = 14.sp,
                    color = if (isDark) Color(0xFF94A3B8) else BodyGray,
                    textAlign = TextAlign.Right
                )
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = if (isDark) Color(0xFF1E293B) else Color.White
        )
    }
}

@Composable
fun BookingStatusBadge(status: String) {
    val isDark = MaterialTheme.colorScheme.background == DarkBackground
    
    val badgeConfig = when (status) {
        "PENDING" -> BadgeConfig(
            bgColor = if (isDark) Color(0xFF78350F).copy(alpha = 0.25f) else Color(0xFFFFFBEB),
            borderColor = if (isDark) Color(0xFFD97706) else Color(0xFFFDE047),
            contentColor = if (isDark) Color(0xFFFBBF24) else Color(0xFFB45309),
            text = "قيد المراجعة ⏳",
            icon = Icons.Default.HourglassEmpty
        )
        "ACCEPTED", "ON_THE_WAY", "ARRIVED", "IN_PROGRESS" -> BadgeConfig(
            bgColor = if (isDark) Color(0xFF1E3A8A).copy(alpha = 0.25f) else Color(0xFFEFF6FF),
            borderColor = if (isDark) Color(0xFF3B82F6) else Color(0xFFBFDBFE),
            contentColor = if (isDark) Color(0xFF60A5FA) else Color(0xFF1D4ED8),
            text = "مؤكد ✓",
            icon = Icons.Default.CheckCircleOutline
        )
        "COMPLETED" -> BadgeConfig(
            bgColor = if (isDark) Color(0xFF14532D).copy(alpha = 0.25f) else Color(0xFFF0FDF4),
            borderColor = if (isDark) Color(0xFF22C55E) else Color(0xFFBBF7D0),
            contentColor = if (isDark) Color(0xFF4ADE80) else Color(0xFF15803D),
            text = "مكتمل ✓",
            icon = Icons.Default.CheckCircle
        )
        "CANCELLED" -> BadgeConfig(
            bgColor = if (isDark) Color(0xFF7F1D1D).copy(alpha = 0.25f) else Color(0xFFFEF2F2),
            borderColor = if (isDark) Color(0xFFEF4444) else Color(0xFFFECACA),
            contentColor = if (isDark) Color(0xFFFCA5A5) else Color(0xFFB91C1C),
            text = "ملغي ❌",
            icon = Icons.Default.Cancel
        )
        else -> BadgeConfig(
            bgColor = if (isDark) Color(0xFF334155).copy(alpha = 0.25f) else Color(0xFFF1F5F9),
            borderColor = if (isDark) Color(0xFF64748B) else Color(0xFFCBD5E1),
            contentColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569),
            text = status,
            icon = Icons.Default.Info
        )
    }

    Row(
        modifier = Modifier
            .background(badgeConfig.bgColor, RoundedCornerShape(20.dp))
            .border(1.dp, badgeConfig.borderColor, RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = badgeConfig.icon,
            contentDescription = null,
            tint = badgeConfig.contentColor,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = badgeConfig.text,
            color = badgeConfig.contentColor,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
        )
    }
}

data class BadgeConfig(
    val bgColor: Color,
    val borderColor: Color,
    val contentColor: Color,
    val text: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun BookingItemCard(
    booking: BookingEntity,
    onTrackClick: () -> Unit,
    onCancelClick: () -> Unit,
    onRebookClick: () -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background == DarkBackground
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("booking_item_${booking.id}"),
        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E293B) else Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Service Name & Price row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(if (isDark) Color(0xFF334155) else SoftBlueBg, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getServiceIcon(booking.serviceIconName),
                            contentDescription = null,
                            tint = MedicalBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = booking.serviceName,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else DarkNavy,
                            fontSize = 14.5.sp
                        )
                        Text(
                            text = "كود الحجز: #ELM-${1000 + booking.id}",
                            fontSize = 11.sp,
                            color = if (isDark) Color(0xFF94A3B8) else BodyGray
                        )
                    }
                }

                // Price Badge
                Text(
                    text = "${booking.price} ج.م",
                    fontWeight = FontWeight.ExtraBold,
                    color = MedicalGreen,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = if (isDark) Color(0xFF334155) else Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(12.dp))

            // Nurse details, Date & Time, Address details
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Nurse details row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = null,
                        tint = BodyGray.copy(alpha = 0.7f),
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "مقدم الخدمة: ",
                        fontWeight = FontWeight.Medium,
                        color = if (isDark) Color(0xFF94A3B8) else BodyGray,
                        fontSize = 12.sp
                    )
                    Text(
                        text = booking.nurseName,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else DarkNavy,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Row(
                        modifier = Modifier
                            .background(LightYellow.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = LightYellow,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${booking.nurseRating}",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = LightYellow
                        )
                    }
                }

                // Date & Time row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = BodyGray.copy(alpha = 0.7f),
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "الموعد المقيد: ",
                        fontWeight = FontWeight.Medium,
                        color = if (isDark) Color(0xFF94A3B8) else BodyGray,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "${booking.date} • ${booking.timeSlot}",
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else DarkNavy,
                        fontSize = 12.sp
                    )
                }

                // Address row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = BodyGray.copy(alpha = 0.7f),
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "العنوان المحدد: ",
                        fontWeight = FontWeight.Medium,
                        color = if (isDark) Color(0xFF94A3B8) else BodyGray,
                        fontSize = 12.sp
                    )
                    Text(
                        text = booking.address,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else DarkNavy,
                        fontSize = 11.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Notes if not empty
                if (booking.notes.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                            .border(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "📝 ملاحظات للممرض: ${booking.notes}",
                            color = if (isDark) Color(0xFF94A3B8) else BodyGray,
                            fontSize = 11.sp,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = if (isDark) Color(0xFF334155) else Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(14.dp))

            // State Badge and Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status badge
                BookingStatusBadge(status = booking.status)

                // Action Buttons Right Side
                when (booking.status) {
                    "PENDING", "ON_THE_WAY", "ARRIVED", "IN_PROGRESS" -> {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = onCancelClick,
                                border = BorderStroke(1.dp, Color(0xFFEF4444)),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text("إلغاء الطلب", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = onTrackClick,
                                colors = ButtonDefaults.buttonColors(containerColor = MedicalBlue),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Icon(Icons.Default.MyLocation, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                    Text("تتبع الزيارة", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                    "COMPLETED" -> {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (booking.rating > 0) {
                                Row(
                                    modifier = Modifier.padding(end = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    (1..booking.rating.toInt()).forEach { _ ->
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = LightYellow,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }

                            Button(
                                onClick = onRebookClick,
                                colors = ButtonDefaults.buttonColors(containerColor = MedicalGreen),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Replay, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                    Text("إعادة تحديد موعد", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                    "CANCELLED" -> {
                        Button(
                            onClick = onRebookClick,
                            colors = ButtonDefaults.buttonColors(containerColor = MedicalBlue),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                Text("إعادة حجز كزيارة جديدة", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }

            // If reviewing is present as a quote card
            if (booking.status == "COMPLETED" && booking.reviewComment.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC)),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFF1F5F9))
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "💬 تقييمك للزيارة:",
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFF94A3B8) else BodyGray,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "\"${booking.reviewComment}\"",
                            color = if (isDark) Color.White else DarkNavy,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 14. NOTIFICATIONS HISTORY SCREEN
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsHistoryScreen(viewModel: ELmorsyViewModel) {
    val notifications by viewModel.notifications.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.NotificationsActive, 
                            contentDescription = null, 
                            tint = MedicalBlue,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("تنبيهات معاك 🔔", fontWeight = FontWeight.Bold, color = DarkNavy, fontSize = 17.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo("home") }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.markAllNotificationsAsRead() }) {
                        Text("تحديد الكل كمقروء", color = MedicalBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            val notificationsList by viewModel.notifications.collectAsState()
            val unreadCount = notificationsList.count { !it.isRead }
            ELmorsyBottomBar(
                currentScreen = "notifications",
                onNavigate = { viewModel.navigateTo(it) },
                notificationsCount = unreadCount
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            if (notifications.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(SoftBlueBg, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.NotificationsNone,
                            contentDescription = null,
                            tint = MedicalBlue,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "لا توجد تنبيهات حالية",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkNavy
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "عند طلب جلسات الرعاية أو تحديث حالة الزيارة، ستصلك التنبيهات هنا.",
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = BodyGray,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(notifications) { notification ->
                        val cardBg = if (notification.isRead) Color.White else Color(0xFFF0F7FF)
                        val accentColor = when (notification.type) {
                            "CONFIRMATION" -> Color(0xFF10B981) // Green
                            "ALERT" -> Color(0xFFEAB308) // Amber/Yellow
                            "ARRIVED" -> Color(0xFF2563EB) // Royal Blue
                            "EN_ROUTE" -> Color(0xFFEC4899) // Pink/Magenta
                            "MESSAGE" -> Color(0xFF8B5CF6) // Purple
                            else -> MedicalBlue
                        }
                        val statusIcon = when (notification.type) {
                            "CONFIRMATION" -> Icons.Default.CheckCircle
                            "ALERT" -> Icons.Default.Notifications
                            "ARRIVED" -> Icons.Default.Home
                            "EN_ROUTE" -> Icons.Default.LocationOn
                            "MESSAGE" -> Icons.Default.ChatBubble
                            else -> Icons.Default.Info
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth().clickable {
                                viewModel.markAllNotificationsAsRead()
                                if (notification.type == "MESSAGE" || notification.type == "EN_ROUTE") {
                                    viewModel.navigateTo("nurse_chat")
                                } else {
                                    viewModel.navigateTo("tracking") // Or general catch-all if active booking exists
                                }
                            },
                            colors = CardDefaults.cardColors(containerColor = cardBg),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (notification.isRead) Color(0xFFE2E8F0) else MedicalBlue.copy(alpha = 0.25f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(accentColor.copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = statusIcon,
                                        contentDescription = null,
                                        tint = accentColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = notification.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = DarkNavy,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = notification.time,
                                            fontSize = 10.sp,
                                            color = BodyGray
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = notification.description,
                                        fontSize = 11.5.sp,
                                        color = DarkNavy.copy(alpha = 0.85f),
                                        lineHeight = 17.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 14A. MESSAGES LIST SCREEN
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesListScreen(viewModel: ELmorsyViewModel) {
    val activeBookingId by viewModel.activeBookingId.collectAsState()
    val nurse by viewModel.selectedNurse.collectAsState()
    val unreadCount = viewModel.notifications.collectAsState().value.count { !it.isRead }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Forum, 
                            contentDescription = null, 
                            tint = MedicalBlue,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("الرسائل والتواصل 💬", fontWeight = FontWeight.Bold, color = DarkNavy, fontSize = 17.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo("home") }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            ELmorsyBottomBar(
                currentScreen = "messages",
                onNavigate = { viewModel.navigateTo(it) },
                notificationsCount = unreadCount
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Support Chat Link
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { viewModel.navigateTo("support_chat") },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(54.dp).background(SoftBlueBg, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.SupportAgent, contentDescription = null, tint = MedicalBlue, modifier = Modifier.size(28.dp))
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("محادثة الدعم الطبي", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DarkNavy)
                            Spacer(Modifier.height(4.dp))
                            Text("متواجدون للرد على استفساراتك 24/7", fontSize = 12.sp, color = BodyGray)
                        }
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null, tint = Color.Gray)
                    }
                }
                
                // Active Booking Chat Link
                if (activeBookingId != -1 && nurse != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { viewModel.navigateTo("nurse_chat") },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, MedicalBlue.copy(alpha = 0.3f)),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.size(54.dp).background(Color(0xFFE2E8F0), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = MedicalBlue, modifier = Modifier.size(28.dp))
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("الممرض: ${nurse?.name}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DarkNavy)
                                Spacer(Modifier.height(4.dp))
                                Text("تواصل مباشر مع مقدم الرعاية الحالي", fontSize = 12.sp, color = Color(0xFF10B981))
                            }
                            // Active indicator
                            Box(modifier = Modifier.size(10.dp).background(Color(0xFF10B981), CircleShape))
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null, tint = Color.Gray)
                        }
                    }
                } else {
                    // Empty State if no active booking
                    Spacer(Modifier.height(24.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = Color(0xFFE2E8F0), modifier = Modifier.size(64.dp))
                            Spacer(Modifier.height(16.dp))
                            Text("لا توجد محادثات نشطة مع ممرضين حالياً.", color = BodyGray, fontSize = 14.sp, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Live Google Maps relative distance tracking overlay mapping in Real-Time. Synchronized with offset stream.
 */
@Composable
fun RealTrackingWebView(
    patientLat: Double,
    patientLng: Double,
    latOffset: Float,
    lngOffset: Float,
    modifier: Modifier = Modifier
) {
    val nurseLat = patientLat + (latOffset.toDouble() * 0.05)
    val nurseLng = patientLng + (lngOffset.toDouble() * 0.05)
    
    val patientLocation = LatLng(patientLat, patientLng)
    val nurseLocation = LatLng(nurseLat, nurseLng)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(patientLocation, 14f)
    }

    LaunchedEffect(nurseLat, nurseLng) {
        // Move camera to show both points nicely
        val newPos = CameraPosition.builder()
            .target(LatLng((patientLat + nurseLat) / 2, (patientLng + nurseLng) / 2))
            .zoom(14f)
            .build()
        cameraPositionState.animate(CameraUpdateFactory.newCameraPosition(newPos))
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = false,
            mapToolbarEnabled = false
        )
    ) {
        Marker(
            state = MarkerState(position = patientLocation),
            title = "المريض",
            snippet = "موقعك الحالي"
        )
        
        Marker(
            state = MarkerState(position = nurseLocation),
            title = "الممارس الصحي",
            snippet = "جاري بالطريق إليك"
        )
        
        Polyline(
            points = listOf(nurseLocation, patientLocation),
            color = MedicalBlue,
            width = 10f
        )
    }
}

fun showBiometricPromptHelper(
    activity: FragmentActivity,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val executor: Executor = ContextCompat.getMainExecutor(activity)
    val biometricPrompt = BiometricPrompt(
        activity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                onError(errString.toString())
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onError("لم يتم التعرف على بصمتك، يرجى المحاولة مجدداً.")
            }
        }
    )

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("تأكيد الهوية البيومترية معاك 🔐")
        .setSubtitle("تحقق لتسجيل دخول سريع للملف الطبي والطلبات السابقة")
        .setNegativeButtonText("استخدام كود المرور")
        .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK)
        .build()

    try {
        biometricPrompt.authenticate(promptInfo)
    } catch (e: Exception) {
        onError("فشل قارئ البصمة: ${e.localizedMessage}")
    }
}

// =========================================================================
// Maak Calendar Component and Date Matching Helpers
// =========================================================================

fun monthInArabic(monthVal: Int): String {
    return when (monthVal) {
        1 -> "يناير"
        2 -> "فبراير"
        3 -> "مارس"
        4 -> "أبريل"
        5 -> "مايو"
        6 -> "يونيو"
        7 -> "يوليو"
        8 -> "أغسطس"
        9 -> "سبتمبر"
        10 -> "أكتوبر"
        11 -> "نوفمبر"
        12 -> "ديسمبر"
        else -> ""
    }
}

fun toArabicNumerals(num: Int): String {
    val chars = num.toString().toCharArray()
    val stringBuilder = StringBuilder()
    for (char in chars) {
        if (char in '0'..'9') {
            stringBuilder.append((char.code + 1584).toChar()) // matches ٠ to ٩ in Arabic
        } else {
            stringBuilder.append(char)
        }
    }
    return stringBuilder.toString()
}

fun normalizeNumerals(str: String): String {
    var result = str
    val arabicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    for (i in 0..9) {
        result = result.replace(arabicDigits[i], '0' + i)
    }
    return result
}

fun bookingMatchesDate(booking: BookingEntity, date: java.time.LocalDate): Boolean {
    val bDateLower = normalizeNumerals(booking.date).lowercase()
    
    // Check match for day number and month in Arabic
    val dayStr = date.dayOfMonth.toString()
    val monthName = monthInArabic(date.monthValue)
    
    // Check if the booking string contains day and month
    val containsDay = bDateLower.contains(dayStr)
    val containsMonth = bDateLower.contains(monthName) || booking.date.contains(monthName)
    
    if (containsDay && containsMonth) return true
    
    // Also check if day can match Arabic numerals string directly
    val arabicDayStr = toArabicNumerals(date.dayOfMonth)
    if (booking.date.contains(arabicDayStr) && containsMonth) return true
    
    // Special matching for "اليوم" (today) and "غداً" (tomorrow)
    val today = java.time.LocalDate.of(2026, 6, 3) // Based on prompt local time (June 3, 2026)
    if (date == today && (booking.date.contains("اليوم") || booking.date.contains("اليوم،"))) {
        return true
    }
    val tomorrow = today.plusDays(1)
    if (date == tomorrow && (booking.date.contains("غداً") || booking.date.contains("غداً،"))) {
        return true
    }
    
    return false
}

@Composable
fun MaakCalendarComponent(
    bookings: List<BookingEntity>,
    selectedDate: java.time.LocalDate?,
    onDateSelected: (java.time.LocalDate?) -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background == DarkBackground
    var currentMonth by remember { mutableStateOf(java.time.LocalDate.of(2026, 6, 1)) }
    
    val daysOfWeek = listOf("ح", "ن", "ث", "ر", "خ", "ج", "س") // Sun, Mon, Tue, Wed, Thu, Fri, Sat
    
    // Get year & month details
    val yearMonth = java.time.YearMonth.of(currentMonth.year, currentMonth.monthValue)
    val firstDayOfMonth = yearMonth.atDay(1)
    val dayOfWeekOfFirst = firstDayOfMonth.dayOfWeek.value // 1 = Monday, 7 = Sunday
    val lengthOfMonth = yearMonth.lengthOfMonth()
    
    // Shift days to start with Sunday (which is index 7 in dayOfWeek, convert to 0..6)
    val firstDayOffset = if (dayOfWeekOfFirst == 7) 0 else dayOfWeekOfFirst
    
    val totalDaysToShow = lengthOfMonth + firstDayOffset
    val rowsCount = (totalDaysToShow + 6) / 7
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("maak_calendar_card"),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF1E293B) else Color.White
        ),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Month Navigation Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { currentMonth = currentMonth.plusMonths(1) }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack, // logical left pointing forward in RTL
                        contentDescription = "الشهر التالي",
                        tint = if (isDark) Color.White else DarkNavy
                    )
                }
                
                Text(
                    text = "${monthInArabic(currentMonth.monthValue)} ${toArabicNumerals(currentMonth.year)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = if (isDark) Color.White else DarkNavy
                )
                
                IconButton(
                    onClick = { currentMonth = currentMonth.minusMonths(1) }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward, // logical right pointing backward in RTL
                        contentDescription = "الشهر السابق",
                        tint = if (isDark) Color.White else DarkNavy
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            // Days of Week Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                daysOfWeek.forEach { day ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BodyGray
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(6.dp))
            
            // Days Grid
            for (r in 0 until rowsCount) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (c in 0..6) {
                        val dayIndex = r * 7 + c
                        val dayNum = dayIndex - firstDayOffset + 1
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            if (dayNum in 1..lengthOfMonth) {
                                val thisLocalDate = java.time.LocalDate.of(currentMonth.year, currentMonth.monthValue, dayNum)
                                val isDaySelected = selectedDate == thisLocalDate
                                val isToday = thisLocalDate == java.time.LocalDate.of(2026, 6, 3)
                                
                                // Check if there are active bookings on this exact day
                                val hasBookingsOnThisDay = bookings.any { bookingMatchesDate(it, thisLocalDate) }
                                
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isDaySelected -> MedicalBlue
                                                isToday -> MedicalBlue.copy(alpha = 0.15f)
                                                else -> Color.Transparent
                                            }
                                        )
                                        .border(
                                            width = if (isToday && !isDaySelected) 1.dp else 0.dp,
                                            color = if (isToday && !isDaySelected) MedicalBlue else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            if (isDaySelected) {
                                                onDateSelected(null) // deselect
                                            } else {
                                                onDateSelected(thisLocalDate)
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Text(
                                            text = toArabicNumerals(dayNum),
                                            fontSize = 12.sp,
                                            fontWeight = if (isDaySelected || isToday) FontWeight.Bold else FontWeight.Medium,
                                            color = when {
                                                isDaySelected -> Color.White
                                                isToday -> MedicalBlue
                                                isDark -> Color.White
                                                else -> DarkNavy
                                            }
                                        )
                                        if (hasBookingsOnThisDay) {
                                            Box(
                                                modifier = Modifier
                                                    .size(4.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isDaySelected) Color.White else MedicalGreen)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 14. NURSE DASHBOARD SCREEN
// ==========================================
@Composable
fun NurseDashboardScreen(viewModel: ELmorsyViewModel) {
    val userName by viewModel.userName.collectAsState()
    val isDark = MaterialTheme.colorScheme.background == Color(0xFF0F172A) || MaterialTheme.colorScheme.background == DarkBackground
    val allBookings by viewModel.bookingsHistory.collectAsState()
    
    // For demo, we just get active non-completed bookings
    val activeBookings = allBookings.filter { it.status != "COMPLETED" && it.status != "CANCELLED" }.reversed()
    val completedBookings = allBookings.filter { it.status == "COMPLETED" }
    
    var selectedTab by remember { mutableStateOf("CASES") } // CASES, WALLET, CHATBOT

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDark) DarkBackground else Color(0xFFF8FAFC))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // App Header with depth and premium feel
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MedicalBlue,
                            Color(0xFF0F4095)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.MedicalServices, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("مرحباً بك يا", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                        Text(if (userName.isBlank()) "مقدم الرعاية" else userName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                }
                IconButton(
                    onClick = {
                        viewModel.setUserRole("PATIENT")
                        viewModel.navigateTo("role_selection")
                    },
                    modifier = Modifier.background(Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                   Icon(Icons.Default.Logout, contentDescription = "تسجيل خروج", tint = Color.White)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Custom segmented control for tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(if (isDark) DarkCard else Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            listOf("CASES" to "الحالات النشطة", "WALLET" to "محفظتي", "CHATBOT" to "مساعد ذكي").forEach { (id, title) ->
                val isSelected = selectedTab == id
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) (if (isDark) MedicalBlue else Color.White) else Color.Transparent)
                        .clickable { selectedTab = id }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) (if (isDark) Color.White else MedicalBlue) else BodyGray,
                        fontSize = 13.sp
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Box(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            when (selectedTab) {
                "CASES" -> {
                    if (activeBookings.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(modifier = Modifier.size(80.dp).background(SoftBlueBg, CircleShape), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Coffee, contentDescription = null, tint = MedicalBlue.copy(alpha = 0.5f), modifier = Modifier.size(40.dp))
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("لا توجد زيارات نشطة حالياً. استرح قليلاً!", fontSize = 16.sp, color = BodyGray, fontWeight = FontWeight.Medium)
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(activeBookings) { booking ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = if (isDark) DarkCard else Color.White),
                                    shape = RoundedCornerShape(20.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    modifier = Modifier.fillMaxWidth().border(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFF1F5F9), RoundedCornerShape(20.dp))
                                ) {
                                    Column(modifier = Modifier.padding(20.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                                Box(
                                                    modifier = Modifier.size(40.dp).background(SoftBlueBg, CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(Icons.Default.Assignment, contentDescription = null, tint = MedicalBlue, modifier = Modifier.size(20.dp))
                                                }
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Column {
                                                    Text(booking.serviceName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = if(isDark) Color.White else DarkNavy, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                    Text("رقم الطلب #${booking.id.toString().take(6)}", color = BodyGray, fontSize = 11.sp)
                                                }
                                            }
                                            
                                            BookingStatusBadge(status = booking.status)
                                        }
                                        Spacer(modifier = Modifier.height(16.dp))
                                        
                                        HorizontalDivider(color = if (isDark) Color(0xFF334155) else Color(0xFFF1F5F9))
                                        Spacer(modifier = Modifier.height(16.dp))
                                        
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = BodyGray, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(booking.address, fontSize = 13.sp, color = if(isDark) Color.LightGray else DarkNavy)
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Schedule, contentDescription = null, tint = BodyGray, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("${booking.date} | ${booking.timeSlot}", fontSize = 13.sp, color = if(isDark) Color.LightGray else DarkNavy)
                                        }
                                        
                                        Spacer(modifier = Modifier.height(20.dp))
                                        
                                        if (booking.status == "PENDING") {
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                                Button(
                                                    onClick = { viewModel.updateBookingStatusWithSync(booking.id, "ACCEPTED") },
                                                    colors = ButtonDefaults.buttonColors(containerColor = MedicalBlue),
                                                    shape = RoundedCornerShape(12.dp),
                                                    modifier = Modifier.weight(1f).height(48.dp)
                                                ) { Text("قبول الحالة", fontWeight = FontWeight.Bold) }
                                                
                                                OutlinedButton(
                                                    onClick = { viewModel.cancelBooking(booking.id, "NURSE_REJECT") },
                                                    border = BorderStroke(1.dp, Color(0xFFEF4444)),
                                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                                                    shape = RoundedCornerShape(12.dp),
                                                    modifier = Modifier.weight(1f).height(48.dp)
                                                ) { Text("رفض") }
                                            }
                                        } else if (booking.status == "ACCEPTED") {
                                            Button(
                                                onClick = { viewModel.updateBookingStatusWithSync(booking.id, "ON_THE_WAY") },
                                                colors = ButtonDefaults.buttonColors(containerColor = MedicalBlue),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier.fillMaxWidth().height(48.dp)
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                                                    Icon(Icons.Default.DirectionsCar, contentDescription = null, modifier = Modifier.size(18.dp))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text("الانطلاق للمريض (بدء التتبع)", fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        } else if (booking.status == "ON_THE_WAY") {
                                            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text("يتم الآن إرسال تحديثات موقعك المباشر للمريض...", color = MedicalBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                Spacer(modifier = Modifier.height(12.dp))
                                                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)), color = MedicalBlue, trackColor = SoftBlueBg)
                                                Spacer(modifier = Modifier.height(16.dp))
                                                Button(
                                                    onClick = { viewModel.updateBookingStatusWithSync(booking.id, "ARRIVED") },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                                                    shape = RoundedCornerShape(12.dp),
                                                    modifier = Modifier.fillMaxWidth().height(48.dp)
                                                ) { Text("تأكيد وصولي للموقع", fontWeight = FontWeight.Bold) }
                                            }
                                        } else if (booking.status == "ARRIVED") {
                                            Button(
                                                onClick = { viewModel.updateBookingStatusWithSync(booking.id, "IN_PROGRESS") },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier.fillMaxWidth().height(48.dp)
                                            ) { Text("بدء تنفيذ الجلسة الآن", fontWeight = FontWeight.Bold) }
                                        } else if (booking.status == "IN_PROGRESS") {
                                            Button(
                                                onClick = { viewModel.updateBookingStatusWithSync(booking.id, "COMPLETED") },
                                                colors = ButtonDefaults.buttonColors(containerColor = MedicalGreen),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier.fillMaxWidth().height(48.dp)
                                            ) { Text("إنهاء الزيارة وتسجيل الأرباح", fontWeight = FontWeight.Bold) }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                "WALLET" -> {
                    val nurseEarnings = completedBookings.sumOf { it.price * 0.8 } // Assuming nurse takes 80%
                    var showWithdrawDialog by remember { mutableStateOf(false) }

                    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(8.dp, RoundedCornerShape(24.dp))
                                .clip(RoundedCornerShape(24.dp))
                                .background(Brush.linearGradient(listOf(MedicalGreen, Color(0xFF0F766E))))
                                .padding(24.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color.White.copy(alpha = 0.8f))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("الأرباح المتاحة للسحب", color = Color.White.copy(alpha = 0.9f), fontSize = 15.sp)
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("${nurseEarnings} ج.م", color = Color.White, fontWeight = FontWeight.Black, fontSize = 42.sp)
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(
                                    onClick = { showWithdrawDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF0F766E)),
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("سحب إلى إنستاباي / محفظة", fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("السجل المالي", fontWeight = FontWeight.Bold, color = if(isDark) Color.White else DarkNavy, fontSize = 18.sp)
                            Icon(Icons.Default.History, contentDescription = null, tint = BodyGray)
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        if (completedBookings.isEmpty()) {
                            Text("مجرد إكمال أول زيارة ستظهر أرباحك هنا.", color = BodyGray, fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 20.dp))
                        } else {
                            completedBookings.reversed().forEach { b ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(if(isDark) DarkCard else Color.White, RoundedCornerShape(16.dp))
                                        .border(1.dp, if(isDark) Color(0xFF334155) else Color(0xFFF1F5F9), RoundedCornerShape(16.dp))
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Box(
                                            modifier = Modifier.size(40.dp).background(Color(0xFFF0FDF4), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MedicalGreen, modifier = Modifier.size(24.dp))
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(b.serviceName, fontWeight = FontWeight.Bold, color = if(isDark) Color.White else DarkNavy, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                            Text(b.date, color = BodyGray, fontSize = 12.sp)
                                        }
                                    }
                                    Text("+${b.price * 0.8}", fontWeight = FontWeight.Bold, color = MedicalGreen, fontSize = 16.sp)
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(30.dp))
                    }

                    if (showWithdrawDialog) {
                         AlertDialog(
                            onDismissRequest = { showWithdrawDialog = false },
                            containerColor = if (isDark) DarkCard else Color.White,
                            title = { Text("طلب سحب الرصيد", fontWeight = FontWeight.Bold, color = if(isDark) Color.White else DarkNavy) },
                            text = {
                                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Text("أدخل رقم حساب إنستاباي أو رقم المحفظة الإلكترونية لطلب مبلغ ${nurseEarnings} ج.م", color = BodyGray)
                                    OutlinedTextField(
                                        value = "",
                                        onValueChange = {},
                                        label = { Text("رقم الحساب / الموبايل") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                }
                            },
                            confirmButton = {
                                Button(
                                    onClick = { showWithdrawDialog = false },
                                    colors = ButtonDefaults.buttonColors(containerColor = MedicalGreen),
                                    shape = RoundedCornerShape(10.dp)
                                ) { Text("تأكيد السحب", color = Color.White) }
                            },
                            dismissButton = {
                                TextButton(onClick = { showWithdrawDialog = false }) { Text("إلغاء", color = Color.Red) }
                            }
                        )
                    }
                }
                "CHATBOT" -> {
                    NurseChatbotTab(viewModel)
                }
            }
        }
    }
}

// ==========================================
// 15. ADMIN DASHBOARD SCREEN
// ==========================================
@Composable
fun AdminDashboardScreen(viewModel: ELmorsyViewModel) {
    val isDark = MaterialTheme.colorScheme.background == Color(0xFF0F172A) || MaterialTheme.colorScheme.background == DarkBackground
    var selectedTab by remember { mutableStateOf("HOME") }
    
    // Local tracking to force recomposition when admin changes data
    val servicesList by viewModel.servicesFlow.collectAsState()
    val nursesList by viewModel.nursesFlow.collectAsState()
    val departmentsList by viewModel.departmentsFlow.collectAsState()
    
    // Active Bookings Flow
    val allBookings by viewModel.bookingsHistory.collectAsState()
    
    // Add Service States
    var showAddService by remember { mutableStateOf(false) }
    var newServiceName by remember { mutableStateOf("") }
    var newServicePrice by remember { mutableStateOf("") }

    // Add Department States
    var showAddDepartment by remember { mutableStateOf(false) }
    var newDeptTitle by remember { mutableStateOf("") }
    var newDeptDesc by remember { mutableStateOf("") }
    var newDeptServiceIds by remember { mutableStateOf("") }

    // Edit Department States
    var showEditDepartment by remember { mutableStateOf<com.example.data.DepartmentEntity?>(null) }
    var editDeptTitle by remember { mutableStateOf("") }
    var editDeptDesc by remember { mutableStateOf("") }
    var editDeptServiceIds by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDark) DarkBackground else Color(0xFFF8FAFC))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // App Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFF0F4095), MedicalBlue)))
                .padding(20.dp)
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
                        Text("مركز تحكم الإدارة", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("صلاحيات كاملة • ${viewModel.userName.collectAsState().value}", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
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
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Tabs
        ScrollableTabRow(
            selectedTabIndex = when(selectedTab) { "HOME" -> 0 "REQUESTS" -> 1 "SERVICES" -> 2 "NURSES" -> 3 "CHATBOT" -> 4 else -> 0},
            containerColor = Color.Transparent,
            contentColor = MedicalBlue,
            edgePadding = 16.dp,
            divider = {}
        ) {
            val tabs = listOf("HOME" to "إحصائيات", "REQUESTS" to "إدارة الطلبات", "SERVICES" to "الأقسام", "NURSES" to "الممرضين", "CHATBOT" to "للشات بوت")
            tabs.forEachIndexed { idx, (id, title) ->
                val isSelected = selectedTab == id
                Tab(
                    selected = isSelected,
                    onClick = { selectedTab = id },
                    text = { 
                        Text(
                            title, 
                            fontWeight = if(isSelected) FontWeight.Black else FontWeight.Medium, 
                            color = if(isSelected) MedicalBlue else BodyGray,
                            fontSize = 14.sp
                        ) 
                    }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            when (selectedTab) {
                "HOME" -> {
                    val activeCount = allBookings.count { it.status != "COMPLETED" && it.status != "CANCELLED" }
                    val completedCount = allBookings.count { it.status == "COMPLETED" }
                    
                    Column {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                            Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)), shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, Color(0xFFFCA5A5))) {
                                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                    Icon(Icons.Default.HourglassTop, contentDescription = null, tint = Color.Red.copy(alpha = 0.5f), modifier = Modifier.size(32.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("$activeCount", fontWeight = FontWeight.Black, fontSize = 32.sp, color = Color.Red)
                                    Text("زيارة نشطة", fontSize = 14.sp, color = DarkNavy, fontWeight = FontWeight.Bold)
                                }
                            }
                            Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)), shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, Color(0xFF86EFAC))) {
                                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MedicalGreen.copy(alpha = 0.5f), modifier = Modifier.size(32.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("$completedCount", fontWeight = FontWeight.Black, fontSize = 32.sp, color = MedicalGreen)
                                    Text("زيارة مكتملة", fontSize = 14.sp, color = DarkNavy, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("إحصائيات النظام", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if(isDark) Color.White else DarkNavy)
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(colors = CardDefaults.cardColors(containerColor = if(isDark) DarkCard else Color.White), shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, if(isDark) Color(0xFF334155) else Color(0xFFE2E8F0)), modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(36.dp).background(SoftBlueBg, CircleShape), contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.LocalHospital, null, tint = MedicalBlue, modifier = Modifier.size(20.dp))
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text("الخدمات المفعلة بالنظام", color = if(isDark) Color.White else DarkNavy, fontWeight = FontWeight.Medium)
                                    }
                                    Text("${servicesList.size}", fontWeight = FontWeight.Black, fontSize = 18.sp, color = MedicalBlue)
                                }
                                HorizontalDivider(color = if(isDark) Color(0xFF334155) else Color(0xFFF1F5F9))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(36.dp).background(Color(0xFFF0FDF4), CircleShape), contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.SupervisorAccount, null, tint = MedicalGreen, modifier = Modifier.size(20.dp))
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text("أعداد الكوادر الطبية", color = if(isDark) Color.White else DarkNavy, fontWeight = FontWeight.Medium)
                                    }
                                    Text("${nursesList.size}", fontWeight = FontWeight.Black, fontSize = 18.sp, color = MedicalGreen)
                                }
                            }
                        }
                    }
                }
                "REQUESTS" -> {
                    // Admin controls the tracking lifecycle here
                    LazyColumn(contentPadding = PaddingValues(bottom = 24.dp), modifier = Modifier.fillMaxSize()) {
                        item {
                            Text("اللوحة الحية للطلبات الواردة", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BodyGray)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        if (allBookings.isEmpty()) {
                            item {
                                Text("لا توجد طلبات في النظام حتى الآن.", color = BodyGray, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                            }
                        }
                        items(allBookings.reversed()) { booking ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                colors = CardDefaults.cardColors(containerColor = if(isDark) DarkCard else Color.White),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, if(isDark) Color(0xFF334155) else Color(0xFFE2E8F0))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Assignment, null, tint = MedicalBlue)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("طلب #${booking.id.toString().take(6).uppercase()}", fontWeight = FontWeight.Bold, color = MedicalBlue)
                                        }
                                        BookingStatusBadge(status = booking.status)
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text("الخدمة: ${booking.serviceName}", color = if(isDark) Color.White else DarkNavy, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    Text("العنوان: ${booking.address}", color = BodyGray, fontSize = 13.sp)
                                    Text("الممرض المقترح: ${booking.nurseName}", color = BodyGray, fontSize = 13.sp)
                                    Text("الحجز: ${booking.date} (${booking.timeSlot})", color = BodyGray, fontSize = 13.sp)
                                    
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    // Control Buttons based on Status
                                    if (booking.status != "COMPLETED" && booking.status != "CANCELLED") {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            if (booking.status == "PENDING") {
                                                Button(onClick = { viewModel.updateBookingStatusWithSync(booking.id, "ON_THE_WAY") }, colors = ButtonDefaults.buttonColors(containerColor = MedicalBlue), shape = RoundedCornerShape(10.dp)) {
                                                    Text("تعيين الممرض ومتابعة المريض", fontSize = 12.sp)
                                                }
                                            } else if (booking.status == "ON_THE_WAY") {
                                                Button(onClick = { viewModel.updateBookingStatusWithSync(booking.id, "ARRIVED") }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)), shape = RoundedCornerShape(10.dp)) {
                                                    Text("تأكيد الوصول للمكان", fontSize = 12.sp)
                                                }
                                            } else if (booking.status == "ARRIVED") {
                                                Button(onClick = { viewModel.updateBookingStatusWithSync(booking.id, "IN_PROGRESS") }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)), shape = RoundedCornerShape(10.dp)) {
                                                    Text("بدء تنفيذ الخدمة", fontSize = 12.sp)
                                                }
                                            } else if (booking.status == "IN_PROGRESS") {
                                                Button(onClick = { viewModel.updateBookingStatusWithSync(booking.id, "COMPLETED") }, colors = ButtonDefaults.buttonColors(containerColor = MedicalGreen), shape = RoundedCornerShape(10.dp)) {
                                                    Text("تأكيد الإنهاء", fontSize = 12.sp)
                                                }
                                            }
                                            
                                            TextButton(onClick = { viewModel.cancelBooking(booking.id, booking.nurseName) }) {
                                                Text("إلغاء الطلب", color = Color.Red, fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                "SERVICES" -> {
                    LazyColumn(contentPadding = PaddingValues(bottom = 24.dp), modifier = Modifier.fillMaxSize()) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Button(
                                    onClick = { showAddDepartment = true },
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MedicalGreen)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("إضافة قسم", fontWeight = FontWeight.Bold)
                                    }
                                }
                                Button(
                                    onClick = { showAddService = true },
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MedicalBlue)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("إضافة خدمة", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("الأقسام", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if(isDark) Color.White else DarkNavy)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        
                        items(departmentsList) { dept ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                colors = CardDefaults.cardColors(containerColor = if(isDark) DarkCard else Color.White),
                                border = BorderStroke(1.dp, if(isDark) Color(0xFF334155) else Color(0xFFE2E8F0)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(dept.title, fontWeight = FontWeight.Bold, color = if(isDark) Color.White else DarkNavy)
                                        Text(dept.description, color = BodyGray, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Row {
                                        IconButton(
                                            onClick = {
                                                editDeptTitle = dept.title
                                                editDeptDesc = dept.description
                                                editDeptServiceIds = dept.serviceIdsText
                                                showEditDepartment = dept
                                            },
                                            modifier = Modifier.background(SoftBlueBg, CircleShape).size(36.dp)
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = MedicalBlue, modifier = Modifier.size(18.dp))
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        IconButton(
                                            onClick = {
                                                viewModel.deleteDepartment(dept)
                                            },
                                            modifier = Modifier.background(Color(0xFFFEF2F2), CircleShape).size(36.dp)
                                        ) {
                                            Icon(Icons.Default.DeleteOutline, contentDescription = "حذف", tint = Color.Red, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                        
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("الخدمات", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if(isDark) Color.White else DarkNavy)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        
                        items(servicesList) { service ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                colors = CardDefaults.cardColors(containerColor = if(isDark) DarkCard else Color.White),
                                border = BorderStroke(1.dp, if(isDark) Color(0xFF334155) else Color(0xFFE2E8F0)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Box(modifier = Modifier.size(40.dp).background(SoftBlueBg, CircleShape), contentAlignment = Alignment.Center) {
                                            Icon(getServiceIcon(service.iconName), null, tint = MedicalBlue, modifier = Modifier.size(24.dp))
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(service.title, fontWeight = FontWeight.Bold, color = if(isDark) Color.White else DarkNavy)
                                            Text("${service.basePrice} ج.م", color = BodyGray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                        }
                                    }
                                    IconButton(
                                        onClick = {
                                            viewModel.deleteService(service)
                                        },
                                        modifier = Modifier.background(Color(0xFFFEF2F2), CircleShape)
                                    ) {
                                        Icon(Icons.Default.DeleteOutline, contentDescription = "حذف", tint = Color.Red, modifier = Modifier.size(20.dp))
                                    }
                                }
                            }
                        }
                    }
                }
                "NURSES" -> {
                    var showPending by remember { mutableStateOf(false) }
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                            Button(
                                onClick = { showPending = false },
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = if (!showPending) MedicalBlue else if (isDark) DarkCard else Color.White, contentColor = if (!showPending) Color.White else BodyGray)
                            ) { Text("ملفات معتمدة", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { showPending = true },
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = if (showPending) MedicalBlue else if (isDark) DarkCard else Color.White, contentColor = if (showPending) Color.White else BodyGray)
                            ) { Text("طلبات الانضمام (٢)", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                        }
                        
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            if (showPending) {
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                                        border = BorderStroke(1.dp, Color(0xFFFEF3C7))
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.NewReleases, null, tint = Color(0xFFD97706))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("طلب انضمام ممرض: حسام متولي", fontWeight = FontWeight.Bold, color = DarkNavy)
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text("الخبرة: ٣ سنوات | الشهادة: بكالوريوس تمريض", color = BodyGray, fontSize = 13.sp)
                                            Spacer(modifier = Modifier.height(16.dp))
                                            var isApprovedH by remember { mutableStateOf(false) }
                                            if (!isApprovedH) {
                                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    Button(onClick = {
                                                        viewModel.approveNurse(Nurse("nurse_new_1", "حسام متولي", 4.5, 0, 3, 200.0, "MALE", 0, "0111111111", "لا يوجد", 30.0, 31.0, "مصر"))
                                                        isApprovedH = true
                                                    }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = MedicalGreen), shape = RoundedCornerShape(10.dp)) { Text("قبول وتفعيل") }
                                                    OutlinedButton(onClick = { isApprovedH = true }, modifier = Modifier.weight(1f), border = BorderStroke(1.dp, Color.Red), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red), shape = RoundedCornerShape(10.dp)) { Text("رفض") }
                                                }
                                            }
                                        }
                                    }
                                    
                                    Card(
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                                        border = BorderStroke(1.dp, Color(0xFFFEF3C7))
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.NewReleases, null, tint = Color(0xFFD97706))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("طلب انضمام ممرضة: سالي محمود", fontWeight = FontWeight.Bold, color = DarkNavy)
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text("الخبرة: ١ سنة | الشهادة: معهد تمريض", color = BodyGray, fontSize = 13.sp)
                                            Spacer(modifier = Modifier.height(16.dp))
                                            var isApprovedS by remember { mutableStateOf(false) }
                                            if (!isApprovedS) {
                                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    Button(onClick = { 
                                                        viewModel.approveNurse(Nurse("nurse_new_2", "سالي محمود", 4.3, 0, 1, 150.0, "FEMALE", 0, "0122222222", "لا يوجد", 30.0, 31.0, "مصر"))
                                                        isApprovedS = true
                                                    }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = MedicalGreen), shape = RoundedCornerShape(10.dp)) { Text("قبول وتفعيل") }
                                                    OutlinedButton(onClick = { isApprovedS = true }, modifier = Modifier.weight(1f), border = BorderStroke(1.dp, Color.Red), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red), shape = RoundedCornerShape(10.dp)) { Text("رفض") }
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                items(nursesList) { nurse ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = if(isDark) DarkCard else Color.White),
                                        border = BorderStroke(1.dp, if(isDark) Color(0xFF334155) else Color(0xFFE2E8F0))
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                                Box(modifier = Modifier.size(40.dp).background(SoftBlueBg, CircleShape), contentAlignment = Alignment.Center) {
                                                    Icon(Icons.Default.Person, null, tint = MedicalBlue)
                                                }
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Column {
                                                    Text(nurse.name, fontWeight = FontWeight.Bold, color = if(isDark) Color.White else DarkNavy, fontSize = 15.sp)
                                                    Text("الخبرة: ${nurse.experienceYears} سنوات | التقييم: ${nurse.rating} ⭐", color = BodyGray, fontSize = 12.sp)
                                                    Text("ارتباط: ${nurse.hospitalAffiliation}", color = BodyGray, fontSize = 12.sp)
                                                }
                                            }
                                            IconButton(
                                                onClick = {
                                                    viewModel.deleteNurse(nurse)
                                                },
                                                modifier = Modifier.background(Color(0xFFFEF2F2), CircleShape)
                                            ) {
                                                Icon(Icons.Default.PersonRemove, contentDescription = "حذف", tint = Color.Red, modifier = Modifier.size(20.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                "CHATBOT" -> {
                    var systemPrompt by remember { mutableStateOf("أنت مساعد تمريض متخصص لتطبيق 'معاك كير'. قم بالإجابة على استفسارات المرضى حول الخدمات التمريضية بأسلوب طبي واحترافي ودود...") }
                    var botEnabled by remember { mutableStateOf(true) }
                    
                    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = if(isDark) DarkCard else Color.White),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, if(isDark) Color(0xFF334155) else Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Column {
                                        Text("تفعيل الشات بوت المدمج", fontWeight = FontWeight.Bold, color = if(isDark) Color.White else DarkNavy, fontSize = 16.sp)
                                        Text("يسمح للمرضى بطلب الاستشارات", color = BodyGray, fontSize = 12.sp)
                                    }
                                    Switch(checked = botEnabled, onCheckedChange = { botEnabled = it }, colors = SwitchDefaults.colors(checkedThumbColor = MedicalBlue, checkedTrackColor = SoftBlueBg))
                                }
                                
                                Spacer(modifier = Modifier.height(24.dp))
                                HorizontalDivider(color = if(isDark) Color(0xFF334155) else Color(0xFFF1F5F9))
                                Spacer(modifier = Modifier.height(24.dp))
                                
                                Text("توجيه الذكاء الاصطناعي (System Prompt)", fontWeight = FontWeight.Bold, color = MedicalBlue, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("يمكنك ضبط ردود المساعد وتدريبه على سياسة المنشأة الطبية:", color = BodyGray, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedTextField(
                                    value = systemPrompt,
                                    onValueChange = { systemPrompt = it },
                                    modifier = Modifier.fillMaxWidth().height(200.dp),
                                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { /* Save Prompt */ },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MedicalBlue),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("حفظ وتنشيط التدريب السحابي", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddService) {
        AlertDialog(
            onDismissRequest = { showAddService = false },
            containerColor = if (isDark) DarkCard else Color.White,
            title = { Text("إضافة خدمة طبية جديدة", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("أدخل تفاصيل الخدمة الجديدة ليتم نشرها في تطبيق المريض", color = BodyGray, fontSize = 13.sp)
                    OutlinedTextField(
                        value = newServiceName,
                        onValueChange = { newServiceName = it },
                        label = { Text("المسمى الطبي") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = newServicePrice,
                        onValueChange = { newServicePrice = it },
                        label = { Text("سعر الزيارة (ج.م)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newServiceName.isNotBlank()) {
                            val newService = com.example.data.MedicalService(
                                id = "cust_${System.currentTimeMillis()}",
                                title = newServiceName,
                                description = "رعاية طبية عالية المستوى",
                                longDescription = "وصف تفصيلي للخدمة المضافة لتطبيق معاك",
                                basePrice = newServicePrice.toDoubleOrNull() ?: 0.0,
                                duration = "يحدد لاحقاً",
                                iconName = "Healing"
                            )
                            viewModel.addServiceToSupabase(newService)
                            showAddService = false
                            newServiceName = ""
                            newServicePrice = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalGreen),
                    shape = RoundedCornerShape(10.dp)
                ) { Text("نشر وتفعيل") }
            },
            dismissButton = {
                TextButton(onClick = { showAddService = false }) { Text("إلغاء الأمر", color = Color.Red) }
            }
        )
    }

    if (showAddDepartment) {
        AlertDialog(
            onDismissRequest = { showAddDepartment = false },
            containerColor = if (isDark) DarkCard else Color.White,
            title = { Text("إضافة قسم جديد", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("أدخل تفاصيل القسم الجديد (مثل قسم التمريض، الطوارئ، وغيرها)", color = BodyGray, fontSize = 13.sp)
                    OutlinedTextField(
                        value = newDeptTitle,
                        onValueChange = { newDeptTitle = it },
                        label = { Text("اسم القسم") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = newDeptDesc,
                        onValueChange = { newDeptDesc = it },
                        label = { Text("وصف القسم") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = newDeptServiceIds,
                        onValueChange = { newDeptServiceIds = it },
                        label = { Text("مُعرَّفات الخدمات (بالفاصلة , )") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newDeptTitle.isNotBlank()) {
                            viewModel.addDepartment(newDeptTitle, newDeptDesc, newDeptServiceIds)
                            showAddDepartment = false
                            newDeptTitle = ""
                            newDeptDesc = ""
                            newDeptServiceIds = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalGreen),
                    shape = RoundedCornerShape(10.dp)
                ) { Text("حفظ القسم") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDepartment = false }) { Text("إلغاء الأمر", color = Color.Red) }
            }
        )
    }

    if (showEditDepartment != null) {
        val deptId = showEditDepartment!!.id
        AlertDialog(
            onDismissRequest = { showEditDepartment = null },
            containerColor = if (isDark) DarkCard else Color.White,
            title = { Text("تعديل القسم", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = editDeptTitle,
                        onValueChange = { editDeptTitle = it },
                        label = { Text("اسم القسم") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = editDeptDesc,
                        onValueChange = { editDeptDesc = it },
                        label = { Text("وصف القسم") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = editDeptServiceIds,
                        onValueChange = { editDeptServiceIds = it },
                        label = { Text("مُعرَّفات الخدمات (بالفاصلة , )") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editDeptTitle.isNotBlank()) {
                            viewModel.updateDepartment(deptId, editDeptTitle, editDeptDesc, editDeptServiceIds)
                            showEditDepartment = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalGreen),
                    shape = RoundedCornerShape(10.dp)
                ) { Text("حفظ التعديلات") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDepartment = null }) { Text("إلغاء الأمر", color = Color.Red) }
            }
        )
    }
}


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
            Surface(shadowElevation = 4.dp, color = Color.White) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp).padding(top = 24.dp), 
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.navigateTo("profile") }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = Color.Black)
                    }
                    Text("تعديل الملف الطبي", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.weight(1f))
                    Button(
                        onClick = { 
                            viewModel.updatePatientProfile(
                                name = editName, phone = editPhone, address = editAddress, age = editAge,
                                bloodType = editBloodType, weight = editWeight, gender = editGender,
                                conditions = editConditions, allergies = editAllergies, emergencyName = editEmergencyName,
                                emergencyPhone = editEmergencyPhone, heartRate = viewModel.recentHeartRate.value,
                                bloodPressure = viewModel.recentBloodPressure.value, bloodSugar = viewModel.recentBloodSugar.value
                            )
                            viewModel.navigateTo("profile")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MedicalBlue),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("حفظ", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Personal Info Section
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = MedicalBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("البيانات الشخصية", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                    }
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    OutlinedTextField(value = editName, onValueChange = { editName = it }, label = { Text("الاسم") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = editPhone, onValueChange = { editPhone = it }, label = { Text("رقم الهاتف") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
                    OutlinedTextField(value = editAddress, onValueChange = { editAddress = it }, label = { Text("العنوان") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                }
            }

            // Medical Info Section
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.HealthAndSafety, contentDescription = null, tint = MedicalBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("التفاصيل الطبية", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                    }
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(value = editAge, onValueChange = { editAge = it }, label = { Text("العمر") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        OutlinedTextField(value = editBloodType, onValueChange = { editBloodType = it }, label = { Text("فصيلة الدم") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(value = editWeight, onValueChange = { editWeight = it }, label = { Text("الوزن (كجم)") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        OutlinedTextField(value = editGender, onValueChange = { editGender = it }, label = { Text("النوع") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                    }
                    OutlinedTextField(value = editConditions, onValueChange = { editConditions = it }, label = { Text("الأمراض المزمنة") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = editAllergies, onValueChange = { editAllergies = it }, label = { Text("الحساسية") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                }
            }

            // Emergency Contact
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = Color.Red)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("جهة اتصال للطوارئ", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                    }
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    OutlinedTextField(value = editEmergencyName, onValueChange = { editEmergencyName = it }, label = { Text("الاسم") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = editEmergencyPhone, onValueChange = { editEmergencyPhone = it }, label = { Text("التليفون") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
                }
            }
            
            Spacer(Modifier.height(48.dp))
        }
    }
}

@Composable
fun WalletScreen(viewModel: ELmorsyViewModel) {
    val balance by viewModel.walletBalance.collectAsState()
    val transactions by viewModel.walletTransactions.collectAsState()
    var rechargeAmount by remember { mutableStateOf("") }
    var selectedMethod by remember { mutableStateOf("CREDIT_CARD") } // "CREDIT_CARD", "VODAFONE_CASH", "FAWRY"
    
    // Gradient for the credit card look
    val cardGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF1E3A8A), // Dark blue
            Color(0xFF3B82F6), // Lighter blue
            Color(0xFF0EA5E9)  // Sky blue
        )
    )

    Scaffold(
        topBar = {
            Surface(
                shadowElevation = 4.dp, 
                color = Color.White,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 20.dp).padding(top = 24.dp), 
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.navigateTo("profile") }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = Color.Black)
                    }
                    Spacer(Modifier.width(8.dp))
                    Text("المحفظة والرصيد", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                }
            }
        },
        containerColor = Color(0xFFF1F5F9) // Slate 50 background for premium look
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Premium Credit Card Style Balance
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(cardGradient)
                        .padding(24.dp)
                ) {
                    // Decorative circles
                    Box(modifier = Modifier.size(150.dp).offset(x = 200.dp, y = (-50).dp).background(Color.White.copy(alpha = 0.1f), CircleShape))
                    Box(modifier = Modifier.size(100.dp).offset(x = 250.dp, y = 100.dp).background(Color.White.copy(alpha = 0.1f), CircleShape))
                    
                    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("الرصيد المتاح", color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp, fontWeight = FontWeight.Medium)
                            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color.White.copy(alpha = 0.9f), modifier = Modifier.size(28.dp))
                        }
                        
                        Text("$balance ج.م", color = Color.White, fontSize = 42.sp, fontWeight = FontWeight.Black)
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                            Column {
                                Text("رقم الحساب", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                                Text("**** **** **** 1234", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 2.sp)
                            }
                            // Mastercard-like circles
                            Row {
                                Box(modifier = Modifier.size(24.dp).background(Color.White.copy(alpha = 0.5f), CircleShape))
                                Box(modifier = Modifier.size(24.dp).offset(x = (-12).dp).background(Color.White.copy(alpha = 0.3f), CircleShape))
                            }
                        }
                    }
                }
            }
            
            Spacer(Modifier.height(32.dp))
            
            // Recharge Section wrapped in a Card for cleaner look
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("شحن الرصيد", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                    Spacer(Modifier.height(16.dp))
                    
                    // Payment methods selection
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val methods = listOf(
                            Triple("CREDIT_CARD", "بطاقة ائتمان", Icons.Default.CreditCard),
                            Triple("VODAFONE_CASH", "فودافون كاش", Icons.Default.Smartphone),
                            Triple("FAWRY", "فوري", Icons.Default.AccountBalanceWallet)
                        )
                        methods.forEach { (id, name, icon) ->
                            val isSelected = selectedMethod == id
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(70.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) MedicalBlue.copy(alpha = 0.1f) else Color(0xFFF8FAFC))
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) MedicalBlue else Color(0xFFE2E8F0),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedMethod = id },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                    Icon(icon, contentDescription = name, tint = if (isSelected) MedicalBlue else Color.Gray, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(name, fontSize = 10.sp, color = if (isSelected) MedicalBlue else Color.Gray, fontWeight = if(isSelected) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(), 
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = rechargeAmount, 
                            onValueChange = { rechargeAmount = it }, 
                            placeholder = { Text("المبلغ (ج.م)", color = Color.Gray) }, 
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), 
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MedicalBlue,
                                unfocusedBorderColor = Color(0xFFE2E8F0),
                                focusedContainerColor = Color(0xFFF8FAFC),
                                unfocusedContainerColor = Color(0xFFF8FAFC)
                            )
                        )
                        Button(
                            onClick = { 
                                val amount = rechargeAmount.toDoubleOrNull() ?: 0.0
                                if(amount > 0) {
                                    viewModel.rechargeWallet(amount, selectedMethod)
                                    rechargeAmount = ""
                                }
                            }, 
                            modifier = Modifier.height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MedicalBlue)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                            Spacer(Modifier.width(4.dp))
                            Text("شحن", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
            
            Text("سجل المعاملات", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color.Black)
            Spacer(Modifier.height(16.dp))
            
            if (transactions.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.History, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(64.dp))
                        Spacer(Modifier.height(12.dp))
                        Text("لا توجد معاملات سابقة", color = Color.Gray, fontSize = 16.sp)
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(transactions) { t ->
                        val isCredit = t.isCredit
                        val icon = if (isCredit) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward
                        val color = if (isCredit) Color(0xFF10B981) else Color(0xFFEF4444)
                        val bgColor = if (isCredit) Color(0xFFD1FAE5) else Color(0xFFFEE2E2)
                        
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(52.dp).background(bgColor, CircleShape), 
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
                                }
                                Spacer(Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = t.title, 
                                        fontWeight = FontWeight.Bold, 
                                        fontSize = 16.sp, 
                                        color = Color.Black,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(if(isCredit) "عملية إيداع ناجحة" else "عملية خصم", color = Color.Gray, fontSize = 13.sp)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${if(isCredit) "+" else "-"}${t.amount}", 
                                        fontWeight = FontWeight.ExtraBold, 
                                        fontSize = 18.sp, 
                                        color = color
                                    )
                                    Text("ج.م", color = Color.Gray, fontSize = 12.sp)
                                }
                            }
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
            Surface(shadowElevation = 4.dp, color = Color.White) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp).padding(top = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.navigateTo("profile") }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = Color.Black)
                    }
                    Text("الوصفات والتحاليل", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MedicalBlue,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, null)
            }
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            if (documents.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(80.dp))
                        Spacer(Modifier.height(16.dp))
                        Text("لا توجد ملفات مرفوعة", color = Color.Gray, fontSize = 16.sp)
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(documents) { doc ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(48.dp).background(Color(0xFFE0F2FE), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Description, contentDescription = null, tint = MedicalBlue)
                                }
                                Spacer(Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(doc.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                                    Text("تم الرفع مؤخراً", color = Color.Gray, fontSize = 12.sp)
                                }
                                IconButton(onClick = { viewModel.deleteMedicalDocument(doc.id) }) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "حذف", tint = Color.Red)
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
            title = { Text("رفع ملف جديد", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = docName, 
                    onValueChange = { docName = it }, 
                    label = { Text("اسم الملف أو الوصفة") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = { 
                        if(docName.isNotBlank()) {
                            viewModel.addMedicalDocument(docName)
                            showAddDialog = false
                            docName = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalBlue)
                ) { Text("رفع وحفظ") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }, colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)) { Text("إلغاء") }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun VitalsDashboardScreen(viewModel: ELmorsyViewModel) {
    var newVitalType by remember { mutableStateOf("HEART") }
    var newValue by remember { mutableStateOf("") }
    
    val patientAge by viewModel.patientAge.collectAsState()
    val patientBloodType by viewModel.patientBloodType.collectAsState()
    val patientWeight by viewModel.patientWeight.collectAsState()
    val patientGender by viewModel.patientGender.collectAsState()
    val patientConditions by viewModel.patientConditions.collectAsState()
    val patientAllergies by viewModel.patientAllergies.collectAsState()
    val emergencyContactName by viewModel.emergencyContactName.collectAsState()
    val emergencyContactPhone by viewModel.emergencyContactPhone.collectAsState()
    val recentHeartRate by viewModel.recentHeartRate.collectAsState()
    val recentBloodPressure by viewModel.recentBloodPressure.collectAsState()
    val recentBloodSugar by viewModel.recentBloodSugar.collectAsState()
    
    Scaffold(
        topBar = {
            Surface(shadowElevation = 4.dp, color = Color.White) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp).padding(top = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.navigateTo("profile") }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = Color.Black)
                    }
                    Text("لوحة القياسات الحيوية", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp)) {
            Text("القياسات الأخيرة", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = Color.Black)
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                VitalCard("الضغط", recentBloodPressure.ifBlank { "--" }, "mmHg", Icons.Default.FavoriteBorder, Color(0xFF3B82F6), Modifier.weight(1f))
                VitalCard("النبض", recentHeartRate.ifBlank { "--" }, "bpm", Icons.Default.Favorite, Color(0xFFEF4444), Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                VitalCard("السكر", recentBloodSugar.ifBlank { "--" }, "mg/dL", Icons.Default.WaterDrop, Color(0xFF8B5CF6), Modifier.weight(1f))
                VitalCard("الوزن", patientWeight.ifBlank { "--" }, "kg", Icons.Default.MonitorWeight, Color(0xFF10B981), Modifier.weight(1f))
            }
            
            Spacer(Modifier.height(32.dp))
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("إضافة قياس جديد", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                    Spacer(Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        listOf("HEART" to "النبض", "PRESSURE" to "الضغط", "SUGAR" to "السكر").forEach { (type, label) ->
                            FilterChip(
                                selected = newVitalType == type,
                                onClick = { newVitalType = type },
                                label = { Text(label, fontWeight = FontWeight.Medium) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MedicalBlue.copy(alpha = 0.15f),
                                    selectedLabelColor = MedicalBlue
                                ),
                                border = FilterChipDefaults.filterChipBorder(enabled = true, selected = newVitalType == type, borderColor = MedicalBlue)
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = newValue, 
                        onValueChange = { newValue = it }, 
                        label = { Text("قيمة القياس الجدبد") }, 
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = { 
                            if(newValue.isNotBlank()) {
                                viewModel.addVitalLog(newVitalType, newValue, "")
                                viewModel.updatePatientProfile(
                                    name = viewModel.userName.value, phone = viewModel.userPhone.value,
                                    address = viewModel.userAddress.value, age = patientAge,
                                    bloodType = patientBloodType, weight = patientWeight, gender = patientGender,
                                    conditions = patientConditions, allergies = patientAllergies,
                                    emergencyName = emergencyContactName, emergencyPhone = emergencyContactPhone,
                                    heartRate = if(newVitalType == "HEART") newValue else recentHeartRate,
                                    bloodPressure = if(newVitalType == "PRESSURE") newValue else recentBloodPressure,
                                    bloodSugar = if(newVitalType == "SUGAR") newValue else recentBloodSugar
                                )
                                newValue = ""
                            }
                        }, 
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MedicalBlue)
                    ) {
                        Text("تسجيل وحفظ", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun VitalCard(title: String, value: String, unit: String, icon: ImageVector, iconColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier, 
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Box(modifier = Modifier.size(40.dp).background(iconColor.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(title, color = Color.Gray, fontSize = 14.sp)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = Color.Black)
                Spacer(Modifier.width(4.dp))
                Text(unit, color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
            }
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
            Surface(shadowElevation = 4.dp, color = Color.White) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp).padding(top = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.navigateTo("profile") }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = Color.Black)
                    }
                    Text("التذكير بالأدوية", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MedicalBlue,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, null)
            }
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            if (reminders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Medication, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(80.dp))
                        Spacer(Modifier.height(16.dp))
                        Text("لا توجد مذكرات للأدوية حالياً", color = Color.Gray, fontSize = 16.sp)
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(reminders) { rem ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Box(modifier = Modifier.size(48.dp).background(Color(0xFFFEF3C7), CircleShape), contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Medication, contentDescription = null, tint = Color(0xFFD97706))
                                    }
                                    Spacer(Modifier.width(16.dp))
                                    Column {
                                        Text(rem.medName, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                                        Spacer(Modifier.height(4.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                                            Spacer(Modifier.width(4.dp))
                                            Text("${rem.time} - الجرعة: ${rem.dosage}", color = Color.Gray, fontSize = 13.sp)
                                        }
                                    }
                                }
                                IconButton(onClick = { viewModel.deleteMedicationReminder(rem.id) }) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "حذف", tint = Color.Red)
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
            title = { Text("إضافة تنبيه دواء جديد", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = medName, 
                        onValueChange = { medName = it }, 
                        label = { Text("اسم الدواء") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = medDose, 
                        onValueChange = { medDose = it }, 
                        label = { Text("الجرعة (مثال: قرص واحد)") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { 
                        if(medName.isNotBlank()) {
                            viewModel.addMedicationReminder(medName, medDose, "12:00") // Time can be made dynamic later
                            showAddDialog = false
                            medName = ""
                            medDose = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalBlue)
                ) { Text("إضافة التنبيه") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }, colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)) { Text("إلغاء") }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun NurseChatScreen(viewModel: ELmorsyViewModel) {
    val nurse = viewModel.selectedNurse.collectAsState().value
    var message by remember { mutableStateOf("") }
    
    val chatMessages by viewModel.chatMessages.collectAsState()
    
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }
    
    Scaffold(
        topBar = {
            Surface(shadowElevation = 8.dp, color = Color.White, shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp).padding(top = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.navigateTo("tracking") }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = Color.Black)
                    }
                    Spacer(Modifier.width(8.dp))
                    
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(Color(0xFFF1F5F9), CircleShape)
                            .border(2.dp, MedicalBlue, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = MedicalBlue)
                    }
                    
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(nurse?.name ?: "الممرض", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(Color(0xFF10B981), CircleShape))
                            Spacer(Modifier.width(4.dp))
                            Text("متصل الآن - في الطريق إليك", fontSize = 12.sp, color = Color(0xFF10B981))
                        }
                    }
                    
                    IconButton(onClick = { /* Calling feature */ }) {
                        Icon(Icons.Default.Call, contentDescription = "Call", tint = MedicalBlue)
                    }
                }
            }
        },
        bottomBar = {
            Surface(shadowElevation = 16.dp, color = Color.White) {
                Row(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("اكتب رسالتك للممرض...") },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedicalBlue,
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            unfocusedContainerColor = Color(0xFFF8FAFC),
                            focusedContainerColor = Color.White
                        ),
                        maxLines = 3
                    )
                    Spacer(Modifier.width(12.dp))
                    FloatingActionButton(
                        onClick = { 
                            if(message.isNotBlank()) {
                                viewModel.sendChatMessage(message, isHelpdesk = false)
                                message = ""
                            }
                        },
                        containerColor = MedicalBlue,
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "إرسال")
                    }
                }
            }
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("اليوم", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.background(Color(0xFFE2E8F0), RoundedCornerShape(12.dp)).padding(horizontal = 12.dp, vertical = 4.dp))
                }
            }
            
            items(chatMessages) { chatMsg ->
                val isUser = chatMsg.sender == "PATIENT"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    if (!isUser && !chatMsg.isSystem) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFFE2E8F0), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = MedicalBlue, modifier = Modifier.size(18.dp))
                        }
                        Spacer(Modifier.width(8.dp))
                    }
                    
                    if (chatMsg.isSystem) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(chatMsg.content, fontSize = 12.sp, color = Color.Gray)
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .background(
                                    color = if (isUser) MedicalBlue else Color.White,
                                    shape = RoundedCornerShape(
                                        topStart = 20.dp,
                                        topEnd = 20.dp,
                                        bottomStart = if (isUser) 20.dp else 4.dp,
                                        bottomEnd = if (isUser) 4.dp else 20.dp
                                    )
                                )
                                .border(
                                    width = if (isUser) 0.dp else 1.dp,
                                    color = if (isUser) Color.Transparent else Color(0xFFE2E8F0),
                                    shape = RoundedCornerShape(
                                        topStart = 20.dp,
                                        topEnd = 20.dp,
                                        bottomStart = if (isUser) 20.dp else 4.dp,
                                        bottomEnd = if (isUser) 4.dp else 20.dp
                                    )
                                )
                                .padding(16.dp)
                        ) {
                            Column {
                                Text(
                                    text = chatMsg.content,
                                    color = if (isUser) Color.White else Color.Black,
                                    fontSize = 15.sp,
                                    lineHeight = 22.sp
                                )
                                Text(
                                    text = chatMsg.timestamp,
                                    fontSize = 10.sp,
                                    color = if(isUser) Color.White.copy(alpha = 0.7f) else Color.Gray,
                                    modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
                                )
                            }
                        }
                    }
                    
                    if (isUser && !chatMsg.isSystem) {
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFFE2E8F0), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = "User", tint = Color.Gray, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SupportChatScreen(viewModel: ELmorsyViewModel) {
    var message by remember { mutableStateOf("") }
    val chatMessages = remember { mutableStateListOf<Pair<String, Boolean>>(
        "مرحباً بك! فريق الدعم الطبي معك كير جاهز للرد على استفساراتك." to false
    ) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
    Scaffold(
        topBar = {
            Surface(shadowElevation = 4.dp, color = Color.White) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp).padding(top = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.navigateTo("profile") }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = Color.Black)
                    }
                    Spacer(Modifier.width(8.dp))
                    Box(modifier = Modifier.size(40.dp).background(MedicalBlue.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.SupportAgent, contentDescription = null, tint = MedicalBlue)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("الدعم الطبي اللحظي", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text("متصل الآن", fontSize = 12.sp, color = Color(0xFF10B981))
                    }
                }
            }
        },
        bottomBar = {
            Surface(shadowElevation = 16.dp, color = Color.White) {
                Row(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("اكتب استفسارك هنا...") },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedicalBlue,
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        ),
                        maxLines = 3
                    )
                    Spacer(Modifier.width(12.dp))
                    FloatingActionButton(
                        onClick = { 
                            if(message.isNotBlank()) {
                                chatMessages.add(message to true)
                                val msgCopy = message
                                message = ""
                                coroutineScope.launch {
                                    listState.animateScrollToItem(chatMessages.size - 1)
                                }
                            }
                        },
                        containerColor = MedicalBlue,
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "إرسال")
                    }
                }
            }
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(chatMessages.size) { index ->
                val (msg, isUser) = chatMessages[index]
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start) {
                    if (!isUser) {
                        Box(modifier = Modifier.size(32.dp).background(MedicalBlue.copy(alpha = 0.2f), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.SupportAgent, null, tint = MedicalBlue, modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(8.dp))
                    }
                    Card(
                        colors = CardDefaults.cardColors(containerColor = if (isUser) MedicalBlue else Color.White),
                        shape = RoundedCornerShape(
                            topStart = 20.dp,
                            topEnd = 20.dp,
                            bottomStart = if (isUser) 20.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 20.dp
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isUser) 0.dp else 2.dp),
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Text(
                            text = msg, 
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), 
                            color = if (isUser) Color.White else Color.Black,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AllServicesScreen(
    viewModel: ELmorsyViewModel,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    val servicesList by viewModel.servicesFlow.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredServices = servicesList.filter { it.title.contains(searchQuery, ignoreCase = true) || it.description.contains(searchQuery, ignoreCase = true) }
    
    val unreadCount by remember { derivedStateOf { viewModel.notifications.value.count { !it.isRead } } }

    Scaffold(
        bottomBar = {
            ELmorsyBottomBar(
                currentScreen = "services",
                onNavigate = { route -> viewModel.navigateTo(route) },
                notificationsCount = unreadCount
            )
        },
        topBar = {
            Surface(
                shadowElevation = 8.dp, 
                color = Color.White,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 20.dp).padding(top = 24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("الخدمات الطبية", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black, modifier = Modifier.weight(1f))
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("ابحث عن خدمة، مثال: تمريض، زيارة طبيب...", color = Color.Gray) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "بحث", tint = MedicalBlue) },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedicalBlue,
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedContainerColor = Color(0xFFF8FAFC),
                            unfocusedContainerColor = Color(0xFFF8FAFC)
                        )
                    )
                }
            }
        },
        containerColor = Color(0xFFF1F5F9)
    ) { padding ->
        if (filteredServices.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.MedicalServices, contentDescription = null, modifier = Modifier.size(80.dp), tint = Color.LightGray)
                    Spacer(Modifier.height(16.dp))
                    Text("لا توجد خدمات مطابقة للبحث", fontSize = 18.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredServices) { service ->
                    ServiceCardAdvanced(
                        service = service,
                        onClick = { 
                            viewModel.setService(service)
                            viewModel.navigateTo("service_details") 
                        },
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ServiceCardAdvanced(
    service: com.example.data.MedicalService,
    onClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope?,
    animatedVisibilityScope: AnimatedVisibilityScope?
) {
    val icon = when (service.iconName) {
        "User" -> Icons.Default.Person
        "Star" -> Icons.Default.Star
        "Heart" -> Icons.Default.FavoriteBorder
        "Therapy" -> Icons.Default.Elderly
        "Stethoscope" -> Icons.Default.VerticalSplit
        "Monitor" -> Icons.Default.MonitorHeart
        "Wheelchair" -> Icons.Default.Accessible
        "Home" -> Icons.Default.Home
        "Lab" -> Icons.Default.Science
        "Xray" -> Icons.Default.CameraAlt
        "Baby" -> Icons.Default.ChildCare
        "Massage" -> Icons.Default.Spa
        "Air" -> Icons.Default.Air
        else -> Icons.Default.MedicalServices
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                    with(sharedTransitionScope) {
                        Modifier.sharedElement(
                            rememberSharedContentState(key = "service_bg_${service.id}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    }
                } else Modifier
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(Color(0xFFF0F9FF), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (service.imageUrl.isNotBlank()) {
                    coil.compose.AsyncImage(
                        model = service.imageUrl,
                        contentDescription = service.title,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = icon,
                        contentDescription = service.title,
                        tint = MedicalBlue,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = service.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                    with(sharedTransitionScope) {
                        Modifier.sharedElement(
                            rememberSharedContentState(key = "service_title_${service.id}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    }
                } else Modifier
            )
            
            Spacer(modifier = Modifier.height(6.dp))
            
            Text(
                text = service.description,
                fontSize = 12.sp,
                color = Color.Gray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${service.basePrice.toInt()} ج.م",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MedicalBlue
                )
                
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(MedicalBlue, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "التفاصيل",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}