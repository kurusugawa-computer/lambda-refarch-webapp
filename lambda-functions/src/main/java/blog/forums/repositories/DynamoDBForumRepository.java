package blog.forums.repositories;

import blog.configuration.ApplicationConfiguration;
import blog.forums.models.Forum;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameOverride;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;

import java.util.List;

public class DynamoDBForumRepository {


    private DynamoDBMapper mapper;

    public DynamoDBForumRepository(ApplicationConfiguration configuration) {
        mapper = new DynamoDBMapper(new AmazonDynamoDBClient(),
                new DynamoDBMapperConfig(new TableNameOverride(configuration.getForumDynamoDBTableName())));
    }

    public List<Forum> findForums() {

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();

        List<Forum> forumList = mapper.scan(Forum.class, scanExpression);
        return forumList;
    }
}
