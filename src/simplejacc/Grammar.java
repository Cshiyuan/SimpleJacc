package simplejacc;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

import javax.xml.transform.Templates;

import struct.ExpCursor;
import struct.ListOfDefinitions;
import struct.SingleDefinition;
/**
* @author 
*    Name: ChenShiYuan
*    E-mail:826718591@qq.com
* @version 
*    Build Time： 2016年12月16日 
*                下午10:11:41
* 类说明
*/

public class Grammar {
	
	private String startNonTerminal;  //其实的开始符号
	
    private Map<String, ListOfDefinitions> grammar; // 生成式规则
    
    private ArrayList<String> nonTerminal = new ArrayList<String>();  //非终结符号
    
    private ArrayList<String> terminal = new ArrayList<String>();   //终结符号
    
    private Map<String,Set<String>> firstSet = new HashMap<String,Set<String>>();//first集 
    
    private Map<String,Set<String>> followSet = new HashMap<String,Set<String>>();//follow集
    
    private Map<ExpCursor,Set<String>> definitionsFirstSet = new HashMap<ExpCursor,Set<String>>();//表达式的集
    
    //分析表
//    public Map<String, Map<String, ExpCursor>> analysisTables = new HashMap<String, Map<String, ExpCursor>>();
    
    private ExpCursor analysisTables[][];

    public Grammar() {
        grammar = new TreeMap<>();
    }

    //从BNF文件创建语法
    public Grammar(BufferedReader reader) throws IOException {
        grammar = new TreeMap<>();
        String currentLine = reader.readLine();
        while ( currentLine != null){
        	//process the line
        	addRule(currentLine);
        	currentLine = reader.readLine();
        }    
        //close the stream
        reader.close();    
    }

    public void addRule(String ruleText) throws IllegalArgumentException {
    	//this condition prevents is from throwing syntaxError on the blank lines
    	if (!(ruleText.equals(""))){
	        BnfTokenizer tokenizer = new BnfTokenizer(ruleText);
	        SingleDefinition singleDef = new SingleDefinition();
	        //get first token on line
	        String nonterminal = tokenizer.nextToken();
	        if(startNonTerminal == null)
	        	startNonTerminal = nonterminal; //存储第一个开始的非终结符
	        //if not nonterminal, throw IllegalArgumentException
	        if(isNonterminal(nonterminal) == false)
	        	syntaxError(ruleText);  
	        
	        //get the next token and enter loop if not null
	    	String token = tokenizer.nextToken();
	        while (token != "EOF"){
	        	if (token.equals("::=")){
	        		//if it is the "::=" skip it and continue the loop
	            	token = tokenizer.nextToken();
	        		continue;
	        	}
	        	if (token.equals("|")){
	        		//if we find the OR sign, add the single def we have and reinitialize to hold the next one
	        		addToGrammar(nonterminal, singleDef);
	        		singleDef = new SingleDefinition();
	        	}
	        	else {
	        		singleDef.add(token);
	        	}
	        	token = tokenizer.nextToken();
	        }
	        //add the definition that was not added due to exiting loop
	        addToGrammar(nonterminal, singleDef);
    	}
    }

    private void addToGrammar(String lhs, SingleDefinition singleDefinition) {
    	//if the nonterminal does not yet exist in grammar define it and add singleDef
        if (grammar.get(lhs) == null){
        	grammar.put(lhs, new ListOfDefinitions());
        	grammar.get(lhs).add(singleDefinition);
        }
        //Otherwise just add the singleDef
        else {
        	grammar.get(lhs).add(singleDefinition);
        }
    }

    private void syntaxError(String rule) {
        throw new IllegalArgumentException("Syntax error in rule: " + rule);
    }
    
    public ListOfDefinitions getDefinitions(String nonterminal) {
        return grammar.get(nonterminal);
    }
    
    public void print() {
        //Note: prints each definition in a full statement on its own line
        for (String key: grammar.keySet()){
        	for (SingleDefinition single : grammar.get(key)){
        		StringBuilder definition = new StringBuilder();
        		for (String token : single){
        			definition.append(token + " ");
        		}
        		System.out.printf("%s ::= %s", key, definition);
        		System.out.println("");
        	}	
        }
    }
    
    private static boolean isNonterminal(String s) {
        return s.startsWith("<");
    }
    
    public void createNonterminal()  //创建非终结符号集合 
    {
    	nonTerminal.clear();  //先清空
    	for(String key : grammar.keySet())
    	{
    		nonTerminal.add(key);  //添加到非终结符号中
    	}
    }
    
    public void createTerminal()   //创建终结符号的集合
    {
    	terminal.clear();  //先清空
    	HashSet<String> terminalSet = new HashSet<String>();
    	
    	 for (String key: grammar.keySet()){
         	for (SingleDefinition single : grammar.get(key)){
         		for (String token : single){
         			if(!myIsNonterminal(token))  //不是非终结符号就是终结符号添加到
         			{
         				terminalSet.add(token);
         			}
         		}
         	}	
         }
    	 
    	 for(String terminal : terminalSet)
    	 {
    		 this.terminal.add(terminal);
    	 }
    }

