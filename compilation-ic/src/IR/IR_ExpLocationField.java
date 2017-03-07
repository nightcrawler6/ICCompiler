package IR;

import java.io.IOException;

import CODEGEN.CODEGEN_AssemblyFilePrinter;
import CODEGEN.CODEGEN_Temporary;
import CODEGEN.CODEGEN_StringAttacher;
import CODEGEN.CODEGEN_TemporaryFactory;
import SEMANTIC.SEMANTIC_SemanticErrorException;

public class IR_ExpLocationField extends IR_EXP{
	public IR_EXP obj;
	public int fieldOffset;

	public IR_ExpLocationField(IR_EXP obj, int fieldOffset){
		this.obj = obj;
		this.fieldOffset = fieldOffset;
	}
	
	@Override
	public CODEGEN_Temporary generateCode() throws IOException, SEMANTIC_SemanticErrorException{
		CODEGEN_Temporary resultTemp = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		CODEGEN_Temporary objTemp = obj.generateCode();
		CODEGEN_Temporary zeroTemp = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		
		CODEGEN_StringAttacher printed = new CODEGEN_StringAttacher();
		printed.appendNL(String.format("li %s,0", zeroTemp.getName()));
		printed.appendNL(String.format("beq %s,%s,%s", 
									   objTemp.getName(), 
									   zeroTemp.getName(),
									   IR_Node.ERROR_LABEL_NAME));		
		
		printed.appendNL(String.format("addi %s,%s,%d", 
									   resultTemp.getName(), 
									   objTemp.getName(),
									   fieldOffset));
		
		CODEGEN_AssemblyFilePrinter.getInstance(null).write(printed.toString());
		return resultTemp;
	}
}
