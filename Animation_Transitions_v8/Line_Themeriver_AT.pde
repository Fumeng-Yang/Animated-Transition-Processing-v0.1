// line - stackedline - themeriver
public class Line_Themeriver_AT extends AtomicTransition { // AT is short for AtomicTransition
    public Line_Themeriver_AT(String s, String e, int d) {
        super(s, e, d);
        stepMax = table.getColumnCount() - 1 + 5; //table.getColumnCount() - 1;
        stepIndice = new int[stepMax + 1];
        for (int i = 0; i < stepIndice.length; i++) {
            stepIndice[i] = constantFrame;
        }
        stepBeforeFill = 2;
        stepAfterFill = 2;
        init();
    }

    // point disappear
    public void step0() {
            recomputeNSeg();
            drawLines(1);
            drawStackedAxis(0, 0);
            drawPoints(1 - stepIndex / float(stepIndexMax));
            nextFrame();
    }

    // first one get stacked
    public void step1() {
        recomputeNSeg();
        drawStackedAxis(stepIndex / float(stepIndexMax), 0);
        drawFirstLines(stepIndex / float(stepIndexMax));
        nextFrame();
    }

    public void step() {
        recomputeNSeg();
        drawStackedAxis(1, 0);
        drawFirstLines(1);
        fillLines(stepIndex / float(stepIndexMax));
        nextFrame();
    }

    public void step1001() {
        recomputeNSeg();
        drawStackedAxis(1, 0); 
        moveToCenter(stepIndex / float(stepIndexMax));
        nextFrame();
    }

    public void step1002() {
        recomputeNSeg();
        drawStackedAxis(1, 0);
        smoothyThread(stepIndex / float(stepIndexMax));
        nextFrame();
    }

    public void step1003() {
        recomputeNSeg();
        drawStackedAxis(1, 0);
        smoothyThread(1);
        drawLegend(stepIndex / float(stepIndexMax));
        nextFrame();
    }

    private void moveToCenter(float f) {
        int colNum = table.getColumnCount();
        float minNumber = 0;
        float allMax = getRowSumMax(table);

        for (int j = colNum - 1; j >= defaultCol; j--) {
            float[] nums = table.getFloatColumn(j);
            strokeWeight(1);
            stroke(lerpColor(colors[0], colors[1], float(j - 1) / colNum));
            fill(lerpColor(colors[0], colors[1], float(j - 1) / colNum));
            beginShape();

            for (int i = 0; i < n; i++) {
                float x1 = ((i + 0.5) * segment);
                float y1 = (getSubRowSum(table, i, defaultCol, j)) / allMax * (canvasHr);
                float margin_top_bottom = (allMax - getRowSum(table, i)) / allMax * canvasHr / 2.0;
                float curY = lerp(y1, y1 + margin_top_bottom, f);
                vertex(canvasXScale(x1), canvasYScale(curY));
            }

            for (int i = n - 1; i >= 0; i--) {
                float x1 = ((i + 0.5) * segment);
                float y1 = (getSubRowSum(table, i, defaultCol, j)) / allMax * (canvasHr);
                float margin_top_bottom = (allMax - getRowSum(table, i)) / allMax * canvasHr / 2.0;
                float curY = lerp(0, margin_top_bottom, f);
                vertex(canvasXScale(x1), canvasYScale(curY));
            }

            endShape(CLOSE);
        }
    }

