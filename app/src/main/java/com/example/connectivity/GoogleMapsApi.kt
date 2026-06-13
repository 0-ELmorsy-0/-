package com.example.connectivity

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class LatLngLiteral(
    val lat: Double,
    val lng: Double
)

@JsonClass(generateAdapter = true)
data class GeocodeGeometry(
    val location: LatLngLiteral?
)

@JsonClass(generateAdapter = true)
data class GeocodeResult(
    @Json(name = "formatted_address") val formattedAddress: String?,
    val geometry: GeocodeGeometry?
)

@JsonClass(generateAdapter = true)
data class GeocodeResponse(
    val status: String,
    val results: List<GeocodeResult>?
)

@JsonClass(generateAdapter = true)
data class AutocompletePrediction(
    val description: String,
    @Json(name = "place_id") val placeId: String?
)

@JsonClass(generateAdapter = true)
data class AutocompleteResponse(
    val status: String,
    val predictions: List<AutocompletePrediction>?
)

interface GoogleMapsApiService {
    @GET("maps/api/geocode/json")
    suspend fun reverseGeocode(
        @Query("latlng") latlng: String,
        @Query("key") apiKey: String,
        @Query("language") language: String = "ar"
    ): Response<GeocodeResponse>

    @GET("maps/api/place/autocomplete/json")
    suspend fun autocomplete(
        @Query("input") input: String,
        @Query("key") apiKey: String,
        @Query("language") language: String = "ar",
        @Query("components") components: String = "country:eg"
    ): Response<AutocompleteResponse>
}

object GoogleMapsClient {
    private val moshi = Moshi.Builder()
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://maps.googleapis.com/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val service: GoogleMapsApiService = retrofit.create(GoogleMapsApiService::class.java)

    /**
     * Retrieves the Google Maps API Key injected via secrets-gradle-plugin.
     */
    fun getApiKey(): String {
        val key = BuildConfig.MAPS_API_KEY
        return if (key == "YOUR_MAPS_API_KEY" || key.isBlank()) "" else key
    }

    /**
     * True if a valid-looking Google Maps API key has been configured.
     */
    fun isKeyConfigured(): Boolean {
        val key = getApiKey()
        return key.isNotEmpty() && key != "YOUR_MAPS_API_KEY"
    }
}
