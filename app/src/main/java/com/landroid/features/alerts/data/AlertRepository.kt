// app/src/main/java/com/landroid/features/alerts/data/AlertRepository.kt
package com.landroid.features.alerts.data

import com.landroid.features.alerts.data.db.AlertDao
import com.landroid.shared.models.Alert
import com.landroid.shared.models.AlertCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface AlertRepository {
    fun getAllAlerts(): Flow<List<Alert>>
    fun getAlertsByCategory(category: AlertCategory): Flow<List<Alert>>
    suspend fun insert(alert: Alert)
    suspend fun markAsRead(id: String)
    suspend fun markAllAsRead()
    fun getUnreadCount(): Flow<Int>
}

class AlertRepositoryImpl @Inject constructor(
    private val dao: AlertDao
) : AlertRepository {
    override fun getAllAlerts() = dao.getAllAlerts()
    override fun getAlertsByCategory(category: AlertCategory) =
        dao.getAlertsByCategory(category.name)
    override suspend fun insert(alert: Alert) = dao.insert(alert)
    override suspend fun markAsRead(id: String) = dao.markAsRead(id)
    override suspend fun markAllAsRead() = dao.markAllAsRead()
    override fun getUnreadCount() = dao.getUnreadCount()
}
