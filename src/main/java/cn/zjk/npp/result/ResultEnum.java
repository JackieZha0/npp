package cn.zjk.npp.result;

import lombok.Data;

/**
 * @description: Result枚举类
 * @Author zjk
 * @className: ResultEnum
 * @date: 2022/8/8 13:46
 */

public enum ResultEnum {
    SUCCESS(200,"成功"),
    FAIL(400,"失败"),
    UNAUTHORIZED(401,"未授权"),
    NOT_FOUND(404,"页面未找到"),
    SERVER_ERROR(500,"服务端未响应")
    ;

    private int code;
    private String message;

    ResultEnum(int code,String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
