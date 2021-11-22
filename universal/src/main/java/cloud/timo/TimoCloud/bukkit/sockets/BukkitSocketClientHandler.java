package cloud.timo.TimoCloud.bukkit.sockets;

import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class BukkitSocketClientHandler extends ChannelInboundHandlerAdapter {

    private Channel channel;
    private String queue;

    public BukkitSocketClientHandler() {
        resetQueue();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        TimoCloudBukkit.getInstance().info("Successfully connected to Core socket!");
        this.channel = ctx.channel();
        TimoCloudBukkit.getInstance().onSocketConnect(ctx.channel());
        flush();
    }

    public void resetQueue() {
        queue = "";
    }

    public void flush() {
        if (channel == null) {
            return;
        }
        channel.writeAndFlush(queue);
        resetQueue();
    }

    public void sendMessage(String message) {
        if (channel == null) {
            queue += message;
        } else {
            channel.writeAndFlush(message);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        //causTimoCloudBukkit.getInstance().severe(e);
        ctx.close();
        TimoCloudBukkit.getInstance().onSocketDisconnect(false);
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
