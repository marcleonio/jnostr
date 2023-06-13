package br.com.nostr.jnostr.nip;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Respost {

    @Setter(AccessLevel.NONE)
    @Builder.Default
    private Integer kind = 6;
    @NotBlank
    private String idNoteOriginal;
    @NotBlank
    private String relayOrigin;
    @NotBlank
    private String pubkey;

}
