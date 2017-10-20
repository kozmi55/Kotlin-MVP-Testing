package com.example.tamaskozmer.kotlinrxexample.fakes.model.repositories

import com.example.tamaskozmer.kotlinrxexample.model.entities.User
import com.example.tamaskozmer.kotlinrxexample.model.entities.UserListModel
import io.reactivex.Single
import io.reactivex.SingleEmitter

/**
 * Created by Tamas_Kozmer on 8/8/2017.
 */
class FakeUserRepository {

    fun getUsers(page: Int, forced: Boolean): Single<UserListModel> {
        val users = (1..10L).map {
            val number = (page - 1) * 10 + it
            User(it, "User $number", number * 100, "")
        }

        return Single.create<UserListModel> { emitter: SingleEmitter<UserListModel> ->
            val userListModel = UserListModel(users)
            emitter.onSuccess(userListModel)
        }
    }
}