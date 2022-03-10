package com.github.learndifferent.mtm.constant.consist;

/**
 * Constants related to HTML files
 *
 * @author zhou
 * @date 2021/09/12
 */
public final class HtmlFileConstant {

    private HtmlFileConstant() {
    }

    /**
     * HTML file starts with:
     */
    public static final String FILE_START = "<!DOCTYPE html><html lang=\"en\">"
            + "<head><meta charset=\"UTF-8\" content=\"text/html\" http-equiv=\"Content-Type\">"
            + "<title>Websites</title></head><body><dl>";

    /**
     * Before img tag:
     */
    public static final String BEFORE_IMG =
            "<dt style=\"border-radius: 25px;border: 2px solid grey;padding: 20px;\"><img src=\"";

    /**
     * After img tag and before URL:
     */
    public static final String AFTER_IMG_BEFORE_URL = "\" width=\"35px\" height=\"35px\"><a href=\"";

    /**
     * After URL and before Description:
     */
    public static final String AFTER_URL_BEFORE_DESC = "</a><br>";

    /**
     * After Description:
     */
    public static final String AFTER_DESC = "</dt><br>";

    /**
     * Before Title:
     */
    public static final String BEFORE_TITLE = "\" target=\"_blank\" style=\"font-size: 25px\">";

    /**
     * HTML file ends with:
     */
    public static final String FILE_END = "</dl></body></html>";
}
