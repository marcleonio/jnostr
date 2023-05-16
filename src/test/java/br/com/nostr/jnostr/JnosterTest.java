package br.com.nostr.jnostr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import br.com.nostr.jnostr.util.NostrUtil;

public class JnosterTest extends BaseTest{
    // private final StringPadderImpl stringPadder = new StringPadderImpl();

    private String jsonNIP01;

    private JNostr jnostr;

    @Before
    public void init(){
        jnostr = new JNostr(NostrUtil.toHex(NostrUtil.generatePrivateKey()));
        jnostr.initialize("relay.taxi");
    }

    @Test
    public void relayInfo() {
        var body = jnostr.relayInfo("relay.taxi");

        assertEquals(body.getSupportedNips().get(0), Integer.valueOf(1));
    
    }
  
    @Test
    public void nip01() {
        jsonNIP01 = createNIP01();
        jnostr.sendMessage("relay.taxi", jsonNIP01);

        jnostr.sendMessage("Hello world, I'm here on JNostr API!");

        assertNotNull(jsonNIP01);

    }

    @Test
    public void nip02() {
        //petname
    }
}
