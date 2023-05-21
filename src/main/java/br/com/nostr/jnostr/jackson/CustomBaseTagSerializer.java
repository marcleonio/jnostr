package br.com.nostr.jnostr.jackson;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import br.com.nostr.jnostr.tags.TagBase;

public class CustomBaseTagSerializer extends JsonSerializer<TagBase>{

    @Override
    public void serialize(TagBase value, JsonGenerator gen, SerializerProvider serializers) throws IOException {

        List<String> list = new ArrayList<>(3);

        Field[] tagBaseFields = value.getClass().getSuperclass().getDeclaredFields();
        Field[] tagFields = value.getClass().getDeclaredFields();
        Field[] allFields = new Field[tagBaseFields.length + tagFields.length];
        Arrays.setAll(allFields, i -> 
        (i < tagBaseFields.length ? tagBaseFields[i] : tagFields[i - tagBaseFields.length]));

        for (Field field : allFields){
            try {
                var getValue = BeanUtils.getProperty(value,field.getName());
                if(getValue!=null){
                    list.add(getValue);
                }
            } catch (IllegalArgumentException | IllegalAccessException | SecurityException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        gen.writePOJO(list);
        
    }

}
