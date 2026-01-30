package prismteksexamplemod.druidmod;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.ChestArmorItem;
import necesse.inventory.lootTable.presets.BodyArmorLootTable;

public class DruidChestArmorItem extends ChestArmorItem {

    public DruidChestArmorItem() {
        super(12, 100, Item.Rarity.UNCOMMON, "druidchest", "druidchestarms", BodyArmorLootTable.bodyArmor);
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(
                new ModifierValue(BuffModifiers.MAX_HEALTH, 0.15f),
                new ModifierValue(BuffModifiers.HEALTH_REGEN_FLAT, 0.5f)
        );
    }
}
