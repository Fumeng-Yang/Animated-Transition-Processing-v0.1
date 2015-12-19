import java.lang.reflect.*;

public abstract class AtomicTransition {
  protected String start = null;
  protected String end = null;
  protected int step = -1;
  protected int stepNameTracker = -1;
  protected int stepMax = -1;

  protected int stepIndex = -1;
  protected int stepIndexMax = -1;

  protected int direction = -1; // be careful about this

  protected int[] stepIndice = null;

  protected int stepCounter = -1;

  protected int n = -1;
  protected float segment  =  -1;

  protected int stepBeforeFill = -1;
  protected int stepAfterFill = -1;

  protected void name() {
    
  }

  public AtomicTransition(String s, String e, int d) {
    this.start = s;
    this.end = e;
    this.direction = d;
  }
  

  public float totalFrames(){
        return getSum(stepIndice);
  }

  public void recomputeNSeg(){
    n = table.getRowCount();
    segment = (canvasW - canvas_margin_left - canvas_margin_right) / float(n);
  }

  protected void init() { // init step0, step, stepIndex stepMax stepIndexMax ...
        if(direction == 1){
            step = 0;
            stepIndex = 0;
            stepIndexMax = stepIndice[step];
            stepCounter = 0;
        }else{
            step = stepMax;
            stepIndex = stepIndice[step];
            stepIndexMax = stepIndice[step];
            stepCounter = 0;
        }

    }

  public boolean isEnd(){
      if (direction == 1){ 
          return (step == stepMax) && (stepCounter == stepIndexMax);
      }else{
          return (step == 0) && (stepCounter == stepIndexMax);
      }
  }  

  public String toString(){
    return "start from " + start + ", end in "+ end;

  } 

  public String getStatus(){
    return step + "/" + stepMax + ", " + stepIndex + "/" + stepIndexMax + ", " + stepCounter + ", d " + direction;
  }

  public void next() {
    String str = "step" + step;
    int colCount =  table.getColumnCount() - 1;
    if((step < stepBeforeFill) || (stepBeforeFill < 0)){
       // good
    }else if(step >= stepBeforeFill && (step - (stepBeforeFill + colCount) <= 0)){
      str = "step";
    }else{
      str = "step" + (assumpDim + (step - stepBeforeFill - colCount));
    }
    try {
      Method m = this.getClass().getMethod(str, null);
      m.invoke(this, null);
    }catch(Exception e) {
      e.printStackTrace();      
    }
  }

