package com.minecolonies.blockout.management.client.network.messages;

import com.minecolonies.blockout.BlockOut;
import com.minecolonies.blockout.connector.core.IGuiKey;
import com.minecolonies.blockout.element.root.RootGuiElement;
import com.minecolonies.blockout.network.message.core.IBlockOutClientToServerMessage;
import com.minecolonies.blockout.util.Log;
import com.minecolonies.blockout.util.keyboard.KeyboardKey;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.NotNull;

public class OnKeyPressedMessage implements IBlockOutClientToServerMessage
{
    @NotNull
    private final int         character;
    @NotNull
    private final KeyboardKey key;

    public OnKeyPressedMessage(@NotNull final int character, @NotNull final KeyboardKey key)
    {
        this.character = character;
        this.key = key;
    }

    @Override
    public void onMessageArrivalAtServer(@NotNull final MessageContext ctx)
    {
        final EntityPlayerMP player = ctx.getServerHandler().player;
        final IGuiKey key = BlockOut.getBlockOut().getProxy().getGuiController().getOpenUI(player);
        if (key == null)
        {
            Log.getLogger().error("Player is not watching a BlockOut gui.");
            return;
        }

        final RootGuiElement rootGuiElement = BlockOut.getBlockOut().getProxy().getGuiController().getRoot(key);
        if (rootGuiElement == null)
        {
            Log.getLogger().error("Player seems to be watching an unknown Gui.");
            return;
        }

        rootGuiElement.getUiManager().getKeyManager().onKeyPressed(character, this.key);
    }
}
