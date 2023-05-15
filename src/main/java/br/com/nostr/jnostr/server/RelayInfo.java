package br.com.nostr.jnostr.server;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelayInfo {
    private String name;
    private String description;
    private String pubkey;
    private String contact;
    @JsonProperty("supported_nips")
    private List<Integer> supportedNips;
    private String software;
    private String version;
}
