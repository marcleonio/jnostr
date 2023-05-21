package br.com.nostr.jnostr.jackson;

import java.io.IOException;
import java.lang.reflect.Field;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;

import br.com.nostr.jnostr.tags.TagBase;
import br.com.nostr.jnostr.tags.TagE;
import br.com.nostr.jnostr.tags.TagP;
import br.com.nostr.jnostr.tags.TagSubject;

public class CustomBaseTagDeSerializer extends StdDeserializer<TagBase> {

    public CustomBaseTagDeSerializer() { 
        this(null); 
    } 

    protected CustomBaseTagDeSerializer(Class<?> vc) {
        super(vc);
        //TODO Auto-generated constructor stub
    }

    @Override
    public TagBase deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        ArrayNode arrNode = p.getCodec().readTree(p);

        TagBase result = null;
        if(arrNode.get(0).asText().equals("e")){
            var tagE = new TagE();
            for (int i = 0; i < arrNode.size(); i++) {
                if(i == 0){
                    tagE.setId(arrNode.get(i).asText());
                }
                if(i == 1){
                    tagE.setIdAnotherEvent(arrNode.get(i).asText());
                }
                if(i == 2){
                    tagE.setRecommendedRelayURL(arrNode.get(i).asText());
                }
            }
            result = tagE;
        }
        if(arrNode.get(0).asText().equals("e")){
            var tagP = new TagP();
            for (int i = 0; i < arrNode.size(); i++) {
                if(i == 0){
                    tagP.setId(arrNode.get(i).asText());
                }
                if(i == 1){
                    tagP.setPubkey(arrNode.get(i).asText());
                }
                if(i == 2){
                    tagP.setRecommendedRelayURL(arrNode.get(i).asText());
                }
            }
            result = tagP;
        }
        if(arrNode.get(0).asText().equals("subject")){
            var tagSubject = new TagSubject();
            for (int i = 0; i < arrNode.size(); i++) {
                if(i == 0){
                    tagSubject.setId(arrNode.get(i).asText());
                }
                if(i == 1){
                    tagSubject.setValue(arrNode.get(i).asText());
                }
            }
            result = tagSubject;
        }

        if(arrNode.get(0).asText().equals("imeta")){
            result = null;
        }


        // String itemName = node.elements() isArray() get().asText();

        return result;
    } 
    
}
