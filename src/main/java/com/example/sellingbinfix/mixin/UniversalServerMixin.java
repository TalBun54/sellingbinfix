package com.example.sellingbinfix.mixin;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.lang.reflect.Field;

@Mixin(targets = "bigchadguys.sellingbin.block.entity.SellingBinBlockEntity", remap = false)
public abstract class UniversalServerMixin {

    private static Field cachedInventoryField = null;

    @Inject(method = "readNbt", at = @At("HEAD"), cancellable = true)
    private void guardAndSync(CompoundTag nbt, HolderLookup.Provider registries, @Coerce Object type, CallbackInfo ci) {
        boolean isClient = Thread.currentThread().getName().contains("Render thread") ||
                Thread.currentThread().getName().contains("Worker-Main");

        if (isClient) {
            if (nbt != null && nbt.contains("timeLeft")) {
                // MANUALLY sync the timer so it moves on the UI
                try {
                    Field timeField = this.getClass().getDeclaredField("timeLeft");
                    timeField.setAccessible(true);
                    timeField.setLong(this, nbt.getLong("timeLeft"));
                } catch (Exception ignored) {}
            }


            ci.cancel();
        } else {
            if (nbt == null || !nbt.contains("inventory")) {
                if (getInventoryField() != null) ci.cancel();
            }
        }
    }

    @Inject(method = "writeNbt", at = @At("TAIL"))
    private void safeWriteForNetwork(CompoundTag nbt, HolderLookup.Provider registries, @Coerce Object type, CallbackInfo ci) {
        String typeName = String.valueOf(type).toUpperCase();

        // Only strip inventory for client updates, never for disk saves
        if (typeName.contains("UPDATE") || typeName.contains("INITIAL")) {
            if (!typeName.contains("SAVE") && !typeName.contains("DISK")) {
                nbt.remove("inventory");
            }
        }
    }

    private Object getInventoryField() {
        try {
            if (cachedInventoryField == null) {
                cachedInventoryField = this.getClass().getDeclaredField("inventory");
                cachedInventoryField.setAccessible(true);
            }
            return cachedInventoryField.get(this);
        } catch (Exception e) {
            return null;
        }
    }
}