package kr.hhplus.be.server.infrastructure.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
class KafkaTestConsumer
{
    public final static CountDownLatch latch = new CountDownLatch(1);
    public static String payload;

    @KafkaListener(topics = {"test_topic"}, groupId = "test")
    public void listen(String message)
    {
        payload = message;
        latch.countDown();
    }
}