package it.jdark.android.example.rxjava.java;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import it.jdark.android.example.rxjava.R;
import it.jdark.android.example.rxjava.java.Adapter.MyRecyclerViewAdapter;
import it.jdark.android.example.rxjava.java.model.GitHubRepo;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();

    CompositeDisposable subscription = new CompositeDisposable();
    RecyclerView mRecyclerView;
    MyRecyclerViewAdapter adapter;
    EditText searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        adapter = new MyRecyclerViewAdapter();
        mRecyclerView.setAdapter(adapter);

        searchText = (EditText) findViewById(R.id.edit_text_username);
        Button searchButton = (Button) findViewById(R.id.search_button);

        searchButton.setOnClickListener(this);
    }

    @Override protected void onDestroy() {
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        String userName = searchText.getText().toString();
        if (!TextUtils.isEmpty(userName)) {
            // VERBOSE WAY
            Log.d(TAG, "onClick: ");
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
                            ((MyRecyclerViewAdapter) mRecyclerView.getAdapter()).setGitHubRepos(value);
                        }
                    }));

            // COMPACT WAY
            subscription.add(GitHubClient.getInstance().getStarredRepos(userName)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(response -> handleResponse(response), error -> handleError(error)));

        }

        // Hide soft keyboard after click
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

    }

    private Observable<List<GitHubRepo>> mObservable(String userName) {
        return Observable.defer(() -> GitHubClient.getInstance().getStarredRepos(userName));
    }

    private void handleError(Throwable error) {
        Toast.makeText(this, "Error "+error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }

    private void handleResponse(List<GitHubRepo> response) {
        ((MyRecyclerViewAdapter) mRecyclerView.getAdapter()).setGitHubRepos(response);
    }
}
