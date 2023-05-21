package br.com.nostr.jnostr.nip;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.nostr.jnostr.enums.TypeClientEnum;
import lombok.Data;

@Data
public class CloseMessage extends Message {

    @JsonProperty("subscription_id")
    private String subscriptionId;

    @Override
    public TypeClientEnum getType() {
        return TypeClientEnum.CLOSE;
    }

    // @JsonValue
    // public String toJson(){
    //     return TypeClientEnum.CLOSE+","+this.subscriptionId;
    // }

}
