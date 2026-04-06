// app/src/main/java/com/landroid/features/tree_count/presentation/TreeCountViewModel.kt
package com.landroid.features.tree_count.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.landroid.features.parcels.data.ParcelRepository
import com.landroid.shared.models.Canopy
import com.landroid.shared.models.Parcel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.opencv.core.MatOfKeyPoint
import org.opencv.core.Size
import org.opencv.features2d.SimpleBlobDetector
import org.opencv.features2d.SimpleBlobDetector_Params
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import javax.inject.Inject

enum class ViewType { MARKERS, HEATMAP }

data class TreeCountUiState(
    val parcel: Parcel? = null,
    val canopies: List<Canopy> = emptyList(),
    val totalCount: Int = 0,
    val stressedCount: Int = 0,
    val previousTotalCount: Int? = null,
    val previousStressedCount: Int? = null,
    val surveyDate: String? = null,
    val previousSurveyDate: String? = null,
    val densityPerAcre: Float = 0f,
    val confidence: Int = 79,
    val viewType: ViewType = ViewType.MARKERS,
    val isLoading: Boolean = false,
    val isAnalyzing: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class TreeCountViewModel @Inject constructor(
    private val repository: ParcelRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TreeCountUiState())
    val state = _state.asStateFlow()

    // Affine transform constants — set from GeoTIFF metadata in production
    private var pixelToLatScale = -0.0001
    private var pixelToLngScale = 0.0001
    private var originLat = 13.0
    private var originLng = 77.0

    fun load(parcelId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            val parcelResult = repository.getParcelById(parcelId)
            val insightsResult = repository.getInsights(parcelId)
            
            parcelResult.fold(
                onSuccess = { parcel ->
                    originLat = parcel.centroidLat
                    originLng = parcel.centroidLng
                    
                    val treeData = insightsResult.getOrNull()?.treeCount
                    val canopies = treeData?.canopies?.map { it.toCanopy() } ?: emptyList()
                    val totalTrees = treeData?.totalCount ?: 0
                    val stressed = treeData?.stressedCount ?: 0
                    val density = treeData?.densityPerAcre ?: 0f

                    _state.update {
                        it.copy(
                            parcel = parcel,
                            canopies = canopies,
                            totalCount = totalTrees,
                            stressedCount = stressed,
                            densityPerAcre = density,
                            previousTotalCount = treeData?.previousTotalCount,
                            previousStressedCount = treeData?.previousStressedCount,
                            surveyDate = treeData?.surveyDate,
                            previousSurveyDate = treeData?.previousSurveyDate,
                            isLoading = false
                        )
                    }
                },
                onFailure = { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
            )
        }
    }

    fun analyzeOrthomosaic(localPath: String) {
        viewModelScope.launch {
            _state.update { it.copy(isAnalyzing = true) }
            withContext(Dispatchers.IO) {
                runCatching {
                    val mat = Imgcodecs.imread(localPath)
                    val grayMat = org.opencv.core.Mat()
                    Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_BGR2GRAY)

                    val params = SimpleBlobDetector_Params().apply {
                        set_minArea(20f)
                        set_maxArea(500f)
                        set_filterByArea(true)
                        set_filterByCircularity(true)
                        set_minCircularity(0.3f)
                        set_filterByConvexity(false)
                        set_filterByInertia(false)
                    }

                    val detector = SimpleBlobDetector.create(params)
                    val keyPoints = MatOfKeyPoint()
                    detector.detect(grayMat, keyPoints)

                    val kpList = keyPoints.toList()
                    val sizes = kpList.map { it.size }.sorted()
                    val median = if (sizes.isNotEmpty()) sizes[sizes.size / 2] else 10f

                    val canopies = kpList.map { kp ->
                        Canopy(
                            lat = latFromPixel(kp.pt.y),
                            lng = lngFromPixel(kp.pt.x),
                            radiusMeters = kp.size / 2f,
                            isStressed = kp.size < median * 0.6f
                        )
                    }

                    val confidence = computeConfidence(mat.cols(), mat.rows())
                    val stressed = canopies.count { it.isStressed }
                    val parcel = _state.value.parcel

                    _state.update {
                        it.copy(
                            canopies = canopies,
                            totalCount = canopies.size,
                            stressedCount = stressed,
                            densityPerAcre = parcel?.let { p -> canopies.size / p.areaAcres.toFloat() } ?: 0f,
                            confidence = confidence,
                            isAnalyzing = false
                        )
                    }

                    mat.release()
                    grayMat.release()
                }.onFailure { e ->
                    _state.update { it.copy(isAnalyzing = false, error = e.message) }
                }
            }
        }
    }

    fun setViewType(type: ViewType) {
        _state.update { it.copy(viewType = type) }
    }

    private fun latFromPixel(y: Double): Double = originLat + y * pixelToLatScale
    private fun lngFromPixel(x: Double): Double = originLng + x * pixelToLngScale

    private fun computeConfidence(width: Int, height: Int): Int {
        val resolution = width.toLong() * height.toLong()
        return when {
            resolution > 10_000_000L -> 87
            resolution > 4_000_000L  -> 79
            resolution > 1_000_000L  -> 68
            else -> 55
        }
    }


}
