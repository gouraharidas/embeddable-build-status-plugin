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

    private static final String COVERAGE_SUBJECT = "line coverage";
    private static final String UNIT_TEST_SUBJECT = "unit tests";
    private static final String KPI_SUBJECT = "kpi";
    private static final String UNAVAILABLE = "not available";
    private static final String AVAILABLE = "available";

    public static Badge getBuildStatusImage(BallColor bc, String style) throws IOException {
        String subject = "build";
        String status = "unknown";
        Color color = Color.lightgrey;

        if (bc.isAnimated()) {
            color = Color.blue;
            status = "running";
        } else {
            switch (bc) {
            case RED:
                color = Color.red;
                status = "failing";
                break;
            case YELLOW:
                color = Color.yellow;
                status = "unstable";
                break;
            case BLUE:
                color = Color.brightgreen;
                status = "passing";
                break;
            case ABORTED:
                color = Color.lightgrey;
                status = "aborted";
                break;
            default:
                color = Color.lightgrey;
                status = "unknown";
                break;
            }
        }

        return new Badge(subject, status, color, style);
    }

    public static Badge getCoverageImage(int coverage, String style) throws IOException {
        String status = String.valueOf(coverage) + "%";
        return new Badge(COVERAGE_SUBJECT, status, findBadgeColor(coverage), style);
    }

    public static Badge getCoverageImageUnavailable(String style) throws IOException {
        return new Badge(COVERAGE_SUBJECT, UNAVAILABLE, Color.lightgrey, style);
    }

    public static Badge getUnitTestImage(int passed, int failed, String style) throws IOException {
        String text = String.format("%d passed, %d failed", passed, failed);
        return new Badge(UNIT_TEST_SUBJECT, text, findTestBadgeColor(passed, failed), style);
    }

    public static Badge getUnitTestImageUnavailable(String style) throws IOException {
        return new Badge(UNIT_TEST_SUBJECT, UNAVAILABLE, Color.lightgrey, style);
    }

    public static Badge getKPIImageUnavailable(String style) throws IOException {
        return new Badge(KPI_SUBJECT, UNAVAILABLE, Color.lightgrey, style);
    }

    public static Badge getKPIImageAvailable(String style) throws IOException {
        return new Badge(KPI_SUBJECT, AVAILABLE, Color.green, style);
    }

    private static Color findTestBadgeColor(int passed, int failed) {
        int percentage = 0;

        if (passed > 0 || failed > 0) {
            percentage = (int) (((double)passed / (passed + failed)) * 100);
        }

        return findBadgeColor(percentage);
    }

    private static Color findBadgeColor(int percentage) {
        Color color = Color.lightgrey;
        if (percentage > 90) {
            color = Color.brightgreen;
        } else if (percentage > 80) {
            color = Color.green;
        } else if (percentage > 70) {
            color = Color.yellowgreen;
        } else if (percentage > 60) {
            color = Color.yellow;
        } else if (percentage > 50) {
            color = Color.orange;
        } else if (percentage >= 0) {
            color = Color.red;
        }
        return color;
    }
}
