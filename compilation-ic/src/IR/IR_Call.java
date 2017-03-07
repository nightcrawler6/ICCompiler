package IR;

import java.io.IOException;
import java.util.List;

import CODEGEN.CODEGEN_AssemblyFilePrinter;
import CODEGEN.CODEGEN_Temporary;
import CODEGEN.CODEGEN_Misc;
import CODEGEN.CODEGEN_StringAttacher;
import CODEGEN.CODEGEN_TemporaryFactory;
import SEMANTIC.SEMANTIC_SemanticErrorException;
import SEMANTIC.SEMANTIC_SymbolTable;

public class IR_Call extends IR_Node{

	public IR_EXP callerAddress;
	public IR_EXP calledFunctionAddress;
	public IR_ExpList args;
	public boolean isPrintIntCall= false;
	
	public IR_Call(IR_EXP calledFunctionAddress, IR_EXP callerAddress, IR_ExpList args){
		this.calledFunctionAddress = calledFunctionAddress;
		this.callerAddress = callerAddress;
		this.args = args;
	}

	public IR_Call(IR_ExpList args, boolean isPrintIntCall){
		this.args = args;
		this.isPrintIntCall = isPrintIntCall;
	}

	public void generateCode() throws IOException, SEMANTIC_SemanticErrorException{
		if(isPrintIntCall){
			CODEGEN_StringAttacher printed = new CODEGEN_StringAttacher();
			CODEGEN_Temporary zeroTemp = CODEGEN_TemporaryFactory.getAndAddNewTemp();
			printed.appendNL(String.format("li %s,0", zeroTemp.getName()));
			List<CODEGEN_Temporary> ts = args.generateCodeList();
			CODEGEN_Misc.codeGen_Push(printed, ts.get(0).getName());
			CODEGEN_Misc.codeGen_Push(printed, zeroTemp.getName());
			printed.appendNL(String.format("jal %s", PRINTINT_FUNC_LABEL));
			CODEGEN_Temporary spOffsetTemp = CODEGEN_TemporaryFactory.getAndAddNewTemp();
			printed.appendNL(String.format("li %s,%d",spOffsetTemp.getName(), SEMANTIC_SymbolTable.ADDRESS_SIZE*2 ));
			printed.appendNL(String.format("add $sp,$sp,%s", spOffsetTemp.getName()));
			CODEGEN_AssemblyFilePrinter.getInstance(null).write(printed.toString());

			return;
		}
		
		CODEGEN_Temporary callerAddressTemp = (CODEGEN_Temporary) callerAddress.generateCode();
		CODEGEN_Temporary zeroTemp = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		
		CODEGEN_StringAttacher printed = new CODEGEN_StringAttacher();
		printed.appendNL(String.format("li %s,0", zeroTemp.getName()));
		printed.appendNL(String.format("beq %s,%s,%s", 
									   callerAddressTemp.getName(), 
									   zeroTemp.getName(),
									   IR_Node.ERROR_LABEL_NAME));
		CODEGEN_AssemblyFilePrinter.getInstance(null).write(printed.toString());
		
		CODEGEN_Temporary functionAddressTemp = (CODEGEN_Temporary) calledFunctionAddress.generateCode();
		printed = new CODEGEN_StringAttacher();

		int spOffset = SEMANTIC_SymbolTable.ADDRESS_SIZE;
		
		if(args!=null){
			List<CODEGEN_Temporary> ts = args.generateCodeList();
			for(int i=ts.size()-1;i>=0;i--){
				CODEGEN_Misc.codeGen_Push(printed, ts.get(i).getName());
			}
			spOffset+=ts.size()*SEMANTIC_SymbolTable.ADDRESS_SIZE;
			
		}
		
		CODEGEN_Misc.codeGen_Push(printed, callerAddressTemp.getName());
		printed.appendNL(String.format("mov $t0,%s", functionAddressTemp.getName()));
		printed.appendNL(String.format("jalr $t0",functionAddressTemp.getName()));
		CODEGEN_Temporary spOffsetTemp = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		printed.appendNL(String.format("li %s,%d",spOffsetTemp.getName(), spOffset ));
		printed.appendNL(String.format("add $sp,$sp,%s", spOffsetTemp.getName()));
		CODEGEN_AssemblyFilePrinter.getInstance(null).write(printed.toString());
	}
}
