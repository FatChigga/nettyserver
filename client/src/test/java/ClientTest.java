import com.doyuyu.client.NettyClient;
import com.doyuyu.common.RpcRequest;
import com.doyuyu.common.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.util.UUID;

/**
 * Created by Song on 2019/4/3.
 */
public class ClientTest {
    public static void main(String[] args) throws Exception {
        //启动server服务
        NettyClient nettyClient = new NettyClient(9023,"127.0.0.1");
        nettyClient.startup();
        Channel channel = nettyClient.getChannel();
        RpcRequest rpcRequest = RpcRequest.builder().id(UUID.randomUUID().toString()).data("client.message").build();
        ChannelFuture future = channel.writeAndFlush(rpcRequest).sync();
    }
}
