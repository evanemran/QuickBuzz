package com.evanemran.quickfeed.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.evanemran.quickfeed.EditProfileActivity;
import com.evanemran.quickfeed.LoginActivity;
import com.evanemran.quickfeed.R;
import com.evanemran.quickfeed.adapters.PostsAdapter;
import com.evanemran.quickfeed.dialogs.CommentDialog;
import com.evanemran.quickfeed.globals.GlobalUser;
import com.evanemran.quickfeed.listeners.PostListener;
import com.evanemran.quickfeed.listeners.PostReactionListener;
import com.evanemran.quickfeed.models.PostData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProfileFragment extends Fragment {
    View view;
    Button button_logout, button_editProfile;
    TextView textView_fullName, textView_postCount;
    ImageView imageView_profile;
    RecyclerView recycler_profile;
    List<PostData> postDataList = new ArrayList<>();
    DatabaseReference databaseReference;
    Query dbQuery;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        button_logout = view.findViewById(R.id.button_logout);
        button_editProfile = view.findViewById(R.id.button_editProfile);
        recycler_profile = view.findViewById(R.id.recycler_profile);
        textView_fullName = view.findViewById(R.id.textView_fullName);
        textView_postCount = view.findViewById(R.id.textView_postCount);
        imageView_profile = view.findViewById(R.id.imageView_profile);

        textView_fullName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        databaseReference = FirebaseDatabase.getInstance().getReference("posts");

        Picasso.get().load(GlobalUser.getInstance().getData().getUserPhoto()).into(imageView_profile);


        button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        button_editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), EditProfileActivity.class));
            }
        });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        dbQuery = databaseReference
                .orderByChild("postedBy")
                .equalTo(GlobalUser.getInstance().getData().getUserId());
        dbQuery.addListenerForSingleValueEvent(
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        postDataList.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                        {
                            PostData post = postSnapshot.getValue(PostData.class);
                            postDataList.add(post);
                        }

                        textView_postCount.setText(String.valueOf(postDataList.size() + " Posts"));
                        recycler_profile.setHasFixedSize(true);
                        recycler_profile.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                        PostsAdapter adapter = new PostsAdapter(getContext(), postDataList, reactionListener);
                        recycler_profile.setAdapter(adapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                })
        );
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

}
