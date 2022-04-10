package com.github.learndifferent.mtm.constant.enums;

/**
 * Public or private
 *
 * @author zhou
 * @date 2022/4/10
 */
public enum Privacy implements ConvertByNames {

    /**
     * Public
     */
    PUBLIC("public", true),
    /**
     * Private
     */
    PRIVATE("private", false);

    private final String privacy;
    private final boolean isPublic;

    Privacy(final String privacy, final boolean isPublic) {
        this.privacy = privacy;
        this.isPublic = isPublic;
    }

    public String getPrivacy() {
        return this.privacy;
    }

    public boolean isPublic() {
        return this.isPublic;
    }

    @Override
    public String[] namesForConverter() {
        return new String[]{this.name(), this.privacy, String.valueOf(this.isPublic)};
    }
}