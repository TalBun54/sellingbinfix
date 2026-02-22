package com.example.sellingbinfix.mixin;

import com.example.sellingbinfix.ModTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Ingredient.class, priority = 2000)
public abstract class IngredientMixin {

    @Inject(method = "test(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void sellingbinfix$smartMatch(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack == null || stack.isEmpty()) return;

        if (stack.is(ModTags.Items.UPGRADEABLE_BASE)) {
            Ingredient self = (Ingredient) (Object) this;
            ItemStack[] recipeStacks = self.getItems();

            for (ItemStack recipeStack : recipeStacks) {
                if (stack.getItem() == recipeStack.getItem()) {

                    if (recipeStack.getComponentsPatch().isEmpty()) {
                        cir.setReturnValue(true);
                        return;
                    }

                }
            }
        }
    }
}