package blog.posts.repositories;

import blog.configuration.ApplicationConfiguration;
import blog.posts.models.LatestPostByForum;
import blog.posts.models.Post;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameOverride;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.ConditionalOperator;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import org.apache.log4j.Logger;

import java.util.*;

public class DynamoDBPostRepository {

    private final static int DEFAULT_LIMIT = 200;
    private static final Logger log = Logger.getLogger(DynamoDBPostRepository.class);
    private static DynamoDBMapper postMapper;
    private static DynamoDBMapper latestPostMapper;

    public DynamoDBPostRepository(ApplicationConfiguration configuration) {
        postMapper = new DynamoDBMapper(new AmazonDynamoDBClient(),
                new DynamoDBMapperConfig(new TableNameOverride(configuration.getPostDynamoDBTableName())));
        latestPostMapper = new DynamoDBMapper(new AmazonDynamoDBClient(),
                new DynamoDBMapperConfig(new TableNameOverride(configuration.getLatestPostDynamoDBTableName())));
    }

    public Post findOne(String forumId, String id) {
        Post post = postMapper.load(Post.class, id);
        return post;
    }

    public Post findByUser(String userId) {
        return null;
    }

    public List<LatestPostByForum> findPosts(String forumId) {

        HashMap<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":v1", new AttributeValue().withS(forumId));

        DynamoDBQueryExpression<LatestPostByForum> queryExpression =
                new DynamoDBQueryExpression<LatestPostByForum>()
                        .withKeyConditionExpression("forumId = :v1")
                        .withExpressionAttributeValues(eav)
                        .withScanIndexForward(false)
                        .withLimit(DEFAULT_LIMIT);

        QueryResultPage<LatestPostByForum> onePageOfResults = latestPostMapper.queryPage(LatestPostByForum.class, queryExpression);
        List<LatestPostByForum> resultList = onePageOfResults.getResults();
        log.debug(resultList.size());

        return resultList;
    }

    public Post save(Post post) {
        UUID guid = UUID.randomUUID();
        post.setId(guid.toString());
        post.setCreatedAt(new Date().getTime());

        LatestPostByForum latestPost = new LatestPostByForum(post);

        DynamoDBSaveExpression saveExpression = new DynamoDBSaveExpression();

        Map<String, ExpectedAttributeValue> expectedAttributes = new
                HashMap<String, ExpectedAttributeValue>();
        expectedAttributes.put("forumId", new ExpectedAttributeValue(false));
        expectedAttributes.put("created_at", new ExpectedAttributeValue(false));
        saveExpression.setExpected(expectedAttributes);
        saveExpression.setConditionalOperator(ConditionalOperator.AND);

        try {
            log.debug("Saving to latestPost" + latestPost);
            latestPostMapper.save(latestPost, saveExpression);

            try {
                log.debug("Saving to post table" + post);
                postMapper.save(post);
                Post savedPost = postMapper.load(Post.class, post.getId());
                log.info("post saved: " + post.getId());
                return savedPost;
            } catch (Exception e) {
                //if the save to the post table fails for any reason, roll back the
                //save to the latestPostInForum table
                latestPostMapper.delete(latestPost);
                return null;
            }

        } catch (ConditionalCheckFailedException e) {
            //there was a collision on the timestamp, so recreate with a new timestamp and try ONCE more before failing
            post.setCreatedAt(new Date().getTime());
            latestPost.setCreatedAt(post.getCreatedAt());
            log.warn("Timestamp Collision occurred when saving Post.  Will re-create timestamp and attempt one more time");
            try {
                latestPostMapper.save(latestPost, saveExpression);

                try {
                    postMapper.save(post);
                    Post savedPost = postMapper.load(Post.class, post.getId());
                    log.info("post saved: " + post.getId());
                    return savedPost;
                } catch (Exception exc) {
                    //if the save to the post table fails for any reason, roll back the
                    //save to the latestPostInForum table
                    latestPostMapper.delete(latestPost);
                    return null;
                }

            } catch (ConditionalCheckFailedException failedCheckAgain) {
                //only retry once, then return null to indicate the failure
                log.error("Failed to post the message: " + post.toString() +
                        " because of timestamp collision");
                return null;
            }
        }

    }
}
