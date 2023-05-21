package br.com.nostr.jnostr.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import br.com.nostr.jnostr.crypto.schnorr.Schnorr;
import br.com.nostr.jnostr.enums.TypeRealyEnum;
import br.com.nostr.jnostr.nip.ClientToRelay;
import br.com.nostr.jnostr.nip.Event;
import br.com.nostr.jnostr.nip.EventMessage;
import br.com.nostr.jnostr.nip.Filters;
import br.com.nostr.jnostr.nip.Message;
import br.com.nostr.jnostr.nip.ReqMessage;
import br.com.nostr.jnostr.tags.TagE;
import br.com.nostr.jnostr.util.NostrUtil;
import jakarta.validation.Valid;

public class RelayThread  {

    // private String[] relays;
    // private List<?> result;
    private CountDownLatch latch;
    private String subscriptionId;
    private ExecutorService executorService;
    private String[] relaysWrite;
    private String[] relaysRead;
    private String privateKey;
    ObjectMapper mapper = new ObjectMapper().setSerializationInclusion(Include.NON_NULL);
    

    public RelayThread(String privateKey, TypeRealyEnum realyEnun,String...relays) {
        this.privateKey = privateKey;
        if(realyEnun.equals(TypeRealyEnum.READ_WRITE)){
            this.relaysWrite = relays;
            this.relaysRead = relays;
            // this.relays = relays;
        }
        if(realyEnun.equals(TypeRealyEnum.READ_ONLY)){
            this.relaysRead = relays;
        }
        if(realyEnun.equals(TypeRealyEnum.WRITE_ONLY)){
            this.relaysWrite = relays;
        }
        this.executorService = Executors.newFixedThreadPool(relays.length);
    }

    // public WebSocket connectRelay(String relay){

    //     return (new WebSocketClient()).startSocket("wss://"+relay);
    // }

    @Valid
    protected Message createReqMessage(Filters filter) {

        ReqMessage message = new ReqMessage();
        message.setSubscriptionId(this.subscriptionId);
        message.setFilters(filter);

        return message;
    }

    public List<Event> list(Filters filter) {

        List<Callable<List<Event>>> callableTasks = new ArrayList<>();
        this.subscriptionId = UUID.randomUUID().toString();

        for (String relay : relaysRead) {
            Callable<List<Event>> callableTask = () -> {
                
                var listEvent = new ArrayList<Event>();
                latch = new CountDownLatch(1);
                var listener = new WebSocket.Listener() {
                    @Override
                    public CompletionStage<Void> onText(WebSocket webSocket, CharSequence data, boolean last) {
                        webSocket.request(1);
                        if(!data.toString().contains("EOSE") && data.toString().substring(data.toString().length() - 1).equals("]")  ){
                            var subString = data.toString().substring(data.toString().indexOf("{"), data.toString().length()-1);
                            try {
                                listEvent.add(mapper.readValue(subString, Event.class));
                            } catch (JsonProcessingException e) {
                                System.out.println("Ori: ---------------");
                                System.out.println(data.toString());
                                System.out.println("Sub: ---------------");
                                System.out.println(subString);
                                e.printStackTrace();
                            }
                        }
                        return CompletableFuture.completedFuture(data)
                                .thenAccept(o -> {/*System.out.println("Handling data: " + o);*/ if(o.toString().contains("EOSE") || o.toString().contains("NOTICE")){latch.countDown();}});
                    }
                };

                var uri = URI.create(relay.startsWith("ws")?relay:("wss://"+relay));
                var webSocket = HttpClient.newHttpClient().newWebSocketBuilder()
                .buildAsync(uri, listener)
                .get();

                ///////////////////////////////////////////////////////////////////////////
                var messages = new ClientToRelay();
                var message = createReqMessage(filter);
                ///////////////////////////////////////////////////////////////////////////
                messages.setMessages(Arrays.asList(message));
                var json = mapper.writeValueAsString(messages);
                System.out.println(json);
                webSocket.sendText(json, true);

                latch.await();
        
                return listEvent;
            };
            callableTasks.add(callableTask);
        }

        Set<Event> result = new HashSet<>();
        try {
            // Future<List<String>> future =   executorService.submit(callableTask);
            
        // result = future.get(200, TimeUnit.MILLISECONDS);

        // result = executorService.invokeAny(callableTasks);// bem sucedida

        List<Future<List<Event>>> futures = executorService.invokeAll(callableTasks);

        for (Future<List<Event>> future : futures) {
            for (Event event : future.get(200, TimeUnit.MILLISECONDS)) {
                result.add(event);
            }
        }

        executorService.shutdown();
       
        if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
            List<Runnable> notExecutedTasks = executorService.shutdownNow();
        } 
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            executorService.shutdownNow();
        }

