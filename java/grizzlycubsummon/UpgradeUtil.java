package grizzlycubsummon;

import java.lang.reflect.Method;

import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public final class UpgradeUtil {
    private UpgradeUtil() {}

    /**
     * Returns an "upgrade tier" integer for an InventoryItem.
     * Uses reflection to support mapping differences. If not found, returns 0.
     */
    public static int getUpgradeTierSafe(InventoryItem item) {
        if (item == null || item.item == null) return 0;

        Item base = item.item;

        // Item.getUpgradeTier(InventoryItem) -> float in 1.1.1
        try {
            Method m = base.getClass().getMethod("getUpgradeTier", InventoryItem.class);
            Object o = m.invoke(base, item);
            if (o instanceof Float) return (int) Math.floor((Float) o);
            if (o instanceof Integer) return (Integer) o;
        } catch (Throwable ignored) {}

        // Item.getUpgradeLevel(InventoryItem) -> int, scaled by 100
        try {
            Method m = base.getClass().getMethod("getUpgradeLevel", InventoryItem.class);
            Object o = m.invoke(base, item);
            if (o instanceof Integer) return Math.max(0, (Integer) o / 100);
        } catch (Throwable ignored) {}

        return 0;
    }
}
