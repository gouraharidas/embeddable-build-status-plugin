/*
* The MIT License
*
* Copyright 2013 Kohsuke Kawaguchi, Dominik Bartholdi
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
*/
package org.jenkinsci.plugins.badge;

import java.io.IOException;
import java.util.HashMap;

import hudson.model.BallColor;

public class ImageResolver {

    private final HashMap<String, StatusImage[]> styles;
    private final StatusImage[] defaultStyle;

    public ImageResolver() throws IOException{
        styles = new HashMap<String, StatusImage[]>();
        // shields.io "plastic" style (aka the old default)
        StatusImage[] plasticImages = new StatusImage[] {
                new StatusImage("build-failing-red.svg"),
                new StatusImage("build-unstable-yellow.svg"),
                new StatusImage("build-passing-brightgreen.svg"),
                new StatusImage("build-running-blue.svg"),
                new StatusImage("build-aborted-lightgrey.svg"),
                new StatusImage("build-unknown-lightgrey.svg")
        };
        styles.put("plastic", plasticImages);
        // shields.io "flat" style (new default from Feb 1 2015)
        StatusImage[] flatImages = new StatusImage[] {
                new StatusImage("build-failing-red-flat.svg"),
                new StatusImage("build-unstable-yellow-flat.svg"),
                new StatusImage("build-passing-brightgreen-flat.svg"),
                new StatusImage("build-running-blue-flat.svg"),
                new StatusImage("build-aborted-lightgrey-flat.svg"),
                new StatusImage("build-unknown-lightgrey-flat.svg")
        };
        styles.put("flat", flatImages);
        // Pick a default style
        defaultStyle = flatImages;
        styles.put("default", defaultStyle);
    }

    public StatusImage getImage(BallColor color) {
        return getImage(color, "default");
    }

    public StatusImage getImage(BallColor color, String style) {
        StatusImage[] images = styles.get(style);
        if (images == null)
            images = defaultStyle;

        if (color.isAnimated())
            return images[3];

        switch (color) {
        case RED:
            return images[0];
        case YELLOW:
            return images[1];
        case BLUE:
            return images[2];
        case ABORTED:
            return images[4];
        default:
            return images[5];
        }
    }

	public StatusImage getCoverageImage(int coverage) throws IOException {
		String subject = "coverage";
		String status = String.valueOf(coverage) + "%";
		String color = "lightgrey";

		if (coverage > 90) {
			color = "brightgreen";
		} else if (coverage > 80) {
			color = "green";
		} else if (coverage > 70) {
			color = "yellowgreen";
		} else if (coverage > 60) {
			color = "yellow";
		} else if (coverage > 50) {
			color = "orange";
		} else if (coverage >= 0) {
			color = "red";
		} else {
			status = "unknown";
		}

		return new StatusImage(subject, status, color);
	}

	public StatusImage getUnitTestImage(int passed, int failed) throws IOException {
		String subject = "unit tests";
		String text = String.format("%d passed, %d failed", passed, failed);
		String color = findTestBadgeColor(passed, failed);
		return new StatusImage(subject, text, color);
	}

	private String findTestBadgeColor(int passed, int failed) {
		int percentage = 0;
		String color = "lightgrey";
		if (passed > 0 || failed > 0) {
			percentage = (int) (((double)passed / (passed + failed)) * 100);
		}

		if (percentage > 90) {
			color = "brightgreen";
		} else if (percentage > 80) {
			color = "green";
		} else if (percentage > 70) {
			color = "yellowgreen";
		} else if (percentage > 60) {
			color = "yellow";
		} else if (percentage > 50) {
			color = "orange";
		} else if (percentage >= 0) {
			color = "red";
		}
		return color;
	}
}
