package dev.grace.companion.listener;

import dev.grace.api.model.MovementSnapshot;
import dev.grace.companion.bridge.DataBridge;
import dev.grace.companion.logging.SnapshotLogger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class MovementListener implements Listener {

    private final DataBridge     Bridge;
    private final SnapshotLogger Logger;

    public MovementListener(DataBridge Bridge, SnapshotLogger Logger) {
        this.Bridge = Bridge;
        this.Logger = Logger;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent Event) {
        if (!Event.hasChangedPosition()) return;

        org.bukkit.Location From = Event.getFrom();
        org.bukkit.Location To   = Event.getTo();

        MovementSnapshot Snapshot = new MovementSnapshot(
            To.getX(),
            To.getY(),
            To.getZ(),
            To.getYaw(),
            To.getPitch(),
            Event.getPlayer().isOnGround(),
            To.getX() - From.getX(),
            To.getY() - From.getY(),
            To.getZ() - From.getZ(),
            System.currentTimeMillis()
        );

        Bridge.sendMovement(Event.getPlayer().getUniqueId(), Snapshot);
        Logger.logMovement(Event.getPlayer().getUniqueId(), Snapshot);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent Event) {
        Logger.flushPlayer(Event.getPlayer().getUniqueId());
    }
}