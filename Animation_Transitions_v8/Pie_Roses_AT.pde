
// pie - donut(single) - rose(multi)
public class Pie_Roses_AT extends AtomicTransition { // AT is short for AtomicTransition
    public Pie_Roses_AT(String s, String e, int d) {
        super(s, e, d);
        stepMax = 7 + table.getColumnCount() - 1;
        stepIndice = new int[stepMax + 1];
        for (int i = 0; i < stepIndice.length; i++) {
            stepIndice[i] = constantFrame;
        }
        stepBeforeFill = 6;
        stepAfterFill = 1;
        init();
    }

   public void step0() {
            recomputeNSeg();
            drawPlXLabelPerc(1 - float(stepIndex) / stepIndexMax);
            drawArc(0);
            nextFrame();
        }

    // adjust radius

    public void step1() {
            recomputeNSeg();
            drawArc(float(stepIndex) / stepIndexMax);
            nextFrame();
        }
        // adj arc
    public void step2() {
            recomputeNSeg();
            adjArc(float(stepIndex) / stepIndexMax);
            nextFrame();
        }
        // draw axis
    public void step3() {
            recomputeNSeg();
            adjArc(1);
            drawPlAxis(float(stepIndex) / stepIndexMax);
            nextFrame();
        }
        // adj color
    public void step4() {
        recomputeNSeg();
        adjColor(float(stepIndex) / stepIndexMax);
        drawPlAxis(1);
        nextFrame();
    }

    public void step5() {
        recomputeNSeg();
        stackFirstArc(float(stepIndex) / stepIndexMax);
        drawPlAxis(1);
        nextFrame();
    }

    public void step() {
        recomputeNSeg();
        stackArcs(float(stepIndex) / stepIndexMax);
        stackFirstArc(1);
        drawPlAxis(1);
        nextFrame();
    }

    public void step1001() {
        recomputeNSeg();
        stackArcs(1);
        //stackFirstArc(1);
        drawPlAxis(1);
        drawPlXLabelUniform(float(stepIndex) / stepIndexMax);
        drawStackedYPldAxis(float(stepIndex) / stepIndexMax);
        drawLegend(float(stepIndex) / stepIndexMax);
        nextFrame();
    }

    private void drawArc(float f) {
        float minNumber = 0; // getColMin(table, defaultCol);
        float maxNumber = getColMax(table, defaultCol);
        float sum = getColSum(table, defaultCol);
        float curSum = 0;
        float[] nums = table.getFloatColumn(defaultCol);

        for (int i = 0; i < n; i++) {
            pushStyle();

            float targetW = canvasHr / 2.0;
            float curW = lerp(1, nums[i] / maxNumber, f) * targetW + 2;
            strokeWeight(curW);

            noFill();

            stroke(lerpColor(colors[0], colors[1], float(i) / n));
            strokeCap(SQUARE);
            ellipseMode(RADIUS);

            float curRadius = lerp(1, nums[i] / maxNumber, f) * canvasHr / 4.0;

            float targetStartArc = curSum / sum * TWO_PI;
            float targetEndArc = ((curSum + nums[i]) / sum * TWO_PI) > TWO_PI ? TWO_PI : ((curSum + nums[i]) / sum * TWO_PI);

            float arcX = canvasXScale(canvasWr / 2.0);
            float arcY = canvasYScale(canvasHr / 2.0);

            arc(arcX, arcY, curRadius, curRadius, targetStartArc, targetEndArc);

            popStyle();

            curSum += nums[i];
        }

    }

