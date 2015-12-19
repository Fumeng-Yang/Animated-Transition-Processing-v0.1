
// themeriver - chocolatedonut - rose
public class Roses_Themeriver_AT extends AtomicTransition { // AT is short for AtomicTransition
    public Roses_Themeriver_AT(String s, String e, int d) {
        super(s, e, d);
        stepMax = 7 + table.getColumnCount() - 1;
        stepIndice = new int[stepMax + 1];
        for (int i = 0; i < stepIndice.length; i++) {
            stepIndice[i] = constantFrame;
        }

        stepBeforeFill = 4;
        stepAfterFill = 3;

        init();
    }

    public void step0(){
        recomputeNSeg();
        stackArcs(0, 1);
        drawPlXLabelUniform(1 - float(stepIndex) / stepIndexMax);
        drawPlAxis(1 - float(stepIndex) / stepIndexMax);
        drawStackedYPldAxis(1 - float(stepIndex) / stepIndexMax);
        drawLegend(1 - stepIndex / float(stepIndexMax));
        nextFrame();       
    }

     public void step1(){
        recomputeNSeg();
        stackArcs(float(stepIndex) / stepIndexMax, 1);
        drawXAxis(float(stepIndex) / stepIndexMax);
        nextFrame();       
    }

    public void step2(){
        recomputeNSeg();
        stackArcs(1, 1);
        connectPoints(float(stepIndex) / stepIndexMax);
        drawXAxis(1);
        nextFrame();       
    }

    public void step3(){
        recomputeNSeg();
        stackArcs(1, 1 - float(stepIndex) / stepIndexMax);
        connectPoints(1);
        fillLines(0, 0.5, 1);
        drawXAxis(1);
        nextFrame();       
    }

    public void step(){
        recomputeNSeg();
        connectPoints(1);
        fillLines(float(stepIndex) / stepIndexMax, 0.5, 1);
        drawXAxis(1);
        nextFrame();       
    }

    public void step1001(){
        recomputeNSeg();
        fillLines(1, float(stepIndex) / stepIndexMax * 0.5 + 0.5, 1);
        drawXAxis(1);
        nextFrame();       
    }

    public void step1002(){
        recomputeNSeg();
        //fillLines(1, 1, 1);
        smoothyThread(float(stepIndex) / stepIndexMax);
        drawXAxis(1);
        nextFrame();       
    }

    public void step1003(){
        recomputeNSeg();
        //fillLines(1, 1, 1 - float(stepIndex) / stepIndexMax);
        smoothyThread(1);
        drawStackAxis(float(stepIndex) / stepIndexMax);
        drawLegend(stepIndex / float(stepIndexMax));
        drawXAxis(1);
        nextFrame();       
    }

    private void connectPoints(float f) {
        int colNum = table.getColumnCount();
        float minNumber = 0;
        float allMaxNumber = getRowSumMax(table);
        float curHeight = canvasHr / 2.0;
        int stopNum = int(f * canvasWr / segment);

        for (int j = colNum - 1; j >= defaultCol; j--) {
            float[] nums = table.getFloatColumn(j);
            strokeWeight(1);
            stroke(lerpColor(colors[0], colors[1], float(j - 1) / colNum));
            fill(lerpColor(colors[0], colors[1], float(j - 1) / colNum));
            noFill();
            beginShape();

            for (int i = 0; i < stopNum; i++) {
                float x1 = ((i + 0.5) * segment);
                float y1 = (getSubRowSum(table, i, defaultCol, j)) / allMaxNumber * curHeight;
                float margin_top_bottom = (allMaxNumber - getRowSum(table, i)) / allMaxNumber * curHeight / 2.0 + canvasHr / 4.0;
                float curY = y1 + margin_top_bottom;
                vertex(canvasXScale(x1), canvasYScale(curY));
            }
            
            for (int i = stopNum - 1; i >= 0; i--) {
                float x1 = ((i + 0.5) * segment);
                float y1 = (getSubRowSum(table, i, defaultCol, j)) / allMaxNumber * curHeight;
                float margin_top_bottom = (allMaxNumber - getRowSum(table, i)) / allMaxNumber * curHeight / 2.0 + canvasHr / 4.0;
                float curY = margin_top_bottom;
                vertex(canvasXScale(x1), canvasYScale(curY));
            }

            endShape(CLOSE);
        }
    }



