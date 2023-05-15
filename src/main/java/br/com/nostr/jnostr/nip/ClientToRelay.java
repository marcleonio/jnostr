package br.com.nostr.jnostr.nip;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import br.com.nostr.jnostr.jackson.CustomClientSerializer;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonSerialize(using=CustomClientSerializer.class)
public class ClientToRelay {

    @Size(max = 1)
    private List<Message> messages;
}
