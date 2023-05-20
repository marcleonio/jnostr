package br.com.nostr.jnostr.nip;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Filters {

    List<String> ids;
    List<String> authors;
    List<Integer> kinds;
    @JsonProperty("#e")
    List<String> e;
    @JsonProperty("#p")
    List<String> p;
    Long since;
    Long until;
    Integer limit;

}
