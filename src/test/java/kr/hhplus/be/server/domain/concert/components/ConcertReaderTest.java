package kr.hhplus.be.server.domain.concert.components;

import kr.hhplus.be.server.domain.concert.repositories.ConcertReaderRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ConcertReaderTest {
    @Mock
    private ConcertReaderRepository concertReaderRepository;

    @InjectMocks
    private ConcertReader concertReader;

    @Test
    void 콘서트가_존재하지_않으면_에러_by_date()
    {
        // given
        Mockito.doReturn(Optional.empty()).when(concertReaderRepository).getByDate(Mockito.any());

        // when, then
        Assertions.assertThatThrownBy(
                () -> concertReader.getByDate(LocalDate.now())
        ).isInstanceOf(RuntimeException.class)
        .hasMessage("존재하지 않는 콘서트입니다.");
    }

    @Test
    void 콘서트가_존재하지_않으면_에러_by_id()
    {
        // given
        Mockito.doReturn(Optional.empty()).when(concertReaderRepository).getById(Mockito.anyLong());

        // when, then
        Assertions.assertThatThrownBy(
                        () -> concertReader.getById(30L)
                ).isInstanceOf(RuntimeException.class)
                .hasMessage("존재하지 않는 콘서트입니다.");
    }
}