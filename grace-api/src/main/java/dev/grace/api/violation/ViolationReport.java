package dev.grace.api.violation;

import java.util.UUID;

import dev.grace.api.check.CheckCategory;
import dev.grace.api.check.CheckTier;

public record ViolationReport(
    UUID PlayerId,
    String PlayerName,
    String CheckName,
    CheckCategory Category,
    CheckTier Tier,
    EvidenceWeight Evidence,
    String Context,
    long Timestamp
) {
    // convenience — is this report serious enough to act on
    public boolean isActionable() {
        return Tier.getLevel() >= CheckTier.HIGH.getLevel();
    }
}