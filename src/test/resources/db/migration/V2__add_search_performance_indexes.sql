-- =========================================================================
-- V2: Add Search Performance Indexes
-- =========================================================================
-- Description: Optimize blockchain search operations with targeted indexes
-- Author:      Private Blockchain CLI
-- Date:        2025-10-18
-- =========================================================================
--
-- This migration adds performance indexes for frequently searched fields:
-- - blocks.timestamp: For date-range searches
-- - blocks.previous_hash: For chain traversal
-- - blocks.hash: For hash lookups
-- - authorized_keys.fingerprint: For key searches
-- - off_chain_data.hash: For off-chain data retrieval
--
-- Expected Impact:
-- - Search by date: 10x faster
-- - Chain validation: 5x faster
-- - Hash lookups: Near-instant
-- =========================================================================

-- Index for timestamp-based searches
CREATE INDEX IF NOT EXISTS idx_blocks_timestamp ON blocks(timestamp);

-- Index for blockchain traversal via previous_hash
CREATE INDEX IF NOT EXISTS idx_blocks_previous_hash ON blocks(previous_hash);

-- Index for hash-based lookups
CREATE INDEX IF NOT EXISTS idx_blocks_hash ON blocks(hash);

-- Index for authorized key fingerprint searches
CREATE INDEX IF NOT EXISTS idx_authorized_keys_fingerprint ON authorized_keys(fingerprint);

-- Index for off-chain data hash lookups
CREATE INDEX IF NOT EXISTS idx_off_chain_data_hash ON off_chain_data(hash);

-- Index for off-chain data by block_id (foreign key optimization)
CREATE INDEX IF NOT EXISTS idx_off_chain_data_block_id ON off_chain_data(block_id);