        return  result.stream().toList();
    }

    public List<String> delete(String ...refIdEvent) {
        List<Callable<List<String>>> callableTasks = new ArrayList<>();

        for (String relay : relaysWrite) {
            Callable<List<String>> callableTask = () -> {
                var listEvent = new ArrayList<String>();
                latch = new CountDownLatch(1);

                var listener = new WebSocket.Listener() {
                    @Override
                    public CompletionStage<Void> onText(WebSocket webSocket, CharSequence data, boolean last) {
                        webSocket.request(1);
                        if(data.toString().contains("OK") ) {
                                listEvent.add(data.toString());
                        }
                        return CompletableFuture.completedFuture(data)
                                .thenAccept(o -> {System.out.println("Handling data: " + o); if(o.toString().contains("OK") || o.toString().contains("NOTICE")){latch.countDown();}});
                    }
                };

                var uri = URI.create(relay.startsWith("ws")?relay:("wss://"+relay));
                var webSocket = HttpClient.newHttpClient().newWebSocketBuilder()
                .buildAsync(uri, listener)
                .get();
                ///////////////////////////////////////////////////////////////////////////
                var messages = new ClientToRelay();
                var message = createEventMessage(refIdEvent);
                ///////////////////////////////////////////////////////////////////////////
                messages.setMessages(Arrays.asList(message));
                var json = mapper.writeValueAsString(messages);
                System.out.println(json);
                webSocket.sendText(json, true);

                return listEvent;
            };

            callableTasks.add(callableTask);
        }

        Set<String> result = new HashSet<>();
        try {

            List<Future<List<String>>> futures = executorService.invokeAll(callableTasks);

            for (Future<List<String>> future : futures) {
                for (String event : future.get(200, TimeUnit.MILLISECONDS)) {
                    result.add(event);
                }
            }

            executorService.shutdown();
        
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                List<Runnable> notExecutedTasks = executorService.shutdownNow();
            } 
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            executorService.shutdownNow();
        }

        return  result.stream().toList();

        // return null;
    }

    private Message createEventMessage(String ... refIdEvent) {

        EventMessage message = new EventMessage();
        
        Event event = new Event();
        event.setKind(5);
        event.setPubkey(NostrUtil.bigIntFromBytes(NostrUtil.genPubKey(privateKey)).toString(16));
        for (String idNoteEvent : refIdEvent) {
            event.setTags(Arrays.asList(TagE.builder().idAnotherEvent(idNoteEvent).build()));
        }
        event.setContent( "these posts were published by accident");

        event.setCreatedAt(Instant.now().getEpochSecond());
        event.setId(NostrUtil.bytesToHex(NostrUtil.sha256(event.serialize())));
        var signed = Schnorr.sign(NostrUtil.sha256(event.serialize()), NostrUtil.hexToBytes(privateKey), NostrUtil.createRandomByteArray(32));
        event.setSig(NostrUtil.bytesToHex(signed));
        message.setEvent(event);

        return message;

    }

    
}
