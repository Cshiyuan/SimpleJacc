package struct;
/**
* @author 
*    Name: ChenShiYuan
*    E-mail:826718591@qq.com
* @version 
*    Build Time： 2016年12月20日 
*                下午9:11:41
* 类说明
*/
public class ExpCursor extends Object{
	
	public String leftExp;  //产生式左端
	public int pos;  //序号
	
	public ExpCursor(String leftExp, int pos)
	{
		this.leftExp = leftExp;
		this.pos = pos;
	}
	
	//重写这几个方法是为了可以在Map中充当Key
	
	@Override
	public int hashCode()
	{
		return leftExp.hashCode() + pos;
	}
	
	@Override
	public boolean equals(Object object)
	{
		if(object instanceof ExpCursor)
		{
			if(leftExp == ((ExpCursor)object).leftExp)
			{
				if(pos == ((ExpCursor)object).pos)
				{
					return true;
				}
			}
		}
		return false;
	}

}
