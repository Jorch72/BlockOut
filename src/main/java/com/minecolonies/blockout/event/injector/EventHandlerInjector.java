package com.minecolonies.blockout.event.injector;

import com.minecolonies.blockout.core.element.IUIElement;
import com.minecolonies.blockout.event.Event;
import com.minecolonies.blockout.util.Log;
import com.minecolonies.blockout.util.reflection.ReflectionUtil;
import org.jetbrains.annotations.NotNull;

public class EventHandlerInjector
{

    private EventHandlerInjector()
    {
        throw new IllegalArgumentException("Utility Class");
    }

    public static void inject(@NotNull final IUIElement target, @NotNull final IEventHandlerProvider provider)
    {
        ReflectionUtil.getFields(target.getClass())
          .stream()
          .filter(field -> field.getType().equals(Event.class))
          .forEach(eventField -> {
              eventField.setAccessible(true);

              final Event<?, ?> event;
              try
              {
                  event = (Event<?, ?>) eventField.get(target);
              }
              catch (Exception e)
              {
                  Log.getLogger().warn("Failed to get event for handler binding: " + eventField.getName() + " from: " + target.getId() + ". Is it populated?");
                  return;
              }

              provider.getEventHandlers(String.format("%s#%s", target.getId(), eventField.getName()), event.getSourceClass(), event.getArgumentClass())
                .forEach(event::registerHandler);
          });
    }
}
