package dev.grace.companion.logging;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dev.grace.api.model.CombatSnapshot;
import dev.grace.api.model.MovementSnapshot;

public final class SnapshotLogger {

    private final File   DataFolder;
    private final Logger Log;
    private final Gson   Gson;

    // per-player in-memory buffer before flush
    private final ConcurrentHashMap<UUID, List<MovementSnapshot>> MovementBuffer
        = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, List<CombatSnapshot>> CombatBuffer
        = new ConcurrentHashMap<>();

    private static final int FlushThreshold = 500;

    public SnapshotLogger(File DataFolder, Logger Log) {
        this.DataFolder = new File(DataFolder, "snapshots");
        this.Log        = Log;
        this.Gson       = new GsonBuilder().setPrettyPrinting().create();
        this.DataFolder.mkdirs();
    }

    public void logMovement(UUID PlayerId, MovementSnapshot Snapshot) {
        MovementBuffer.computeIfAbsent(PlayerId, Id -> new ArrayList<>()).add(Snapshot);
        if (MovementBuffer.get(PlayerId).size() >= FlushThreshold)
            flushPlayer(PlayerId);
    }

    public void logCombat(UUID PlayerId, CombatSnapshot Snapshot) {
        CombatBuffer.computeIfAbsent(PlayerId, Id -> new ArrayList<>()).add(Snapshot);
    }

    // flush single player buffers to disk
    public void flushPlayer(UUID PlayerId) {
        flushMovement(PlayerId);
        flushCombat(PlayerId);
    }

    // flush all players — called on plugin disable
    public void flush() {
        MovementBuffer.keySet().forEach(this::flushMovement);
        CombatBuffer.keySet().forEach(this::flushCombat);
    }

    private void flushMovement(UUID PlayerId) {
        List<MovementSnapshot> Snapshots = MovementBuffer.remove(PlayerId);
        if (Snapshots == null || Snapshots.isEmpty()) return;
        writeJson(PlayerId, "movement", Snapshots);
    }

    private void flushCombat(UUID PlayerId) {
        List<CombatSnapshot> Snapshots = CombatBuffer.remove(PlayerId);
        if (Snapshots == null || Snapshots.isEmpty()) return;
        writeJson(PlayerId, "combat", Snapshots);
    }

    private void writeJson(UUID PlayerId, String Type, List<?> Data) {
        File PlayerDir = new File(DataFolder, PlayerId.toString());
        PlayerDir.mkdirs();

        File Out = new File(PlayerDir, Type + "_" + System.currentTimeMillis() + ".json");
        try (FileWriter Writer = new FileWriter(Out)) {
            Gson.toJson(Data, Writer);
        } catch (IOException Ex) {
            Log.warning("SnapshotLogger failed to write " + Type + " for " + PlayerId + ": " + Ex.getMessage());
        }
    }
}