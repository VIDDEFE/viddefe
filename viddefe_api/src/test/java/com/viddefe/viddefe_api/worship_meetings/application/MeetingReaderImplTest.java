package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.worship_meetings.domain.models.Meeting;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.MeetingRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MeetingReaderImpl Tests")
class MeetingReaderImplTest {

    @Mock
    private MeetingRepository meetingRepository;

    @InjectMocks
    private MeetingReaderImpl meetingReader;

    private UUID meetingId;
    private Meeting meeting;

    @BeforeEach
    void setUp() {
        meetingId = UUID.randomUUID();
        meeting = new Meeting();
        meeting.setId(meetingId);
        meeting.setName("Sunday Service");
    }

    @Nested
    @DisplayName("getById Tests")
    class GetByIdTests {

        @Test
        @DisplayName("Should return meeting when found")
        void shouldReturnMeetingWhenFound() {
            when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(meeting));

            Meeting result = meetingReader.getById(meetingId);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(meetingId);
            assertThat(result.getName()).isEqualTo("Sunday Service");
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when not found")
        void shouldThrowWhenNotFound() {
            when(meetingRepository.findById(meetingId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> meetingReader.getById(meetingId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Meeting not found");
        }
    }
}

