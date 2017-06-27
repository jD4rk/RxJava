package it.jdark.android.example.rxjava.java;

import android.support.annotation.NonNull;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import io.reactivex.Observable;
import it.jdark.android.example.rxjava.java.model.GitHubRepo;
import it.jdark.android.example.rxjava.java.rest.GitHubService;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created on 31/05/17.
 * @Autor jDark
 */

class GitHubClient {
    private static final String GITHUB_BASE_URL = "https://api.github.com/";

    private static GitHubClient instance;
    private GitHubService gitHubService;

    private GitHubClient() {
        final Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(GITHUB_BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        gitHubService = retrofit.create(GitHubService.class);
    }

    static GitHubClient getInstance() {
        if (instance == null) {
            instance = new GitHubClient();
        }
        return instance;
    }

    Observable<List<GitHubRepo>> getStarredRepos(@NonNull String userName) {
        return gitHubService.getStarredRepositories(userName);
    }

}
