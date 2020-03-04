package com.offcn.entity;

import java.io.Serializable;

/**
 * 操作状态数据
 */
public class ResultMessage implements Serializable {

    private int code;//状态码
    private  String message;//提示信息

    public ResultMessage() {
    }

    public ResultMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