   protected void drawAxis(float f){ 
            int n = table.getRowCount();
            float minNumber = 0;// getColMin(table, defaultCol);
            float maxNumber = getColMax(table, defaultCol);
            float numStep = maxNumber / numTickets;

            strokeWeight(1);
            fill(0);
            stroke(0);
         // x axis
            line(canvasXScale(0)
                , canvasYScale(0)
                , canvasXScale((canvasW - canvas_margin_left - canvas_margin_right) * f)
                , canvasYScale(0));
            
            for(int i = 0 ; i < n; i++){
                 float curX = (i + 0.5) / n * (canvasW - canvas_margin_right - canvas_margin_left);
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

    protected void drawXAxis(float f){ 
            int n = table.getRowCount();
            float minNumber = 0;// getColMin(table, defaultCol);
            float maxNumber = getColMax(table, defaultCol);
            float numStep = maxNumber / numTickets;

            strokeWeight(1);
            fill(0);
            stroke(0);
         // x axis
            line(canvasXScale(0)
                , canvasYScale(0)
                , canvasXScale((canvasW - canvas_margin_left - canvas_margin_right) * f)
                , canvasYScale(0));
            
            for(int i = 0 ; i < n; i++){
                 float curX = (i + 0.5) / n * (canvasW - canvas_margin_right - canvas_margin_left);
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

            fill(255);
            noStroke();
            rectMode(CORNER);
            rect((canvasWr + canvas_margin_right) * f, canvasYScale(0)
              , (canvasWr + canvas_margin_right), canvas_margin_bottom);
            //rect(canvasXScale(0) - canvas_margin_left, canvasYScale(canvasHr), canvas_margin_left + 1, canvasHr * (1 - f));      
    }

    protected void drawStackAxis(float f){ 
            int n = table.getRowCount();
            float minNumber = 0;// getColMin(table, defaultCol);
            float maxNumber = getColMax(table, defaultCol);
            float allMaxNumber = getRowSumMax(table);
            float numStep = allMaxNumber / numTickets;

            strokeWeight(1);
            fill(0);
            stroke(0);
         // x axis
            line(canvasXScale(0)
                , canvasYScale(0)
                , canvasXScale((canvasW - canvas_margin_left - canvas_margin_right) * f)
                , canvasYScale(0));
            
            for(int i = 0 ; i < n; i++){
                 float curX = (i + 0.5) / n * (canvasW - canvas_margin_right - canvas_margin_left);
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


    protected void drawStackedAxis(float f, float yOffSet) {
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
            float tmpY = i / float(numTickets) * canvasHr;
            if (yOffSet > 0) {
                tmpY = lerp(tmpY, tmpY / 2.0 + canvasHr / 2.0, yOffSet);
            }
            tmpY = canvasYScale(tmpY);
            float curStep = lerp(numStep, numStep / 2.0, yOffSet);
            String str = nfc(curStep * i , 1);
            stroke(0);
            line(tmpX, tmpY, tmpX - 5, tmpY);
            fill(0);
            textAlign(RIGHT);
            text(str, tmpX - 7, tmpY + fontSize / 2.0);
        }
    }

    protected void drawStackedYPldAxis(float f) {
        int n = table.getRowCount();
        int curStep = int (f / (1.0 / numTickets));
        float allMaxNumber = getRowSumMax(table);
        float numStep = allMaxNumber / numTickets;
        pushStyle();
        strokeWeight(1);
        fill(0);
        stroke(0);
        float tmpX = canvasXScale(0) + canvasWr / 2.0;

        for (int i = 0; i < curStep; i++) {     
            float tmpY = i / float(numTickets) * canvasHr / 2.0 + canvasHr / 2.0;
            tmpY = canvasYScale(tmpY);
            String str = nfc(numStep * i , 1);
            stroke(0);
            line(tmpX, tmpY, tmpX - 5, tmpY);
            fill(0);
            textAlign(RIGHT);
            text(str, tmpX - 7, tmpY + fontSize / 2.0);
            
            noFill();
            stroke(0, 180);
            strokeWeight(0.3);
            ellipseMode(RADIUS);
            ellipse(canvasXScale(canvasWr / 2.0), canvasYScale(canvasHr / 2.0), tmpY - canvasYScale(canvasHr / 2.0), tmpY - canvasYScale(canvasHr / 2.0));

        }
        strokeWeight(0.5);
        stroke(0, 190);
        line(tmpX, canvasYScale(canvasHr / 2.0), tmpX, canvasYScale(canvasHr));
        popStyle();
    }

    
    protected void drawLegend(float f) {
        int n = table.getColumnCount() - 1; // the first one doesn't count
        float rectH = (canvasWr / 50 > fontSize) ? (canvasWr / 50) : fontSize;
        float curSegment = canvasHr / n;
        int curStop = int(f / (1.0 / n)) + 1;
        String[] header = table.getColumnTitles();
        pushStyle();
        for (int i = 1, j = curStop; i <= n; i++) {
          if(i - curStop <= -1){
            float tmpY = canvas_margin_top + 2 * rectH * i;
            noStroke();
            color c = lerpColor(colors[0], colors[1], float(i - 1) / n);        
            fill(c, f * 255);
            rectMode(CENTER);
            rect(canvasXScale(canvasWr) + rectH, tmpY, rectH, rectH);

            strokeWeight(1);
            fill(color(0, f * 255));
            stroke(0);
            textAlign(LEFT);
            text(header[i], canvasXScale(canvasWr) + rectH * 2, tmpY + fontSize / 2.0);
          }
        }
        popStyle();
    }

    protected void smoothyThread(float f) {
        int colNum = table.getColumnCount();
        float minNumber = 0;
        float allMax = getRowSumMax(table);
        int numPixel = int(n * segment);
        float stopPixel = f * numPixel + 0.5 * segment;

        for (int j = colNum - 1; j >= defaultCol; j--) {
            float[] nums = table.getFloatColumn(j);
            strokeWeight(1);
            stroke(lerpColor(colors[0], colors[1], float(j - 1) / colNum));
            fill(lerpColor(colors[0], colors[1], float(j - 1) / colNum));
            float lStart = stopPixel;
            int nLStart = int(f * numPixel) / int(segment);

                beginShape();
                for (int i = nLStart; i < n; i++) {
                    float x1 = ((i + 0.5) * segment);
                    float y1 = (getSubRowSum(table, i, defaultCol, j)) / allMax * (canvasHr);
                    float margin_top_bottom = (allMax - getRowSum(table, i)) / allMax * canvasHr / 2.0;
                    float curY = y1 + margin_top_bottom;
                    vertex(canvasXScale(x1), canvasYScale(curY));
                }

                for (int i = n - 1; i >= nLStart; i--) {
                    float x1 = ((i + 0.5) * segment);
                    float y1 = (getSubRowSum(table, i, defaultCol, j)) / allMax * (canvasHr);
                    float margin_top_bottom = (allMax - getRowSum(table, i)) / allMax * canvasHr / 2.0;
                    vertex(canvasXScale(x1), canvasYScale(margin_top_bottom));
                }
                endShape(CLOSE);
            
                for (int i = 0; i < nLStart; i++) {
                    stroke(lerpColor(colors[0], colors[1], float(j - 1) / colNum));
                    fill(lerpColor(colors[0], colors[1], float(j - 1) / colNum));
                    if (i < n - 1) {
                        float px1 = canvasXScale((i + 0.5) * segment);
                        float cy1 = (getSubRowSum(table, i, defaultCol, j)) / allMax * (canvasHr);
                        float margin_top_bottom1 = (allMax - getRowSum(table, i)) / allMax * canvasHr / 2.0;
                        float py1 = canvasYScale(cy1 + margin_top_bottom1);

                        float bmargin_top_bottom1 = (allMax - getRowSum(table, i)) / allMax * canvasHr / 2.0;
                        float bpy1 = canvasYScale(bmargin_top_bottom1);

                        float px2 = canvasXScale((i + 1.5) * segment);
                        float cy2 = (getSubRowSum(table, i + 1, defaultCol, j)) / allMax * (canvasHr);
                        float margin_top_bottom2 = (allMax - getRowSum(table, i + 1)) / allMax * canvasHr / 2.0;
                        float py2 = canvasYScale(cy2 + margin_top_bottom2);

                        float bmargin_top_bottom2 = (allMax - getRowSum(table, i + 1)) / allMax * canvasHr / 2.0;
                        float bpy2 = canvasYScale(bmargin_top_bottom2);
                        strokeWeight(1);
                        strokeJoin(SQUARE);
                        beginShape();
                        for (float pixel = px1; pixel - px2 < 0; pixel++) {
                            float pxlx1 = pixel;
                            float pxly1 = zeroSpline((pixel - px1) / (segment), py1, py2);
                            vertex(pxlx1, pxly1);
                        }

                        for (float pixel = px2; pixel - px1 > 0; pixel--) {
                            float pxlx1 = pixel;
                            float pxly1 = zeroSpline((pixel - px1 ) / (segment), bpy1, bpy2);
                            vertex(pxlx1, pxly1);
                        }
                        endShape(CLOSE);
                    }
                }
        }
    }

    protected void drawPlXLabelUniform(float f) {
        float minNumber = 0;
        float maxNumber = getColMax(table, defaultCol);
        float[] nums = table.getFloatColumn(defaultCol);
        int colNum = nums.length;

        float floatSegment = 1.0 / colNum;
        int curIndex = round(f / floatSegment);

        float radarRadius = canvasHr / 2.0;
        fill(color(0));
        stroke(color(0));
        strokeWeight(1);

        for (int i = 0; i < curIndex; i++) {
            float x1 = canvasXScale(((i + 0.5) * segment));
            float y1 = ((nums[i % colNum] - minNumber) / maxNumber * (canvasHr));
            y1 = canvasYScale(y1);

            float ang = (i + 0.5) / n * TWO_PI + PI / 2.0;

            float ty1 = canvasYScale(cos(ang) * (fontSize + radarRadius) + radarRadius);
            float tx1 = canvasXScale(canvasWr / 2.0 + sin(ang) * (fontSize + radarRadius));

            float lx1 = tx1;
            float ly1 = ty1;

            pushMatrix();
            translate(lx1, ly1);
            rotate(ang);
            text(table.getStringColumn(0)[i], 0, 0);
            popMatrix();
        }
    }

    protected void drawPlXLabelPerc(float f) {
        float minNumber = 0;
        float maxNumber = getColMax(table, defaultCol);
        float[] nums = table.getFloatColumn(defaultCol);
        float sum = getColSum(table, defaultCol);
        int colNum = nums.length;

        float floatSegment = 1.0 / colNum;
        int curIndex = round(f / floatSegment);

        float radarRadius = canvasHr / 2.0;
        fill(color(0));
        stroke(color(0));
        strokeWeight(1);

        float curSum = 0;

        for (int i = 0; i < curIndex; i++) {
            float x1 = canvasXScale(((i + 0.5) * segment));
            float y1 = ((nums[i % colNum] - minNumber) / maxNumber * (canvasHr));
            y1 = canvasYScale(y1);

            float ang = (i + 0.5) / n * TWO_PI + PI / 2.0;

            float targetStartArc = curSum / sum * TWO_PI;
            float targetEndArc = ((curSum + nums[i]) / sum * TWO_PI) > TWO_PI ? TWO_PI : ((curSum + nums[i]) / sum * TWO_PI);
            
            ang = (targetStartArc + targetEndArc) * 0.5 + PI / 2.0;
            
            float ty1 = canvasYScale(cos(ang) * (fontSize + radarRadius) + radarRadius);
            float tx1 = canvasXScale(canvasWr / 2.0 + sin(ang) * (fontSize + radarRadius));
            
            float lx1 = tx1;
            float ly1 = ty1;

            pushMatrix();
            translate(lx1, ly1);
            rotate(ang);
            text(table.getStringColumn(0)[i], 0, 0);
            popMatrix();

            curSum += nums[i];
        }
    }


    protected void nextFrame(){
        if(stepCounter == stepIndice[step] && !isEnd()){
            step += direction;
            stepIndexMax = stepIndice[step];
            stepIndex = (direction == -1) ? stepIndexMax : 0;
            stepCounter = 0;
        }else if(!isEnd()){
            stepCounter++;
            stepIndex += direction;
        }else{
            ;
        }
    }
}

