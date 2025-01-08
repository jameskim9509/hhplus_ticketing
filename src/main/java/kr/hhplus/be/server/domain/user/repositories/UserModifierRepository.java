package kr.hhplus.be.server.domain.user.repositories;

import kr.hhplus.be.server.domain.user.User;

public interface UserModifierRepository {
    public User modifyUser(User user);
}
