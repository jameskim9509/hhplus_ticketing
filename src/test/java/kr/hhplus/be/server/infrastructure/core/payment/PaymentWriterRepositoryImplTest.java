package kr.hhplus.be.server.infrastructure.core.payment;

import kr.hhplus.be.server.domain.payment.Payment;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
class PaymentWriterRepositoryImplTest {
    @Autowired
    private PaymentWriterRepositoryImpl paymentWriterRepository;
    @Autowired
    private PaymentJpaRepository paymentJpaRepository;

    @Transactional
    @Test
    void createPayment() {
        // given, when
        Payment payment = paymentWriterRepository.createPayment(
                Payment.builder().build()
        );

        paymentJpaRepository.flush();

        Assertions.assertThat(paymentJpaRepository.findById(payment.getId())).isNotEmpty();
    }
}