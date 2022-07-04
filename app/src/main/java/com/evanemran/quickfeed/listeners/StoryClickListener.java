package com.evanemran.quickfeed.listeners;

import com.evanemran.quickfeed.models.StoryData;

public interface StoryClickListener {
    void onStoryAddClicked(StoryData data);
    void onStoryClicked(StoryData data);
}
