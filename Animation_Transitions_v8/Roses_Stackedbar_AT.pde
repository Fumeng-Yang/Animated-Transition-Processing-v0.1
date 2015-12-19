
// roses - kaleidoscope - stackedbar
public class Roses_Stackedbar_AT extends AtomicTransition {
    public Roses_Stackedbar_AT(String s, String e, int d) {
        super(s, e, d);
        stepMax = 3;
        stepIndice = new int[stepMax + 1];
        for (int i = 0; i < stepIndice.length; i++) {
            stepIndice[i] = constantFrame;
        }
        init();
    }

    public void step0(){
        recomputeNSeg();
        stackArcs(0);
        drawPlXLabelUniform(1 - float(stepIndex) / stepIndexMax);
        drawPlAxis(1 - float(stepIndex) / stepIndexMax);
        drawStackedYPldAxis(1 - float(stepIndex) / stepIndexMax);
        drawLegend(1 - stepIndex / float(stepIndexMax));
        nextFrame();       
    }

    public void step1(){
        recomputeNSeg();
        stackArcs(float(stepIndex) / stepIndexMax);
        nextFrame();       
    }

    public void step2(){
        recomputeNSeg();
        //stackArcs(1);
        drawShortBars(float(stepIndex) / stepIndexMax);
        drawXAxis(float(stepIndex) / stepIndexMax);
        nextFrame();       
    }
    
    public void step3(){
        recomputeNSeg();
        //stackArcs(1);
        drawBars(float(stepIndex) / stepIndexMax);    
        drawStackYAxis(float(stepIndex) / stepIndexMax);
        drawXAxis(1);
        drawLegend(stepIndex / float(stepIndexMax));
        nextFrame();       
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
            float tx1 = canvasXScale(canvasWr / 2.0 + sin(float(i) / n * TWO_PI  + PI / 2.0) * tRadarRadius);

            float x2 = x1;
            float y2 = canvasYScale(0);
            float tx2 = canvasXScale(canvasWr / 2.0);
            float ty2 = canvasYScale(canvasHr / 2.0);

            float lx1 =  tx1;
            float lx2 = tx2;
            float ly1 = ty1;
            float ly2 =  ty2;
            if (i <= n - 1)
                line(lx1, ly1, lx2, ly2);
        }
    }

    private void stackArcs(float f) {
        
        float minNumber = 0; // getColMin(table, defaultCol);
        float maxNumber = getColMax(table, defaultCol);
        float allMaxNumber = getRowSumMax(table);
        int colNum = table.getColumnCount();
        int realStep = colNum - 1;
        float sum = getColSum(table, defaultCol);
        float curSum = 0;

        for (int j = realStep; j >= defaultCol; j--) {

            float[] nums = table.getFloatColumn(j);
           
            for (int i = 0; i < n; i++) {
                pushStyle();
                float preW = (getSubRowSum(table, i, defaultCol, j - 1)) / allMaxNumber * canvasHr / 2.0;
                float targetW = (getSubRowSum(table, i, defaultCol, j)) / allMaxNumber * canvasHr / 2.0;
                float curW =  targetW;
                strokeWeight(targetW);

                fill(lerpColor(colors[0], colors[1], float(j - 1) / colNum));
                stroke(lerpColor(colors[0], colors[1], float(j - 1) / colNum));

                strokeWeight(1);
                strokeCap(SQUARE);
                ellipseMode(RADIUS);
                float preRadius = (getSubRowSum(table, i, defaultCol, j - 1)) / allMaxNumber * canvasHr / 2.0;
                float targetRadius = (getSubRowSum(table, i, defaultCol, j)) / allMaxNumber * canvasHr / 2.0;
                float curRadius =  targetRadius;

                float targetStartArc = float(i) / n * TWO_PI;
                float targetEndArc = (float(i + 1) / n * TWO_PI) > TWO_PI ? TWO_PI : (float(i + 1) / n) * TWO_PI;

                float arcX = canvasXScale(canvasWr / 2.0);
                float arcY = canvasYScale(canvasHr / 2.0);
                
                float targetArcX = canvasXScale((i + 0.5) * segment);
                float targetArcY = canvasYScale(0);

                float curX = lerp(arcX, targetArcX, f);
                float curY = lerp(arcY, targetArcY, f);

                pushMatrix();
                translate(curX, curY);
                float preRotat = 0;
                float deltaArc = (targetEndArc - targetStartArc) / 2.0;
                float targetRotat = 1.5 * PI - (targetStartArc + targetEndArc) / 2.0;
                rotate(lerp((targetStartArc + targetEndArc) / 2.0, 1.5 * PI, f));
                arc(0, 0, curRadius, curRadius, -1 * deltaArc, deltaArc);
                popMatrix();

                popStyle();

                curSum += nums[i];
            }
        }
    }

