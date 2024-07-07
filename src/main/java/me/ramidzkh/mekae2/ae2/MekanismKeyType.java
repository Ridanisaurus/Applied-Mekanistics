package me.ramidzkh.mekae2.ae2;

import java.util.Objects;
import java.util.stream.Stream;

import com.google.common.collect.Streams;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.tags.TagKey;

import me.ramidzkh.mekae2.AMText;
import me.ramidzkh.mekae2.AppliedMekanistics;
import mekanism.api.MekanismAPI;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;

public class MekanismKeyType extends AEKeyType {

    public static final AEKeyType TYPE = new MekanismKeyType();

    private MekanismKeyType() {
        super(AppliedMekanistics.id("chemical"), MekanismKey.class, AMText.CHEMICALS.formatted());
    }

    @Override
    public MapCodec<? extends AEKey> codec() {
        return MekanismKey.MAP_CODEC;
    }

    // Copied from AEFluidKey
    @Override
    public int getAmountPerOperation() {
        // On Forge this was 125mb (so 125/1000th of a bucket)
        return AEFluidKey.AMOUNT_BUCKET * 125 / 1000;
    }

    // Copied from AEFluidKey
    @Override
    public int getAmountPerByte() {
        return 8 * AEFluidKey.AMOUNT_BUCKET;
    }

    @Override
    public MekanismKey readFromPacket(RegistryFriendlyByteBuf input) {
        Objects.requireNonNull(input);
        return MekanismKey.fromPacket(input);
    }

    @Override
    public MekanismKey loadKeyFromTag(HolderLookup.Provider registries, CompoundTag tag) {
        return MekanismKey.fromTag(registries, tag);
    }

    // Copied from AEFluidKey
    @Override
    public int getAmountPerUnit() {
        return AEFluidKey.AMOUNT_BUCKET;
    }

    @Override
    public Stream<TagKey<?>> getTagNames() {
        return Streams.concat(
                MekanismAPI.GAS_REGISTRY.getTagNames(),
                MekanismAPI.INFUSE_TYPE_REGISTRY.getTagNames(),
                MekanismAPI.PIGMENT_REGISTRY.getTagNames(),
                MekanismAPI.SLURRY_REGISTRY.getTagNames());
    }

    // Copied from AEFluidKey
    @Override
    public String getUnitSymbol() {
        return "B";
    }
}
