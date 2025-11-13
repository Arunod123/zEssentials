package com.github.zEssentialsXConverter.data;

import org.bukkit.Location;

public class Home {
    private SafeLocation location;
    private String name;

    public Location getLocation() {
        return location.getLocation();
    }

    public String getName() {
        return name;
    }
}
