package org.jenkinsci.plugins.badge;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import hudson.model.BallColor;


/**
 * Just the fun bits: check that actions register correctly
 */
public class IconResolverTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    public ImageResolver resolver = new ImageResolver();

    @Before
    public void beforeTest() throws Exception {
        WorkflowJob job = j.getInstance().createProject(WorkflowJob.class, "wf");
        job.setDefinition(new CpsFlowDefinition("println('hello')"));
	}

    @Test
    public void testBuildStatusRunningFlat() throws Exception {
    	Badge badge = resolver.getBuildStatusImage(BallColor.BLUE, "flat");
    	saveImage(badge, "status-running-flat");
    }

    @Test
    public void testBuildStatusRunning3D() throws Exception {
    	Badge badge = resolver.getBuildStatusImage(BallColor.BLUE, null);
    	saveImage(badge, "status-running-3d");
    }

    @Test
    public void testBuildStatusUnstableFlat() throws Exception {
    	Badge badge = resolver.getBuildStatusImage(BallColor.YELLOW, "flat");
    	saveImage(badge, "status-unstable-flat");
    }

    @Test
    public void testBuildStatusUnstable3D() throws Exception {
    	Badge badge = resolver.getBuildStatusImage(BallColor.YELLOW, null);
    	saveImage(badge, "status-unstable-3d");
    }

    @Test
    public void testBuildStatusFailingFlat() throws Exception {
    	Badge badge = resolver.getBuildStatusImage(BallColor.RED, "flat");
    	saveImage(badge, "status-failing-flat");
    }

    @Test
    public void testBuildStatusFailing3D() throws Exception {
    	Badge badge = resolver.getBuildStatusImage(BallColor.RED, null);
    	saveImage(badge, "status-failing-3d");
    }

    @Test
    public void testBuildStatusAbortedFlat() throws Exception {
    	Badge badge = resolver.getBuildStatusImage(BallColor.ABORTED, "flat");
    	saveImage(badge, "status-aborted-flat");
    }

    @Test
    public void testBuildStatusAborted3D() throws Exception {
    	Badge badge = resolver.getBuildStatusImage(BallColor.ABORTED, null);
    	saveImage(badge, "status-aborted-3d");
    }

    @Test
    public void testBuildStatusAnimatedFlat() throws Exception {
    	Badge badge = resolver.getBuildStatusImage(BallColor.ABORTED_ANIME, "flat");
    	saveImage(badge, "status-animated-flat");
    }

    @Test
    public void testBuildStatusAnimated3D() throws Exception {
    	Badge badge = resolver.getBuildStatusImage(BallColor.ABORTED_ANIME, null);
    	saveImage(badge, "status-animated-3d");
    }

    @Test
    public void testBuildStatusDisabledFlat() throws Exception {
    	Badge badge = resolver.getBuildStatusImage(BallColor.DISABLED, "flat");
    	saveImage(badge, "status-disabled-flat");
    }

    @Test
    public void testBuildStatusDisabled3D() throws Exception {
    	Badge badge = resolver.getBuildStatusImage(BallColor.DISABLED, null);
    	saveImage(badge, "status-disabled-3d");
    }

    @Test
    public void testCogerateFlat() throws Exception {
    	Badge badge = resolver.getCoverageImage(random(1, 100), "flat");
    	saveImage(badge, "coverage-flat");
    }

    @Test
    public void testCogerate3D() throws Exception {
    	Badge badge = resolver.getCoverageImage(random(1, 100), null);
    	saveImage(badge, "coverage-3d");
    }

    @Test
    public void testUnitTestFlat() throws Exception {
    	Badge badge = resolver.getUnitTestImage(random(10, 20), random(60, 70), "flat");
    	saveImage(badge, "unit-test-failure-flat");
    }

    @Test
    public void testUnitTest3D() throws Exception {
    	Badge badge = resolver.getUnitTestImage(random(300, 350), random(2, 3), null);
    	saveImage(badge, "unit-test-success-3d");
    }

    private static void saveImage(Badge badge, String name) throws Exception {
        InputStream is = null;
        OutputStream os = null;
        try {
        	File f = new File("/home/goura/Downloads/badge/" + name + ".svg");
        	os = new FileOutputStream(f);
            os.write(badge.getPayload());
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

    private static int random(int start, int end) {
        return new Random().nextInt(end - start) + start;
    }
}
