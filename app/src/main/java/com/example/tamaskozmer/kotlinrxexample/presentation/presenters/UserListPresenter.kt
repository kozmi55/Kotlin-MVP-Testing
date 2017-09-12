package com.example.tamaskozmer.kotlinrxexample.presentation.presenters

import com.example.tamaskozmer.kotlinrxexample.domain.interactors.GetUsers
import com.example.tamaskozmer.kotlinrxexample.presentation.view.UserListView
import com.example.tamaskozmer.kotlinrxexample.presentation.view.viewmodels.UserViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Tamas_Kozmer on 7/4/2017.
 */
class UserListPresenter(
        private val getUsers: GetUsers) : BasePresenter<UserListView>() {

    private val offset = 5

    private var page = 1
    private var loading = false

    fun getUsers(forced: Boolean = false) {
        loading = true
        val pageToRequest = if (forced) 1 else page
        getUsers.execute(pageToRequest, forced)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { users -> handleSuccess(forced, users) },
                        { handleError() })
    }

    private fun handleSuccess(forced: Boolean, users: List<UserViewModel>) {
        loading = false
        if (forced) {
            page = 1
        }
        if (page == 1) {
            view?.clearList()
            view?.hideEmptyListError()
        }
        view?.addUsersToList(users)
        view?.hideLoading()
        page++
    }

    private fun handleError() {
        loading = false
        view?.hideLoading()
        if (page == 1) {
            view?.showEmptyListError()
        } else {
            view?.showToastError()
        }
    }

    fun onScrollChanged(lastVisibleItemPosition: Int, totalItemCount: Int) {
        val shouldGetNextPage = !loading && lastVisibleItemPosition >= totalItemCount - offset
        if (shouldGetNextPage) {
            getUsers()
        }

        if (loading && lastVisibleItemPosition >= totalItemCount) {
            view?.showLoading()
        }
    }
}