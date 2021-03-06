package com.minecolonies.blockout.context;

import com.minecolonies.blockout.context.core.IContext;
import com.minecolonies.blockout.util.Constants;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class EntityContext implements IContext
{
    private static final long serialVersionUID = Constants.SERIAL_VAR_ID;

    @NotNull
    private UUID entityId = new UUID(0,0);

    public EntityContext()
    {
    }

    public EntityContext(@NotNull final UUID entityId) {
        this.entityId = entityId;
    }

    public EntityContext(@NotNull final Entity entity)
    {
        this.entityId = entity.getPersistentID();
    }

    @NotNull
    public UUID getEntityId()
    {
        return entityId;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof EntityContext))
        {
            return false;
        }

        final EntityContext that = (EntityContext) o;

        return getEntityId().equals(that.getEntityId());
    }

    @Override
    public int hashCode()
    {
        return getEntityId().hashCode();
    }

    @Override
    public String toString()
    {
        return "EntityContext{" +
                 "entityId=" + entityId +
                 '}';
    }
}
