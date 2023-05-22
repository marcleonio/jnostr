package br.com.nostr.jnostr.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TypeMarkerEnum {
    ROOT,REPLY,MENTION;

    @JsonValue
    public String toValue() {
        return name().toLowerCase();
    }
}
