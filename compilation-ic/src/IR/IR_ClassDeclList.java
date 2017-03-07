package IR;

import java.io.IOException;

import SEMANTIC.SEMANTIC_SemanticAnalysisException;

public class IR_ClassDeclList extends IR_Node {

	public IR_CLASS_DECL head;
	public IR_ClassDeclList tail;
	public String mainLabel;
	
	public IR_ClassDeclList(IR_CLASS_DECL head, IR_ClassDeclList tail){
		this.head = head;
		this.tail = tail;
	}
	
	public void generateCode() throws IOException, SEMANTIC_SemanticAnalysisException{
		this.head.generateCode();
		if(this.tail != null){
			this.tail.generateCode();
		}
	}
	
}
