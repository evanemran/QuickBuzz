package com.evanemran.quickfeed.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.evanemran.quickfeed.R;
import com.evanemran.quickfeed.listeners.ClickListener;
import com.evanemran.quickfeed.listeners.PostReactionListener;
import com.evanemran.quickfeed.models.PostData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsViewHolder>{

    Context context;
    List<PostData> list;
    PostReactionListener<PostData> listener;

    public PostsAdapter(Context context, List<PostData> list, PostReactionListener<PostData> listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostsViewHolder(LayoutInflater.from(context).inflate(R.layout.list_posts, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostsViewHolder holder, int position) {
        final PostData data = list.get(position);
        holder.textView_poster.setText(data.getPostedBy());
        holder.textView_postBody.setText(data.getPostBody());

        holder.textView_postTime.setText(getFormattedTime(data.getPosTTime()));
    }

    private String getFormattedTime(String posTTime) {
        String time = posTTime;
        SimpleDateFormat today_formatter = new SimpleDateFormat("hh:mm a");
        Date today = new Date();

        try {
            Date postDate = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a").parse(time);
            if (postDate.getDate() == today.getDate()){
                return "Today at "+today_formatter.format(postDate);
            }
            else {
                return time;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return time;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
class PostsViewHolder extends RecyclerView.ViewHolder {
    TextView textView_poster, textView_postBody, textView_postTime;

    public PostsViewHolder(@NonNull View itemView) {
        super(itemView);

        textView_poster = itemView.findViewById(R.id.textView_poster);
        textView_postBody = itemView.findViewById(R.id.textView_postBody);
        textView_postTime = itemView.findViewById(R.id.textView_postTime);
    }
}
