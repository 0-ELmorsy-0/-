package com.example.connectivity

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit
import android.util.Log

@JsonClass(generateAdapter = true)
data class SupabaseDepartment(
    @Json(name = "id") val id: String? = null,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String,
    @Json(name = "service_ids_text") val serviceIdsText: String
)

@JsonClass(generateAdapter = true)
data class SupabaseMedicalService(
    @Json(name = "id") val id: String? = null,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String,
    @Json(name = "long_description") val longDescription: String,
    @Json(name = "base_price") val basePrice: Double,
    @Json(name = "duration") val duration: String,
    @Json(name = "icon_name") val iconName: String,
    @Json(name = "image_url") val imageUrl: String = ""
)

@JsonClass(generateAdapter = true)
data class SupabaseNurse(
    @Json(name = "id") val id: String? = null,
    @Json(name = "name") val name: String,
    @Json(name = "rating") val rating: Double,
    @Json(name = "rating_count") val ratingCount: Int,
    @Json(name = "experience_years") val experienceYears: Int,
    @Json(name = "price_per_visit") val pricePerVisit: Double,
    @Json(name = "gender") val gender: String,
    @Json(name = "completed_visits") val completedVisits: Int,
    @Json(name = "phone") val phone: String,
    @Json(name = "hospital_affiliation") val hospitalAffiliation: String,
    @Json(name = "lat") val lat: Double,
    @Json(name = "lng") val lng: Double,
    @Json(name = "home_district") val homeDistrict: String,
    @Json(name = "status") val status: String = "ACTIVE"
)

@JsonClass(generateAdapter = true)
data class SupabaseBooking(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "service_name") val serviceName: String,
    @Json(name = "service_icon_name") val serviceIconName: String,
    @Json(name = "nurse_name") val nurseName: String,
    @Json(name = "nurse_rating") val nurseRating: Double,
    @Json(name = "nurse_experience") val nurseExperience: Int,
    @Json(name = "price") val price: Double,
    @Json(name = "date") val date: String,
    @Json(name = "time_slot") val timeSlot: String,
    @Json(name = "address") val address: String,
    @Json(name = "notes") val notes: String = "",
    @Json(name = "payment_method") val paymentMethod: String,
    @Json(name = "status") val status: String,
    @Json(name = "timestamp") val timestamp: Long,
    @Json(name = "rating") val rating: Float = 0f,
    @Json(name = "review_comment") val reviewComment: String = ""
)

@JsonClass(generateAdapter = true)
data class SupabaseAdmin(
    @Json(name = "id") val id: String? = null,
    @Json(name = "email") val email: String
)

