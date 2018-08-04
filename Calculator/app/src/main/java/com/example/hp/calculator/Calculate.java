package com.example.hp.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.hp.calculator.StringExam.isOperation;
import static com.example.hp.calculator.StringExam.killBrackets;

class infinityException extends  Exception{}

public class Calculate {


    static String [] forWkl ={" _(:з」∠)_ "," QaQ"," QAQ"," qwq"," OvO"," 0v0"," 0 v 0"," O v O"," ( ˘•灬•˘ )"," 0^0真是可爱"};

    private static final Map<String,Integer> basic =new HashMap<>();
    static {
        basic.put("+",1);
        basic.put("-", 1);
        basic.put("×", 2);
        basic.put("÷", 2);
        basic.put("^",3);
        basic.put("(", 0);
    }

    protected static List<String> parseString(String s,boolean charge) {//分割数字与操作符    将×与÷后的减号绑定到后面的数字上
        String source = new String(s);
        if(charge&&source.contains("-(")) {
            try {
                source = killBrackets(source);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        List<String> strings = new ArrayList<String>();
        int i=0;
        int j=0;
        while (j<source.length()){
            while (i < source.length() && isOperation(source.charAt(i)) == false && source.charAt(i) != '('
                    && source.charAt(i) != ')') {
                i++;
            }
            //到末尾或操作符
            if(i!=j)
                strings.add(source.substring(j,i));
            if(i == source.length()){//末尾
                break;
            }else{
                strings.add(source.substring(i,i+1));
                i++;
            }
            j = i;
        }//分隔完毕
//        System.out.println(strings);
        for(i = 0 ; i<strings.size()-1;i++){
            if((i==0&&strings.get(0).equals("-"))||(strings.get(i).equals("-")&&(i>0&&(strings.get(i-1).equals("×")
                    ||strings.get(i-1).equals("÷")||strings.get(i-1).equals("^")||strings.get(i-1).equals("("))||strings.get(i-1).equals("+")||strings.get(i-1).equals("-")/*这里可加+、-实现真正的负号*/))){
                if(strings.get(i+1).equals("-")==false)//3×-(4-5)
                    strings.set(i+1,"-"+strings.get(i+1));
                else
                    strings.remove(i);
                strings.remove(i);
                i = 0;
            }
        }
//        System.out.println(strings);
        return strings;
    }

    private static List<String> infixToPostfix(List<String> strings){
        List<String> stringList = new ArrayList<>();
        List<String> operations = new ArrayList<>();
        int i = 0;
        for(i=0;i<strings.size();i++){
            String s  =strings.get(i);
            if(s.length()==1&&(isOperation(s.charAt(0))||s.charAt(0)=='(')){//运算符或左括号
                if(operations.size()==0||s.charAt(0)=='('||(basic.get(operations.get(operations.size()-1))<basic.get(s))){
                    operations.add(s);
                }else {
                    while(operations.size()>0&&basic.get(operations.get(operations.size()-1))>=basic.get(s)){//弹栈
                        stringList.add(operations.remove(operations.size()-1));
                    }
                    operations.add(s);
                }
            }else if(s.charAt(0)==')'){//右括号
                while(  !operations.get(operations.size()-1).equals("(")){//出栈
                    stringList.add(operations.remove(operations.size()-1));
                }
                operations.remove(operations.size()-1);//左括号出栈
            }else {//数字
                stringList.add(s);
            }
        }
        while(operations.size()!=0){
            stringList.add(operations.remove(operations.size()-1));
        }
        return stringList;
    }

    private static String calculate(List<String> strings) throws infinityException{
        DecimalFormat df = new DecimalFormat("#.#########");
        //DecimalFormat std = new DecimalFormat("#,###.#########");
        int i = 0;
        for(i = 0;i<strings.size();i++){
            String s = (String) strings.get(i);
            if(s.length() == 1&&isOperation(s.charAt(0))){//找到一个操作符
                switch (s){
                    case "+":strings.set(i-2, String.valueOf((new BigDecimal(strings.get(i-2))).add(new BigDecimal(strings.get(i-1)))));break;
                    case "-":strings.set(i-2, String.valueOf((new BigDecimal(strings.get(i-2))).subtract(new BigDecimal(strings.get(i-1)))));break;
                    case "×":strings.set(i-2, String.valueOf((new BigDecimal(strings.get(i-2))).multiply(new BigDecimal(strings.get(i-1)))) );break;
                    case "÷":BigDecimal bigDecimal = new BigDecimal(strings.get(i-1));
                        if(bigDecimal.compareTo(BigDecimal.valueOf(0))==0){throw new infinityException();}
                        else {BigDecimal bd = new BigDecimal(strings.get(i-2)).divide(bigDecimal,9, RoundingMode.HALF_UP);
                                    strings.set(i-2, String.valueOf((df.format(bd))));}break;
                    case "^"://strings.set(i-2, String.valueOf((new BigDecimal(strings.get(i-2))).pow(Integer.parseInt(strings.get(i-1)))));break;
                        Double x = Double.parseDouble(strings.get(i-2));
                        Double y= Double.parseDouble(strings.get(i-1));
                        if(x==0&&y==0){
                            int random = (int)(Math.random()*10);
                            strings.set(i-2,forWkl[random]);
                        }else{
                        Double re = Math.pow(x,y);
                        strings.set(i-2,String.valueOf(df.format(re)));
                        }break;//弱化次方
                    default:break;
                }
                strings.remove(i-1);
                strings.remove(i-1);
                i=0;
            }
        }
        return strings.get(0);
    }

    public static String arrange(String source) throws Exception{
        return calculate(infixToPostfix(parseString(source,true)));
    }

}
