package com.viddefe.viddefe_api.churches.contracts;

import java.util.List;
import java.util.UUID;

public interface  ChurchMemberShip {
    List<UUID> getPeopleIdsByChurchId(UUID churchId);
}
