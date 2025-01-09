package kr.hhplus.be.server.domain.user.components;

import kr.hhplus.be.server.domain.user.repositories.UserReaderRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class UserReaderTest {
    @Mock
    UserReaderRepository userReaderRepository;

    @InjectMocks
    UserReader userReader;

    @Test
    void 사용자_없으면_에러_by_id()
    {
        // given
        Mockito.doReturn(Optional.empty()).when(userReaderRepository).readById(Mockito.anyLong());

        // when, then
        Assertions.assertThatThrownBy(
                () -> userReader.readById(10L)
        ).isInstanceOf(RuntimeException.class)
        .hasMessage("존재하지 않는 사용자입니다.");
    }

    @Test
    void 사용자_없으면_에러_by_uuid()
    {
        // given
        Mockito.doReturn(Optional.empty()).when(userReaderRepository).readByUuid(Mockito.anyString());

        // when, then
        Assertions.assertThatThrownBy(
                        () -> userReader.readByUuid(UUID.randomUUID().toString())
                ).isInstanceOf(RuntimeException.class)
                .hasMessage("존재하지 않는 사용자입니다.");
    }

    @Test
    void 사용자_없으면_에러_by_uuid_with_lock()
    {
        // given
        Mockito.doReturn(Optional.empty()).when(userReaderRepository).readByUuidWithLock(Mockito.anyString());

        // when, then
        Assertions.assertThatThrownBy(
                        () -> userReader.readByUuidWithLock(UUID.randomUUID().toString())
                ).isInstanceOf(RuntimeException.class)
                .hasMessage("존재하지 않는 사용자입니다.");
    }
}