package it.jdark.android.example.rxjava.common;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.jdark.android.example.rxjava.R;
import it.jdark.android.example.rxjava.java.JavaActivity;
import it.jdark.android.example.rxjava.kotlin.KotlinActivity;


public class ChooseActivity extends BaseActivity {

    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new MyAdapter());
        mRecyclerView.setHasFixedSize(true);


    }

    private static class MyAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
        private static final Class[] CLASSES = new Class[]{
                JavaActivity.class,
                KotlinActivity.class,
        };

        private static final int[] DESCRIPTION_NAMES = new int[]{
                R.string.java_desc,
                R.string.kotlin_desc,
        };

        private static final int[] DESCRIPTION_IDS = new int[]{
                R.string.java_version,
                R.string.kotlin_version,
        };

        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_choose_item, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerViewHolder holder, int position) {
            holder.bind(CLASSES[position], DESCRIPTION_NAMES[position], DESCRIPTION_IDS[position]);
        }

        @Override
        public int getItemCount() {
            return CLASSES.length;
        }
    }


    private static class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitle;
        private TextView mDescription;

        private Class mStarterClass;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.text1);
            mDescription = (TextView) itemView.findViewById(R.id.text2);
        }

        private void bind(Class aClass, @StringRes int name, @StringRes int description) {
            mStarterClass = aClass;

            mTitle.setText(name);
            mDescription.setText(description);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemView.getContext().startActivity(new Intent(itemView.getContext(), mStarterClass));
        }
    }

}
