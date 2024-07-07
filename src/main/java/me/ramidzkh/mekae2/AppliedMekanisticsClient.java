package me.ramidzkh.mekae2;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.util.FastColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import me.ramidzkh.mekae2.ae2.AMChemicalStackRenderer;

import appeng.items.storage.BasicStorageCell;
import appeng.items.tools.powered.PortableCellItem;

public class AppliedMekanisticsClient {

    public static void initialize(IEventBus bus) {
        bus.addListener(AppliedMekanisticsClient::registerItemColors);
        AMChemicalStackRenderer.initialize(bus);
    }

    private static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        ItemColor cells = (stack, tintIndex) -> {
            return FastColor.ARGB32.opaque(BasicStorageCell.getColor(stack, tintIndex));
        };
        ItemColor portableCells = (stack, tintIndex) -> {
            return FastColor.ARGB32.opaque(PortableCellItem.getColor(stack, tintIndex));
        };

        for (var tier : AMItems.Tier.values()) {
            event.register(cells, AMItems.get(tier)::get);
            event.register(portableCells, AMItems.getPortableCell(tier)::get);
        }
    }
}
