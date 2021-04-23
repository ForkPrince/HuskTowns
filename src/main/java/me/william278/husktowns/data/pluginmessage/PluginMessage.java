package me.william278.husktowns.data.pluginmessage;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.william278.husktowns.HuskTowns;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Locale;

public class PluginMessage {

    private static final HuskTowns plugin = HuskTowns.getInstance();

    // Move a player to a different server in the bungee network
    public static void sendPlayer(Player p, String targetServer) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(targetServer);
        p.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }

    private final int clusterID;
    private final PluginMessageType messageType;
    private final String targetPlayerName;
    private final String messageData;

    /**
     * Creates a Plugin Message ready to be sent
     * @param targetPlayerName Name of the player the message will be sent to over Bungee
     * @param pluginMessageType Type of the plugin message
     * @param messageData Associated message data
     */
    public PluginMessage(String targetPlayerName, PluginMessageType pluginMessageType, String messageData) {
        this.clusterID = HuskTowns.getSettings().getClusterID();
        this.messageType = pluginMessageType;
        this.messageData = messageData;
        this.targetPlayerName = targetPlayerName;
    }

    public PluginMessage(int clusterID, String targetPlayerName, String pluginMessageType, String messageData) {
        this.clusterID = clusterID;
        this.messageType = PluginMessageType.valueOf(pluginMessageType.toUpperCase(Locale.ENGLISH));
        this.messageData = messageData;
        this.targetPlayerName = targetPlayerName;
    }

    // Get the string version of the plugin message type
    private String getPluginMessageString(PluginMessageType type) {
        return type.name().toLowerCase(Locale.ENGLISH);
    }

    /**
     * Send the plugin message
     * @param sender The player to send the message
     */
    public void send(Player sender) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        // Send a plugin message to the specified player name
        out.writeUTF("ForwardToPlayer");
        out.writeUTF(targetPlayerName);

        // Send the HuskTowns message with a specific type
        out.writeUTF("HuskTowns:" + clusterID + ":" + getPluginMessageString(messageType));
        ByteArrayOutputStream messageBytes = new ByteArrayOutputStream();
        DataOutputStream messageOut = new DataOutputStream(messageBytes);

        // Send the message data; output an exception if there's an error
        try {
            messageOut.writeUTF(messageData);
        } catch (IOException e) {
            plugin.getLogger().warning("An error occurred trying to send a plugin message (" + e.getCause() + ")");
            e.printStackTrace();
        }

        // Write the messages to the output packet
        out.writeShort(messageBytes.toByteArray().length);
        out.write(messageBytes.toByteArray());

        // Send the constructed plugin message packet
        sender.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }

    public int getClusterID() {
        return clusterID;
    }

    public PluginMessageType getMessageType() {
        return messageType;
    }

    public String getTargetPlayerName() {
        return targetPlayerName;
    }

    public String getMessageData() {
        return messageData;
    }
}
