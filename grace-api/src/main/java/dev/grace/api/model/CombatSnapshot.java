package dev.grace.api.model;

import java.util.UUID;

public record CombatSnapshot(
    UUID AttackerId,
    UUID TargetId,
    double Distance,
    float YawDelta,
    float PitchDelta,
    long TimeSinceLastHit,
    boolean WasCrit,
    long Timestamp
) {}

