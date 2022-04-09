package com.github.learndifferent.mtm.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * New Bookmark
 *
 * @author zhou
 * @date 2022/4/8
 */
public class NewBookmarkDTO implements Serializable {

    public static NewBookmarkDTO of(BasicWebDataDTO data, String username, Boolean isPublic) {
        String title = data.getTitle();
        String url = data.getUrl();
        String img = data.getImg();
        String desc = data.getDesc();

        return NewBookmarkDTO.of(username, title, url, img, desc, isPublic);
    }

    public static NewBookmarkDTO of(String username,
                                    String title,
                                    String url,
                                    String img,
                                    String desc,
                                    Boolean isPublic) {
        return new NewBookmarkDTO(username, title, url, img, desc, Instant.now(), isPublic);
    }

    private NewBookmarkDTO(String userName,
                           String title,
                           String url,
                           String img,
                           String desc,
                           Instant createTime,
                           Boolean isPublic) {
        this.userName = userName;
        this.title = title;
        this.url = url;
        this.img = img;
        this.desc = desc;
        this.createTime = createTime;
        this.isPublic = isPublic;
    }

    /**
     * name of the user who bookmarked the website
     */
    private final String userName;
    /**
     * title of the bookmarked website
     */
    private final String title;
    /**
     * URL of the bookmarked website
     */
    private final String url;
    /**
     * Image of the bookmarked website
     */
    private final String img;
    /**
     * Description of the bookmarked website
     */
    private final String desc;
    /**
     * Creation time
     */
    private final Instant createTime;

    /**
     * True if this is a public bookmark
     */
    private final Boolean isPublic;

    public String getUserName() {
        return userName;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getImg() {
        return img;
    }

    public String getDesc() {
        return desc;
    }

    public Instant getCreateTime() {
        return createTime;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    @Override
    public String toString() {
        return "NewBookmarkDTO{" +
                "userName='" + userName + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", img='" + img + '\'' +
                ", desc='" + desc + '\'' +
                ", createTime=" + createTime +
                ", isPublic=" + isPublic +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NewBookmarkDTO that = (NewBookmarkDTO) o;
        return Objects.equals(userName, that.userName) && Objects.equals(title, that.title)
                && Objects.equals(url, that.url) && Objects.equals(img, that.img) && Objects
                .equals(desc, that.desc) && Objects.equals(createTime, that.createTime) && Objects
                .equals(isPublic, that.isPublic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, title, url, img, desc, createTime, isPublic);
    }

    private static final long serialVersionUID = 1L;
}