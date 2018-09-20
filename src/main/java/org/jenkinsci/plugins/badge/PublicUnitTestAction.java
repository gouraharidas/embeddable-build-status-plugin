package org.jenkinsci.plugins.badge;

import java.awt.FontFormatException;
import java.io.IOException;

import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import hudson.Extension;
import hudson.model.Job;
import hudson.model.Run;
import hudson.model.UnprotectedRootAction;
import hudson.tasks.junit.TestResultAction;

/**
 * Exposes the coverage status badge via unprotected URL.
 * http://localhost:8080/unit-test/icon?job=[JOBNAME]
 */
@Extension
public class PublicUnitTestAction extends AbstractBadgeAction implements UnprotectedRootAction {
	private final ImageResolver iconResolver;
	private TestResultAction testAction;

	public PublicUnitTestAction() throws IOException {
		iconResolver = new ImageResolver();
	}

	@Override
	public String getUrlName() {
		return "unit-test";
	}

	@Override
	public String getIconFileName() {
		return null;
	}

	@Override
	public String getDisplayName() {
		return null;
	}

	public HttpResponse doIcon(StaplerRequest req, StaplerResponse rsp, @QueryParameter String job,
			@QueryParameter String style) throws IOException, FontFormatException {
		Job<?, ?> project = getProject(job);
		Run<?, ?> lastBuild = project.getLastBuild();
		testAction = lastBuild.getAction(TestResultAction.class);
		
		int total = testAction.getTotalCount();
		int failed = testAction.getFailCount();
		int skipped = testAction.getSkipCount();
		int passed = total - (failed + skipped);
		return iconResolver.getXUnitImage(passed, failed);
	}

}
