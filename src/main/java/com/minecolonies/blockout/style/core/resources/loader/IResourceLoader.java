package com.minecolonies.blockout.style.core.resources.loader;

import com.google.gson.JsonElement;
import com.minecolonies.blockout.style.core.resources.core.IResource;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public interface IResourceLoader<T extends IResource>
{

    /**
     * Returns the if of the type that this {@link IResourceLoader} can load.
     *
     * @return The id if the type. EG: image, template.
     */
    @NotNull
    String getTypeId();

    /**
     * Loads a resource for a given id and data.
     *
     * @param id   THe id to load for.
     * @param data The data to load from.
     */
    T load(@NotNull final ResourceLocation id, @NotNull final JsonElement data);
}
