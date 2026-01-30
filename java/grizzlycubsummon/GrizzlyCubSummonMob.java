package grizzlycubsummon;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerFollowerCollisionChaserAI;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class GrizzlyCubSummonMob extends AttackingFollowingMob {

    // Evolution threshold: upgraded staff at or above this tier uses adult bear visuals + stats
    public static final int EVOLVE_AT_TIER = 3;

    public static GameTexture cubTexture;
    public static GameTexture bearTexture;

    // Written by staff on spawn
    public int evolutionTier = 0;
    public int ownerUniqueID = -1;

    // Pounce/charge
    private int chargeTicks = 0;
    private int cooldownTicks = 0;

    private static final int CHARGE_TIME = 10;
    private static final int CHARGE_COOLDOWN = 120;

    private static final int MIN_DIST = 60;
    private static final int MAX_DIST = 170;

    private static final float NORMAL_SPEED_CUB = 70f;
    private static final float CHARGE_SPEED_CUB = 110f;

    private static final float NORMAL_SPEED_ADULT = 62f;
    private static final float CHARGE_SPEED_ADULT = 96f;

    private static final float SPEED_SMOOTHING = 0.18f;

    // Bleed-on-pounce
    private static final int BLEED_RANGE = 42;
    private static final int BLEED_DURATION_MS = 2400;
    private int bleedGateTicks = 0;

    private float smoothSpeed = NORMAL_SPEED_CUB;

    // Pack
    private int packCheckTicks = 0;
    private int packCountCached = 1;

    public GrizzlyCubSummonMob() {
        super(70);

        setFriction(3.2f);
        setSpeed(NORMAL_SPEED_CUB);

        collision = new Rectangle(-11, -8, 22, 16);
        hitBox = new Rectangle(-15, -13, 30, 26);
        selectBox = new Rectangle(-16, -46, 32, 56);
    }

    private boolean isEvolved() {
        return evolutionTier >= EVOLVE_AT_TIER;
    }

    private GameTexture getActiveTexture() {
        return isEvolved() ? bearTexture : cubTexture;
    }

    private int getSpriteSize() {
        return isEvolved() ? 128 : 64;
    }

    private int getBaseDrawYOffset() {
        return isEvolved() ? 36 : 13;
    }

    Mob getAICurrentTarget() {
        if (this.ai == null) {
            return null;
        }
        return this.ai.blackboard.getObject(Mob.class, "currentTarget");
    }

    @Override
    public void init() {
        super.init();

        this.ai = new BehaviourTreeAI(this,
                new PlayerFollowerCollisionChaserAI(
                        360,
                        this.summonDamage,
                        isEvolved() ? 28 : 22, // evolved hits harder in feel (knockback)
                        isEvolved() ? 520 : 460,
                        360,
                        isEvolved() ? 58 : 52
                )
        );
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList,
                                Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);

        GameTexture texture = getActiveTexture();
        if (texture == null) {
            return;
        }

        GameLight light = level.getLightLevel(getTileX(), getTileY());
        int spriteSize = getSpriteSize();
        int drawX = camera.getDrawX(x) - spriteSize / 2;
        int drawY = camera.getDrawY(y) - spriteSize + getBaseDrawYOffset();

        Point sprite = getAnimSprite(x, y, getDir());
        drawY += getBobbing(x, y);
        drawY += getLevel().getTile(getTileX(), getTileY()).getMobSinkingAmount(this);

        DrawOptions drawOptions = texture.initDraw()
                .sprite(sprite.x, sprite.y, spriteSize)
                .light(light)
                .pos(drawX, drawY);

        list.add(new MobDrawable() {
            @Override
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });

        addShadowDrawables(tileList, level, x, y, light, camera);
    }

    @Override
    public void serverTick() {
        super.serverTick();

        // Pack behavior
        if (packCheckTicks-- <= 0) {
            packCheckTicks = 30;
            packCountCached = PackUtil.countPackMates(getLevel(), this, ownerUniqueID, 260);
            if (packCountCached < 1) packCountCached = 1;

            Mob shared = PackUtil.findSharedTarget(getLevel(), this, ownerUniqueID, 340);
            if (shared != null) {
                PackUtil.trySetTarget(this, shared);
            }
        }

        int extra = Math.min(3, packCountCached - 1);
        float speedBonus = 1.0f + 0.05f * extra;

        boolean evolved = isEvolved();
        float normalSpeed = evolved ? NORMAL_SPEED_ADULT : NORMAL_SPEED_CUB;
        float chargeSpeed = evolved ? CHARGE_SPEED_ADULT : CHARGE_SPEED_CUB;

        // Timers
        if (cooldownTicks > 0) cooldownTicks--;
        if (chargeTicks > 0) chargeTicks--;
        if (bleedGateTicks > 0) bleedGateTicks--;

        // Apply movement state (smoothed to avoid abrupt speed jumps)
        float targetSpeed = (chargeTicks > 0 ? chargeSpeed : normalSpeed) * speedBonus;
        if (smoothSpeed <= 0.0f) {
            smoothSpeed = targetSpeed;
        } else {
            smoothSpeed += (targetSpeed - smoothSpeed) * SPEED_SMOOTHING;
        }
        setSpeed(smoothSpeed);

        // Start pounce window
        if (cooldownTicks == 0 && chargeTicks == 0) {
            Mob target = getAICurrentTarget();
            if (target != null && target.isSamePlace(this) && !target.removed()) {
                int dx = target.getX() - getX();
                int dy = target.getY() - getY();
                int dist = (int) Math.sqrt(dx * dx + dy * dy);

                if (dist >= MIN_DIST && dist <= MAX_DIST) {
                    chargeTicks = CHARGE_TIME;
                    cooldownTicks = CHARGE_COOLDOWN;
                }
            }
        }

        // Bleed on pounce (once per short window, gated)
        if (chargeTicks > 0 && bleedGateTicks == 0) {
            Mob target = getAICurrentTarget();
            if (target != null && target.isSamePlace(this) && !target.removed()) {
                int dx = target.getX() - getX();
                int dy = target.getY() - getY();
                int dist = (int) Math.sqrt(dx * dx + dy * dy);

                if (dist <= BLEED_RANGE) {
                    BuffUtil.applyBleed(target, BLEED_DURATION_MS, this);
                    bleedGateTicks = 30;
                }
            }
        }
    }

    @Override
    public GameMessage getLocalization() {
        return new LocalMessage("mob", "grizzlycubsummon");
    }

    @Override
    public int getRockSpeed() {
        return 20;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(evolutionTier);
        writer.putNextInt(ownerUniqueID);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        evolutionTier = reader.getNextInt();
        ownerUniqueID = reader.getNextInt();
    }
}
