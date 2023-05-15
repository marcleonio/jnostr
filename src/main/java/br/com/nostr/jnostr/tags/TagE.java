package br.com.nostr.jnostr.tags;

import java.util.Arrays;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class TagE extends TagBase {
    
    private String idAnotherEvent;
    private String recommendedRelayURL;

    @Override
    public String getId() {
        return "e";
    }

    @Override
    public String toTag() {
        return Arrays.asList(getId(),idAnotherEvent,recommendedRelayURL).toString();
    }


}
