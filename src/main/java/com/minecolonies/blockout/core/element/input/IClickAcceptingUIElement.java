package com.minecolonies.blockout.core.element.input;

import com.minecolonies.blockout.core.element.IUIElement;
import com.minecolonies.blockout.util.mouse.MouseButton;

public interface IClickAcceptingUIElement extends IUIElement
{
    boolean canAcceptMouseInput(final int localX, final int localY, final MouseButton button);

    void onMouseClickBegin(final int localX, final int localY, final MouseButton button);

    void onMouseClickEnd(final int localX, final int localY, final MouseButton button);

    void onMouseClickMove(final int localX, final int localY, final MouseButton button, final float timeElapsed);
}
