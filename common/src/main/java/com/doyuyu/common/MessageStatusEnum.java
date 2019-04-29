package com.doyuyu.common;

/**
 *
 * @author Song
 * @date 2019/4/3
 */
public enum MessageStatusEnum {

    /**
     * 失败
     */
    FAILED(0),
    /**
     * 成功
     */
    SUCCESS(1),
    /**
     * 常态
     */
    NORMAL(-1);

    private Integer value;

    MessageStatusEnum(int value) {
        this.value = value;
    }
}
