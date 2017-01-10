package struct;
import java.util.ArrayList;
/**
* @author 
*    Name: ChenShiYuan
*    E-mail:826718591@qq.com
* @version 
*    Build Time： 2016年12月20日 
*                下午9:11:41
* 类说明
*/
public class SingleDefinition extends ArrayList<String> {


    public SingleDefinition() {}
    
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (String item : this){
        	result.append(item + " ");
        }
        
        return result.toString().trim();
    }
}
