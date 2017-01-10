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
*    Build Time�� 2016��12��16�� 
*                ����10:11:41
* ��˵��
*/

public class Grammar {
	
	private String startNonTerminal;  //��ʵ�Ŀ�ʼ����
	
    private Map<String, ListOfDefinitions> grammar; // ����ʽ����
    
    private ArrayList<String> nonTerminal = new ArrayList<String>();  //���ս����
    
    private ArrayList<String> terminal = new ArrayList<String>();   //�ս����
    
    private Map<String,Set<String>> firstSet = new HashMap<String,Set<String>>();//first�� 
    
    private Map<String,Set<String>> followSet = new HashMap<String,Set<String>>();//follow��
    
    private Map<ExpCursor,Set<String>> definitionsFirstSet = new HashMap<ExpCursor,Set<String>>();//���ʽ�ļ�
    
    //������
//    public Map<String, Map<String, ExpCursor>> analysisTables = new HashMap<String, Map<String, ExpCursor>>();
    
    private ExpCursor analysisTables[][];

    public Grammar() {
        grammar = new TreeMap<>();
    }

    //��BNF�ļ������﷨
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
	        	startNonTerminal = nonterminal; //�洢��һ����ʼ�ķ��ս��
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
    
    public void createNonterminal()  //�������ս���ż��� 
    {
    	nonTerminal.clear();  //�����
    	for(String key : grammar.keySet())
    	{
    		nonTerminal.add(key);  //��ӵ����ս������
    	}
    }
    
