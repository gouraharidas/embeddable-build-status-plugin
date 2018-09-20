package org.jenkinsci.plugins.badge;

import hudson.util.IOUtils;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;

import java.awt.Canvas;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static javax.servlet.http.HttpServletResponse.*;

class Badge implements HttpResponse {
    private final byte[] payload;
    private static final String PLGIN_NAME = "embeddable-build-status";

    /**
     * To improve the caching, compute unique ETag.
     *
     * This needs to differentiate different image types, and possible future image changes
     * in newer versions of this plugin.
     */
    private final String etag;

    private final String length;

    private final Map<String, String> colors = new HashMap<String, String>() {
        private static final long serialVersionUID = 1L;

        {
            put( "red", "#e05d44" );
            put( "brightgreen", "#44cc11" );
            put( "green", "#97CA00" );
            put( "yellowgreen", "#a4a61d" );
            put( "yellow", "#dfb317" );
            put( "orange", "#fe7d37" );
            put( "lightgrey", "#9f9f9f" );
            put( "blue", "#007ec6" );
        };
    };

    Badge(String fileName) throws IOException {
        etag = '"' + Jenkins.RESOURCE_PATH + '/' + fileName + '"';

        URL image = new URL(
            Jenkins.getInstance().pluginManager.getPlugin(PLGIN_NAME).baseResourceURL,
            "status/"+fileName);
        InputStream s = image.openStream();
        try {
            payload = IOUtils.toByteArray(s);
        } finally {
            IOUtils.closeQuietly(s);
        }
        length = Integer.toString(payload.length);
    }

	Badge(String subject, String status, String colorName) throws IOException {
		this(subject, status, colorName, null);
	}

	Badge(String subject, String status, String colorName, String style) throws IOException {
		etag = Jenkins.RESOURCE_PATH + '/' + subject + status + colorName;

		if (style == null) {
			style = "default";
		}

		URL image = new URL(Jenkins.getInstance().pluginManager.getPlugin(PLGIN_NAME).baseResourceURL,
				"status/" + style + ".svg");
		InputStream s = image.openStream();

		double[] widths = { measureText(subject) + 10, measureText(status) + 10 };

		String color = colors.get(colorName);

		if (color == null) {
			color = '#' + colorName;
		}

		String fullwidth = String.valueOf(widths[0] + widths[1]);
		String blockPos = String.valueOf(widths[0]);
		String blockWidth = String.valueOf(widths[1]);
		String subjectPos = String.valueOf((widths[0] / 2) + 1);
		String statusPos = String.valueOf(widths[0] + (widths[1] / 2) - 1);

		try {
			payload = IOUtils.toString(s, "utf-8").replace("{{fullwidth}}", fullwidth).replace("{{blockPos}}", blockPos)
					.replace("{{blockWidth}}", blockWidth).replace("{{subjectPos}}", subjectPos)
					.replace("{{statusPos}}", statusPos).replace("{{subject}}", subject).replace("{{status}}", status)
					.replace("{{color}}", color).getBytes();
		} finally {
			IOUtils.closeQuietly(s);
		}

		length = Integer.toString(payload.length);
	}

	public int measureText(String text) throws IOException {
		URL fontURL = new URL(Jenkins.getInstance().pluginManager.getPlugin(PLGIN_NAME).baseResourceURL,
				"fonts/verdana.ttf");
		InputStream fontStream = fontURL.openStream();
		Font defaultFont = null;
		try {
			defaultFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
		} catch (FontFormatException e) {
			throw new IOException(e.getMessage());
		}
		defaultFont = defaultFont.deriveFont(11f);
		Canvas canvas = new Canvas();
		FontMetrics fontMetrics = canvas.getFontMetrics(defaultFont);
		return fontMetrics.stringWidth(text);
	}

    public void generateResponse(StaplerRequest req, StaplerResponse rsp, Object node) throws IOException, ServletException {
        String v = req.getHeader("If-None-Match");
        if (etag.equals(v)) {
            rsp.setStatus(SC_NOT_MODIFIED);
            return;
        }

        rsp.setHeader("ETag",etag);
        rsp.setHeader("Expires","Fri, 01 Jan 1984 00:00:00 GMT");
        rsp.setHeader("Cache-Control", "no-cache, private");
        rsp.setHeader("Content-Type", "image/svg+xml;charset=utf-8");
        rsp.setHeader("Content-Length", length);
        rsp.getOutputStream().write(payload);
    }

    public byte[] getPayload() {
    	return this.payload;
    }
}
