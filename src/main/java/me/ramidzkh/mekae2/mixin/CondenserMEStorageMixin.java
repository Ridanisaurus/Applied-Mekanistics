package me.ramidzkh.mekae2.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.ramidzkh.mekae2.ae2.MekanismKey;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;

@Mixin(targets = "appeng.blockentity.misc.CondenserMEStorage")
public class CondenserMEStorageMixin {

    @Inject(method = "insert", at = @At("HEAD"), cancellable = true)
    private void onInsert(AEKey what, long amount, Actionable mode, IActionSource source,
            CallbackInfoReturnable<Long> cir) {
        if (what instanceof MekanismKey key) {
            if (!ChemicalAttributeValidator.DEFAULT.process(key.getStack())) {
                cir.setReturnValue(0L);
            }
        }
    }
}
