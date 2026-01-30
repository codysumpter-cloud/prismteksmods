package prismteksexamplemod.saddlemod;

import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.mountItem.MountItem;
import necesse.level.maps.Level;

public class SaddleMountItem extends MountItem {
    public SaddleMountItem() {
        super(null);
        singleUse = false;
    }

    @Override
    public String canUseMount(InventoryItem item, PlayerMob player, Level level) {
        return null;
    }

    @Override
    public InventoryItem useMount(ServerClient client, float x, float y, InventoryItem item, Level level) {
        return item;
    }
}
