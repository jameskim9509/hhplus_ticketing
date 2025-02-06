package kr.hhplus.be.server.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class SeatRedisRepository {
    public static final String UNAVAILABLE_SEAT_SET_PREFIX = "seats-unavailable";

    private final StringRedisTemplate stringRedisTemplate;

    public Long SetAdd(String key, String value)
    {
        return stringRedisTemplate.opsForSet().add(key, value);
    }

    public Set<String> SetMembers(String key)
    {
        return stringRedisTemplate.opsForSet().members(key);
    }

    public Long SetRemove(String key, String value)
    {
        return stringRedisTemplate.opsForSet().remove(key, value);
    }
}
