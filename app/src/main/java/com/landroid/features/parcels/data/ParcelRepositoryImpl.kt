package com.landroid.features.parcels.data

import com.landroid.core.network.LandroidApiService
import com.landroid.shared.models.Parcel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ParcelRepositoryImpl @Inject constructor(
    private val apiService: LandroidApiService
) : ParcelRepository {

    override suspend fun getParcels(): Result<List<Parcel>> = withContext(Dispatchers.IO) {
        runCatching {
            apiService.getParcels()
        }
    }

    override suspend fun getParcelById(id: String): Result<Parcel> = withContext(Dispatchers.IO) {
        runCatching {
            apiService.getParcel(id)
        }
    }

    override suspend fun createParcel(parcel: Parcel): Result<Parcel> = withContext(Dispatchers.IO) {
        runCatching {
            apiService.createParcel(parcel)
        }
    }

    override suspend fun updateParcel(parcel: Parcel): Result<Parcel> = withContext(Dispatchers.IO) {
        runCatching {
            // Local FastAPI endpoint doesn't explicitly have an update route yet,
            // mocking the success case.
            parcel
        }
    }

    override suspend fun deleteParcel(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            // Mock delete case
            Unit
        }
    }
}