private void drawShortBars(float f) {
        
        float minNumber = 0; // getColMin(table, defaultCol);
        float maxNumber = getColMax(table, defaultCol);
        float allMaxNumber = getRowSumMax(table);
        int colNum = table.getColumnCount();
        int realStep = colNum - 1;
        float sum = getColSum(table, defaultCol);
        float curSum = 0;

        for (int j = realStep; j >= defaultCol; j--) {
            float[] nums = table.getFloatColumn(j);

            for (int i = 0; i < n; i++) {
                pushStyle();
                float targetW = (getSubRowSum(table, i, defaultCol, j)) / allMaxNumber * canvasHr / 2.0;
                strokeWeight(targetW);

                strokeWeight(1);
                strokeCap(SQUARE);
                ellipseMode(RADIUS);
                float preRadius = (getSubRowSum(table, i, defaultCol, j)) / allMaxNumber * canvasHr / 2.0;

                float targetStartArc = float(i) / n * TWO_PI;
                float targetEndArc = (float(i + 1) / n * TWO_PI) > TWO_PI ? TWO_PI : (float(i + 1) / n) * TWO_PI;

                float arcX = canvasXScale(canvasWr / 2.0);
                float arcY = canvasYScale(canvasHr / 2.0);
                
                float targetArcX = canvasXScale((i + 0.5) * segment);
                float targetArcY = canvasYScale(0);

                fill(lerpColor(colors[0], colors[1], float(j - 1) / colNum), (1 - f) * 255);
                stroke(lerpColor(colors[0], colors[1], float(j - 1) / colNum), (1 - f) * 255);

                pushMatrix();
                translate(targetArcX, targetArcY);
                float deltaArc = (targetEndArc - targetStartArc) / 2.0;
                rotate(1.5 * PI);
                arc(0, 0, preRadius, preRadius, -1 * deltaArc, deltaArc);
                popMatrix();

                fill(lerpColor(colors[0], colors[1], float(j - 1) / colNum), f * 255);
                stroke(lerpColor(colors[0], colors[1], float(j - 1) / colNum), f * 255);

                float rowRatio = nums[i] / allMaxNumber;
                float x1 = ((i + 0.25) * segment);
                float y1 = ((getSubRowSum(table, i, defaultCol, j) - minNumber) / allMaxNumber * (canvasHr / 2.0));
                float barH = rowRatio * (canvasHr / 2.0);
                float barW = 0.5 * segment;

                rectMode(CORNER);
                strokeWeight(1);

                rect(canvasXScale(x1), canvasYScale(y1), barW, barH);

                popStyle();
            }
        }
    }

private void drawBars(float f) {
        float minNumber = 0; // getColMin(table, defaultCol);
        float maxNumber = getColMax(table, defaultCol);
        float allMaxNumber = getRowSumMax(table);
        int colNum = table.getColumnCount();
        int realStep = colNum - 1;
        float sum = getColSum(table, defaultCol);
        float curSum = 0;

        for (int j = realStep; j >= defaultCol; j--) {
            float[] nums = table.getFloatColumn(j);

            for (int i = 0; i < n; i++) {
                pushStyle();          
                fill(lerpColor(colors[0], colors[1], float(j - 1) / colNum));
                stroke(lerpColor(colors[0], colors[1], float(j - 1) / colNum));
                float preH = canvasHr / 2.0;
                float targetH = canvasHr;
                float curH = lerp(preH, targetH, f);
                float rowRatio = nums[i] / allMaxNumber;
                float x1 = ((i + 0.25) * segment);
                float y1 = ((getSubRowSum(table, i, defaultCol, j) - minNumber) / allMaxNumber * curH);
                float barH = rowRatio * curH;
                float barW = 0.5 * segment;

                rectMode(CORNER);
                strokeWeight(1);

                rect(canvasXScale(x1), canvasYScale(y1), barW, barH);

                popStyle();
            }
        }
    }

     protected void drawStackYAxis(float f){ 
            int n = table.getRowCount();
            float minNumber = 0;// getColMin(table, defaultCol);
            float maxNumber = getColMax(table, defaultCol);
            float allMaxNumber = getRowSumMax(table);
            float numStep = allMaxNumber / numTickets;

            strokeWeight(1);
            fill(0);
            stroke(0);
       
        // y axis
            line(canvasXScale(0)
                , canvasYScale(0)
                , canvasXScale(0)
                , canvasYScale(canvasHr * f));

            for(int i = 0 ; i < numTickets; i++){
                 float tmpX = canvasXScale(0);
                 float tmpY = canvasYScale(i / float(numTickets) * canvasHr);             
                 String str = nfc(numStep * i, 1);
                 stroke(0);
                 line(tmpX, tmpY, tmpX - 5, tmpY);
                 fill(0);
                 textAlign(RIGHT);
                 text(str, tmpX - 7, tmpY + fontSize / 2.0);
            }

            fill(255);
            noStroke();
            rectMode(CORNER);
            rect((canvasWr + canvas_margin_right) * f, canvasYScale(0)
              , (canvasWr + canvas_margin_right), canvas_margin_bottom);
            rect(canvasXScale(0) - canvas_margin_left, canvasYScale(canvasHr), canvas_margin_left + 1, canvasHr * (1 - f));      
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

}
