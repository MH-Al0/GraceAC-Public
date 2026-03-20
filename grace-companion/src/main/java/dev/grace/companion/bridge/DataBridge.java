package dev.grace.companion.bridge;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import dev.grace.api.model.CombatSnapshot;
import dev.grace.api.model.MovementSnapshot;

public final class DataBridge {

    private final JavaPlugin        Plugin;
    private final FileConfiguration Config;
    private final Logger            Log;

    private Socket      Connection;
    private PrintWriter Writer;
    private boolean     Connected = false;

    public DataBridge(JavaPlugin Plugin, FileConfiguration Config, Logger Log) {
        this.Plugin = Plugin;
        this.Config = Config;
        this.Log    = Log;
    }

    public void connect() {
        String Host = Config.getString("bridge.host", "127.0.0.1");
        int    Port = Config.getInt("bridge.port", 27016);
        attemptConnect(Host, Port);
    }

    private void attemptConnect(String Host, int Port) {
        try {
            Connection = new Socket(Host, Port);
            Writer     = new PrintWriter(Connection.getOutputStream(), true);
            Connected  = true;
            Log.info("DataBridge connected to Grace proxy at " + Host + ":" + Port);
        } catch (Exception Ex) {
            Log.warning("DataBridge could not connect to Grace proxy: " + Ex.getMessage());
            Connected = false;
            scheduleReconnect(Host, Port);
        }
    }

    // retry every 30s until connected
    private void scheduleReconnect(String Host, int Port) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(Plugin, () -> {
            if (!Connected) {
                Log.info("DataBridge attempting reconnect...");
                attemptConnect(Host, Port);
            }
        }, 600L);
    }

    public void disconnect() {
        try {
            if (Writer != null)     Writer.close();
            if (Connection != null) Connection.close();
        } catch (Exception Ex) {
            Log.warning("DataBridge disconnect error: " + Ex.getMessage());
        }
        Connected = false;
    }

    public void sendMovement(UUID PlayerId, MovementSnapshot Snapshot) {
        if (!Connected) return;
        try {
            Writer.println("MOV:" + PlayerId + ":" + serialiseMovement(Snapshot));
        } catch (Exception Ex) {
            Log.warning("DataBridge send movement failed: " + Ex.getMessage());
            Connected = false;
        }
    }

    public void sendCombat(UUID PlayerId, CombatSnapshot Snapshot) {
        if (!Connected) return;
        try {
            Writer.println("CMB:" + PlayerId + ":" + serialiseCombat(Snapshot));
        } catch (Exception Ex) {
            Log.warning("DataBridge send combat failed: " + Ex.getMessage());
            Connected = false;
        }
    }

    // mov snapshot -> CSV line
    private String serialiseMovement(MovementSnapshot S) {
        return S.X() + "," + S.Y() + "," + S.Z() + ","
            + S.Yaw() + "," + S.Pitch() + ","
            + S.OnGround() + ","
            + S.DeltaX() + "," + S.DeltaY() + "," + S.DeltaZ() + ","
            + S.Timestamp();
    }

    // combat snapshot -> CSV line
    private String serialiseCombat(CombatSnapshot S) {
        return S.AttackerId() + "," + S.TargetId() + ","
            + S.Distance() + ","
            + S.YawDelta() + "," + S.PitchDelta() + ","
            + S.TimeSinceLastHit() + ","
            + S.WasCrit() + ","
            + S.Timestamp();
    }

    public boolean isConnected() { return Connected; }
}