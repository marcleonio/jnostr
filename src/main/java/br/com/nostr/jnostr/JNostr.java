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
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.nostr.jnostr.client.WebSocketClient;
import br.com.nostr.jnostr.crypto.schnorr.Schnorr;
import br.com.nostr.jnostr.enums.TypeClientEnum;
import br.com.nostr.jnostr.nip.ClientToRelay;
import br.com.nostr.jnostr.nip.EventMessage;
import br.com.nostr.jnostr.nip.Message;
import br.com.nostr.jnostr.nip.Event;
import br.com.nostr.jnostr.nip.ReqMessage;
import br.com.nostr.jnostr.server.RelayInfo;
import br.com.nostr.jnostr.tags.TagP;
import br.com.nostr.jnostr.util.NostrUtil;
import jakarta.validation.Valid;
import lombok.extern.java.Log;

@Log
class JNostr {

    
    private WebSocketClient wsc = new WebSocketClient();
    private WebSocket connectedRelay;
    private String privateKey;
    private String[] relays;

    public JNostr(String privateKey){
        this.privateKey = privateKey;
    }

    public String[] getRelays() {
        return relays;
    }

    public JNostr initialize(String ... relays){
        Objects.requireNonNull(relays, "null relay");
        // this.wsc = new WebSocketClient();
        this.connectedRelay = wsc.startSocket("wss://"+relays[0]);

        // wsc.onText(connectedRelay, privateKey, false)
        this.relays = relays;
        return this;
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

    public JNostr connectRelay(String relay){
        // this.wsc = new WebSocketClient();
        this.connectedRelay = wsc.startSocket("wss://"+relay);

        return this;
    }

    public void sendMessage(String relay, ClientToRelay messages)  {
        
        connectRelay(relay);
        sendMessage(messages);

    }

    public String sendMessage(@Valid ClientToRelay messages) {
        ObjectMapper mapper = new ObjectMapper().setSerializationInclusion(Include.NON_NULL);
        try {
            //TODO verificar se é melhor com customJackson ou enviar em lista
            // List<String> jsonList = new ArrayList<>();
            // jsonList.add("\"" + TypeClientEnum.EVENT.name() + "\"");
            // jsonList.add(mapper.writeValueAsString(messages.getMessages().get(0).getNip()));
            // System.out.println(jsonList.toString());
        
            var json = mapper.writeValueAsString(messages);
            if(connectedRelay!=null) {
                connectedRelay.sendText(json, true);
                wsc.getLatch().await();
                return wsc.getData();
            } else {
                throw new RuntimeException("relay not found");
            }
        } catch (JsonProcessingException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // public void sendMessage(String relay, Event nip) {
    //         connectRelay(relay);
    //         sendMessage(nip);
    // }

    // public void sendMessage(Event nip) {
    //     try {
        
    //         byte[] privkey = NostrUtil.hexToBytes(privateKey);
    //         String pubkey = NostrUtil.bigIntFromBytes(NostrUtil.genPubKey(privkey)).toString(16);

    //         nip.setPubkey(pubkey);
    //         nip.setCreatedAt(Instant.now().getEpochSecond());
    //         nip.setTags(Arrays.asList(TagP.builder().pubkey(pubkey).recommendedRelayURL("JNostr").build()));
            
    //         nip.setId(NostrUtil.bytesToHex(NostrUtil.sha256(nip.serialize())));

    //         var signed = Schnorr.sign(NostrUtil.sha256(nip.serialize()), privkey, NostrUtil.createRandomByteArray(32));

    //         nip.setSig(NostrUtil.bytesToHex(signed));

    //         var messages = new ClientToRelay();
    //         EventMessage message = new EventMessage();
    //         message.setNip(nip);
    //         messages.setMessages(Arrays.asList(message));

    //         sendMessage(messages);

    //     } catch (Exception e) {
    //         throw new RuntimeException(e);
    //     }

    // }

    public void sendMessage(String relay, @Valid Message message) {
        connectRelay(relay);
        sendMessage(message);
    }

    public String sendMessage(@Valid Message message) {
        var messages = new ClientToRelay();

        if(message instanceof EventMessage){
            var event = ((EventMessage)message).getEvent();
            event.setCreatedAt(Instant.now().getEpochSecond());

            byte[] privkey = NostrUtil.hexToBytes(privateKey);
            String pubkey = NostrUtil.bigIntFromBytes(NostrUtil.genPubKey(privkey)).toString(16);

            event.setPubkey(pubkey);
            event.setTags(Arrays.asList(TagP.builder().pubkey(pubkey).recommendedRelayURL("JNostr").build()));
            event.setId(NostrUtil.bytesToHex(NostrUtil.sha256(event.serialize())));

            var signed = Schnorr.sign(NostrUtil.sha256(event.serialize()), privkey, NostrUtil.createRandomByteArray(32));
            event.setSig(NostrUtil.bytesToHex(signed));
        }


        messages.setMessages(Arrays.asList(message));
        return sendMessage(messages);
    }

    

    


}
