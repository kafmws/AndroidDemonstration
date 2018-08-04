package com.example.hp.calculator;

import android.os.Build;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.example.hp.calculator.Calculate.arrange;
import static com.example.hp.calculator.Calculate.parseString;

public abstract class StringExam {

    private static DecimalFormat df =new DecimalFormat("#,###");

    public static boolean isNum(char obj){
        if(obj>='0'&&obj<='9'||obj=='∞')
            return true;
        return false;
    }

    public static boolean isOperation(char obj){
        if(obj=='+'||obj=='-'||obj=='×'||obj=='÷'||obj=='^')
            return true;
        return false;
    }

    public static int cntCharacter(String s){
        int n = s.length()-1;
        if(n<0)
            return 100;
        int cnt = 0;
        while(n>0&&!isNum(s.charAt(n--))){
            cnt++;
        }
        return cnt;
    }

    public static int cntBrackets(String s){//统计左括号减去右括号
        int cntLeft = 0;
        int cntRight = 0;
        for(int i = 0;i<s.length();i++){
            if(s.charAt(i)=='('){
                cntLeft++;
            }else if(s.charAt(i)==')'){
                cntRight++;
            }
        }
        return cntLeft-cntRight;
    }

    public static boolean judgeDel(String s){//当前数字是否有小数点
        boolean flagDel=false;
        int index = Math.max(Math.max(Math.max(Math.max(s.lastIndexOf("+"),s.lastIndexOf("-"))
                ,s.lastIndexOf("×")),s.lastIndexOf("÷")),s.lastIndexOf("^"));//当前数字
        if(index == -1){
            index=0;
        }
        for(int i = index;i<s.length();i++){
            if(s.charAt(i)=='.'){
                flagDel = true;
                break;
            }
        }
        return flagDel;
    }

    public static String devideByDel(String s){
        if(s.length()<4||s.charAt(0)==' ')
            return s;
        String [] strings = s.split("\\.");
        boolean flag = true;
        for(int i = 0;i<strings[0].length();i++){
            if(!isNum(strings[0].charAt(i))){
                flag = false;break;
            }
        }
        if(flag&&strings[0].length()>=4){//&&strings[0].charAt(0)!='王'
            strings[0] = df.format(new BigDecimal(strings[0]));
            if(strings.length == 2){
                strings[0] = strings[0].concat("."+strings[1]);
            }else if(s.charAt(s.length()-1) == '.'){
                strings[0] = strings[0].concat(".");
            }
            return strings[0];
        }
        return s;
    }

    public static String parseStringAndDevideByDel(String ss){
        String source = new String(ss);
        List<String> strings = parseString(source,false);
//        for (String s : strings){
//            if(s.length()>3){
//                s = devideByDel(s);
//            }
//        }
        for(int i = 0;i<strings.size();i++){
            if(strings.get(i).length()>3){
                strings.set(i,devideByDel(strings.get(i)));
            }
        }
        String s = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            s = String.join("", strings);
        }
        return s;
    }

    public static String completeString(String s){
        StringBuffer sb = new StringBuffer(s);
        while(sb.length()>0&&isNum(sb.charAt(sb.length()-1))==false&&sb.charAt(sb.length()-1)!=')'){//非数字
            sb = sb.delete(sb.length()-1,sb.length());
        }
        int n = cntBrackets(sb.toString());//处理括号
        while(n--!=0){
            sb.append(')');
        }
        return sb.toString();
    }


    public static String killBrackets(String s) throws Exception {
        String source = new String(s);
        int leftBracket = source.lastIndexOf("-(")+1;//-1  +1
        if(leftBracket == 0)return source;
        int rightBracket = 0;
        StringBuffer sb = new StringBuffer();
        while(leftBracket!=0){
            rightBracket = source.indexOf(')',leftBracket);
            if(rightBracket == -1)rightBracket = source.length();//定位要处理的括号的下标
            String firstTarget = arrange(completeString(source.substring(leftBracket+1,rightBracket)));
            sb = new StringBuffer(source.substring(0,leftBracket));
//            if(firstTarget.charAt(0)=='-'&&sb.charAt(sb.length()-1)=='-'){
//                sb.deleteCharAt(sb.length()-1);
//                firstTarget =firstTarget.substring(1);
//            }
            sb.append(firstTarget);
            if(cntBrackets(sb.toString())>0){
                sb.append(source.substring(rightBracket+1,source.length()));
            }
            leftBracket = sb.lastIndexOf("-(")+1;
            source = sb.toString();
        }
        return sb.toString();
    }


}
