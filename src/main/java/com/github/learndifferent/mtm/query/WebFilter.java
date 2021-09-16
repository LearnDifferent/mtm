package com.github.learndifferent.mtm.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.learndifferent.mtm.exception.ServiceException;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 筛选器（根据用户名和日期，筛选网页，并根据需要加载的数量显示网页）
 *
 * @author zhou
 * @date 2021/09/05
 */
public class WebFilter implements Serializable {

    /**
     * 需要加载多少条数据
     */
    @Value("${website-filter.load}")
    private int load;

    /**
     * 需要筛选的用户名，为空或 null 时表示筛选全部
     */
    private List<String> usernames;

    /**
     * 表示筛选范围从这一天开始（包括这一天），如果为 null，表示不筛选
     * 注意 MyBatis 中要指定：jdbcType=DATE
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date fromDate;

    /**
     * 表示筛选范围到这一天结束（包括这一天），如果为 null，表示不筛选
     * 注意 MyBatis 中要指定：jdbcType=DATE
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date toDate;

    /**
     * 按照什么字段排序
     */
    @Value("${website-filter.order}")
    private String order;

    /**
     * 是否按照 desc 排序
     */
    @Value("${website-filter.isDesc}")
    private Boolean desc;

    private static final String CREATE_TIME = "createTime";

    private static final String USER_NAME = "userName";

    /**
     * 将前端传入的数据转化为实体类只需要无参构造器即可
     */
    public WebFilter() {
    }

    /**
     * 将传入的日期列表 dates，转化为实例内的两个日期属性
     */
    public void setDates(List<Date> dates) {
        // 列表内有多少个日期
        int len = dates.size();

        switch (len) {
            // 如果长度为 0，表示筛选全部日期（让两个属性都为 null 即可）
            case 0:
                this.fromDate = null;
                this.toDate = null;
                break;
            // 如果长度为 1，表示只筛选这一天
            case 1:
                this.fromDate = dates.get(0);
                this.toDate = dates.get(0);
                break;
            // 如果长度为 2，表示筛选两个日期之间
            case 2:
                Date date1 = dates.get(0);
                Date date2 = dates.get(1);
                // 给日期按照时间顺序排序
                orderDate(date1, date2);
                break;
            // 其他情况，抛出异常（这个异常类似于 IllegalArgumentException）
            default:
                throw new ServiceException("不能设定" + len + "个日期");
        }
    }

    private void orderDate(Date date1, Date date2) {
        if (date1.before(date2)) {
            this.fromDate = date1;
            this.toDate = date2;
        } else {
            this.fromDate = date2;
            this.toDate = date1;
        }
    }

    /**
     * 是否按照时间排序
     */
    public void setIfOrderByTime(Boolean ifOrderByTime) {
        if (BooleanUtils.isTrue(ifOrderByTime)) {
            this.order = CREATE_TIME;
        } else {
            // 这种情况包括了 null
            this.order = USER_NAME;
        }
    }

    /**
     * 是否按照 desc 排序
     */
    public void setIfDesc(Boolean ifDesc) {
        this.desc = ifDesc;
    }

    public void setLoad(int load) {
        this.load = load;
    }

    public void setUsernames(List<String> usernames) {
        this.usernames = usernames;
    }

    @Override
    public String toString() {
        return "WebFilter{" +
                "load=" + load +
                ", usernames=" + usernames +
                ", fromDate=" + fromDate +
                ", toDate=" + toDate +
                ", order='" + order + '\'' +
                ", desc=" + desc +
                '}';
    }

    private static final long serialVersionUID = 1L;
}
