package com.minecolonies.blockout.connector.common.definition.loader;

import com.google.common.io.CharStreams;
import com.minecolonies.blockout.BlockOut;
import com.minecolonies.blockout.connector.core.IGuiDefinitionLoader;
import com.minecolonies.blockout.util.Log;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Objects;

public class CommonResourceLocationBasedGuiDefinitionLoader implements IGuiDefinitionLoader
{

    @NotNull
    private final String domain;
    @NotNull
    private final String path;

    private CommonResourceLocationBasedGuiDefinitionLoader()
    {
        this.domain = "";
        this.path = "";
    }

    public CommonResourceLocationBasedGuiDefinitionLoader(final ResourceLocation location)
    {
        this.domain = location.getResourceDomain();
        this.path = location.getResourcePath();
    }

    @NotNull
    @Override
    public String getGuiDefinition()
    {
        try
        {
            final InputStream stream = BlockOut.getBlockOut().getProxy().getResourceStream(getLocation());
            String data;
            try (final Reader reader = new InputStreamReader(stream))
            {
                data = CharStreams.toString(reader);
            }

            return data;
        }
        catch (Exception e)
        {
            Log.getLogger().warn("Failed to read data from location: " + getLocation(), e);
            return "";
        }
    }

    @NotNull
    public ResourceLocation getLocation()
    {
        return new ResourceLocation(domain, path);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(getLocation());
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        final CommonResourceLocationBasedGuiDefinitionLoader that = (CommonResourceLocationBasedGuiDefinitionLoader) o;
        return Objects.equals(getLocation(), that.getLocation());
    }
}
