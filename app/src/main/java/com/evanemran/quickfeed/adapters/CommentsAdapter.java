package com.evanemran.quickfeed.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.evanemran.quickfeed.R;
import com.evanemran.quickfeed.models.CommentData;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsViewHolder>{

    Context context;
    List<CommentData> list;

    public CommentsAdapter(Context context, List<CommentData> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentsViewHolder(LayoutInflater.from(context).inflate(R.layout.list_comments, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsViewHolder holder, int position) {
        holder.textView_poster.setText(list.get(position).getCommenter());
        holder.textView_postBody.setText(list.get(position).getCommentBody());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
class CommentsViewHolder extends RecyclerView.ViewHolder {

    RoundedImageView imageView_user;
    TextView textView_poster, textView_postBody;

    public CommentsViewHolder(@NonNull View itemView) {
        super(itemView);

        textView_postBody = itemView.findViewById(R.id.textView_postBody);
        textView_poster = itemView.findViewById(R.id.textView_poster);
        imageView_user = itemView.findViewById(R.id.imageView_user);
    }
}
