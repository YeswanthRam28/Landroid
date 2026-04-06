// app/src/main/java/com/landroid/core/network/ApiEndpoints.kt
package com.landroid.core.network

object ApiEndpoints {
    const val OVERPASS_API = "https://overpass-api.de/api/interpreter"
    const val NOMINATIM_API = "https://nominatim.openstreetmap.org/"
    const val OPEN_METEO_API = "https://api.open-meteo.com/v1/"
    const val PLANETARY_COMPUTER_BASE = "https://planetarycomputer.microsoft.com/api/stac/v1/"
    const val SENTINEL2_COLLECTION = "sentinel-2-l2a"
    const val CHIRPS_COLLECTION = "chirps"
    const val ERA5_COLLECTION = "era5-pds"
    const val VIIRS_COLLECTION = "viirs-nightfire"
}

// Response wrappers
data class OverpassResponse(
    val elements: List<OverpassElement>
)

data class OverpassElement(
    val id: Long,
    val type: String,
    val lat: Double? = null,
    val lon: Double? = null,
    val tags: Map<String, String>? = null
)

data class NominatimResponse(
    val place_id: Long,
    val display_name: String,
    val lat: String,
    val lon: String,
    val boundingbox: List<String>
)

data class SoilResponse(
    val properties: Map<String, Any>
)

data class StacSearchRequest(
    val collections: List<String>,
    val bbox: List<Double>,
    val datetime: String,
    val limit: Int = 10
)

data class StacSearchResponse(
    val features: List<StacFeature>
)

data class StacFeature(
    val id: String,
    val properties: Map<String, Any>,
    val assets: Map<String, StacAsset>
)

data class StacAsset(
    val href: String,
    val type: String
)
