import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.lang.reflect.*; 
import java.lang.reflect.*; 
import java.lang.String; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Animation_Transitions_v8 extends PApplet {

/*
 * This is the main class of control
 */
 
int fontSize = 12;
int canvasX, canvasY, canvasW, canvasH;
float canvasWr, canvasHr;
int choiceW, choiceH, pathW, pathH, buttonW, buttonH = -1;
int choiceX, choiceY, pathX, pathY, buttonX, buttonY;
float canvas_margin_top, canvas_margin_bottom, canvas_margin_left, canvas_margin_right;

boolean mouseDraggedBl = false;
boolean mouseReleasedBl = false;
boolean mouseClickedBl = false;
boolean mousePressedBl = false;
Object main = null;

public void setup(){
	size((int)(displayWidth * 0.8f), PApplet.parseInt(displayHeight * 0.9f));//, "processing.core.PGraphicsRetina2D");
	rectMode(CORNER);
	
	frame.setResizable(true);
	frame.setTitle("Fumeng's Animation Transition");

	eventHandler = new EventHandler();
	initVisInfo();
	loadData();
	frameRate(50);

	main = this;
}

public void setParameters(){
	fontSize = width / 80;
	pointSize = width < height ? width / 70 : height / 70;
    textSize(fontSize);	
	canvasX = 0;
	canvasY = 0;
	canvasW = width;
	canvasH = PApplet.parseInt(height * 0.75f);

	choiceX = 0;
	choiceY = canvasH;
	choiceW = PApplet.parseInt(0.27f * width);
	choiceH = PApplet.parseInt(height - canvasH);

	pathX = choiceW;
	pathY = canvasH;
	pathW = PApplet.parseInt(0.63f * width);
	pathH = choiceH;

	buttonX = choiceW + pathW;
	buttonY= canvasH;
	buttonW = PApplet.parseInt(width - choiceW - pathW);
	buttonH = choiceH;

	canvas_margin_top = 0.1f * PApplet.parseFloat(canvasH);
	canvas_margin_bottom = 0.15f * PApplet.parseFloat(canvasH);
	canvas_margin_left = 0.1f * PApplet.parseFloat(canvasW);
	canvas_margin_right = 0.1f * PApplet.parseFloat(canvasW);

	canvasWr = canvasW - canvas_margin_right - canvas_margin_left;
	canvasHr = canvasH - canvas_margin_bottom - canvas_margin_top;

	MAX_RADIUS = canvasHr / 8.0f;
}

public void draw(){

    setParameters();
    drawPath();
    drawCanvas();
    drawChoices();
    drawButtons();
    drawWarning();
     if(curAT != null){
    	//println(curAT.getStatus());
    }
}


public class AnimationPath{
    private String start = null;
	private String end = null;
	private AnimationPathNode curNode = null;
    private int curNodeIndex = -1;
    private ArrayList<AnimationPathNode> apns = null;
    private StringList allNames = null;

    public AnimationPath(){
    	apns = new ArrayList<AnimationPathNode>();
        allNames = new StringList();
    }

    public boolean buildAnimTransPath(StringList names){
    	if(names.size() < 2){
            warningText = "Not enough visualizations!";      
            println("not enough!");
            return false;
    	}
        apns.clear();
        allNames.clear();
    	this.start = names.get(0);
    	this.end = names.get(names.size() - 1);
        
        for(int i = 0 ;i < names.size(); i++){
            if(i != names.size() - 1){
            	String s = names.get(i);
            	String e = names.get(i + 1);
            	AnimationPathNode apn = new AnimationPathNode(s, e);
            	apn.interpret();
                apns.add(apn);
                allNames.append(apn.getNames().values());
            }
        }
        warningText = "Sucessfully interpreted!";
        println("succeed interpreting! ");
        return true;
    }

    public float totalFrames(){
        float sum = 0;
        for(AnimationPathNode apn : apns){
            sum += apn.totalFrames();
        }
        return sum;
    }

    public boolean hasNextNode(){
    	return curNodeIndex < (apns.size() - 1);
    }

    public void nextNode(){
        curNodeIndex++;
        curNode = apns.get(curNodeIndex);
    }

    public boolean hasNextAT(){
    	return (curNode.hasNextAT() || hasNextNode());
    }

    public AtomicTransition nextAT(){
        if(curNode == null || (!curNode.hasNextAT() && hasNextNode())){
            nextNode();
        }     
        return curNode.nextAT();
    }

    public StringList getNames(){
        return this.allNames;
    }

// ---------- begin new class ----------- //
// ---------- begin new class ----------- //
// ---------- begin new class ----------- //
// ---------- begin new class ----------- //

     private class AnimationPathNode{
        private String start = null;
        private String end = null;
        private int atIndex = -1;
        private AtomicTransition curAT = null;
        ArrayList<AtomicTransition> ats = null;
        private StringList names = null;
    
        public AnimationPathNode(String s, String e){
            this.start = s;
            this.end = e;
            ats = new ArrayList<AtomicTransition>();
            names = new StringList();
        }

        public int size(){
            return ats.size();
        }

        public float totalFrames(){
            float sum = 0;
            for(AtomicTransition at : ats){
                 sum += at.totalFrames();
            }
            return sum;
        }

        public void interpret(){
            if(this.start == null || this.end == null){
                return;
            }
            StringList nb = (StringList)visInfo.get(start);
            names.clear();
            if(nb.hasValue(end)){
                AtomicTransition at = constructAT(start, end); 
                ats.add(at); 
                names.append(start);   
                names.append(end);               
            }else{
                String s1 = start;
                String e1 = nb.get(0);
                String s2 = e1;
                String e2 = end;
                
                AtomicTransition at1 = constructAT(s1, e1); 
                AtomicTransition at2 = constructAT(s2, e2); 
                ats.add(at1); 
                ats.add(at2);
                names.append(start); 
                names.append(e1);   
                names.append(end);  
            }
            return;
        }
        public boolean hasNextAT(){
             return atIndex < ats.size() - 1;
        }

        public AtomicTransition nextAT(){
            atIndex++;
            AtomicTransition at = ats.get(atIndex);
            return at;
        }

        private AtomicTransition constructAT(String start, String end){
        AtomicTransition at = null;
        try{
            int sCharIndex = (int)start.charAt(0);
            int eCharIndex = (int)end.charAt(0);
            if(sCharIndex < eCharIndex){
                String className = curProjName + "$" + mapToStandardName(start) + "_" + mapToStandardName(end) + "_" + "AT";
                Class c = Class.forName(className);
                Constructor cons = c.getConstructor(Class.forName(curProjName)
                            , java.lang.String.class, java.lang.String.class, int.class);
                at = (AtomicTransition) cons.newInstance(main, start, end, 1);

            }else{
                String className = curProjName + "$" + mapToStandardName(end) + "_" + mapToStandardName(start) + "_" + "AT";
                Class c = Class.forName(className);
                Constructor cons = c.getConstructor(Class.forName(curProjName)
                            , java.lang.String.class, java.lang.String.class, int.class);
                at = (AtomicTransition) cons.newInstance(main, start, end, -1);
            }
            }catch (Exception e){
                e.printStackTrace();
            }
            return at;
    }
    private StringList getNames() {
        return names;
    }
    } // end

}


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
    segment = (canvasW - canvas_margin_left - canvas_margin_right) / PApplet.parseFloat(n);
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
                 float curX = (i + 0.5f) / n * (canvasW - canvas_margin_right - canvas_margin_left);
                 float tmpY = canvasYScale(0) + textWidth(table.getStringColumn(0)[i]) / 2.0f + 7;
                 stroke(0);
                 line(canvasXScale(curX), canvasYScale(0), canvasXScale(curX), canvasYScale(0) + 5);
                 pushMatrix();
                 translate(canvasXScale(curX + fontSize / 2.0f), tmpY);
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
                 float tmpY = canvasYScale(i / PApplet.parseFloat(numTickets) * canvasHr);             
                 String str = nfc(numStep * i, 1);
                 stroke(0);
                 line(tmpX, tmpY, tmpX - 5, tmpY);
                 fill(0);
                 textAlign(RIGHT);
                 text(str, tmpX - 7, tmpY + fontSize / 2.0f);
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
                 float curX = (i + 0.5f) / n * (canvasW - canvas_margin_right - canvas_margin_left);
                 float tmpY = canvasYScale(0) + textWidth(table.getStringColumn(0)[i]) / 2.0f + 7;
                 stroke(0);
                 line(canvasXScale(curX), canvasYScale(0), canvasXScale(curX), canvasYScale(0) + 5);
                 pushMatrix();
                 translate(canvasXScale(curX + fontSize / 2.0f), tmpY);
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
                 float curX = (i + 0.5f) / n * (canvasW - canvas_margin_right - canvas_margin_left);
                 float tmpY = canvasYScale(0) + textWidth(table.getStringColumn(0)[i]) / 2.0f + 7;
                 stroke(0);
                 line(canvasXScale(curX), canvasYScale(0), canvasXScale(curX), canvasYScale(0) + 5);
                 pushMatrix();
                 translate(canvasXScale(curX + fontSize / 2.0f), tmpY);
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
                 float tmpY = canvasYScale(i / PApplet.parseFloat(numTickets) * canvasHr);             
                 String str = nfc(numStep * i, 1);
                 stroke(0);
                 line(tmpX, tmpY, tmpX - 5, tmpY);
                 fill(0);
                 textAlign(RIGHT);
                 text(str, tmpX - 7, tmpY + fontSize / 2.0f);
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
            float curX = (i + 0.5f) / n * (canvasWr);
            float tmpY = canvasYScale(0) + textWidth(table.getStringColumn(0)[i]) / 2.0f + 7;
            stroke(0);
            line(canvasXScale(curX), canvasYScale(0), canvasXScale(curX), canvasYScale(0) + 5);
            pushMatrix();
            translate(canvasXScale(curX + fontSize / 2.0f), tmpY);
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
            float tmpY = i / PApplet.parseFloat(numTickets) * canvasHr;
            if (yOffSet > 0) {
                tmpY = lerp(tmpY, tmpY / 2.0f + canvasHr / 2.0f, yOffSet);
            }
            tmpY = canvasYScale(tmpY);
            float curStep = lerp(numStep, numStep / 2.0f, yOffSet);
            String str = nfc(curStep * i , 1);
            stroke(0);
            line(tmpX, tmpY, tmpX - 5, tmpY);
            fill(0);
            textAlign(RIGHT);
            text(str, tmpX - 7, tmpY + fontSize / 2.0f);
        }
    }

    protected void drawStackedYPldAxis(float f) {
        int n = table.getRowCount();
        int curStep = PApplet.parseInt (f / (1.0f / numTickets));
        float allMaxNumber = getRowSumMax(table);
        float numStep = allMaxNumber / numTickets;
        pushStyle();
        strokeWeight(1);
        fill(0);
        stroke(0);
        float tmpX = canvasXScale(0) + canvasWr / 2.0f;

        for (int i = 0; i < curStep; i++) {     
            float tmpY = i / PApplet.parseFloat(numTickets) * canvasHr / 2.0f + canvasHr / 2.0f;
            tmpY = canvasYScale(tmpY);
            String str = nfc(numStep * i , 1);
            stroke(0);
            line(tmpX, tmpY, tmpX - 5, tmpY);
            fill(0);
            textAlign(RIGHT);
            text(str, tmpX - 7, tmpY + fontSize / 2.0f);
            
            noFill();
            stroke(0, 180);
            strokeWeight(0.3f);
            ellipseMode(RADIUS);
            ellipse(canvasXScale(canvasWr / 2.0f), canvasYScale(canvasHr / 2.0f), tmpY - canvasYScale(canvasHr / 2.0f), tmpY - canvasYScale(canvasHr / 2.0f));

        }
        strokeWeight(0.5f);
        stroke(0, 190);
        line(tmpX, canvasYScale(canvasHr / 2.0f), tmpX, canvasYScale(canvasHr));
        popStyle();
    }

    
    protected void drawLegend(float f) {
        int n = table.getColumnCount() - 1; // the first one doesn't count
        float rectH = (canvasWr / 50 > fontSize) ? (canvasWr / 50) : fontSize;
        float curSegment = canvasHr / n;
        int curStop = PApplet.parseInt(f / (1.0f / n)) + 1;
        String[] header = table.getColumnTitles();
        pushStyle();
        for (int i = 1, j = curStop; i <= n; i++) {
          if(i - curStop <= -1){
            float tmpY = canvas_margin_top + 2 * rectH * i;
            noStroke();
            int c = lerpColor(colors[0], colors[1], PApplet.parseFloat(i - 1) / n);        
            fill(c, f * 255);
            rectMode(CENTER);
            rect(canvasXScale(canvasWr) + rectH, tmpY, rectH, rectH);

            strokeWeight(1);
            fill(color(0, f * 255));
            stroke(0);
            textAlign(LEFT);
            text(header[i], canvasXScale(canvasWr) + rectH * 2, tmpY + fontSize / 2.0f);
          }
        }
        popStyle();
    }

    protected void smoothyThread(float f) {
        int colNum = table.getColumnCount();
        float minNumber = 0;
        float allMax = getRowSumMax(table);
        int numPixel = PApplet.parseInt(n * segment);
        float stopPixel = f * numPixel + 0.5f * segment;

        for (int j = colNum - 1; j >= defaultCol; j--) {
            float[] nums = table.getFloatColumn(j);
            strokeWeight(1);
            stroke(lerpColor(colors[0], colors[1], PApplet.parseFloat(j - 1) / colNum));
            fill(lerpColor(colors[0], colors[1], PApplet.parseFloat(j - 1) / colNum));
            float lStart = stopPixel;
            int nLStart = PApplet.parseInt(f * numPixel) / PApplet.parseInt(segment);

                beginShape();
                for (int i = nLStart; i < n; i++) {
                    float x1 = ((i + 0.5f) * segment);
                    float y1 = (getSubRowSum(table, i, defaultCol, j)) / allMax * (canvasHr);
                    float margin_top_bottom = (allMax - getRowSum(table, i)) / allMax * canvasHr / 2.0f;
                    float curY = y1 + margin_top_bottom;
                    vertex(canvasXScale(x1), canvasYScale(curY));
                }

                for (int i = n - 1; i >= nLStart; i--) {
                    float x1 = ((i + 0.5f) * segment);
                    float y1 = (getSubRowSum(table, i, defaultCol, j)) / allMax * (canvasHr);
                    float margin_top_bottom = (allMax - getRowSum(table, i)) / allMax * canvasHr / 2.0f;
                    vertex(canvasXScale(x1), canvasYScale(margin_top_bottom));
                }
                endShape(CLOSE);
            
                for (int i = 0; i < nLStart; i++) {
                    stroke(lerpColor(colors[0], colors[1], PApplet.parseFloat(j - 1) / colNum));
                    fill(lerpColor(colors[0], colors[1], PApplet.parseFloat(j - 1) / colNum));
                    if (i < n - 1) {
                        float px1 = canvasXScale((i + 0.5f) * segment);
                        float cy1 = (getSubRowSum(table, i, defaultCol, j)) / allMax * (canvasHr);
                        float margin_top_bottom1 = (allMax - getRowSum(table, i)) / allMax * canvasHr / 2.0f;
                        float py1 = canvasYScale(cy1 + margin_top_bottom1);

                        float bmargin_top_bottom1 = (allMax - getRowSum(table, i)) / allMax * canvasHr / 2.0f;
                        float bpy1 = canvasYScale(bmargin_top_bottom1);

                        float px2 = canvasXScale((i + 1.5f) * segment);
                        float cy2 = (getSubRowSum(table, i + 1, defaultCol, j)) / allMax * (canvasHr);
                        float margin_top_bottom2 = (allMax - getRowSum(table, i + 1)) / allMax * canvasHr / 2.0f;
                        float py2 = canvasYScale(cy2 + margin_top_bottom2);

                        float bmargin_top_bottom2 = (allMax - getRowSum(table, i + 1)) / allMax * canvasHr / 2.0f;
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

        float floatSegment = 1.0f / colNum;
        int curIndex = round(f / floatSegment);

        float radarRadius = canvasHr / 2.0f;
        fill(color(0));
        stroke(color(0));
        strokeWeight(1);

        for (int i = 0; i < curIndex; i++) {
            float x1 = canvasXScale(((i + 0.5f) * segment));
            float y1 = ((nums[i % colNum] - minNumber) / maxNumber * (canvasHr));
            y1 = canvasYScale(y1);

            float ang = (i + 0.5f) / n * TWO_PI + PI / 2.0f;

            float ty1 = canvasYScale(cos(ang) * (fontSize + radarRadius) + radarRadius);
            float tx1 = canvasXScale(canvasWr / 2.0f + sin(ang) * (fontSize + radarRadius));

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

        float floatSegment = 1.0f / colNum;
        int curIndex = round(f / floatSegment);

        float radarRadius = canvasHr / 2.0f;
        fill(color(0));
        stroke(color(0));
        strokeWeight(1);

        float curSum = 0;

        for (int i = 0; i < curIndex; i++) {
            float x1 = canvasXScale(((i + 0.5f) * segment));
            float y1 = ((nums[i % colNum] - minNumber) / maxNumber * (canvasHr));
            y1 = canvasYScale(y1);

            float ang = (i + 0.5f) / n * TWO_PI + PI / 2.0f;

            float targetStartArc = curSum / sum * TWO_PI;
            float targetEndArc = ((curSum + nums[i]) / sum * TWO_PI) > TWO_PI ? TWO_PI : ((curSum + nums[i]) / sum * TWO_PI);
            
            ang = (targetStartArc + targetEndArc) * 0.5f + PI / 2.0f;
            
            float ty1 = canvasYScale(cos(ang) * (fontSize + radarRadius) + radarRadius);
            float tx1 = canvasXScale(canvasWr / 2.0f + sin(ang) * (fontSize + radarRadius));
            
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
        drawBars(1 - stepIndex / PApplet.parseFloat(stepIndexMax));
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
        drawHorizonBars(1 - stepIndex / PApplet.parseFloat(stepIndexMax));   
        nextFrame();         
    }

// into scatterplot
    public void step2(){
        recomputeNSeg();
        drawAxis(1);
        drawLines(0);
        drawPoints(stepIndex / PApplet.parseFloat(stepIndexMax));
        drawHorizonBars(0);
        drawBars(0);
        nextFrame();  
    }
    
// line connect
    public void step3(){     
        recomputeNSeg();
        drawAxis(1);
        drawLines(stepIndex / PApplet.parseFloat(stepIndexMax));
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
                float x1 = ((i + 0.5f) * segment);
                float x2 = ((i + 1 + 0.5f) * segment);
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
            float x1 = ((i + 0.5f) * segment);
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
                float x1 = ((i + 0.5f) * segment);
                float y1 = ((nums[i] - minNumber) / maxNumber * (canvasHr));
                float barW = 0.5f * segment * f;
                if(abs(y1 - 0) < 0.1f && (f > 0.999f)){
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
                float x1 = ((i + 0.5f) * segment) - 0.25f * segment;
                float y1 = ((nums[i] - minNumber) / maxNumber * (canvasHr));
                float barW = 0.5f * segment;
                float barH = f * y1;
                noStroke();
          
                rectMode(CORNER);
                fill(colors[0]);
                rect(canvasXScale(x1), canvasYScale(y1), barW, barH);
         }
    }

}
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
        drawBars(PApplet.parseFloat(stepIndex) / stepIndexMax);
    	nextFrame(); 
    }

// axis dispear
    public void step1(){
    	recomputeNSeg();
        drawAxis(1 - PApplet.parseFloat(stepIndex) / stepIndexMax);
        drawBars(1);
    	nextFrame(); 
    }

// get stacked
    public void step2(){
    	recomputeNSeg();
    	float f = PApplet.parseFloat(stepIndex) / (stepIndexMax * 0.85f);
    	if(f > 1)
    		f = 1;
        drawStacked(f, 0);
    	nextFrame(); 
    }

    public void step3(){
        recomputeNSeg();
        float f = PApplet.parseFloat(stepIndex) / (stepIndexMax * 0.85f);
        if(f > 1)
            f = 1;
        drawStacked(1, f);
        nextFrame(); 
    }


    public void step4(){
    	recomputeNSeg();    
    	drawArc(PApplet.parseFloat(stepIndex) / stepIndexMax);
    	nextFrame(); 
    }

    public void step5(){
        recomputeNSeg();    
        drawArc(1);
        drawPlXLabelPerc(PApplet.parseFloat(stepIndex) / stepIndexMax);
        nextFrame(); 
    }

    private void drawArc(float f){
    	float minNumber = 0;// getColMin(table, defaultCol);
        float maxNumber = getColMax(table, defaultCol);
        float sum = getColSum(table, defaultCol);
        float curSum = 0;
        float[] nums = table.getFloatColumn(defaultCol);

        for(int i = 0; i < n; i++){
        	    float x1 = 0.5f * segment;
                float y1 = curSum / sum * canvasHr;

                float barW = segment;
                float barH = nums[i] / sum * canvasHr;             
                
                pushStyle();

                float targetW = canvasHr / 2.0f;
                strokeWeight(lerp(barW, targetW, f));
               
                noFill();

                stroke(lerpColor(colors[0], colors[1], PApplet.parseFloat(i) / n));
                strokeCap(SQUARE);

                ellipseMode(RADIUS);

                float curRadius = lerp(MAX_RADIUS, canvasHr / 4.0f, f);

                float arcDelta = asin((barH / 2.0f) / curRadius);
                float degreeDelta = (nums[i] / sum) > 1 ? (nums[i] / sum - 1) : (nums[i] / sum);
                float targetDelta = asin(degreeDelta);

                float targetStartArc = curSum / sum * TWO_PI;
                float targetEndArc = ((curSum + nums[i]) / sum * TWO_PI) > TWO_PI ? TWO_PI : ((curSum + nums[i]) / sum * TWO_PI);
                
                float curStartArc = lerp(PI - lerp(arcDelta, targetDelta, f), targetStartArc, f);
                float curEndArc = lerp(PI + lerp(arcDelta, targetDelta, f), targetEndArc, f);

                float arcX = lerp(canvasXScale(x1) + barW + curRadius, canvasXScale(canvasWr / 2.0f), f);
                float arcY = lerp(canvasYScale(y1), canvasYScale(canvasHr / 2.0f), f);
     
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
                float preX = ((i + 0.25f) * segment);
                float preY = ((nums[i] - minNumber) / maxNumber * (canvasHr));
                float x1 = lerp(preX, 0.5f * segment , c);
                float y1 = lerp(preY, curSum / sum * canvasHr, f);
                float barW = 0.5f * segment + 0.5f * segment * f;
                float barH = lerp(preY, nums[i] / sum * canvasHr, f);
          
                rectMode(CORNER);
                strokeWeight(1);
                stroke(lerpColor(colors[0], colors[1], PApplet.parseFloat(i) / n));
                fill(lerpColor(colors[0], colors[1], PApplet.parseFloat(i) / n));
                rect(canvasXScale(x1), canvasYScale(y1), barW, barH);
         }
    }

    private void drawBars(float f){
        float minNumber = 0;// getColMin(table, defaultCol);
        float maxNumber = getColMax(table, defaultCol);
        float[] nums = table.getFloatColumn(defaultCol);

        for(int i = 0; i < n; i++){
                float x1 = ((i + 0.25f) * segment);
                float y1 = ((nums[i] - minNumber) / maxNumber * (canvasH - canvas_margin_bottom - canvas_margin_top));
                float barW = 0.5f * segment;
                float barH = y1;
                noStroke();
          
                rectMode(CORNER);
                if(step > 0)
                	fill(lerpColor(colors[0], colors[1], PApplet.parseFloat(i) / n));
                else if(i <= stepIndex)
                    fill(lerpColor(colors[0], colors[1], PApplet.parseFloat(i) / stepIndexMax));
                else
                	fill(colors[0]);

                rect(canvasXScale(x1), canvasYScale(y1), barW, barH);
         }
    }
}
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
        drawStackedAxis(PApplet.parseFloat(stepIndex) / stepIndexMax);
        drawFirstBars(PApplet.parseFloat(stepIndex) / stepIndexMax);
        nextFrame();
    }

    public void step() {
        recomputeNSeg();
        drawStackedAxis(1);
        drawFirstBarsReal();
        drawBars(PApplet.parseFloat(stepIndex) / stepIndexMax);
        nextFrame();
    }


public void step1001() {
        recomputeNSeg();
        drawStackedAxis(1);
        drawBars(1);
        drawLegend(PApplet.parseFloat(stepIndex) / stepIndexMax);
        nextFrame();
    }

    private void drawFirstBars(float f) {
        float minNumber = 0; // getColMin(table, defaultCol);
        float maxNumber = getColMax(table, defaultCol);
        float allMaxNum = getRowSumMax(table);
        float[] nums = table.getFloatColumn(step + 1);
        int colNum = table.getColumnCount();
        for (int i = 0; i < n; i++) {
            float x1 = ((i + 0.25f) * segment);
            float y1 = ((nums[i] - minNumber) / maxNumber * canvasHr);
            float barW = 0.5f * segment;
            float barH = y1;
            float tBarH = (nums[i] - minNumber) / allMaxNum * canvasHr;
            float tBarY = (getSubRowSum(table, i, defaultCol, defaultCol) - minNumber) / allMaxNum * canvasHr;

            rectMode(CORNER);
            strokeWeight(1);
            stroke(lerpColor(colors[0], colors[1], PApplet.parseFloat(0) / colNum));
            fill(lerpColor(colors[0], colors[1], PApplet.parseFloat(0) / colNum));

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
            float x1 = ((i + 0.25f) * segment);
            float barW = 0.5f * segment;
            float tBarH = (nums[i] - minNumber) / allMaxNum * canvasHr;
            float tBarY = (getSubRowSum(table, i, defaultCol, defaultCol) - minNumber) / allMaxNum * canvasHr;

            rectMode(CORNER);
            strokeWeight(1);
            fill(lerpColor(colors[0], colors[1], PApplet.parseFloat(0) / colNum));
            stroke(lerpColor(colors[0], colors[1], PApplet.parseFloat(0) / colNum));

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
                float x1 = ((i + 0.25f) * segment);
                if((j == realStep) && (j != defaultCol)){
                    x1 += 0.25f * segment * (1 - f);
                }
                float y1 = ((getSubRowSum(table, i, defaultCol, j) - minNumber) / maxNumber * canvasHr);
                float barH = rowRatioInside * canvasHr;
                float barW = 0.5f * segment;

                rectMode(CORNER);
                strokeWeight(1);
                fill(lerpColor(colors[0], colors[1], PApplet.parseFloat(j - 1) / colNum));
                stroke(lerpColor(colors[0], colors[1], PApplet.parseFloat(j - 1) / colNum));
                if((j == realStep) && (j != defaultCol)){
                     fill(lerpColor(colors[0], colors[1], PApplet.parseFloat(j - 1) / colNum), f * 255);
                     stroke(lerpColor(colors[0], colors[1], PApplet.parseFloat(j - 1) / colNum), f * 255);
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
            float curX = (i + 0.5f) / n * (canvasWr);
            float tmpY = canvasYScale(0) + textWidth(table.getStringColumn(0)[i]) / 2.0f + 7;
            stroke(0);
            line(canvasXScale(curX), canvasYScale(0), canvasXScale(curX), canvasYScale(0) + 5);
            pushMatrix();
            translate(canvasXScale(curX + fontSize / 2.0f), tmpY);
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
            float tmpY = canvasYScale(i / PApplet.parseFloat(numTickets) * canvasHr);
            String str = nfc(numStep * i, 1);
            stroke(0);
            line(tmpX, tmpY, tmpX - 5, tmpY);
            fill(0);
            textAlign(RIGHT);
            text(str, tmpX - 7, tmpY + fontSize / 2.0f);
        }
    }

}
public class EventHandler {
    public void remove() {
        if (path != null) {
            path.removeLast();
            transPath = null;
            curAT = null;
            transPath = null;
            totalStep = -1;
            curStep = -1;
            path.setWholeNameList(null);
        }
    }

    public void interpret() {
        if (transPath == null) {
            transPath = new AnimationPath();
        }
        if (transPath.buildAnimTransPath(path.getNodesNames())) {
            if (path != null) {
                path.setWholeNameList(transPath.getNames());
            }
            totalStep = transPath.totalFrames();
        }
    }
    
    public void begin() {
        if (transPath == null) {
            warningText = "Please interpret before you begin!";
            println("interpret before you begin");
            return;
        }
        curStep = 0;
        curAT = transPath.nextAT();
    }

    public void reset() {
        curAT = null;
        transPath = null;
        totalStep = -1;
        curStep = -1;
        path.reset();
    }

}

public void mouseClicked() {
    buttons.handleMouseClicked();;
}

public void mouseDragged() {
    choices.handleMouseDragged();
    path.handleMouseDragged();
}

public void mousePressed() {
    choices.handleMousePressed();
}

public void mouseReleased() {
    choices.handleMouseReleased();
    path.handleMouseReleased();
}

public void mouseMoved() {
    warningText = null;
}
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
        drawAxis(1 - PApplet.parseFloat(stepIndex) / stepIndexMax);
        drawPlAxis(0, PApplet.parseFloat(stepIndex) / stepIndexMax);
        drawPlLines(0);
        nextFrame();
    }
