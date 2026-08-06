package com.jokrisbest.bulk_crafting.mixin;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BasinOperatingBlockEntity.class)
public abstract class BasinOperatingBlockEntityMixin {

    /**
     * Intercepts the call to BasinRecipe.apply inside applyBasinRecipe().
     * After executing the initial recipe application, if successful, it loops
     * to process additional batch operations up to available ingredient quantities
     * and output capacity within a single animation cycle.
     */
    @Redirect(
        method = "applyBasinRecipe",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/processing/basin/BasinRecipe;apply(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;)Z"
        )
    )
    private boolean bulkCrafting$applyBasinRecipeInBulk(BasinBlockEntity basin, Recipe<?> recipe) {
        // Perform the first recipe application as standard Create does
        if (!BasinRecipe.apply(basin, recipe)) {
            return false;
        }

        // Continually apply the recipe in bulk for remaining ingredients in the basin
        // Cap at 100,000 operations per cycle as a defensive safety net against
        // zero-cost generator recipes from external mods
        int operations = 1;
        while (operations < 100_000 && BasinRecipe.apply(basin, recipe)) {
            operations++;
        }

        return true;
    }
}
