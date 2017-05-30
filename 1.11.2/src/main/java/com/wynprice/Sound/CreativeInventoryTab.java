package com.wynprice.Sound;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CreativeInventoryTab extends CreativeTabs
{

	public CreativeInventoryTab()
	{
		super("tabWynPrice");
	}

	@Override
	public ItemStack getTabIconItem()
	{
		return new ItemStack(Items.DIAMOND_HOE);
	}

}
