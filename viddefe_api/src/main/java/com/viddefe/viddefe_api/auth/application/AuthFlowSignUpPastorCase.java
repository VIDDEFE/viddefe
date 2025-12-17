package com.viddefe.viddefe_api.auth.application;

import com.viddefe.viddefe_api.people.contracts.PeopleLookup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public  class AuthFlowSignUpPastorCase {
    static public Boolean pastorHasPreviousPersonalCreation(PeopleLookup peopleLookup,String cc) {
        try {
            peopleLookup.getPastorByCcWithoutChurch(cc);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
