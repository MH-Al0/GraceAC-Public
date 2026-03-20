package dev.grace.api.violation;

import dev.grace.api.check.CheckTier;

public record EvidenceWeight(
    String CheckName,
    double Confidence,  // 0.0 - 1.0
    CheckTier Tier,
    String Detail
) {
    public EvidenceWeight {
        if (Confidence < 0.0 || Confidence > 1.0)
            throw new IllegalArgumentException("Confidence must be between 0.0 and 1.0");
    }
}