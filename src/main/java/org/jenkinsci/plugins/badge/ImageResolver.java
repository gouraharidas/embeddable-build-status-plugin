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

import hudson.model.BallColor;

public class ImageResolver {

    public Badge getBuildStatusImage(BallColor bc, String style) throws IOException {
		String subject = "build";
		String status = "unknown";
		String color = "lightgrey";

        if (bc.isAnimated()) {
            color = "blue";
            status = "running";
        } else {
            switch (bc) {
            case RED:
                color = "red";
                status = "failing";
                break;
            case YELLOW:
                color = "yellow";
                status = "unstable";
                break;
            case BLUE:
                color = "brightgreen";
                status = "passing";
                break;
            case ABORTED:
                color = "lightgrey";
                status = "aborted";
                break;
            default:
                color = "lightgrey";
                status = "unknown";
                break;
            }
        }

        return new Badge(subject, status, color, style);
    }

	public Badge getCoverageImage(int coverage, String style) throws IOException {
		String subject = "coverage";
		String status = String.valueOf(coverage) + "%";
		String color = findBadgeColor(coverage);
		return new Badge(subject, status, color, style);
	}

	public Badge getUnitTestImage(int passed, int failed, String style) throws IOException {
		String subject = "unit tests";
		String text = String.format("%d passed, %d failed", passed, failed);
		String color = findTestBadgeColor(passed, failed);
		return new StatusImage(subject, text, color);
	}

	private String findTestBadgeColor(int passed, int failed) {
		int percentage = 0;
		
		if (passed > 0 || failed > 0) {
			percentage = (int) (((double)passed / (passed + failed)) * 100);
		}

		String color = findBadgeColor(percentage);
		return new Badge(subject, text, color, style);
	}

	private static String findBadgeColor(int percentage) {
		String color = "lightgrey";
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
