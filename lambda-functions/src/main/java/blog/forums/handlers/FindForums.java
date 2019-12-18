package blog.forums.handlers;

import blog.configuration.ApplicationConfiguration;
import blog.configuration.ApplicationConfigurationStore;
import blog.forums.models.FindForumsRequest;
import blog.forums.models.Forum;
import blog.forums.repositories.DynamoDBForumRepository;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.List;


public class FindForums implements RequestHandler<FindForumsRequest, List<Forum>> {

    private ApplicationConfigurationStore applicationConfigurationStore = new ApplicationConfigurationStore();

    @Override
    public List<Forum> handleRequest(FindForumsRequest request, Context context) {
        LambdaLogger logger = context.getLogger();

        logger.log("request:\n" + request + "\n");
        logger.log("requestconfig: " + request);
        ApplicationConfiguration applicationConfiguration = applicationConfigurationStore
                .getApplicationConfiguration(request.getConfig());
        logger.log("applicationConfiguration:\n" + applicationConfiguration + "\n");

        DynamoDBForumRepository forumRepo = new DynamoDBForumRepository(applicationConfiguration);
        return forumRepo.findForums();
    }
}
