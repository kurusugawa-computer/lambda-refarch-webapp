package blog.comments.models;

import blog.configuration.RequestConfiguration;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@DynamoDBTable(tableName = "REPLACED_BY_API")
public class Comment {

    private String postId;
    private long createdAt;
    private String message;
    private String email;
    private RequestConfiguration requestConfiguration;

    @DynamoDBHashKey(attributeName = "postId")
    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    @DynamoDBRangeKey(attributeName = "created_at")
    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @DynamoDBAttribute
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @DynamoDBAttribute
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @DynamoDBIgnore
    @JsonIgnore
    public RequestConfiguration getRequestConfiguration() {
        return requestConfiguration;
    }

    @JsonSetter("config")
    public void setRequestConfiguration(RequestConfiguration requestConfiguration) {
        this.requestConfiguration = requestConfiguration;
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
