package dev.grace.companion;

import org.bukkit.plugin.java.JavaPlugin;

import dev.grace.companion.bridge.DataBridge;
import dev.grace.companion.listener.CombatListener;
import dev.grace.companion.listener.MovementListener;
import dev.grace.companion.logging.SnapshotLogger;

public final class GraceCompanionPlugin extends JavaPlugin {

    private DataBridge     Bridge;
    private SnapshotLogger Logger;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.Logger = new SnapshotLogger(getDataFolder(), getLogger());
        this.Bridge = new DataBridge(this, getConfig(), getLogger());        this.Bridge = new DataBridge(this, getConfig(), getLogger());

        getServer().getPluginManager().registerEvents(
            new MovementListener(Bridge, Logger), this
        );
        getServer().getPluginManager().registerEvents(
            new CombatListener(Bridge, Logger), this
        );

        Bridge.connect();
        getLogger().info("GraceCompanion enabled");
    }

    @Override
    public void onDisable() {
        Bridge.disconnect();
        Logger.flush();
        getLogger().info("GraceCompanion disabled");
    }
}
