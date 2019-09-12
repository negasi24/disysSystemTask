package com.disys.systemtask.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.disys.systemtask.R;
import com.disys.systemtask.model.News;



import java.util.List;


/**
 * Created by user on 24-01-2018.
 * This adapter is for display the news list smoothly
 * Used Glid libery for keep the image in catch memory and show image even offline
 *
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {

    List<News> newsList;

    Context context;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle, tvDate, tvDesc;
        public ImageView imgNews;
        int Pos;


        public MyViewHolder(View view) {
            super(view);
            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            tvDesc = (TextView) view.findViewById(R.id.tvDescription);
            tvDate = (TextView) view.findViewById(R.id.tvDate);
            imgNews = (ImageView) view.findViewById(R.id.imgNews);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }
    }

    public NewsAdapter(List<News> newsList, Context context) {
        this.newsList = newsList;

        this.context = context;


    }


    @Override
    public NewsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_item_layout, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final NewsAdapter.MyViewHolder holder, int position) {
        News news = newsList.get(position);
        holder.Pos = position;
        holder.tvTitle.setText(news.getTitle());
        holder.tvDate.setText(news.getDate());
        holder.tvDesc.setText(news.getDescription());

        //Used Glid libery for keep the image in catch memory and show image even offline
        Glide.with(context)
                .load(news.getImage())
                .centerCrop()
                .placeholder(R.drawable.loading)
                .into(holder.imgNews);

    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }


}
