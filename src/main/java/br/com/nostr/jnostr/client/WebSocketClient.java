package br.com.nostr.jnostr.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;

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
            latch.countDown();
        }
        // return WebSocket.Listener.super.onText(webSocket, data, last);

        return new CompletableFuture(). completedStage(data);
    }

    @Override
    public CompletionStage<?> onPong(java.net.http.WebSocket arg0, ByteBuffer arg1) {
        // TODO Auto-generated method stub
        return Listener.super.onPong(arg0, arg1);
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
