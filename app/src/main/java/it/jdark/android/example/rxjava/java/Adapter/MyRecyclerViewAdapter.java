package it.jdark.android.example.rxjava.java.Adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.jdark.android.example.rxjava.java.model.GitHubRepo;
import java.util.ArrayList;
import java.util.List;

import it.jdark.android.example.rxjava.R;

/**
 * Created on 31/05/17.
 * @Autor jDark
 */

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.GitHubRepoViewHolder> {

    private List<GitHubRepo> mData = new ArrayList<>();

    @Override
    public GitHubRepoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, null);
        return new GitHubRepoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GitHubRepoViewHolder gitHubRepoViewHolder, int i) {
        gitHubRepoViewHolder.textRepoName.setText(mData.get(i).getName());
        gitHubRepoViewHolder.textLanguage.setText(mData.get(i).getLanguage());
        gitHubRepoViewHolder.textRepoDescription.setText(mData.get(i).getDescription());
        gitHubRepoViewHolder.textStars.setText(String.valueOf(mData.get(i).getStargazersCount()));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public void setGitHubRepos(@Nullable List<GitHubRepo> repos) {
        mData.clear();
        if (repos != null) {
            mData.addAll(repos);
        }
        notifyDataSetChanged();
    }

    class GitHubRepoViewHolder extends RecyclerView.ViewHolder {
        private TextView textRepoName;
        private TextView textRepoDescription;
        private TextView textLanguage;
        private TextView textStars;

        public GitHubRepoViewHolder(View itemView) {
            super(itemView);
            textRepoName = (TextView) itemView.findViewById(R.id.text_view_name);
            textRepoDescription = (TextView) itemView.findViewById(R.id.text_view_desc);
            textLanguage = (TextView) itemView.findViewById(R.id.text_view_lang);
            textStars = (TextView) itemView.findViewById(R.id.text_view_stars);
        }
    }

}
