package evilprotectorrelics;

import java.lang.reflect.Field;

import necesse.engine.GameEvents;
import necesse.engine.GameEventListener;
import necesse.engine.events.loot.MobLootTableDropsEvent;
import necesse.engine.registries.MobRegistry;
import necesse.entity.mobs.Mob;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;

public final class EvilProtectorDropInjector {
    private EvilProtectorDropInjector() {}

    /**
     * Guaranteed boss drops:
     *  1) Static lootTable injection when exposed (PolarBearMountMod pattern).
     *  2) MobLootTableDropsEvent fallback (EverythingsaMountMod pattern).
     */
    public static void install(final String evilProtectorStringID,
                               final boolean dropBoth,
                               final String relicA,
                               final String relicB) {

        if (tryInjectStaticLootTable(evilProtectorStringID, dropBoth, relicA, relicB)) {
            return;
        }

        GameEvents.addListener(MobLootTableDropsEvent.class, new GameEventListener<MobLootTableDropsEvent>() {
            @Override
            public void onEvent(MobLootTableDropsEvent event) {
                if (event == null || event.mob == null) return;

                if (event.mob.isSummoned) return;
                if (!event.mob.dropsLoot) return;
                if (!evilProtectorStringID.equals(event.mob.getStringID())) return;

                if (dropBoth) {
                    addIfMissing(event, relicA);
                    addIfMissing(event, relicB);
                } else {
                    addIfMissing(event, relicA);
                }
            }

            private void addIfMissing(MobLootTableDropsEvent event, String itemID) {
                for (InventoryItem it : event.drops) {
                    if (it != null && it.item != null && itemID.equals(it.item.getStringID())) return;
                }
                event.drops.add(new InventoryItem(itemID));
            }
        });
    }

    private static boolean tryInjectStaticLootTable(String mobStringID, boolean dropBoth, String relicA, String relicB) {
        try {
            int id = MobRegistry.getMobID(mobStringID);
            Mob mob = MobRegistry.getMob(id);
            if (mob == null) return false;

            Field f = mob.getClass().getField("lootTable");
            Object o = f.get(null); // static field
            if (!(o instanceof LootTable)) return false;

            LootTable table = (LootTable) o;
            addLootItemIfMissing(table, relicA);
            if (dropBoth) addLootItemIfMissing(table, relicB);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static void addLootItemIfMissing(LootTable table, String itemID) {
        for (LootItemInterface li : table.items) {
            if (li instanceof LootItem) {
                LootItem lootItem = (LootItem) li;
                if (itemID.equals(lootItem.itemStringID)) return;
            }
        }
        table.items.add(new LootItem(itemID));
    }
}
