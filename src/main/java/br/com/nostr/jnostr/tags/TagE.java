package br.com.nostr.jnostr.tags;

import java.util.Arrays;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import br.com.nostr.jnostr.enums.TypeMarkerEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class TagE extends TagBase {
    
    private String idAnotherEvent;

    @Builder.Default
    private String recommendedRelayURL = "";

    @Builder.Default
    private TypeMarkerEnum marker = TypeMarkerEnum.ROOT;

    @Override
    public String getId() {
        return "e";
    }

    @Override
    public String toTag() {
        return Arrays.asList(getId(),idAnotherEvent,recommendedRelayURL).toString();
    }


}
