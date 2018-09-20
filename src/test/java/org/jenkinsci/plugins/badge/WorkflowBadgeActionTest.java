package org.jenkinsci.plugins.badge;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.recipes.PresetData;

import com.gargoylesoftware.htmlunit.WebResponse;

import hudson.security.GlobalMatrixAuthorizationStrategy;
import hudson.security.SecurityRealm;


/**
 * Just the fun bits: check that actions register correctly
 */
public class WorkflowBadgeActionTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @PresetData(PresetData.DataSet.NO_ANONYMOUS_READACCESS)
    @Test
    @Ignore
    public void authenticatedAccess() throws Exception {
        WorkflowJob job = j.getInstance().createProject(WorkflowJob.class, "wf");
        job.setDefinition(new CpsFlowDefinition("println('hello')"));

        JenkinsRule.WebClient wc = j.createWebClient();
        wc.login("alice", "alice");

        wc.goTo("buildStatus/icon?job=wf", "image/svg+xml");
        job.setQuietPeriod(0);
        job.scheduleBuild();
        j.waitUntilNoActivityUpTo(5000);
        wc.goTo("buildStatus/icon?job=wf&build=1", "image/svg+xml");
    }

    @Test
    @Ignore
    public void anonymousViewStatusAccess() throws Exception {
        // Allows anonymous access at security realm level
        final SecurityRealm realm = j.createDummySecurityRealm();
        j.jenkins.setSecurityRealm(realm);
        GlobalMatrixAuthorizationStrategy auth = new GlobalMatrixAuthorizationStrategy();
        auth.add(PublicBadgeAction.VIEW_STATUS, "anonymous");
        j.getInstance().setSecurityRealm(realm);
        j.getInstance().setAuthorizationStrategy(auth);

        WorkflowJob job = j.getInstance().createProject(WorkflowJob.class, "wf");
        job.setDefinition(new CpsFlowDefinition("println('hello')"));
        JenkinsRule.WebClient wc = j.createWebClient();
        wc.goTo("buildStatus/icon?job=wf&", "image/svg+xml");
    }

    @PresetData(PresetData.DataSet.ANONYMOUS_READONLY)
    @Test
    public void anonymousReadStatus() throws Exception {
        WorkflowJob job = j.getInstance().createProject(WorkflowJob.class, "wf");
        job.setDefinition(new CpsFlowDefinition("println('hello')"));
        JenkinsRule.WebClient wc = j.createWebClient();
        WebResponse response = wc.goTo("buildStatus/icon?job=wf&", "image/svg+xml").getWebResponse();
        saveImage(response, "status-icon");
    }

    @PresetData(PresetData.DataSet.ANONYMOUS_READONLY)
    @Test
    public void anonymousReadCoverage() throws Exception {
        WorkflowJob job = j.getInstance().createProject(WorkflowJob.class, "wf");
        job.setDefinition(new CpsFlowDefinition("println('hello')"));
        JenkinsRule.WebClient wc = j.createWebClient();
        WebResponse response = wc.goTo("coverage/icon?job=wf&", "image/svg+xml").getWebResponse();
        saveImage(response, "coverage-icon");
    }

    @PresetData(PresetData.DataSet.ANONYMOUS_READONLY)
    @Test
    public void anonymousReadUnitTestReport() throws Exception {
        WorkflowJob job = j.getInstance().createProject(WorkflowJob.class, "wf");
        job.setDefinition(new CpsFlowDefinition("println('hello')"));
        JenkinsRule.WebClient wc = j.createWebClient();
        WebResponse response = wc.goTo("unit-test/icon?job=wf&", "image/svg+xml").getWebResponse();
        saveImage(response, "unit-icon");
    }

    private void saveImage(WebResponse response, String name) throws Exception {
        InputStream is = null;
        OutputStream os = null;
        try {
        	is = response.getContentAsStream();
        	File f = new File("/home/goura/Downloads/" + name + ".svg");
        	os = new FileOutputStream(f);
            byte[] buf = new byte[1024];
            int len;

            while ((len = is.read(buf)) > 0) {
                os.write(buf, 0, len);
            }
        } catch (IOException e) {
        	e.printStackTrace(System.err);
        } finally {
        	if (is == null) {
        		is.close();
        	}
        	if (os == null) {
        		os.close();
        	}
        }
    }
}
