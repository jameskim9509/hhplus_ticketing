package kr.hhplus.be.server.infrastructure.core.reservation;

import kr.hhplus.be.server.domain.reservation.Payment;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
class PaymentManagerRepositoryImplTest {
    @Autowired
    private PaymentManagerRepositoryImpl paymentManagerRepository;
    @Autowired
    private PaymentJpaRepository paymentJpaRepository;

    @Transactional
    @Test
    void createPayment() {
        // given, when
        Payment payment = paymentManagerRepository.createPayment(
                Payment.builder().build()
        );

        paymentJpaRepository.flush();

        Assertions.assertThat(paymentJpaRepository.findById(payment.getId())).isNotEmpty();
    }
}