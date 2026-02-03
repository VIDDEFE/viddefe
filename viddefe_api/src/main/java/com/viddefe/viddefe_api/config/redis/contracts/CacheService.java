package com.viddefe.viddefe_api.config.redis.contracts;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Contrato para operaciones de caché.
 */
public interface CacheService {

    /**
     * Almacena un valor en caché.
     *
     * @param key   Clave única del valor
     * @param value Valor a almacenar
     * @param <T>   Tipo del valor
     */
    <T> void put(String key, T value);

    /**
     * Almacena un valor en caché con TTL personalizado.
     *
     * @param key     Clave única del valor
     * @param value   Valor a almacenar
     * @param ttl     Tiempo de vida del valor
     * @param <T>     Tipo del valor
     */
    <T> void put(String key, T value, Duration ttl);

    /**
     * Obtiene un valor de la caché.
     *
     * @param key  Clave del valor
     * @param type Clase del tipo esperado
     * @param <T>  Tipo del valor
     * @return Optional con el valor si existe
     */
    <T> Optional<T> get(String key, Class<T> type);

    /**
     * Obtiene un valor de la caché, o lo calcula y almacena si no existe.
     *
     * Este método implementa el patrón "cache-aside" de forma atómica.
     *
     * @param key      Clave del valor
     * @param type     Clase del tipo esperado
     * @param supplier Proveedor del valor si no existe en caché
     * @param <T>      Tipo del valor
     * @return El valor de la caché o el calculado
     */
    <T> T getOrCompute(String key, Class<T> type, Supplier<T> supplier);

    /**
     * Obtiene un valor de la caché, o lo calcula y almacena con TTL personalizado.
     *
     * @param key      Clave del valor
     * @param type     Clase del tipo esperado
     * @param supplier Proveedor del valor si no existe en caché
     * @param ttl      Tiempo de vida del valor
     * @param <T>      Tipo del valor
     * @return El valor de la caché o el calculado
     */
    <T> T getOrCompute(String key, Class<T> type, Supplier<T> supplier, Duration ttl);

    /**
     * Verifica si existe un valor en la caché.
     *
     * @param key Clave a verificar
     * @return true si existe
     */
    boolean exists(String key);

    /**
     * Elimina un valor de la caché.
     *
     * @param key Clave del valor a eliminar
     * @return true si se eliminó correctamente
     */
    boolean delete(String key);

    /**
     * Elimina todos los valores que coincidan con el patrón.
     *
     * @param pattern Patrón de claves (ej: "users:*")
     * @return Cantidad de claves eliminadas
     */
    long deleteByPattern(String pattern);

    /**
     * Obtiene todas las claves que coinciden con un patrón.
     *
     * @param pattern Patrón de claves
     * @return Set de claves encontradas
     */
    Set<String> getKeysByPattern(String pattern);

    /**
     * Actualiza el TTL de una clave existente.
     *
     * @param key Clave a actualizar
     * @param ttl Nuevo TTL
     * @return true si se actualizó correctamente
     */
    boolean expire(String key, Duration ttl);

    /**
     * Obtiene el TTL restante de una clave.
     *
     * @param key Clave a consultar
     * @return Duración restante, o Duration.ZERO si no existe o no tiene TTL
     */
    Duration getTimeToLive(String key);
}
