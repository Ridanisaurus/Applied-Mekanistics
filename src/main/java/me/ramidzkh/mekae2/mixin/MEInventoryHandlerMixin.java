package me.ramidzkh.mekae2.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import me.ramidzkh.mekae2.ae2.MekanismKey;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;

import appeng.api.stacks.AEKey;
import appeng.me.storage.MEInventoryHandler;

@Mixin(MEInventoryHandler.class)
public class MEInventoryHandlerMixin {

    @ModifyExpressionValue(method = "insert", at = @At(value = "FIELD", target = "Lappeng/me/storage/MEInventoryHandler;voidOverflow:Z"))
    private boolean voidOverflow(boolean original, AEKey what) {
        if (what instanceof MekanismKey key) {
            if (!ChemicalAttributeValidator.DEFAULT.process(key.getStack())) {
                return false;
            }
        }

        return original;
    }
}
