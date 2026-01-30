package grizzlycubsummon;

import java.awt.Point;
import java.util.List;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public abstract class BaseVisualSummonMob extends AttackingFollowingMob {

    private final String defaultVisualMobID;
    private final int defaultSpriteSize;
    private final float visualScale;

    protected BaseVisualSummonMob(int health, String visualMobID, int spriteSize, float visualScale) {
        super(health);
        this.defaultVisualMobID = visualMobID;
        this.defaultSpriteSize = spriteSize;
        this.visualScale = visualScale;
    }

    protected String getVisualMobID() { return defaultVisualMobID; }
    protected int getVisualSpriteSize() { return defaultSpriteSize; }
    protected int getBaseDrawYOffset() { return 13; }

    protected GameTexture getVisualTexture() {
        return VanillaVisuals.resolveMobTexture(getVisualMobID());
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileDrawables, OrderableDrawables topDrawables,
                                Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileDrawables, topDrawables, level, x, y, tickManager, camera, perspective);

        GameTexture texture = getVisualTexture();
        if (texture == null) return;

        int spriteSize = getVisualSpriteSize();
        GameLight light = level.getLightLevel(getTileX(), getTileY());

        int drawSize = Math.max(1, Math.round(spriteSize * visualScale));
        int drawYOffset = Math.round(getBaseDrawYOffset() * visualScale);
        int drawX = camera.getDrawX(x) - drawSize / 2;
        int drawY = camera.getDrawY(y) - drawSize + drawYOffset;

        Point sprite = getAnimSprite(x, y, getDir());
        drawY += getBobbing(x, y);

        GameTile tile = level.getTile(getTileX(), getTileY());
        drawY += tile.getMobSinkingAmount(this);

        TextureDrawOptionsEnd draw = texture.initDraw()
                .sprite(sprite.x, sprite.y, spriteSize)
                .light(light)
                .pos(drawX, drawY)
                .size(drawSize);

        DrawOptions finalDraw = draw;
        list.add(new MobDrawable() {
            @Override
            public void draw(TickManager tickManager) {
                finalDraw.draw();
            }
        });

        addShadowDrawables(tileDrawables, level, x, y, light, camera);
    }
}
