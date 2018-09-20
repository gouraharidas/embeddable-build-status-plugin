package org.jenkinsci.plugins.badge;

import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.recipes.PresetData;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.gargoylesoftware.htmlunit.WebResponse;

import hudson.model.FreeStyleProject;
import hudson.model.Run;
import hudson.model.queue.QueueTaskFuture;
import hudson.plugins.cobertura.CoberturaBuildAction;
import hudson.plugins.cobertura.targets.CoverageMetric;
import hudson.security.GlobalMatrixAuthorizationStrategy;
import hudson.security.SecurityRealm;


/**
 * Just the fun bits: check that actions register correctly
 */
public class WorkflowBadgeActionTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    private WorkflowJob job = null;

    @Mock
    private CoberturaBuildAction coverageAction;

    @Mock
    private Run<WorkflowJob, WorkflowRun> build;

    public void beforeEachTest() throws Exception {
    	MockitoAnnotations.initMocks(this);
    	FreeStyleProject project = j.createFreeStyleProject("freestyle");
    	project.scheduleBuild2(0);
    	while (project.isBuilding()) {
    		TimeUnit.SECONDS.sleep(2);
    	}

        System.out.println("project: " + project);
        System.out.println("last build: " + project.getLastBuild());

        // Mock for Code Coverage
    	/*when(job.getLastBuild()).thenReturn((WorkflowRun) run.get());
    	when(build.getAction(CoberturaBuildAction.class)).thenReturn(coverageAction);
    	when(
    		coverageAction.getResult().
    			getCoverage(CoverageMetric.LINE).
    			getPercentage()
    		).thenReturn(10);
        System.out.println("last build: " + job.getLastBuild());*/
	}

    @PresetData(PresetData.DataSet.NO_ANONYMOUS_READACCESS)
    @Test
    public void authenticatedAccess() throws Exception {
        job = j.getInstance().createProject(WorkflowJob.class, "wf");
        job.setDefinition(new CpsFlowDefinition("println('hello')"));

        JenkinsRule.WebClient wc = j.createWebClient();
        wc.login("alice", "alice");

        wc.goTo("build-status/icon?job=wf", "image/svg+xml");
        job.setQuietPeriod(0);
        job.scheduleBuild();
        j.waitUntilNoActivityUpTo(5000);
        wc.goTo("build-status/icon?job=wf&build=1", "image/svg+xml");
    }

    @Test
    public void anonymousViewStatusAccess() throws Exception {
        // Allows anonymous access at security realm level
        final SecurityRealm realm = j.createDummySecurityRealm();
        j.jenkins.setSecurityRealm(realm);
        GlobalMatrixAuthorizationStrategy auth = new GlobalMatrixAuthorizationStrategy();
        auth.add(PublicBuildStatusAction.VIEW_STATUS, "anonymous");
        j.getInstance().setSecurityRealm(realm);
        j.getInstance().setAuthorizationStrategy(auth);

        job = j.getInstance().createProject(WorkflowJob.class, "wf");
        job.setDefinition(new CpsFlowDefinition("println('hello')"));
        JenkinsRule.WebClient wc = j.createWebClient();
        wc.goTo("build-status/icon?job=wf&", "image/svg+xml");
    }

    @PresetData(PresetData.DataSet.ANONYMOUS_READONLY)
    @Test
    public void anonymousReadStatus() throws Exception {
        job = j.getInstance().createProject(WorkflowJob.class, "wf");
        job.setDefinition(new CpsFlowDefinition("println('hello')"));
        JenkinsRule.WebClient wc = j.createWebClient();
        WebResponse response = wc.goTo("build-status/icon?job=wf&", "image/svg+xml").getWebResponse();
        saveImage(response, "status-icon");
    }

    @PresetData(PresetData.DataSet.ANONYMOUS_READONLY)
    @Test
    public void anonymousReadCoverage() throws Exception {
        job = j.getInstance().createProject(WorkflowJob.class, "wf");
        job.setDefinition(new CpsFlowDefinition("println('hello')"));
        JenkinsRule.WebClient wc = j.createWebClient();
        WebResponse response = wc.goTo("coverage/icon?job=wf&", "image/svg+xml").getWebResponse();
        saveImage(response, "coverage-icon");
    }

    @PresetData(PresetData.DataSet.ANONYMOUS_READONLY)
    @Test
    public void anonymousReadUnitTestReport() throws Exception {
        job = j.getInstance().createProject(WorkflowJob.class, "wf");
        job.setDefinition(new CpsFlowDefinition("println('hello')"));
        JenkinsRule.WebClient wc = j.createWebClient();
        WebResponse response = wc.goTo("unit-test/icon?job=wf&", "image/svg+xml").getWebResponse();
        saveImage(response, "unit-icon");
    }

    private static void saveImage(WebResponse response, String name) throws Exception {
        InputStream is = null;
        OutputStream os = null;
        try {
        	is = response.getContentAsStream();
        	File f = new File("/home/goura/Downloads/badge/" + name + ".svg");
        	os = new FileOutputStream(f);
            byte[] buf = new byte[1024];
            int len;

            while ((len = is.read(buf)) > 0) {
                os.write(buf, 0, len);
            }
        } catch (IOException e) {
        	e.printStackTrace(System.err);
        } finally {
        	if (is != null) {
        		is.close();
        	}
        	if (os != null) {
        		os.close();
        	}
        }
    }
}
