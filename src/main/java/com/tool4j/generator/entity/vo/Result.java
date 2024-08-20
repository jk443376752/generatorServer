package com.tool4j.generator.entity.vo;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

/**
 * @author Deng.Weiping
 * @since 2023/7/13 15:34
 */
@Data
@Accessors(chain = true)
public class Result<T> {

    private Integer code;

    private String message;

    private String detail;

    private T data;

    /**
     * 返回正确结果
     *
     * @return
     */
    public static Result ok() {
        return new Result()
                .setCode(HttpStatus.OK.value());
    }

    /**
     * 返回正确结果
     *
     * @param obj 返回数据
     * @return
     */
    public static <T> Result<T> ok(T obj) {
        return new Result()
                .setCode(HttpStatus.OK.value())
                .setData(obj);
    }

    /**
     * 返回错误结果
     *
     * @return
     */
    public static Result error() {
        return new Result()
                .setCode(HttpStatus.OK.value());
    }

    /**
     * 返回错误结果
     *
     * @param message 错误信息
     * @return
     */
    public static <T> Result<T> error(String message) {
        return new Result()
                .setCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .setMessage(message);
    }

    /**
     * 返回错误结果
     *
     * @param message 错误码
     * @param message 错误信息
     * @return
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result()
                .setCode(code)
                .setMessage(message);
    }

    /**
     * 返回错误结果
     *
     * @param message 错误信息
     * @param detail  详细信息
     * @return
     */
    public static <T> Result<T> error(String message, String detail) {
        return new Result<T>()
                .setCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .setMessage(message)
                .setDetail(detail);
    }

    /**
     * 是否错误
     *
     * @return
     */
    public boolean errored() {
        if (this == null || HttpStatus.OK.value() != this.getCode()) {
            return true;
        }
        return false;
    }

    /**
     * 是否成功
     *
     * @return
     */
    public boolean succeed() {
        if (this != null && HttpStatus.OK.value() == this.getCode()) {
            return true;
        }
        return false;
    }

}