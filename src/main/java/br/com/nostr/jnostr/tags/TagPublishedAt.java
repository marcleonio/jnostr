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
public class TagPublishedAt extends TagBase {
    
    private String value;

    @Override
    public String getId() {
        return "published_at";
    }

    @Override
    public String toTag() {
        return Arrays.asList(getId(),value).toString();
    }


}
