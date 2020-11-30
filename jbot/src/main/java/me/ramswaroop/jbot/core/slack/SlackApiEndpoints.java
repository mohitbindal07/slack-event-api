package me.ramswaroop.jbot.core.slack;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SlackApiEndpoints {

    /**
     * Endpoint for Slack Api
     */
    @Value("${slackApi}")
    private String slackApi;

    /**
     * @return endpoint for RTM.connect()
     */
    public String getRtmConnectApi() {
        return slackApi + "/rtm.connect?token=xoxb-1507109434786-1523963379766-afPTzPbkhrJ80MNgBjPQCFKB";
    	// return slackApi + "/rtm.connect";
    }

    public String getImListApi() {
        return slackApi + "/conversations.list?token={xoxb-1507109434786-1523963379766-afPTzPbkhrJ80MNgBjPQCFKB}&limit={limit}&cursor={cursor}&types=im";
    }
}
