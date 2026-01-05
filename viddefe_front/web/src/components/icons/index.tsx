import React from "react";
import { 
  FiBarChart2, 
  FiUsers, 
  FiCalendar, 
  FiMenu, 
  FiX, 
  FiLogOut 
} from "react-icons/fi";
import { MdChurch } from "react-icons/md";
import { GiPrayer } from "react-icons/gi";

function IconWrapper({ children }: { children: React.ReactNode }) {
  return <span className="inline-flex w-6 h-6 items-center justify-center">{children}</span>;
}

const ICON_SIZE = 20;

export function IconDashboard() {
  return (
    <IconWrapper>
      <FiBarChart2 size={ICON_SIZE} />
    </IconWrapper>
  );
}

export function IconChurch() {
  return (
    <IconWrapper>
      <MdChurch size={ICON_SIZE} />
    </IconWrapper>
  );
}

export function IconPeople() {
  return (
    <IconWrapper>
      <FiUsers size={ICON_SIZE} />
    </IconWrapper>
  );
}

export function IconServices() {
  return (
    <IconWrapper>
      <FiCalendar size={ICON_SIZE} />
    </IconWrapper>
  );
}

export function IconGroups() {
  return (
    <IconWrapper>
      <FiUsers size={ICON_SIZE} />
    </IconWrapper>
  );
}

export function IconEvents() {
  return (
    <IconWrapper>
      <FiCalendar size={ICON_SIZE} />
    </IconWrapper>
  );
}

export function IconWorship() {
  return (
    <IconWrapper>
      <GiPrayer size={ICON_SIZE} />
    </IconWrapper>
  );
}

export function IconLogout() {
  return (
    <IconWrapper>
      <FiLogOut size={ICON_SIZE} />
    </IconWrapper>
  );
}

export function IconMenu() {
  return (
    <IconWrapper>
      <FiMenu size={ICON_SIZE} />
    </IconWrapper>
  );
}

export function IconClose() {
  return (
    <IconWrapper>
      <FiX size={ICON_SIZE} />
    </IconWrapper>
  );
}
