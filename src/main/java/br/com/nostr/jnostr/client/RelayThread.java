package br.com.nostr.jnostr.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
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

import br.com.nostr.jnostr.nip.ClientToRelay;
import br.com.nostr.jnostr.nip.Event;
import br.com.nostr.jnostr.nip.Filters;
import br.com.nostr.jnostr.nip.Message;
import br.com.nostr.jnostr.nip.ReqMessage;
import jakarta.validation.Valid;

public class RelayThread  {

    private String[] relays;
    // private List<?> result;
    private CountDownLatch latch;
    private String subscriptionId;
    private ExecutorService executorService;
    

    public RelayThread(String...relays) {
        this.relays = relays;
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
        ObjectMapper mapper = new ObjectMapper().setSerializationInclusion(Include.NON_NULL);

        List<Callable<List<Event>>> callableTasks = new ArrayList<>();
        this.subscriptionId = UUID.randomUUID().toString();

        for (String relay : relays) {
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

                var messages = new ClientToRelay();
                var message = createReqMessage(filter);
                messages.setMessages(Arrays.asList(message));

                var json = mapper.writeValueAsString(messages);

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

    
}
