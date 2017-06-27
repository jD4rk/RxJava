package it.jdark.android.example.rxjava.kotlin

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import io.reactivex.Observable
import it.jdark.android.example.rxjava.kotlin.rest.GitHubService
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import it.jdark.android.example.rxjava.kotlin.model.GitHubRepo

/**
 * Created on 06/06/17.
 * @Autor jDark
 */

internal class GitHubClient{
    private val gitHubService: GitHubService

    init {
        val gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
        val retrofit = Retrofit.Builder().baseUrl(GITHUB_BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        gitHubService = retrofit.create(GitHubService::class.java)
    }

    fun getStarredRepos(userName: String): Observable<List<GitHubRepo>> {
        return gitHubService.getStarredRepositories(userName)
    }


    companion object {
        private val GITHUB_BASE_URL = "https://api.github.com/"
    }

}

