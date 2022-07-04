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
import com.evanemran.quickfeed.listeners.StoryClickListener;
import com.evanemran.quickfeed.models.StoryData;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class StoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context context;
    List<StoryData> list;
    StoryClickListener listener;

    public static final int VIEWHOLDER_ADD = 1;
    public static final int VIEWHOLDER_STORY = 2;

    public StoriesAdapter(Context context, List<StoryData> list, StoryClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEWHOLDER_ADD) {
            return new StoriesViewHolderUpload(LayoutInflater.from(context).inflate(R.layout.list_story_add, parent, false));
        }
        else{
            return new StoriesViewHolder(LayoutInflater.from(context).inflate(R.layout.list_story, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final StoryData data = list.get(position);
        if (holder instanceof StoriesViewHolderUpload){
//            Picasso.get().load(list.get(position).getStoryImageUrl()).into(((StoriesViewHolderUpload) holder).imageView_story);
            ((StoriesViewHolderUpload) holder).textView_story_name.setText("Add Story");
            ((StoriesViewHolderUpload) holder).imageView_story.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onStoryAddClicked(data);
                }
            });
        }
        else if (holder instanceof StoriesViewHolder){
            Picasso.get().load(list.get(position).getStoryImageUrl()).into(((StoriesViewHolder) holder).imageView_story);
            ((StoriesViewHolder) holder).textView_story_name.setText(list.get(position).getStoryOwner());
            ((StoriesViewHolder) holder).imageView_story.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onStoryClicked(data);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position==0){
            return VIEWHOLDER_ADD;
        }
        else{
            return VIEWHOLDER_STORY;
        }
    }
}

class StoriesViewHolder extends RecyclerView.ViewHolder {

    RoundedImageView imageView_story;
    TextView textView_story_name;

    public StoriesViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView_story = itemView.findViewById(R.id.imageView_story);
        textView_story_name = itemView.findViewById(R.id.textView_story_name);
    }
}

class StoriesViewHolderUpload extends RecyclerView.ViewHolder {

    RoundedImageView imageView_story;
    TextView textView_story_name;

    public StoriesViewHolderUpload(@NonNull View itemView) {
        super(itemView);

        imageView_story = itemView.findViewById(R.id.imageView_story);
        textView_story_name = itemView.findViewById(R.id.textView_story_name);
    }
}
