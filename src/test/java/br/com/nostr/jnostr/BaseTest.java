package br.com.nostr.jnostr;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.nostr.jnostr.crypto.schnorr.Schnorr;
import br.com.nostr.jnostr.enums.TypeClientEnum;
import br.com.nostr.jnostr.nip.ClientToRelay;
import br.com.nostr.jnostr.nip.EventMessage;
import br.com.nostr.jnostr.nip.Filters;
import br.com.nostr.jnostr.nip.Message;
import br.com.nostr.jnostr.nip.Event;
import br.com.nostr.jnostr.nip.ReqMessage;
import br.com.nostr.jnostr.tags.TagP;
import br.com.nostr.jnostr.util.NostrUtil;
import jakarta.validation.Valid;

public class BaseTest {
    
    public String createNIP01() {
        try {
            ObjectMapper mapper = new ObjectMapper().setSerializationInclusion(Include.NON_NULL);
            
            var messages = new ClientToRelay();
            EventMessage message = (EventMessage) createEventMessage();
            messages.setMessages(Arrays.asList(message));
            
            return mapper.writeValueAsString(messages);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
        
    }

    public Message createEventMessage() {
        // var privateKey = NostrUtil.generatePrivateKey();
        // String pubkey = NostrUtil.bigIntFromBytes(NostrUtil.genPubKey(privateKey)).toString(16);

        var event = new Event();

        event.setKind(1);
        // nip.setTags(Arrays.asList(TagP.builder().pubkey(pubkey).recommendedRelayURL("JNostr").build()));
        // nip.setTags(Arrays.asList(TagP.builder().pubkey(pubkey).recommendedRelayURL("JNostr").build()));
        event.setContent("Hello world, I'm here on JNostr API!");
            
            
        EventMessage message = new EventMessage();
        message.setEvent(event);
        return message;
    }

    @Valid
    protected Message createReqMessage() {

        ReqMessage message = new ReqMessage();
        message.setSubscriptionId(UUID.randomUUID().toString());
        Filters filter = new Filters();
        filter.setKinds(Arrays.asList(1));
        filter.setLimit(100);
        message.setFilters(filter);

        return message;
    }
}
