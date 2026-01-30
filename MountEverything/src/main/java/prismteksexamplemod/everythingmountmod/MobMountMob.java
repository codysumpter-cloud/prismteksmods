package prismteksexamplemod.everythingmountmod;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.leaves.PlayerFollowerAINode;
import necesse.entity.mobs.summon.summonFollowingMob.mountFollowingMob.MountFollowingMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.engine.registries.MobRegistry;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MobMountMob extends MountFollowingMob {
    private static final String BULL_MOB_ID = "bull";
    private static final String BULL_MOUNT_MOB_ID = "bullmountmob";
    private static final String MOUNT_MOB_PREFIX = "mountmob_";
    private static final Map<String, GameTexture> TEXTURES = new HashMap<>();
    private static final Map<String, Integer> SPRITE_SIZES = new HashMap<>();
    private static final Map<Class<?>, Method> ADD_DRAWABLES_CACHE = new HashMap<>();
    private static final Map<Class<?>, Field> SLEEP_FIELDS = new HashMap<>();
    private static final Field DISTANCE_RAN_FIELD = findMobField("distanceRan");

    private String baseMobId;
    private transient Mob visualMob;
    private transient Level visualMobLevel;
    private transient String visualMobId;
    private transient boolean boundsSynced;

    public static void clearTextureCache() {
        TEXTURES.clear();
        SPRITE_SIZES.clear();
    }

    // MUST HAVE an empty constructor
    public MobMountMob() {
        super(200);
        setSpeed(100);
        setFriction(3);

        collision = new Rectangle(-12, -8, 24, 16);
        hitBox = new Rectangle(-16, -12, 32, 24);
        selectBox = new Rectangle(-18, -7 - 40, 36, 60);
    }

    @Override
    public void init() {
        super.init();
        ai = new BehaviourTreeAI<>(this, new PlayerFollowerAINode(12 * 32, 64));
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextString(baseMobId != null ? baseMobId : "");
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        String id = reader.getNextString();
        if (id != null && !id.isEmpty()) {
            setBaseMobId(id);
        }
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);

        Mob baseMob = getVisualMob(level);
        if (baseMob != null) {
            syncVisualMob(baseMob);
            if (tryAddBaseDrawables(baseMob, list, tileList, topList, level, x, y, tickManager, camera, perspective)) {
                return;
            }
        }

        GameLight light = level.getLightLevel(getTileX(), getTileY());
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 51;

        Point sprite = getAnimSprite(x, y, getDir());

        drawY += getBobbing(x, y);
        drawY += getLevel().getTile(getTileX(), getTileY()).getMobSinkingAmount(this);

        DrawOptions drawOptions = getMountTexture().initDraw()
                .sprite(sprite.x, sprite.y, 64)
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
    public int getRockSpeed() {
        return 20;
    }

    @Override
    protected GameMessage getSummonLocalization() {
        GameMessage mountName = buildMountLocalization();
        return mountName != null ? mountName : super.getSummonLocalization();
    }

    @Override
    public int getRiderDrawYOffset() {
        Level level = getLevel();
        Mob baseMob = null;
        if (level != null) {
            baseMob = getVisualMob(level);
            if (!boundsSynced) {
                syncVisualMob(baseMob);
            }
        }
        if (selectBox != null && selectBox.height > 0) {
            int swimOffset = 0;
            if (baseMob != null) {
                MaskShaderOptions swimMask = baseMob.getSwimMaskShaderOptions(baseMob.inLiquidFloat(getDrawX(), getDrawY()));
                if (swimMask != null) {
                    swimOffset = swimMask.drawYOffset;
                }
            }
            int riderOffset = selectBox.y + (selectBox.height / 2);
            if (riderOffset > -40) {
                riderOffset = -40;
            }
            return riderOffset + swimOffset;
        }
        int spriteSize = getBaseSpriteSize();
        if (spriteSize > 0) {
            return -(spriteSize / 2);
        }
        return super.getRiderDrawYOffset();
    }

    private GameMessage buildMountLocalization() {
        String baseId = getBaseMobId();
        if (baseId == null || baseId.isEmpty()) {
            return null;
        }
        GameMessage baseName = MobRegistry.getLocalization(baseId);
        GameMessageBuilder builder = new GameMessageBuilder();
        if (baseName != null) {
            builder.append(baseName);
        } else {
            builder.append(baseId);
        }
        builder.append(" Mount");
        return builder;
    }

    private GameTexture getMountTexture() {
        String baseMobId = getBaseMobId();
        GameTexture texture = TEXTURES.get(baseMobId);
        if (texture == null) {
            texture = resolveBaseMobTexture(baseMobId);
            TEXTURES.put(baseMobId, texture);
        }
        return texture;
    }

    private GameTexture resolveBaseMobTexture(String baseMobId) {
        Mob baseMob = MobRegistry.getMob(baseMobId);
        GameTexture texture = tryGetTextureFromMob(baseMob);
        if (texture != null) {
            return texture;
        }
        return GameTexture.fromFile("mobs/" + baseMobId);
    }

    private GameTexture tryGetTextureFromMob(Mob baseMob) {
        if (baseMob == null) {
            return null;
        }
        Method textureMethod = findTextureMethod(baseMob.getClass());
        if (textureMethod != null) {
            try {
                textureMethod.setAccessible(true);
                Object value = textureMethod.invoke(baseMob);
                if (value instanceof GameTexture) {
                    return (GameTexture) value;
                }
            } catch (ReflectiveOperationException ignored) {
            }
        }
        Field textureField = findTextureField(baseMob.getClass());
        if (textureField != null) {
            try {
                textureField.setAccessible(true);
                Object value = Modifier.isStatic(textureField.getModifiers())
                        ? textureField.get(null)
                        : textureField.get(baseMob);
                if (value instanceof GameTexture) {
                    return (GameTexture) value;
                }
            } catch (ReflectiveOperationException ignored) {
            }
        }
        return null;
    }

    private Mob getVisualMob(Level level) {
        String baseMobId = getBaseMobId();
        if (baseMobId == null) {
            return null;
        }
        if (visualMob == null || visualMobLevel != level || !baseMobId.equals(visualMobId)) {
            visualMob = MobRegistry.getMob(baseMobId, level);
            visualMobLevel = level;
            visualMobId = baseMobId;
            boundsSynced = false;
        }
        return visualMob;
    }

    private void syncVisualMob(Mob baseMob) {
        if (baseMob == null) {
            return;
        }
        baseMob.x = this.x;
        baseMob.y = this.y;
        baseMob.dx = this.dx;
        baseMob.dy = this.dy;
        baseMob.moveX = this.moveX;
        baseMob.moveY = this.moveY;
        baseMob.setDir(getDir());
        if (DISTANCE_RAN_FIELD != null) {
            try {
                DISTANCE_RAN_FIELD.setDouble(baseMob, getDistanceRan());
            } catch (IllegalAccessException ignored) {
            }
        }
        if (!boundsSynced) {
            syncBoundsFromBase(baseMob);
            boundsSynced = true;
        }
        clearSleepState(baseMob);
    }

    private void syncBoundsFromBase(Mob baseMob) {
        Rectangle baseCollision = baseMob.getCollision(0, 0);
        if (baseCollision != null) {
            collision = new Rectangle(baseCollision);
        }
        Rectangle baseHitBox = baseMob.getHitBox(0, 0);
        if (baseHitBox != null) {
            hitBox = new Rectangle(baseHitBox);
        }
        Rectangle baseSelectBox = baseMob.getSelectBox(0, 0);
        if (baseSelectBox != null) {
            selectBox = new Rectangle(baseSelectBox);
        }
    }

    private boolean tryAddBaseDrawables(Mob baseMob, List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        Method method = getAddDrawablesMethod(baseMob.getClass());
        if (method == null) {
            return false;
        }
        try {
            method.invoke(baseMob, list, tileList, topList, level, x, y, tickManager, camera, perspective);
            return true;
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
    }

    private Method getAddDrawablesMethod(Class<?> type) {
        if (ADD_DRAWABLES_CACHE.containsKey(type)) {
            return ADD_DRAWABLES_CACHE.get(type);
        }
        Method method = findAddDrawablesMethod(type);
        ADD_DRAWABLES_CACHE.put(type, method);
        return method;
    }

    private Method findAddDrawablesMethod(Class<?> type) {
        Class<?> current = type;
        while (current != null && current != Object.class) {
            try {
                Method method = current.getDeclaredMethod("addDrawables", List.class, OrderableDrawables.class, OrderableDrawables.class, Level.class, int.class, int.class, TickManager.class, GameCamera.class, PlayerMob.class);
                method.setAccessible(true);
                if (method.getDeclaringClass() == Mob.class) {
                    return null;
                }
                return method;
            } catch (NoSuchMethodException ignored) {
            }
            current = current.getSuperclass();
        }
        return null;
    }

    private int getBaseSpriteSize() {
        String baseMobId = getBaseMobId();
        if (baseMobId == null) {
            return 0;
        }
        Integer size = SPRITE_SIZES.get(baseMobId);
        if (size != null) {
            return size;
        }
        GameTexture texture = getMountTexture();
        if (texture == null) {
            return 0;
        }
        int height = texture.getHeight();
        int spriteSize = height / 4;
        if (spriteSize <= 0) {
            spriteSize = 64;
        }
        SPRITE_SIZES.put(baseMobId, spriteSize);
        return spriteSize;
    }

    public void setBaseMobId(String baseMobId) {
        if (baseMobId == null || baseMobId.isEmpty()) {
            return;
        }
        if (baseMobId.equals(this.baseMobId)) {
            return;
        }
        this.baseMobId = baseMobId;
        this.visualMob = null;
        this.visualMobLevel = null;
        this.visualMobId = null;
        this.boundsSynced = false;
    }

    public String getBaseMobIdForMountItem() {
        return getBaseMobId();
    }

    private Method findTextureMethod(Class<?> type) {
        Class<?> current = type;
        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredMethod("getTexture");
            } catch (NoSuchMethodException ignored) {
            }
            current = current.getSuperclass();
        }
        return null;
    }

    private Field findTextureField(Class<?> type) {
        Class<?> current = type;
        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredField("texture");
            } catch (NoSuchFieldException ignored) {
            }
            current = current.getSuperclass();
        }
        return null;
    }

    private String getBaseMobId() {
        if (baseMobId != null && !baseMobId.isEmpty()) {
            return baseMobId;
        }
        String mobId = getStringID();
        if (mobId == null) {
            return BULL_MOB_ID;
        }
        if (mobId.equals(BULL_MOUNT_MOB_ID)) {
            return BULL_MOB_ID;
        }
        if (mobId.startsWith(MOUNT_MOB_PREFIX)) {
            return mobId.substring(MOUNT_MOB_PREFIX.length());
        }
        return mobId;
    }

    private void clearSleepState(Mob baseMob) {
        Field sleepField = getSleepField(baseMob.getClass());
        if (sleepField == null) {
            return;
        }
        try {
            sleepField.setBoolean(baseMob, false);
        } catch (IllegalAccessException ignored) {
        }
    }

    private Field getSleepField(Class<?> type) {
        if (SLEEP_FIELDS.containsKey(type)) {
            return SLEEP_FIELDS.get(type);
        }
        Field field = findNamedField(type, "isSleeping");
        SLEEP_FIELDS.put(type, field);
        return field;
    }

    private static Field findMobField(String name) {
        try {
            Field field = Mob.class.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException ignored) {
            return null;
        }
    }

    private Field findNamedField(Class<?> type, String name) {
        Class<?> current = type;
        while (current != null && current != Object.class) {
            try {
                Field field = current.getDeclaredField(name);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException ignored) {
            }
            current = current.getSuperclass();
        }
        return null;
    }
}
