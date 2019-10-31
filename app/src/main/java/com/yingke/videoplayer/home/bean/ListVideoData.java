package com.yingke.videoplayer.home.bean;

import android.text.TextUtils;

import com.yingke.player.java.IVideoBean;

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
public class ListVideoData extends IVideoBean {

    private String title;
    private String url;
    // 封面图 是本地路径 在data/data里
    private transient String thumbPath;
    private String authorName;
    private String authorAvatar;
    private String description;
    private int commentCount;
    private int voteCount;
    private boolean isVote;

    // 广告
    private AdBean ad;

    @Override
    public int getFirstType() {
//        if (ad != null && !TextUtils.isEmpty(ad.getAdUrl())) {
//            return TYPE_AD;
//        }
        return TYPE_REAL;
    }

    @Override
    public String getSource() {
        if (mCurrentType == TYPE_AD) {
            return ad.getAdUrl();
        }
        return getUrl();
    }

    @Override
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

    public AdBean getAd() {
        return ad;
    }

    /**
     * @return 广告视频链接
     */
    public String getAdUrl() {
        if (ad != null && !TextUtils.isEmpty(ad.getAdUrl())) {
            return ad.getAdUrl();
        }
        return null;
    }

    /**
     * @return 广告缩略图
     */
    public String getAdThumbPath() {
        if (ad != null && !TextUtils.isEmpty(ad.getThumbAdPath())) {
            return ad.getThumbAdPath();
        }
        return null;
    }

    /**
     * 设置广告缩略图
     * @param path
     */
    public void setAdThumbPath(String path) {
        if (ad != null) {
            ad.setThumbAdPath(path);
        }
    }

    /**
     * @return 广告详情链接
     */
    public String getAdWebUrl() {
        if (ad != null && !TextUtils.isEmpty(ad.getWebUrl())) {
            return ad.getWebUrl();
        }
        return null;
    }

    public class AdBean{
        private String adurl;
        private String weburl;
        private transient String thumbAdPath;

        public String getAdUrl() {
            return adurl;
        }

        public String getWebUrl() {
            return weburl;
        }

        public String getThumbAdPath() {
            return thumbAdPath;
        }

        public void setThumbAdPath(String thumbAdPath) {
            this.thumbAdPath = thumbAdPath;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListVideoData videoData = (ListVideoData) o;
        return Objects.equals(url, videoData.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }
}
