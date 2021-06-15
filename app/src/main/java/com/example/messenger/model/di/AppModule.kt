package com.example.messenger.model.di

import android.app.Application
import com.example.messenger.model.Repository
import com.example.messenger.model.adapters.MessageListAdapter
import com.example.messenger.model.adapters.UserListAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideUserListAdapter() = UserListAdapter()

    @Singleton
    @Provides
    fun provideMessageListAdapter() = MessageListAdapter()

    @Singleton
    @Provides
    fun provideRepository(
        application: Application
    ) = Repository(application)
}