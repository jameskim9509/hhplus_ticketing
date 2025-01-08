package kr.hhplus.be.server.domain.user.repositories;

import kr.hhplus.be.server.domain.user.User;

import java.util.Optional;

public interface UserReaderRepository {
    public Optional<User> readById(Long userId);
    public Optional<User> readByUuid(String uuid);
    public Optional<User> readByUuidWithLock(String uuid);
}
