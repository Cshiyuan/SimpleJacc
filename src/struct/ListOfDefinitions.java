package struct;
import java.util.ArrayList;
/**
* @author 
*    Name: ChenShiYuan
*    E-mail:826718591@qq.com
* @version 
*    Build Time�� 2016��12��16�� 
*                ����5:11:41
* ��˵��
*/
public class ListOfDefinitions extends ArrayList<SingleDefinition> {
    

    public ListOfDefinitions() {}
    

    @Override
    public String toString() {
    	String contents = new String();
        for (SingleDefinition item : this){
        	contents = contents + item.toString() + " | ";
        }
        return contents.substring(0, contents.lastIndexOf("|")).trim();
    }
}
