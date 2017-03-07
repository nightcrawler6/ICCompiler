package IR;

import java.io.IOException;

import SEMANTIC.SEMANTIC_SemanticAnalysisException;

public class IR_CLASS_DECL extends IR_Node{

	public IR_MethodList methods;
	
	public IR_CLASS_DECL(IR_MethodList methods){
		this.methods = methods;
	}
	
	public void generateCode() throws IOException, SEMANTIC_SemanticAnalysisException{
		if (methods != null){
			methods.generateCode();	
		}
	}
}
