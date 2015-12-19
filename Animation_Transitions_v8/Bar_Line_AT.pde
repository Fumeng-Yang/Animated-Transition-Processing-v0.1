public class Bar_Line_AT extends AtomicTransition { // AT is short for AtomicTransition
    public Bar_Line_AT(String s, String e, int d) {
        super(s, e, d);
        stepMax = 3;
        stepIndice = new int[stepMax + 1];
        for (int i = 0; i < stepIndice.length; i++) {
            stepIndice[i] = 2 * constantFrame;
        }
        init();
    }

// bar dis
    public void step0(){
        recomputeNSeg();
        drawLines(0);
        //drawPoints(0);
        drawHorizonBars(1);   
        drawBars(1 - stepIndex / float(stepIndexMax));
        drawAxis(1);
        nextFrame(); 
    }
    
// into small square
    public void step1(){
        recomputeNSeg();
        drawAxis(1);
        drawLines(0);
        drawPoints(0);
        drawBars(0);
        drawHorizonBars(1 - stepIndex / float(stepIndexMax));   
        nextFrame();         
    }

// into scatterplot
    public void step2(){
        recomputeNSeg();
        drawAxis(1);
        drawLines(0);
        drawPoints(stepIndex / float(stepIndexMax));
        drawHorizonBars(0);
        drawBars(0);
        nextFrame();  
    }
    
// line connect
    public void step3(){     
        recomputeNSeg();
        drawAxis(1);
        drawLines(stepIndex / float(stepIndexMax));
        drawPoints(1);
        drawHorizonBars(0);
        drawBars(0);
        nextFrame();     
    }

    private void drawLines(float f){   
        float minNumber = 0;// getColMin(table, defaultCol);
        float maxNumber = getColMax(table, defaultCol);
        float[] nums = table.getFloatColumn(defaultCol);

// cannot handle x = c
         for(int i = 0; i < n; i++){
              if(i != n - 1){
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

    private void drawPoints(float f){
        float minNumber = 0;// getColMin(table, defaultCol);
        float maxNumber = getColMax(table, defaultCol);
        float[] nums = table.getFloatColumn(defaultCol);

        for(int i = 0; i < n; i++){
            float x1 = ((i + 0.5) * segment);
            float y1 = ((nums[i] - minNumber) / maxNumber * (canvasH - canvas_margin_bottom - canvas_margin_top));

            noStroke();

            ellipseMode(CENTER);
            fill(colors[0], f * 255);
            ellipse(canvasXScale(x1), canvasYScale(y1), pointSize, pointSize);

            rectMode(CENTER);
            fill(colors[0], (1 - f) * 255);
            rect(canvasXScale(x1), canvasYScale(y1), pointSize, pointSize);
         }

    }

   private void drawHorizonBars(float f){  
        float minNumber = 0;// getColMin(table, defaultCol);
        float maxNumber = getColMax(table, defaultCol);
        float[] nums = table.getFloatColumn(defaultCol);
        float curSize = pointSize;
        for(int i = 0; i < n; i++){
                float x1 = ((i + 0.5) * segment);
                float y1 = ((nums[i] - minNumber) / maxNumber * (canvasHr));
                float barW = 0.5 * segment * f;
                if(abs(y1 - 0) < 0.1 && (f > 0.999)){
                    curSize = 1;
                }
                
                noStroke();

                rectMode(CENTER);
                fill(colors[0]);
                rect(canvasXScale(x1), canvasYScale(y1), barW, curSize);
         }
    }

    private void drawBars(float f){
        float minNumber = 0;// getColMin(table, defaultCol);
        float maxNumber = getColMax(table, defaultCol);
        float[] nums = table.getFloatColumn(defaultCol);

        for(int i = 0; i < n; i++){
                float x1 = ((i + 0.5) * segment) - 0.25 * segment;
                float y1 = ((nums[i] - minNumber) / maxNumber * (canvasHr));
                float barW = 0.5 * segment;
                float barH = f * y1;
                noStroke();
          
                rectMode(CORNER);
                fill(colors[0]);
                rect(canvasXScale(x1), canvasYScale(y1), barW, barH);
         }
    }

}
