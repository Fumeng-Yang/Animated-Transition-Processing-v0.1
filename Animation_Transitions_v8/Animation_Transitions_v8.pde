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

void setup(){
	size((int)(displayWidth * 0.8), int(displayHeight * 0.9));//, "processing.core.PGraphicsRetina2D");
	rectMode(CORNER);
	
	frame.setResizable(true);
	frame.setTitle("Fumeng's Animation Transition");

	eventHandler = new EventHandler();
	initVisInfo();
	loadData();
	frameRate(50);

	main = this;
}

void setParameters(){
	fontSize = width / 80;
	pointSize = width < height ? width / 70 : height / 70;
    textSize(fontSize);	
	canvasX = 0;
	canvasY = 0;
	canvasW = width;
	canvasH = int(height * 0.75);

	choiceX = 0;
	choiceY = canvasH;
	choiceW = int(0.27 * width);
	choiceH = int(height - canvasH);

	pathX = choiceW;
	pathY = canvasH;
	pathW = int(0.63 * width);
	pathH = choiceH;

	buttonX = choiceW + pathW;
	buttonY= canvasH;
	buttonW = int(width - choiceW - pathW);
	buttonH = choiceH;

	canvas_margin_top = 0.1 * float(canvasH);
	canvas_margin_bottom = 0.15 * float(canvasH);
	canvas_margin_left = 0.1 * float(canvasW);
	canvas_margin_right = 0.1 * float(canvasW);

	canvasWr = canvasW - canvas_margin_right - canvas_margin_left;
	canvasHr = canvasH - canvas_margin_bottom - canvas_margin_top;

	MAX_RADIUS = canvasHr / 8.0;
}

void draw(){

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


