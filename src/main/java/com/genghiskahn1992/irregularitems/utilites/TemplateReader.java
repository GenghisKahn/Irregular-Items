package com.genghiskahn1992.irregularitems.utilites;

import net.minecraft.block.Block;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class TemplateReader{

    private Template template = new Template();
    private CompoundNBT templateNBT = null;
    private Map<BlockPos, CompoundNBT> blockMapping = new HashMap<>();

    public TemplateReader() {
    }

    public boolean ProcessTemplate(Path location, String name) {
        boolean templateProcessed = false;

        File file = new File(location + "/" + name + ".nbt");

        InputStream inputStreamIn = null;
        try {
            inputStreamIn = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            templateNBT = CompressedStreamTools.readCompressed(inputStreamIn);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (templateNBT != null) {
            template.read(templateNBT);
            this.mapBlocks();
            templateProcessed = true;
        }

        return templateProcessed;
    }

    public Template getTemplate(){
        return template;
    }

    public Map<BlockPos, CompoundNBT> getBlockMapping(){ return blockMapping; }

    public Map<Block, Integer> getBlockRequirements(){
        Map<Block, Integer> blockHashMap = new HashMap<>();
        CompoundNBT[] blockTypes = null, blockPositions = null;
        int[] counts;

        if (templateNBT.contains("palette")){
            ListNBT blockPaletteList = templateNBT.getList("palette", 10);
            blockTypes = new CompoundNBT[blockPaletteList.size()];
            for(int i=0 ; i<blockPaletteList.size() ; i++){
                blockTypes[i] = blockPaletteList.getCompound(i);
            }
        }

        if(templateNBT.contains("blocks")){
            ListNBT blockPositionList = templateNBT.getList("blocks", 10);
            blockPositions = new CompoundNBT[blockPositionList.size()];
            for (int i = 0; i < blockPositionList.size(); i++) {
                blockPositions[i] = blockPositionList.getCompound(i);
            }
        }

        if(blockTypes != null && blockPositions != null) {
            counts = new int[blockTypes.length];
            for (CompoundNBT blockPosition : blockPositions) {
                counts[blockPosition.getInt("state")]++;
            }

            for (int i = 0; i < blockTypes.length; i++) {
                String[] name = blockTypes[i].getString("Name").split(":");
                Block temp = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(name[0], name[1]));

                blockHashMap.put(temp, counts[i]);
            }

        }
        return blockHashMap;
    }

    private void mapBlocks(){
        CompoundNBT[] blockTypes = null, blockPositions = null;

        if (templateNBT.contains("palette")){
            ListNBT blockPaletteList = (ListNBT) templateNBT.get("palette");
            blockTypes = new CompoundNBT[blockPaletteList.size()];
            for(int i=0 ; i<blockPaletteList.size() ; i++){
                blockTypes[i] = blockPaletteList.getCompound(i);
            }
        }

        if(templateNBT.contains("blocks")){
            ListNBT blockPositionList = (ListNBT) templateNBT.get("blocks");
            blockPositions = new CompoundNBT[blockPositionList.size()];
            for (int i = 0; i < blockPositionList.size(); i++) {
                blockPositions[i] = blockPositionList.getCompound(i);
            }
        }

        if(blockTypes != null && blockPositions != null) {
            CompoundNBT tempTag;
            for (int i = 0; i < blockPositions.length; i++) {
                tempTag = blockTypes[blockPositions[i].getInt("state")];
//                String[] name = blockTypes[blockPositions[i].getInt("state")].getString("Name").split(":");
//                Block tempBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(name[0], name[1]));
                ListNBT tempPosArray = (ListNBT) blockPositions[i].get("pos");

                if(tempPosArray.size() != 0)
                    blockMapping.put(new BlockPos(tempPosArray.getInt(0), tempPosArray.getInt(1), tempPosArray.getInt(2)), tempTag);
            }
        }
    }

    public void reset(){
        this.template = new Template();
        this.templateNBT = null;
        this.blockMapping = new HashMap<>();
    }

}
