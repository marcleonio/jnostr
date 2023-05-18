package br.com.nostr.jnostr.jackson;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import br.com.nostr.jnostr.nip.ClientToRelay;
import br.com.nostr.jnostr.nip.EventMessage;
import br.com.nostr.jnostr.nip.Message;
import br.com.nostr.jnostr.nip.ReqMessage;

public class CustomClientSerializer extends JsonSerializer <ClientToRelay>{

    @Override
    public void serialize(ClientToRelay value, JsonGenerator gen, SerializerProvider serializers) throws IOException {

        var message = value.getMessages().iterator().next();
        // gen.writeStartObject();
        
        gen.writeStartArray();
        gen.writeString(message.getType().name());
        // gen.writeStringField("subscription_id", message.getSubscriptionId());
        //<pubkey, as a (lowercase) hex string>,

        Field[] tagBaseFields = message.getClass().getSuperclass().getDeclaredFields();
        Field[] tagFields = message.getClass().getDeclaredFields();
        
        for (Field field : tagFields){
           
            try {
                BeanUtils.getProperty(message,field.getName());
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if(message instanceof EventMessage){
            gen.writePOJO( ((EventMessage) message).getEvent());
        }
        if(message instanceof ReqMessage){
            gen.writePOJO( ((ReqMessage) message).getSubscriptionId());
            gen.writePOJO( ((ReqMessage) message).getFilters());
        }
        
        gen.writeEndArray();
        // gen.writeNumberField("itemNr", value.itemNr);
        // gen.writeNumberField("createdBy", value.user.id);
        // gen.writeEndObject();

        // gen.writeString(format);
        // gen.writeNumber(id);
        // gen.writeNull();

        // var list = value.getMessages().parallelStream().map(obj -> toJson(obj))
		// 			.collect(Collectors.toList());
			
		// 	gen.writePOJO(list);
    }

    private Object toJson(Message obj) {
        return null;
    }

}
