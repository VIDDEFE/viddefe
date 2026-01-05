import type { WorshipDetail, OfferingType, Person, OfferingAnalytics } from '../../../models';

// Tipo extendido para la tabla de asistencia
export interface AttendanceTableItem {
  id: string;
  fullName: string;
  phone: string;
  avatar?: string;
  typePerson: string;
  status: string;
  isPresent: boolean;
  peopleId: string;
}

// Tipo extendido para la tabla de ofrendas
export interface OfferingTableItem {
  id: string;
  personName: string;
  avatar?: string;
  typeName: string;
  amount: number;
  peopleId: string | null;
  offeringTypeId: number;
}

// Props comunes
export interface WorshipInfoProps {
  readonly worship: WorshipDetail;
}

export interface AttendanceSummaryProps {
  readonly worship: WorshipDetail;
}

export interface OfferingsSectionProps {
  readonly offeringTableData: OfferingTableItem[];
  readonly offeringTotalElements: number;
  readonly offeringTotalPages: number;
  readonly totalOfferingsAmount: number;
  readonly analytics: OfferingAnalytics[] | null;
  readonly isLoading: boolean;
  readonly viewMode: 'table' | 'cards';
  readonly onViewModeChange: (mode: 'table' | 'cards') => void;
  readonly currentPage: number;
  readonly pageSize: number;
  readonly onPageChange: (page: number) => void;
  readonly onPageSizeChange: (size: number) => void;
  readonly onAddOffering: () => void;
  readonly onEditOffering: (offering: OfferingTableItem) => void;
  readonly onDeleteOffering: (id: string) => void;
}

export interface OfferingFormData {
  amount: string;
  peopleId: string;
  offeringTypeId: string;
}

export interface OfferingModalProps {
  readonly isOpen: boolean;
  readonly onClose: () => void;
  readonly formData: OfferingFormData;
  readonly onFormChange: (data: OfferingFormData) => void;
  readonly onSave: () => void;
  readonly isEditing: boolean;
  readonly isSaving: boolean;
  readonly offeringTypes: OfferingType[];
  readonly people: Person[];
}

export interface DeleteOfferingModalProps {
  readonly isOpen: boolean;
  readonly onClose: () => void;
  readonly onConfirm: () => void;
  readonly isDeleting: boolean;
}
