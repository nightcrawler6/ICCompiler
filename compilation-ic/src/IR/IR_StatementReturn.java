package IR;

import java.io.IOException;

import CODEGEN.CODEGEN_AssemblyFilePrinter;
import CODEGEN.CODEGEN_Temporary;
import CODEGEN.CODEGEN_StringNLBuilder;
import SEMANTIC.SEMANTIC_SemanticErrorException;

public class IR_StatementReturn extends IR_Statement{
	public IR_EXP returnedExpression;
	public String methodEpilogLabelName;
	
	public IR_StatementReturn(IR_EXP returnedExpression, String methodEpilogLabelName){
		this.returnedExpression = returnedExpression;
		this.methodEpilogLabelName = methodEpilogLabelName;
	}
	
	@Override
	public void generateCode() throws IOException, SEMANTIC_SemanticErrorException{
		CODEGEN_StringNLBuilder builder = new CODEGEN_StringNLBuilder();
		if (returnedExpression != null){
			CODEGEN_Temporary returnValue = returnedExpression.generateCode();
			builder.appendNL(String.format("mov $v0,%s",returnValue.getName()));
		}
		builder.appendNL(String.format("j %s", methodEpilogLabelName));
		CODEGEN_AssemblyFilePrinter.getInstance(null).write(builder.toString());
	}
}
