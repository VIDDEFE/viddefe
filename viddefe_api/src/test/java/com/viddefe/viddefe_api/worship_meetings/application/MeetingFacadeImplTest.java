package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.churches.contracts.ChurchLookup;
import com.viddefe.viddefe_api.homeGroups.contracts.HomeGroupReader;
import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.contracts.AttendanceService;
import com.viddefe.viddefe_api.worship_meetings.contracts.GroupMeetingService;
import com.viddefe.viddefe_api.worship_meetings.contracts.WorshipService;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.MeetingRepository;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.CreateMeetingDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.GroupMeetingDetailedDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.MeetingDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.WorshipDetailedDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MeetingFacadeImpl Tests")
class MeetingFacadeImplTest {

    @Mock
    private WorshipService worshipService;

    @Mock
    private GroupMeetingService groupMeetingService;

    @Mock
    private AttendanceService attendanceService;

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private ChurchLookup churchLookup;

    @Mock
    private HomeGroupReader homeGroupReader;

    @InjectMocks
    private MeetingFacadeImpl meetingFacade;

    private UUID contextId;
    private UUID meetingId;
    private UUID churchId;
    private CreateMeetingDto createMeetingDto;
    private MeetingDto meetingDto;

    @BeforeEach
    void setUp() {
        contextId = UUID.randomUUID();
        meetingId = UUID.randomUUID();
        churchId = UUID.randomUUID();

        createMeetingDto = new CreateMeetingDto();
        createMeetingDto.setName("Sunday Service");

        meetingDto = new MeetingDto();
        meetingDto.setId(meetingId);
        meetingDto.setName("Sunday Service");
    }

    @Nested
    @DisplayName("createMeeting Tests")
    class CreateMeetingTests {

        @Test
        @DisplayName("Should delegate to worshipService for TEMPLE_WORSHIP")
        void shouldDelegateToWorshipServiceForTempleWorship() {
            when(worshipService.createWorship(createMeetingDto, contextId)).thenReturn(meetingDto);

            MeetingDto result = meetingFacade.createMeeting(
                    createMeetingDto, contextId, TopologyEventType.TEMPLE_WORHSIP, churchId);

            assertThat(result).isEqualTo(meetingDto);
            verify(worshipService).createWorship(createMeetingDto, contextId);
            verify(groupMeetingService, never()).createGroupMeeting(any(), any(), any());
        }

        @Test
        @DisplayName("Should delegate to groupMeetingService for GROUP_MEETING")
        void shouldDelegateToGroupMeetingServiceForGroupMeeting() {
            when(groupMeetingService.createGroupMeeting(createMeetingDto, contextId, churchId)).thenReturn(meetingDto);

            MeetingDto result = meetingFacade.createMeeting(
                    createMeetingDto, contextId, TopologyEventType.GROUP_MEETING, churchId);

            assertThat(result).isEqualTo(meetingDto);
            verify(groupMeetingService).createGroupMeeting(createMeetingDto, contextId, churchId);
            verify(worshipService, never()).createWorship(any(), any());
        }
    }

    @Nested
    @DisplayName("getMeetingById Tests")
    class GetMeetingByIdTests {

        @Test
        @DisplayName("Should delegate to worshipService for TEMPLE_WORSHIP")
        void shouldDelegateToWorshipServiceForTempleWorship() {
            when(worshipService.getWorshipById(meetingId)).thenReturn(mock(WorshipDetailedDto.class));

            meetingFacade.getMeetingById(contextId, meetingId, TopologyEventType.TEMPLE_WORHSIP);

            verify(worshipService).getWorshipById(meetingId);
        }

        @Test
        @DisplayName("Should delegate to groupMeetingService for GROUP_MEETING")
        void shouldDelegateToGroupMeetingServiceForGroupMeeting() {
            when(groupMeetingService.getGroupMeetingById(contextId, meetingId)).thenReturn(mock(GroupMeetingDetailedDto.class));

            meetingFacade.getMeetingById(contextId, meetingId, TopologyEventType.GROUP_MEETING);

            verify(groupMeetingService).getGroupMeetingById(contextId, meetingId);
        }
    }

