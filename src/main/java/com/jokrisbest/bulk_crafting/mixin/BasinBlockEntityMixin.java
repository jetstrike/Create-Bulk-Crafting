package com.jokrisbest.bulk_crafting.mixin;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(BasinBlockEntity.class)
public abstract class BasinBlockEntityMixin {

    @Shadow protected List<ItemStack> spoutputBuffer;

    /**
     * Before attempting to clear overflow items through basin funnels/spouts into adjacent blocks
     * (such as Chutes, Depots, or Belts), consolidate identical item stacks in spoutputBuffer up to
     * their maximum stack size. Since bulk crafting produces individual recipe result stacks for each
     * operation in a cycle, merging them ensures the Basin deposits consolidated stacks (e.g. 64 items)
     * at once rather than transferring single items sequentially.
     */
    @Inject(method = "tryClearingSpoutputOverflow", at = @At("HEAD"))
    private void bulkCrafting$consolidateSpoutputBuffer(CallbackInfo ci) {
        if (spoutputBuffer == null || spoutputBuffer.size() <= 1) {
            return;
        }

        List<ItemStack> consolidated = new ArrayList<>();
        for (ItemStack incoming : spoutputBuffer) {
            if (incoming.isEmpty()) continue;

            boolean merged = false;
            for (ItemStack existing : consolidated) {
                if (ItemStack.isSameItemSameComponents(existing, incoming)) {
                    int space = existing.getMaxStackSize() - existing.getCount();
                    if (space > 0) {
                        int toAdd = Math.min(space, incoming.getCount());
                        existing.grow(toAdd);
                        incoming.shrink(toAdd);
                        if (incoming.isEmpty()) {
                            merged = true;
                            break;
                        }
                    }
                }
            }
            if (!merged && !incoming.isEmpty()) {
                consolidated.add(incoming);
            }
        }
        spoutputBuffer.clear();
        spoutputBuffer.addAll(consolidated);
    }
}
