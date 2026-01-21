package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.worship_meetings.contracts.MinistryFunctionReminderSentWriter;
import com.viddefe.viddefe_api.worship_meetings.domain.models.MinistryFunction;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.MinistryFunctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class MinistryFunctionReminderSentWriterImpl implements MinistryFunctionReminderSentWriter {
    private final MinistryFunctionRepository ministryFunctionRepository;

    @Override
    public void writeMinistryFunctionReminderSent(MinistryFunction ministryFunction) {
        ministryFunction.setReminderSentAt(Instant.now());
        ministryFunctionRepository.save(ministryFunction);
    }
}
