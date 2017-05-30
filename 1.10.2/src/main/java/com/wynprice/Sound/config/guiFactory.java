package com.wynprice.Sound.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.ibm.icu.util.ULocale.Category;
import com.wynprice.Sound.References;

import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiConfigEntries.CategoryEntry;
import net.minecraftforge.fml.client.config.IConfigElement;

public class guiFactory implements IModGuiFactory
{

	@Override
	public void initialize(Minecraft minecraftInstance) {
		
	}
	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass() {
		return SoundConfigGui.class;
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return null;
	}

	@Override
	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
		return null;
	}
	
	public static class SoundConfigGui extends GuiConfig
	{

		public SoundConfigGui(GuiScreen parentScreen) 
		{
			super(parentScreen, getConfigElements(), References.MODID, false, false, I18n.format("gui.title"));
			// TODO Auto-generated constructor stub
		}

		private static List<IConfigElement> getConfigElements() 
		{
			List<IConfigElement> list = new ArrayList<IConfigElement>();
			list.add(new DummyCategoryElement("gui.category.enabled", "gui.category.enabled", CategoryEnabled.class));
			list.add(new DummyCategoryElement("gui.category.server", "gui.category.server", CategoryServer.class));
			return list;
		}
		
		
		
		public static class CategoryEnabled extends CategoryEntry
		{

			public CategoryEnabled(GuiConfig owningScreen, GuiConfigEntries owningEntryList,
					IConfigElement configElement) {
				super(owningScreen, owningEntryList, configElement);
			}
			
			@Override
			protected GuiScreen buildChildScreen() 
			{
				Configuration config = SoundConfig.getConfig();
				ConfigElement catEnabled = new ConfigElement(config.getCategory(SoundConfig.CATEGORY_SOUNDS_ENABLED));
				List<IConfigElement> propOnScreen = catEnabled.getChildElements();
				String windowTitle = I18n.format("gui.category.enabled");
				return new GuiConfig(owningScreen, propOnScreen, owningScreen.modID, this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart,  this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart, windowTitle);
			}	
		}
		
		public static class CategoryServer extends CategoryEntry
		{
			public CategoryServer(GuiConfig owningScreen, GuiConfigEntries owningEntryList,
					IConfigElement configElement) {
				super(owningScreen, owningEntryList, configElement);
			}
			
			@Override
			protected GuiScreen buildChildScreen() {
				Configuration config = SoundConfig.getConfig();
				ConfigElement catEnabled = new ConfigElement(config.getCategory(SoundConfig.CATEGORY_SERVER_SETTINGS));
				List<IConfigElement> propOnScreen = catEnabled.getChildElements();
				String windowTitle = I18n.format("gui.category.server");
				return new GuiConfig(owningScreen, propOnScreen, owningScreen.modID, this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart,  this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart, windowTitle);
			}
		}

			
		
	}

}
