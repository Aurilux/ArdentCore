package aurilux.ardentcore.common.mod;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.List;

/**
 * This class was created by <Aurilux>. It's distributed as part of the Titles Mod.
 * <p/>
 * Titles is Open Source and distributed under the GNU Lesser General Public License v3.0
 * (https://www.gnu.org/licenses/lgpl.html)
 * <p/>
 * File Created @ [07 Apr 2015]
 */
public class NetworkWrapper extends SimpleNetworkWrapper {
    private int discriminator;

    public NetworkWrapper(String channelName) {
        super(channelName);
        discriminator = 0;
    }

    //TODO see if I can make this helper work. Send the class just once, discriminator handled by this class
    /*
    public <REQ extends IMessage, REPLY extends IMessage> void registerClient/ServerMessage(Class<REQ> message, Side side) {
        registerMessage(message, message, discriminator++, side);
    }
    */
    public <REQ extends IMessage, REPLY extends IMessage> void registerClientMessage(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType) {
        registerMessage(messageHandler, requestMessageType, discriminator++, Side.CLIENT);
    }

    public <REQ extends IMessage, REPLY extends IMessage> void registerServerMessage(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType) {
        registerMessage(messageHandler, requestMessageType, discriminator++, Side.SERVER);
    }

    /** This is a wrapper method with a name that gives more context */
    public void sendToPlayer(IMessage message, EntityPlayerMP player) {
        this.sendTo(message, player);
    }

    public void sendToAllExcept(IMessage message, EntityPlayerMP player) {
        List playerList = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList;
        for(int i = 0; i < playerList.size(); i++) {
            EntityPlayerMP player1 = (EntityPlayerMP) playerList.get(i);
            if(player.getCommandSenderName().equalsIgnoreCase(player1.getCommandSenderName())) continue;
            sendToPlayer(message, player1);
        }
    }
}