    public void createFirstSet()   //创建FirstSet集合
    {
    	for(String terminal : terminal)   //处理终结符的first集，直接添加到first集中
    	{
    		if(!firstSet.containsKey(terminal))  //是否已存在过了
    		{
    			Set<String> tempFirstSet = new HashSet<String>(); 
    			tempFirstSet.add(terminal);
    			firstSet.put(terminal, tempFirstSet);
    		}
    	}
    	
    	for(String nonterminal : nonTerminal)  //处理非终结符的first集
    	{
    		getNonTerminalFirstSet(nonterminal);  //获得每一个nonterminal的first集合
    	}
    }
    
    public void getNonTerminalFirstSet(String nonterminal)  //获取非终结符号其First集合
    {
		if(!firstSet.containsKey(nonterminal))  //没有获取其First集合过
		{
			Set<String> tempFirstSet = new HashSet<String>(); 
			ListOfDefinitions definitions = grammar.get(nonterminal);  //获取其产生式
			for(SingleDefinition definition : definitions)   //遍历其产生式
			{
				for(String charater : definition)  //遍历产生式的字符
				{
					if(charater.equals("") && definition.size() == 1)  //如果产生式生成空串
					{
						tempFirstSet.add("");
						continue;
					}
					else
					{
						if(firstSet.containsKey(charater))  //如果First已经算过
						{
							tempFirstSet.addAll(firstSet.get(charater));  //将其First集添加到里面
						}
						else 
						{
							getNonTerminalFirstSet(charater);  //递归计算其first
							tempFirstSet.addAll(firstSet.get(charater)); //求出其First集后添加到
						}
						if(!firstSet.get(charater).contains(""))  //不包含空串的话直接跳过循环 查看下一个产生式
						{
							break;
						}
					}
				}
				firstSet.put(nonterminal, tempFirstSet);
			}
		}
    }
    
    public void createFollowSet()  //创建FollowSet集合
    {
    	for(String nonterminal : nonTerminal)  //处理非终结符的follow集
    	{
    		getNonTerminalFollowSet(nonterminal);  //获得每一个nonterminal的follow集合
    	}
    }
    
    public void getNonTerminalFollowSet(String nonTerminal)   //处理非终结符号的follow集
    {
    	if(!followSet.containsKey(nonTerminal)) //没有获取其Follow集合过
    	{
    		Set<String> tempFollowSet = new HashSet<String>();
    		tempFollowSet.add("<$>");  //结束符号加入
    		 for (String key: grammar.keySet()){  //遍历所有产生式
    			 for (SingleDefinition single : grammar.get(key)){
    				//产生式中存在X并且X不在第一个
    				 if(single.contains(nonTerminal) && key != nonTerminal)
    				 {
    					 int position = single.indexOf(nonTerminal); //获得此非终结符的在表达式的位置
    					 int length = single.size();  //获得此表达式的大小
    					 
    					 if(position < length-1)   //后面还是存在一个符号
    					 {
    						 tempFollowSet.addAll(firstSet.get(single.get(position+1))); ;//把first(β)加入follow（X）中
    						 if(firstSet.get(single.get(position+1)).contains(""))  //如果包含空串
    						 {
    							 if(followSet.containsKey(key))
    							 {
    								 tempFollowSet.addAll(followSet.get(key));  //将生成式左端的follow添加进去
    							 }
    							 else
    							 {
    								 getNonTerminalFollowSet(key);
    								 tempFollowSet.addAll(followSet.get(key));
    							 }
    						 }
    					 }
    					 if(position == length -1)
    					 {
    						 if(followSet.containsKey(key))
							 {
								 tempFollowSet.addAll(followSet.get(key));  //将生成式左端的follow添加进去
							 }
							 else
							 {
								 getNonTerminalFollowSet(key);
								 tempFollowSet.addAll(followSet.get(key));
							 }
    					 }
    				 }
    			 }
    		 }
    		 tempFollowSet.remove("");  //移除空串
    		 followSet.put(nonTerminal, tempFollowSet);
    	}
    }
    
    private boolean myIsNonterminal(String s){ //通过检查是否在key的集合里
    	
    	return grammar.keySet().contains(s);
    }

    public void createDefinitionsFirstSet() {  //创建表达式的First
    	
    	for (String key: grammar.keySet()){
    		for(int i = 0; i < grammar.get(key).size(); i++)
    		{
    			ExpCursor cursor = new ExpCursor(key, i);
    			getDefinitionFirstSet(grammar.get(key).get(i),cursor);
    		}
       }
	}
    
    private void getDefinitionFirstSet(SingleDefinition single, ExpCursor expCursor)  //创建每个表达式的First
    {
    	Set<String> tempFirstDefinition = new HashSet<String>();
    	for(String charater : single)  //遍历表达式
    	{
    		tempFirstDefinition.addAll(firstSet.get(charater));
    		if(!firstSet.get(charater).contains(""))  //不包含空的话直接跳过循环，不然继续下一步
    			break; 
    	}
    	definitionsFirstSet.put(expCursor, tempFirstDefinition);
    }

