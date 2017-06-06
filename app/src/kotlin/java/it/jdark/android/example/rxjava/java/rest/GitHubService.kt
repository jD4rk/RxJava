package it.jdark.android.example.rxjava.java.rest

import io.reactivex.Observable
import it.jdark.android.example.rxjava.java.model.GitHubRepo
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created on 06/06/17.
 * @Autor jDark
 */
interface GitHubService {
    @GET("users/{user}/starred")
    fun getStarredRepositories(@Path("user") userName: String): Observable<List<GitHubRepo>>
}
