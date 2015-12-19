void drawChoices(){
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

void drawPath(){
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

void drawButtons(){
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

 void drawWarning(){  
    if(warningText != null){
        pushStyle();
        fill(warningColor);
        textAlign(RIGHT);
        textSize(fontSize * 2);
        text(warningText, canvasW, canvasH);
        popStyle();
    }
 
 }
