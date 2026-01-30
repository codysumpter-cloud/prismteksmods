package summoningweaponexample;

import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;

public class SummoningWeaponExampleMod {

    public void init() {
        ItemRegistry.registerItem("impsummonstaff", new ImpSummonStaffItem(), 35, true);
        MobRegistry.registerMob("impsummonmob", ImpSummonMob.class, true);
    }

    public void initResources() {
        ImpSummonMob.texture = GameTexture.fromFile("mobs/impsummonmob");
    }

    public void postInit() {
        Recipes.registerModRecipe(new Recipe(
                "impsummonstaff",
                1,
                RecipeTechRegistry.WORKSTATION,
                new Ingredient[]{
                        new Ingredient("oaklog", 8),
                        new Ingredient("ironbar", 4)
                }
        ).showAfter("woodboat"));
    }

}
