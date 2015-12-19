// bar - stackedbar
public class Bar_Stackedbar_AT extends AtomicTransition { // AT is short for AtomicTransition

    public Bar_Stackedbar_AT(String s, String e, int d) {
        super(s, e, d);
        stepMax = table.getColumnCount() - 1 + 2;
        stepIndice = new int[stepMax + 1];
        for (int i = 0; i < stepIndice.length; i++) {
            stepIndice[i] = constantFrame;
        }
        stepBeforeFill = 1;
        stepAfterFill = 1;
        init();
    }

    public void step0() {
        recomputeNSeg();
        drawStackedAxis(float(stepIndex) / stepIndexMax);
        drawFirstBars(float(stepIndex) / stepIndexMax);
        nextFrame();
    }

    public void step() {
        recomputeNSeg();
        drawStackedAxis(1);
        drawFirstBarsReal();
        drawBars(float(stepIndex) / stepIndexMax);
        nextFrame();
    }


public void step1001() {
        recomputeNSeg();
        drawStackedAxis(1);
        drawBars(1);
        drawLegend(float(stepIndex) / stepIndexMax);
        nextFrame();
    }

    private void drawFirstBars(float f) {
        float minNumber = 0; // getColMin(table, defaultCol);
        float maxNumber = getColMax(table, defaultCol);
        float allMaxNum = getRowSumMax(table);
        float[] nums = table.getFloatColumn(step + 1);
        int colNum = table.getColumnCount();
        for (int i = 0; i < n; i++) {
            float x1 = ((i + 0.25) * segment);
            float y1 = ((nums[i] - minNumber) / maxNumber * canvasHr);
            float barW = 0.5 * segment;
            float barH = y1;
            float tBarH = (nums[i] - minNumber) / allMaxNum * canvasHr;
            float tBarY = (getSubRowSum(table, i, defaultCol, defaultCol) - minNumber) / allMaxNum * canvasHr;

            rectMode(CORNER);
            strokeWeight(1);
            stroke(lerpColor(colors[0], colors[1], float(0) / colNum));
            fill(lerpColor(colors[0], colors[1], float(0) / colNum));

            float tmpH = lerp(barH, tBarH, f);
            float tmpY = lerp(y1, tBarY, f);

            rect(canvasXScale(x1), canvasYScale(tmpH), barW, tmpH);
        }
    }


private void drawFirstBarsReal() {
        float minNumber = 0; // getColMin(table, defaultCol);
        float maxNumber = getColMax(table, defaultCol);
        float allMaxNum = getRowSumMax(table);
        float[] nums = table.getFloatColumn(defaultCol);
        int colNum = table.getColumnCount();
        for (int i = 0; i < n; i++) {
            float x1 = ((i + 0.25) * segment);
            float barW = 0.5 * segment;
            float tBarH = (nums[i] - minNumber) / allMaxNum * canvasHr;
            float tBarY = (getSubRowSum(table, i, defaultCol, defaultCol) - minNumber) / allMaxNum * canvasHr;

            rectMode(CORNER);
            strokeWeight(1);
            fill(lerpColor(colors[0], colors[1], float(0) / colNum));
            stroke(lerpColor(colors[0], colors[1], float(0) / colNum));

            float tmpH = tBarH;
            float tmpY = tBarY;

            rect(canvasXScale(x1), canvasYScale(tmpH), barW, tmpH);
        }
    }

    private void drawBars(float f) {
        float minNumber = 0; // getColMin(table, defaultCol);
        float maxNumber = getRowSumMax(table);
        float sum = getColSum(table, defaultCol);
        float curSum = 0;
        int colNum = table.getColumnCount();

        int realStep = (step - stepBeforeFill) > (colNum - 1) ? (colNum - 1) : (step - stepBeforeFill); 
            for (int j = realStep; j >= defaultCol ; j--) {
                float[] nums = table.getFloatColumn(j);
             for (int i = 0; i < n; i++) {               
                float rowRatioInside = nums[i] / maxNumber;
                float x1 = ((i + 0.25) * segment);
                if((j == realStep) && (j != defaultCol)){
                    x1 += 0.25 * segment * (1 - f);
                }
                float y1 = ((getSubRowSum(table, i, defaultCol, j) - minNumber) / maxNumber * canvasHr);
                float barH = rowRatioInside * canvasHr;
                float barW = 0.5 * segment;

                rectMode(CORNER);
                strokeWeight(1);
                fill(lerpColor(colors[0], colors[1], float(j - 1) / colNum));
                stroke(lerpColor(colors[0], colors[1], float(j - 1) / colNum));
                if((j == realStep) && (j != defaultCol)){
                     fill(lerpColor(colors[0], colors[1], float(j - 1) / colNum), f * 255);
                     stroke(lerpColor(colors[0], colors[1], float(j - 1) / colNum), f * 255);
                }

                rect(canvasXScale(x1), canvasYScale(y1), barW, barH);
                }

          
        }
    }

    protected void drawStackedAxis(float f) {
        int n = table.getRowCount();
        float minNumber = 0; // getColMin(table, defaultCol);
        float maxNumber = getRowSumMax(table);
        float maxNumberOg = getColMax(table, defaultCol);
        float numStep = lerp(maxNumberOg, maxNumber, f) / numTickets;

        strokeWeight(1);
        fill(0);
        stroke(0);
        // x axis
        line(canvasXScale(0), canvasYScale(0), canvasXScale((canvasW - canvas_margin_left - canvas_margin_right)), canvasYScale(0));

        for (int i = 0; i < n; i++) {
            float curX = (i + 0.5) / n * (canvasWr);
            float tmpY = canvasYScale(0) + textWidth(table.getStringColumn(0)[i]) / 2.0 + 7;
            stroke(0);
            line(canvasXScale(curX), canvasYScale(0), canvasXScale(curX), canvasYScale(0) + 5);
            pushMatrix();
            translate(canvasXScale(curX + fontSize / 2.0), tmpY);
            rotate(radians(-90));
            textAlign(CENTER);
            fill(0);
            text(table.getStringColumn(0)[i], 0, 0);
            popMatrix();
        }
        // y axis
        line(canvasXScale(0), canvasYScale(0), canvasXScale(0), canvasYScale(canvasHr));

        for (int i = 0; i < numTickets; i++) {
            float tmpX = canvasXScale(0);
            float tmpY = canvasYScale(i / float(numTickets) * canvasHr);
            String str = nfc(numStep * i, 1);
            stroke(0);
            line(tmpX, tmpY, tmpX - 5, tmpY);
            fill(0);
            textAlign(RIGHT);
            text(str, tmpX - 7, tmpY + fontSize / 2.0);
        }
    }

}
