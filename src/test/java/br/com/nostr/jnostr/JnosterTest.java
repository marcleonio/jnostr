package br.com.nostr.jnostr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.WebSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.nostr.jnostr.enums.TypeRealyEnum;
import br.com.nostr.jnostr.nip.ClientToRelay;
import br.com.nostr.jnostr.nip.Filters;
import br.com.nostr.jnostr.nip.Message;
import br.com.nostr.jnostr.nip.ReqMessage;
import br.com.nostr.jnostr.server.RelayInfo;
import br.com.nostr.jnostr.server.RelayToClient;
import br.com.nostr.jnostr.util.NostrUtil;
import jakarta.validation.Valid;
import static org.awaitility.Awaitility.await;

public class JnosterTest extends BaseTest {
    // private final StringPadderImpl stringPadder = new StringPadderImpl();

    private JNostr jnostr;

    @Before
    public void init() {
        jnostr = new JNostr(NostrUtil.toHex(NostrUtil.generatePrivateKey()));
        jnostr.initialize("relay.taxi");
    }

    @Test
    public void relayInfo() {
        var body = jnostr.relayInfo("relay.nostr.band");
        System.out.println(body);
        assertEquals(body.getSupportedNips().get(0), Integer.valueOf(1));

    }

    @Test
    public void nip01EVENT() throws JsonMappingException, JsonProcessingException {
        var data = jnostr.sendMessage(createEventMessage());

        ObjectMapper mapper = new ObjectMapper();
        List<?> list = mapper.readValue(data.toString(), List.class);

        assertEquals("OK", list.get(0));
        assertEquals("[\"OK\"", data.split(",")[0]);
    }

    @Test
    public void nip01REQ() {
        var data = jnostr.sendMessage(createReqMessage());

        assertEquals("[\"EOSE\"", data.split(",")[0]);
    }

    @Test
    public void nip01Close() {

        var data = jnostr.sendMessage(createReqMessage());

        jnostr.sendMessage(createCloseMessage());

        assertEquals("[\"EOSE\"", data.split(",")[0]);
    }

    @Test
    public void testeReqList() throws Exception {
        var echoed = new AtomicBoolean(false);
        var list = new ArrayList<String>();
        var listener = new WebSocket.Listener() {
            @Override
            public CompletionStage<Void> onText(WebSocket webSocket, CharSequence data, boolean last) {
                webSocket.request(1);
                list.add(data.toString());
                return CompletableFuture.completedFuture(data)
                        .thenAccept(o -> {System.out.println("Handling data: " + o); echoed.set(o.toString().contains("EOSE")); });
            }
        };

        var uri = URI.create("wss://relay.taxi");
        var webSocket = HttpClient.newHttpClient().newWebSocketBuilder()
                .buildAsync(uri, listener)
                .get();

                
        var messages = new ClientToRelay();
        var message = createReqMessage();
        messages.setMessages(Arrays.asList(message));

        ObjectMapper mapper = new ObjectMapper().setSerializationInclusion(Include.NON_NULL);
        //TODO verificar se é melhor com customJackson ou enviar em lista
        // List<String> jsonList = new ArrayList<>();
        // jsonList.add("\"" + messages.getMessages().get(0).getType().name() + "\"");
        // jsonList.add(mapper.writeValueAsString(messages.getMessages().get(0).getEvent()));
        // System.out.println(jsonList.toString());
        
        var json = mapper.writeValueAsString(messages);
            
        // webSocket.sendText("\"" + messages.getMessages().get(0).getType().name() + "\"", false);
        // webSocket.sendText(mapper.writeValueAsString(((ReqMessage)message).getSubscriptionId()), false);
        // webSocket.sendText(mapper.writeValueAsString(((ReqMessage)message).getFilters()), true);

        webSocket.sendText(json, true);

        await().untilTrue(echoed);

        assertEquals(11,list.size());
    }

    @Test
    public void testManyRelay() {
        // jnostr = new JNostr(NostrUtil.toHex(NostrUtil.generatePrivateKey()));
        var relays = jnostr.relayInit(TypeRealyEnum.READ_ONLY,"wss://relay.nostr.band","relay.taxi");//read_lony/write_only
        var events = relays.list(createFilter());

        assertFalse(events.isEmpty());
    }

    @Test
    public void listPubRelay() throws Exception {
        // Connect to one of the relays and then query for kind 3,10002 events
        // https://github.com/nostr-protocol/nips/blob/master/65.md

        // String json = "[\"EVENT\",{\"kind\": 10002, \"content\": \"\"}]";
        // var echoed = new AtomicBoolean(false);
        // var listener = new WebSocket.Listener() {
        //     @Override
        //     public CompletionStage<Void> onText(WebSocket webSocket, CharSequence data, boolean last) {
        //         webSocket.request(1);
        //         return CompletableFuture.completedFuture(data)
        //                 .thenAccept(o -> {System.out.println("Handling data: " + o); echoed.set(o.toString().contains("EOSE")); });
        //     }
        // };


        // var uri = URI.create("wss://relay.taxi");
        // var webSocket = HttpClient.newHttpClient().newWebSocketBuilder()
        //         .buildAsync(uri, listener)
        //         .get();

        // webSocket.sendText(json, true);

        // await().untilTrue(echoed);

        ObjectMapper mapper = new ObjectMapper();
        HttpResponse<String> response = null;
        try {

            HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI("https://nostr.watch/geo.json"))
            .GET()
            .build();

            response = HttpClient.newHttpClient()
            .send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            var list = mapper.readValue(response.body(), LinkedHashMap.class);

            List<String> relayList = new ArrayList<>();
            for (Object item : list.keySet()) {
                relayList.add(item.toString());
            }

            assertFalse(relayList.isEmpty());

        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
        
    }


    @Test
    public void nip02() {
        // petname
    }
}
