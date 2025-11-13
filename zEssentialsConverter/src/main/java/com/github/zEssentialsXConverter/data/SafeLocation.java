package com.github.zEssentialsXConverter.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class SafeLocation {
    private String world;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public Location getLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }
}
