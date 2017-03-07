package IR;

import java.io.IOException;

import SEMANTIC.SEMANTIC_SemanticErrorException;

public abstract class IR_Statement extends IR_Node{
	public abstract void generateCode() throws IOException, SEMANTIC_SemanticErrorException;
}