    public void createAnalysisTables() throws Exception  //创建分析表
    {
    	terminal.add("<$>");
    	terminal.remove("");
    	analysisTables = new ExpCursor[nonTerminal.size()][terminal.size()]; //初始化分析表
    	
    	for (String key: grammar.keySet()){
    		for(int i = 0; i < grammar.get(key).size(); i++)
    		{
    			//获取此表达式的First集
    			Set<String> definitionFirstSet = definitionsFirstSet.get(new ExpCursor(key, i)); 
//    			String firstCharater = grammar.get(key).get(i).get(0);
    			int x = nonTerminal.indexOf(key);
    			//对于First(a)中的每一个终结符号a，将A->a加入到M[A,a]中。
    			for(int i2 = 0; i2 < terminal.size() ; i2++)
    			{
    				String charater = terminal.get(i2);
    				if(definitionFirstSet.contains(charater))
    				{
    					if(analysisTables[x][i2] == null) //从来没有表达式放进来过，有的话则不是LL1，然后报错结束
    					{
    						analysisTables[x][i2] = new ExpCursor(key, i);
    					}
    					else {
    						System.out.println("文法出错！有可能不是LL1");
//    	                    System.exit(0);
    						throw new Exception("LL1 ERROR");
    					}
    				}
    				if(definitionFirstSet.contains(""))  //包含空串
    				{
    					if(followSet.get(key).contains(charater))
    					{
    						if(analysisTables[x][i2] == null) //从来没有表达式放进来过，有的话则不是LL1，然后报错结束
        					{
        						analysisTables[x][i2] = new ExpCursor(key, i);
        					}
        					else {
        						System.out.println("文法出错！有可能不是LL1");
//        	                    System.exit(0);
        						throw new Exception("LL1 ERROR");
        					}
    					}
    				}
    			}

    		}
    	}
    }

    public boolean runGrammar(ArrayList<String> tok)  //利用预测分析表进行语法分析
    {
    	int cursor = 0;
    	Stack<String> stack = new Stack<String>();
    	stack.push("<$>");  //先压栈
    	stack.push(startNonTerminal);  //压人起始非终结符
    	String Top = stack.peek(); //获取栈顶
    	
    	while(!Top.equals("<$>"))  //没有到底栈底的终结符
    	{
    		if(Top.equals(tok.get(cursor)))  //匹配
    		{
    			stack.pop();  //弹出
    			cursor++;
    		}
    		else if(terminal.contains(Top))  //属于终结符
    		{
    			  //出错
                 return false;
    		}
    		else
    		{
    			//获取预测分析表动作
    			int position = terminal.indexOf(tok.get(cursor));
    			if(position == -1)
    			{
    				return false;
    			}
    			ExpCursor expCursor = analysisTables[nonTerminal.indexOf(Top)][position];
    			if(expCursor == null)
    			{
    				return false; //出错 //不存在相应的动作
    			}
    			else{  //存在相应动作
//    				printExp(expCursor); //打印动作
    				stack.pop();
    				//需要倒着入栈
    				for(int i = getDefinitionByExpCursor(expCursor).size()-1; i >= 0 ;i--)
    				{
    					if(!getDefinitionByExpCursor(expCursor).get(i).equals("")) //空串不入栈
    					{
    						stack.push(getDefinitionByExpCursor(expCursor).get(i));
    					}
    				}
    			}
    		}
    		Top = stack.peek();
    		
    	}
    	if(cursor == tok.size() - 1)
    	return true;
    	else
    	return false;
    }

    public boolean readTok(BufferedReader reader) throws IOException
    {
    	String tempString = null;
        ArrayList<String> Input=new ArrayList<String>();
        // 一次读入一行，直到读入null为文件结束
        while ((tempString = reader.readLine()) != null) {
            Input.add(tempString);
        }
        Input.add("<$>");
        return runGrammar(Input);
    }

    public void printArrayString(ArrayList<String> list)
    {
        for(int i = 0; i < list.size(); i++)
            System.out.print(list.get(i) + " ");
        System.out.println();
    }
    
    public void printExp(ExpCursor expCursor)
    {
    	ArrayList<String> definition= grammar.get(expCursor.leftExp).get(expCursor.pos);
    	System.out.print(expCursor.leftExp + " -> ");
    	printArrayString(definition);
    }

    public ArrayList<String> getDefinitionByExpCursor(ExpCursor expCursor)
    {
    	return grammar.get(expCursor.leftExp).get(expCursor.pos);
    }

    public void buildGrammarAnalysisTables() throws Exception   //一口气创建
    {
    	createNonterminal();  //创建非终结符集合
    	createTerminal();   //创建终结符集
    	createFirstSet();  //建立Firs
    	createFollowSet();  //建立Follo
    	createDefinitionsFirstSet();  //建立每个表达式的FirstSet
    	createAnalysisTables();   
    }



}