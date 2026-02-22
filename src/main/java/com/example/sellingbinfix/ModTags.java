package com.example.sellingbinfix;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags {
    public static class Items {
        // This creates a tag called "sellingbinfix:upgradeable"
        public static final TagKey<Item> UPGRADEABLE_BASE = tag("upgradeable");

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("sellingbinfix", name));
        }
    }
}