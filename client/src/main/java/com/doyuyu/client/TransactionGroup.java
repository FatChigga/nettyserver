package com.doyuyu.client;

import org.springframework.transaction.TransactionStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Song
 * @date 2019/4/23
 */
public class TransactionGroup{

    private Set<Map<String,TransactionStatus>> transactionSet;

    public void addTransaction(String id, TransactionStatus status){
        Map<String,TransactionStatus> transactionMap = new HashMap<>(1);
        transactionMap.put(id, status);
        transactionSet.add(transactionMap);
    }

    public TransactionStatus getTransaction(String id){
        return transactionSet.stream().filter(longThreadMap -> longThreadMap.containsKey(id)).findAny().get().get(id);
    }

    public void removeTransaction(String id){
        transactionSet.removeIf(longTransactionStatusMap -> longTransactionStatusMap.containsKey(id));
    }
}
