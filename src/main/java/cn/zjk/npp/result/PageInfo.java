package cn.zjk.npp.result;

import lombok.Data;

/**
 * @description: TODO
 * @Author zjk
 * @className: PageInfo
 * @date: 2022/8/9 11:01
 */

@Data
public class PageInfo {
    /**
     * 当前页
     */
    private int currentPage;
    /**
     * 每页条数
     */
    private int pageSize;
    /**
     * 总页数
     */
    private int totalPage;
    /**
     * 总条数
     */
    private int total;
}
