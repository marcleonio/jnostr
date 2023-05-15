package br.com.nostr.jnostr.nip;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.nostr.jnostr.enums.TypeClientEnum;
import lombok.Data;

@Data
public class Message {
    
    private TypeClientEnum type;
    @JsonProperty("subscription_id")
    private String subscriptionId;
    private Nip nip;
    private Filters filters;
}
