public class TransitionPath extends Menu {
    private color nullColor = color(230, 230, 230);
    private color nullTextColor = color(100, 100, 100);
    private int distance = 50;
    private int itemWidth = -1;
    private int itemHeight = -1;
    private int lineWeight = 3;
    color lineColor = color(210, 219, 230);
    color nodeColor = color(210, 219, 230);
    private StringList allNames = null;

    private ArrayList < MenuItem > nodes = new ArrayList <MenuItem> ();

    public TransitionPath() {

    }

    public void setDistance(int dist) {
        this.distance = dist;
    }

    public TransitionPath setNodeColor(color c) {
        this.nodeColor = c;
        return this;
    }

    public TransitionPath setLineColor(color c) {
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
            text("Please drag visualizations here", x + iWidth / 2.0, y + iHeight / 2.0);
        } else {
            int n = nodes.size();
            int margin_left = 10;
            // do good
            itemWidth = (int) choices.getItemWidth();
            itemHeight = (int) choices.getItemHeight();
            distance = int(itemWidth);

            while (n * (itemWidth + distance) > iWidth) {
                distance = distance / 2 + 5;
                //itemWidth = int(iWidth / n - distance);
            }
            margin_left = int((iWidth - n * (itemWidth + distance)) / 2.0);

            int curX = int(margin_left + x), curY = int(y + (iHeight / 2.0));
            ellipseMode(CENTER);

            for (int i = 0; i < n; i++) {
                MenuItem mi = nodes.get(i);
                mi.setPosition(curX, curY - itemHeight / 2.0)
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
            text("Please drag visualization here", x + iWidth / 2.0, y + iHeight / 2.0);
        } else {
            int n = nodes.size();
            int j = 0;
            for (int i = 0; i < n; i++) {
                if (i != n - 1) {
                    strokeWeight(lineWeight);
                    stroke(lineColor);
                    line(nodes.get(i).getX() + itemWidth / 2.0, nodes.get(i).getY() + nodes.get(i).getHeight() / 2.0,
                        nodes.get(i).getX() + distance + itemWidth, nodes.get(i).getY() + nodes.get(i).getHeight() / 2.0);
                    line(nodes.get(i).getX() + distance + itemWidth, nodes.get(i).getY() + nodes.get(i).getHeight() / 2.0,
                        nodes.get(i).getX() + distance + itemWidth - canvasH / 100.0, nodes.get(i).getY() + nodes.get(i).getHeight() / 2.0 + canvasH / 100.0
                        );
                    line(nodes.get(i).getX() + distance + itemWidth, nodes.get(i).getY() + nodes.get(i).getHeight() / 2.0,
                        nodes.get(i).getX() + distance + itemWidth - canvasH / 100.0, nodes.get(i).getY() + nodes.get(i).getHeight() / 2.0 - canvasH / 100.0
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
                        translate(nodes.get(i).getX() - distance / 2.0, nodes.get(i).getY() + nodes.get(i).getHeight() / 2.0 - fontSize);
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
            float startY = nodes.get(0).getY() + nodes.get(0).getHeight() + pathH * 0.2;
            float endX = nodes.get(nodes.size() - 1).getX() + nodes.get(0).getWidth();
            strokeWeight(((height < width) ? height : width) * 0.01);
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
