package com.viddefe.viddefe_api.auth.Infrastructure.dto;

import com.viddefe.viddefe_api.auth.Config.AuthFlowPastorEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class AuthProcessResponse<T> {

    private boolean completed;
    private AuthFlowPastorEnum nextStep;
    private T data;

    public static <T> AuthProcessResponse<T> completed(T data) {
        return new AuthProcessResponse<>(true, AuthFlowPastorEnum.DONE, data);
    }

    public static <T> AuthProcessResponse<T> pending(
            AuthFlowPastorEnum nextStep,
            T data
    ) {
        return new AuthProcessResponse<>(false, nextStep, data);
    }
}
