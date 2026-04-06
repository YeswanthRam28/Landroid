// app/src/main/java/com/landroid/features/documents/presentation/DocumentViewModel.kt
package com.landroid.features.documents.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class DocumentItem(
    val name: String,
    val sizeKb: Long,
    val uploadedAt: Long,
    val downloadUrl: String,
    val type: DocumentType
)

enum class DocumentType { PDF, IMAGE, GEOTIFF, OTHER }

data class DocumentUiState(
    val documents: List<DocumentItem> = emptyList(),
    val isLoading: Boolean = false,
    val uploadProgress: Float? = null,
    val error: String? = null
)

@HiltViewModel
class DocumentViewModel @Inject constructor(
    private val storage: FirebaseStorage
) : ViewModel() {

    private val _state = MutableStateFlow(DocumentUiState())
    val state = _state.asStateFlow()

    fun loadDocuments(parcelId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            runCatching {
                val listResult = storage.reference.child("parcels/$parcelId/docs").listAll().await()
                val docs = listResult.items.map { ref ->
                    val meta = ref.metadata.await()
                    val url = ref.downloadUrl.await().toString()
                    DocumentItem(
                        name = ref.name,
                        sizeKb = (meta.sizeBytes / 1024),
                        uploadedAt = meta.updatedTimeMillis,
                        downloadUrl = url,
                        type = when {
                            ref.name.endsWith(".pdf", true)  -> DocumentType.PDF
                            ref.name.endsWith(".tif", true) || ref.name.endsWith(".tiff", true) -> DocumentType.GEOTIFF
                            ref.name.endsWith(".jpg", true) || ref.name.endsWith(".png", true)  -> DocumentType.IMAGE
                            else -> DocumentType.OTHER
                        }
                    )
                }
                _state.update { it.copy(documents = docs, isLoading = false) }
            }.onFailure { e ->
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