    private void adjArc(float f) {
        float minNumber = 0; // getColMin(table, defaultCol);
        float maxNumber = getColMax(table, defaultCol);
        float sum = getColSum(table, defaultCol);
        float curSum = 0;
        float[] nums = table.getFloatColumn(defaultCol);

        for (int i = 0; i < n; i++) {
            float x1 = 0.5 * segment;
            float y1 = curSum / sum * canvasHr;

            float barW = segment;
            float barH = nums[i] / sum * canvasHr;

            pushStyle();

            float targetW = canvasHr / 2.0;
            float curW = nums[i] / maxNumber * targetW + 2;
            strokeWeight(curW);

            //noFill();

            stroke(lerpColor(colors[0], colors[1], float(i) / n));
            strokeCap(SQUARE);
            ellipseMode(RADIUS);

            float curRadius = nums[i] / maxNumber * canvasHr / 4.0;

            float preStartArc = curSum / sum * TWO_PI;
            float preEndArc = ((curSum + nums[i]) / sum * TWO_PI) > TWO_PI ? TWO_PI : ((curSum + nums[i]) / sum * TWO_PI);

            float targetStartArc = float(i) / n * TWO_PI;
            float targetEndArc = (float(i + 1) / n * TWO_PI) > TWO_PI ? TWO_PI : (float(i + 1) / n) * TWO_PI;

            float curStartArc = lerp(preStartArc, targetStartArc, f);
            float curEndArc = lerp(preEndArc, targetEndArc, f);

            float arcX = canvasXScale(canvasWr / 2.0);
            float arcY = canvasYScale(canvasHr / 2.0);

            arc(arcX, arcY, curRadius, curRadius, curStartArc, curEndArc);

            popStyle();

            curSum += nums[i];
        }

    }

    private void drawPlAxis(float f) {
        float minNumber = 0;
        float maxNumber = getColMax(table, defaultCol);
        float[] nums = table.getFloatColumn(defaultCol);
        int colNum = nums.length;
        float radarRadius = canvasHr / 2.0;
        fill(color(0));
        stroke(color(0));
        strokeWeight(1);
        // cannot handle x = c
        int stopIndex = f > 0 ? (n + 1) : n;
        for (int i = 0; i < stopIndex; i++) {
            float yRadarRadius = canvasHr / 2.0;

            float x1 = canvasXScale(((i + 0.5) * segment));
            float y1 = ((nums[i % colNum] - minNumber) / maxNumber * (canvasHr));
            yRadarRadius = y1 / 2.0;
            y1 = canvasYScale(y1);

            float tRadarRadius = lerp(0, radarRadius, f);

            float ty1 = canvasYScale(cos(float(i) / n * TWO_PI + PI / 2.0) * tRadarRadius + radarRadius);
            float tx1 = canvasXScale(canvasWr / 2.0 + sin(float(i) / n * TWO_PI + PI / 2.0) * tRadarRadius);

            float x2 = x1;
            float y2 = canvasYScale(0);
            float tx2 = canvasXScale(canvasWr / 2.0);
            float ty2 = canvasYScale(canvasHr / 2.0);

            float lx1 = tx1;
            float lx2 = tx2;
            float ly1 = ty1;
            float ly2 = ty2;
            if (i <= n - 1)
                line(lx1, ly1, lx2, ly2);
        }
    }


    private void stackFirstArc(float f) {
        float minNumber = 0; // getColMin(table, defaultCol);
        float maxNumber = getColMax(table, defaultCol);
        float allMaxNumber = getRowSumMax(table);

        float sum = getColSum(table, defaultCol);
        float curSum = 0;
        float[] nums = table.getFloatColumn(defaultCol);

        for (int i = 0; i < n; i++) {
            pushStyle();
            fill(colors[0]);
            strokeCap(SQUARE);
            ellipseMode(RADIUS);

            float preRadius = nums[i] / maxNumber * canvasHr / 2.0;
            float targetRadius = nums[i] / allMaxNumber * canvasHr / 2.0;
            float curRadius = lerp(preRadius, targetRadius, f);

            float targetStartArc = float(i) / n * TWO_PI;
            float targetEndArc = (float(i + 1) / n * TWO_PI) > TWO_PI ? TWO_PI : (float(i + 1) / n * TWO_PI);

            float arcX = canvasXScale(canvasWr / 2.0);
            float arcY = canvasYScale(canvasHr / 2.0);

            arc(arcX, arcY, curRadius, curRadius, targetStartArc, targetEndArc);

            popStyle();

            curSum += nums[i];
        }

    }


