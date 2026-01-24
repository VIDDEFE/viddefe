import type { AttendaceQualityDto } from '../models';
import { apiService } from './api';

export const meetingAttendanceService = {
  getLevels: () => apiService.get<AttendaceQualityDto[]>('/meetings/attedance/levels'),
};
