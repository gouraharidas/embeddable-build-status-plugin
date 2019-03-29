package org.jenkinsci.plugins.badge;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import hudson.Extension;
import hudson.model.Job;
import hudson.model.Run;
import hudson.model.UnprotectedRootAction;

/**
 * Exposes the kpi status badge via unprotected URL.
 * http://localhost:8080/kpi/icon?job=[JOBNAME]
 */
@Extension
public class PublicKPIStatusAction extends AbstractBadgeAction implements UnprotectedRootAction {
    private static String KPI_FILE = "kpi.details.md";

    @Override
    public String getUrlName() {
        return "kpi";
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
        Boolean kpi_found = Boolean.FALSE;
        if (null != lastBuild) {
            try {
                boolean recursive = true;
                Collection files = FileUtils.listFiles(lastBuild.getRootDir(), null, recursive);

                for (Iterator iterator = files.iterator(); iterator.hasNext();) {
                    File file = (File) iterator.next();
                    if (KPI_FILE.equals(file.getName())) {
                        kpi_found = true;
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return kpi_found ? ImageResolver.getKPIImageAvailable(style)
                : ImageResolver.getKPIImageUnavailable(style);
    }

}