    private void fillLines(float f, float r, float c) {
        
        int colNum = table.getColumnCount();
        float minNumber = 0;
        float maxNumber = getColMax(table, defaultCol);
        float allMaxNumber = getRowSumMax(table);
        float curHeight = canvasHr * r;
        float offset = (canvasHr - curHeight) * r;
        int realStep = (step - stepBeforeFill) > (colNum - 1) ? (colNum - 1): (step - stepBeforeFill);

        for (int j = realStep; j >= defaultCol; j--) {
            float[] nums = table.getFloatColumn(j);
            strokeWeight(1);
            stroke(lerpColor(colors[0], colors[1], float(j - 1) / colNum), c * 255);
            fill(lerpColor(colors[0], colors[1], float(j - 1) / colNum), c * 255);
            beginShape();

            if (j != realStep) {
                for (int i = 0; i < n; i++) {
                    float x1 = ((i + 0.5) * segment);
                    float y1 = (getSubRowSum(table, i, defaultCol, j)) / allMaxNumber * (curHeight);
                    float margin_top_bottom = (allMaxNumber - getRowSum(table, i)) / allMaxNumber * curHeight / 2.0 + offset;
                    vertex(canvasXScale(x1), canvasYScale(y1 + margin_top_bottom));
                }
                for (int i = n - 1; i >= 0; i--) {
                    float x1 = ((i + 0.5) * segment);
                    float y1 = (getSubRowSum(table, i, defaultCol, j)) / allMaxNumber * curHeight;
                    float margin_top_bottom = (allMaxNumber - getRowSum(table, i)) / allMaxNumber * curHeight / 2.0 + offset;
                    float curY = margin_top_bottom;
                    vertex(canvasXScale(x1), canvasYScale(curY));
                }
                endShape(CLOSE);
            } else {
                for (int i = 0; i < n; i++) {
                    float x1 = ((i + 0.5) * segment);
                    float y1 = (getSubRowSum(table, i, defaultCol, j)) / allMaxNumber * (curHeight);
                    float preY = (getSubRowSum(table, i, defaultCol, j - 1)) / allMaxNumber * (curHeight);
                    float margin_top_bottom = (allMaxNumber - getRowSum(table, i)) / allMaxNumber * curHeight / 2.0 + offset;
                    float curY = lerp(preY + margin_top_bottom, y1 + margin_top_bottom, f);
                    vertex(canvasXScale(x1), canvasYScale(curY));
                }
                for (int i = n - 1; i >= 0; i--) {
                    float x1 = ((i + 0.5) * segment);
                     //float y1 = (getSubRowSum(table, i, defaultCol, j)) / allMaxNumber * (curHeight);
                    float preY = (getSubRowSum(table, i, defaultCol, j - 1)) / allMaxNumber * curHeight;
                    float y1 = (allMaxNumber - getRowSum(table, i)) / allMaxNumber * curHeight / 2.0 + canvasHr / 4.0;
                    float margin_top_bottom = (allMaxNumber - getRowSum(table, i)) / allMaxNumber * curHeight / 2.0 + offset;
                    float curY = margin_top_bottom;
                    vertex(canvasXScale(x1), canvasYScale(curY));
                }
                endShape(CLOSE);
            }
        }
    }



    private void stackArcs(float f, float c) {      
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

                fill(lerpColor(colors[0], colors[1], float(j - 1) / colNum), c * 255);
                stroke(lerpColor(colors[0], colors[1], float(j - 1) / colNum), c * 255);

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
                float margin_top_bottom = (allMaxNumber - getRowSum(table, i)) / allMaxNumber * canvasHr / 2.0;
                float targetArcY = canvasYScale(margin_top_bottom + getRowSum(table, i) / allMaxNumber * canvasHr / 4.0);

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
}
