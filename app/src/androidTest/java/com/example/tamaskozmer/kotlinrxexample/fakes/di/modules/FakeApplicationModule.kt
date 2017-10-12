package com.example.tamaskozmer.kotlinrxexample.fakes.di.modules

import com.example.tamaskozmer.kotlinrxexample.fakes.model.repositories.FakeUserRepository
import com.example.tamaskozmer.kotlinrxexample.model.repositories.UserRepository
import com.example.tamaskozmer.kotlinrxexample.util.AppSchedulerProvider
import com.example.tamaskozmer.kotlinrxexample.util.SchedulerProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Tamas_Kozmer on 8/8/2017.
 */
@Module
class FakeApplicationModule {

    @Provides
    @Singleton
    fun provideUserRepository() : UserRepository {
        return FakeUserRepository()
    }

    @Provides
    @Singleton
    fun provideSchedulerProvider() : SchedulerProvider = AppSchedulerProvider()
}