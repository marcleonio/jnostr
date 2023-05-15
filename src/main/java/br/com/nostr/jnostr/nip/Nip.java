package br.com.nostr.jnostr.nip;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import br.com.nostr.jnostr.tags.TagBase;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Nip {

    private String id;
    private String pubkey;
    @JsonProperty("created_at")
    private Long createdAt; 
    private Integer kind;
    @Size(max = 3)
    private List<TagBase> tags;
    private String content;
    private String sig;


    public String serialize() {
        ObjectMapper mapper = new ObjectMapper().setSerializationInclusion(Include.NON_NULL);
    	var arrayNode = JsonNodeFactory.instance.arrayNode();
    	
    	try {
	    	arrayNode.add(0);
	    	arrayNode.add(this.pubkey.toLowerCase());
	    	arrayNode.add(this.createdAt);
	    	arrayNode.add(this.kind);
			arrayNode.add(mapper.valueToTree(tags));
	    	arrayNode.add(this.content);
	    	
	    	return mapper.writeValueAsString(arrayNode);
    	} catch (JsonProcessingException e) {
            throw new RuntimeException(e);
    	}
    }

}