    @Nested
    @DisplayName("getAllMeetings Tests")
    class GetAllMeetingsTests {

        @Test
        @DisplayName("Should return paginated worships for TEMPLE_WORSHIP")
        void shouldReturnPaginatedWorshipsForTempleWorship() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<MeetingDto> expectedPage = new PageImpl<>(List.of(meetingDto));
            when(worshipService.getAllWorships(pageable, contextId)).thenReturn(expectedPage);

            Page<MeetingDto> result = meetingFacade.getAllMeetings(contextId, TopologyEventType.TEMPLE_WORHSIP, pageable);

            assertThat(result.getContent()).hasSize(1);
            verify(worshipService).getAllWorships(pageable, contextId);
        }

        @Test
        @DisplayName("Should return paginated group meetings for GROUP_MEETING")
        void shouldReturnPaginatedGroupMeetingsForGroupMeeting() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<MeetingDto> expectedPage = new PageImpl<>(List.of(meetingDto));
            when(groupMeetingService.getGroupMeetingByGroupId(contextId, pageable)).thenReturn(expectedPage);

            Page<MeetingDto> result = meetingFacade.getAllMeetings(contextId, TopologyEventType.GROUP_MEETING, pageable);

            assertThat(result.getContent()).hasSize(1);
            verify(groupMeetingService).getGroupMeetingByGroupId(contextId, pageable);
        }
    }

    @Nested
    @DisplayName("updateMeeting Tests")
    class UpdateMeetingTests {

        @Test
        @DisplayName("Should delegate to worshipService for TEMPLE_WORSHIP")
        void shouldDelegateToWorshipServiceForTempleWorship() {
            when(worshipService.updateWorship(meetingId, createMeetingDto, contextId)).thenReturn(meetingDto);

            MeetingDto result = meetingFacade.updateMeeting(
                    createMeetingDto, contextId, meetingId, TopologyEventType.TEMPLE_WORHSIP);

            assertThat(result).isEqualTo(meetingDto);
            verify(worshipService).updateWorship(meetingId, createMeetingDto, contextId);
        }

        @Test
        @DisplayName("Should delegate to groupMeetingService for GROUP_MEETING")
        void shouldDelegateToGroupMeetingServiceForGroupMeeting() {
            when(groupMeetingService.updateGroupMeeting(createMeetingDto, contextId, meetingId)).thenReturn(meetingDto);

            MeetingDto result = meetingFacade.updateMeeting(
                    createMeetingDto, contextId, meetingId, TopologyEventType.GROUP_MEETING);

            assertThat(result).isEqualTo(meetingDto);
            verify(groupMeetingService).updateGroupMeeting(createMeetingDto, contextId, meetingId);
        }
    }

    @Nested
    @DisplayName("deleteMeeting Tests")
    class DeleteMeetingTests {

        @Test
        @DisplayName("Should delegate to worshipService for TEMPLE_WORSHIP")
        void shouldDelegateToWorshipServiceForTempleWorship() {
            doNothing().when(worshipService).deleteWorship(meetingId);

            meetingFacade.deleteMeeting(contextId, meetingId, TopologyEventType.TEMPLE_WORHSIP);

            verify(worshipService).deleteWorship(meetingId);
            verify(groupMeetingService, never()).deleteGroupMeeting(any(), any());
        }

        @Test
        @DisplayName("Should delegate to groupMeetingService for GROUP_MEETING")
        void shouldDelegateToGroupMeetingServiceForGroupMeeting() {
            doNothing().when(groupMeetingService).deleteGroupMeeting(contextId, meetingId);

            meetingFacade.deleteMeeting(contextId, meetingId, TopologyEventType.GROUP_MEETING);

            verify(groupMeetingService).deleteGroupMeeting(contextId, meetingId);
            verify(worshipService, never()).deleteWorship(any());
        }
    }
}

