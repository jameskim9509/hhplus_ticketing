package kr.hhplus.be.server.infrastructure.core.concert;

import kr.hhplus.be.server.domain.concert.Concert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ActiveProfiles("test")
@SpringBootTest
class ConcertReaderRepositoryImplTest {
    @Autowired
    private ConcertReaderRepositoryImpl concertReaderRepository;

    @Transactional
    @Test
    void getByDate() {
        // when
        Optional<Concert> concert = concertReaderRepository.getByDate(LocalDate.of(2025, 07, 01));

        // then
        Assertions.assertThat(concert.isPresent()).isTrue();
    }

    @Transactional
    @Test
    void getById() {
        // when
        Optional<Concert> concert = concertReaderRepository.getById(1L);

        // then
        Assertions.assertThat(concert.isPresent()).isTrue();
    }

    @Transactional
    @Test
    void getByDateBetween() {
        // when
        List<Concert> concertList =
                concertReaderRepository.getByDateBetween(
                        LocalDate.of(2025,7,1),
                        LocalDate.of(2025, 10, 1)
                );

        // then
        Assertions.assertThat(concertList.size()).isEqualTo(3);
    }
}