package br.com.nostr.jnostr.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;

import br.com.nostr.jnostr.util.NostrUtil;
import lombok.Getter;

@Getter
public class WebSocketClient implements WebSocket.Listener {

    private String data;
    private CountDownLatch latch;

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        // TODO Auto-generated method stub
        // return Listener.super.onClose(arg0, arg1, arg2);
        // webSocket.sendClose(CUSTOM_STATUS_CODE, CUSTOM_REASON);
        webSocket.sendClose(statusCode, reason);
        latch.countDown();
        return new CompletableFuture<Void>();
    }

    // Insert Body here
    @Override
    public void onOpen(WebSocket webSocket) {
        // TODO Auto-generated method stub
        Listener.super.onOpen(webSocket);

        System.out.println("onOpen using subprotocol " + webSocket.getSubprotocol());
        // WebSocket.Listener.super.onOpen(webSocket);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        // TODO Auto-generated method stub
        // Listener.super.onError(webSocket, error);

        System.out.println("Bad day! " + webSocket.toString());
        WebSocket.Listener.super.onError(webSocket, error);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        // TODO Auto-generated method stub
        // return Listener.super.onText(webSocket, data, last);
        System.out.println("onText received " + data);

        if(last){
            this.data = data.toString();
            if(this.data.contains("EOSE")){
                latch.countDown();
            }else{
                webSocket.request(1);
            }
        }

        return CompletableFuture.completedFuture(data)
          .thenAccept(o -> System.out.println("Handling data: " + o));

       
        // return WebSocket.Listener.super.onText(webSocket, data, last);

        // return new CompletableFuture(). completedStage(data);
    }

    @Override
    public CompletionStage<?> onBinary(WebSocket arg0, ByteBuffer arg1, boolean arg2) {
        // TODO Auto-generated method stub
        return Listener.super.onBinary(arg0, arg1, arg2);
    }

    @Override
    public CompletionStage<?> onPong(WebSocket arg0, ByteBuffer arg1) {
        
        System.out.println("Pong received with data: " + new String(arg1.array()));
        return Listener.super.onPong(arg0, arg1);
    }

    @Override
    public CompletionStage<?> onPing(WebSocket arg0, ByteBuffer arg1) {
        // TODO Auto-generated method stub
        System.out.println("Ping received with data: " + new String(arg1.array()));
        return Listener.super.onPing(arg0, arg1);
    }

    public WebSocket startSocket(String connection) {
        CompletableFuture<WebSocket> server_cf = HttpClient.newHttpClient().newWebSocketBuilder().buildAsync(
                URI.create(connection), this);
                latch = new CountDownLatch(1);
                this.data = null;
        return server_cf.join();
    }

    public void setLatch(CountDownLatch countDownLatch) {
        this.latch = countDownLatch;
    }
}
