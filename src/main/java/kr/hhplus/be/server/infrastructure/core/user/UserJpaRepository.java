package kr.hhplus.be.server.infrastructure.core.user;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from User u where u.id=:userId")
    Optional<User> findByIdWithLock(@Param("userId") Long userId);

    Optional<User> findByUuid(String uuid);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from User u where u.uuid=:uuid")
    Optional<User> findByUuidWithLock(@Param("uuid") String uuid);
}
