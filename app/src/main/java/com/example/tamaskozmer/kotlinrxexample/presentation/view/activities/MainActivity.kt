package com.example.tamaskozmer.kotlinrxexample.view.activities

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import com.example.tamaskozmer.kotlinrxexample.R
import com.example.tamaskozmer.kotlinrxexample.di.modules.UserListFragmentModule
import com.example.tamaskozmer.kotlinrxexample.presentation.presenters.UserListPresenter
import com.example.tamaskozmer.kotlinrxexample.presentation.view.UserListView
import com.example.tamaskozmer.kotlinrxexample.presentation.view.viewmodels.UserViewModel
import com.example.tamaskozmer.kotlinrxexample.util.customApplication
import com.example.tamaskozmer.kotlinrxexample.view.adapters.UserListAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), UserListView {

    private val presenter: UserListPresenter by lazy { component.presenter() }
    private val component by lazy { customApplication.component.plus(UserListFragmentModule()) }
    private val adapter by lazy {
        val userList = mutableListOf<UserViewModel>()
        UserListAdapter(userList) {
            user -> showUserClickedSnackbar(user)
        }
    }

    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        initAdapter()

        presenter.attachView(this)

        showLoading()
        presenter.getUsers()
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    private fun initViews() {
        swipeRefreshLayout.setOnRefreshListener {
            presenter.getUsers(forced = true)
        }
    }

    // region View interface methods
    override fun showLoading() {
        swipeRefreshLayout.isRefreshing = true
    }

    override fun hideLoading() {
        swipeRefreshLayout.isRefreshing = false
    }

    override fun addUsersToList(users: List<UserViewModel>) {
        val adapter = recyclerView.adapter as UserListAdapter
        adapter.addUsers(users)
    }

    override fun showEmptyListError() {
        errorView.visibility = View.VISIBLE
    }

    override fun hideEmptyListError() {
        errorView.visibility = View.GONE
    }

    override fun showToastError() {
        Toast.makeText(this, "Error loading data", Toast.LENGTH_SHORT).show()
    }

    override fun clearList() {
        adapter.clearUsers()
    }
    // endregion

    private fun initAdapter() {
        layoutManager = LinearLayoutManager(customApplication)
        recyclerView.layoutManager = layoutManager

        recyclerView.adapter = adapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {

                val lastVisibleItemPosition = layoutManager.findFirstVisibleItemPosition() + layoutManager.childCount
                val totalItemCount = layoutManager.itemCount

                presenter.onScrollChanged(lastVisibleItemPosition, totalItemCount)
            }
        })
    }

    private fun showUserClickedSnackbar(user: UserViewModel) {
        Snackbar.make(recyclerView, "${user.displayName}: ${user.reputation} pts", Snackbar.LENGTH_SHORT)
                .show()
    }
}
