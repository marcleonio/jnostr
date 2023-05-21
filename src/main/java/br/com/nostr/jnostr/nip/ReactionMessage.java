package br.com.nostr.jnostr.nip;

import br.com.nostr.jnostr.enums.TypeClientEnum;
import lombok.Data;

@Data
public class ReactionMessage extends Message{

    private Event event;
    
    @Override
    public TypeClientEnum getType() {
        return TypeClientEnum.EVENT;
    }
    
}
