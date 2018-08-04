package com.example.hp.calculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.Format;

import static com.example.hp.calculator.Calculate.arrange;
import static com.example.hp.calculator.StringExam.*;

public class InteractActivity extends AppCompatActivity implements View.OnClickListener{

    private StringBuffer text = new StringBuffer();
    private TextView edit_screen;
    private TextView result_screen;
    private String s = new String();
    private boolean flag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_activity_main);
        //0x7f070022 ~0032
        int id = 0x7f070022;
        for(id=0x7f070022;id<=0x7f070034;id++){
            findViewById(id).setOnClickListener(this);
        }
        findViewById(R.id.btn_clear).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                text = new StringBuffer();
                s = "";
                edit_screen.setText("0");
                result_screen.setText("");
                return false;
            }
        });
        edit_screen = findViewById(R.id.text_screen);
        result_screen = findViewById(R.id.text_result);
        findViewById(R.id.btn_leftBracket).setOnClickListener(this);
        findViewById(R.id.btn_rightBracket).setOnClickListener(this);
//        edit_screen.setMovementMethod(ScrollingMovementMethod.getInstance());
    }


    @Override
    public void onClick(View view) {
        int textLen = text.length();
        switch (view.getId()){
            case R.id.btn_0:
                if(flag){
                    text = new StringBuffer();
                    textLen = 0;
                }
                if(textLen==0||text.charAt(textLen-1)!=')')
                    text.append('0');
                break;
            case R.id.btn_1://0x7f070022
            case R.id.btn_2:
            case R.id.btn_3:
            case R.id.btn_4:
            case R.id.btn_5:
            case R.id.btn_6:
            case R.id.btn_7:
            case R.id.btn_8:
            case R.id.btn_9:
                if(flag){
                    text = new StringBuffer();
                    textLen = 0;
                }
                if(textLen==0||text.charAt(textLen-1)!=')')
                    text.append((char) ('1'+view.getId()-0x7f070023));
                break;
                //根据id判断所加字符
            case R.id.btn_del:
                if(textLen==0)
                    text.append("0.");
                else if(isNum(text.charAt(textLen-1))&&judgeDel(text.toString())==false)
                    text.append('.');
                break;
            case R.id.btn_leftBracket:
                if(textLen==0||(textLen>0&&text.charAt(textLen-1)!='.'&&isNum(text.charAt(textLen-1))==false))
                    text.append('(');
                    break;
            case R.id.btn_rightBracket:
                if(cntBrackets(text.toString())>0&&textLen>0&&!isOperation(text.charAt(textLen-1))&&text.charAt(textLen-1)!='.'&&text.charAt(textLen-1)!='(')
                    text.append(')');
                break;
            case R.id.btn_plus:
                //if(textLen>0&&!isOperation(text.charAt(textLen-1))&&text.charAt(textLen-1)!='.')
                if(textLen>0&&(isNum(text.charAt(textLen-1))||text.charAt(textLen-1)==')'))
                    text.append('+');
                else if(textLen>0&&cntCharacter(text.toString())==1)
                    text.setCharAt(textLen-1,'+');
                break;
            case R.id.btn_minus:
//             if(textLen==0||(text.charAt(textLen-1)!='+'&&text.charAt(textLen-1)!='-'&&text.charAt(textLen-1)!='.'))
                if(textLen==0)
                    text.append('-');
                else if(textLen==1){
                    if(text.charAt(textLen-1)!='-')
                    text.append('-');
                }
                else if(textLen == 2&&text.charAt(0)=='('){
                    if(text.charAt(1)!='-')
                        text.append('-');
                }
                else if(text.charAt(textLen-1)!='.'&&cntCharacter(text.toString())<2||(cntCharacter(text.toString())==2&&(text.charAt(textLen-1)!='-'&&text.charAt(textLen-2)!='-'))||text.charAt(textLen-1)=='(')
                    text.append('-');
//                if(textLen>0){
//                    Toast.makeText(this,String.valueOf(text.charAt(textLen-1)!='.'&&cntCharacter(text.toString())<=2||(textLen==1&&text.charAt(textLen-1)!='-')),Toast.LENGTH_SHORT).show();
//                }
                break;
            case R.id.btn_multiply:
                if(textLen>0&&(isNum(text.charAt(textLen-1))||text.charAt(textLen-1)==')'))
                    text.append('×');
                else if(textLen>0&&cntCharacter(text.toString())==1)
                    text.setCharAt(textLen-1,'×');
                break;
            case R.id.btn_devide:
                if(textLen>0&&(isNum(text.charAt(textLen-1))||text.charAt(textLen-1)==')'))
                    text.append('÷');
                else if(textLen>0&&cntCharacter(text.toString())==1)
                    text.setCharAt(textLen-1,'÷');
                break;
            case R.id.btn_power:
                if(textLen>0&&(isNum(text.charAt(textLen-1))||text.charAt(textLen-1)==')'))
                    text.append('^');
                else if(cntCharacter(text.toString())==1){
                    text.setCharAt(textLen-1,'^');
                }
                break;
            case R.id.btn_clear:
                if(textLen>0)
                    text.deleteCharAt(textLen-1);
                if(text.length()==0)
                    s="";
                break;
            case R.id.btn_calculate:
                if(!edit_screen.getText().equals(result_screen.getText())){
                    flag = true;}
                try{
                    text = new StringBuffer(s);
                    if(s.equals("520")||s.equals("521")||s.equals("1314")||s.equals("5201314")){
                        edit_screen.setTextSize(36);
                        edit_screen.setTextColor(getApplicationContext().getResources().getColor(R.color.likeYou));
                        result_screen.setTextColor(getApplicationContext().getResources().getColor(R.color.likeYou));
                        s = "王凯丽❤";
                        Toast.makeText(this,"by kafm",Toast.LENGTH_SHORT).show();
                    }
                    edit_screen.setText(devideByDel(s));
                    result_screen.setText("");
                } catch (Exception e){
                    edit_screen.setText("bug發現,請聯繫開發者kafm。QQ1002605741");
                    text = new StringBuffer();
                    s = "";
                }
                break;
        }
        if(view.getId()!=R.id.btn_calculate){
            flag = false;
//            String tem = devideByDel();
            edit_screen.setText(parseStringAndDevideByDel(text.toString()));//可用正则表达式优化
            if(text.length()>0)
                s = completeString(text.toString());
            if(s.length()>0) {
                try {
                    s=arrange(s);
                } catch (infinityException e) {
                    s = "∞";
                } catch (Exception e) {

                }
            }
            result_screen.setText(devideByDel(s));
        }
    }
}
