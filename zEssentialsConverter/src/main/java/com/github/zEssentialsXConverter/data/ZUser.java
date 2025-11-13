package com.github.zEssentialsXConverter.data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ZUser {
    private UUID uniqueId;
    private String name;
    private Map<String, BigDecimal> balances;
    private List<Home> homes;

    public UUID getUniqueId() {
        return uniqueId;
    }

    public String getName() {
        return name;
    }

    public Map<String, BigDecimal> getBalances() {
        return balances;
    }

    public List<Home> getHomes() {
        return homes;
    }
}
