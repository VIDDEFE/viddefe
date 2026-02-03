package com.viddefe.viddefe_api.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuración del sistema de caché con Redis.
 *
 * Define:
 * - Cache Manager con Redis como backend
 * - TTL por defecto y TTL específicos por caché
 * - Serialización JSON para valores
 *
 * Cachés disponibles:
 * - PEOPLE_CACHE: Personas (TTL: 30 min)
 * - CHURCHES_CACHE: Iglesias (TTL: 1 hora)
 * - USERS_CACHE: Usuarios (TTL: 15 min)
 * - ROLES_CACHE: Roles (TTL: 2 horas)
 * - PERMISSIONS_CACHE: Permisos (TTL: 2 horas)
 * - MEETINGS_CACHE: Reuniones (TTL: 10 min)
 * - HOME_GROUPS_CACHE: Grupos de hogar (TTL: 30 min)
 * - STATES_CITIES_CACHE: Estados y ciudades (TTL: 24 horas)
 */
@EnableCaching
@Configuration
public class CacheConfig {

    // Constantes de nombres de caché
    public static final String PEOPLE_CACHE = "people";
    public static final String CHURCHES_CACHE = "churches";
    public static final String USERS_CACHE = "users";
    public static final String ROLES_CACHE = "roles";
    public static final String PERMISSIONS_CACHE = "permissions";
    public static final String MEETINGS_CACHE = "meetings";
    public static final String HOME_GROUPS_CACHE = "homeGroups";
    public static final String STATES_CITIES_CACHE = "statesCities";
    public static final String SESSION_CACHE = "sessions";

    /**
     * Configuración base de caché Redis.
     */
    @Bean
    public RedisCacheConfiguration defaultCacheConfiguration(
            @Qualifier("redisObjectMapper") ObjectMapper redisObjectMapper
    ) {
        GenericJackson2JsonRedisSerializer jsonSerializer =
                new GenericJackson2JsonRedisSerializer(redisObjectMapper);

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30)) // TTL por defecto
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new StringRedisSerializer()
                        )
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer)
                )
                .disableCachingNullValues();
    }

    /**
     * Cache Manager configurado con Redis.
     */
    @Bean
    public CacheManager cacheManager(
            RedisConnectionFactory connectionFactory,
            RedisCacheConfiguration defaultCacheConfiguration
    ) {
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // Configuraciones específicas por caché
        cacheConfigurations.put(PEOPLE_CACHE,
                defaultCacheConfiguration.entryTtl(Duration.ofMinutes(30)));

        cacheConfigurations.put(CHURCHES_CACHE,
                defaultCacheConfiguration.entryTtl(Duration.ofHours(1)));

        cacheConfigurations.put(USERS_CACHE,
                defaultCacheConfiguration.entryTtl(Duration.ofMinutes(15)));

        cacheConfigurations.put(ROLES_CACHE,
                defaultCacheConfiguration.entryTtl(Duration.ofHours(2)));

        cacheConfigurations.put(PERMISSIONS_CACHE,
                defaultCacheConfiguration.entryTtl(Duration.ofHours(2)));

        cacheConfigurations.put(MEETINGS_CACHE,
                defaultCacheConfiguration.entryTtl(Duration.ofMinutes(10)));

        cacheConfigurations.put(HOME_GROUPS_CACHE,
                defaultCacheConfiguration.entryTtl(Duration.ofMinutes(30)));

        cacheConfigurations.put(STATES_CITIES_CACHE,
                defaultCacheConfiguration.entryTtl(Duration.ofHours(24)));

        cacheConfigurations.put(SESSION_CACHE,
                defaultCacheConfiguration.entryTtl(Duration.ofHours(8)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultCacheConfiguration)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }
}
