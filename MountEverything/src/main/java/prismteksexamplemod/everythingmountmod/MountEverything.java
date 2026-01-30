package prismteksexamplemod.everythingmountmod;

import necesse.engine.GameEventListener;
import necesse.engine.GameEvents;
import necesse.engine.events.loot.MobLootTableDropsEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.registries.ClassIDDataContainer;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.summon.summonFollowingMob.mountFollowingMob.MountFollowingMob;
import necesse.inventory.InventoryItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ModEntry
public class MountEverything {
    private static final String BULL_MOB_ID = "bull";
    private static final String BULL_MOUNT_ITEM_ID = "bullmount";
    private static final String BULL_MOUNT_MOB_ID = "bullmountmob";
    private static final String MOUNT_ITEM_PREFIX = "mount_";
    private static final String MOUNT_MOB_PREFIX = "mountmob_";
    private static final Map<String, String> MOB_TO_MOUNT_ITEM = new HashMap<>();

    public void init() {
        registerBullMount();
        registerCreatureMounts();
        registerDropListener();
    }

    private void registerBullMount() {
        GameMessage bullMountName = buildMountName(MobRegistry.getLocalization(BULL_MOB_ID), BULL_MOB_ID);
        registerMount(BULL_MOB_ID, BULL_MOUNT_ITEM_ID, BULL_MOUNT_MOB_ID, bullMountName);
    }

    private void registerCreatureMounts() {
        List<String> baseMobIds = new ArrayList<>();
        for (ClassIDDataContainer<Mob> mobData : MobRegistry.getMobs()) {
            if (!isBaseGameMob(mobData)) {
                continue;
            }
            String mobId = mobData.getIDData().getStringID();
            if (mobId == null) {
                continue;
            }
            if (mobId.equals(BULL_MOB_ID)) {
                continue;
            }
            if (mobId.equals(BULL_MOUNT_MOB_ID)) {
                continue;
            }
            if (mobId.startsWith(MOUNT_MOB_PREFIX)) {
                continue;
            }
            baseMobIds.add(mobId);
        }

        for (String mobId : baseMobIds) {
            String mountMobId = MOUNT_MOB_PREFIX + mobId;
            String mountItemId = MOUNT_ITEM_PREFIX + mobId;
            GameMessage mountName = buildMountName(MobRegistry.getLocalization(mobId), mobId);
            registerMount(mobId, mountItemId, mountMobId, mountName);
        }
    }

    private void registerMount(String baseMobId, String mountItemId, String mountMobId, GameMessage displayName) {
        if (!MobRegistry.mobExists(mountMobId)) {
            MobRegistry.registerMob(mountMobId, MobMountMob.class, false, false, displayName, null, false);
        }
        if (!ItemRegistry.itemExists(mountItemId)) {
            ItemRegistry.registerItem(mountItemId, new MountEverythingMod(mountMobId, baseMobId, displayName), 100, true, true);
        }
        if (ItemRegistry.itemExists(mountItemId)) {
            MOB_TO_MOUNT_ITEM.put(baseMobId, mountItemId);
        }
    }

    private boolean isBaseGameMob(ClassIDDataContainer<Mob> mobData) {
        if (mobData == null || mobData.getIDData() == null) {
            return false;
        }
        Class<? extends Mob> mobClass = mobData.getIDData().aClass;
        if (mobClass == null) {
            return false;
        }
        String className = mobClass.getName();
        return className != null && className.startsWith("necesse.");
    }

    private void registerDropListener() {
        GameEvents.addListener(MobLootTableDropsEvent.class, new GameEventListener<MobLootTableDropsEvent>() {
            @Override
            public void onEvent(MobLootTableDropsEvent event) {
                if (event.mob instanceof MountFollowingMob) {
                    return;
                }
                if (event.mob.isSummoned || !event.mob.dropsLoot) {
                    return;
                }
                if (!hasPlayerKill(event.mob)) {
                    return;
                }
                String itemId = MOB_TO_MOUNT_ITEM.get(event.mob.getStringID());
                if (itemId == null) {
                    return;
                }
                for (InventoryItem drop : event.drops) {
                    if (drop.item != null && drop.item.getStringID().equals(itemId)) {
                        return;
                    }
                }
                event.drops.add(new InventoryItem(itemId));
            }
        });
    }

    private boolean hasPlayerKill(Mob mob) {
        if (mob == null) {
            return false;
        }
        for (Attacker attacker : mob.getAttackers()) {
            if (attacker != null && attacker.getFirstPlayerOwner() != null) {
                return true;
            }
        }
        return false;
    }

    private GameMessage buildMountName(GameMessage baseName, String fallbackId) {
        GameMessageBuilder builder = new GameMessageBuilder();
        if (baseName != null) {
            builder.append(baseName);
        } else {
            builder.append(fallbackId);
        }
        builder.append(" Mount");
        return builder;
    }

    public void initResources() {
        MobMountMob.clearTextureCache();
    }
}
