-- Migration Script: Convert existing datetime columns to timestamptz (UTC)
-- This script assumes existing data was stored in America/Bogota timezone

-- =====================================================
-- WORSHIP_SERVICES TABLE
-- =====================================================

-- Step 1: Convert scheduled_date from timestamp to timestamptz
-- Assumes existing values are in America/Bogota (-05:00)
ALTER TABLE worship_services
    ALTER COLUMN scheduled_date TYPE TIMESTAMPTZ
    USING scheduled_date AT TIME ZONE 'America/Bogota';

-- Step 2: Convert creation_date from timestamp to timestamptz
ALTER TABLE worship_services
    ALTER COLUMN creation_date TYPE TIMESTAMPTZ
    USING creation_date AT TIME ZONE 'America/Bogota';

-- =====================================================
-- GROUP_MEETINGS TABLE
-- =====================================================

-- Convert date column from timestamp to timestamptz
ALTER TABLE group_meetings
    ALTER COLUMN date TYPE TIMESTAMPTZ
    USING date AT TIME ZONE 'America/Bogota';

-- =====================================================
-- NOTES:
-- =====================================================
-- 1. After migration, all timestamps will be stored in UTC
-- 2. PostgreSQL will automatically convert to/from UTC
-- 3. Application should send OffsetDateTime with timezone info
-- 4. Example valid inputs:
--    - "2026-01-07T01:34:00Z" (UTC)
--    - "2026-01-06T20:34:00-05:00" (Colombia time)
-- 5. Backend will always return UTC timestamps

