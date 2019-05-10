package com.doyuyu.common;

/**
 *
 * @author Song
 * @date 2019/4/3
 */
public enum TransactionStatusEnum {

    /**
     * 加入事务
     */
    JOIN(0),
    /**
     * 提交事务
     */
    COMMIT(1),
    /**
     * 回滚
     */
    ROLLBACK(2);

    private Integer value;

    TransactionStatusEnum(int value) {
        this.value = value;
    }
}
