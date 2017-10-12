package com.example.tamaskozmer.kotlinrxexample.model.repositories

import com.example.tamaskozmer.kotlinrxexample.model.entities.User
import com.example.tamaskozmer.kotlinrxexample.model.entities.UserListModel
import com.example.tamaskozmer.kotlinrxexample.model.persistence.daos.UserDao
import com.example.tamaskozmer.kotlinrxexample.model.services.UserService
import com.example.tamaskozmer.kotlinrxexample.util.CalendarWrapper
import com.example.tamaskozmer.kotlinrxexample.util.ConnectionHelper
import com.example.tamaskozmer.kotlinrxexample.util.PreferencesHelper
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import retrofit2.Call
import retrofit2.Response
import org.mockito.Mockito.`when` as whenever

/**
 * Created by Tamas_Kozmer on 7/24/2017.
 */
class UserRepositoryTest {

    @Mock
    lateinit var mockUserService: UserService

    @Mock
    lateinit var mockUserDao: UserDao

    @Mock
    lateinit var mockConnectionHelper: ConnectionHelper

    @Mock
    lateinit var mockPreferencesHelper: PreferencesHelper

    @Mock
    lateinit var mockCalendarWrapper: CalendarWrapper

    @Mock
    lateinit var mockUserCall: Call<UserListModel>

    @Mock
    lateinit var mockUserResponse: Response<UserListModel>

    lateinit var userRepository: UserRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        userRepository = DefaultUserRepository(mockUserService, mockUserDao, mockConnectionHelper, mockPreferencesHelper, mockCalendarWrapper)
    }

    @Test
    fun testGetUsers_isOnlineReceivedOneItem_emitListWithOneItem() {
        val userListModel = UserListModel(listOf(User()))
        setUpStubbing(true, 1000 * 60 * 60 * 12 + 1, 0, modelFromUserService = userListModel)

        val testObserver = userRepository.getUsers(1, false).test()

        testObserver.assertNoErrors()
        testObserver.assertValue { userListModelResult: UserListModel -> userListModelResult.items.size == 1 }
        verify(mockUserDao).insertAll(userListModel.items)
    }

    @Test
    fun testGetUsers_isOfflineOneItemInDatabase_emitListWithOneItem() {
        val modelFromDatabase = listOf(User())
        setUpStubbing(false, 1000 * 60 * 60 * 12 + 1, 0, modelFromDatabase = modelFromDatabase)

        val testObserver = userRepository.getUsers(1, false).test()

        testObserver.assertNoErrors()
        testObserver.assertValue { userListModelResult: UserListModel -> userListModelResult.items.size == 1 }
    }

    private fun setUpStubbing(isOnline: Boolean, currentTime: Long, lastUpdateTime: Long,
                              modelFromUserService: UserListModel = UserListModel(emptyList()),
                              modelFromDatabase: List<User> = emptyList()) {
        whenever(mockConnectionHelper.isOnline()).thenReturn(isOnline)
        whenever(mockCalendarWrapper.getCurrentTimeInMillis()).thenReturn(currentTime)
        whenever(mockPreferencesHelper.loadLong("last_update_page_1")).thenReturn(lastUpdateTime)

        whenever(mockUserService.getUsers(1)).thenReturn(mockUserCall)
        whenever(mockUserCall.execute()).thenReturn(mockUserResponse)
        whenever(mockUserResponse.body()).thenReturn(modelFromUserService)
        whenever(mockUserDao.getUsers(1)).thenReturn(modelFromDatabase)
    }
}