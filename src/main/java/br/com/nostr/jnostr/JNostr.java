package br.com.nostr.jnostr;

import br.com.nostr.jnostr.client.RelayThread;
import br.com.nostr.jnostr.enums.TypeRealyEnum;
import br.com.nostr.jnostr.nip.Message;
import br.com.nostr.jnostr.server.RelayInfo;
import jakarta.validation.Valid;

public interface JNostr {

    RelayInfo relayInfo(String relay);

    JNostrImpl initialize(String ... relays);

    void sendMessage(String relay, Message nip01);

    RelayThread relayInit(TypeRealyEnum realyEnun,String ... relays);

    String sendMessage(@Valid Message createReqMessage);

    String getPrivateKey();
    
}
