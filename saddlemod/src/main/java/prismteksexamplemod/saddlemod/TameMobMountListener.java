package prismteksexamplemod.saddlemod;

import necesse.engine.GameEventListener;
import necesse.engine.GameEvents;
import necesse.engine.events.players.MobInteractEvent;
import necesse.engine.network.packet.PacketMobMount;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.HusbandryMob;
import necesse.inventory.InventorySlot;
import necesse.inventory.PlayerInventoryManager;

import java.lang.reflect.Field;

public class TameMobMountListener extends GameEventListener<MobInteractEvent> {
    private static final float TAME_THRESHOLD = 1.0f;
    private static final Field TAMENESS_FIELD = initTamenessField();

    public static void register() {
        GameEvents.addListener(MobInteractEvent.class, new TameMobMountListener());
    }

    @Override
    public void onEvent(MobInteractEvent event) {
        if (event.isPrevented()) {
            return;
        }

        if (!(event.mob instanceof HusbandryMob)) {
            return;
        }

        PlayerMob player = event.player;
        if (!player.isServerClient()) {
            return;
        }

        ServerClient client = player.getServerClient();
        if (client == null) {
            return;
        }

        HusbandryMob husbandry = (HusbandryMob) event.mob;
        if (!husbandry.isGrown() || getTameness(husbandry) < TAME_THRESHOLD) {
            return;
        }

        if (!hasSaddleEquipped(player)) {
            return;
        }

        if (husbandry.isMounted()) {
            Mob rider = husbandry.getRider();
            if (rider == null || rider.getUniqueID() != player.getUniqueID()) {
                return;
            }

            player.dismount();
            sendMountPacket(client, -1, false, player);
            event.preventDefault();
            return;
        }

        Mob currentMount = player.getMount();
        if (currentMount != null) {
            player.dismount();
            sendMountPacket(client, -1, false, player);
        }

        if (husbandry.getMountDismountError(player, null) != null) {
            return;
        }

        if (player.mount(husbandry, false)) {
            sendMountPacket(client, husbandry.getUniqueID(), false, player);
            event.preventDefault();
        }
    }

    private static Field initTamenessField() {
        try {
            Field field = HusbandryMob.class.getDeclaredField("tameness");
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    private static float getTameness(HusbandryMob mob) {
        if (TAMENESS_FIELD == null) {
            return 0.0f;
        }
        try {
            return TAMENESS_FIELD.getFloat(mob);
        } catch (IllegalAccessException e) {
            return 0.0f;
        }
    }

    private static boolean hasSaddleEquipped(PlayerMob player) {
        InventorySlot slot = player.getInv().equipment.getSelectedEquipmentSlot(PlayerInventoryManager.EQUIPMENT_MOUNT_SLOT);
        return slot != null && "saddle".equals(slot.getItemStringID());
    }

    private static void sendMountPacket(ServerClient client, int mountUniqueID, boolean setMounterPos, PlayerMob player) {
        client.getServer().network.sendToClientsWithEntity(
                new PacketMobMount(client.slot, mountUniqueID, setMounterPos, player.x, player.y),
                player
        );
    }
}
