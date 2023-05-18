package br.com.nostr.jnostr.server;

import br.com.nostr.jnostr.enums.TypeServerEnum;
import lombok.Data;

@Data
public class NoticeMessage extends ServerMessage {

    String message;

    @Override
    public TypeServerEnum getType() {
        return TypeServerEnum.NOTICE;
    }
    
}
