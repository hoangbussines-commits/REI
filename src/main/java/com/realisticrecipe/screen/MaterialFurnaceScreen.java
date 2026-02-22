package com.realisticrecipe.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.realisticrecipe.block.entity.MaterialFurnaceBlockEntity;
import com.realisticrecipe.energymanager.materialenergy.energy.EnergyInfoArea;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.Optional;

public class MaterialFurnaceScreen extends AbstractContainerScreen<MaterialFurnaceMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("realisticrecipe", "textures/gui/material_furnace.png");

    private EnergyInfoArea energyInfoArea;

    public MaterialFurnaceScreen(MaterialFurnaceMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void init() {
        super.init();
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        IEnergyStorage energyView = new IEnergyStorage() {
            @Override
            public int getEnergyStored() {
                return menu.getEnergy();
            }

            @Override
            public int getMaxEnergyStored() {
                return menu.getMaxEnergy();
            }

            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return 0;
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                return 0;
            }

            @Override
            public boolean canExtract() {
                return false;
            }

            @Override
            public boolean canReceive() {
                return false;
            }
        };

        energyInfoArea = new EnergyInfoArea(
                x + 156,
                y + 13,
                energyView,
                8,
                64
        );
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // Vẽ mũi tên progress
        int progress = menu.getScaledProgress();
        guiGraphics.blit(TEXTURE, x + 79, y + 35, 176, 0, progress, 17);

        // Vẽ thanh năng lượng
        if (energyInfoArea != null) {
            energyInfoArea.draw(guiGraphics);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }

    private boolean lastOver = false;

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        if (energyInfoArea != null) {
            boolean over = energyInfoArea.isMouseOver(mouseX - x, mouseY - y);
            if (over != lastOver) {
                System.out.println("[REI] over=" + over + " at " + mouseX + "," + mouseY);
                lastOver = over;
            }
            if (over) {
                guiGraphics.renderTooltip(font, energyInfoArea.getTooltips(), Optional.empty(), mouseX, mouseY);
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}