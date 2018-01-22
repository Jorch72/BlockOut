package com.minecolonies.blockout.element.management;

import com.minecolonies.blockout.core.element.IUIElement;
import com.minecolonies.blockout.core.management.IUIManager;
import com.minecolonies.blockout.core.management.focus.IFocusManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FocusManager implements IFocusManager
{

    @NotNull
    private final IUIManager manager;

    @Nullable
    private IUIElement focusedElement = null;

    public FocusManager(final IUIManager manager) {this.manager = manager;}

    @Nullable
    @Override
    public IUIElement getFocusedElement()
    {
        return focusedElement;
    }

    @Override
    public void setFocusedElement(@Nullable final IUIElement focusedElement)
    {
        if (this.focusedElement.equals(focusedElement))
        {
            return;
        }

        final IUIElement oldElement = this.focusedElement;
        this.focusedElement = focusedElement;

        manager.getNetworkManager().onFocusChanged(oldElement, focusedElement);
    }
}