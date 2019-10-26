package com.yingke.videoplayer.tiktok.bean;

import com.yingke.player.java.IVideoBean;

import java.util.Objects;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019-10-21
 * @email 13661091407@163.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class ListTiktokBean extends IVideoBean {

    private String id;
    // 视频链接
    private String url;
    // 封面图 是本地路径 在data/data里
    private String coverImage;
    // 头衔
    private String userAvatar;
    // 是否关注
    private boolean isWatched;
    // 点赞数量
    private int voteCount;
    // 评论数量
    private int commentCount;
    // 分享数量
    private int shareCount;

    // 用户名
    private String userName;
    // 描述
    private String description;

    // 音乐
    private String music;
    // 音乐图片
    private String musicImage;
    // 创建时间
    private long createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public boolean isWatched() {
        return isWatched;
    }

    public void setWatched(boolean watched) {
        isWatched = watched;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getShareCount() {
        return shareCount;
    }

    public void setShareCount(int shareCount) {
        this.shareCount = shareCount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMusic() {
        return music;
    }

    public void setMusic(String music) {
        this.music = music;
    }

    public String getMusicImage() {
        return musicImage;
    }

    public void setMusicImage(String musicImage) {
        this.musicImage = musicImage;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListTiktokBean bean = (ListTiktokBean) o;
        return isWatched == bean.isWatched &&
                voteCount == bean.voteCount &&
                commentCount == bean.commentCount &&
                shareCount == bean.shareCount &&
                createTime == bean.createTime &&
                id.equals(bean.id) &&
                url.equals(bean.url) &&
                Objects.equals(userAvatar, bean.userAvatar) &&
                Objects.equals(userName, bean.userName) &&
                Objects.equals(description, bean.description) &&
                Objects.equals(music, bean.music) &&
                Objects.equals(musicImage, bean.musicImage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, url, userAvatar, isWatched, voteCount, commentCount, shareCount, userName, description, music, musicImage, createTime);
    }

    @Override
    public String getSource() {
        return url;
    }


    @Override
    public String getTitle() {
        return description;
    }
}
