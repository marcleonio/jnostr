package br.com.nostr.jnostr;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.WebSocket;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

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

/**
 * Hello world!
 *
 */
public class App 
{
    
    public static void main( String[] args ) {
        System.out.println("Java Version: " + NostrUtil.getJavaVersion());
        JNostr jnostr = JNostrFactory.getInstance(NostrUtil.toHex(NostrUtil.generatePrivateKey()));
        jnostr.initialize("relay.taxi");
        try {
            CountDownLatch latch = new CountDownLatch(1);
            String relay = "relay.taxi";

            jnostr.sendMessage(relay, nip01());

            jnostr.sendMessage(relay,new ReqMessage());
            
            System.out.println(jnostr.relayInfo(relay).toString());

            latch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Message nip01()
            throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, Exception {
        var privateKey = NostrUtil.generatePrivateKey();
        String pubkey = NostrUtil.bigIntFromBytes(NostrUtil.genPubKey(privateKey)).toString(16);

        var event = new Event();
        event.setKind(1);
        event.setTags(Arrays.asList(TagP.builder().pubkey(pubkey).recommendedRelayURL("JNostr").build()));
        event.setContent("Hello world, I'm here on JNostr API!");
        event.setPubkey(pubkey);
        event.setCreatedAt(Instant.now().getEpochSecond());
        
        event.setId(NostrUtil.bytesToHex(NostrUtil.sha256(event.serialize())));

        var signed = Schnorr.sign(NostrUtil.sha256(event.serialize()), privateKey, NostrUtil.createRandomByteArray(32));

        event.setSig(NostrUtil.bytesToHex(signed));

        var messages = new ClientToRelay();
        EventMessage message = new EventMessage();
        message.setEvent(event);
        messages.setMessages(Arrays.asList(message));

        return message;
    }

}
