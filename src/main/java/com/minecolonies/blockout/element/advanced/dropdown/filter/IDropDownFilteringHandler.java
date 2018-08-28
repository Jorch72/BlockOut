package com.minecolonies.blockout.element.advanced.dropdown.filter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface IDropDownFilteringHandler<T>
{
    boolean matches(@Nullable final T target, @NotNull final String searchString);
}