@JsonClass(generateAdapter = true)
data class SupabaseAuthRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String,
    @Json(name = "data") val data: Map<String, String>? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseUserMetadata(
    @Json(name = "full_name") val fullName: String? = null,
    @Json(name = "phone") val phone: String? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseUser(
    @Json(name = "id") val id: String? = null,
    @Json(name = "email") val email: String? = null,
    @Json(name = "user_metadata") val userMetadata: SupabaseUserMetadata? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseAuthResponse(
    @Json(name = "access_token") val accessToken: String? = null,
    @Json(name = "user") val user: SupabaseUser? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseProfile(
    @Json(name = "id") val id: String? = null,
    @Json(name = "phone") val phone: String,
    @Json(name = "name") val name: String,
    @Json(name = "email") val email: String? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseNurseRequest(
    @Json(name = "id") val id: String? = null,
    @Json(name = "name") val name: String,
    @Json(name = "phone") val phone: String,
    @Json(name = "email") val email: String? = null,
    @Json(name = "experience_years") val experienceYears: Int = 0,
    @Json(name = "certificate") val certificate: String = "",
    @Json(name = "hospital_affiliation") val hospitalAffiliation: String = "",
    @Json(name = "gender") val gender: String = "MALE",
    @Json(name = "home_district") val homeDistrict: String = "",
    @Json(name = "status") val status: String = "PENDING",
    @Json(name = "notes") val notes: String = "",
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class DeveloperProfile(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "name") val name: String,
    @Json(name = "title") val title: String,
    @Json(name = "message") val message: String,
    @Json(name = "image_url") val imageUrl: String
)

@JsonClass(generateAdapter = true)
data class AppSlider(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String,
    @Json(name = "image_url") val imageUrl: String
)

@JsonClass(generateAdapter = true)
data class SupabaseAppSetting(
    @Json(name = "key") val key: String,
    @Json(name = "value") val value: String
)

interface SupabaseApiService {
    @GET("rest/v1/departments")
    suspend fun getDepartments(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("select") select: String = "*"
    ): Response<List<SupabaseDepartment>>

    @POST("rest/v1/departments")
    suspend fun insertDepartment(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Body department: SupabaseDepartment
    ): Response<ResponseBody>

    @GET("rest/v1/services")
    suspend fun getServices(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("select") select: String = "*"
    ): Response<List<SupabaseMedicalService>>

    @POST("rest/v1/services")
    suspend fun insertService(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Body service: SupabaseMedicalService
    ): Response<ResponseBody>

    @DELETE("rest/v1/services")
    suspend fun deleteService(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("id") idFilter: String // e.g., "eq.service_id"
    ): Response<ResponseBody>

    @GET("rest/v1/nurses")
    suspend fun getNurses(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("select") select: String = "*"
    ): Response<List<SupabaseNurse>>

    @POST("rest/v1/nurses")
    suspend fun insertNurse(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Body nurse: SupabaseNurse
    ): Response<ResponseBody>
    
    @PATCH("rest/v1/nurses")
    suspend fun updateNurse(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("id") idFilter: String,
        @Body updateData: Map<String, String>
    ): Response<ResponseBody>

    @DELETE("rest/v1/nurses")
    suspend fun deleteNurse(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("id") idFilter: String
    ): Response<ResponseBody>

    @PATCH("rest/v1/departments")
    suspend fun updateDepartment(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("id") idFilter: String,
        @Body dept: SupabaseDepartment
    ): Response<ResponseBody>

    @DELETE("rest/v1/departments")
    suspend fun deleteDepartment(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("id") idFilter: String
    ): Response<ResponseBody>

    @POST("rest/v1/bookings")
    @Headers("Prefer: return=representation")
    suspend fun insertBooking(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Body booking: SupabaseBooking
    ): Response<List<ResponseBody>>

    @PATCH("rest/v1/bookings")
    @Headers("Prefer: return=representation")
    suspend fun updateBooking(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("id") idFilter: String, // e.g., "eq.10"
        @Body bookingUpdate: Map<String, String>
    ): Response<List<ResponseBody>>

    @GET("rest/v1/bookings")
    suspend fun getBookings(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("select") select: String = "*"
    ): Response<List<SupabaseBooking>>

    // Official Supabase Auth Signup
    @POST("auth/v1/signup")
    suspend fun signUp(
        @Header("apikey") apiKey: String,
        @Body body: SupabaseAuthRequest
    ): Response<SupabaseAuthResponse>

    // Official Supabase Auth Token Signin
    @POST("auth/v1/token")
    suspend fun signIn(
        @Header("apikey") apiKey: String,
        @Query("grant_type") grantType: String,
        @Body body: SupabaseAuthRequest
    ): Response<SupabaseAuthResponse>

    // Custom clinical fallback profiles for instant phone registrations (requires no paid SMS gateway configured)
    @POST("rest/v1/profiles")
    suspend fun insertProfile(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Body profile: SupabaseProfile
    ): Response<ResponseBody>

    @GET("rest/v1/profiles")
    suspend fun getProfile(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("phone") phoneFilter: String // e.g. "eq.010214312"
    ): Response<List<SupabaseProfile>>

    @GET("rest/v1/profiles")
    suspend fun getProfileByEmail(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("email") emailFilter: String // e.g. "eq.m@icloud.com"
    ): Response<List<SupabaseProfile>>

    @GET("rest/v1/admins")
    suspend fun getAdmin(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("email") emailFilter: String // e.g. "eq.admin@aistudio.com"
    ): Response<List<SupabaseAdmin>>

    @PATCH("rest/v1/services")
    suspend fun updateService(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("id") idFilter: String,
        @Body serviceUpdate: Map<String, String>
    ): Response<ResponseBody>

    @GET("rest/v1/nurse_requests")
    suspend fun getNurseRequests(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("status") statusFilter: String = "eq.PENDING",
        @Query("select") select: String = "*",
        @Query("order") order: String = "created_at.desc"
    ): Response<List<SupabaseNurseRequest>>

    @PATCH("rest/v1/nurse_requests")
    suspend fun updateNurseRequest(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("id") idFilter: String,
        @Body update: Map<String, String>
    ): Response<ResponseBody>

    @GET("rest/v1/app_settings")
    suspend fun getSetting(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("key") keyFilter: String
    ): Response<List<SupabaseAppSetting>>

    @GET("rest/v1/app_settings")
    suspend fun getAllSettings(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("select") select: String = "*"
    ): Response<List<SupabaseAppSetting>>

    @GET("rest/v1/developer_profile")
    suspend fun getDeveloperProfile(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("select") select: String = "*"
    ): Response<List<DeveloperProfile>>

    @GET("rest/v1/app_sliders")
    suspend fun getAppSliders(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("select") select: String = "*"
    ): Response<List<AppSlider>>

    @PATCH("rest/v1/developer_profile")
    suspend fun updateDeveloperProfile(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("id") idFilter: String,
        @Body update: Map<String, String>
    ): Response<ResponseBody>

    @POST("rest/v1/developer_profile")
    suspend fun insertDeveloperProfile(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Body profile: Map<String, String>
    ): Response<ResponseBody>

    @PATCH("rest/v1/app_sliders")
    suspend fun updateAppSlider(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("id") idFilter: String,
        @Body update: Map<String, String>
    ): Response<ResponseBody>

    @POST("rest/v1/app_sliders")
    suspend fun insertAppSlider(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Body slider: Map<String, String>
    ): Response<ResponseBody>

    @PATCH("rest/v1/app_settings")
    suspend fun updateSetting(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("key") keyFilter: String,
        @Body update: Map<String, String>
    ): Response<ResponseBody>

    @GET("rest/v1/bookings")
    suspend fun getBookingsByStatus(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("status") statusFilter: String,
        @Query("select") select: String = "*",
        @Query("order") order: String = "timestamp.desc"
    ): Response<List<SupabaseBooking>>

    @GET("rest/v1/profiles")
    suspend fun getAllProfiles(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("select") select: String = "*"
    ): Response<List<SupabaseProfile>>
}

object SupabaseClient {
    private const val TAG = "SupabaseClient"

    private val moshi = Moshi.Builder().build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    fun getSupabaseUrl(): String {
        // Safe access builder
        var url = try {
            BuildConfig.SUPABASE_URL
        } catch (e: Exception) {
            ""
        }
        url = url.trim().replace("\"", "").replace("'", "")
        
        if (url.isBlank() || url == "YOUR_SUPABASE_URL") {
            return "https://aaxzcqoewjagcdlpqngu.supabase.co/"
        }
        
        // If they just entered 'aaxzcqoewjagcdlpqngu' or 'aaxzcqoewjagcdlapqngu' or any other project reference ID
        if (!url.contains(".") && !url.contains("/")) {
            // It's a pure project ID, format it correctly
            return "https://$url.supabase.co/"
        }
        
        // Let's also fix the typo in case they entered the misspelled lapqngu inside configured url
        if (url.contains("aaxzcqoewjagcdlapqngu")) {
            url = url.replace("aaxzcqoewjagcdlapqngu", "aaxzcqoewjagcdlpqngu")
        }

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://$url"
        }
        
        return if (url.endsWith("/")) url else "$url/"
    }

    fun getSupabaseKey(): String {
        var key = try {
            BuildConfig.SUPABASE_KEY
        } catch (e: Exception) {
            ""
        }
        key = key.trim().replace("\"", "").replace("'", "")
        return if (key.isBlank() || key == "YOUR_SUPABASE_KEY") {
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImFheHpjcW9ld2phZ2NkbHBxbmd1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODAzNTM0NDcsImV4cCI6MjA5NTkyOTQ0N30.XvncGIsV6WMt4oOQ1YG__iS2rY4T_Gi0js3pyOTaeLU"
        } else {
            key
        }
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(getSupabaseUrl())
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    val service: SupabaseApiService by lazy {
        retrofit.create(SupabaseApiService::class.java)
    }

    fun isConfigured(): Boolean {
        val url = getSupabaseUrl()
        val key = getSupabaseKey()
        return url.isNotEmpty() && url.contains("supabase.co") && key.isNotEmpty()
    }
}
