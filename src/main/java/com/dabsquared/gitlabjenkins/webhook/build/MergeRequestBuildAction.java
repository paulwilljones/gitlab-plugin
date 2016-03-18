package com.dabsquared.gitlabjenkins.webhook.build;

import com.dabsquared.gitlabjenkins.GitLabPushTrigger;
import com.dabsquared.gitlabjenkins.model.MergeRequestHook;
import com.dabsquared.gitlabjenkins.util.GsonUtil;
import com.dabsquared.gitlabjenkins.webhook.WebHookAction;
import hudson.model.AbstractProject;
import hudson.security.ACL;
import hudson.util.HttpResponses;
import org.kohsuke.stapler.StaplerResponse;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.dabsquared.gitlabjenkins.util.GsonUtil.toPrettyPrint;

/**
 * @author Robin Müller
 */
public class MergeRequestBuildAction implements WebHookAction {

    private final static Logger LOGGER = Logger.getLogger(MergeRequestBuildAction.class.getName());
    private AbstractProject<?, ?> project;
    private MergeRequestHook mergeRequestHook;

    public MergeRequestBuildAction(AbstractProject<?, ?> project, String json) {
        LOGGER.log(Level.FINE, "MergeRequest: {0}", toPrettyPrint(json));
        this.project = project;
        this.mergeRequestHook = GsonUtil.getGson().fromJson(json, MergeRequestHook.class);
    }

    public void execute(StaplerResponse response) {
        ACL.impersonate(ACL.SYSTEM, new Runnable() {
            public void run() {
                GitLabPushTrigger trigger = project.getTrigger(GitLabPushTrigger.class);
                if (trigger != null) {
                    trigger.onPost(mergeRequestHook);
                }
            }
        });
        throw HttpResponses.ok();
    }
}