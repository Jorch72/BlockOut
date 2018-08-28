package com.minecolonies.blockout.loader.core;

import com.minecolonies.blockout.binding.dependency.IDependencyObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

public interface IUIElementData
{

    @NotNull
    default <T extends Serializable> IDependencyObject<T> getBoundAttribute(@NotNull final String attributeName)
    {
        return getBoundAttribute(attributeName, null);
    }

    @NotNull
    <T extends Serializable> IDependencyObject<T> getBoundAttribute(@NotNull final String attributeName, @Nullable final T def);
}
