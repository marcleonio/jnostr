package br.com.nostr.jnostr.jackson;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import br.com.nostr.jnostr.server.RelayToClient;

public class CustomServerSerializer extends JsonSerializer <RelayToClient>{

    @Override
    public void serialize(RelayToClient value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        var message = value.getMessages().iterator().next();

        gen.writeStartArray();
        gen.writeString(message.getType().name());

        Field[] tagFields = message.getClass().getDeclaredFields();
        
        for (Field field : tagFields){
           
            try {
                gen.writePOJO( BeanUtils.getProperty(message,field.getName()));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        gen.writeEndArray();
    }
    
}
