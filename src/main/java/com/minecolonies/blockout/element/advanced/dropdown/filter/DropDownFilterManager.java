package com.minecolonies.blockout.element.advanced.dropdown.filter;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class DropDownFilterManager
{
    private static DropDownFilterManager                       ourInstance                   = new DropDownFilterManager();
    private final  Map<Class<?>, IDropDownFilteringHandler<?>> filteringHandlerLinkedHashMap = new HashMap<>();

    private DropDownFilterManager()
    {
    }

    public static DropDownFilterManager getInstance()
    {
        return ourInstance;
    }

    public <T> void registerFilterHandler(@NotNull final Class<T> clz, @NotNull final IDropDownFilteringHandler<? super T> handler)
    {
        filteringHandlerLinkedHashMap.put(clz, handler);
    }

    public <T> Collection<T> filter(@NotNull final Collection<T> enumeration, @NotNull final String filterString)
    {
        final Optional<T> firstNonNullEntryOptional = enumeration.stream().filter(Objects::nonNull).findFirst();

        if (!firstNonNullEntryOptional.isPresent())
        {
            return ImmutableList.of();
        }

        final T firstNonNullEntry = firstNonNullEntryOptional.get();

        final Collection<IDropDownFilteringHandler<? super T>> filters = filteringHandlerLinkedHashMap.entrySet()
                                                                           .stream()
                                                                           .filter(e -> e.getKey().isInstance(firstNonNullEntry))
                                                                           .map(Map.Entry::getValue)
                                                                           .map(f -> (IDropDownFilteringHandler<? super T>) f)
                                                                           .collect(
                                                                             Collectors.toList());

        return enumeration.stream()
                 .filter(e -> filters.stream().allMatch(f -> f.matches(e, filterString))).collect(Collectors.toList());
    }
}
