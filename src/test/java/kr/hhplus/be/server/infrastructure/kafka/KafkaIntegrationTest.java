package kr.hhplus.be.server.infrastructure.kafka;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;

@ActiveProfiles("test")
@SpringBootTest
public class KafkaIntegrationTest {
    @Autowired
    KafkaTemplate<Long, String> kafkaTemplate;

    @Test
    public void test() throws InterruptedException {
        kafkaTemplate.send("test_topic", 1L, "test_data");

        KafkaTestConsumer.latch.await(10000, TimeUnit.MILLISECONDS);

        Assertions.assertThat(KafkaTestConsumer.latch.getCount()).isEqualTo(0L);
        Assertions.assertThat(KafkaTestConsumer.payload).isEqualTo("test_data");
    }
}
