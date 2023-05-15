package br.com.nostr.jnostr;

import java.time.Instant;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.nostr.jnostr.crypto.schnorr.Schnorr;
import br.com.nostr.jnostr.enums.TypeClientEnum;
import br.com.nostr.jnostr.nip.ClientToRelay;
import br.com.nostr.jnostr.nip.Message;
import br.com.nostr.jnostr.nip.Nip;
import br.com.nostr.jnostr.tags.TagP;
import br.com.nostr.jnostr.util.NostrUtil;

public class BaseTest {
    
    public String createNIP01() {
        try {
            ObjectMapper mapper = new ObjectMapper().setSerializationInclusion(Include.NON_NULL);
            
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
            
            return mapper.writeValueAsString(messages);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
        
    }
}
