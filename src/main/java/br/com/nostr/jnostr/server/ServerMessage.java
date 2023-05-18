package br.com.nostr.jnostr.server;

import br.com.nostr.jnostr.enums.TypeClientEnum;
import br.com.nostr.jnostr.enums.TypeServerEnum;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public abstract class ServerMessage {
    
    @Setter(AccessLevel.NONE)
    private TypeServerEnum type;

    public abstract TypeServerEnum getType();
}
