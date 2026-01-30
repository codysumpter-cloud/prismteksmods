package prismteksexamplemod.druidmod;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.BootsArmorItem;
import necesse.inventory.lootTable.presets.FeetArmorLootTable;

public class DruidBootsArmorItem extends BootsArmorItem {

    public DruidBootsArmorItem() {
        super(6, 100, Item.Rarity.UNCOMMON, "druidboots", FeetArmorLootTable.feetArmor);
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(
                new ModifierValue(BuffModifiers.SPEED, 0.15f),
                new ModifierValue(BuffModifiers.ATTACK_SPEED, 0.10f)
        );
    }
}
