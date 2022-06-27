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
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
class PostsViewHolder extends RecyclerView.ViewHolder {
    TextView textView_poster, textView_postBody;

    public PostsViewHolder(@NonNull View itemView) {
        super(itemView);

        textView_poster = itemView.findViewById(R.id.textView_poster);
        textView_postBody = itemView.findViewById(R.id.textView_postBody);
    }
}
