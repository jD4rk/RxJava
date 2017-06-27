package it.jdark.android.example.rxjava.java;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import it.jdark.android.example.rxjava.R;
import it.jdark.android.example.rxjava.common.BaseActivity;
import it.jdark.android.example.rxjava.java.Adapter.MyRecyclerViewAdapter;
import it.jdark.android.example.rxjava.java.model.GitHubRepo;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class JavaActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    private final String TAG = getClass().getSimpleName();

    CompositeDisposable subscription = new CompositeDisposable();
    RecyclerView mRecyclerView;
    MyRecyclerViewAdapter adapter;
    EditText searchText;

    // Face permission checked before request (just to show how le library works!)
    private static final int RC_LOCATION_AND_CAMERA = 124;
    String[] perms = { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA };


    private Observable<List<GitHubRepo>> mObservable(String userName) {
        return Observable.defer(() -> GitHubClient.getInstance().getStarredRepos(userName));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.java_title);
        setSupportActionBar(toolbar);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        adapter = new MyRecyclerViewAdapter();
        mRecyclerView.setAdapter(adapter);

        searchText = findViewById(R.id.edit_text_username);
        findViewById(R.id.search_button).setOnClickListener(v-> checkPermissionAndExecuteListener());

    }

    @Override protected void onDestroy() {
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
        }
        super.onDestroy();
    }

    @AfterPermissionGranted(RC_LOCATION_AND_CAMERA)
    public void checkPermissionAndExecuteListener() {
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Have permissions, do the thing!
            Toast.makeText(this, "Execute Call!", Toast.LENGTH_LONG).show();
            clickAction();
        } else {
            // Ask for both permissions
//            EasyPermissions.requestPermissions(this, getString(R.string.rationale_location_contacts), RC_LOCATION_AND_CAMERA, perms);
            EasyPermissions.requestPermissions(this,
                    getString(R.string.rationale_location_contacts),
                    R.string.rationale_ok, R.string.rationale_exit,
                    RC_LOCATION_AND_CAMERA, perms);
        }
    }

    private void clickAction() {
        String userName = searchText.getText().toString();
        if (!TextUtils.isEmpty(userName)) {
            // VERBOSE WAY
            Log.d(TAG, "checkPermissionAndExecuteListener: ");

            // REGISTER OBSERVER
            // Verbose Mode
            subscription.add(mObservable(userName)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<List<GitHubRepo>>() {
                        @Override
                        public void onComplete() {
                            Log.d(TAG, "onComplete: Finish!");
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getApplicationContext(), "Error "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            ((MyRecyclerViewAdapter) mRecyclerView.getAdapter()).setGitHubRepos(null);
                        }

                        @Override
                        public void onNext(List<GitHubRepo> value) {
                            Log.i(TAG, "onNext -> Verbose!");
                            ((MyRecyclerViewAdapter) mRecyclerView.getAdapter()).setGitHubRepos(value);
                        }
                    }));

            // Compact mode (callback)
            subscription.add(mObservable(userName)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(response -> handleResponse(response), error -> handleError(error)));

        }

        // Hide soft keyboard after click
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    private void handleError(Throwable error) {
        Toast.makeText(this, "Error "+error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }

    private void handleResponse(List<GitHubRepo> response) {
        Log.i(TAG, "onNext -> Compact!");
        ((MyRecyclerViewAdapter) mRecyclerView.getAdapter()).setGitHubRepos(response);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: Code -> " + requestCode);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            if (EasyPermissions.somePermissionDenied(this, perms)) {
                Log.d(TAG, "onActivityResult: EXIT because some permession aren't granted!");
                findViewById(R.id.search_button).setEnabled(false);
            }
            // Do something after user returned from app settings screen, like showing a Toast.
//            Toast.makeText(this, R.string.returned_from_app_settings_to_activity, Toast.LENGTH_SHORT)
//                    .show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: Code -> " + requestCode);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int i, List<String> list) {
        Log.d(TAG, "onPermissionsGranted:" + i + ":" + list.size() + " -- " + list.toString());

    }

    @Override
    public void onPermissionsDenied(int i, List<String> list) {
        Log.d(TAG, "onPermissionsDenied:" + i + ":" + list.size() + " -- " + list.toString());

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, list)) {
            new AppSettingsDialog.Builder(this).build().show();
        }

        // (Optional) Behaviour if any permission is not granted
        if (EasyPermissions.somePermissionDenied(this, perms)) {
            Toast.makeText(this, "Permission denied: " + list.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
