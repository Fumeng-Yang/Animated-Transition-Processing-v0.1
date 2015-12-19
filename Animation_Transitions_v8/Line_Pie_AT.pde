// line - radar  + PI / 2.0e
public class Line_Pie_AT extends AtomicTransition { // AT is short for AtomicTransition
    public Line_Pie_AT(String s, String e, int d) {
        super(s, e, d);
        stepMax = 6;
        stepIndice = new int[stepMax + 1];
        for (int i = 0; i < stepIndice.length; i++) {
            stepIndice[i] = constantFrame;
        }
        init();
    }
// raise lines's axes
    public void step0() {
        recomputeNSeg();
        drawAxis(1 - float(stepIndex) / stepIndexMax);
        drawPlAxis(0, float(stepIndex) / stepIndexMax);
        drawPlLines(0);
        nextFrame();
    }
// change to radar chart
    public void step1() {
        recomputeNSeg();
        drawPlAxis(float(stepIndex) / stepIndexMax, 1);
        drawPlLines(float(stepIndex) / stepIndexMax);
        nextFrame();
    }
// adj angles
    public void step2() {
        recomputeNSeg();
        adjAnglesAxes(float(stepIndex) / stepIndexMax);
        adjAnglesPoints(float(stepIndex) / stepIndexMax);
        nextFrame();
    }
// adj points' radius
    public void step3() {
        recomputeNSeg();
        adjAnglesAxes(1);
        adjRadiusPoints(float(stepIndex) / stepIndexMax, 1);
        nextFrame();
    }
// raise points
    public void step4() {
        recomputeNSeg();
        adjAnglesAxes(1);
        adjRadiusPoints(1, 1 - float(stepIndex) / stepIndexMax);
        nextFrame();
    }
// fill in
    public void step5(){
        recomputeNSeg();
        adjAnglesAxes(1);
        adjRadiusPoints(1, 0);
        drawArc(float(stepIndex) / stepIndexMax);
        nextFrame();
    }

    public void step6(){
        recomputeNSeg();
        drawArc(1);
        drawPlXLabelPerc(float(stepIndex) / stepIndexMax);
        nextFrame();
    }

    private void drawPlAxis(float f, float c) {
        float minNumber = 0;
        float maxNumber = getColMax(table, defaultCol);
        float[] nums = table.getFloatColumn(defaultCol);
        int colNum = nums.length;
        float radarRadius = canvasHr / 2.0;
        fill(color(0), c * 255);
        stroke(color(0), c * 255);
        strokeWeight(1);
        // cannot handle x = c
        int stopIndex = f > 0 ? (n + 1) : n;
        for (int i = 0; i < stopIndex; i++) {
            float yRadarRadius = canvasHr / 2.0;

            float x1 = canvasXScale(((i + 0.5) * segment));
            float y1 = ((nums[i % colNum] - minNumber) / maxNumber * (canvasHr));
            yRadarRadius = y1 / 2.0;
            y1 = canvasYScale(y1);

            float tRadarRadius1 = lerp(yRadarRadius, radarRadius, f);

            float ty1 = canvasYScale(cos(float(i) / n * TWO_PI + PI / 2.0) * tRadarRadius1 + radarRadius);
            float tx1 = canvasXScale(canvasWr / 2.0 + sin(float(i) / n * TWO_PI  + PI / 2.0) * tRadarRadius1);

            float x2 = x1;
            float y2 = canvasYScale(0);
            float tx2 = canvasXScale(canvasWr / 2.0);
            float ty2 = canvasYScale(canvasHr / 2.0);

            float lx1 = lerp(x1, tx1, f);
            float lx2 = lerp(x2, tx2, f);
            float ly1 = lerp(y1, ty1, f);
            float ly2 = lerp(y2, ty2, f);
            if (i <= n - 1)
                line(lx1, ly1, lx2, ly2);
        }
    }


