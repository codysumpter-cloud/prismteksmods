package grizzlycubsummon;

import java.lang.reflect.Field;

import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.Item;

public final class VanillaVisuals {
    private VanillaVisuals() {}

    /**
     * Resolve a vanilla mob texture using (1) static field on the mob class and (2) common file paths.
     */
    public static GameTexture resolveMobTexture(String mobStringID) {
        // 1) Mob class static field 'texture'
        try {
            int id = MobRegistry.getMobID(mobStringID);
            Object mob = MobRegistry.getMob(id);
            if (mob != null) {
                try {
                    Field f = mob.getClass().getField("texture");
                    Object o = f.get(null); // static
                    if (o instanceof GameTexture) return (GameTexture) o;
                } catch (Throwable ignored) {}
            }
        } catch (Throwable ignored) {}

        // 2) File path fallbacks
        for (String path : new String[] {
                "mobs/" + mobStringID,
                "pets/" + mobStringID,
                "mobs/pets/" + mobStringID,
                "player/pets/" + mobStringID
        }) {
            try {
                GameTexture t = GameTexture.fromFile(path);
                if (t != null) return t;
            } catch (Throwable ignored) {}
        }

        return null;
    }

    /**
     * Apply a vanilla mob icon to a custom item (no item PNG required).
     */
    public static void applyItemIconFromMob(String itemStringID, String mobStringID) {
        try {
            Item item = ItemRegistry.getItem(itemStringID);
            if (item == null) return;

            int mobID = MobRegistry.getMobID(mobStringID);
            GameTexture icon = MobRegistry.getMobIcon(mobID);
            if (icon == null) return;

            setField(item, "itemTexture", icon);
            setField(item, "texture", icon);
        } catch (Throwable ignored) {}
    }

    private static void setField(Item item, String fieldName, GameTexture tex) {
        // Try public field first, then declared
        try {
            Field f = item.getClass().getField(fieldName);
            f.set(item, tex);
            return;
        } catch (Throwable ignored) {}

        try {
            Field f = item.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(item, tex);
        } catch (Throwable ignored) {}
    }
}
