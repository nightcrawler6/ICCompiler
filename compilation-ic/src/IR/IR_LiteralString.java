package IR;

import java.io.IOException;

import CODEGEN.CODEGEN_AssemblyFilePrinter;
import CODEGEN.CODEGEN_Temporary;
import CODEGEN.CODEGEN_StringCollector;
import CODEGEN.CODEGEN_TemporaryFactory;
import SEMANTIC.SEMANTIC_TooManyTempsException;

public class IR_LiteralString extends IR_Literal{
	
	public String quote;
	public IR_AsmLabel label;
	public IR_LiteralString(String quote){
		this.quote=quote;
		this.label = new IR_AsmLabel(CODEGEN_StringCollector.addStringAndLabelMapping(this.quote));
	}
	
	public CODEGEN_Temporary generateCode() throws IOException, SEMANTIC_TooManyTempsException{
		CODEGEN_Temporary register = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		CODEGEN_AssemblyFilePrinter.getInstance(null).write(String.format("la %s,%s%s", 
				register.getName(), 
				this.label.name,
				CODEGEN_AssemblyFilePrinter.NEW_LINE_STRING));
		return register;
	}
}
