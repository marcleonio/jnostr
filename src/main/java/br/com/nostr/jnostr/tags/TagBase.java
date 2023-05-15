package br.com.nostr.jnostr.tags;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import br.com.nostr.jnostr.jackson.CustomBaseTagSerializer;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonSerialize(using=CustomBaseTagSerializer.class)
public abstract class TagBase {

    @Size(max=1)
    protected String id;

    public abstract String getId();

    public abstract String toTag();
}
