package cn.zjk.npp.result;

/**
 * @description: Result工具类
 * @Author zjk
 * @className: ResultUtil
 * @date: 2022/8/8 13:53
 */
public class ResultUtil {
    public static Result success(){
        return new Result(ResultEnum.SUCCESS.getCode(),ResultEnum.SUCCESS.getMessage(),null);
    }
    public static Result success(Object data){
        return new Result(ResultEnum.SUCCESS.getCode(),ResultEnum.SUCCESS.getMessage(),data);
    }
    public static Result success(String message){
        return new Result(ResultEnum.SUCCESS.getCode(),message,null);
    }
    public static Result success(String message,Object data){
        return new Result(ResultEnum.SUCCESS.getCode(),message,data);
    }
    public static Result fail(){
        return new Result(ResultEnum.FAIL.getCode(),ResultEnum.FAIL.getMessage(),null);
    }
    public static Result fail(String message){
        return new Result(ResultEnum.FAIL.getCode(),message,null);
    }
    public static Result fail(String message,Object data){
        return new Result(ResultEnum.FAIL.getCode(),message,data);
    }
}
