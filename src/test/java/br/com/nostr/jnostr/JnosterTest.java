package br.com.nostr.jnostr;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.nostr.jnostr.server.RelayToClient;
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
    public void nip01() throws JsonMappingException, JsonProcessingException {
        var data = jnostr.sendMessage(createEventMessage());

        
        ObjectMapper mapper = new ObjectMapper();
        List<?> list = mapper.readValue(data.toString(), List.class);
        
        assertEquals("OK",list.get(0));
        assertEquals("[\"OK\"",data.split(",")[0]);
    }

    @Test
    public void nip02() {
        //petname
    }
}
