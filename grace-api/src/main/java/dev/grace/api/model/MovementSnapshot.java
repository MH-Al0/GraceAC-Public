package dev.grace.api.model;

public record MovementSnapshot(
    double X,
    double Y,
    double Z,
    float Yaw,
    float Pitch,
    boolean OnGround,
    double DeltaX,
    double DeltaY,
    double DeltaZ,
    long Timestamp
) {
    // calc horiz speed from deltas
    public double getHorizontalSpeed() {
        return Math.sqrt(DeltaX * DeltaX + DeltaZ * DeltaZ);
    }

    public double getVerticalSpeed() {
        return Math.abs(DeltaY);
    }
}