package com.nwcg.icbs.yantra.soap;

import java.util.*;
import java.io.*;

public class CodeTemplate extends Hashtable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -4598701525638305384L;
	String str;
    String name;
    
    /** Creates new CodeTemplate */
    public CodeTemplate() {
    	
    } 
    
    public CodeTemplate(String str) {
        this.str = str;
        
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    
    
    public String getName(){
        return name;
    }
    
    public Vector getSlots(){
        Vector v = new Vector();
        
        //foo ^AB^  sock ^CF^ coo
        int cIndex = str.indexOf('^');
        
        while(cIndex != -1){
            
            
            
            int cIndexPrime = str.indexOf('^', cIndex+1);
            if(cIndexPrime < 0) {
                cIndex = -1;
                continue;
            }
           
            //System.out.println("cindexA: " + cIndexPrime);
            String slot = str.substring(cIndex+1,cIndexPrime);
            //System.out.println("S:"+slot);
            v.addElement(slot);
            
            cIndex = str.indexOf('^', cIndexPrime+1);
            //System.out.println("cindexB: " + cIndex);
        }
        
        return v;
        }
   
    
    
    public String concatenateVars(Vector v){
        Enumeration e = v.elements();
        
        String s = "";
        while(e.hasMoreElements()){
            String so = (String)e.nextElement();
            s = s + so + "\n";
        }
            
        return s;
    }

    public void setSlot(String slotname, String value){
        put(slotname,value);
        
    }
    
    String subparse(String s, String slotname, String value, Boolean on){
        //methodname> throws CharlieeException{
        //System.out.println("sp: " + s);
        String line ="";
        StringTokenizer tok2 = new StringTokenizer(s,"^");
        String sC = tok2.nextToken();
        //System.out.println("sC: " + sC);
        //System.out.println("on2: " + on);
                if(sC.equals(slotname)){
                    
                    String sD = "";
                    if(tok2.hasMoreTokens()) sD = tok2.nextToken();
                    
                    //System.out.println("sD:" + sD);
                    line = value+sD;
                    //System.out.println("M:" +line);
                    //on = new Boolean(true);
                    //System.out.println(on);
                    return "\\"+line;
                }
        
                else if(on.booleanValue()){
                    on = new Boolean(false);
                    return s;
                }else{
                    return "^"+s;
                }
        
        //TODO: THere is a bug with this.  If you don't have a char after the last '^', then it will be lost.
                //else return s;
  
    }
    public void replaceSlot(String slotname, String value){
        //System.out.println("RS");
   String strout = "";     
 try{
      
        StringReader sr = new StringReader(str);
        
        LineNumberReader lnr = new LineNumberReader(sr);
              
        while(lnr.ready()){
            //System.out.println("LNR ready!");
            String line = lnr.readLine();
            if(line == null)          break;
    
       
            
              //          System.out.println("LNR ready1: " + line);
            String lineout = "";
            //System.out.println("L:"+line);
            if(line.startsWith("!")) continue; //skipp over comments
            if(line.startsWith("^")) line = "\\"+line;
                
                //          System.out.println("LNR ready2");
            StringTokenizer tok = new StringTokenizer(line,"^");
            //System.out.println ("here1");
            if(!tok.hasMoreTokens()) {
                strout = strout +line +"\n";
                continue;
            }
            
            String sA1 = tok.nextToken();
            lineout = lineout + sA1;
            
            //System.out.println("lineoutA: " + lineout);
            Boolean on = new Boolean(false);
            while(tok.hasMoreTokens()){
                
            sA1 = tok.nextToken();
            //lineout = lineout + sA1;
            
            //System.out.println("sA1: " + sA1);
            
            String x = subparse(sA1,slotname,value,on);
           
            
            //System.out.println(x);
            if(x.startsWith("\\")) {
                on = new Boolean(true);
                lineout = lineout+x.substring(1);
            }
            else {
                on = new Boolean(false);
                lineout = lineout+x;
            }
            //System.out.println("on1: " + on);
            
           
            //tok = new StringTokenizer(line,"<");
           
            }
            //System.out.println("LineoutB: '" + lineout+"'");
            if(lineout.equals("null")) lineout="";
            if(lineout.startsWith("\\")){
                strout = strout +lineout.substring(1)+"\n";
            }
            
          
            else strout = strout +lineout+"\n";
                
            
              //           System.out.println ("hereY");
        }
        
       }catch (Exception e){
           System.out.println(e);
       }
       
             //System.out.println ("hereX");
       str = strout;
    }

    //this should first check to see that all necessary slots are filled in
    public String generateCode(){
        //add slot for date
      //  System.out.println("GenCode:" + name );
        //setSlot("date", (new Date()).toString());
        
        Enumeration e = keys();
        
        while(e.hasMoreElements()){
            String key = (String)e.nextElement();
            String value = (String) get(key);
            
           // ProfileTimer pt = new ProfileTimer();
        //    pt.start();
        //    System.out.println(key+":"+value);
            replaceSlot(key,value);
           //  pt.stop("CodeTemplate","StrLenght: " + str.length());
            //pt.stop("CodeTemplate","ValLength: " + value.length());
            //pt.stop("CodeTemplate","ReplaceSlot:"+key);
            
        }
      //  System.out.println("finished gen code");
        //interate through all hashtable keys, replacing them 
        return str;
    }


}
