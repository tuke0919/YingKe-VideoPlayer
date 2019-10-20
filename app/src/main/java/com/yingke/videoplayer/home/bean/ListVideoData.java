package com.yingke.videoplayer.home.bean;

import android.graphics.Bitmap;

import com.yingke.videoplayer.util.PlayerUtil;

import java.util.Objects;

/**
 * 功能：
 * </p>
 * <p>Copyright xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019-10-14
 * @email 13661091407@163.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class ListVideoData {

    private String title;
    private String url;
    private transient String thumbPath;
    private String authorName;
    private String authorAvatar;
    private String description;
    private int commentCount;
    private int voteCount;
    private boolean isVote;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorAvatar() {
        return authorAvatar;
    }

    public void setAuthorAvatar(String authorAvatar) {
        this.authorAvatar = authorAvatar;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public boolean isVote() {
        return isVote;
    }

    public void setVote(boolean vote) {
        isVote = vote;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListVideoData videoData = (ListVideoData) o;
        return commentCount == videoData.commentCount &&
                voteCount == videoData.voteCount &&
                isVote == videoData.isVote &&
                Objects.equals(title, videoData.title) &&
                Objects.equals(url, videoData.url) &&
                Objects.equals(authorName, videoData.authorName) &&
                Objects.equals(authorAvatar, videoData.authorAvatar) &&
                Objects.equals(description, videoData.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, url, authorName, authorAvatar, description, commentCount, voteCount, isVote);
    }
}
