package com.hostflow.marketing.entity;

/**
 * SIMPLIFIED: previously included GENERATING/READY/GENERATION_FAILED, all tied to
 * the now-removed AI content generation pipeline. A campaign is now just a
 * planning record (DRAFT -> PUBLISHED -> ARCHIVED); actual content creation is a
 * manual/external process, or will be produced via the Claude API integration
 * (module-ai, built separately) rather than this module owning generation state.
 */
public enum CampaignStatus {
    DRAFT,
    PUBLISHED,
    ARCHIVED
}
