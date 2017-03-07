package IR;

import java.io.IOException;

import SEMANTIC.SEMANTIC_SemanticErrorException;

public class IR_StatementList extends IR_Statement 
{
	public IR_Statement head; 
	public IR_StatementList tail;
	
	public IR_StatementList(IR_Statement head, IR_StatementList tail){
		this.head = head;
		this.tail = tail;
	}
	
	@Override
	public void generateCode() throws IOException, SEMANTIC_SemanticErrorException
	{
		if (head != null){
			head.generateCode();
			
			if (tail != null){
				tail.generateCode();		
			}
		}
	}
}
