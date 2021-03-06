/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.servicecomb.pack.alpha.fsm.channel.kafka;

import org.apache.servicecomb.pack.alpha.core.fsm.channel.MessagePublisher;
import org.apache.servicecomb.pack.alpha.core.fsm.event.base.BaseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.ExecutionException;

public class KafkaMessagePublisher implements MessagePublisher {

    private static final Logger logger = LoggerFactory.getLogger(KafkaMessagePublisher.class);

    private String topic;
    private KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaMessagePublisher(String topic, KafkaTemplate<String, Object> kafkaTemplate) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(Object data) {
        if(logger.isDebugEnabled()){
            logger.debug("send message [{}] to [{}]", data, topic);
        }

        try {
            if(data instanceof BaseEvent) {
                BaseEvent event = (BaseEvent) data;
                kafkaTemplate.send(topic, event.getGlobalTxId(), event).get();
            }else{
                throw new UnsupportedOperationException("data must be BaseEvent type");
            }
        } catch (InterruptedException | ExecutionException | UnsupportedOperationException e) {
            logger.error("publish Exception = [{}]", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
