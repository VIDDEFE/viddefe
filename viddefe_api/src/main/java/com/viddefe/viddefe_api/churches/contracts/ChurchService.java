package com.viddefe.viddefe_api.churches.contracts;

import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchDTO;
import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchDetailedResDto;
import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ChurchService {
    /**     * Agrega una nueva iglesia.
     *
     * @param dto DTO con los datos de la iglesia a agregar.
     * @return DTO de la iglesia creada.
     */
    ChurchResDto addChurch(ChurchDTO dto);
    /**     * Agrega una iglesia hija a una iglesia padre existente.
     *
     * @param parentChurchId  ID de la iglesia padre.
     * @param dto             DTO con los datos de la iglesia hija a agregar.
     * @param creatorPastorId ID del pastor que crea la iglesia hija.
     * @return DTO de la iglesia hija creada.
     */
    ChurchResDto addChildChurch(UUID parentChurchId, ChurchDTO dto, UUID creatorPastorId);
    /**     * Actualiza una iglesia existente.
     *
     * @param id               ID de la iglesia a actualizar.
     * @param dto              DTO con los datos actualizados de la iglesia.
     * @param updaterPastorId  ID del pastor que realiza la actualización.
     * @return DTO de la iglesia actualizada.
     */
    ChurchResDto updateChurch(UUID id, ChurchDTO dto, UUID updaterPastorId);
    /**     * Elimina una iglesia por su ID.
     *
     * @param id ID de la iglesia a eliminar.
     */
    void deleteChurch(UUID id);
    /**
     * Obtiene una página de iglesias hijas de una iglesia dada.
     *
     * @param pageable  Información de paginación.
     * @param churchId  ID de la iglesia padre.
     * @return Página de DTOs de iglesias hijas.
     */
    Page<ChurchResDto> getChildrenChurches(Pageable pageable, UUID churchId);
    /**
     * Obtiene los detalles de una iglesia por su ID.
     *
     * @param id ID de la iglesia.
     * @return DTO detallado de la iglesia.
     */
    ChurchDetailedResDto getChurchById(UUID id);
    /**
     * Obtiene las iglesias hijas de una iglesia dada, ordenadas por distancia a una posición geográfica.
     *
     * @param churchId  ID de la iglesia padre.
     * @param southLat  Latitud sur del área de interés.
     * @param westLng   Longitud oeste del área de interés.
     * @param northLat  Latitud norte del área de interés.
     * @param eastLng   Longitud este del área de interés.
     * @return Lista de DTOs de iglesias hijas ordenadas por distancia a la posición dada.
     */
    List<ChurchResDto> getChildrenChurchesByPositionInMap(UUID churchId,BigDecimal southLat,
                                                          BigDecimal westLng,
                                                          BigDecimal northLat,
                                                          BigDecimal eastLng);
}

