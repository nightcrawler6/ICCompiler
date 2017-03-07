package IR;

import java.io.IOException;

import SEMANTIC.SEMANTIC_SemanticAnalysisException;

public class IR_ClassDeclList extends IR_Node {

	public IR_ClassDecl head;
	public IR_ClassDeclList tail;
	public String mainLabel;
	
	public IR_ClassDeclList(IR_ClassDecl head, IR_ClassDeclList tail){
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