    public void createTerminal()   //�����ս���ŵļ���
    {
    	terminal.clear();  //�����
    	HashSet<String> terminalSet = new HashSet<String>();
    	
    	 for (String key: grammar.keySet()){
         	for (SingleDefinition single : grammar.get(key)){
         		for (String token : single){
         			if(!myIsNonterminal(token))  //���Ƿ��ս���ž����ս������ӵ�
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

    public void createFirstSet()   //����FirstSet����
    {
    	for(String terminal : terminal)   //�����ս����first����ֱ����ӵ�first����
    	{
    		if(!firstSet.containsKey(terminal))  //�Ƿ��Ѵ��ڹ���
    		{
    			Set<String> tempFirstSet = new HashSet<String>(); 
    			tempFirstSet.add(terminal);
    			firstSet.put(terminal, tempFirstSet);
    		}
    	}
    	
    	for(String nonterminal : nonTerminal)  //������ս����first��
    	{
    		getNonTerminalFirstSet(nonterminal);  //���ÿһ��nonterminal��first����
    	}
    }
    
    public void getNonTerminalFirstSet(String nonterminal)  //��ȡ���ս������First����
    {
		if(!firstSet.containsKey(nonterminal))  //û�л�ȡ��First���Ϲ�
		{
			Set<String> tempFirstSet = new HashSet<String>(); 
			ListOfDefinitions definitions = grammar.get(nonterminal);  //��ȡ�����ʽ
			for(SingleDefinition definition : definitions)   //���������ʽ
			{
				for(String charater : definition)  //��������ʽ���ַ�
				{
					if(charater.equals("") && definition.size() == 1)  //�������ʽ���ɿմ�
					{
						tempFirstSet.add("");
						continue;
					}
					else
					{
						if(firstSet.containsKey(charater))  //���First�Ѿ����
						{
							tempFirstSet.addAll(firstSet.get(charater));  //����First����ӵ�����
						}
						else 
						{
							getNonTerminalFirstSet(charater);  //�ݹ������first
							tempFirstSet.addAll(firstSet.get(charater)); //�����First������ӵ�
						}
						if(!firstSet.get(charater).contains(""))  //�������մ��Ļ�ֱ������ѭ�� �鿴��һ������ʽ
						{
							break;
						}
					}
				}
				firstSet.put(nonterminal, tempFirstSet);
			}
		}
    }
    
    public void createFollowSet()  //����FollowSet����
    {
    	for(String nonterminal : nonTerminal)  //������ս����follow��
    	{
    		getNonTerminalFollowSet(nonterminal);  //���ÿһ��nonterminal��follow����
    	}
    }
    
    public void getNonTerminalFollowSet(String nonTerminal)   //������ս���ŵ�follow��
    {
    	if(!followSet.containsKey(nonTerminal)) //û�л�ȡ��Follow���Ϲ�
    	{
    		Set<String> tempFollowSet = new HashSet<String>();
    		tempFollowSet.add("<$>");  //�������ż���
    		 for (String key: grammar.keySet()){  //�������в���ʽ
    			 for (SingleDefinition single : grammar.get(key)){
    				//����ʽ�д���X����X���ڵ�һ��
    				 if(single.contains(nonTerminal) && key != nonTerminal)
    				 {
    					 int position = single.indexOf(nonTerminal); //��ô˷��ս�����ڱ��ʽ��λ��
    					 int length = single.size();  //��ô˱��ʽ�Ĵ�С
    					 
    					 if(position < length-1)   //���滹�Ǵ���һ������
    					 {
    						 tempFollowSet.addAll(firstSet.get(single.get(position+1))); ;//��first(��)����follow��X����
    						 if(firstSet.get(single.get(position+1)).contains(""))  //��������մ�
    						 {
    							 if(followSet.containsKey(key))
    							 {
    								 tempFollowSet.addAll(followSet.get(key));  //������ʽ��˵�follow��ӽ�ȥ
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
								 tempFollowSet.addAll(followSet.get(key));  //������ʽ��˵�follow��ӽ�ȥ
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
    		 tempFollowSet.remove("");  //�Ƴ��մ�
    		 followSet.put(nonTerminal, tempFollowSet);
    	}
    }
    
    private boolean myIsNonterminal(String s){ //ͨ������Ƿ���key�ļ�����
    	
    	return grammar.keySet().contains(s);
    }

    public void createDefinitionsFirstSet() {  //�������ʽ��First
    	
    	for (String key: grammar.keySet()){
    		for(int i = 0; i < grammar.get(key).size(); i++)
    		{
    			ExpCursor cursor = new ExpCursor(key, i);
    			getDefinitionFirstSet(grammar.get(key).get(i),cursor);
    		}
       }
	}
    
    private void getDefinitionFirstSet(SingleDefinition single, ExpCursor expCursor)  //����ÿ�����ʽ��First
    {
    	Set<String> tempFirstDefinition = new HashSet<String>();
    	for(String charater : single)  //�������ʽ
    	{
    		tempFirstDefinition.addAll(firstSet.get(charater));
    		if(!firstSet.get(charater).contains(""))  //�������յĻ�ֱ������ѭ������Ȼ������һ��
    			break; 
    	}
    	definitionsFirstSet.put(expCursor, tempFirstDefinition);
    }

    public void createAnalysisTables() throws Exception  //����������
    {
    	terminal.add("<$>");
    	terminal.remove("");
    	analysisTables = new ExpCursor[nonTerminal.size()][terminal.size()]; //��ʼ��������
    	
    	for (String key: grammar.keySet()){
    		for(int i = 0; i < grammar.get(key).size(); i++)
    		{
    			//��ȡ�˱��ʽ��First��
    			Set<String> definitionFirstSet = definitionsFirstSet.get(new ExpCursor(key, i)); 
//    			String firstCharater = grammar.get(key).get(i).get(0);
    			int x = nonTerminal.indexOf(key);
    			//����First(a)�е�ÿһ���ս����a����A->a���뵽M[A,a]�С�
    			for(int i2 = 0; i2 < terminal.size() ; i2++)
    			{
    				String charater = terminal.get(i2);
    				if(definitionFirstSet.contains(charater))
    				{
    					if(analysisTables[x][i2] == null) //����û�б��ʽ�Ž��������еĻ�����LL1��Ȼ�󱨴����
    					{
    						analysisTables[x][i2] = new ExpCursor(key, i);
    					}
    					else {
    						System.out.println("�ķ������п��ܲ���LL1");
//    	                    System.exit(0);
    						throw new Exception("LL1 ERROR");
    					}
    				}
    				if(definitionFirstSet.contains(""))  //�����մ�
    				{
    					if(followSet.get(key).contains(charater))
    					{
    						if(analysisTables[x][i2] == null) //����û�б��ʽ�Ž��������еĻ�����LL1��Ȼ�󱨴����
        					{
        						analysisTables[x][i2] = new ExpCursor(key, i);
        					}
        					else {
        						System.out.println("�ķ������п��ܲ���LL1");
//        	                    System.exit(0);
        						throw new Exception("LL1 ERROR");
        					}
    					}
    				}
    			}

    		}
    	}
    }

    public boolean runGrammar(ArrayList<String> tok)  //����Ԥ�����������﷨����
    {
    	int cursor = 0;
    	Stack<String> stack = new Stack<String>();
    	stack.push("<$>");  //��ѹջ
    	stack.push(startNonTerminal);  //ѹ����ʼ���ս��
    	String Top = stack.peek(); //��ȡջ��
    	
    	while(!Top.equals("<$>"))  //û�е���ջ�׵��ս��
    	{
    		if(Top.equals(tok.get(cursor)))  //ƥ��
    		{
    			stack.pop();  //����
    			cursor++;
    		}
    		else if(terminal.contains(Top))  //�����ս��
    		{
    			  //����
                 return false;
    		}
    		else
    		{
    			//��ȡԤ���������
    			int position = terminal.indexOf(tok.get(cursor));
    			if(position == -1)
    			{
    				return false;
    			}
    			ExpCursor expCursor = analysisTables[nonTerminal.indexOf(Top)][position];
    			if(expCursor == null)
    			{
    				return false; //���� //��������Ӧ�Ķ���
    			}
    			else{  //������Ӧ����
//    				printExp(expCursor); //��ӡ����
    				stack.pop();
    				//��Ҫ������ջ
    				for(int i = getDefinitionByExpCursor(expCursor).size()-1; i >= 0 ;i--)
    				{
    					if(!getDefinitionByExpCursor(expCursor).get(i).equals("")) //�մ�����ջ
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
        // һ�ζ���һ�У�ֱ������nullΪ�ļ�����
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

    public void buildGrammarAnalysisTables() throws Exception   //һ��������
    {
    	createNonterminal();  //�������ս������
    	createTerminal();   //�����ս����
    	createFirstSet();  //����Firs
    	createFollowSet();  //����Follo
    	createDefinitionsFirstSet();  //����ÿ�����ʽ��FirstSet
    	createAnalysisTables();   
    }



}