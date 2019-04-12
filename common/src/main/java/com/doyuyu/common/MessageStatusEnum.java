package com.doyuyu.common;

/**
 *
 * @author Song
 * @date 2019/4/3
 */
public enum MessageStatusEnum {

    /**
     * 接受消息失败
     */
    FAILED(0),
    /**
     * 接受消息成功
     */
    SUCCESS(1);

    private Integer value;

    MessageStatusEnum(int value) {
        this.value = value;
    }
}
