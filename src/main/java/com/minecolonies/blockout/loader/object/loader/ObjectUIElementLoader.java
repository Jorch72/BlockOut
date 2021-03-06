package com.minecolonies.blockout.loader.object.loader;

import com.minecolonies.blockout.loader.ILoader;
import com.minecolonies.blockout.loader.IUIElementData;
import com.minecolonies.blockout.loader.object.ObjectUIElementDataBuilder;
import org.jetbrains.annotations.NotNull;

public class ObjectUIElementLoader implements ILoader
{
    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public IUIElementData createFromFile(@NotNull final String data)
    {
        if (!data.endsWith(".class"))
        {
            throw new IllegalArgumentException("Not a class name");
        }

        try
        {
            final String className = data.replace(".class", "");
            final Class<?> potentialFileLoader = Class.forName(className);
            final Class<? extends IClassBasedUICreator> fileLoader = (Class<? extends IClassBasedUICreator>) potentialFileLoader;
            final IClassBasedUICreator instance = fileLoader.newInstance();

            final ObjectUIElementDataBuilder builder = new ObjectUIElementDataBuilder();

            instance.build(builder);

            return builder.build();
        }
        catch (Exception ex)
        {
            throw new IllegalArgumentException("Construction using a code based class has failed!");
        }
    }
}
