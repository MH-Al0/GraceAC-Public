package dev.grace.companion.listener;

import dev.grace.api.model.CombatSnapshot;
import dev.grace.companion.bridge.DataBridge;
import dev.grace.companion.logging.SnapshotLogger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public final class CombatListener implements Listener {

    private final DataBridge     Bridge;
    private final SnapshotLogger Logger;

    // track last hit time per player for TimeSinceLastHit calc
    private final java.util.concurrent.ConcurrentHashMap<java.util.UUID, Long> LastHitTime
        = new java.util.concurrent.ConcurrentHashMap<>();

    public CombatListener(DataBridge Bridge, SnapshotLogger Logger) {
        this.Bridge = Bridge;
        this.Logger = Logger;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAttack(EntityDamageByEntityEvent Event) {
        if (!(Event.getDamager() instanceof Player Attacker)) return;
        if (!(Event.getEntity() instanceof Player Target)) return;

        long Now          = System.currentTimeMillis();
        long LastHit      = LastHitTime.getOrDefault(Attacker.getUniqueId(), Now);
        long TimeSinceLast = Now - LastHit;

        LastHitTime.put(Attacker.getUniqueId(), Now);

        org.bukkit.Location AttackerLoc = Attacker.getLocation();
        org.bukkit.Location TargetLoc   = Target.getLocation();

        double Distance  = AttackerLoc.distance(TargetLoc);
        float YawDelta   = Math.abs(AttackerLoc.getYaw()   - TargetLoc.getYaw());
        float PitchDelta = Math.abs(AttackerLoc.getPitch() - TargetLoc.getPitch());

        // crit -> falling and not on ground
        boolean WasCrit = !Attacker.isOnGround()
            && Attacker.getFallDistance() > 0;

        CombatSnapshot Snapshot = new CombatSnapshot(
            Attacker.getUniqueId(),
            Target.getUniqueId(),
            Distance,
            YawDelta,
            PitchDelta,
            TimeSinceLast,
            WasCrit,
            Now
        );

        Bridge.sendCombat(Attacker.getUniqueId(), Snapshot);
        Logger.logCombat(Attacker.getUniqueId(), Snapshot);
    }
}