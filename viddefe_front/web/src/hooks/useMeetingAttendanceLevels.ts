import { useQuery } from '@tanstack/react-query';
import { meetingAttendanceService } from '../services/meetingAttendanceService';

export function useMeetingAttendanceLevels() {
  return useQuery({
    queryKey: ['meetingAttendanceLevels'],
    queryFn: meetingAttendanceService.getLevels,
  });
}
