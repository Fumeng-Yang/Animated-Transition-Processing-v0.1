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

void mouseClicked() {
    buttons.handleMouseClicked();;
}

void mouseDragged() {
    choices.handleMouseDragged();
    path.handleMouseDragged();
}

void mousePressed() {
    choices.handleMousePressed();
}

void mouseReleased() {
    choices.handleMouseReleased();
    path.handleMouseReleased();
}

void mouseMoved() {
    warningText = null;
}
