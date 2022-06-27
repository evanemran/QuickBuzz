package com.evanemran.quickfeed.listeners;

public interface PostReactionListener<T> {
    void onLikeClicked(T data);
    void onShareClicked(T data);
    void onCommentClicked(T data);
}
