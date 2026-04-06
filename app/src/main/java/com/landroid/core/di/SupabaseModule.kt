// app/src/main/java/com/landroid/core/di/SupabaseModule.kt
package com.landroid.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SupabaseModule {

    private val SUPABASE_URL = com.landroid.BuildConfig.SUPABASE_URL
    private val SUPABASE_ANON_KEY = com.landroid.BuildConfig.SUPABASE_ANON_KEY

    private val supabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_ANON_KEY
        ) {
            install(Auth)
            install(Postgrest)
            install(Storage)
        }
    }

    @Provides
    @Singleton
    fun provideAuth(): Auth = supabaseClient.pluginManager.getPlugin(Auth)

    @Provides
    @Singleton
    fun providePostgrest(): Postgrest = supabaseClient.pluginManager.getPlugin(Postgrest)

    @Provides
    @Singleton
    fun provideStorage(): Storage = supabaseClient.pluginManager.getPlugin(Storage)
}
