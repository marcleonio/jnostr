package br.com.nostr.jnostr;

import java.util.Objects;

/**
 * Factory for creating instances of {@link JNostr}.
 */
public class JNostrFactory {
    
    private JNostrFactory() {}
    /**
     * Creates an instance of {@link JNostr}.
     *
     * @return the new instance
     */
    public static JNostr getInstance(String privateKey) {
        Objects.requireNonNull(privateKey, "null private key");
        return new JNostr(privateKey).initialize(privateKey);
    }

    /**
     * Creates an instance of {@link JNostr}.
     *
     * @return the new instance
     */
    public static JNostr getInstance(String privateKey, String ... relay) {
        Objects.requireNonNull(privateKey, "null private key");
        return new JNostr(privateKey).initialize(relay);
    }
}
