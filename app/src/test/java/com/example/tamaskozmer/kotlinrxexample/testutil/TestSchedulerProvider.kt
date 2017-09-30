package com.example.tamaskozmer.kotlinrxexample.testutil

import com.example.tamaskozmer.kotlinrxexample.util.SchedulerProvider
import io.reactivex.schedulers.TestScheduler

/**
 * Created by Tamas_Kozmer on 9/21/2017.
 */
class TestSchedulerProvider() : SchedulerProvider {

    val testScheduler: TestScheduler = TestScheduler()

    override fun uiScheduler() = testScheduler
    override fun ioScheduler() = testScheduler
}