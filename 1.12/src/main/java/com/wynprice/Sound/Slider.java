package com.wynprice.Sound;

import net.minecraftforge.fml.client.config.ConfigGuiType;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.config.GuiConfigEntries.ButtonEntry;

public class Slider extends ButtonEntry
{
    protected final double beforeValue;

    public Slider(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement)
    {
        super(owningScreen, owningEntryList, configElement, new GuiSlider(0, owningEntryList.controlX, 0, owningEntryList.controlWidth, 18,
                "", "", Double.valueOf(configElement.getMinValue().toString()), Double.valueOf(configElement.getMaxValue().toString()),
                Double.valueOf(configElement.get().toString()), configElement.getType() == ConfigGuiType.DOUBLE, true));

        this.beforeValue = Double.valueOf(configElement.get().toString());
    }

    @Override
    public void updateValueButtonText()
    {
        ((GuiSlider) this.btnValue).updateSlider();
    }

    @Override
    public void valueButtonPressed(int slotIndex) {}

    @Override
    public boolean isDefault()
    {
        if (configElement.getType() == ConfigGuiType.INTEGER)
            return ((GuiSlider) this.btnValue).getValueInt() == Integer.valueOf(configElement.getDefault().toString());
        else
            return ((GuiSlider) this.btnValue).getValue() == Double.valueOf(configElement.getDefault().toString());
    }

    @Override
    public void setToDefault()
    {
        if (this.enabled())
        {
            ((GuiSlider) this.btnValue).setValue(Double.valueOf(configElement.getDefault().toString()));
            ((GuiSlider) this.btnValue).updateSlider();
        }
    }

    @Override
    public boolean isChanged()
    {
        if (configElement.getType() == ConfigGuiType.INTEGER)
            return ((GuiSlider) this.btnValue).getValueInt() != (int) Math.round(beforeValue);
        else
            return ((GuiSlider) this.btnValue).getValue() != beforeValue;
    }

    @Override
    public void undoChanges()
    {
        if (this.enabled())
        {
            ((GuiSlider) this.btnValue).setValue(beforeValue);
            ((GuiSlider) this.btnValue).updateSlider();
        }
    }

    @Override
    public boolean saveConfigElement()
    {
        if (this.enabled() && this.isChanged())
        {
            if (configElement.getType() == ConfigGuiType.INTEGER)
                configElement.set(((GuiSlider) this.btnValue).getValueInt());
            else
                configElement.set(((GuiSlider) this.btnValue).getValue());
            return configElement.requiresMcRestart();
        }
        return false;
    }

    @Override
    public Object getCurrentValue()
    {
        if (configElement.getType() == ConfigGuiType.INTEGER)
            return ((GuiSlider) this.btnValue).getValueInt();
        else
            return ((GuiSlider) this.btnValue).getValue();
    }

    @Override
    public Object[] getCurrentValues()
    {
        return new Object[] { getCurrentValue() };
    }
}