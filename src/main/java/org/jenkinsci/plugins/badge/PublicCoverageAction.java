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
		Boolean available = Boolean.TRUE;
		int coverage = 0;
		if (null != lastBuild) {
			CoberturaBuildAction coberturaAction = lastBuild.getAction(CoberturaBuildAction.class);
			if (null != coberturaAction) {
				coverage = coberturaAction.getResult().getCoverage(CoverageMetric.LINE).getPercentage();
			} else {
				available = Boolean.FALSE;
			}
		} else {
			available = Boolean.FALSE;
		}
		return available? ImageResolver.getCoverageImage(coverage, style) : ImageResolver.getCoverageImageUnavailable(style);
	}

}
