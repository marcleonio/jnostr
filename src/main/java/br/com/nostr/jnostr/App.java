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
import br.com.nostr.jnostr.nip.Message;
import br.com.nostr.jnostr.nip.Nip;
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
            
            System.out.println(jnostr.relayInfo(relay).toString());

            latch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ClientToRelay nip01()
            throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, Exception {
        var privateKey = NostrUtil.generatePrivateKey();
        String pubkey = NostrUtil.bigIntFromBytes(NostrUtil.genPubKey(privateKey)).toString(16);

        var nip = new Nip();
        nip.setKind(1);
        nip.setTags(Arrays.asList(TagP.builder().pubkey(pubkey).recommendedRelayURL("JNostr").build()));
        nip.setContent("Hello world, I'm here on JNostr API!");
        nip.setPubkey(pubkey);
        nip.setCreatedAt(Instant.now().getEpochSecond());
        
        nip.setId(NostrUtil.bytesToHex(NostrUtil.sha256(nip.serialize())));

        var signed = Schnorr.sign(NostrUtil.sha256(nip.serialize()), privateKey, NostrUtil.createRandomByteArray(32));

        nip.setSig(NostrUtil.bytesToHex(signed));

        var messages = new ClientToRelay();
        Message message = new Message();
        message.setType(TypeClientEnum.EVENT);
        message.setNip(nip);
        messages.setMessages(Arrays.asList(message));

        return messages;
    }

}
