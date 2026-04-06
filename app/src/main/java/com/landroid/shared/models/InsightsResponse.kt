// app/src/main/java/com/landroid/shared/models/InsightsResponse.kt
package com.landroid.shared.models

data class InsightsResponse(
    val plantZones: List<NdviZoneDto>,
    val treeCount: TreeCountDto
)

data class NdviZoneDto(
    val id: String,
    val colorHex: String,
    val minNdvi: Float,
    val maxNdvi: Float,
    val areaPercent: Float,
    val previousAreaPercent: Float? = null
) {
    fun toNdviZone(): NdviZone {
        return NdviZone(
            id = id,
            color = androidx.compose.ui.graphics.Color(
                android.graphics.Color.parseColor(colorHex)
            ),
            min = minNdvi,
            max = maxNdvi,
            areaPercent = areaPercent
        )
    }
}

data class TreeCountDto(
    val canopies: List<CanopyDto>,
    val totalCount: Int,
    val stressedCount: Int,
    val densityPerAcre: Float,
    val previousTotalCount: Int? = null,
    val previousStressedCount: Int? = null,
    val surveyDate: String? = null,
    val previousSurveyDate: String? = null
)

data class CanopyDto(
    val lat: Double,
    val lng: Double,
    val radiusMeters: Float,
    val isStressed: Boolean
) {
    fun toCanopy(): Canopy {
        return Canopy(
            lat = lat,
            lng = lng,
            radiusMeters = radiusMeters,
            isStressed = isStressed
        )
    }
}
