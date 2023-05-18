package br.com.nostr.jnostr.nip;

import br.com.nostr.jnostr.enums.TypeClientEnum;
import lombok.Data;

@Data
public class ReqMessage extends CloseMessage {

    private Filters filters;

    @Override
    public TypeClientEnum getType() {
        return TypeClientEnum.REQ;
    }
    
}
