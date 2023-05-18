package br.com.nostr.jnostr.nip;

import br.com.nostr.jnostr.enums.TypeClientEnum;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public abstract class Message {
    
    @Setter(AccessLevel.NONE)
    private TypeClientEnum type;

    public abstract TypeClientEnum getType();
}
