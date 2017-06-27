package it.jdark.android.example.rxjava.kotlin

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import it.jdark.android.example.rxjava.R
import it.jdark.android.example.rxjava.common.BaseActivity
import it.jdark.android.example.rxjava.kotlin.adapter.MyRecyclerViewAdapter
import it.jdark.android.example.rxjava.kotlin.model.GitHubRepo
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


class KotlinActivity : BaseActivity(), View.OnClickListener, EasyPermissions.PermissionCallbacks {
    lateinit var adapter: MyRecyclerViewAdapter

    internal val TAG = javaClass.simpleName

    private var subscription: CompositeDisposable = CompositeDisposable()


    val RC_LOCATION_AND_CAMERA = 124
    internal val perms = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar.setTitle(R.string.kotlin_title)
        setSupportActionBar(toolbar)

        // Example of usage Kotlin Android Extension
        edit_text_username.setText(getString(R.string.kotlin_android_extension_msg))

        recycler_view.setHasFixedSize(true)
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        adapter = MyRecyclerViewAdapter()
        recycler_view.adapter = adapter

        search_button.setOnClickListener(this)
    }

    override fun onDestroy() {
        subscription.dispose()
        super.onDestroy()
    }

    override fun onClick(v: View) {
        checkPermsionAndExecute()

    }

    private fun checkPermsionAndExecute() {
        if (EasyPermissions.hasPermissions(this, *perms)) {
            // Have permissions, do the thing!
            shortToast("Execute Call!")
            execution()
        } else {
            // Ask for both permissions
            // EasyPermissions.requestPermissions(this, getString(R.string.rationale_location_contacts), RC_LOCATION_AND_CAMERA, perms);
            EasyPermissions.requestPermissions(this,
                    getString(R.string.rationale_location_contacts),
                    R.string.rationale_ok, R.string.rationale_exit,
                    RC_LOCATION_AND_CAMERA, *perms)
        }
    }

    private fun execution() {
        var userName = edit_text_username.text.toString()
        if (!TextUtils.isEmpty(userName)) {
            // VERBOSE WAY
            debugLog("onClick: ")
//            subscription.add(mObservable(userName)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribeWith(object : DisposableObserver<List<GitHubRepo>>() {
//                        override fun onComplete() {
//                            debugLog("onComplete: Finish!")
//                        }
//
//                        override fun onError(e: Throwable) {
//                            shortToast("Error " + e.localizedMessage)
//                            (recycler_view.adapter as MyRecyclerViewAdapter).setGitHubRepos(null)
//                        }
//
//                        override fun onNext(value: List<GitHubRepo>) {
//                            (recycler_view.adapter as MyRecyclerViewAdapter).setGitHubRepos(value)
//                        }
//                    }))

            // COMPACT WAY
            subscription.add(mObservable(userName)
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
        shortToast("Error " + error.localizedMessage)
    }

    private fun handleResponse(response: List<GitHubRepo>) {
        (recycler_view.adapter as MyRecyclerViewAdapter).setGitHubRepos(response)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        debugLog("onActivityResult: Code -> " + requestCode)
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            if (EasyPermissions.somePermissionDenied(this, *perms)) {
                Log.d(TAG, "onActivityResult: EXIT because some permession aren't granted!")
                search_button.isEnabled = false
            }
            // Do something after user returned from app settings screen, like showing a Toast.
            //            Toast.makeText(this, R.string.returned_from_app_settings_to_activity, Toast.LENGTH_SHORT)
            //                    .show();
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        debugLog("onRequestPermissionsResult: Code -> " + requestCode)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(i: Int, list: List<String>) {
        debugLog("onPermissionsGranted:" + i + ":" + list.size + " -- " + list.toString())
    }

    override fun onPermissionsDenied(i: Int, list: List<String>) {
        Log.d(TAG, "onPermissionsDenied:" + i + ":" + list.size + " -- " + list.toString())

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, list)) {
            AppSettingsDialog.Builder(this).build().show()
        }

        // (Optional) Behaviour if any permission is not granted
        if (EasyPermissions.somePermissionDenied(this, *perms)) {
            shortToast("Permission denied: " + list.toString())
        }
    }


    // Some Kotlin Facilities
    fun Context.shortToast(message: CharSequence) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    fun Context.longToast(message: CharSequence) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()

    fun Context.debugLog(message: String) = Log.d(TAG, message)
    fun Context.errorLog(message: String) = Log.e(TAG, message)
    fun Context.errorInf(message: String) = Log.i(TAG, message)
}
