
// ThemeRiver - stem - stackedbar
public class Stackedbar_Themeriver_AT extends AtomicTransition { // AT is short for AtomicTransition
    public Stackedbar_Themeriver_AT(String s, String e, int d) {
        super(s, e, d);
        stepMax = 4 + table.getColumnCount() - 1;
        stepIndice = new int[stepMax + 1];
        for (int i = 0; i < stepIndice.length; i++) {
            stepIndice[i] = 2 * constantFrame;
        }
        stepBeforeFill = 3;
        stepAfterFill = 1;
        init();
    }

    public void step0(){
        recomputeNSeg();
        drawBars(float(stepIndex) / stepIndexMax, 1);
        drawLegend(1);
        drawXAxis(1);
        drawStackYAxis(1);
        nextFrame();      
    }

   public void step1(){
        recomputeNSeg();
        drawBars(1, 1);
        drawXAxis(1);
        drawStackYAxis(1);
        connectPoints(float(stepIndex) / stepIndexMax);
        drawLegend(1);
        nextFrame();      
    }

    public void step2(){
        recomputeNSeg();
        drawBars(1, 1 - float(stepIndex) / stepIndexMax);
        drawXAxis(1);
        drawStackYAxis(1);
        connectPoints(1);
        drawLegend(1);
        nextFrame();      
    }

     public void step(){
        recomputeNSeg();
        drawXAxis(1);
        drawStackYAxis(1);
        connectPoints(1);
        fillLines(float(stepIndex) / stepIndexMax, 1);
        drawLegend(1);
        nextFrame();      
    }

    public void step1001(){
        recomputeNSeg();
        drawXAxis(1);
        drawStackYAxis(1);
        //connectPoints(1);
        //fillLines(1, 1);
        smoothyThread(float(stepIndex) / stepIndexMax);
        drawLegend(1);
        nextFrame();      
    }

    private void fillLines(float f, float r) {    
        int colNum = table.getColumnCount();
        int realStep = (step - stepBeforeFill) > (colNum - 1) ? (colNum - 1): (step - stepBeforeFill);
        float minNumber = 0;
        float maxNumber = getColMax(table, defaultCol);
        float allMaxNumber = getRowSumMax(table);
        float curHeight = canvasHr * r;
        float offset = (canvasHr - curHeight) * r;

        for (int j = realStep; j >= defaultCol; j--) {
            float[] nums = table.getFloatColumn(j);
            strokeWeight(1);
            stroke(lerpColor(colors[0], colors[1], float(j - 1) / colNum));
            fill(lerpColor(colors[0], colors[1], float(j - 1) / colNum));
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

    private void drawBars(float f, float c) {
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
                fill(lerpColor(colors[0], colors[1], float(j - 1) / colNum), c * 255);
                stroke(lerpColor(colors[0], colors[1], float(j - 1) / colNum) , c * 255);
                float targetH = canvasHr;
                float rowRatio = nums[i] / allMaxNumber;
                float x1 = ((i + 0.25) * segment);
                float y1 = ((getSubRowSum(table, i, defaultCol, j) - minNumber) / allMaxNumber * targetH);
   
                float margin_top_bottom = (allMaxNumber - getRowSum(table, i)) / allMaxNumber * canvasHr / 2.0;
                float targetY1 = margin_top_bottom + y1;
                
                float curY = lerp(y1, targetY1, f);

                float barH = rowRatio * targetH;
                float barW = 0.5 * segment;

                rectMode(CORNER);
                strokeWeight(1);

                rect(canvasXScale(x1), canvasYScale(curY), barW, barH);

                popStyle();
            }
        }
    }

     private void connectPoints(float f) {
        int colNum = table.getColumnCount();
        float minNumber = 0;
        float allMaxNumber = getRowSumMax(table);
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
                float y1 = (getSubRowSum(table, i, defaultCol, j)) / allMaxNumber * canvasHr;
                float margin_top_bottom = (allMaxNumber - getRowSum(table, i)) / allMaxNumber * canvasHr / 2.0;
                float curY = y1 + margin_top_bottom;
                vertex(canvasXScale(x1), canvasYScale(curY));
            }
            
            for (int i = stopNum - 1; i >= 0; i--) {
                float x1 = ((i + 0.5) * segment);
                float y1 = (getSubRowSum(table, i, defaultCol, j)) / allMaxNumber * canvasHr;
                float margin_top_bottom = (allMaxNumber - getRowSum(table, i)) / allMaxNumber * canvasHr / 2.0;
                float curY = margin_top_bottom;
                vertex(canvasXScale(x1), canvasYScale(curY));
            }

            endShape(CLOSE);
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

}
