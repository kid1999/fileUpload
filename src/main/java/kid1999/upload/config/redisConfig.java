package kid1999.upload.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;

/**
 * @author kid1999
 * @title: redisConfig
 * @date 2019/12/9 11:47
 */

public class redisConfig {
	@Value("${sessionMaxAge}")
	private Long sessionMaxAge;

	@Bean
	CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
		/* 默认配置， 默认超时时间为30s */
		RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration
				.ofSeconds(sessionMaxAge)).disableCachingNullValues();
		RedisCacheManager cacheManager = RedisCacheManager.builder(RedisCacheWriter.lockingRedisCacheWriter
				(connectionFactory)).cacheDefaults(defaultCacheConfig).transactionAware().build();
		return cacheManager;
	}
}


