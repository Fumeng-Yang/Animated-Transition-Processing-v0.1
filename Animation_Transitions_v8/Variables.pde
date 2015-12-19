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

color[] colors = {
    color(4, 90, 141),
    color(208, 239, 209),
};

color[] colors2 = {
  color(253,224,221),
  color(250,159,181)
};

color warningColor = color(245, 108, 108);

int defaultCol = 1;
int pointSize = 10;
int numTickets = 10;
int constantFrame = 30;
int constantFrameMore = 60;
float MAX_RADIUS = Integer.MAX_VALUE;
float totalStep = -1;
float curStep = -1;
int assumpDim= 1000;


void initVisInfo() {
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

String mapToStandardName(String str) {
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

void loadData() {
    table = loadTable(pathToData, "header");
}


float getColMax(Table t, int col) {
    float[] fs = t.getFloatColumn(col);
    return max(fs);
}

float getColMin(Table t, int col) {
    float[] fs = t.getFloatColumn(col);
    return 0 > min(fs) ? min(fs) : 0;
}

float getColSum(Table t, int col) {
    float[] fs = t.getFloatColumn(col);
    float sum = 0;
    for (float f: fs) {
        sum += f;
    }
    return sum;
}

float getSubColSum(Table t, int col, int s, int e) {
    float[] fs = t.getFloatColumn(col);
    float sum = 0;
    for (int i = s; i <= e; i++) {
        sum += fs[i];
    }
    return sum;
}

float getRowSum(Table t, int row) {
    float[] fs = getFloatRow(t, row);
    float sum = 0;
    return getSum(fs);
}

float getSum(float[] fs) {
    float sum = 0;
    if(fs == null){
        return 0;
    }
    for (int i = 0; i < fs.length; i++) {
        sum += fs[i];
    }
    return sum;
}

float getSum(int[] fs) {
    float sum = 0;
    if(fs == null){
        return 0;
    }
    for (int i = 0; i < fs.length; i++) {
        sum += fs[i];
    }
    return sum;
}

float getSubRowSum(Table t, int row, int s, int e) {
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

float getRowSumMax(Table t) {
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

float getSubRowSumMax(Table t, int s, int e) {
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

float canvasXScale(float x) {
    return x + canvas_margin_left;
}

float canvasYScale(float y) {
    return canvasH - canvas_margin_bottom - y;
}

float[] getFloatRow(Table t, int row) {
    float[] fs = new float[t.getColumnCount() - 1];
    for (int i = 0; i < fs.length; i++) {
        fs[i] = t.getFloat(row, i + 1);
    }
    return fs;
}

float[] getFloatRow(Table t, int row, int s, int e) {
    float[] fs = new float[e - s + 1];
    for (int i = 0; i < fs.length; i++) {
        fs[i] = t.getFloat(row, i + s);
    }
    return fs;
}


float zeroSpline(float t, float sPos, float ePos){
     return calcSpline(t, sPos, ePos, 0, 0);
}

float calcSpline(float t, float sPos, float ePos, float sVel, float eVel) {
    float value = t * t * t * (2.0 * sPos - 2.0 * ePos + 1.0 * sVel + 1.0 * eVel) +
                  t * t * (-3.0 * sPos + 3.0 * ePos - 2.0 * sVel - 1.0 * eVel) +
                  t * (sVel) +
                  (sPos);
    return value;
}

float qerp(float s, float e, float p){
    return lerp(s, e, p * p);
}
