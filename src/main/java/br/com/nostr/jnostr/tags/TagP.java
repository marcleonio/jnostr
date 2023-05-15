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
public class TagP extends TagBase {
    
    private String pubkey;
    private String recommendedRelayURL;

    @Override
    public String getId() {
        return "p";
    }

    @Override
    public String toTag() {

        this.getClass().getSigners();
        // var list = StreamSupport.stream(
		//                 Spliterators.spliteratorUnknownSize(fields, Spliterator.ORDERED), false)
		//                 .map(f -> f.getValue().asText().toLowerCase() )
		//                 .collect(Collectors.toList());

        return Arrays.asList(getId(),pubkey,recommendedRelayURL).toString();
    }


}
