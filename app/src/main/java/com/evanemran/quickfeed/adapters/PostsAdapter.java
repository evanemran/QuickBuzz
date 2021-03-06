package com.evanemran.quickfeed.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.evanemran.quickfeed.R;
import com.evanemran.quickfeed.listeners.ClickListener;
import com.evanemran.quickfeed.listeners.PostReactionListener;
import com.evanemran.quickfeed.models.PostData;
import com.evanemran.quickfeed.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostsAdapter extends RecyclerView.Adapter<PostsViewHolder>{

    Context context;
    List<PostData> list;
    PostReactionListener<PostData> listener;
    PrettyTime prettyTime = new PrettyTime(Locale.getDefault());
    DatabaseReference databaseReference;

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

        try{
            getProfilePic(holder.imageView_user, data.getPostedBy().getUserId());
        }catch (Exception e){
            e.printStackTrace();
        }

        holder.textView_poster.setText(data.getPostedBy().getUserFullName());
        holder.textView_postBody.setText(data.getPostBody());
//        holder.textView_postTime.setText(getFormattedTime(data.getPosTTime()));
        holder.textView_postTime.setText(prettyTime.format(getDateFromStr(data.getPosTTime())));

        holder.textView_likeCount.setText(data.getLikes()+""/* + "\nLikes"*/);
        holder.textView_commentCount.setText(data.getCommentsCount()+""/* + "\nComments"*/);
        holder.textView_shareCount.setText(data.getShareCount()+""/* + "\nShares"*/);

        if (data.getImage().isEmpty() || data.getImage()==null){
            holder.imageView_post.setVisibility(View.GONE);
        }
        else{
            holder.imageView_post.setVisibility(View.VISIBLE);
            Picasso.get().load(data.getImage()).placeholder(R.drawable.image_placeholder).into(holder.imageView_post);
        }

        holder.button_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onLikeClicked(data);
                /*if (data.isLiked()){
                    data.setLiked(false);
                    holder.button_like.setImageResource(R.drawable.ic_like_fill);
                }
                else{
                    data.setLiked(true);
                    holder.button_like.setImageResource(R.drawable.ic_like);
                }*/
            }
        });
        holder.button_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCommentClicked(data);
            }
        });
        holder.button_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onShareClicked(data);
            }
        });
    }

    private Date getDateFromStr(String posTTime) {
        try {
            Date postDate = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a").parse(posTTime);
            return postDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
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

    private void getProfilePic(ImageView imageView, String uId){
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        Query dbQuery = databaseReference
                .orderByChild("userId")
                .equalTo(uId);

        dbQuery.addListenerForSingleValueEvent(
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                        {
                            String image = postSnapshot.child("userPhoto").getValue().toString();
                            if (!image.isEmpty()){
                                Picasso.get().load(image).into(imageView);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                })
        );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
class PostsViewHolder extends RecyclerView.ViewHolder {
    TextView textView_poster, textView_postBody, textView_postTime;
    TextView textView_shareCount, textView_commentCount, textView_likeCount;
    ImageView imageView_post, imageView_user;
    LinearLayout button_share, button_comment, button_like;

    public PostsViewHolder(@NonNull View itemView) {
        super(itemView);

        textView_poster = itemView.findViewById(R.id.textView_poster);
        textView_postBody = itemView.findViewById(R.id.textView_postBody);
        textView_postTime = itemView.findViewById(R.id.textView_postTime);
        imageView_post = itemView.findViewById(R.id.imageView_post);
        button_share = itemView.findViewById(R.id.button_share);
        button_comment = itemView.findViewById(R.id.button_comment);
        button_like = itemView.findViewById(R.id.button_like);
        textView_likeCount = itemView.findViewById(R.id.textView_likeCount);
        textView_commentCount = itemView.findViewById(R.id.textView_commentCount);
        textView_shareCount = itemView.findViewById(R.id.textView_shareCount);
        imageView_user = itemView.findViewById(R.id.imageView_user);
    }
}