    private void adjColor(float f) {
        float minNumber = 0; // getColMin(table, defaultCol);
        float maxNumber = getColMax(table, defaultCol);
        float sum = getColSum(table, defaultCol);
        float curSum = 0;
        float[] nums = table.getFloatColumn(defaultCol);

        for (int i = 0; i < n; i++) {
            float barW = segment;
            float barH = nums[i] / sum * canvasHr;

            pushStyle();

            float targetW = canvasHr / 2.0;
            float curW = nums[i] / maxNumber * targetW + 2;
            strokeWeight(curW);

            noFill();

            int stopFab = int(f * n);

            if (i < stopFab)
                stroke(colors[0]);
            else
                stroke(lerpColor(colors[0], colors[1], float(i) / n));

            strokeCap(SQUARE);
            ellipseMode(RADIUS);

            float curRadius = nums[i] / maxNumber * canvasHr / 4.0;

            float targetStartArc = float(i) / n * TWO_PI;
            float targetEndArc = (float(i + 1) / n * TWO_PI) > TWO_PI ? TWO_PI : (float(i + 1) / n) * TWO_PI;

            float arcX = canvasXScale(canvasWr / 2.0);
            float arcY = canvasYScale(canvasHr / 2.0);

            arc(arcX, arcY, curRadius, curRadius, targetStartArc, targetEndArc);

            popStyle();

            curSum += nums[i];
        }

    }

    private void stackArcs(float f) {
        
        float minNumber = 0; // getColMin(table, defaultCol);
        float maxNumber = getColMax(table, defaultCol);
        float allMaxNumber = getRowSumMax(table);
        int colNum = table.getColumnCount();
        float sum = getColSum(table, defaultCol);
        float curSum = 0;
        int realStep = (step - stepBeforeFill) > (colNum - 1) ? (colNum - 1) : (step - stepBeforeFill);

        for (int j = realStep; j >= defaultCol; j--) {
            float[] nums = table.getFloatColumn(j);
            for (int i = 0; i < n; i++) {
                pushStyle();
                float preW = (getSubRowSum(table, i, defaultCol, j - 1)) / allMaxNumber * canvasHr / 2.0;
                float targetW = (getSubRowSum(table, i, defaultCol, j)) / allMaxNumber * canvasHr / 2.0;
                float curW = (j == realStep) ? lerp(preW, targetW, f) : targetW;
                strokeWeight(targetW);

                fill(lerpColor(colors[0], colors[1], float(j - 1) / colNum));
                stroke(lerpColor(colors[0], colors[1], float(j - 1) / colNum));

                strokeWeight(1);
                strokeCap(SQUARE);
                ellipseMode(RADIUS);
                float preRadius = (getSubRowSum(table, i, defaultCol, j - 1)) / allMaxNumber * canvasHr / 2.0;
                float targetRadius = (getSubRowSum(table, i, defaultCol, j)) / allMaxNumber * canvasHr / 2.0;
                float curRadius = (j == realStep) ? lerp(preRadius, targetRadius, f) : targetRadius;

                float targetStartArc = float(i) / n * TWO_PI;
                float targetEndArc = (float(i + 1) / n * TWO_PI) > TWO_PI ? TWO_PI : (float(i + 1) / n) * TWO_PI;

                float arcX = canvasXScale(canvasWr / 2.0);
                float arcY = canvasYScale(canvasHr / 2.0);


                arc(arcX, arcY, curRadius, curRadius, targetStartArc, targetEndArc);

                popStyle();

                curSum += nums[i];
            }
        }
    }
}
