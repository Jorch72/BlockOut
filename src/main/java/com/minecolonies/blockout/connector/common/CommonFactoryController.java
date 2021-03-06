package com.minecolonies.blockout.connector.common;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.minecolonies.blockout.connector.core.IUIElementFactoryController;
import com.minecolonies.blockout.core.element.IUIElement;
import com.minecolonies.blockout.core.factory.IUIElementFactory;
import com.minecolonies.blockout.loader.IUIElementData;
import com.minecolonies.blockout.loader.IUIElementDataBuilder;
import com.minecolonies.blockout.loader.object.ObjectUIElementDataBuilder;
import com.minecolonies.blockout.util.Constants;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class CommonFactoryController implements IUIElementFactoryController
{
    private final BiMap<ResourceLocation, IUIElementFactory<?>> factoryBiMap = HashBiMap.create();

    @Override
    public IUIElementFactoryController registerFactory(@NotNull final IUIElementFactory<?> factory)
    {
        factoryBiMap.forcePut(factory.getType(), factory);
        return this;
    }

    @NotNull
    @Override
    public IUIElement getElementFromData(@NotNull final IUIElementData data)
    {
        final ResourceLocation type = data.getType();

        if (!factoryBiMap.containsKey(type))
        {
            throw new IllegalArgumentException("Unknown type is contained in the given data.");
        }

        return factoryBiMap.get(type).readFromElementData(data);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public <T extends IUIElement> IUIElementData getDataFromElement(@NotNull final T element)
    {
        final ResourceLocation type = element.getType();

        if (!factoryBiMap.containsKey(type))
        {
            throw new IllegalArgumentException("Unknown type is contained in the given data.");
        }

        final IUIElementFactory<T> factory = (IUIElementFactory<T>) factoryBiMap.get(type);

        final IUIElementDataBuilder builder = new ObjectUIElementDataBuilder();
        builder.setType(type);
        builder.addString(Constants.Controls.General.CONST_ID, element.getId());
        builder.addResourceLocation(Constants.Controls.General.CONST_STYLE_ID, element.getStyleId());

        factory.writeToElementData(element, builder);

        return builder.build();
    }
}
