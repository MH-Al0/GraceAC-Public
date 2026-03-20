package dev.grace.api.player;

import java.util.List;
import java.util.UUID;

import dev.grace.api.model.CombatSnapshot;
import dev.grace.api.model.MovementSnapshot;
import dev.grace.api.violation.ViolationReport;

public interface IGracePlayer {

    UUID getUuid();

    String getName();

    // recent movement history, newest last
    List<MovementSnapshot> getMovementHistory();

    // recent combat history, newest last
    List<CombatSnapshot> getCombatHistory();

    // all violations accumulated this session
    List<ViolationReport> getViolations();

    // latest movement snapshot
    MovementSnapshot getLatestMovement();

    // latest combat snapshot, if any combat has occurred
    CombatSnapshot getLatestCombat();

    int getPing();

    boolean isOnGround();
}