package it.jdark.android.example.rxjava.java.rest;

import java.util.List;

import io.reactivex.Observable;
import it.jdark.android.example.rxjava.java.model.GitHubRepo;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created on 31/05/17.
 *
 * @Autor jDark
 */

public interface GitHubService {
    @GET("users/{user}/starred")
    Observable<List<GitHubRepo>> getStarredRepositories(@Path("user") String userName);
}
