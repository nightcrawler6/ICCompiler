package IR;

import java.io.IOException;

import CODEGEN.CODEGEN_AssemblyFilePrinter;
import CODEGEN.CODEGEN_Temporary;
import CODEGEN.CODEGEN_TemporaryFactory;
import SEMANTIC.SEMANTIC_SemanticErrorException;

public class IR_ExpMemory extends IR_EXP{
	public IR_EXP address;
	
	public IR_ExpMemory(IR_EXP address){
		this.address = address;
	}
	
	@Override
	public CODEGEN_Temporary generateCode() throws IOException, SEMANTIC_SemanticErrorException{
		CODEGEN_Temporary result = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		CODEGEN_Temporary t1= this.address.generateCode();
		CODEGEN_AssemblyFilePrinter.getInstance(null).write(String.format("lw %s,0(%s)%s",result.getName(),t1.getName(),CODEGEN_AssemblyFilePrinter.NEW_LINE_STRING));
		return result;
	}
}
