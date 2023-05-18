package br.com.nostr.jnostr.server;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import br.com.nostr.jnostr.jackson.CustomServerSerializer;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonSerialize(using=CustomServerSerializer.class)
public class RelayToClient {
    
    @Size(max = 1)
    private List<ServerMessage> messages;
    
}
