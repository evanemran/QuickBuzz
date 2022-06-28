package com.evanemran.quickfeed.listeners;

import com.evanemran.quickfeed.models.CommentData;

public interface CommentListener {
    void onCommentSubmit(CommentData data);
}
