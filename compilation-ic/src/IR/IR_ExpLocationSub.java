package IR;

import java.io.IOException;

import CODEGEN.CODEGEN_AssemblyFilePrinter;
import CODEGEN.CODEGEN_Temporary;
import CODEGEN.CODEGEN_StringNLBuilder;
import CODEGEN.CODEGEN_TemporaryFactory;
import SEMANTIC.SEMANTIC_SemanticAnalysisException;

public class IR_ExpLocationSub extends IR_EXP{
	public IR_EXP arrayBase;
	public IR_EXP index;
	
	public IR_ExpLocationSub(IR_EXP arrayBase, IR_EXP index){
		this.arrayBase = arrayBase;
		this.index = index;
	}
	
	@Override
	public CODEGEN_Temporary generateCode() throws IOException, SEMANTIC_SemanticAnalysisException{
		CODEGEN_Temporary resultTemp = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		CODEGEN_Temporary arrayBaseTemp = arrayBase.generateCode();
		CODEGEN_Temporary zeroTemp = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		
		CODEGEN_StringNLBuilder printed = new CODEGEN_StringNLBuilder();
		printed.appendNL(String.format("li %s,0", zeroTemp.getName()));
		printed.appendNL(String.format("beq %s,%s,%s", 
									   arrayBaseTemp.getName(), 
									   zeroTemp.getName(),
									   IR_Node.ERROR_LABEL_NAME));		
		CODEGEN_AssemblyFilePrinter.getInstance(null).write(printed.toString());
		
		CODEGEN_Temporary offsetTemp = index.generateCode();
		CODEGEN_Temporary arrayLengthTemp = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		CODEGEN_Temporary registerForFour = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		
		printed = new CODEGEN_StringNLBuilder();
		printed.appendNL(String.format("lw %s,0(%s)", 
									   arrayLengthTemp.getName(), 
									   arrayBaseTemp.getName()));
		printed.appendNL(String.format("bge %s,%s,%s", 
									   offsetTemp.getName(), 
									   arrayLengthTemp.getName(),
									   IR_Node.ERROR_LABEL_NAME));
		
		printed.appendNL(String.format("addi %s,%s,1", 
									   offsetTemp.getName(), 
									   offsetTemp.getName()));
		
		printed.appendNL(String.format("li %s,4", registerForFour.getName()));
		printed.appendNL(String.format("mul %s,%s,%s", 
									   offsetTemp.getName(),
									   offsetTemp.getName(),
									   registerForFour.getName()));
		
		printed.appendNL(String.format("add %s,%s,%s", 
									   resultTemp.getName(),
									   offsetTemp.getName(),
									   arrayBaseTemp.getName()));
		
		CODEGEN_AssemblyFilePrinter.getInstance(null).write(printed.toString());
		return resultTemp;
	}
}
