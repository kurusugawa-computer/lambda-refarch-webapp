package blog.posts.handlers;

import blog.configuration.ApplicationConfiguration;
import blog.configuration.ApplicationConfigurationStore;
import blog.posts.models.FindPostsRequest;
import blog.posts.models.LatestPostByForum;
import blog.posts.repositories.DynamoDBPostRepository;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.List;

public class FindPosts implements RequestHandler<FindPostsRequest, List<LatestPostByForum>> {

    private ApplicationConfigurationStore applicationConfigurationStore = new ApplicationConfigurationStore();

    @Override
    public List<LatestPostByForum> handleRequest(FindPostsRequest request, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("request:\n" + request + "\n");
        logger.log("requestconfig: " + request.getConfig());
        ApplicationConfiguration applicationConfiguration = applicationConfigurationStore
                .getApplicationConfiguration(request.getConfig());
        logger.log("applicationConfiguration:\n" + applicationConfiguration + "\n");

        DynamoDBPostRepository postRepo = new DynamoDBPostRepository(applicationConfiguration);
        return postRepo.findPosts(request.getForumId());
    }
}
