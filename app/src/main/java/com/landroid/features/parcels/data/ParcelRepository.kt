// app/src/main/java/com/landroid/features/parcels/data/ParcelRepository.kt
package com.landroid.features.parcels.data

import com.landroid.shared.models.Parcel

interface ParcelRepository {
    suspend fun getParcels(): Result<List<Parcel>>
    suspend fun getParcelById(id: String): Result<Parcel>
    suspend fun createParcel(parcel: Parcel): Result<Parcel>
    suspend fun updateParcel(parcel: Parcel): Result<Parcel>
    suspend fun deleteParcel(id: String): Result<Unit>
}
