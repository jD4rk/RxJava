package it.jdark.android.example.rxjava.java

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import it.jdark.android.example.rxjava.R
import it.jdark.android.example.rxjava.java.adapter.MyRecyclerViewAdapter
import it.jdark.android.example.rxjava.java.model.GitHubRepo
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), View.OnClickListener {
    lateinit var adapter: MyRecyclerViewAdapter
    lateinit var searchText: EditText

    private val TAG = javaClass.simpleName

    private var subscription: CompositeDisposable? = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar.setTitle(R.string.title)
        setSupportActionBar(toolbar)

        // Example of usage Kotlin Android Extension
        edit_text_username.setText("Test Kotlin Android Extendions")

        recycler_view.setHasFixedSize(true)
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        adapter = MyRecyclerViewAdapter()
        recycler_view.adapter = adapter

        searchText = findViewById(R.id.edit_text_username) as EditText
        val searchButton = findViewById(R.id.search_button) as Button

        searchButton.setOnClickListener(this)
    }

    override fun onDestroy() {
        subscription!!.dispose()
        super.onDestroy()
    }

    override fun onClick(v: View) {
        val userName = searchText.text.toString()
        if (!TextUtils.isEmpty(userName)) {
            // VERBOSE WAY
            Log.d(TAG, "onClick: ")
            subscription!!.add(mObservable(userName)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<List<GitHubRepo>>() {
                        override fun onComplete() {
                            Log.d(TAG, "onComplete: Finish!")
                        }

                        override fun onError(e: Throwable) {
                            Toast.makeText(applicationContext, "Error " + e.localizedMessage, Toast.LENGTH_SHORT).show()
                            (recycler_view.adapter as MyRecyclerViewAdapter).setGitHubRepos(null)
                        }

                        override fun onNext(value: List<GitHubRepo>) {
                            (recycler_view.adapter as MyRecyclerViewAdapter).setGitHubRepos(value)
                        }
                    }))

            // COMPACT WAY
            subscription!!.add(GitHubClient().getStarredRepos(userName)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(this::handleResponse, this::handleError))

        }

        // Hide soft keyboard after click
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)

    }

    private fun mObservable(userName: String): Observable<List<GitHubRepo>> {
        return Observable.defer { GitHubClient().getStarredRepos(userName) }
    }

    private fun handleError(error: Throwable) {
        Toast.makeText(this, "Error " + error.localizedMessage, Toast.LENGTH_SHORT).show()
    }

    private fun handleResponse(response: List<GitHubRepo>) {
        (recycler_view.adapter as MyRecyclerViewAdapter).setGitHubRepos(response)
    }
}
