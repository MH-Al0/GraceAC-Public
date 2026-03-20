package dev.grace.api.check;

import dev.grace.api.player.IGracePlayer;
import dev.grace.api.violation.ViolationReport;

import java.util.Optional;

public interface ICheck {

    // check name e.g. "KillAura", "Reach"
    String getName();

    // category this check belongs to
    CheckCategory getCategory();

    // max tier this check can escalate to
    CheckTier getMaxTier();

    // core evaluate — returns a report if flagged, empty if clean
    Optional<ViolationReport> evaluate(IGracePlayer Player);
}