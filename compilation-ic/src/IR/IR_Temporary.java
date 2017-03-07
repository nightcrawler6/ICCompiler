package IR;

import java.io.IOException;

import CODEGEN.CODEGEN_AssemblyFilePrinter;
import CODEGEN.CODEGEN_Temporary;
import CODEGEN.CODEGEN_StringNLBuilder;
import CODEGEN.CODEGEN_TemporaryFactory;
import SEMANTIC.SEMANTIC_TempsPastLimitException;

public class IR_Temporary extends IR_EXP{
	IR_TemporaryType tempType;
	public IR_Temporary(IR_TemporaryType tempType){
		this.tempType=tempType;
	}
	
	public CODEGEN_Temporary generateCode() throws IOException, SEMANTIC_TempsPastLimitException{
		CODEGEN_Temporary newTemp = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		CODEGEN_StringNLBuilder printed = new CODEGEN_StringNLBuilder();
		
		if(tempType==IR_TemporaryType.fp){
			printed.appendNL(String.format("mov %s,$fp",newTemp.getName()));			
		}
			
		CODEGEN_AssemblyFilePrinter.getInstance(null).write(printed.toString());

		return newTemp;
	}
}
