package com.evanemran.quickfeed.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.evanemran.quickfeed.R;
import com.evanemran.quickfeed.StorySelectorActivity;
import com.evanemran.quickfeed.adapters.PostsAdapter;
import com.evanemran.quickfeed.adapters.StoriesAdapter;
import com.evanemran.quickfeed.dialogs.CommentDialog;
import com.evanemran.quickfeed.dialogs.PostDialog;
import com.evanemran.quickfeed.listeners.ClickListener;
import com.evanemran.quickfeed.listeners.CommentListener;
import com.evanemran.quickfeed.listeners.PostListener;
import com.evanemran.quickfeed.listeners.PostReactionListener;
import com.evanemran.quickfeed.listeners.StoryClickListener;
import com.evanemran.quickfeed.models.CommentData;
import com.evanemran.quickfeed.models.PostData;
import com.evanemran.quickfeed.models.StoryData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment {

    View view;
    RecyclerView recycler_home, recycler_story;
    DatabaseReference databaseReference;
    List<PostData> postDataList = new ArrayList<>();
    List<StoryData> storyDataList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        recycler_home = view.findViewById(R.id.recycler_home);
        recycler_story = view.findViewById(R.id.recycler_story);

        showStories();

        showPosts();

        return view;
    }

    private void showPosts() {
        databaseReference = FirebaseDatabase.getInstance().getReference("posts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postDataList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    PostData post = postSnapshot.getValue(PostData.class);
                    postDataList.add(post);
                }

                Collections.reverse(postDataList);

                recycler_home.setHasFixedSize(true);
                recycler_home.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                PostsAdapter adapter = new PostsAdapter(getContext(), postDataList, reactionListener);
                recycler_home.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showStories() {
        databaseReference = FirebaseDatabase.getInstance().getReference("stories");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                storyDataList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    StoryData story = postSnapshot.getValue(StoryData.class);
                    storyDataList.add(story);
                }

                Collections.reverse(storyDataList);
                storyDataList.add(0, new StoryData("0","0","0"));

                recycler_story.setHasFixedSize(true);
                recycler_story.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                StoriesAdapter adapter = new StoriesAdapter(getContext(), storyDataList, storyDataClickListener);
                recycler_story.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*List<StoryData> storyDataList = new ArrayList<>();
        storyDataList.add(new StoryData("01", "Evan", "https://images.unsplash.com/photo-1519791883288-dc8bd696e667?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8MXx8c3Rvcnl8ZW58MHx8MHx8&w=1000&q=80"));
        storyDataList.add(new StoryData("01", "Evan", "https://images.unsplash.com/photo-1519791883288-dc8bd696e667?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8MXx8c3Rvcnl8ZW58MHx8MHx8&w=1000&q=80"));
        storyDataList.add(new StoryData("01", "Evan", "https://images.unsplash.com/photo-1519791883288-dc8bd696e667?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8MXx8c3Rvcnl8ZW58MHx8MHx8&w=1000&q=80"));
        storyDataList.add(new StoryData("01", "Evan", "https://images.unsplash.com/photo-1519791883288-dc8bd696e667?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8MXx8c3Rvcnl8ZW58MHx8MHx8&w=1000&q=80"));
        storyDataList.add(new StoryData("01", "Evan", "https://images.unsplash.com/photo-1519791883288-dc8bd696e667?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8MXx8c3Rvcnl8ZW58MHx8MHx8&w=1000&q=80"));
        storyDataList.add(new StoryData("01", "Evan", "https://images.unsplash.com/photo-1519791883288-dc8bd696e667?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8MXx8c3Rvcnl8ZW58MHx8MHx8&w=1000&q=80"));
        storyDataList.add(new StoryData("01", "Evan", "https://images.unsplash.com/photo-1519791883288-dc8bd696e667?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8MXx8c3Rvcnl8ZW58MHx8MHx8&w=1000&q=80"));
        storyDataList.add(new StoryData("01", "Evan", "https://images.unsplash.com/photo-1519791883288-dc8bd696e667?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8MXx8c3Rvcnl8ZW58MHx8MHx8&w=1000&q=80"));

        recycler_story.setHasFixedSize(true);
        recycler_story.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        StoriesAdapter adapter = new StoriesAdapter(getContext(), storyDataList, storyDataClickListener);
        recycler_story.setAdapter(adapter);*/
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private final PostReactionListener<PostData> reactionListener = new PostReactionListener<PostData>() {
        @Override
        public void onLikeClicked(PostData data) {
            HashMap post = new HashMap();
            post.put("likes", data.getLikes()+1);
            databaseReference.child(data.getPostId()).updateChildren(post).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Toast.makeText(getContext(), "Liked!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getContext(), "Couldn't update!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public void onShareClicked(PostData data) {

        }

        @Override
        public void onCommentClicked(PostData data) {
            CommentDialog commentDialog = new CommentDialog(data, commentListener);
            commentDialog.show(getChildFragmentManager(), "comment");
        }
    };

    private final PostListener commentListener = new PostListener() {
        @Override
        public void onPostClicked(PostData data, Uri imageUri) {
            HashMap post = new HashMap();
            post.put("commentsCount", data.getCommentsCount()+1);
            databaseReference.child(data.getPostId()).updateChildren(post).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Toast.makeText(getContext(), "Commented!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getContext(), "Couldn't comment!", Toast.LENGTH_SHORT).show();
                    }
                }
            });



            HashMap comment = new HashMap();
            comment.put("comments", data.getComments());
            databaseReference.child(data.getPostId()).updateChildren(comment).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Toast.makeText(getContext(), "Commented!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getContext(), "Couldn't comment!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    };

    private final StoryClickListener storyDataClickListener = new StoryClickListener() {
        @Override
        public void onStoryAddClicked(StoryData data) {
            startActivity(new Intent(getContext(), StorySelectorActivity.class));
        }

        @Override
        public void onStoryClicked(StoryData data) {
            Toast.makeText(getContext(), data.getStoryOwner(), Toast.LENGTH_SHORT).show();
        }
    };
}
