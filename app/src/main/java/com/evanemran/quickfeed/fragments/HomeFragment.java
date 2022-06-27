package com.evanemran.quickfeed.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.evanemran.quickfeed.R;
import com.evanemran.quickfeed.adapters.PostsAdapter;
import com.evanemran.quickfeed.listeners.PostReactionListener;
import com.evanemran.quickfeed.models.PostData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    View view;
    RecyclerView recycler_home;
    DatabaseReference databaseReference;
    List<PostData> postDataList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference("posts");

        recycler_home = view.findViewById(R.id.recycler_home);



        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postDataList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    PostData post = postSnapshot.getValue(PostData.class);
                    postDataList.add(post);
                }

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

    private final PostReactionListener<PostData> reactionListener = new PostReactionListener<PostData>() {
        @Override
        public void onLikeClicked(PostData data) {

        }

        @Override
        public void onShareClicked(PostData data) {

        }

        @Override
        public void onCommentClicked(PostData data) {

        }
    };
}
