package blog.posts.models;

import blog.configuration.RequestConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FindPostsRequest {

    private RequestConfiguration config;
    private String forumId;

    public RequestConfiguration getConfig() {
        return config;
    }

    public void setConfig(RequestConfiguration config) {
        this.config = config;
    }

    public String getForumId() {
        return forumId;
    }

    public void setForumId(String forumId) {
        this.forumId = forumId;
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
