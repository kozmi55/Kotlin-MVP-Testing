package com.example.tamaskozmer.kotlinrxexample.presentation.presenters

import com.example.tamaskozmer.kotlinrxexample.domain.interactors.GetUsers
import com.example.tamaskozmer.kotlinrxexample.presentation.view.UserListView
import com.example.tamaskozmer.kotlinrxexample.presentation.view.viewmodels.UserViewModel
import com.example.tamaskozmer.kotlinrxexample.testutil.TestSchedulerProvider
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.anyBoolean
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import java.util.concurrent.TimeUnit
import org.mockito.Mockito.`when` as whenever

/**
 * Created by Tamas_Kozmer on 7/21/2017.
 */
class UserListPresenterTest {

    @Mock
    lateinit var mockGetUsers: GetUsers

    @Mock
    lateinit var mockView: UserListView

    lateinit var userListPresenter: UserListPresenter

    lateinit var testSchedulerProvider: TestSchedulerProvider

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        testSchedulerProvider = TestSchedulerProvider()
        userListPresenter = UserListPresenter(mockGetUsers, testSchedulerProvider)
    }

    @Test
    fun testGetUsers_errorCase_showError() {
        // Given
        val error = "Test error"
        val single: Single<List<UserViewModel>> = Single.create {
            emitter ->
            emitter.onError(Exception(error))
        }

        // When
        whenever(mockGetUsers.execute(anyInt(), anyBoolean())).thenReturn(single)

        userListPresenter.attachView(mockView)
        userListPresenter.getUsers()

        testSchedulerProvider.testScheduler.triggerActions()

        // Then
        verify(mockView).hideLoading()
        verify(mockView).showEmptyListError()
    }

    @Test
    fun testGetUsers_successCaseFirstPage_clearList() {
        // Given
        val users = listOf(UserViewModel(1, "Name", 1000, ""))
        val single: Single<List<UserViewModel>> = Single.create {
            emitter ->
            emitter.onSuccess(users)
        }

        // When
        whenever(mockGetUsers.execute(anyInt(), anyBoolean())).thenReturn(single)

        userListPresenter.attachView(mockView)
        userListPresenter.getUsers()

        testSchedulerProvider.testScheduler.triggerActions()

        // Then
        verify(mockView).clearList()
    }

    @Test
    fun testGetUsers_successCaseMultipleTimes_clearListOnlyOnce() {
        // Given
        val users = listOf(UserViewModel(1, "Name", 1000, ""))
        val single: Single<List<UserViewModel>> = Single.create {
            emitter ->
            emitter.onSuccess(users)
        }

        // When
        whenever(mockGetUsers.execute(anyInt(), anyBoolean())).thenReturn(single)

        userListPresenter.attachView(mockView)
        userListPresenter.getUsers()
        userListPresenter.getUsers()

        testSchedulerProvider.testScheduler.triggerActions()

        // Then
        verify(mockView).clearList()
        verify(mockView, times(2)).hideLoading()
        verify(mockView, times(2)).addUsersToList(users)
    }

    @Test
    fun testGetUsers_forcedSuccessCaseMultipleTimes_clearListEveryTime() {
        // Given
        val users = listOf(UserViewModel(1, "Name", 1000, ""))
        val single: Single<List<UserViewModel>> = Single.create {
            emitter ->
            emitter.onSuccess(users)
        }

        // When
        whenever(mockGetUsers.execute(anyInt(), anyBoolean())).thenReturn(single)

        userListPresenter.attachView(mockView)
        userListPresenter.getUsers(forced = true)
        userListPresenter.getUsers(forced = true)

        testSchedulerProvider.testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        // Then
        verify(mockView, times(2)).clearList()
        verify(mockView, times(2)).hideLoading()
        verify(mockView, times(2)).addUsersToList(users)
    }

    @Test
    fun testOnScrollChanged_offsetReachedAndLoading_dontRequestNextPage() {
        callOnScrollChanged(5, 1)

        // Then
        verify(mockGetUsers, times(1))
                .execute(ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean())
    }

    @Test
    fun testOnScrollChanged_offsetReachedAndNotLoading_requestNextPage() {
        callOnScrollChanged(5, 3)

        // Then
        verify(mockGetUsers, times(2))
                .execute(ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean())
    }

    @Test
    fun testOnScrollChanged_lastItemReachedAndLoading_showLoading() {
        callOnScrollChanged(10, 1)

        // Then
        verify(mockView).showLoading()
    }

    private fun callOnScrollChanged(lastVisibleItemPosition: Int, secondsDelay: Long) {
        getUsersWithLoadingDelay()
        testSchedulerProvider.testScheduler.advanceTimeBy(secondsDelay, TimeUnit.SECONDS)

        userListPresenter.onScrollChanged(lastVisibleItemPosition, 10)
    }

    private fun getUsersWithLoadingDelay() {
        // Given
        val users = listOf(UserViewModel(1, "Name", 1000, ""))
        val single: Single<List<UserViewModel>> = Single.create {
            emitter ->
            emitter.onSuccess(users)
        }

        val delayedSingle = single.delay(2, TimeUnit.SECONDS, testSchedulerProvider.testScheduler)

        // When
        Mockito.`when`(mockGetUsers.execute(anyInt(), anyBoolean())).thenReturn(delayedSingle)

        userListPresenter.attachView(mockView)
        userListPresenter.getUsers()
    }

}