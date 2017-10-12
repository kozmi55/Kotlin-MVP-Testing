package com.example.tamaskozmer.kotlinrxexample.fakes.di.components

import com.example.tamaskozmer.kotlinrxexample.di.components.ApplicationComponent
import com.example.tamaskozmer.kotlinrxexample.fakes.di.modules.FakeApplicationModule
import dagger.Component
import javax.inject.Singleton

/**
 * Created by Tamas_Kozmer on 8/8/2017.
 */
@Singleton
@Component(modules = arrayOf(FakeApplicationModule::class))
interface FakeApplicationComponent : ApplicationComponent