package com.github.learndifferent.mtm.dto;

import com.github.learndifferent.mtm.constant.enums.Privacy;
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

    public static NewBookmarkDTO of(BasicWebDataDTO data, long id, long userId, Privacy privacy) {
        String title = data.getTitle();
        String url = data.getUrl();
        String img = data.getImg();
        String desc = data.getDesc();

        return NewBookmarkDTO.of(id, userId, title, url, img, desc, privacy);
    }

    public static NewBookmarkDTO of(Long id,
                                    Long userId,
                                    String title,
                                    String url,
                                    String img,
                                    String desc,
                                    Privacy privacy) {

        return new NewBookmarkDTO(id, userId, title, url, img, desc, Instant.now(), privacy.isPublic());
    }

    private NewBookmarkDTO(Long id,
                           Long userId,
                           String title,
                           String url,
                           String img,
                           String desc,
                           Instant createTime,
                           Boolean isPublic) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.url = url;
        this.img = img;
        this.desc = desc;
        this.createTime = createTime;
        this.isPublic = isPublic;
    }

    private final Long id;

    /**
     * User ID of the owner of the bookmark
     */
    private final Long userId;
    /**
     * Title of the bookmark
     */
    private final String title;
    /**
     * URL of the bookmark
     */
    private final String url;
    /**
     * Image of the bookmark
     */
    private final String img;
    /**
     * Description of the bookmark
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

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
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

    public Boolean getPublic() {
        return isPublic;
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
        return Objects.equals(id, that.id) && Objects.equals(userId, that.userId)
                && Objects.equals(title, that.title) && Objects.equals(url, that.url)
                && Objects.equals(img, that.img) && Objects.equals(desc, that.desc)
                && Objects.equals(createTime, that.createTime) && Objects.equals(isPublic,
                that.isPublic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, title, url, img, desc, createTime, isPublic);
    }

    @Override
    public String toString() {
        return "NewBookmarkDTO{" +
                "id=" + id +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", img='" + img + '\'' +
                ", desc='" + desc + '\'' +
                ", createTime=" + createTime +
                ", isPublic=" + isPublic +
                '}';
    }

    private static final long serialVersionUID = 1L;
}