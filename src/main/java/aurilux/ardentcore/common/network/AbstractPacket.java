package aurilux.ardentcore.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;

/**
 * This class was created by <Aurilux>. It's distributed as part of the ArdentCore Mod.
 * <p/>
 * ArdentCore is Open Source and distributed under the GNU Lesser General Public License v3.0
 * (https://www.gnu.org/licenses/lgpl.html)
 * <p/>
 * File Created @ [12 Jan 2015]
 */
public abstract class AbstractPacket<REQ extends IMessage> implements IMessage, IMessageHandler<REQ, REQ> {
    @Override
    public REQ onMessage(REQ message, MessageContext ctx) {
        if (ctx.side == Side.SERVER) {
            handleServerSide(message, ctx.getServerHandler().playerEntity);
        } else {
            handleClientSide(message, null);
        }
        return null;
    }

    /**
     * Handle a packet on the client side. Note this occurs after decoding has completed.
     *
     * @param message
     * @param player  the player reference
     */
    public abstract void handleClientSide(REQ message, EntityPlayer player);

    /**
     * Handle a packet on the server side. Note this occurs after decoding has completed.
     *
     * @param message
     * @param player  the player reference
     */
    public abstract void handleServerSide(REQ message, EntityPlayer player);
}