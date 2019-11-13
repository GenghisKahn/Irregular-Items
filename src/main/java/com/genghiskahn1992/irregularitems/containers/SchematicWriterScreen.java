package com.genghiskahn1992.irregularitems.containers;

import com.genghiskahn1992.irregularitems.IrregularItems;
import com.genghiskahn1992.irregularitems.blocks.ModBlocks;
import com.genghiskahn1992.irregularitems.tileentities.SchematicWriterTile;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.nio.file.Path;

public class SchematicWriterScreen extends ContainerScreen<SchematicWriterContainer> {

    private ResourceLocation GUI = new ResourceLocation(IrregularItems.MODID, "textures/gui/schematicwriterblock_gui.png");

    private String[] structures;
    private int index = 0;
    private String schematicID;

    public SchematicWriterScreen(SchematicWriterContainer container, PlayerInventory inv, ITextComponent name){
        super(container, inv, name);

        Path structureDir = FMLPaths.CONFIGDIR.get().resolve("structures");

        File[] files = structureDir.toFile().listFiles();
        structures = new String[files.length];
        for(int i=0; i<files.length; i++){
            if(files[i].getName().length() > 4)
                structures[i] = files[i].getName().substring(0, files[i].getName().length() - 4);
        }

        schematicID = structures[index];
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        int relX = (this.width - this.xSize) / 2;
        int relY = (this.height - this.ySize) / 2;

        //Scroll Left button
        addButton(new Button(relX + 16, relY + 41, 9, 20, "<", p_onPress_1_ -> {
            this.scrollLeft();
        }));

        //Scroll Right button
        addButton(new Button(relX + 155, relY + 41, 9, 20, ">", p_onPress_1_ -> {
            this.scrollRight();
        }));

        //Schematic Name
        drawCenteredString(Minecraft.getInstance().fontRenderer, schematicID, 90, 47, 0xffffff);

        //Draw Schematic button
        addButton(new Button(relX + 70, relY + 15, 80, 20, "Draw Schematic", p_onPress_1_ -> {
            this.drawSchematic();
        }));

        //Inventory Names
        this.font.drawString(ModBlocks.SCHEMATICWRITER_BLOCK.getNameTextComponent().getFormattedText(), 8.0F, 4.0F, 4210752);
//        this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
    }

    private void scrollLeft(){
        index = (index + (structures.length-1))%structures.length;
        schematicID = structures[index];
    }

    private void scrollRight(){
        index = (index + 1)%structures.length;
        schematicID = structures[index];
    }

    private void drawSchematic(){
        if(this.container.getTile() instanceof SchematicWriterTile){
            ((SchematicWriterTile) this.container.getTile()).setStructureName(schematicID);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(GUI);
        int relX = (this.width - this.xSize) / 2;
        int relY = (this.height - this.ySize) / 2;
        this.blit(relX, relY, 0, 0, this.xSize, this.ySize);
    }


}
