package prismteksexamplemod.druidmod;

import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LineHitbox;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.MagicProjectileToolItem;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;
import necesse.level.maps.Level;

import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class DruidFocusItem extends MagicProjectileToolItem implements ItemInteractAction {

    private static final String FORM_KEY = "druidForm";
    private static final float FORM_COOLDOWN_SECONDS = 3f;

    private static final int HUMAN_ANIM_TIME = 350;
    private static final int HUMAN_COOLDOWN_TIME = 350;
    private static final int WOLF_ANIM_TIME = 220;
    private static final int WOLF_COOLDOWN_TIME = 220;
    private static final int BEAR_ANIM_TIME = 600;
    private static final int BEAR_COOLDOWN_TIME = 600;

    private static final int HUMAN_RANGE = 700;
    private static final int WOLF_RANGE = 90;
    private static final int BEAR_RANGE = 120;

    private static final int HUMAN_KNOCKBACK = 40;
    private static final int WOLF_KNOCKBACK = 70;
    private static final int BEAR_KNOCKBACK = 140;

    private static final float HUMAN_DAMAGE = 24f;
    private static final float WOLF_DAMAGE = 18f;
    private static final float BEAR_DAMAGE = 40f;

    private static final float WOLF_HITBOX_WIDTH = 20f;
    private static final float BEAR_HITBOX_WIDTH = 46f;

    private final FloatUpgradeValue wolfDamage = new FloatUpgradeValue();
    private final FloatUpgradeValue bearDamage = new FloatUpgradeValue();
    private final IntUpgradeValue wolfRange = new IntUpgradeValue();
    private final IntUpgradeValue bearRange = new IntUpgradeValue();
    private final IntUpgradeValue wolfKnockback = new IntUpgradeValue();
    private final IntUpgradeValue bearKnockback = new IntUpgradeValue();

    public DruidFocusItem() {
        super(400, null);
        rarity = Item.Rarity.UNCOMMON;
        attackAnimTime.setBaseValue(HUMAN_ANIM_TIME);
        attackCooldownTime.setBaseValue(HUMAN_COOLDOWN_TIME);
        attackDamage.setBaseValue(HUMAN_DAMAGE);
        attackRange.setBaseValue(HUMAN_RANGE);
        knockback.setBaseValue(HUMAN_KNOCKBACK);
        velocity.setBaseValue(120);
        manaCost.setBaseValue(6f);

        attackXOffset = 12;
        attackYOffset = 22;

        wolfDamage.setBaseValue(WOLF_DAMAGE);
        bearDamage.setBaseValue(BEAR_DAMAGE);
        wolfRange.setBaseValue(WOLF_RANGE);
        bearRange.setBaseValue(BEAR_RANGE);
        wolfKnockback.setBaseValue(WOLF_KNOCKBACK);
        bearKnockback.setBaseValue(BEAR_KNOCKBACK);
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "druidfocustip"), 400);
        return tooltips;
    }

    @Override
    public GameDamage getFlatAttackDamage(InventoryItem item) {
        DruidForm form = getFormFromItem(item);
        float tier = getUpgradeTier(item);
        if (form == DruidForm.WOLF) {
            return new GameDamage(getDamageType(item), wolfDamage.getValue(tier));
        }
        if (form == DruidForm.BEAR) {
            return new GameDamage(getDamageType(item), bearDamage.getValue(tier));
        }
        return super.getFlatAttackDamage(item);
    }

    @Override
    public int getAttackRange(InventoryItem item) {
        DruidForm form = getFormFromItem(item);
        float tier = getUpgradeTier(item);
        if (form == DruidForm.WOLF) {
            return wolfRange.getValue(tier);
        }
        if (form == DruidForm.BEAR) {
            return bearRange.getValue(tier);
        }
        return super.getAttackRange(item);
    }

    @Override
    public int getKnockback(InventoryItem item, necesse.entity.mobs.Attacker attacker) {
        DruidForm form = getFormFromItem(item);
        float tier = getUpgradeTier(item);
        if (form == DruidForm.WOLF) {
            return wolfKnockback.getValue(tier);
        }
        if (form == DruidForm.BEAR) {
            return bearKnockback.getValue(tier);
        }
        return super.getKnockback(item, attacker);
    }

    @Override
    public int getLifeSteal(InventoryItem item) {
        return getFormFromItem(item) == DruidForm.WOLF ? 3 : 0;
    }

    @Override
    public int getAttackAnimTime(InventoryItem item, ItemAttackerMob attackerMob) {
        DruidForm form = getForm(attackerMob, item);
        if (form == DruidForm.WOLF) {
            return WOLF_ANIM_TIME;
        }
        if (form == DruidForm.BEAR) {
            return BEAR_ANIM_TIME;
        }
        return super.getAttackAnimTime(item, attackerMob);
    }

    @Override
    public int getAttackCooldownTime(InventoryItem item, ItemAttackerMob attackerMob) {
        DruidForm form = getForm(attackerMob, item);
        if (form == DruidForm.WOLF) {
            return WOLF_COOLDOWN_TIME;
        }
        if (form == DruidForm.BEAR) {
            return BEAR_COOLDOWN_TIME;
        }
        return super.getAttackCooldownTime(item, attackerMob);
    }

    @Override
    public necesse.entity.mobs.gameDamageType.DamageType getDamageType(InventoryItem item) {
        DruidForm form = getFormFromItem(item);
        if (form == DruidForm.WOLF || form == DruidForm.BEAR) {
            return necesse.engine.registries.DamageTypeRegistry.MELEE;
        }
        return necesse.engine.registries.DamageTypeRegistry.MAGIC;
    }

    @Override
    public ArrayList<Shape> getHitboxes(InventoryItem item, AttackAnimMob mob, int dirX, int dirY, ToolItemMobAbilityEvent event, boolean firstHitbox) {
        ArrayList<Shape> shapes = new ArrayList<>();
        DruidForm form = getFormFromItem(item);
        float width = form == DruidForm.BEAR ? BEAR_HITBOX_WIDTH : WOLF_HITBOX_WIDTH;

        int range = getAttackRange(item);
        Point2D.Float dir = GameMath.normalize(dirX, dirY);
        Line2D.Float line = new Line2D.Float(mob.x, mob.y, mob.x + dir.x * range, mob.y + dir.y * range);

        shapes.add(new LineHitbox(line, width));
        return shapes;
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (getForm(attackerMob, item) == DruidForm.HUMAN) {
            if (level.isClient()) {
                SoundManager.playSound(GameResources.magicbolt1, SoundEffect.effect(attackerMob).volume(0.7f));
            }
            return;
        }
        super.showAttack(level, x, y, attackerMob, attackHeight, item, animAttack, seed, mapContent);
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        DruidForm form = getForm(attackerMob, item);
        syncItemForm(attackerMob, item, form);

        if (form == DruidForm.HUMAN) {
            Projectile projectile = new DruidNatureBoltProjectile(
                    level,
                    attackerMob,
                    attackerMob.x,
                    attackerMob.y,
                    x,
                    y,
                    getProjectileVelocity(item, attackerMob),
                    getAttackRange(item),
                    getAttackDamage(item),
                    getKnockback(item, attackerMob)
            );
            GameRandom random = new GameRandom(seed);
            projectile.resetUniqueID(random);
            projectile.moveDist(40);
            attackerMob.addAndSendAttackerProjectile(projectile);
            consumeMana(attackerMob, item);
            return item;
        }

        return super.onAttack(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
    }

    @Override
    public boolean canLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return attackerMob != null
                && attackerMob.isPlayer
                && attackerMob.buffManager.hasBuff("druidsetbonus")
                && !attackerMob.buffManager.hasBuff("druidformcooldown");
    }

    @Override
    public InventoryItem onLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int seed, GNDItemMap mapContent) {
        if (level.isClient() || attackerMob == null || !attackerMob.isPlayer) {
            return item;
        }
        if (!attackerMob.buffManager.hasBuff("druidsetbonus") || attackerMob.buffManager.hasBuff("druidformcooldown")) {
            return item;
        }

        DruidForm current = getForm(attackerMob, item);
        DruidForm next = current.next();
        DruidSetBonusBuff.setForm(attackerMob, next);
        setItemForm(item, next);
        attackerMob.buffManager.addBuff(new necesse.entity.mobs.buffs.ActiveBuff("druidformcooldown", attackerMob, FORM_COOLDOWN_SECONDS, null), false);
        return item;
    }

    @Override
    public float getItemCooldownPercent(InventoryItem item, PlayerMob player) {
        if (player == null) {
            return 0f;
        }
        float left = player.buffManager.getBuffDurationLeftSeconds("druidformcooldown");
        if (left <= 0) {
            return 0f;
        }
        return left / FORM_COOLDOWN_SECONDS;
    }

    private DruidForm getForm(ItemAttackerMob attackerMob, InventoryItem item) {
        if (attackerMob != null) {
            return DruidSetBonusBuff.getForm(attackerMob);
        }
        return getFormFromItem(item);
    }

    private DruidForm getFormFromItem(InventoryItem item) {
        GNDItemMap data = item.getGndData();
        return DruidForm.fromOrdinal(data.getInt(FORM_KEY, DruidForm.HUMAN.ordinal()));
    }

    private void setItemForm(InventoryItem item, DruidForm form) {
        item.getGndData().setInt(FORM_KEY, form.ordinal());
    }

    private void syncItemForm(ItemAttackerMob attackerMob, InventoryItem item, DruidForm form) {
        if (attackerMob != null && attackerMob.getLevel() != null && attackerMob.getLevel().isServer()) {
            setItemForm(item, form);
        }
    }
}
