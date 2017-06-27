package it.jdark.android.example.rxjava.kotlin.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import it.jdark.android.example.rxjava.R
import kotlinx.android.synthetic.main.item.view.*
import java.util.*
import it.jdark.android.example.rxjava.kotlin.model.GitHubRepo


/**
 * Created on 06/06/17.
 * @Autor jDark
 */
class MyRecyclerViewAdapter : RecyclerView.Adapter<MyRecyclerViewAdapter.GitHubRepoViewHolder>() {

    private val mData = ArrayList<GitHubRepo>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): GitHubRepoViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item, null)
        return GitHubRepoViewHolder(view)
    }

    override fun onBindViewHolder(gitHubRepoViewHolder: GitHubRepoViewHolder, i: Int) {
        gitHubRepoViewHolder.textRepoName.text = mData[i].name
        gitHubRepoViewHolder.textLanguage.text = mData[i].language
        gitHubRepoViewHolder.textRepoDescription.text = mData[i].description
        gitHubRepoViewHolder.textStars.text = mData[i].stargazersCount.toString()
    }

    override fun getItemCount(): Int {
        return mData.size
    }


    fun setGitHubRepos(repos: List<GitHubRepo>?) {
        mData.clear()
        if (repos != null) {
            mData.addAll(repos)
        }
        notifyDataSetChanged()
    }

    inner class GitHubRepoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textRepoName: TextView
        val textRepoDescription: TextView
        val textLanguage: TextView
        val textStars: TextView

        init {
            textRepoName = itemView.text_view_name
            textRepoDescription = itemView.text_view_desc
            textLanguage = itemView.text_view_lang
            textStars = itemView.text_view_stars
        }
    }

}
