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
