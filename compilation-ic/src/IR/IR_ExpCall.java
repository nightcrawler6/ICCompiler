package IR;

import java.io.IOException;

import CODEGEN.CODEGEN_AssemblyFilePrinter;
import CODEGEN.CODEGEN_Temporary;
import CODEGEN.CODEGEN_StringAttacher;
import CODEGEN.CODEGEN_TemporaryFactory;
import SEMANTIC.SEMANTIC_SemanticErrorException;

public class IR_ExpCall extends IR_EXP{
	public IR_Call call;
	public IR_ExpCall(IR_Call call){
		this.call=call;
	}
	
	@Override
	public CODEGEN_Temporary generateCode() throws IOException, SEMANTIC_SemanticErrorException{
		this.call.generateCode();
		
		CODEGEN_Temporary resultTemp = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		CODEGEN_StringAttacher printed = new CODEGEN_StringAttacher();
		
		printed.appendNL(String.format("mov %s,$v0",resultTemp.getName()));
		
		CODEGEN_AssemblyFilePrinter.getInstance(null).write(printed.toString());
		return resultTemp;
	}
}
