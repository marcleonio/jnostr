package br.com.nostr.jnostr.nip;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import br.com.nostr.jnostr.enums.TypeClientEnum;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
@JsonInclude(Include.NON_NULL)
public abstract class Message {
    
    // @JsonProperty("")
    @Setter(AccessLevel.NONE)
    private TypeClientEnum type;

    public abstract TypeClientEnum getType();
}
