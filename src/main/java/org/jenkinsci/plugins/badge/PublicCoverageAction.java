package org.jenkinsci.plugins.badge;

import java.io.IOException;

import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import hudson.Extension;
import hudson.model.Job;
import hudson.model.Run;
import hudson.model.UnprotectedRootAction;
import hudson.plugins.cobertura.CoberturaBuildAction;
import hudson.plugins.cobertura.targets.CoverageMetric;

/**
 * Exposes the coverage status badge via unprotected URL.
 * http://localhost:8080/coverage/icon?job=[JOBNAME]
 */
@Extension
public class PublicCoverageAction extends AbstractBadgeAction implements UnprotectedRootAction {
	private final ImageResolver iconResolver;

	private CoberturaBuildAction coberturaAction = null;

	public PublicCoverageAction() throws IOException {
		iconResolver = new ImageResolver();
	}

	@Override
	public String getUrlName() {
		return "coverage";
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
			@QueryParameter String style) throws IOException {
		Job<?, ?> project = getProject(job);
		Run<?, ?> lastBuild = project.getLastBuild();
        coberturaAction = lastBuild.getAction(CoberturaBuildAction.class);
        int coverage = coberturaAction.getResult().getCoverage(CoverageMetric.LINE).getPercentage();
		return iconResolver.getCoverageImage(coverage, style);
	}

}