    private void fillLines(float f) {   
        int colNum = table.getColumnCount();
        float minNumber = 0;
        float maxNumber = getColMax(table, defaultCol);
        float allMax = getRowSumMax(table);

        int realStep = (step - stepBeforeFill) > (colNum - 1) ? (colNum - 1) : (step - stepBeforeFill);
        for (int j = realStep; j >= defaultCol; j--) {
            float[] nums = table.getFloatColumn(j);
            strokeWeight(1);
            stroke(lerpColor(colors[0], colors[1], float(j - 1) / colNum));
            fill(lerpColor(colors[0], colors[1], float(j - 1) / colNum));
            beginShape();
            if (j != realStep) {
                for (int i = 0; i < n; i++) {
                    float x1 = ((i + 0.5) * segment);
                    float y1 = (getSubRowSum(table, i, defaultCol, j)) / allMax * (canvasHr);
                    vertex(canvasXScale(x1), canvasYScale(y1));
                }
                vertex(canvasXScale((n - 0.5) * segment), canvasYScale(0));
                vertex(canvasXScale(0.5 * segment), canvasYScale(0));
                endShape(CLOSE);
            } else {
                for (int i = 0; i < n; i++) {
                    float x1 = ((i + 0.5) * segment);
                    float y1 = (getSubRowSum(table, i, defaultCol, j)) / allMax * (canvasHr);
                    float preY = (getSubRowSum(table, i, defaultCol, j - 1)) / allMax * (canvasHr);
                    float curY = lerp(preY, y1, f);
                    vertex(canvasXScale(x1), canvasYScale(curY));
                }
                vertex(canvasXScale((n - 0.5) * segment), canvasYScale(0));
                vertex(canvasXScale(0.5 * segment), canvasYScale(0));
                endShape(CLOSE);
            }
        }
    }



    private void drawFirstLines(float f) {
        float minNumber = 0;
        float maxNumber = getColMax(table, defaultCol);
        float allMax = getRowSumMax(table);
        float[] nums = table.getFloatColumn(defaultCol);

        for (int i = 0; i < n; i++) {
            if (i != n - 1) {
                float rowRatio1 = nums[i] / allMax;
                float rowRatio2 = nums[i + 1] / allMax;
                float x1 = ((i + 0.5) * segment);
                float y1 = ((nums[i] - minNumber) / maxNumber * (canvasHr));
                float x2 = ((i + 1 + 0.5) * segment);
                float y2 = (nums[i + 1] - minNumber) / maxNumber * (canvasHr);

                float ty1 = y1 * rowRatio1;
                float ty2 = y2 * rowRatio2;

                float cy1 = lerp(y1, ty1, f);
                float cy2 = lerp(y2, ty2, f);

                strokeWeight(1);
                stroke(colors[0]);
                fill(colors[0]);
                line(canvasXScale(x1), canvasYScale(cy1), canvasXScale(x2), canvasYScale(cy2));
            }
        }
    }

    private void drawLines(float f) {
        float minNumber = 0; // getColMin(table, defaultCol);
        float maxNumber = getColMax(table, defaultCol);
        float[] nums = table.getFloatColumn(defaultCol);

        // cannot handle x = c
        for (int i = 0; i < n; i++) {
            if (i != n - 1) {
                float x1 = ((i + 0.5) * segment);
                float x2 = ((i + 1 + 0.5) * segment);
                float y1 = ((nums[i] - minNumber) / maxNumber * (canvasH - canvas_margin_bottom - canvas_margin_top));
                float y2 = ((nums[i + 1] - minNumber) / maxNumber * (canvasH - canvas_margin_bottom - canvas_margin_top));

                float k = (y1 - y2) / (x1 - x2);
                float b = y1 - (k * x1);

                float rx2 = x1 + f * segment;
                float ry2 = k * rx2 + b;

                fill(colors[0]);
                stroke(colors[0]);
                strokeWeight(1);
                line(canvasXScale(x1), canvasYScale(y1), canvasXScale(rx2), canvasYScale(ry2));
            }
        }
    }

    private void drawPoints(float f) {
        float minNumber = 0;
        float maxNumber = getColMax(table, defaultCol);
        float[] nums = table.getFloatColumn(defaultCol);

        for (int i = 0; i < n; i++) {
            float x1 = ((i + 0.5) * segment);
            float y1 = ((nums[i] - minNumber) / maxNumber * (canvasHr));

            noStroke();

            ellipseMode(CENTER);
            fill(colors[0], f * 255);
            ellipse(canvasXScale(x1), canvasYScale(y1), pointSize, pointSize);
        }
    }
}
