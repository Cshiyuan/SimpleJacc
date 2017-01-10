package struct;
/**
* @author 
*    Name: ChenShiYuan
*    E-mail:826718591@qq.com
* @version 
*    Build Time�� 2016��12��20�� 
*                ����9:11:41
* ��˵��
*/
public class ExpCursor extends Object{
	
	public String leftExp;  //����ʽ���
	public int pos;  //���
	
	public ExpCursor(String leftExp, int pos)
	{
		this.leftExp = leftExp;
		this.pos = pos;
	}
	
	//��д�⼸��������Ϊ�˿�����Map�г䵱Key
	
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
