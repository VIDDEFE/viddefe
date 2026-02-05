package com.viddefe.viddefe_api.worship_meetings.contracts;

import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.domain.models.TopologyMeetingModel;

public interface TopologyMeetingReader {
    /**
     * Busca y devuelve un modelo de reunión topológica basado en el tipo de evento proporcionado.
     *
     * @param type El tipo de evento topológico a buscar.
     * @return El modelo de reunión topológica correspondiente al tipo de evento.
     */
    TopologyMeetingModel findByTopologyMeetingEnum(TopologyEventType type);
}
