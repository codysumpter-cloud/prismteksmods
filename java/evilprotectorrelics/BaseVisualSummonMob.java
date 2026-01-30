package evilprotectorrelics;

import java.awt.Color;
import java.awt.Point;
import java.util.List;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.Mob;
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

    protected final String visualMobID;
    protected final int spriteSize;     // 64 (minions) or 128 (boss)
    protected final float visualScale;  // applied after native draw size

    protected BaseVisualSummonMob(int health, String visualMobID, int spriteSize, float visualScale) {
        super(health);
        this.visualMobID = visualMobID;
        this.spriteSize = spriteSize;
        this.visualScale = visualScale;
    }

    protected abstract GameTexture getVisualTexture();

    protected int getBaseDrawYOffset() { return 13; }

    protected Point getDrawSprite(int x, int y, int dir) {
        return getAnimSprite(x, y, dir);
    }

    protected boolean shouldMirrorX(int dir) { return false; }

    /**
     * Glow/shadow polish:
     * return 0 to disable glow for this mob.
     */
    protected float getGlowAlpha() { return 0.0f; }

    /**
     * Glow color (only used if glow alpha > 0).
     */
    protected Color getGlowColor() { return new Color(180, 120, 255); }

    @Override
    public void clientTick() {
        super.clientTick();

        Mob owner = getFollowingMob();
        if (owner == null) {
            owner = getFirstAttackOwner();
        }

        if (owner != null) {
            float dx = owner.dx;
            float dy = owner.dy;

            if (Math.abs(dx) > 0.01f || Math.abs(dy) > 0.01f) {
                if (Math.abs(dx) > Math.abs(dy)) {
                    setDir(dx > 0 ? 1 : 3);
                } else {
                    setDir(dy > 0 ? 2 : 0);
                }
            } else {
                setDir(owner.getDir());
            }
        }
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileDrawables, OrderableDrawables topDrawables,
                                Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileDrawables, topDrawables, level, x, y, tickManager, camera, perspective);

        GameTexture texture = getVisualTexture();
        if (texture == null) return;

        GameLight light = level.getLightLevel(getTileX(), getTileY());

        int drawSize = Math.max(1, Math.round(spriteSize * visualScale));
        int drawYOffset = Math.round(getBaseDrawYOffset() * visualScale);

        // Necesse standard-ish mob anchoring: center X, bias Y upward.
        int baseDrawX = camera.getDrawX(x) - drawSize / 2;
        int baseDrawY = camera.getDrawY(y) - drawSize + drawYOffset;

        Point sprite = getDrawSprite(x, y, getDir());
        baseDrawY += getBobbing(x, y);

        GameTile tile = level.getTile(getTileX(), getTileY());
        baseDrawY += tile.getMobSinkingAmount(this);

        // Base draw (normal)
        TextureDrawOptionsEnd base = texture.initDraw()
                .sprite(sprite.x, sprite.y, spriteSize)
                .light(light)
                .pos(baseDrawX, baseDrawY)
                .size(drawSize);

        if (shouldMirrorX(getDir())) {
            base = base.mirrorX();
        }

        DrawOptions baseDraw = base;

        list.add(new MobDrawable() {
            @Override
            public void draw(TickManager tickManager) {
                baseDraw.draw();
            }
        });

        // Visual-only glow (second pass). Kept cosmetic: does not affect gameplay.
        float glowA = getGlowAlpha();
        if (glowA > 0.0f) {
            Color c = getGlowColor();

            TextureDrawOptionsEnd glow = texture.initDraw()
                    .sprite(sprite.x, sprite.y, spriteSize)
                    .light(light)
                    .pos(baseDrawX, baseDrawY)
                    .size(drawSize);

            if (shouldMirrorX(getDir())) {
                glow = glow.mirrorX();
            }

            // These methods exist in typical Necesse builds; if your mapping differs,
            // you can remove color/alpha and keep the second pass only.
            glow = glow.color(c);
            glow = glow.alpha(glowA);

            DrawOptions glowDraw = glow;

            topDrawables.add((tm) -> glowDraw.draw());
        }

        addShadowDrawables(tileDrawables, level, x, y, light, camera);
    }
}
