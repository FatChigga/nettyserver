package com.doyuyu.server;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * @author songyuxiang
 * @description
 * @date 2019/5/23
 */

@Getter
public class QueueParser {
    private String name;
    private String exchangeName;
    private String routingKey;
    private RabbitMqExchangeType exchangeType;
    private QueueType componentType;


    public QueueParser(QueueDefinition definition) {
        QueueType type = definition.getType();
        Optional.ofNullable(definition.getType()).orElseThrow(() -> new RuntimeException("队列类型不能为null"));
        switch (type) {
            case queue:
                exchangeType = RabbitMqExchangeType.DIRECT;
                componentType = QueueType.queue;
                exchangeName = RabbitMqClient.DEFAULT_DIRECT_EXCHANGE_NAME;
                break;
            case topic:
                exchangeType = RabbitMqExchangeType.TOPIC;
                componentType = QueueType.topic;
                exchangeName = RabbitMqClient.DEFAULT_TOPIC_EXCHANGE_NAME;
                break;
            default:
                throw new RuntimeException("暂不支持的定义类型:" + type);
        }
        String signature = definition.getSignature();
        if(StringUtils.isEmpty(signature)){
            throw new RuntimeException("消息组件签名必须有值");
        }
        String[] strs = signature.split(":");
        switch (strs.length) {
            case 1:
                routingKey = strs[0];
                name = strs[0];
                break;
            case 2:
                exchangeName = strs[0];
                routingKey = strs[1];
                name = strs[1];
                break;
            case 3:
                exchangeName = strs[0];
                routingKey = strs[1];
                name = strs[2];
                break;
            case 0:
            default:
                throw new RuntimeException("消息组件定义错误,规则[exchangeName:[routingKey:]]queueName");
        }
    }

    @Override
    public String toString() {
        return "ComponentDefinitionParser [name=" + name + ", exchangeName=" + exchangeName + ", routingKey=" + routingKey
                + ", exchangeType=" + exchangeType + ", componentType=" + componentType + "]";
    }
}
