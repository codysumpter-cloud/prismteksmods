package prismteksexamplemod.druidmod;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.followingProjectile.FollowingProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.Color;
import java.util.List;

public class DruidNatureBoltProjectile extends FollowingProjectile {

    public DruidNatureBoltProjectile() {
    }

    public DruidNatureBoltProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
        setLevel(level);
        setOwner(owner);
        this.x = x;
        this.y = y;
        setTarget(targetX, targetY);
        this.speed = speed;
        this.distance = distance;
        setDamage(damage);
        this.knockback = knockback;
    }

    public void init() {
        super.init();
        turnSpeed = 0.85f;
        givesLight = false;
        height = 18;
        trailOffset = -12f;
        setWidth(16, true);
        piercing = 1;
    }

    @Override
    public Color getParticleColor() {
        return new Color(80, 170, 60);
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, getLevel(), new Color(120, 210, 90), 26, 500, getHeight());
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (removed()) return;
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(x) - texture.getWidth() / 2;
        int drawY = camera.getDrawY(y);
        TextureDrawOptions options = texture.initDraw()
                .light(light)
                .rotate(getAngle(), texture.getWidth() / 2, 2)
                .pos(drawX, drawY - (int) getHeight());

        list.add(new EntityDrawable(this) {
            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });

        addShadowDrawables(tileList, drawX, drawY, light, getAngle(), texture.getWidth() / 2, 2);
    }
}
