package IR;

import java.io.IOException;

import SEMANTIC.SEMANTIC_SemanticErrorException;

public class IR_ClassDecl extends IR_Node{

	public IR_MethodList methods;
	
	public IR_ClassDecl(IR_MethodList methods){
		this.methods = methods;
	}
	
	public void generateCode() throws IOException, SEMANTIC_SemanticErrorException{
		if (methods != null){
			methods.generateCode();	
		}
	}
}