// change to radar chart
    public void step1() {
        recomputeNSeg();
        drawPlAxis(PApplet.parseFloat(stepIndex) / stepIndexMax, 1);
        drawPlLines(PApplet.parseFloat(stepIndex) / stepIndexMax);
        nextFrame();
    }
// adj angles
    public void step2() {
        recomputeNSeg();
        adjAnglesAxes(PApplet.parseFloat(stepIndex) / stepIndexMax);
        adjAnglesPoints(PApplet.parseFloat(stepIndex) / stepIndexMax);
        nextFrame();
    }
// adj points' radius
    public void step3() {
        recomputeNSeg();
        adjAnglesAxes(1);
        adjRadiusPoints(PApplet.parseFloat(stepIndex) / stepIndexMax, 1);
        nextFrame();
    }
// raise points
    public void step4() {
        recomputeNSeg();
        adjAnglesAxes(1);
        adjRadiusPoints(1, 1 - PApplet.parseFloat(stepIndex) / stepIndexMax);
        nextFrame();
    }
// fill in
    public void step5(){
        recomputeNSeg();
        adjAnglesAxes(1);
        adjRadiusPoints(1, 0);
        drawArc(PApplet.parseFloat(stepIndex) / stepIndexMax);
        nextFrame();
    }

    public void step6(){
        recomputeNSeg();
        drawArc(1);
        drawPlXLabelPerc(PApplet.parseFloat(stepIndex) / stepIndexMax);
        nextFrame();
    }

    private void drawPlAxis(float f, float c) {
        float minNumber = 0;
        float maxNumber = getColMax(table, defaultCol);
        float[] nums = table.getFloatColumn(defaultCol);
        int colNum = nums.length;
        float radarRadius = canvasHr / 2.0f;
        fill(color(0), c * 255);
        stroke(color(0), c * 255);
        strokeWeight(1);
        // cannot handle x = c
        int stopIndex = f > 0 ? (n + 1) : n;
        for (int i = 0; i < stopIndex; i++) {
            float yRadarRadius = canvasHr / 2.0f;

            float x1 = canvasXScale(((i + 0.5f) * segment));
            float y1 = ((nums[i % colNum] - minNumber) / maxNumber * (canvasHr));
            yRadarRadius = y1 / 2.0f;
            y1 = canvasYScale(y1);

            float tRadarRadius1 = lerp(yRadarRadius, radarRadius, f);

            float ty1 = canvasYScale(cos(PApplet.parseFloat(i) / n * TWO_PI + PI / 2.0f) * tRadarRadius1 + radarRadius);
            float tx1 = canvasXScale(canvasWr / 2.0f + sin(PApplet.parseFloat(i) / n * TWO_PI  + PI / 2.0f) * tRadarRadius1);

            float x2 = x1;
            float y2 = canvasYScale(0);
            float tx2 = canvasXScale(canvasWr / 2.0f);
            float ty2 = canvasYScale(canvasHr / 2.0f);

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
        float radarRadius = canvasHr / 2.0f;
        // cannot handle x = c
        int stopIndex = f > 0 ? (n + 1) : n;
        for (int i = 0; i < n; i++) {
            float lx1 = -1;
            float lx2 = -1;
            float ly1 = -1;
            float ly2 = -1;

            float yRadarRadius1 = -1;
            float x1 = canvasXScale(((i % colNum + 0.5f) * segment));
            float y1 = (nums[i % colNum] - minNumber) / maxNumber * (canvasHr);
            yRadarRadius1 = y1 / 2.0f;
            y1 = canvasYScale(y1);

            float ty1 = canvasYScale(cos(PApplet.parseFloat(i) / n * TWO_PI  + PI / 2.0f) * yRadarRadius1 + radarRadius);
            float tx1 = canvasXScale(canvasWr / 2.0f + sin(PApplet.parseFloat(i) / n * TWO_PI  + PI / 2.0f) * yRadarRadius1);

            float yRadarRadius2 = -1;
            float x2 = canvasXScale((((i + 1) % colNum + 0.5f) * segment));
            float y2 = (nums[(i + 1) % colNum] - minNumber) / maxNumber * (canvasHr);
            yRadarRadius2 = y2 / 2.0f;
            y2 = canvasYScale(y2);

            float ty2 = canvasYScale(cos(PApplet.parseFloat(i + 1) / n * TWO_PI  + PI / 2.0f) * yRadarRadius2 + radarRadius);
            float tx2 = canvasXScale(canvasWr / 2.0f + sin(PApplet.parseFloat(i + 1) / n * TWO_PI  + PI / 2.0f) * yRadarRadius2);

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
        float radarRadius = canvasHr / 2.0f;
        // cannot handle x = c   
        for (int i = 0; i < n; i++) {
            float lx1 = -1;
            float lx2 = -1;
            float ly1 = -1;
            float ly2 = -1;

            float yRadarRadius1 = -1;
            float x1 = canvasXScale(((i + 0.5f) * segment));
            float y1 = (nums[i] - minNumber) / maxNumber * (canvasHr);
            yRadarRadius1 = y1 / 2.0f;
            y1 = canvasYScale(y1);

            float ang1 = PApplet.parseFloat(i) / n * TWO_PI  + PI / 2.0f;
            float ang2 = getSubColSum(table, defaultCol, 0, i) / colSum * TWO_PI  + PI / 2.0f;
            float ang = lerp(ang1, ang2, f);

            float ty1 = canvasYScale(cos(ang) * yRadarRadius1 + radarRadius);
            float tx1 = canvasXScale(canvasWr / 2.0f + sin(ang) * yRadarRadius1);

            float yRadarRadius2, x2, y2, angg1, angg2, angg, tx2, ty2;
            if (i < n - 1) {
                x2 = canvasXScale((i + 1 + 0.5f) * segment);
                y2 = ((nums[i + 1] - minNumber) / maxNumber * (canvasHr));
                yRadarRadius2 = y2 / 2.0f;
                y2 = canvasYScale(y2);

                angg1 = PApplet.parseFloat(i + 1) / n * TWO_PI  + PI / 2.0f;
                angg2 = (getSubColSum(table, defaultCol, 0, i + 1)) / colSum * TWO_PI  + PI / 2.0f;

                angg = lerp(angg1, angg2, f);

                tx2 = canvasXScale(canvasWr / 2.0f + sin(angg) * yRadarRadius2);
                ty2 = canvasYScale(cos(angg) * yRadarRadius2 + radarRadius);
            } else {
                x2 = canvasXScale(0.5f * segment);
                y2 = ((nums[0] - minNumber) / maxNumber * (canvasHr));
                yRadarRadius2 = y2 / 2.0f;
                y2 = canvasYScale(y2);

                angg1 = 0 / n * TWO_PI  + PI / 2.0f;
                angg2 = (getSubColSum(table, defaultCol, 0, 0)) / colSum * TWO_PI  + PI / 2.0f;

                angg = lerp(angg1, angg2, f);

                tx2 = canvasXScale(canvasWr / 2.0f + sin(angg) * yRadarRadius2);
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

        float radarRadius = canvasHr / 2.0f;
        fill(color(128));
        stroke(color(128));
        strokeWeight(1);
        // cannot handle x = c

        for (int i = 0; i < n; i++) {
            float yRadarRadius = canvasHr / 2.0f;

            float ang1 = PApplet.parseFloat(i) / n * TWO_PI  + PI / 2.0f;
            float ang2 = getSubColSum(table, defaultCol, 0, i) / colSum * TWO_PI  + PI / 2.0f;
            float ang = lerp(ang1, ang2, f);

            float ty1 = canvasYScale(cos(ang) * radarRadius + radarRadius);
            float tx1 = canvasXScale(canvasWr / 2.0f + sin(ang) * radarRadius);

            float tx2 = canvasXScale(canvasWr / 2.0f);
            float ty2 = canvasYScale(canvasHr / 2.0f);

            line(tx1, ty1, tx2, ty2);
        }
    }

    private void adjRadiusPoints(float f, float c) {
        float minNumber = 0; // getColMin(table, defaultCol);
        float maxNumber = getColMax(table, defaultCol);
        float[] nums = table.getFloatColumn(defaultCol);
        float colSum = getSum(nums);
        int colNum = nums.length;
        float radarRadius = canvasHr / 2.0f;
        // cannot handle x = c

        for (int i = 0; i < n; i++) {
            float lx1 = -1;
            float lx2 = -1;
            float ly1 = -1;
            float ly2 = -1;

            float yRadarRadius1 = -1;
            float x1 = canvasXScale(((i % colNum + 0.5f) * segment));
            float y1 = (nums[i % colNum] - minNumber) / maxNumber * (canvasHr);
            yRadarRadius1 = y1 / 2.0f;
            y1 = canvasYScale(y1);

            float r1 = lerp(yRadarRadius1, radarRadius, f);

            float ang1 = PApplet.parseFloat(i) / n * TWO_PI  + PI / 2.0f;
            float ang2 = getSubColSum(table, defaultCol, 0, i) / colSum * TWO_PI  + PI / 2.0f;
            float ang = ang2;

            float ty1 = canvasYScale(cos(ang) * r1 + radarRadius);
            float tx1 = canvasXScale(canvasWr / 2.0f + sin(ang) * r1);


            float yRadarRadius2 = -1;
            float x2 = canvasXScale((i % colNum + 1 + 0.5f) * segment);
            float y2 = ((nums[(i + 1) % colNum] - minNumber) / maxNumber * (canvasHr));
            yRadarRadius2 = y2 / 2.0f;
            y2 = canvasYScale(y2);

            float r2 = lerp(yRadarRadius2, radarRadius, f);

            float angg1 = PApplet.parseFloat(i % colNum + 1) / n * TWO_PI  + PI / 2.0f;
            float angg2 = -1;

            if (i >= n - 1) {
                angg2 = (getSubColSum(table, defaultCol, 0, 0)) / colSum * TWO_PI  + PI / 2.0f;
            } else {
                angg2 = (getSubColSum(table, defaultCol, 0, i + 1)) / colSum * TWO_PI  + PI / 2.0f;
            }

            float angg = angg2;

            float ty2 = canvasYScale(cos(angg) * r2 + radarRadius);
            float tx2 = canvasXScale(canvasWr / 2.0f + sin(angg) * r2);

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
                float x1 = 0.5f * segment;
                float y1 = curSum / sum * canvasHr;

                float barW = segment;
                float barH = nums[i] / sum * canvasHr;            
                
                pushStyle();

                float targetW = canvasHr / 2;
                strokeWeight(lerp(0, targetW, f));
               
                noFill();

                stroke(lerpColor(colors[0], colors[1], PApplet.parseFloat(i) / n));
                strokeCap(SQUARE);
                ellipseMode(RADIUS);

                float curRadius = lerp(0, canvasHr / 4.0f, f);

                float targetStartArc = curSum / sum * TWO_PI;
                float targetEndArc = ((curSum + nums[i]) / sum * TWO_PI) > TWO_PI ? TWO_PI : ((curSum + nums[i]) / sum * TWO_PI);

                float arcX = canvasXScale(canvasWr / 2.0f);
                float arcY = canvasYScale(canvasHr / 2.0f);
     
                arc(arcX, arcY, curRadius, curRadius, targetStartArc, targetEndArc);

                popStyle();
                
                curSum += nums[i];
         }

    }


}
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
            drawPoints(1 - stepIndex / PApplet.parseFloat(stepIndexMax));
            nextFrame();
    }

    // first one get stacked
    public void step1() {
        recomputeNSeg();
        drawStackedAxis(stepIndex / PApplet.parseFloat(stepIndexMax), 0);
        drawFirstLines(stepIndex / PApplet.parseFloat(stepIndexMax));
        nextFrame();
    }

    public void step() {
        recomputeNSeg();
        drawStackedAxis(1, 0);
        drawFirstLines(1);
        fillLines(stepIndex / PApplet.parseFloat(stepIndexMax));
        nextFrame();
    }

    public void step1001() {
        recomputeNSeg();
        drawStackedAxis(1, 0); 
        moveToCenter(stepIndex / PApplet.parseFloat(stepIndexMax));
        nextFrame();
    }

    public void step1002() {
        recomputeNSeg();
        drawStackedAxis(1, 0);
        smoothyThread(stepIndex / PApplet.parseFloat(stepIndexMax));
        nextFrame();
    }

    public void step1003() {
        recomputeNSeg();
        drawStackedAxis(1, 0);
        smoothyThread(1);
        drawLegend(stepIndex / PApplet.parseFloat(stepIndexMax));
        nextFrame();
    }

    private void moveToCenter(float f) {
        int colNum = table.getColumnCount();
        float minNumber = 0;
        float allMax = getRowSumMax(table);

        for (int j = colNum - 1; j >= defaultCol; j--) {
            float[] nums = table.getFloatColumn(j);
            strokeWeight(1);
            stroke(lerpColor(colors[0], colors[1], PApplet.parseFloat(j - 1) / colNum));
            fill(lerpColor(colors[0], colors[1], PApplet.parseFloat(j - 1) / colNum));
            beginShape();

            for (int i = 0; i < n; i++) {
                float x1 = ((i + 0.5f) * segment);
                float y1 = (getSubRowSum(table, i, defaultCol, j)) / allMax * (canvasHr);
                float margin_top_bottom = (allMax - getRowSum(table, i)) / allMax * canvasHr / 2.0f;
                float curY = lerp(y1, y1 + margin_top_bottom, f);
                vertex(canvasXScale(x1), canvasYScale(curY));
            }

            for (int i = n - 1; i >= 0; i--) {
                float x1 = ((i + 0.5f) * segment);
                float y1 = (getSubRowSum(table, i, defaultCol, j)) / allMax * (canvasHr);
                float margin_top_bottom = (allMax - getRowSum(table, i)) / allMax * canvasHr / 2.0f;
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
            stroke(lerpColor(colors[0], colors[1], PApplet.parseFloat(j - 1) / colNum));
            fill(lerpColor(colors[0], colors[1], PApplet.parseFloat(j - 1) / colNum));
            beginShape();
            if (j != realStep) {
                for (int i = 0; i < n; i++) {
                    float x1 = ((i + 0.5f) * segment);
                    float y1 = (getSubRowSum(table, i, defaultCol, j)) / allMax * (canvasHr);
                    vertex(canvasXScale(x1), canvasYScale(y1));
                }
                vertex(canvasXScale((n - 0.5f) * segment), canvasYScale(0));
                vertex(canvasXScale(0.5f * segment), canvasYScale(0));
                endShape(CLOSE);
            } else {
                for (int i = 0; i < n; i++) {
                    float x1 = ((i + 0.5f) * segment);
                    float y1 = (getSubRowSum(table, i, defaultCol, j)) / allMax * (canvasHr);
                    float preY = (getSubRowSum(table, i, defaultCol, j - 1)) / allMax * (canvasHr);
                    float curY = lerp(preY, y1, f);
                    vertex(canvasXScale(x1), canvasYScale(curY));
                }
                vertex(canvasXScale((n - 0.5f) * segment), canvasYScale(0));
                vertex(canvasXScale(0.5f * segment), canvasYScale(0));
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
                float x1 = ((i + 0.5f) * segment);
                float y1 = ((nums[i] - minNumber) / maxNumber * (canvasHr));
                float x2 = ((i + 1 + 0.5f) * segment);
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
                float x1 = ((i + 0.5f) * segment);
                float x2 = ((i + 1 + 0.5f) * segment);
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
            float x1 = ((i + 0.5f) * segment);
            float y1 = ((nums[i] - minNumber) / maxNumber * (canvasHr));

            noStroke();

            ellipseMode(CENTER);
            fill(colors[0], f * 255);
            ellipse(canvasXScale(x1), canvasYScale(y1), pointSize, pointSize);
        }
    }
}



class MenuItem {
    float x = -1;
    float y = -1;
    float iWidth = -1;
    float iHeight = -1;
    String text = null;
    int textColor = color(0);
    int backgroundColor = color(255);
    int curColor = backgroundColor;
    int activeColor = color(166, 189, 219);
    int highLightColor = color(208, 209, 230);
    int strokeColor = color(208, 209, 230);
    int disableColor = color(208, 209, 230);
    int shallowColor = color(230, 230, 230, 230);
    int shallowTextColor = color(60, 60, 60, 60);
    private String shape = "rect";
    private boolean drawShallow = false;
    private int strokeWeightNum = 1;
    private String bindFunc = null;
    private Object bindObj = null;

    PImage img = null;
    boolean able = true;

    MenuItem(String text, String shape) {
        this.text = text;
        this.shape = shape;
    }

    MenuItem(String text, String shape, Object o, String func) {
        this.text = text;
        this.shape = shape;
        this.bindObj = o;
        this.bindFunc = func;
    }

    MenuItem(float x, float y, float iWidth, float iHeight, String text) {
        this.x = x;
        this.y = y;
        this.iWidth = iWidth;
        this.iHeight = iHeight;
        this.text = text;
    }

    public void setFunction(Object obj, String func) {
        this.bindObj = obj;
        this.bindFunc = func;
    }

    public void runFunc() {
        try {
            Method ms = bindObj.getClass().getMethod(bindFunc, null);
            Object returnValue = ms.invoke(bindObj, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean equals(Object o) {
        MenuItem mi = (MenuItem) o;
        return mi.getName().equals(this.text) && mi.getShape().equals(this.shape);
    }

    public void setAble(boolean flag) {
        this.able = flag;
    }

    public MenuItem setShape(String str) {
        this.shape = str;
        return this;
    }

    public MenuItem setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public MenuItem setSize(float w, float h) {
        this.iWidth = w;
        this.iHeight = h;
        return this;
    }

    public MenuItem setText(String text) {
        this.text = text;
        return this;
    }

    public MenuItem setImage(PImage img) {
        this.img = img;
        return this;
    }

    public boolean isAble() {
        return this.able;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return iWidth;
    }

    public float getHeight() {
        return iHeight;
    }

    public int getActiveColor() {
        return activeColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public int getHighLightColor() {
        return highLightColor;
    }

    public MenuItem setTextColor(int c) {
        this.textColor = c;
        return this;
    }

    public MenuItem setBackgroundColor(int c) {
        this.backgroundColor = c;
        return this;
    }

    public MenuItem setActiveColor(int c) {
        this.activeColor = c;
        return this;
    }

    public MenuItem setStrokeColor(int c){
        this.strokeColor = c;
        return this;
    }

    public void setColor(int c) {
        this.curColor = c;
    }

    public String getText() {
        return text;
    }

    public String getName() {
        return text;
    }

    public String getShape() {
        return this.shape;
    }

    public void setShallow(boolean sh) {
        this.drawShallow = sh;
    }

    public MenuItem setStrokeWeight(int i) {
        this.strokeWeightNum = i;
        return this;
    }

    public boolean isOn() {
        if (shape.equals("rect")) {
            return (mouseX <= x + iWidth) && (mouseX >= x) && (mouseY <= y + iHeight) && (mouseY >= y);
        } else if (shape.equals("ellipse")) {
            return sq(mouseX - (x + iWidth / 2.0f)) / sq(iWidth / 2.0f) + sq(mouseY - (y + iHeight / 2.0f)) / sq(iHeight / 2.0f) <= 1;
        }
        return false;
    }

    public void drawMe() {
        if (isOn() && able) {
            curColor = activeColor;
        } else if (!able) {
            curColor = disableColor;
        } else {
            curColor = backgroundColor;
        }
        fill(curColor);
        stroke(strokeColor);
        if (strokeWeightNum == 0) {
            noStroke();
        } else {
            strokeWeight(strokeWeightNum);
        }
        if (shape.equals("rect")) {
            rectMode(CORNER);
            rect(x, y, iWidth, iHeight, 3.0f);
            if (drawShallow) {
                fill(shallowColor);
                stroke(shallowTextColor);
                rect(mouseX - iWidth, mouseY - iHeight, iWidth, iHeight, 3.0f);
            }
        } else if (shape.equals("ellipse")) {
            ellipseMode(CORNER);
            ellipse(x, y, iWidth, iHeight);
            if (drawShallow) {
                fill(shallowColor);
                stroke(shallowTextColor);
                if (strokeWeightNum == 0) {
                    noStroke();
                } else {
                    strokeWeight(strokeWeightNum);
                }
                ellipse(mouseX - iWidth / 2.0f, mouseY - iHeight / 2.0f, iWidth, iHeight);
            }
        } else {
            println("do not support " + shape);
        }
        textAlign(CENTER);
        fill(textColor);
        text(this.text, x + iWidth / 2.0f, y + iHeight / 2.0f + fontSize / 2.0f);
        if (drawShallow) {
            fill(shallowTextColor);
            text(this.text, mouseX - iWidth / 2.0f, mouseY - fontSize / 2.0f, iWidth, iHeight);
        }
    }


}
/*
 * assume row major layout
 */
class Menu {
    private float margin = 10;
    protected float x = -1;
    protected float y = -1;
    protected float iHeight = -1;
    protected float iWidth = -1;
    private int margin_top = -1;
    private int margin_bottom = -1;
    private int margin_left = -1;
    private int margin_right = -1;
    private int colNum = 1;
    private int rowNum = 1;
    private int round = 5;
    private ArrayList < MenuItem > items = new ArrayList < MenuItem > ();
    protected int textColor = color(0);
    protected int backgroundColor = color(240);
    protected int curColor = backgroundColor;
    protected int activeColor = color(166, 189, 219);
    protected int highLightColor = color(208, 209, 230);
    protected int disableColor = color(208, 209, 230);
    protected int shallowColor = color(230, 230, 230, 230);
    protected int shallowTextColor = color(60, 60, 60, 60);

    public Menu() {

    }

    public Menu setSize(float w, float h) {
        this.iWidth = w;
        this.iHeight = h;
        return this;
    }

    public Menu setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Menu setMargins(int margin_top, int margin_left, int margin_bottom, int margin_right) {
        this.margin_top = margin_top;
        this.margin_left = margin_left;
        this.margin_right = margin_right;
        this.margin_bottom = margin_bottom;
        return this;
    }

    public Menu setColNum(int colNum) {
        this.colNum = colNum;
        return this;
    }

    public Menu setRowNum(int rowNum) {
        this.rowNum = rowNum;
        return this;
    }

    public MenuItem addItem(String text) {
        MenuItem mi = new MenuItem(text, "rect");
        items.add(mi);
        return mi;
    }

    public MenuItem addItem(String text, String shape) {
        MenuItem mi = new MenuItem(text, shape);
        items.add(mi);
        return mi;
    }

    public MenuItem addItem(String text, String shape, Object obj, String func) {
        MenuItem mi = new MenuItem(text, shape, obj, func);
        items.add(mi);
        return mi;
    }

    public void rebuildMenu() {
        for (int index = 0; index < items.size(); index++) {
            MenuItem mi = items.get(index);
            int rowIndex = index / colNum;
            int colIndex = index % colNum;
            float xStep = iWidth / colNum;
            float yStep = iHeight / rowNum;
            float xtmp = x + colIndex * xStep + margin_left;
            float ytmp = y + rowIndex * yStep + margin_top;

            mi.setPosition(xtmp, ytmp)
                .setSize(xStep - margin_right - margin_left, yStep - margin_left - margin_right);
        }
    }

    public Menu setTextColor(int c) {
        this.textColor = c;
        return this;
    }

    public Menu setBackgroundColor(int c) {
        this.backgroundColor = c;
        return this;
    }

    public Menu setActiveColor(int c) {
        this.activeColor = c;
        return this;
    }

    public Menu setColor(int c) {
        this.curColor = c;
        return this;
    }

    public int getNumItems() {
        return items.size();
    }

    public ArrayList < MenuItem > getItems() {
        return items;
    }

    public MenuItem getItem(int index) {
        return items.get(index);
    }

    public void removeAll() {
        items.clear();
    }

    public void drawMe() {
        fill(backgroundColor);
        noStroke();
        rectMode(CORNER);
        rect(x, y, iWidth, iHeight);
        for (MenuItem mi: items) {
            mi.drawMe();
        }
    }

    public void handleMousePressed() {
        for (MenuItem mi: items) {
            if (mi.isOn()) {
                dragging = mi;
                return;
            }
        }
    }

    public void handleMouseDragged() {
        if (dragging != null) {
            dragging.setShallow(true);
            return;
        }
    }

    public void handleMouseReleased() {
        for (MenuItem mi: items) {
            mi.setShallow(false);
        }
    }

    public void handleMouseClicked() {
        for (MenuItem mi: items) {
            if (mi.isOn())
                mi.runFunc();
        }
    }

    public float getItemHeight() {
        if (items.size() == 0) {
            return -1;
        } else {
            return items.get(0).getHeight();
        }
    }

    public float getItemWidth() {
        if (items.size() == 0) {
            return -1;
        } else {
            return items.get(0).getWidth();
        }
    }
}

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
            drawPlXLabelPerc(1 - PApplet.parseFloat(stepIndex) / stepIndexMax);
            drawArc(0);
            nextFrame();
        }

    // adjust radius

    public void step1() {
            recomputeNSeg();
            drawArc(PApplet.parseFloat(stepIndex) / stepIndexMax);
            nextFrame();
        }
        // adj arc
    public void step2() {
            recomputeNSeg();
            adjArc(PApplet.parseFloat(stepIndex) / stepIndexMax);
            nextFrame();
        }
        // draw axis
    public void step3() {
            recomputeNSeg();
            adjArc(1);
            drawPlAxis(PApplet.parseFloat(stepIndex) / stepIndexMax);
            nextFrame();
        }
        // adj color
    public void step4() {
        recomputeNSeg();
        adjColor(PApplet.parseFloat(stepIndex) / stepIndexMax);
        drawPlAxis(1);
        nextFrame();
    }

    public void step5() {
        recomputeNSeg();
        stackFirstArc(PApplet.parseFloat(stepIndex) / stepIndexMax);
        drawPlAxis(1);
        nextFrame();
    }

    public void step() {
        recomputeNSeg();
        stackArcs(PApplet.parseFloat(stepIndex) / stepIndexMax);
        stackFirstArc(1);
        drawPlAxis(1);
        nextFrame();
    }

    public void step1001() {
        recomputeNSeg();
        stackArcs(1);
        //stackFirstArc(1);
        drawPlAxis(1);
        drawPlXLabelUniform(PApplet.parseFloat(stepIndex) / stepIndexMax);
        drawStackedYPldAxis(PApplet.parseFloat(stepIndex) / stepIndexMax);
        drawLegend(PApplet.parseFloat(stepIndex) / stepIndexMax);
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

            float targetW = canvasHr / 2.0f;
            float curW = lerp(1, nums[i] / maxNumber, f) * targetW + 2;
            strokeWeight(curW);

            noFill();

            stroke(lerpColor(colors[0], colors[1], PApplet.parseFloat(i) / n));
            strokeCap(SQUARE);
            ellipseMode(RADIUS);

            float curRadius = lerp(1, nums[i] / maxNumber, f) * canvasHr / 4.0f;

            float targetStartArc = curSum / sum * TWO_PI;
            float targetEndArc = ((curSum + nums[i]) / sum * TWO_PI) > TWO_PI ? TWO_PI : ((curSum + nums[i]) / sum * TWO_PI);

            float arcX = canvasXScale(canvasWr / 2.0f);
            float arcY = canvasYScale(canvasHr / 2.0f);

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
            float x1 = 0.5f * segment;
            float y1 = curSum / sum * canvasHr;

            float barW = segment;
            float barH = nums[i] / sum * canvasHr;

            pushStyle();

            float targetW = canvasHr / 2.0f;
            float curW = nums[i] / maxNumber * targetW + 2;
            strokeWeight(curW);

            //noFill();

            stroke(lerpColor(colors[0], colors[1], PApplet.parseFloat(i) / n));
            strokeCap(SQUARE);
            ellipseMode(RADIUS);

            float curRadius = nums[i] / maxNumber * canvasHr / 4.0f;

            float preStartArc = curSum / sum * TWO_PI;
            float preEndArc = ((curSum + nums[i]) / sum * TWO_PI) > TWO_PI ? TWO_PI : ((curSum + nums[i]) / sum * TWO_PI);

            float targetStartArc = PApplet.parseFloat(i) / n * TWO_PI;
            float targetEndArc = (PApplet.parseFloat(i + 1) / n * TWO_PI) > TWO_PI ? TWO_PI : (PApplet.parseFloat(i + 1) / n) * TWO_PI;

            float curStartArc = lerp(preStartArc, targetStartArc, f);
            float curEndArc = lerp(preEndArc, targetEndArc, f);

            float arcX = canvasXScale(canvasWr / 2.0f);
            float arcY = canvasYScale(canvasHr / 2.0f);

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
        float radarRadius = canvasHr / 2.0f;
        fill(color(0));
        stroke(color(0));
        strokeWeight(1);
        // cannot handle x = c
        int stopIndex = f > 0 ? (n + 1) : n;
        for (int i = 0; i < stopIndex; i++) {
            float yRadarRadius = canvasHr / 2.0f;

            float x1 = canvasXScale(((i + 0.5f) * segment));
            float y1 = ((nums[i % colNum] - minNumber) / maxNumber * (canvasHr));
            yRadarRadius = y1 / 2.0f;
            y1 = canvasYScale(y1);

            float tRadarRadius = lerp(0, radarRadius, f);

            float ty1 = canvasYScale(cos(PApplet.parseFloat(i) / n * TWO_PI + PI / 2.0f) * tRadarRadius + radarRadius);
            float tx1 = canvasXScale(canvasWr / 2.0f + sin(PApplet.parseFloat(i) / n * TWO_PI + PI / 2.0f) * tRadarRadius);

            float x2 = x1;
            float y2 = canvasYScale(0);
            float tx2 = canvasXScale(canvasWr / 2.0f);
            float ty2 = canvasYScale(canvasHr / 2.0f);

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

            float preRadius = nums[i] / maxNumber * canvasHr / 2.0f;
            float targetRadius = nums[i] / allMaxNumber * canvasHr / 2.0f;
            float curRadius = lerp(preRadius, targetRadius, f);

            float targetStartArc = PApplet.parseFloat(i) / n * TWO_PI;
            float targetEndArc = (PApplet.parseFloat(i + 1) / n * TWO_PI) > TWO_PI ? TWO_PI : (PApplet.parseFloat(i + 1) / n * TWO_PI);

            float arcX = canvasXScale(canvasWr / 2.0f);
            float arcY = canvasYScale(canvasHr / 2.0f);

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

            float targetW = canvasHr / 2.0f;
            float curW = nums[i] / maxNumber * targetW + 2;
            strokeWeight(curW);

            noFill();

            int stopFab = PApplet.parseInt(f * n);

            if (i < stopFab)
                stroke(colors[0]);
            else
                stroke(lerpColor(colors[0], colors[1], PApplet.parseFloat(i) / n));

            strokeCap(SQUARE);
            ellipseMode(RADIUS);

            float curRadius = nums[i] / maxNumber * canvasHr / 4.0f;

            float targetStartArc = PApplet.parseFloat(i) / n * TWO_PI;
            float targetEndArc = (PApplet.parseFloat(i + 1) / n * TWO_PI) > TWO_PI ? TWO_PI : (PApplet.parseFloat(i + 1) / n) * TWO_PI;

            float arcX = canvasXScale(canvasWr / 2.0f);
            float arcY = canvasYScale(canvasHr / 2.0f);

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
                float preW = (getSubRowSum(table, i, defaultCol, j - 1)) / allMaxNumber * canvasHr / 2.0f;
                float targetW = (getSubRowSum(table, i, defaultCol, j)) / allMaxNumber * canvasHr / 2.0f;
                float curW = (j == realStep) ? lerp(preW, targetW, f) : targetW;
                strokeWeight(targetW);

                fill(lerpColor(colors[0], colors[1], PApplet.parseFloat(j - 1) / colNum));
                stroke(lerpColor(colors[0], colors[1], PApplet.parseFloat(j - 1) / colNum));

                strokeWeight(1);
                strokeCap(SQUARE);
                ellipseMode(RADIUS);
                float preRadius = (getSubRowSum(table, i, defaultCol, j - 1)) / allMaxNumber * canvasHr / 2.0f;
                float targetRadius = (getSubRowSum(table, i, defaultCol, j)) / allMaxNumber * canvasHr / 2.0f;
                float curRadius = (j == realStep) ? lerp(preRadius, targetRadius, f) : targetRadius;

                float targetStartArc = PApplet.parseFloat(i) / n * TWO_PI;
                float targetEndArc = (PApplet.parseFloat(i + 1) / n * TWO_PI) > TWO_PI ? TWO_PI : (PApplet.parseFloat(i + 1) / n) * TWO_PI;

                float arcX = canvasXScale(canvasWr / 2.0f);
                float arcY = canvasYScale(canvasHr / 2.0f);


                arc(arcX, arcY, curRadius, curRadius, targetStartArc, targetEndArc);

                popStyle();

                curSum += nums[i];
            }
        }
    }
}

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
        drawPlXLabelUniform(1 - PApplet.parseFloat(stepIndex) / stepIndexMax);
        drawPlAxis(1 - PApplet.parseFloat(stepIndex) / stepIndexMax);
        drawStackedYPldAxis(1 - PApplet.parseFloat(stepIndex) / stepIndexMax);
        drawLegend(1 - stepIndex / PApplet.parseFloat(stepIndexMax));
        nextFrame();       
    }

    public void step1(){
        recomputeNSeg();
        stackArcs(PApplet.parseFloat(stepIndex) / stepIndexMax);
        nextFrame();       
    }

    public void step2(){
        recomputeNSeg();
        //stackArcs(1);
        drawShortBars(PApplet.parseFloat(stepIndex) / stepIndexMax);
        drawXAxis(PApplet.parseFloat(stepIndex) / stepIndexMax);
        nextFrame();       
    }
    
    public void step3(){
        recomputeNSeg();
        //stackArcs(1);
        drawBars(PApplet.parseFloat(stepIndex) / stepIndexMax);    
        drawStackYAxis(PApplet.parseFloat(stepIndex) / stepIndexMax);
        drawXAxis(1);
        drawLegend(stepIndex / PApplet.parseFloat(stepIndexMax));
        nextFrame();       
    }

    private void drawPlAxis(float f) {
        float minNumber = 0;
        float maxNumber = getColMax(table, defaultCol);
        float[] nums = table.getFloatColumn(defaultCol);
        int colNum = nums.length;
        float radarRadius = canvasHr / 2.0f;
        fill(color(0));
        stroke(color(0));
        strokeWeight(1);
        // cannot handle x = c
        int stopIndex = f > 0 ? (n + 1) : n;
        for (int i = 0; i < stopIndex; i++) {
            float yRadarRadius = canvasHr / 2.0f;

            float x1 = canvasXScale(((i + 0.5f) * segment));
            float y1 = ((nums[i % colNum] - minNumber) / maxNumber * (canvasHr));
            yRadarRadius = y1 / 2.0f;
            y1 = canvasYScale(y1);

            float tRadarRadius = lerp(0, radarRadius, f);

            float ty1 = canvasYScale(cos(PApplet.parseFloat(i) / n * TWO_PI + PI / 2.0f) * tRadarRadius + radarRadius);
            float tx1 = canvasXScale(canvasWr / 2.0f + sin(PApplet.parseFloat(i) / n * TWO_PI  + PI / 2.0f) * tRadarRadius);

            float x2 = x1;
            float y2 = canvasYScale(0);
            float tx2 = canvasXScale(canvasWr / 2.0f);
            float ty2 = canvasYScale(canvasHr / 2.0f);

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
                float preW = (getSubRowSum(table, i, defaultCol, j - 1)) / allMaxNumber * canvasHr / 2.0f;
                float targetW = (getSubRowSum(table, i, defaultCol, j)) / allMaxNumber * canvasHr / 2.0f;
                float curW =  targetW;
                strokeWeight(targetW);

                fill(lerpColor(colors[0], colors[1], PApplet.parseFloat(j - 1) / colNum));
                stroke(lerpColor(colors[0], colors[1], PApplet.parseFloat(j - 1) / colNum));

                strokeWeight(1);
                strokeCap(SQUARE);
                ellipseMode(RADIUS);
                float preRadius = (getSubRowSum(table, i, defaultCol, j - 1)) / allMaxNumber * canvasHr / 2.0f;
                float targetRadius = (getSubRowSum(table, i, defaultCol, j)) / allMaxNumber * canvasHr / 2.0f;
                float curRadius =  targetRadius;

                float targetStartArc = PApplet.parseFloat(i) / n * TWO_PI;
                float targetEndArc = (PApplet.parseFloat(i + 1) / n * TWO_PI) > TWO_PI ? TWO_PI : (PApplet.parseFloat(i + 1) / n) * TWO_PI;

                float arcX = canvasXScale(canvasWr / 2.0f);
                float arcY = canvasYScale(canvasHr / 2.0f);
                
                float targetArcX = canvasXScale((i + 0.5f) * segment);
                float targetArcY = canvasYScale(0);

                float curX = lerp(arcX, targetArcX, f);
                float curY = lerp(arcY, targetArcY, f);

                pushMatrix();
                translate(curX, curY);
                float preRotat = 0;
                float deltaArc = (targetEndArc - targetStartArc) / 2.0f;
                float targetRotat = 1.5f * PI - (targetStartArc + targetEndArc) / 2.0f;
                rotate(lerp((targetStartArc + targetEndArc) / 2.0f, 1.5f * PI, f));
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
                float targetW = (getSubRowSum(table, i, defaultCol, j)) / allMaxNumber * canvasHr / 2.0f;
                strokeWeight(targetW);

                strokeWeight(1);
                strokeCap(SQUARE);
                ellipseMode(RADIUS);
                float preRadius = (getSubRowSum(table, i, defaultCol, j)) / allMaxNumber * canvasHr / 2.0f;

                float targetStartArc = PApplet.parseFloat(i) / n * TWO_PI;
                float targetEndArc = (PApplet.parseFloat(i + 1) / n * TWO_PI) > TWO_PI ? TWO_PI : (PApplet.parseFloat(i + 1) / n) * TWO_PI;

                float arcX = canvasXScale(canvasWr / 2.0f);
                float arcY = canvasYScale(canvasHr / 2.0f);
                
                float targetArcX = canvasXScale((i + 0.5f) * segment);
                float targetArcY = canvasYScale(0);

                fill(lerpColor(colors[0], colors[1], PApplet.parseFloat(j - 1) / colNum), (1 - f) * 255);
                stroke(lerpColor(colors[0], colors[1], PApplet.parseFloat(j - 1) / colNum), (1 - f) * 255);

                pushMatrix();
                translate(targetArcX, targetArcY);
                float deltaArc = (targetEndArc - targetStartArc) / 2.0f;
                rotate(1.5f * PI);
                arc(0, 0, preRadius, preRadius, -1 * deltaArc, deltaArc);
                popMatrix();

                fill(lerpColor(colors[0], colors[1], PApplet.parseFloat(j - 1) / colNum), f * 255);
                stroke(lerpColor(colors[0], colors[1], PApplet.parseFloat(j - 1) / colNum), f * 255);

                float rowRatio = nums[i] / allMaxNumber;
                float x1 = ((i + 0.25f) * segment);
                float y1 = ((getSubRowSum(table, i, defaultCol, j) - minNumber) / allMaxNumber * (canvasHr / 2.0f));
                float barH = rowRatio * (canvasHr / 2.0f);
                float barW = 0.5f * segment;

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
                fill(lerpColor(colors[0], colors[1], PApplet.parseFloat(j - 1) / colNum));
                stroke(lerpColor(colors[0], colors[1], PApplet.parseFloat(j - 1) / colNum));
                float preH = canvasHr / 2.0f;
                float targetH = canvasHr;
                float curH = lerp(preH, targetH, f);
                float rowRatio = nums[i] / allMaxNumber;
                float x1 = ((i + 0.25f) * segment);
                float y1 = ((getSubRowSum(table, i, defaultCol, j) - minNumber) / allMaxNumber * curH);
                float barH = rowRatio * curH;
                float barW = 0.5f * segment;

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
                 float tmpY = canvasYScale(i / PApplet.parseFloat(numTickets) * canvasHr);             
                 String str = nfc(numStep * i, 1);
                 stroke(0);
                 line(tmpX, tmpY, tmpX - 5, tmpY);
                 fill(0);
                 textAlign(RIGHT);
                 text(str, tmpX - 7, tmpY + fontSize / 2.0f);
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

            float preRadius = nums[i] / maxNumber * canvasHr / 2.0f;
            float targetRadius = nums[i] / allMaxNumber * canvasHr / 2.0f;
            float curRadius = lerp(preRadius, targetRadius, f);

            float targetStartArc = PApplet.parseFloat(i) / n * TWO_PI;
            float targetEndArc = (PApplet.parseFloat(i + 1) / n * TWO_PI) > TWO_PI ? TWO_PI : (PApplet.parseFloat(i + 1) / n * TWO_PI);

            float arcX = canvasXScale(canvasWr / 2.0f);
            float arcY = canvasYScale(canvasHr / 2.0f);

            arc(arcX, arcY, curRadius, curRadius, targetStartArc, targetEndArc);

            popStyle();

            curSum += nums[i];
        }

    }

}

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
        drawPlXLabelUniform(1 - PApplet.parseFloat(stepIndex) / stepIndexMax);
        drawPlAxis(1 - PApplet.parseFloat(stepIndex) / stepIndexMax);
        drawStackedYPldAxis(1 - PApplet.parseFloat(stepIndex) / stepIndexMax);
        drawLegend(1 - stepIndex / PApplet.parseFloat(stepIndexMax));
        nextFrame();       
    }

     public void step1(){
        recomputeNSeg();
        stackArcs(PApplet.parseFloat(stepIndex) / stepIndexMax, 1);
        drawXAxis(PApplet.parseFloat(stepIndex) / stepIndexMax);
        nextFrame();       
    }

    public void step2(){
        recomputeNSeg();
        stackArcs(1, 1);
        connectPoints(PApplet.parseFloat(stepIndex) / stepIndexMax);
        drawXAxis(1);
        nextFrame();       
    }

    public void step3(){
        recomputeNSeg();
        stackArcs(1, 1 - PApplet.parseFloat(stepIndex) / stepIndexMax);
        connectPoints(1);
        fillLines(0, 0.5f, 1);
        drawXAxis(1);
        nextFrame();       
    }

    public void step(){
        recomputeNSeg();
        connectPoints(1);
        fillLines(PApplet.parseFloat(stepIndex) / stepIndexMax, 0.5f, 1);
        drawXAxis(1);
        nextFrame();       
    }

    public void step1001(){
        recomputeNSeg();
        fillLines(1, PApplet.parseFloat(stepIndex) / stepIndexMax * 0.5f + 0.5f, 1);
        drawXAxis(1);
        nextFrame();       
    }

    public void step1002(){
        recomputeNSeg();
        //fillLines(1, 1, 1);
        smoothyThread(PApplet.parseFloat(stepIndex) / stepIndexMax);
        drawXAxis(1);
        nextFrame();       
    }

    public void step1003(){
        recomputeNSeg();
        //fillLines(1, 1, 1 - float(stepIndex) / stepIndexMax);
        smoothyThread(1);
        drawStackAxis(PApplet.parseFloat(stepIndex) / stepIndexMax);
        drawLegend(stepIndex / PApplet.parseFloat(stepIndexMax));
        drawXAxis(1);
        nextFrame();       
    }

    private void connectPoints(float f) {
        int colNum = table.getColumnCount();
        float minNumber = 0;
        float allMaxNumber = getRowSumMax(table);
        float curHeight = canvasHr / 2.0f;
        int stopNum = PApplet.parseInt(f * canvasWr / segment);

        for (int j = colNum - 1; j >= defaultCol; j--) {
            float[] nums = table.getFloatColumn(j);
            strokeWeight(1);
            stroke(lerpColor(colors[0], colors[1], PApplet.parseFloat(j - 1) / colNum));
            fill(lerpColor(colors[0], colors[1], PApplet.parseFloat(j - 1) / colNum));
            noFill();
            beginShape();

            for (int i = 0; i < stopNum; i++) {
                float x1 = ((i + 0.5f) * segment);
                float y1 = (getSubRowSum(table, i, defaultCol, j)) / allMaxNumber * curHeight;
                float margin_top_bottom = (allMaxNumber - getRowSum(table, i)) / allMaxNumber * curHeight / 2.0f + canvasHr / 4.0f;
                float curY = y1 + margin_top_bottom;
                vertex(canvasXScale(x1), canvasYScale(curY));
            }
            
            for (int i = stopNum - 1; i >= 0; i--) {
                float x1 = ((i + 0.5f) * segment);
                float y1 = (getSubRowSum(table, i, defaultCol, j)) / allMaxNumber * curHeight;
                float margin_top_bottom = (allMaxNumber - getRowSum(table, i)) / allMaxNumber * curHeight / 2.0f + canvasHr / 4.0f;
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
            stroke(lerpColor(colors[0], colors[1], PApplet.parseFloat(j - 1) / colNum), c * 255);
            fill(lerpColor(colors[0], colors[1], PApplet.parseFloat(j - 1) / colNum), c * 255);
            beginShape();

            if (j != realStep) {
                for (int i = 0; i < n; i++) {
                    float x1 = ((i + 0.5f) * segment);
                    float y1 = (getSubRowSum(table, i, defaultCol, j)) / allMaxNumber * (curHeight);
                    float margin_top_bottom = (allMaxNumber - getRowSum(table, i)) / allMaxNumber * curHeight / 2.0f + offset;
                    vertex(canvasXScale(x1), canvasYScale(y1 + margin_top_bottom));
                }
                for (int i = n - 1; i >= 0; i--) {
                    float x1 = ((i + 0.5f) * segment);
                    float y1 = (getSubRowSum(table, i, defaultCol, j)) / allMaxNumber * curHeight;
                    float margin_top_bottom = (allMaxNumber - getRowSum(table, i)) / allMaxNumber * curHeight / 2.0f + offset;
                    float curY = margin_top_bottom;
                    vertex(canvasXScale(x1), canvasYScale(curY));
                }
                endShape(CLOSE);
            } else {
                for (int i = 0; i < n; i++) {
                    float x1 = ((i + 0.5f) * segment);
                    float y1 = (getSubRowSum(table, i, defaultCol, j)) / allMaxNumber * (curHeight);
                    float preY = (getSubRowSum(table, i, defaultCol, j - 1)) / allMaxNumber * (curHeight);
                    float margin_top_bottom = (allMaxNumber - getRowSum(table, i)) / allMaxNumber * curHeight / 2.0f + offset;
                    float curY = lerp(preY + margin_top_bottom, y1 + margin_top_bottom, f);
                    vertex(canvasXScale(x1), canvasYScale(curY));
                }
                for (int i = n - 1; i >= 0; i--) {
                    float x1 = ((i + 0.5f) * segment);
                     //float y1 = (getSubRowSum(table, i, defaultCol, j)) / allMaxNumber * (curHeight);
                    float preY = (getSubRowSum(table, i, defaultCol, j - 1)) / allMaxNumber * curHeight;
                    float y1 = (allMaxNumber - getRowSum(table, i)) / allMaxNumber * curHeight / 2.0f + canvasHr / 4.0f;
                    float margin_top_bottom = (allMaxNumber - getRowSum(table, i)) / allMaxNumber * curHeight / 2.0f + offset;
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
                float preW = (getSubRowSum(table, i, defaultCol, j - 1)) / allMaxNumber * canvasHr / 2.0f;
                float targetW = (getSubRowSum(table, i, defaultCol, j)) / allMaxNumber * canvasHr / 2.0f;
                float curW =  targetW;
                strokeWeight(targetW);

                fill(lerpColor(colors[0], colors[1], PApplet.parseFloat(j - 1) / colNum), c * 255);
                stroke(lerpColor(colors[0], colors[1], PApplet.parseFloat(j - 1) / colNum), c * 255);

                strokeWeight(1);
                strokeCap(SQUARE);
                ellipseMode(RADIUS);
                float preRadius = (getSubRowSum(table, i, defaultCol, j - 1)) / allMaxNumber * canvasHr / 2.0f;
                float targetRadius = (getSubRowSum(table, i, defaultCol, j)) / allMaxNumber * canvasHr / 2.0f;
                float curRadius =  targetRadius;

                float targetStartArc = PApplet.parseFloat(i) / n * TWO_PI;
                float targetEndArc = (PApplet.parseFloat(i + 1) / n * TWO_PI) > TWO_PI ? TWO_PI : (PApplet.parseFloat(i + 1) / n) * TWO_PI;

                float arcX = canvasXScale(canvasWr / 2.0f);
                float arcY = canvasYScale(canvasHr / 2.0f);
                
                float targetArcX = canvasXScale((i + 0.5f) * segment);
                float margin_top_bottom = (allMaxNumber - getRowSum(table, i)) / allMaxNumber * canvasHr / 2.0f;
                float targetArcY = canvasYScale(margin_top_bottom + getRowSum(table, i) / allMaxNumber * canvasHr / 4.0f);

                float curX = lerp(arcX, targetArcX, f);
                float curY = lerp(arcY, targetArcY, f);

                pushMatrix();
                translate(curX, curY);
                float preRotat = 0;
                float deltaArc = (targetEndArc - targetStartArc) / 2.0f;
                float targetRotat = 1.5f * PI - (targetStartArc + targetEndArc) / 2.0f;
                rotate(lerp((targetStartArc + targetEndArc) / 2.0f, 1.5f * PI, f));
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
        float radarRadius = canvasHr / 2.0f;
        fill(color(0));
        stroke(color(0));
        strokeWeight(1);
        // cannot handle x = c
        int stopIndex = f > 0 ? (n + 1) : n;
        for (int i = 0; i < stopIndex; i++) {
            float yRadarRadius = canvasHr / 2.0f;

            float x1 = canvasXScale(((i + 0.5f) * segment));
            float y1 = ((nums[i % colNum] - minNumber) / maxNumber * (canvasHr));
            yRadarRadius = y1 / 2.0f;
            y1 = canvasYScale(y1);

            float tRadarRadius = lerp(0, radarRadius, f);

            float ty1 = canvasYScale(cos(PApplet.parseFloat(i) / n * TWO_PI + PI / 2.0f) * tRadarRadius + radarRadius);
            float tx1 = canvasXScale(canvasWr / 2.0f + sin(PApplet.parseFloat(i) / n * TWO_PI  + PI / 2.0f) * tRadarRadius);

            float x2 = x1;
            float y2 = canvasYScale(0);
            float tx2 = canvasXScale(canvasWr / 2.0f);
            float ty2 = canvasYScale(canvasHr / 2.0f);

            float lx1 =  tx1;
            float lx2 = tx2;
            float ly1 = ty1;
            float ly2 =  ty2;
            if (i <= n - 1)
                line(lx1, ly1, lx2, ly2);
        }
    }
}

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
        drawBars(PApplet.parseFloat(stepIndex) / stepIndexMax, 1);
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
        connectPoints(PApplet.parseFloat(stepIndex) / stepIndexMax);
        drawLegend(1);
        nextFrame();      
    }

    public void step2(){
        recomputeNSeg();
        drawBars(1, 1 - PApplet.parseFloat(stepIndex) / stepIndexMax);
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
        fillLines(PApplet.parseFloat(stepIndex) / stepIndexMax, 1);
        drawLegend(1);
        nextFrame();      
    }

    public void step1001(){
        recomputeNSeg();
        drawXAxis(1);
        drawStackYAxis(1);
        //connectPoints(1);
        //fillLines(1, 1);
        smoothyThread(PApplet.parseFloat(stepIndex) / stepIndexMax);
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
            stroke(lerpColor(colors[0], colors[1], PApplet.parseFloat(j - 1) / colNum));
            fill(lerpColor(colors[0], colors[1], PApplet.parseFloat(j - 1) / colNum));
            beginShape();

            if (j != realStep) {
                for (int i = 0; i < n; i++) {
                    float x1 = ((i + 0.5f) * segment);
                    float y1 = (getSubRowSum(table, i, defaultCol, j)) / allMaxNumber * (curHeight);
                    float margin_top_bottom = (allMaxNumber - getRowSum(table, i)) / allMaxNumber * curHeight / 2.0f + offset;
                    vertex(canvasXScale(x1), canvasYScale(y1 + margin_top_bottom));
                }
                for (int i = n - 1; i >= 0; i--) {
                    float x1 = ((i + 0.5f) * segment);
                    float y1 = (getSubRowSum(table, i, defaultCol, j)) / allMaxNumber * curHeight;
                    float margin_top_bottom = (allMaxNumber - getRowSum(table, i)) / allMaxNumber * curHeight / 2.0f + offset;
                    float curY = margin_top_bottom;
                    vertex(canvasXScale(x1), canvasYScale(curY));
                }
                endShape(CLOSE);
            } else {
                for (int i = 0; i < n; i++) {
                    float x1 = ((i + 0.5f) * segment);
                    float y1 = (getSubRowSum(table, i, defaultCol, j)) / allMaxNumber * (curHeight);
                    float preY = (getSubRowSum(table, i, defaultCol, j - 1)) / allMaxNumber * (curHeight);
                    float margin_top_bottom = (allMaxNumber - getRowSum(table, i)) / allMaxNumber * curHeight / 2.0f + offset;
                    float curY = lerp(preY + margin_top_bottom, y1 + margin_top_bottom, f);
                    vertex(canvasXScale(x1), canvasYScale(curY));
                }
                for (int i = n - 1; i >= 0; i--) {
                    float x1 = ((i + 0.5f) * segment);
                     //float y1 = (getSubRowSum(table, i, defaultCol, j)) / allMaxNumber * (curHeight);
                    float preY = (getSubRowSum(table, i, defaultCol, j - 1)) / allMaxNumber * curHeight;
                    float y1 = (allMaxNumber - getRowSum(table, i)) / allMaxNumber * curHeight / 2.0f + canvasHr / 4.0f;
                    float margin_top_bottom = (allMaxNumber - getRowSum(table, i)) / allMaxNumber * curHeight / 2.0f + offset;
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
                fill(lerpColor(colors[0], colors[1], PApplet.parseFloat(j - 1) / colNum), c * 255);
                stroke(lerpColor(colors[0], colors[1], PApplet.parseFloat(j - 1) / colNum) , c * 255);
                float targetH = canvasHr;
                float rowRatio = nums[i] / allMaxNumber;
                float x1 = ((i + 0.25f) * segment);
                float y1 = ((getSubRowSum(table, i, defaultCol, j) - minNumber) / allMaxNumber * targetH);
   
                float margin_top_bottom = (allMaxNumber - getRowSum(table, i)) / allMaxNumber * canvasHr / 2.0f;
                float targetY1 = margin_top_bottom + y1;
                
                float curY = lerp(y1, targetY1, f);

                float barH = rowRatio * targetH;
                float barW = 0.5f * segment;

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
        int stopNum = PApplet.parseInt(f * canvasWr / segment);

        for (int j = colNum - 1; j >= defaultCol; j--) {
            float[] nums = table.getFloatColumn(j);
            strokeWeight(1);
            stroke(lerpColor(colors[0], colors[1], PApplet.parseFloat(j - 1) / colNum));
            fill(lerpColor(colors[0], colors[1], PApplet.parseFloat(j - 1) / colNum));
            noFill();
            beginShape();

            for (int i = 0; i < stopNum; i++) {
                float x1 = ((i + 0.5f) * segment);
                float y1 = (getSubRowSum(table, i, defaultCol, j)) / allMaxNumber * canvasHr;
                float margin_top_bottom = (allMaxNumber - getRowSum(table, i)) / allMaxNumber * canvasHr / 2.0f;
                float curY = y1 + margin_top_bottom;
                vertex(canvasXScale(x1), canvasYScale(curY));
            }
            
            for (int i = stopNum - 1; i >= 0; i--) {
                float x1 = ((i + 0.5f) * segment);
                float y1 = (getSubRowSum(table, i, defaultCol, j)) / allMaxNumber * canvasHr;
                float margin_top_bottom = (allMaxNumber - getRowSum(table, i)) / allMaxNumber * canvasHr / 2.0f;
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
                 float tmpY = canvasYScale(i / PApplet.parseFloat(numTickets) * canvasHr);             
                 String str = nfc(numStep * i, 1);
                 stroke(0);
                 line(tmpX, tmpY, tmpX - 5, tmpY);
                 fill(0);
                 textAlign(RIGHT);
                 text(str, tmpX - 7, tmpY + fontSize / 2.0f);
            }

            fill(255);
            noStroke();
            rectMode(CORNER);
            rect((canvasWr + canvas_margin_right) * f, canvasYScale(0)
              , (canvasWr + canvas_margin_right), canvas_margin_bottom);
            rect(canvasXScale(0) - canvas_margin_left, canvasYScale(canvasHr), canvas_margin_left + 1, canvasHr * (1 - f));      
    }

}
public class TransitionPath extends Menu {
    private int nullColor = color(230, 230, 230);
    private int nullTextColor = color(100, 100, 100);
    private int distance = 50;
    private int itemWidth = -1;
    private int itemHeight = -1;
    private int lineWeight = 3;
    int lineColor = color(210, 219, 230);
    int nodeColor = color(210, 219, 230);
    private StringList allNames = null;

    private ArrayList < MenuItem > nodes = new ArrayList <MenuItem> ();

    public TransitionPath() {

    }

    public void setDistance(int dist) {
        this.distance = dist;
    }

    public TransitionPath setNodeColor(int c) {
        this.nodeColor = c;
        return this;
    }

    public TransitionPath setLineColor(int c) {
        this.lineColor = c;
        return this;
    }

    public TransitionPath setWholeNameList(StringList list) {
        if(list == null){
            this.allNames = null;
            return null;
        }
        String[] strs = list.values();
        StringList newlist = new StringList();
        String preStr = strs[0];
        newlist.append(preStr);
        for(int i = 1; i < list.size(); i++){
            String curStr = strs[i];
            if(!curStr.equals(preStr)){
                  newlist.append(curStr);
                  preStr = curStr;
            }
        }
        this.allNames = newlist;
        return this;
    }

    public StringList getNodesNames() {
        StringList list = new StringList();
        for (MenuItem mi: nodes) {
            list.append(mi.getName());
        }
        return list;
    }

    public void rebuildPath() {
        if (nodes.size() == 0) {
            fill(nullTextColor);
            textAlign(CENTER);
            text("Please drag visualizations here", x + iWidth / 2.0f, y + iHeight / 2.0f);
        } else {
            int n = nodes.size();
            int margin_left = 10;
            // do good
            itemWidth = (int) choices.getItemWidth();
            itemHeight = (int) choices.getItemHeight();
            distance = PApplet.parseInt(itemWidth);

            while (n * (itemWidth + distance) > iWidth) {
                distance = distance / 2 + 5;
                //itemWidth = int(iWidth / n - distance);
            }
            margin_left = PApplet.parseInt((iWidth - n * (itemWidth + distance)) / 2.0f);

            int curX = PApplet.parseInt(margin_left + x), curY = PApplet.parseInt(y + (iHeight / 2.0f));
            ellipseMode(CENTER);

            for (int i = 0; i < n; i++) {
                MenuItem mi = nodes.get(i);
                mi.setPosition(curX, curY - itemHeight / 2.0f)
                    .setSize(itemWidth, itemHeight);
                curX += distance + itemWidth;
            }
        }
    }

    public void drawMe() {
        pushStyle();
        fill(backgroundColor);
        noStroke();
        rect(x, y, iWidth, iHeight);
        if (nodes.size() == 0) {
            fill(nullTextColor);
            textAlign(CENTER);
            text("Please drag visualization here", x + iWidth / 2.0f, y + iHeight / 2.0f);
        } else {
            int n = nodes.size();
            int j = 0;
            for (int i = 0; i < n; i++) {
                if (i != n - 1) {
                    strokeWeight(lineWeight);
                    stroke(lineColor);
                    line(nodes.get(i).getX() + itemWidth / 2.0f, nodes.get(i).getY() + nodes.get(i).getHeight() / 2.0f,
                        nodes.get(i).getX() + distance + itemWidth, nodes.get(i).getY() + nodes.get(i).getHeight() / 2.0f);
                    line(nodes.get(i).getX() + distance + itemWidth, nodes.get(i).getY() + nodes.get(i).getHeight() / 2.0f,
                        nodes.get(i).getX() + distance + itemWidth - canvasH / 100.0f, nodes.get(i).getY() + nodes.get(i).getHeight() / 2.0f + canvasH / 100.0f
                        );
                    line(nodes.get(i).getX() + distance + itemWidth, nodes.get(i).getY() + nodes.get(i).getHeight() / 2.0f,
                        nodes.get(i).getX() + distance + itemWidth - canvasH / 100.0f, nodes.get(i).getY() + nodes.get(i).getHeight() / 2.0f - canvasH / 100.0f
                        );
                }
                nodes.get(i).drawMe();
                if (i != n) {
                    if (allNames != null && nodes.get(i).getName().equals(allNames.get(j))) {
                        j += 1;
                    } else if (allNames != null && !nodes.get(i).getName().equals(allNames.get(j))) {
                        fill(lineColor);
                        textAlign(CENTER);
                        pushMatrix();
                        translate(nodes.get(i).getX() - distance / 2.0f, nodes.get(i).getY() + nodes.get(i).getHeight() / 2.0f - fontSize);
                        float ang = distance < itemWidth ? -90 : 0;
                        if(ang < 0){
                            textAlign(LEFT);
                        }
                        rotate(radians(ang));
                        text(allNames.get(j), 0, 0);
                        popMatrix();
                        j += 2;
                    }
                }
            }
        if(!(totalStep < 0)){  
            float startX = nodes.get(0).getX();
            float startY = nodes.get(0).getY() + nodes.get(0).getHeight() + pathH * 0.2f;
            float endX = nodes.get(nodes.size() - 1).getX() + nodes.get(0).getWidth();
            strokeWeight(((height < width) ? height : width) * 0.01f);
            stroke(color(180));
            line(startX, startY, endX, startY);

            if(!(curStep < 0)){
                stroke(color(244,165,130));
                line(startX, startY, (endX - startX) * curStep / totalStep + startX, startY);
                curStep++;
            }

            if(curStep >= totalStep){
                curStep = totalStep;
            }
        }
        }

        popStyle();
    }

    public boolean isOnMe() {
        return (mouseX <= x + iWidth) && (mouseX >= x) && (mouseY <= y + iHeight) && (mouseY >= y);
    }

    public void handleMouseDragged() {
        if (isOnMe()) {
            curColor = activeColor;
        }
    }

    public void handleMouseReleased() {
        if (dragging != null && isOnMe()) {
            if (!nodes.contains(dragging)) {
                MenuItem mi = new MenuItem(dragging.getName(), dragging.getShape());
                mi.setBackgroundColor(nodeColor)
                    .setTextColor(textColor)
                    .setStrokeWeight(0);
                nodes.add(mi);
                itemWidth = (int) dragging.getWidth();
                itemHeight = (int) dragging.getHeight();
            }
        }
        curColor = backgroundColor;
        dragging = null;
    }

    public void removeLast() {
        if (nodes.size() > 0) {
            nodes.remove(nodes.size() - 1);
        }
    }

    public void reset() {
        allNames = null;
        nodes.clear();
    }
}
Menu choices = null;
Menu buttons = null;
TransitionPath path = null;
MenuItem dragging = null;

EventHandler eventHandler = null;

AnimationPath transPath = null;
AtomicTransition curAT = null;

HashMap visInfo = null;

String pathToData = "testdata.csv";
String curProjName = "Animation_Transitions_v8";
String warningText = null;

Table table = null;

int[] colors = {
    color(4, 90, 141),
    color(208, 239, 209),
};

int[] colors2 = {
  color(253,224,221),
  color(250,159,181)
};

int warningColor = color(245, 108, 108);

int defaultCol = 1;
int pointSize = 10;
int numTickets = 10;
int constantFrame = 30;
int constantFrameMore = 60;
float MAX_RADIUS = Integer.MAX_VALUE;
float totalStep = -1;
float curStep = -1;
int assumpDim= 1000;


public void initVisInfo() {
    visInfo = new HashMap();
    String[] strs0 = {
        "ThemeRiver", "Pie Chart", "Bar Chart"
    };
    visInfo.put("Line Graphs", new StringList(strs0));

    String[] strs1 = {
        "Rose Chart", "Line Graphs", "Bar Chart"
    };
    visInfo.put("Pie Chart", new StringList(strs1));

    String[] strs2 = {
        "Stacked Bar", "Line Graphs", "Pie Chart"
    };
    visInfo.put("Bar Chart", new StringList(strs2));

    String[] strs3 = {
        "Line Graphs", "Rose Chart", "Stacked Bar"
    };
    visInfo.put("ThemeRiver", new StringList(strs3));

    String[] strs4 = {
        "Pie Chart", "ThemeRiver", "Stacked Bar"
    };
    visInfo.put("Rose Chart", new StringList(strs4));

    String[] strs5 = {
        "Bar Chart", "Rose Chart", "ThemeRiver"
    };
    visInfo.put("Stacked Bar", new StringList(strs5));
}

public String mapToStandardName(String str) {
    if (str.equals("Line Graphs")) {
        return "Line";
    } else if (str.equals("Pie Chart")) {
        return "Pie";
    } else if (str.equals("Bar Chart")) {
        return "Bar";
    } else if (str.equals("ThemeRiver")) {
        return "Themeriver";
    } else if (str.equals("Rose Chart")) {
        return "Roses";
    } else if (str.equals("Stacked Bar")) {
        return "Stackedbar";
    } else {
        println("could not find a standard name for " + str);
        return null;
    }
}

public void loadData() {
    table = loadTable(pathToData, "header");
}


public float getColMax(Table t, int col) {
    float[] fs = t.getFloatColumn(col);
    return max(fs);
}

public float getColMin(Table t, int col) {
    float[] fs = t.getFloatColumn(col);
    return 0 > min(fs) ? min(fs) : 0;
}

public float getColSum(Table t, int col) {
    float[] fs = t.getFloatColumn(col);
    float sum = 0;
    for (float f: fs) {
        sum += f;
    }
    return sum;
}

public float getSubColSum(Table t, int col, int s, int e) {
    float[] fs = t.getFloatColumn(col);
    float sum = 0;
    for (int i = s; i <= e; i++) {
        sum += fs[i];
    }
    return sum;
}

public float getRowSum(Table t, int row) {
    float[] fs = getFloatRow(t, row);
    float sum = 0;
    return getSum(fs);
}

public float getSum(float[] fs) {
    float sum = 0;
    if(fs == null){
        return 0;
    }
    for (int i = 0; i < fs.length; i++) {
        sum += fs[i];
    }
    return sum;
}

public float getSum(int[] fs) {
    float sum = 0;
    if(fs == null){
        return 0;
    }
    for (int i = 0; i < fs.length; i++) {
        sum += fs[i];
    }
    return sum;
}

public float getSubRowSum(Table t, int row, int s, int e) {
    if(s > e){
        return 0;
    }
    float[] fs = getFloatRow(t, row);
    float sum = 0;
    for (int i = s - 1; i <= e - 1; i++) {
        sum += fs[i];
    }
    return sum;
}

public float getRowSumMax(Table t) {
    float sum = 0;
    for (int i = 0; i < t.getRowCount(); i++) {
        float[] fs = getFloatRow(t, i);
        float tmp = getSum(fs);
        if (tmp - sum > 0) {
            sum = tmp;
        }
    }
    return sum;
}

public float getSubRowSumMax(Table t, int s, int e) {
    float sum = 0;
    if(e > s)
        return 0;
    for (int i = 0; i < t.getRowCount(); i++) {
        float[] fs = getFloatRow(t, i, s, e);
        float tmp = getSum(fs);
        if (tmp - sum > 0) {
            sum = tmp;
        }
    }
    return sum;
}

public float canvasXScale(float x) {
    return x + canvas_margin_left;
}

public float canvasYScale(float y) {
    return canvasH - canvas_margin_bottom - y;
}

public float[] getFloatRow(Table t, int row) {
    float[] fs = new float[t.getColumnCount() - 1];
    for (int i = 0; i < fs.length; i++) {
        fs[i] = t.getFloat(row, i + 1);
    }
    return fs;
}

public float[] getFloatRow(Table t, int row, int s, int e) {
    float[] fs = new float[e - s + 1];
    for (int i = 0; i < fs.length; i++) {
        fs[i] = t.getFloat(row, i + s);
    }
    return fs;
}


public float zeroSpline(float t, float sPos, float ePos){
     return calcSpline(t, sPos, ePos, 0, 0);
}

public float calcSpline(float t, float sPos, float ePos, float sVel, float eVel) {
    float value = t * t * t * (2.0f * sPos - 2.0f * ePos + 1.0f * sVel + 1.0f * eVel) +
                  t * t * (-3.0f * sPos + 3.0f * ePos - 2.0f * sVel - 1.0f * eVel) +
                  t * (sVel) +
                  (sPos);
    return value;
}

public float qerp(float s, float e, float p){
    return lerp(s, e, p * p);
}
public void drawChoices(){
	if(choices == null){
        choices = new Menu();

        choices.addItem("Line Graphs", "ellipse")
        .setBackgroundColor(color(123,192,219,230))
        .setTextColor(color(255))
        .setStrokeWeight(0)
        ;

        choices.addItem("Pie Chart", "ellipse")
        .setBackgroundColor(color(123,192,219,230))
        .setTextColor(color(255))
        .setStrokeWeight(0)
        ;

        choices.addItem("Bar Chart", "ellipse")
        .setBackgroundColor(color(123,192,219,230))
        .setTextColor(color(255))
        .setStrokeWeight(0)
        ;

        choices.addItem("ThemeRiver", "ellipse")
        .setBackgroundColor(color(123,192,219,230))
        .setTextColor(color(255))
        .setStrokeWeight(0)
        ;

        choices.addItem("Rose Chart", "ellipse")
        .setBackgroundColor(color(123,192,219,230))
        .setTextColor(color(255))
        .setStrokeWeight(0)
        ;

        choices.addItem("Stacked Bar", "ellipse")
        .setBackgroundColor(color(123,192,219,230))
        .setTextColor(color(255))
        .setStrokeWeight(0)
        ;

        choices.setMargins(10, 10, 10, 10)
        .setColNum(3)
        .setRowNum(2)
        .setBackgroundColor(color(225))
        ;
	}

	choices.setPosition(choiceX, choiceY)
           .setSize(choiceW, choiceH)
           ;

	choices.rebuildMenu();
	choices.drawMe();
}

public void drawPath(){
    if(path == null){
        path = new TransitionPath();
        path.setNodeColor(color(126,179,207))
        .setLineColor(color(126,179,207))
        .setTextColor(color(255))
        .setBackgroundColor(color(245))
        ;
    }

    path.setPosition(pathX, pathY)
        .setSize(pathW, pathH);

    path.rebuildPath();
    path.drawMe();
}

public void drawButtons(){
    fill(240);
	noStroke();
	rect(buttonX, buttonY, buttonW, buttonH);
    if(buttons == null){
    	buttons = new Menu();
        buttons.setBackgroundColor(color(225));

    	buttons.addItem("remove","rect", eventHandler, "remove")
        .setBackgroundColor(color(123,192,200,200))
        .setTextColor(color(255))
        .setStrokeColor(color(4,90,141))
        .setStrokeWeight(0)
        ;

        buttons.addItem("interpret", "rect", eventHandler, "interpret")
        .setBackgroundColor(color(123,192,200,200))
        .setTextColor(color(255))
        .setStrokeColor(color(4,90,141))
        .setStrokeWeight(0)
        ;

        buttons.addItem("begin", "rect", eventHandler, "begin")
        .setBackgroundColor(color(123,192,200,200))
        .setTextColor(color(255))
        .setStrokeColor(color(4,90,141))
        .setStrokeWeight(0)
        ;
        
        buttons.addItem("reset", "rect", eventHandler, "reset")
        .setBackgroundColor(color(123,192,200,200))
        .setTextColor(color(255))
        .setStrokeColor(color(4,90,141))
        .setStrokeWeight(0)
        ;

        buttons.setMargins(10, 11, 10, 11)
        .setColNum(1)
        .setRowNum(4)
        ;
    }
     buttons.setPosition(buttonX, buttonY)
        .setSize(buttonW, buttonH)      
        ;

    buttons.rebuildMenu();
    buttons.drawMe();
}

 public void drawCanvas(){
       clearCanvas();
       if(transPath != null && curAT != null){
            if(!curAT.isEnd()){
                curAT.next();
            } else if(curAT.isEnd() && transPath.hasNextAT()){
                curAT = transPath.nextAT();
            } else if(curAT.isEnd() && !transPath.hasNextAT()){
                curAT.next();
            } else {
                println("uncovered cases");
            }
       }
   }
   
 protected void clearCanvas(){
        fill(255);
        noStroke();
        rectMode(CORNER);
        rect(canvasX, canvasY, canvasW, canvasH);
   }

 public void drawWarning(){  
    if(warningText != null){
        pushStyle();
        fill(warningColor);
        textAlign(RIGHT);
        textSize(fontSize * 2);
        text(warningText, canvasW, canvasH);
        popStyle();
    }
 
 }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Animation_Transitions_v8" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
