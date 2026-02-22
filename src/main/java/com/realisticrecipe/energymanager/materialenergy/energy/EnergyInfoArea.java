package com.realisticrecipe.energymanager.materialenergy.energy;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.List;

public class EnergyInfoArea {
    private final Rect2i area;
    private final IEnergyStorage energy;

    public EnergyInfoArea(int x, int y, IEnergyStorage energy, int width, int height) {
        this.area = new Rect2i(x, y, width, height);
        this.energy = energy;
        System.out.println("Energy: " + energy.getEnergyStored() + "/" + energy.getMaxEnergyStored());

    }

    public List<Component> getTooltips() {
        if (energy == null) return List.of(Component.literal("N/A"));

        return List.of(Component.literal(
                String.format("⚡ %d / %d FE",
                        energy.getEnergyStored(),
                        energy.getMaxEnergyStored()
                )
        ));
    }

    public void draw(GuiGraphics guiGraphics) {
        if (energy == null) return;

        int current = energy.getEnergyStored();
        int max = energy.getMaxEnergyStored();
        if (max <= 0) return;

        int height = area.getHeight();
        int fillHeight = (int) ((float) current / max * height);
        int fillY = area.getY() + (height - fillHeight);

        // Nền đen
        guiGraphics.fill(area.getX(), area.getY(), area.getX() + area.getWidth(), area.getY() + height, 0xFF000000);

        guiGraphics.fillGradient(
                area.getX(), fillY,
                area.getX() + area.getWidth(), area.getY() + height,
                0xFFb51500,
                0xFF600b00
        );
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= area.getX() && mouseX <= area.getX() + area.getWidth()
                && mouseY >= area.getY() && mouseY <= area.getY() + area.getHeight();
    }


}