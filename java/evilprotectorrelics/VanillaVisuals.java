package evilprotectorrelics;

import java.lang.reflect.Field;

import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.Item;

public final class VanillaVisuals {
    private VanillaVisuals() {}

    /**
     * Resolve a vanilla mob texture.
     * Priority:
     *  1) Mob class static field 'texture' (many vanilla mobs).
     *  2) Load by file path 'mobs/<id>' which resolves to vanilla resources.
     */
    public static GameTexture resolveMobTexture(String mobStringID) {
        String textureID = mapMobTextureID(mobStringID);

        try {
            int id = MobRegistry.getMobID(mobStringID);
            Object mob = MobRegistry.getMob(id);
            if (mob != null) {
                try {
                    Field f = mob.getClass().getField("texture");
                    Object o = f.get(null);
                    if (o instanceof GameTexture) return (GameTexture) o;
                } catch (Throwable ignored) { }
            }
        } catch (Throwable ignored) { }

        for (String path : new String[] {
                "mobs/" + textureID,
                "pets/" + textureID,
                "mobs/pets/" + textureID,
                "player/pets/" + textureID
        }) {
            try {
                GameTexture t = GameTexture.fromFile(path);
                if (t != null) return t;
            } catch (Throwable ignored) { }
        }

        return null;
    }

    /**
     * Apply a vanilla mob icon as the inventory icon for a custom item.
     * This avoids shipping any item PNGs.
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
        } catch (Throwable ignored) { }
    }

    private static void setField(Item item, String fieldName, GameTexture tex) {
        try {
            Field f = item.getClass().getField(fieldName);
            f.set(item, tex);
            return;
        } catch (Throwable ignored) { }

        try {
            Field f = item.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(item, tex);
        } catch (Throwable ignored) { }
    }

    private static String mapMobTextureID(String mobStringID) {
        if ("petevilminion".equals(mobStringID)) {
            return "evilminion";
        }
        return mobStringID;
    }
}
