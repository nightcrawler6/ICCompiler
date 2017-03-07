package IR;

import java.io.IOException;

import SEMANTIC.SEMANTIC_SemanticAnalysisException;

public class IR_MethodList extends IR_Node{
	public IR_Method head;
	public IR_MethodList tail;
	
	public IR_MethodList(IR_Method head, IR_MethodList tail){
		this.head = head;
		this.tail = tail;
	}
	
	public void generateCode() throws IOException, SEMANTIC_SemanticAnalysisException{
		if (head != null){
			head.generateCode();
			if(this.tail!=null){
				tail.generateCode();
			}
		}
	}
}
