package com.github.learndifferent.mtm.constant.consist;

/**
 * html 文件常量
 *
 * @author zhou
 * @date 2021/09/12
 */
public class HtmlFileConstant {

    private HtmlFileConstant() {
    }

    /**
     * HTML 文件的开头
     */
    public static final String FILE_START = "<!DOCTYPE html><html lang=\"en\">" +
            "<head><meta charset=\"UTF-8\" content=\"text/html\" http-equiv=\"Content-Type\">" +
            "<title>Websites</title></head><body><dl>";

    /**
     * 在 img 之前
     */
    public static final String BEFORE_IMG =
            "<dt style=\"border-radius: 25px;border: 2px solid grey;padding: 20px;\"><img src=\"";

    /**
     * 在 url 之前
     */
    public static final String AFTER_IMG_BEFORE_URL = "\" width=\"35px\" height=\"35px\"><a href=\"";

    /**
     * 在 desc 之前
     */
    public static final String AFTER_URL_BEFORE_DESC = "</a><br>";

    /**
     * 在 desc 之后
     */
    public static final String AFTER_DESC = "</dt><br>";


    /**
     * 在 title 之前
     */
    public static final String BEFORE_TITLE = "\" target=\"_blank\" style=\"font-size: 25px\">";


    /**
     * HTML 文件的结尾
     */
    public static final String FILE_END = "</dl></body></html>";
}
