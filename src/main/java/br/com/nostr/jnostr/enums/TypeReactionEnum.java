package br.com.nostr.jnostr.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TypeReactionEnum {
    LIKE("+"),DISLIKE("+");

    private String value;

    @JsonValue
    public String toValue() {
        return value;
    }
}
