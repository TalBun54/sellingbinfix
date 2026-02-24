package com.example.sellingbinfix;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import java.lang.reflect.Method;

@Mod("sellingbinfix")
public class SellingBinFixMod {

    public SellingBinFixMod(IEventBus modEventBus) {
        // This tells the game to run the "gatherData" method when it's time to generate files
        modEventBus.addListener(this::gatherData);
        modEventBus.addListener(this::registerCapabilities);
    }

    public void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        ModBlockTagsProvider blockTags = new ModBlockTagsProvider(output, lookupProvider, existingFileHelper);
        generator.addProvider(event.includeServer(), blockTags);

        generator.addProvider(event.includeServer(),
                new ModItemTagsProvider(output, lookupProvider, blockTags.contentsGetter(), existingFileHelper));
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        try {
            Class<?> entitiesClass = Class.forName("bigchadguys.sellingbin.init.ModBlocks$Entities");
            Object registrySupplier = entitiesClass.getField("SELLING_BIN").get(null);

            Method getMethod = registrySupplier.getClass().getMethod("get");

            getMethod.setAccessible(true);

            BlockEntityType<?> binEntityType = (BlockEntityType<?>) getMethod.invoke(registrySupplier);

            event.registerBlockEntity(
                    Capabilities.ItemHandler.BLOCK,
                    binEntityType,
                    (be, side) -> {
                        try {
                            Method getMode = be.getClass().getMethod("getMode");
                            getMode.setAccessible(true);

                            if ("BLOCK_BOUND".equals(String.valueOf(getMode.invoke(be)))) {
                                Method getInv = be.getClass().getMethod("getInventory");
                                getInv.setAccessible(true);
                                Object inv = getInv.invoke(be);

                                if (inv instanceof net.minecraft.world.Container container) {
                                    return new net.neoforged.neoforge.items.wrapper.InvWrapper(container);
                                }
                            }
                        } catch (Exception ignored) {}
                        return null;
                    }
            );
            System.out.println("SellingBinFix: Redstone Bin Capability registered successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}