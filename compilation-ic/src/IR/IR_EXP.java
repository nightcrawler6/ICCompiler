package IR;

import java.io.IOException;

import CODEGEN.CODEGEN_Temporary;
import SEMANTIC.SEMANTIC_SemanticAnalysisException;

public abstract class IR_EXP extends IR_Node{
	 public abstract CODEGEN_Temporary generateCode() throws IOException, SEMANTIC_SemanticAnalysisException;
}
