package cn.zjk.npp.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: Result实体类
 * @Author zjk
 * @className: Result
 * @date: 2022/8/8 13:44
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    private int code;
    private String message;
    private Object data;
    private PageInfo pageInfo;

    public Result(int code,String message,Object data){
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
