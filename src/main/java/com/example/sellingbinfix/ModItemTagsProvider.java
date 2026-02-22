package com.example.sellingbinfix;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends ItemTagsProvider {
    public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                               CompletableFuture<TagLookup<Block>> blockTags, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, "sellingbinfix", existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        TagKey<Item> enchantablesTag = TagKey.create(
                Registries.ITEM,
                ResourceLocation.fromNamespaceAndPath("c", "enchantables")
        );

        tag(ModTags.Items.UPGRADEABLE_BASE)
                .addTag(enchantablesTag)
                .addOptional(ResourceLocation.fromNamespaceAndPath("sellingbin", "selling_bin"));
    }
}