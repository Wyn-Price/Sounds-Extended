package com.wynprice.Sound.commands;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.wynprice.Sound.SoundEventPlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.biome.Biome;

public class CommandBiomeDictonary extends CommandBase {

	@Override
	public String getName() {
		return "biomedictonary";
	}
	
	@Override
	public List<String> getAliases() {
		return Lists.newArrayList("bd, biomedictonary");
	}
	

	@Override
	public String getUsage(ICommandSender sender) {
		return  "commands.biomedictonary.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		Biome biome = sender.getEntityWorld().getBiome(sender.getPosition());
		ResourceLocation biomeRegistryName = biome.getRegistryName();
		ArrayList<String> traits = new ArrayList<String>();
		if(SoundEventPlay.beach.contains(biomeRegistryName)) traits.add("beach");
		if(SoundEventPlay.cricket.contains(biomeRegistryName)) traits.add("cricket");
		if(SoundEventPlay.forest.contains(biomeRegistryName)) traits.add("forest");
		if(SoundEventPlay.jungle.contains(biomeRegistryName)) traits.add("jungle");
		if(SoundEventPlay.storm.contains(biomeRegistryName)) traits.add("storm");
		if(traits.size() == 0)
		{
			sender.sendMessage(new TextComponentTranslation("commands.biomedictonary.failure." + (biomeRegistryName.getResourceDomain().equals("minecraft")? "vanilla" : "modded"), biome.getBiomeName()));
			return;
		}
		String printTraits = "";
		for(int i = 0; i < traits.size(); i++)
			if(i == traits.size() - 1) printTraits += traits.get(i);
			else if(i == traits.size() - 2) printTraits += traits.get(i) + " & ";
			else printTraits += traits.get(i) + ", ";
		sender.sendMessage(new TextComponentTranslation("commands.biomedictonary.success", biome.getBiomeName(), printTraits));
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}
	
}
