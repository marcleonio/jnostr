package br.com.nostr.jnostr.nip;

import br.com.nostr.jnostr.enums.TypeClientEnum;

public class CountMessage extends ReqMessage {

    @Override
    public TypeClientEnum getType() {
        return TypeClientEnum.COUNT;
    }
    
}
