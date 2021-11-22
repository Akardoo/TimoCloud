package cloud.timo.TimoCloud.core.sockets;

import cloud.timo.TimoCloud.common.utils.network.NettyUtil;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;

public class CoreSocketServer {

    public void init(String address, int port) throws Exception {
        EventLoopGroup bossGroup = NettyUtil.getEventLoopGroup();
        EventLoopGroup workerGroup = NettyUtil.getEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b
                    .childHandler(new CorePipeline())
                    .group(bossGroup, workerGroup)
                    .channel(NettyUtil.getServerSocketChannelClass())
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
            ;


            // Start the server.
            ChannelFuture f = b.bind(address, port).sync();
            TimoCloudCore.getInstance().setChannel(f.channel());
            TimoCloudCore.getInstance().info("Successfully started socket server on " + address + ":" + port + "!");
            // Wait until the server socket is closed.
            try {
                f.channel().closeFuture().sync();
            } catch (Exception e) {
                TimoCloudCore.getInstance().info("Socketserver closed.");
            }
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}