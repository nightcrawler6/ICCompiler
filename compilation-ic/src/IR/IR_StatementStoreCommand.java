package IR;

import java.io.IOException;

import CODEGEN.CODEGEN_AssemblyFilePrinter;
import CODEGEN.CODEGEN_Temporary;
import CODEGEN.CODEGEN_StringNLBuilder;
import SEMANTIC.SEMANTIC_SemanticErrorException;

public class IR_StatementStoreCommand extends IR_Statement{
	public IR_EXP dstAddress; 
	public IR_EXP srcValue;
	
	public IR_StatementStoreCommand(IR_EXP dstAddress, IR_EXP srcValue){
		this.dstAddress = dstAddress;
		this.srcValue = srcValue;
	}

	@Override
	public void generateCode() throws IOException, SEMANTIC_SemanticErrorException{
		CODEGEN_Temporary srcValueTemp = srcValue.generateCode();
		
		CODEGEN_Temporary dstAddressTemp = dstAddress.generateCode();
		
		CODEGEN_StringNLBuilder printed = new CODEGEN_StringNLBuilder();
		printed.appendNL(String.format("sw %s,0(%s)", 
									   srcValueTemp.getName(),
									   dstAddressTemp.getName()));
		CODEGEN_AssemblyFilePrinter.getInstance(null).write(printed.toString());
	}
}
