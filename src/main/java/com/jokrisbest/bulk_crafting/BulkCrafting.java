package com.jokrisbest.bulk_crafting;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(BulkCrafting.MODID)
public class BulkCrafting {

    public static final String MODID = "bulk_crafting";
    private static final Logger LOGGER = LogUtils.getLogger();

    public BulkCrafting(IEventBus modEventBus) {
        modEventBus.addListener(this::commonSetup);
        LOGGER.info("Create: Bulk Crafting addon initializing!");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Create: Bulk Crafting common setup completed. Basin bulk mixing and compacting enabled!");
    }
}
