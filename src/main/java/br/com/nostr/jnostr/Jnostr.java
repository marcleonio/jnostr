package br.com.nostr.jnostr;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.WebSocket;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.nostr.jnostr.client.WebSocketClient;
import br.com.nostr.jnostr.crypto.schnorr.Schnorr;
import br.com.nostr.jnostr.enums.TypeClientEnum;
import br.com.nostr.jnostr.nip.ClientToRelay;
import br.com.nostr.jnostr.nip.Message;
import br.com.nostr.jnostr.nip.Nip;
import br.com.nostr.jnostr.server.RelayInfo;
import br.com.nostr.jnostr.tags.TagP;
import br.com.nostr.jnostr.util.NostrUtil;
import lombok.extern.java.Log;

@Log
public class Jnostr {

    private WebSocket server;
    private String privateKey;


    public void initialize(String privateKey, String ... relay){
        WebSocketClient wsc = new WebSocketClient();
        this.server = wsc.startSocket("wss://"+relay[0]);
        this.privateKey = privateKey;
    }

    public RelayInfo relayInfo(String relay) {
        ObjectMapper mapper = new ObjectMapper();
        HttpResponse<String> response = null;
        try {
            
            HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI("https://"+relay))
            .headers("Accept", "application/nostr+json")
            .GET()
            .build();
            
            response = HttpClient.newHttpClient()
            .send(request, HttpResponse.BodyHandlers.ofString());
            
            return mapper.readValue(response.body(), RelayInfo.class);
        } catch (URISyntaxException | IOException | InterruptedException e) {
            log.log(Level.WARNING, "Interrupted!", e);
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        
    }

    public WebSocket connectRelay(String relay){
        WebSocketClient wsc = new WebSocketClient();
        this.server = wsc.startSocket("wss://"+relay);

        return server;
    }

    public void sendMessage(String relay, ClientToRelay messages)  {
        
        server = connectRelay(relay);

        sendMessage(messages);

    }

    public void sendMessage(ClientToRelay messages) {
        ObjectMapper mapper = new ObjectMapper().setSerializationInclusion(Include.NON_NULL);
        try {
            //TODO verificar se é melhor com customJackson ou enviar em lista
            // List<String> jsonList = new ArrayList<>();
            // jsonList.add("\"" + TypeClientEnum.EVENT.name() + "\"");
            // jsonList.add(mapper.writeValueAsString(messages.getMessages().get(0).getNip()));
            // System.out.println(jsonList.toString());
        
            var json = mapper.writeValueAsString(messages);
            if(server!=null) {
                server.sendText(json, true);
            } else {
                throw new RuntimeException("server not found");
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String relay, String content) {
        ObjectMapper mapper = new ObjectMapper();
        server = connectRelay(relay);
        ClientToRelay messages = null;
        try {
            messages = mapper.readValue(content, ClientToRelay.class);
        } catch (Exception e) {}

        if(messages==null){
            sendMessage(content);
        }else{
            sendMessage(messages);
        }
    }

    public void sendMessage(String content) {

        ObjectMapper mapper = new ObjectMapper();

        ClientToRelay messages = null;
        try {
            messages = mapper.readValue(content, ClientToRelay.class);
        } catch (Exception e) {}

        if(messages==null){
            Nip nip = new Nip();
            nip.setKind(1);
            nip.setContent(content);
            
            sendMessage(nip);
        }else{
            sendMessage(messages);
        }
        
    }

    public void sendMessage(String relay, Nip nip) {
        try {
        
            server = connectRelay(relay);

            sendMessage(nip);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void sendMessage(Nip nip) {
        try {
        
            byte[] privkey = NostrUtil.hexToBytes(privateKey);
            String pubkey = NostrUtil.bigIntFromBytes(NostrUtil.genPubKey(privkey)).toString(16);

            nip.setPubkey(pubkey);
            nip.setCreatedAt(Instant.now().getEpochSecond());
            nip.setTags(Arrays.asList(TagP.builder().pubkey(pubkey).recommendedRelayURL("JNostr").build()));
            
            nip.setId(NostrUtil.bytesToHex(NostrUtil.sha256(nip.serialize())));

            var signed = Schnorr.sign(NostrUtil.sha256(nip.serialize()), privkey, NostrUtil.createRandomByteArray(32));

            nip.setSig(NostrUtil.bytesToHex(signed));

            var messages = new ClientToRelay();
            Message message = new Message();
            message.setType(TypeClientEnum.EVENT);
            message.setNip(nip);
            messages.setMessages(Arrays.asList(message));

            sendMessage(messages);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


}
