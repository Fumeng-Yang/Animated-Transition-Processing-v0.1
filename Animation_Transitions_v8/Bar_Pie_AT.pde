public class Bar_Pie_AT extends AtomicTransition { // AT is short for AtomicTransition

    public Bar_Pie_AT(String s, String e, int d) {
        super(s, e, d);
        stepMax = 5;
        stepIndice = new int[stepMax + 1];
        stepIndice[0] = table.getRowCount();
        stepIndice[1] = constantFrame;
        stepIndice[2] = constantFrame;
        stepIndice[3] = 2 * constantFrame;
        stepIndice[4] = 2 * constantFrame;
        stepIndice[5] = constantFrame;
        init();
    }

// change color
    public void step0(){
    	recomputeNSeg();
        drawAxis(1);
        drawBars(float(stepIndex) / stepIndexMax);
    	nextFrame(); 
    }

// axis dispear
    public void step1(){
    	recomputeNSeg();
        drawAxis(1 - float(stepIndex) / stepIndexMax);
        drawBars(1);
    	nextFrame(); 
    }

// get stacked
    public void step2(){
    	recomputeNSeg();
    	float f = float(stepIndex) / (stepIndexMax * 0.85);
    	if(f > 1)
    		f = 1;
        drawStacked(f, 0);
    	nextFrame(); 
    }

    public void step3(){
        recomputeNSeg();
        float f = float(stepIndex) / (stepIndexMax * 0.85);
        if(f > 1)
            f = 1;
        drawStacked(1, f);
        nextFrame(); 
    }


    public void step4(){
    	recomputeNSeg();    
    	drawArc(float(stepIndex) / stepIndexMax);
    	nextFrame(); 
    }

    public void step5(){
        recomputeNSeg();    
        drawArc(1);
        drawPlXLabelPerc(float(stepIndex) / stepIndexMax);
        nextFrame(); 
    }

    private void drawArc(float f){
    	float minNumber = 0;// getColMin(table, defaultCol);
        float maxNumber = getColMax(table, defaultCol);
        float sum = getColSum(table, defaultCol);
        float curSum = 0;
        float[] nums = table.getFloatColumn(defaultCol);

        for(int i = 0; i < n; i++){
        	    float x1 = 0.5 * segment;
                float y1 = curSum / sum * canvasHr;

                float barW = segment;
                float barH = nums[i] / sum * canvasHr;             
                
                pushStyle();

                float targetW = canvasHr / 2.0;
                strokeWeight(lerp(barW, targetW, f));
               
                noFill();

                stroke(lerpColor(colors[0], colors[1], float(i) / n));
                strokeCap(SQUARE);

                ellipseMode(RADIUS);

                float curRadius = lerp(MAX_RADIUS, canvasHr / 4.0, f);

                float arcDelta = asin((barH / 2.0) / curRadius);
                float degreeDelta = (nums[i] / sum) > 1 ? (nums[i] / sum - 1) : (nums[i] / sum);
                float targetDelta = asin(degreeDelta);

                float targetStartArc = curSum / sum * TWO_PI;
                float targetEndArc = ((curSum + nums[i]) / sum * TWO_PI) > TWO_PI ? TWO_PI : ((curSum + nums[i]) / sum * TWO_PI);
                
                float curStartArc = lerp(PI - lerp(arcDelta, targetDelta, f), targetStartArc, f);
                float curEndArc = lerp(PI + lerp(arcDelta, targetDelta, f), targetEndArc, f);

                float arcX = lerp(canvasXScale(x1) + barW + curRadius, canvasXScale(canvasWr / 2.0), f);
                float arcY = lerp(canvasYScale(y1), canvasYScale(canvasHr / 2.0), f);
     
                arc(arcX, arcY, curRadius, curRadius, curStartArc, curEndArc);

                popStyle();
                
                curSum += nums[i];
         }

    }

    private void drawStacked(float f, float c){
    	float minNumber = 0;// getColMin(table, defaultCol);
        float maxNumber = getColMax(table, defaultCol);
        float sum = getColSum(table, defaultCol);
        float curSum = 0;
        float[] nums = table.getFloatColumn(defaultCol);

        for(int i = 0; i < n; i++){
                curSum += nums[i];
                float preX = ((i + 0.25) * segment);
                float preY = ((nums[i] - minNumber) / maxNumber * (canvasHr));
                float x1 = lerp(preX, 0.5 * segment , c);
                float y1 = lerp(preY, curSum / sum * canvasHr, f);
                float barW = 0.5 * segment + 0.5 * segment * f;
                float barH = lerp(preY, nums[i] / sum * canvasHr, f);
          
                rectMode(CORNER);
                strokeWeight(1);
                stroke(lerpColor(colors[0], colors[1], float(i) / n));
                fill(lerpColor(colors[0], colors[1], float(i) / n));
                rect(canvasXScale(x1), canvasYScale(y1), barW, barH);
         }
    }

    private void drawBars(float f){
        float minNumber = 0;// getColMin(table, defaultCol);
        float maxNumber = getColMax(table, defaultCol);
        float[] nums = table.getFloatColumn(defaultCol);

        for(int i = 0; i < n; i++){
                float x1 = ((i + 0.25) * segment);
                float y1 = ((nums[i] - minNumber) / maxNumber * (canvasH - canvas_margin_bottom - canvas_margin_top));
                float barW = 0.5 * segment;
                float barH = y1;
                noStroke();
          
                rectMode(CORNER);
                if(step > 0)
                	fill(lerpColor(colors[0], colors[1], float(i) / n));
                else if(i <= stepIndex)
                    fill(lerpColor(colors[0], colors[1], float(i) / stepIndexMax));
                else
                	fill(colors[0]);

                rect(canvasXScale(x1), canvasYScale(y1), barW, barH);
         }
    }
}
