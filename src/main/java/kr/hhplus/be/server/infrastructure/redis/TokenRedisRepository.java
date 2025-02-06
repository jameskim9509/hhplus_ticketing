package kr.hhplus.be.server.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class TokenRedisRepository {
    public static final String WAIT_TOKEN_SET_NAME = "wait-tokens";
    public static final String ACTIVE_TOKEN_SET_NAME = "active-tokens";

    private final StringRedisTemplate stringRedisTemplate;

    public Boolean zSetAdd(String key, String member, double score)
    {
        return stringRedisTemplate.opsForZSet().addIfAbsent(key, member, score);
    }

    public Set<ZSetOperations.TypedTuple<String>> zSetPopMin(String key, long count)
    {
        return stringRedisTemplate.opsForZSet().popMin(key, count);
    }

    public Long zSetRank(String key, String member)
    {
        return stringRedisTemplate.opsForZSet().rank(key, member);
    }

    public Long zSetCard(String key)
    {
        return stringRedisTemplate.opsForZSet().zCard(key);
    }

    public Double zSetScore(String key, String member)
    {
        return stringRedisTemplate.opsForZSet().score(key, member);
    }

    public Long zSetRemoveRangeByScore(String key, double min, double max)
    {
        return stringRedisTemplate.opsForZSet().removeRangeByScore(key, min, max);
    }

    public Boolean zSetModify(String key, String member, double expiredAt)
    {
        return stringRedisTemplate.opsForZSet().add(key, member, expiredAt);
    }
}
