package IR;

import java.io.IOException;

import CODEGEN.CODEGEN_AssemblyFilePrinter;
import CODEGEN.CODEGEN_Temporary;
import CODEGEN.CODEGEN_TemporaryFactory;
import SEMANTIC.SEMANTIC_TooManyTempsException;

public class IR_LiteralConstant extends IR_Literal{
	int constInteger;
	public IR_LiteralConstant(int i){
		this.constInteger=i;
	}
	
	public CODEGEN_Temporary generateCode() throws IOException, SEMANTIC_TooManyTempsException{
		CODEGEN_Temporary register = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		CODEGEN_AssemblyFilePrinter.getInstance(null).write(String.format("li %s,%d%s", 
				register.getName(),constInteger,CODEGEN_AssemblyFilePrinter.NEW_LINE_STRING));
		return register;
		
	}
}
