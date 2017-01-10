package simplejacc;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;

/**
* @author 
*    Name: ChenShiYuan
*    E-mail:826718591@qq.com
* @version 
*    Build Time： 2016年12月16日 
*                下午7:11:41
* 类说明
*/

public class BnfTokenizer {
    private enum State { INITIAL, IN_NONTERMINAL, AFTER_COLON, AFTER_COLON_COLON }
    private StreamTokenizer tokenizer;

    public BnfTokenizer() {}
    
    public BnfTokenizer(String text) { tokenize(text); }
    
    public void tokenize(String text) {
        Reader reader = new StringReader(text);
        tokenizer = new StreamTokenizer(reader);
        //determines whether or not line ends are treated as tokens
        tokenizer.eolIsSignificant(true);
        tokenizer.ordinaryChar('\'');
        tokenizer.ordinaryChar('.');
    }

    public String nextToken() throws IllegalStateException {
        int currentToken = 0;
        String BnfToken = "NO TOKEN";
        State state = State.INITIAL;
        
        if (tokenizer == null) {
            throw new IllegalStateException("BnfTokenizer was not given anything to tokenize!");
        }
        
        while (true) {
            try { currentToken = tokenizer.nextToken(); }
            catch (IOException e) { throw new Error(e); } // Should never happen
            
            if (currentToken == StreamTokenizer.TT_EOF) return "EOF";
            switch (state) {
                case INITIAL:
                    if (currentToken == '<') {
                        BnfToken = "<";
                        state = State.IN_NONTERMINAL;
                    }
                    else if (currentToken == ':') {
                        state = State.AFTER_COLON;
                    }
                    else {
                        // stay in initial state
                        return toString(currentToken);
                    }
                    break;
                case IN_NONTERMINAL:
                    if (currentToken == '>') {
                        BnfToken += ">";
                        state = State.INITIAL;
                        return BnfToken;
                    }
                    if (BnfToken.length() > 1) BnfToken += " ";
                    BnfToken += toString(currentToken);
                    // stay in this state                        
                    break;
                case AFTER_COLON:
                    if (currentToken == ':') {
                        state = State.AFTER_COLON_COLON;
                        break;
                    }
                    tokenizer.pushBack();
                    return ":";
                case AFTER_COLON_COLON:
                    if (currentToken == '=') {
                        state = State.INITIAL;
                        return "::=";
                    }
                    return "[ERROR]";
            }
        }
    }
    
    private String toString(int currentToken) {
        switch (currentToken) {
            case StreamTokenizer.TT_WORD: return tokenizer.sval;
            case StreamTokenizer.TT_NUMBER: return tokenizer.nval + "";
            case StreamTokenizer.TT_EOL: return "\n";
            case StreamTokenizer.TT_EOF: return "[END OF FILE]";
            default: 
                if (currentToken == '"') return tokenizer.sval;
                return "" + (char)currentToken;
        }
    }
}