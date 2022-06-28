package com.evanemran.quickfeed.listeners;

import android.net.Uri;

import com.evanemran.quickfeed.models.PostData;

public interface PostListener {
    void onPostClicked(PostData data, Uri imageUri);
}
