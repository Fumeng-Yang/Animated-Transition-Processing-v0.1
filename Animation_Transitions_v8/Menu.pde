import java.lang.reflect.*;
import java.lang.String;

class MenuItem {
    float x = -1;
    float y = -1;
    float iWidth = -1;
    float iHeight = -1;
    String text = null;
    color textColor = color(0);
    color backgroundColor = color(255);
    color curColor = backgroundColor;
    color activeColor = color(166, 189, 219);
    color highLightColor = color(208, 209, 230);
    color strokeColor = color(208, 209, 230);
    color disableColor = color(208, 209, 230);
    color shallowColor = color(230, 230, 230, 230);
    color shallowTextColor = color(60, 60, 60, 60);
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

    void setAble(boolean flag) {
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

    boolean isAble() {
        return this.able;
    }

    float getX() {
        return x;
    }

    float getY() {
        return y;
    }

    float getWidth() {
        return iWidth;
    }

    float getHeight() {
        return iHeight;
    }

    color getActiveColor() {
        return activeColor;
    }

    color getTextColor() {
        return textColor;
    }

    color getBackgroundColor() {
        return backgroundColor;
    }

    color getHighLightColor() {
        return highLightColor;
    }

    MenuItem setTextColor(color c) {
        this.textColor = c;
        return this;
    }

    MenuItem setBackgroundColor(color c) {
        this.backgroundColor = c;
        return this;
    }

    MenuItem setActiveColor(color c) {
        this.activeColor = c;
        return this;
    }

    MenuItem setStrokeColor(color c){
        this.strokeColor = c;
        return this;
    }

    void setColor(color c) {
        this.curColor = c;
    }

    String getText() {
        return text;
    }

    String getName() {
        return text;
    }

    String getShape() {
        return this.shape;
    }

    void setShallow(boolean sh) {
        this.drawShallow = sh;
    }

    MenuItem setStrokeWeight(int i) {
        this.strokeWeightNum = i;
        return this;
    }

    boolean isOn() {
        if (shape.equals("rect")) {
            return (mouseX <= x + iWidth) && (mouseX >= x) && (mouseY <= y + iHeight) && (mouseY >= y);
        } else if (shape.equals("ellipse")) {
            return sq(mouseX - (x + iWidth / 2.0)) / sq(iWidth / 2.0) + sq(mouseY - (y + iHeight / 2.0)) / sq(iHeight / 2.0) <= 1;
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
            rect(x, y, iWidth, iHeight, 3.0);
            if (drawShallow) {
                fill(shallowColor);
                stroke(shallowTextColor);
                rect(mouseX - iWidth, mouseY - iHeight, iWidth, iHeight, 3.0);
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
                ellipse(mouseX - iWidth / 2.0, mouseY - iHeight / 2.0, iWidth, iHeight);
            }
        } else {
            println("do not support " + shape);
        }
        textAlign(CENTER);
        fill(textColor);
        text(this.text, x + iWidth / 2.0, y + iHeight / 2.0 + fontSize / 2.0);
        if (drawShallow) {
            fill(shallowTextColor);
            text(this.text, mouseX - iWidth / 2.0, mouseY - fontSize / 2.0, iWidth, iHeight);
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
    protected color textColor = color(0);
    protected color backgroundColor = color(240);
    protected color curColor = backgroundColor;
    protected color activeColor = color(166, 189, 219);
    protected color highLightColor = color(208, 209, 230);
    protected color disableColor = color(208, 209, 230);
    protected color shallowColor = color(230, 230, 230, 230);
    protected color shallowTextColor = color(60, 60, 60, 60);

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

    void rebuildMenu() {
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

    Menu setTextColor(color c) {
        this.textColor = c;
        return this;
    }

    Menu setBackgroundColor(color c) {
        this.backgroundColor = c;
        return this;
    }

    Menu setActiveColor(color c) {
        this.activeColor = c;
        return this;
    }

    Menu setColor(color c) {
        this.curColor = c;
        return this;
    }

    int getNumItems() {
        return items.size();
    }

    ArrayList < MenuItem > getItems() {
        return items;
    }

    MenuItem getItem(int index) {
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