    private void drawPlLines(float f) {
        float minNumber = 0; // getColMin(table, defaultCol);
        float maxNumber = getColMax(table, defaultCol);
        float[] nums = table.getFloatColumn(defaultCol);
        int colNum = nums.length;
        float radarRadius = canvasHr / 2.0;
        // cannot handle x = c
        int stopIndex = f > 0 ? (n + 1) : n;
        for (int i = 0; i < n; i++) {
            float lx1 = -1;
            float lx2 = -1;
            float ly1 = -1;
            float ly2 = -1;

            float yRadarRadius1 = -1;
            float x1 = canvasXScale(((i % colNum + 0.5) * segment));
            float y1 = (nums[i % colNum] - minNumber) / maxNumber * (canvasHr);
            yRadarRadius1 = y1 / 2.0;
            y1 = canvasYScale(y1);

            float ty1 = canvasYScale(cos(float(i) / n * TWO_PI  + PI / 2.0) * yRadarRadius1 + radarRadius);
            float tx1 = canvasXScale(canvasWr / 2.0 + sin(float(i) / n * TWO_PI  + PI / 2.0) * yRadarRadius1);

            float yRadarRadius2 = -1;
            float x2 = canvasXScale((((i + 1) % colNum + 0.5) * segment));
            float y2 = (nums[(i + 1) % colNum] - minNumber) / maxNumber * (canvasHr);
            yRadarRadius2 = y2 / 2.0;
            y2 = canvasYScale(y2);

            float ty2 = canvasYScale(cos(float(i + 1) / n * TWO_PI  + PI / 2.0) * yRadarRadius2 + radarRadius);
            float tx2 = canvasXScale(canvasWr / 2.0 + sin(float(i + 1) / n * TWO_PI  + PI / 2.0) * yRadarRadius2);

            lx1 = lerp(x1, tx1, f);
            lx2 = lerp(x2, tx2, f);
            ly1 = lerp(y1, ty1, f);
            ly2 = lerp(y2, ty2, f);

            fill(colors[0]);
            stroke(colors[0]);
            strokeWeight(1);

            if (i < n - 1) {
                line(lx1, ly1, lx2, ly2);
            }
            ellipseMode(CENTER);
            fill(colors[0]);
            ellipse(lx1, ly1, pointSize, pointSize);

        }
    }

    private void adjAnglesPoints(float f) {
        float minNumber = 0; // getColMin(table, defaultCol);
        float maxNumber = getColMax(table, defaultCol);
        float[] nums = table.getFloatColumn(defaultCol);
        float colSum = getSum(nums);
        int colNum = nums.length;
        float radarRadius = canvasHr / 2.0;
        // cannot handle x = c   
        for (int i = 0; i < n; i++) {
            float lx1 = -1;
            float lx2 = -1;
            float ly1 = -1;
            float ly2 = -1;

            float yRadarRadius1 = -1;
            float x1 = canvasXScale(((i + 0.5) * segment));
            float y1 = (nums[i] - minNumber) / maxNumber * (canvasHr);
            yRadarRadius1 = y1 / 2.0;
            y1 = canvasYScale(y1);

            float ang1 = float(i) / n * TWO_PI  + PI / 2.0;
            float ang2 = getSubColSum(table, defaultCol, 0, i) / colSum * TWO_PI  + PI / 2.0;
            float ang = lerp(ang1, ang2, f);

            float ty1 = canvasYScale(cos(ang) * yRadarRadius1 + radarRadius);
            float tx1 = canvasXScale(canvasWr / 2.0 + sin(ang) * yRadarRadius1);

            float yRadarRadius2, x2, y2, angg1, angg2, angg, tx2, ty2;
            if (i < n - 1) {
                x2 = canvasXScale((i + 1 + 0.5) * segment);
                y2 = ((nums[i + 1] - minNumber) / maxNumber * (canvasHr));
                yRadarRadius2 = y2 / 2.0;
                y2 = canvasYScale(y2);

                angg1 = float(i + 1) / n * TWO_PI  + PI / 2.0;
                angg2 = (getSubColSum(table, defaultCol, 0, i + 1)) / colSum * TWO_PI  + PI / 2.0;

                angg = lerp(angg1, angg2, f);

                tx2 = canvasXScale(canvasWr / 2.0 + sin(angg) * yRadarRadius2);
                ty2 = canvasYScale(cos(angg) * yRadarRadius2 + radarRadius);
            } else {
                x2 = canvasXScale(0.5 * segment);
                y2 = ((nums[0] - minNumber) / maxNumber * (canvasHr));
                yRadarRadius2 = y2 / 2.0;
                y2 = canvasYScale(y2);

                angg1 = 0 / n * TWO_PI  + PI / 2.0;
                angg2 = (getSubColSum(table, defaultCol, 0, 0)) / colSum * TWO_PI  + PI / 2.0;

                angg = lerp(angg1, angg2, f);

                tx2 = canvasXScale(canvasWr / 2.0 + sin(angg) * yRadarRadius2);
                ty2 = canvasYScale(cos(angg) * yRadarRadius2 + radarRadius);
            }
            lx2 = tx2;
            ly2 = ty2;

            lx1 = tx1;
            ly1 = ty1;

            fill(colors[0]);
            stroke(colors[0]);
            strokeWeight(1);

            line(lx1, ly1, lx2, ly2);

            ellipseMode(CENTER);
            fill(colors[0]);
            ellipse(lx1, ly1, pointSize, pointSize);
        }
    }

