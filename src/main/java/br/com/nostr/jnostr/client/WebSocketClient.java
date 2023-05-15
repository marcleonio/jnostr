package br.com.nostr.jnostr.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class WebSocketClient implements WebSocket.Listener {
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
        return WebSocket.Listener.super.onText(webSocket, data, last);
    }

    @Override
    public CompletionStage<?> onPong(java.net.http.WebSocket arg0, ByteBuffer arg1) {
        // TODO Auto-generated method stub
        return Listener.super.onPong(arg0, arg1);
    }

    public WebSocket startSocket(String connection) {
        CompletableFuture<WebSocket> server_cf = HttpClient.newHttpClient().newWebSocketBuilder().buildAsync(
                URI.create(connection),
                new WebSocketClient());
 
        return server_cf.join();
    }
}
