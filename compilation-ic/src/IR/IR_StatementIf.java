package IR;

import java.io.IOException;

import CODEGEN.CODEGEN_AssemblyFilePrinter;
import CODEGEN.CODEGEN_Temporary;
import CODEGEN.CODEGEN_StringNLBuilder;
import CODEGEN.CODEGEN_TemporaryFactory;
import SEMANTIC.SEMANTIC_SemanticAnalysisException;

public class IR_StatementIf extends IR_StatementCondition{
	
	public IR_StatementIf(IR_EXP cond, IR_Statement body, String labelName){
		super(cond, body, labelName);
	}
	
	@Override
	public void generateCode() throws IOException, SEMANTIC_SemanticAnalysisException{
		CODEGEN_Temporary zeroTemp = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		
		startLabel.generateCode();
		
		CODEGEN_StringNLBuilder printed = new CODEGEN_StringNLBuilder();
		printed.appendNL(String.format("li %s,0", zeroTemp.getName()));
		CODEGEN_AssemblyFilePrinter.getInstance(null).write(printed.toString());
		
		CODEGEN_Temporary condTemp = cond.generateCode();
		
		printed = new CODEGEN_StringNLBuilder();
		printed.appendNL(String.format("beq %s,%s,%s",
									   condTemp.getName(),
									   zeroTemp.getName(),
									   endLabel.name));
		CODEGEN_AssemblyFilePrinter.getInstance(null).write(printed.toString());
		
		if (body != null){
			body.generateCode();
		}
		
		endLabel.generateCode();
	}
}