    private void adjAnglesAxes(float f) {
        float minNumber = 0;
        float maxNumber = getColMax(table, defaultCol);
        float[] nums = table.getFloatColumn(defaultCol);
        int colNum = nums.length;
        float colSum = getSum(nums);

        float radarRadius = canvasHr / 2.0;
        fill(color(128));
        stroke(color(128));
        strokeWeight(1);
        // cannot handle x = c

        for (int i = 0; i < n; i++) {
            float yRadarRadius = canvasHr / 2.0;

            float ang1 = float(i) / n * TWO_PI  + PI / 2.0;
            float ang2 = getSubColSum(table, defaultCol, 0, i) / colSum * TWO_PI  + PI / 2.0;
            float ang = lerp(ang1, ang2, f);

            float ty1 = canvasYScale(cos(ang) * radarRadius + radarRadius);
            float tx1 = canvasXScale(canvasWr / 2.0 + sin(ang) * radarRadius);

            float tx2 = canvasXScale(canvasWr / 2.0);
            float ty2 = canvasYScale(canvasHr / 2.0);

            line(tx1, ty1, tx2, ty2);
        }
    }

    private void adjRadiusPoints(float f, float c) {
        float minNumber = 0; // getColMin(table, defaultCol);
        float maxNumber = getColMax(table, defaultCol);
        float[] nums = table.getFloatColumn(defaultCol);
        float colSum = getSum(nums);
        int colNum = nums.length;
        float radarRadius = canvasHr / 2.0;
        // cannot handle x = c

        for (int i = 0; i < n; i++) {
            float lx1 = -1;
            float lx2 = -1;
            float ly1 = -1;
            float ly2 = -1;

            float yRadarRadius1 = -1;
            float x1 = canvasXScale(((i % colNum + 0.5) * segment));
            float y1 = (nums[i % colNum] - minNumber) / maxNumber * (canvasHr);
            yRadarRadius1 = y1 / 2.0;
            y1 = canvasYScale(y1);

            float r1 = lerp(yRadarRadius1, radarRadius, f);

            float ang1 = float(i) / n * TWO_PI  + PI / 2.0;
            float ang2 = getSubColSum(table, defaultCol, 0, i) / colSum * TWO_PI  + PI / 2.0;
            float ang = ang2;

            float ty1 = canvasYScale(cos(ang) * r1 + radarRadius);
            float tx1 = canvasXScale(canvasWr / 2.0 + sin(ang) * r1);


            float yRadarRadius2 = -1;
            float x2 = canvasXScale((i % colNum + 1 + 0.5) * segment);
            float y2 = ((nums[(i + 1) % colNum] - minNumber) / maxNumber * (canvasHr));
            yRadarRadius2 = y2 / 2.0;
            y2 = canvasYScale(y2);

            float r2 = lerp(yRadarRadius2, radarRadius, f);

            float angg1 = float(i % colNum + 1) / n * TWO_PI  + PI / 2.0;
            float angg2 = -1;

            if (i >= n - 1) {
                angg2 = (getSubColSum(table, defaultCol, 0, 0)) / colSum * TWO_PI  + PI / 2.0;
            } else {
                angg2 = (getSubColSum(table, defaultCol, 0, i + 1)) / colSum * TWO_PI  + PI / 2.0;
            }

            float angg = angg2;

            float ty2 = canvasYScale(cos(angg) * r2 + radarRadius);
            float tx2 = canvasXScale(canvasWr / 2.0 + sin(angg) * r2);

            lx2 = tx2;
            ly2 = ty2;

            lx1 = tx1;
            ly1 = ty1;
            fill(colors[0]);
            stroke(colors[0]);
            strokeWeight(1);

            line(lx1, ly1, lx2, ly2);

            ellipseMode(CENTER);
            stroke(colors[0], c * 255);
            fill(colors[0], c * 255);
            ellipse(lx1, ly1, pointSize, pointSize);
        }
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

                float targetW = canvasHr / 2;
                strokeWeight(lerp(0, targetW, f));
               
                noFill();

                stroke(lerpColor(colors[0], colors[1], float(i) / n));
                strokeCap(SQUARE);
                ellipseMode(RADIUS);

                float curRadius = lerp(0, canvasHr / 4.0, f);

                float targetStartArc = curSum / sum * TWO_PI;
                float targetEndArc = ((curSum + nums[i]) / sum * TWO_PI) > TWO_PI ? TWO_PI : ((curSum + nums[i]) / sum * TWO_PI);

                float arcX = canvasXScale(canvasWr / 2.0);
                float arcY = canvasYScale(canvasHr / 2.0);
     
                arc(arcX, arcY, curRadius, curRadius, targetStartArc, targetEndArc);

                popStyle();
                
                curSum += nums[i];
         }

    }


}
