package prismteksexamplemod.everythingmountmod;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.Localization;
import necesse.engine.registries.MobRegistry;
import necesse.gfx.gameTexture.GameTexture;
import necesse.entity.mobs.Mob;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.PlayerMob;
import necesse.engine.util.GameBlackboard;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.mountItem.MountItem;
import necesse.level.maps.Level;

import java.util.HashMap;
import java.util.Map;

public class MountEverythingMod extends MountItem {
    private static final Map<String, GameTexture> ICON_TEXTURES = new HashMap<>();
    private final GameMessage displayName;
    private final String baseMobId;

    public MountEverythingMod(String mountMobId, String baseMobId, GameMessage displayName) {
        super(mountMobId);
        this.baseMobId = baseMobId;
        this.displayName = displayName;
        singleUse = false;
    }

    @Override
    public GameMessage getNewLocalization() {
        return displayName != null ? displayName : super.getNewLocalization();
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        if (baseMobId != null && !tooltips.isEmpty()) {
            tooltips.removeLast();
            String baseName = MobRegistry.getDisplayName(MobRegistry.getMobID(baseMobId));
            if (baseName == null || baseName.isEmpty()) {
                baseName = baseMobId;
            }
            tooltips.add(Localization.translate("itemtooltip", "summonmounttip", "mob", baseName));
        }
        return tooltips;
    }

    @Override
    protected void loadItemTextures() {
        GameTexture iconTexture = ICON_TEXTURES.get(baseMobId);
        if (iconTexture == null) {
            iconTexture = MobRegistry.getMobIcon(baseMobId);
            if (iconTexture == null) {
                iconTexture = GameTexture.fromFile("mobs/icons/" + baseMobId);
            }
            ICON_TEXTURES.put(baseMobId, iconTexture);
        }
        itemTexture = iconTexture;
    }

    @Override
    protected void loadHoldTextures() {
        holdTexture = itemTexture;
    }

    @Override
    protected void loadAttackTexture() {
        attackTexture = itemTexture;
    }

    @Override
    protected void beforeSpawn(Mob mob, InventoryItem item, PlayerMob player) {
        if (mob instanceof MobMountMob) {
            ((MobMountMob) mob).setBaseMobId(baseMobId);
        }
    }

    @Override
    public InventoryItem useMount(ServerClient client, float x, float y, InventoryItem item, Level level) {
        PlayerMob player = client.playerMob;
        Mob currentMount = player.getMount();
        if (currentMount != null) {
            player.dx = currentMount.dx;
            player.dy = currentMount.dy;

            boolean matches = currentMount.getStringID().equals(mobStringID);
            if (!matches && currentMount instanceof MobMountMob) {
                String mountBaseId = ((MobMountMob) currentMount).getBaseMobIdForMountItem();
                if (mountBaseId != null && mountBaseId.equals(baseMobId)) {
                    matches = true;
                }
            }

            if (matches) {
                player.buffManager.removeBuff("summonedmount", true);
                if (singleUse) {
                    item.setAmount(item.getAmount() - 1);
                }
                return item;
            }
        }
        return super.useMount(client, x, y, item, level);
    }
}
