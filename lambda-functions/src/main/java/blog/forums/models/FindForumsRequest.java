package blog.forums.models;

import blog.configuration.RequestConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FindForumsRequest {

    private RequestConfiguration config;

    public RequestConfiguration getConfig() {
        return config;
    }

    public void setConfig(RequestConfiguration config) {
        this.config = config;
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "error";
        }
    }

}
