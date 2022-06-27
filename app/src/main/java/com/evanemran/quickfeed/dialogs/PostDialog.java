package com.evanemran.quickfeed.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.evanemran.quickfeed.R;
import com.evanemran.quickfeed.listeners.ClickListener;
import com.evanemran.quickfeed.models.PostData;

public class PostDialog extends DialogFragment implements ClickListener<PostData> {

    private ClickListener<PostData> listener;
    EditText editText_post;
    Button button_post;

    public PostDialog(ClickListener<PostData> listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editText_post = view.findViewById(R.id.editText_post);
        button_post = view.findViewById(R.id.button_post);

        button_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostData postData = new PostData();
                postData.setPostBody(editText_post.getText().toString());
                listener.onClicked(postData);
                dismiss();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            d.getWindow().setLayout(width, height);
        }
    }


    @Override
    public void onClicked(PostData data) {
        listener.onClicked(data);
        dismiss();
    }
}
