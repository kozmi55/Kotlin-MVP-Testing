package com.example.tamaskozmer.kotlinrxexample.domain.interactors

import com.example.tamaskozmer.kotlinrxexample.model.entities.User
import com.example.tamaskozmer.kotlinrxexample.model.entities.UserListModel
import com.example.tamaskozmer.kotlinrxexample.model.repositories.UserRepository
import com.example.tamaskozmer.kotlinrxexample.presentation.view.viewmodels.UserViewModel
import io.reactivex.Single
import io.reactivex.SingleEmitter
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.Mockito.`when` as whenever

/**
 * Created by Tamas_Kozmer on 7/24/2017.
 */
class GetUsersTest {

    @Mock
    lateinit var mockUserRepository: UserRepository

    lateinit var getUsers: GetUsers

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        getUsers = GetUsers(mockUserRepository)
    }

    @Test
    fun testExecute_userListModelWithOneItem_emitListWithOneViewModel() {
        val userListModel = UserListModel(listOf(User(1, "Name", 100, "Image url")))
        setUpStubbing(userListModel)

        val testObserver = getUsers.execute(1, false).test()

        testObserver.assertNoErrors()
        testObserver.assertValue { userViewModels: List<UserViewModel> -> userViewModels.size == 1 }
        testObserver.assertValue { userViewModels: List<UserViewModel> ->
            userViewModels.get(0) == UserViewModel(1, "Name", 100, "Image url") }
    }

    @Test
    fun testExecute_userListModelEmpty_emitEmptyList() {
        val userListModel = UserListModel(emptyList())
        setUpStubbing(userListModel)

        val testObserver = getUsers.execute(1, false).test()

        testObserver.assertNoErrors()
        testObserver.assertValue { userViewModels: List<UserViewModel> -> userViewModels.isEmpty() }
    }

    private fun setUpStubbing(userListModel: UserListModel) {
        val mockSingle = Single.create { e: SingleEmitter<UserListModel>? ->
            e?.onSuccess(userListModel) }

        whenever(mockUserRepository.getUsers(1, false))
                .thenReturn(mockSingle)
    }
}