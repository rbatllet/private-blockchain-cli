-- =========================================================================
-- V1: Create Initial Blockchain Schema
-- =========================================================================
-- Description: Baseline migration for blockchain database schema
-- Author:      Private Blockchain CLI
-- Date:        2025-10-18
-- =========================================================================
--
-- This migration serves as a baseline for tracking schema version.
-- The actual tables (blocks, authorized_keys, off_chain_data, etc.)
-- are created by Hibernate's hbm2ddl.auto=update feature.
--
-- This migration is automatically applied when JPAUtil initializes
-- a new database, establishing a tracked baseline for future migrations.
-- =========================================================================

-- Dummy statement to make migration valid
-- (Actual schema is managed by Hibernate)
SELECT 1;
