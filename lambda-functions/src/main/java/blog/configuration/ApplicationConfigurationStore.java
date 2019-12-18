package blog.configuration;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import java.util.HashMap;
import java.util.Map;

public class ApplicationConfigurationStore {

    private static DynamoDBMapper mapper = new DynamoDBMapper(new AmazonDynamoDBClient());
    private Map<String, ApplicationConfiguration> configurations;

    public ApplicationConfigurationStore() {
        configurations = new HashMap<String, ApplicationConfiguration>();
    }

    public ApplicationConfiguration getApplicationConfiguration(RequestConfiguration requestConfiguration) {
        String stageName = requestConfiguration.getStageName();

        if (configurations.get(stageName) == null) {
            configurations.put(stageName, getApplicationConfigurationFromDynamoDB(stageName));
        }
        return configurations.get(stageName);
    }

    private ApplicationConfiguration getApplicationConfigurationFromDynamoDB(String stageName) {
        return mapper.load(ApplicationConfiguration.class, stageName);
    }
}
