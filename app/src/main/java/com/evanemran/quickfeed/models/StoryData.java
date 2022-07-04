package com.evanemran.quickfeed.models;

public class StoryData {
    String storyId = "";
    String storyOwner = "";
    String storyImageUrl = "";
    String storyTime = "";

    public StoryData() {
    }

    public StoryData(String storyId, String storyOwner, String storyImageUrl) {
        this.storyId = storyId;
        this.storyOwner = storyOwner;
        this.storyImageUrl = storyImageUrl;
    }

    public String getStoryTime() {
        return storyTime;
    }

    public void setStoryTime(String storyTime) {
        this.storyTime = storyTime;
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public String getStoryOwner() {
        return storyOwner;
    }

    public void setStoryOwner(String storyOwner) {
        this.storyOwner = storyOwner;
    }

    public String getStoryImageUrl() {
        return storyImageUrl;
    }

    public void setStoryImageUrl(String storyImageUrl) {
        this.storyImageUrl = storyImageUrl;
    }
}
