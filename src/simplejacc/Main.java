package simplejacc;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import simplejacc.Grammar;
import simplejacc.BnfTokenizer;
import javax.swing.JFileChooser;
/**
* @author 
*    Name: ChenShiYuan
*    E-mail:826718591@qq.com
* @version 
*    Build Time： 2016年12月16日 
*                下午9:11:41
* 类说明
*/
public class Main {
	
	static String rootTestFolder = "testcases/";
	
	static String[] testFolder = {"testcase1/","testcase2/","testcase3/","testcase4/","testcase5/",
			                      "testcase6/","testcase7/","testcase8/","testcase9/","testcase10/"};
	
	static String[] testTokenFile = {"tokenstream1.tok","tokenstream2.tok","tokenstream3.tok",
			                         "tokenstream4.tok","tokenstream5.tok","tokenstream6.tok",
			                         "tokenstream7.tok","tokenstream8.tok","tokenstream9.tok",
			                         "tokenstream10.tok",};

    public static void main(String[] args) {
    	
        try {
        	//打开每个测试文件夹
        	for(int i = 0; i < testFolder.length; i++)
        	{
//        		BufferedReader bReader = getFileReader("testcases/testcase1/input.bnf");
        		System.out.println("Start Test : " + testFolder[i]);
        		BufferedReader bReader = getFileReader(rootTestFolder + testFolder[i] + "input.bnf");
                Grammar grammar = new Grammar(bReader);
      
            	try {
					grammar.buildGrammarAnalysisTables(); //创建预测分析表
				} catch (Exception e) {
					System.out.println(e.getMessage());   //抛出错误，有可能不是LL1文法
					System.out.println();
					continue;  
				}  
        		for(int i2 = 0; i2 < testTokenFile.length; i2++)
        		{
        			boolean bool = grammar.readTok(getFileReader(rootTestFolder + testFolder[i] + testTokenFile[i2]));
                	
                	if(bool)
                		System.out.println("YES");
                	else
                		System.out.println("NO");
        		}
            	System.out.printf("\n-----\n");
            	
        	}
        	
        	
//        	for(String s : grammar.firstSet.get("<F>"))
//        	{
//        		System.out.println(s);
//        	}
        	
        	System.out.printf("\n-----\n");
//        	for(int i = 0; i < 20; i++){
//        		List<String> generatedString = generate("<character>");
//        		printAsSentence (generatedString);
//        	} 
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (IllegalArgumentException e){
        	//catches Grammar.sytaxError() if thrown
        	System.out.println("Syntax error in your file.");
        }
    }


    
    private static BufferedReader getFileReader(String fileName) {
        BufferedReader reader = null;
        
        try{
        	reader = new BufferedReader(new FileReader(fileName));
        }catch(Exception e)
        {
        	System.err.println(e);
        }
        return reader;
    }
    

}

