package com.minecolonies.blockout.management.common.input;

import com.minecolonies.blockout.core.element.IUIElement;
import com.minecolonies.blockout.core.element.input.IKeyAcceptingUIElement;
import com.minecolonies.blockout.core.management.IUIManager;
import com.minecolonies.blockout.core.management.input.IKeyManager;
import com.minecolonies.blockout.util.keyboard.KeyboardKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class KeyManager extends AbstractInputManager implements IKeyManager
{
    public KeyManager(final IUIManager manager)
    {
        super(manager);
    }

    @Override
    public void onKeyPressed(final int character, final KeyboardKey key)
    {
        attemptInputInteraction(
          t -> t.canAcceptKeyInput(character, key),
          t -> t.onKeyPressed(character, key)
        );
    }

    protected void attemptInputInteraction(
      final IInteractionAcceptanceCallback acceptanceCallback,
      final IInteractionExecutionCallback executionCallback)
    {
        @Nullable final IUIElement currentFocus = getManager().getFocusManager().getFocusedElement();

        if (currentFocus instanceof IKeyAcceptingUIElement)
        {
            if (attemptMouseInteractionWith(currentFocus, acceptanceCallback, executionCallback))
            {
                return;
            }
        }

        onAcceptanceFailure();
    }

    private boolean attemptMouseInteractionWith(
      @NotNull final IUIElement target,
      final IInteractionAcceptanceCallback acceptanceCallback,
      final IInteractionExecutionCallback executionCallback)
    {
        @NotNull final IKeyAcceptingUIElement t = (IKeyAcceptingUIElement) target;

        if (acceptanceCallback.apply(t))
        {
            executionCallback.apply(t);
            return true;
        }

        return false;
    }

    @FunctionalInterface
    protected interface IInteractionAcceptanceCallback
    {
        boolean apply(@NotNull final IKeyAcceptingUIElement target);
    }

    @FunctionalInterface
    protected interface IInteractionExecutionCallback
    {
        void apply(@NotNull final IKeyAcceptingUIElement target);
    }
}
