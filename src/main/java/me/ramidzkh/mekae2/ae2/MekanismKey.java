package me.ramidzkh.mekae2.ae2;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import me.ramidzkh.mekae2.util.ChemicalBridge;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.radiation.IRadiationManager;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.core.AELog;

public class MekanismKey extends AEKey {

    public static final MapCodec<MekanismKey> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Chemical.BOXED_CODEC.fieldOf("id").forGetter(key -> key.getStack().getChemical()))
            .apply(instance, chemical -> MekanismKey.of(chemical.getStack(1))));
    public static final Codec<MekanismKey> CODEC = MAP_CODEC.codec();

    public static final byte GAS = 0;
    public static final byte INFUSION = 1;
    public static final byte PIGMENT = 2;
    public static final byte SLURRY = 3;

    private final ChemicalStack<?> stack;

    private MekanismKey(ChemicalStack<?> stack) {
        this.stack = stack;
    }

    @Nullable
    public static MekanismKey of(ChemicalStack<?> stack) {
        if (stack.isEmpty()) {
            return null;
        }

        return new MekanismKey(stack.copy());
    }

    public ChemicalStack<?> getStack() {
        return stack;
    }

    public ChemicalStack<?> withAmount(long amount) {
        return ChemicalBridge.withAmount(stack, amount);
    }

    public byte getForm() {
        return switch (stack) {
            case GasStack ignored -> GAS;
            case InfusionStack ignored -> INFUSION;
            case PigmentStack ignored -> PIGMENT;
            case SlurryStack ignored -> SLURRY;
            default -> throw new UnsupportedOperationException();
        };
    }

    @Override
    public AEKeyType getType() {
        return MekanismKeyType.TYPE;
    }

    @Override
    public AEKey dropSecondary() {
        return this;
    }

    public static MekanismKey fromTag(HolderLookup.Provider registries, CompoundTag tag) {
        var ops = registries.createSerializationContext(NbtOps.INSTANCE);

        try {
            return CODEC.decode(ops, tag).getOrThrow().getFirst();
        } catch (Exception e) {
            AELog.debug("Tried to load an invalid chemical key from NBT: %s", tag, e);
            return null;
        }
    }

    @Override
    public CompoundTag toTag(HolderLookup.Provider registries) {
        var ops = registries.createSerializationContext(NbtOps.INSTANCE);
        return (CompoundTag) CODEC.encodeStart(ops, this).getOrThrow();
    }

    @Override
    public Object getPrimaryKey() {
        return stack.getChemical();
    }

    @Override
    public ResourceLocation getId() {
        return stack.getTypeRegistryName();
    }

    @Override
    public void addDrops(long amount, List<ItemStack> drops, Level level, BlockPos pos) {
        if (stack instanceof GasStack gasStack) {
            IRadiationManager.INSTANCE.dumpRadiation(GlobalPos.of(level.dimension(), pos),
                    ChemicalBridge.withAmount(gasStack, amount));
        }
    }

    @Override
    protected Component computeDisplayName() {
        return stack.getChemical().getTextComponent();
    }

    @Override
    public boolean isTagged(TagKey<?> tag) {
        return switch (stack.getChemical()) {
            case Gas gas -> tag.registry().equals(MekanismAPI.GAS_REGISTRY_NAME)
                    && gas.is((TagKey<Gas>) tag);
            case InfuseType infuse -> tag.registry().equals(MekanismAPI.INFUSE_TYPE_REGISTRY_NAME)
                    && infuse.is((TagKey<InfuseType>) tag);
            case Pigment pigment -> tag.registry().equals(MekanismAPI.PIGMENT_REGISTRY_NAME)
                    && pigment.is((TagKey<Pigment>) tag);
            case Slurry slurry -> tag.registry().equals(MekanismAPI.SLURRY_REGISTRY_NAME)
                    && slurry.is((TagKey<Slurry>) tag);
            default -> throw new UnsupportedOperationException();
        };
    }

    @Override
    public <T> @Nullable T get(DataComponentType<T> type) {
        return null;
    }

    @Override
    public boolean hasComponents() {
        return false;
    }

    @Override
    public void writeToPacket(RegistryFriendlyByteBuf data) {
        ChemicalStack.BOXED_STREAM_CODEC.encode(data, stack);
    }

    public static MekanismKey fromPacket(RegistryFriendlyByteBuf data) {
        var stack = ChemicalStack.BOXED_STREAM_CODEC.decode(data);
        return new MekanismKey(stack);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        var that = (MekanismKey) o;
        return Objects.equals(stack.getChemical(), that.stack.getChemical());
    }

    @Override
    public int hashCode() {
        return Objects.hash(stack.getChemical());
    }

    @Override
    public String toString() {
        return "MekanismKey{" +
                "stack=" + stack.getChemical() +
                '}';
    }
}
