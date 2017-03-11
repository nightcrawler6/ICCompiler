package IR;

import java.io.IOException;

import CODEGEN.CODEGEN_AssemblyFilePrinter;
import CODEGEN.CODEGEN_Temporary;
import CODEGEN.CODEGEN_Misc;
import CODEGEN.CODEGEN_StringAttacher;
import CODEGEN.CODEGEN_TemporaryFactory;
import SEMANTIC.SEMANTIC_SemanticErrorException;
import SEMANTIC.SEMANTIC_SymbolTable;
import SEMANTIC.SEMANTIC_TempsPastLimitException;

public class IR_ExpNewArray extends IR_EXP{
	public IR_EXP size;
	public IR_ExpNewArray(IR_EXP size)
	{
		this.size=size;
	}
	
	public void generateNullForArrayElements(CODEGEN_Temporary addressOnHeap,CODEGEN_Temporary arraySize,CODEGEN_StringAttacher printed) throws IOException, SEMANTIC_TempsPastLimitException{			
		CODEGEN_Temporary currIndex = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		CODEGEN_Temporary tempIterator = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		CODEGEN_Temporary zeroTemp = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		String initArrayLabel = String.format("Label_%d_initialize_array", CODEGEN_AssemblyFilePrinter.addLabelIndex());
		String startLoopLabel = String.format("Label_%d_begin_loop", CODEGEN_AssemblyFilePrinter.addLabelIndex());
		String endLoopLabel =  String.format("Label_%d_finish_loop", CODEGEN_AssemblyFilePrinter.addLabelIndex());
		printed.appendNL(initArrayLabel+":");
		printed.appendNL(String.format("li %s,0", zeroTemp.getName()));
		printed.appendNL(String.format("li %s,1", currIndex.getName()));
		printed.appendNL(String.format("mov %s,%s", tempIterator.getName(),addressOnHeap.getName()));
		printed.appendNL(String.format("addi %s,%s,%d", tempIterator.getName(),tempIterator.getName(),SEMANTIC_SymbolTable.ADDRESS_SIZE));
		printed.appendNL(startLoopLabel+":");
		printed.appendNL(String.format("bgt %s,%s,%s",currIndex.getName(),arraySize.getName(),endLoopLabel));
		printed.appendNL(String.format("sw %s,0(%s)", zeroTemp.getName(),tempIterator.getName()));
		printed.appendNL(String.format("addi %s,%s,%d", tempIterator.getName(),tempIterator.getName(),SEMANTIC_SymbolTable.ADDRESS_SIZE));
		printed.appendNL(String.format("addi %s,%s,1", currIndex.getName(), currIndex.getName()));
		printed.appendNL(String.format("j %s", startLoopLabel));
		printed.appendNL(endLoopLabel+":");
	}
		
	@Override
	public CODEGEN_Temporary generateCode() throws IOException, SEMANTIC_SemanticErrorException{
		CODEGEN_Temporary generatedNumOfElements = this.size.generateCode();
		CODEGEN_Temporary generatedNumOfElementsPlusOne = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		CODEGEN_StringAttacher printed = new CODEGEN_StringAttacher();
		CODEGEN_Temporary fourTemp = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		
		CODEGEN_Temporary oneTemp = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		printed.appendNL(String.format("li %s,1",oneTemp.getName()));
		printed.appendNL(String.format("blt %s,%s,%s",
					generatedNumOfElements.getName(),
					oneTemp.getName(),
					IR_Node.ERROR_LABEL_NAME));
		
		printed.appendNL(String.format("li %s,4",fourTemp.getName()));
		printed.appendNL(String.format("addi %s,%s,1", generatedNumOfElementsPlusOne.getName(), generatedNumOfElements.getName()));
		CODEGEN_Temporary generatedSize = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		printed.appendNL(String.format("mul %s,%s,%s", generatedSize.getName(),generatedNumOfElementsPlusOne.getName(),fourTemp.getName()));
		CODEGEN_Temporary heapAddress = CODEGEN_Misc.codeGen_malloc(printed,generatedSize);
		printed.appendNL(String.format("sw %s,0(%s)",generatedNumOfElements.getName(),heapAddress.getName()));
		generateNullForArrayElements(heapAddress,generatedNumOfElements,printed);
		CODEGEN_AssemblyFilePrinter.getInstance(null).write(printed.toString());
		return heapAddress;
	}
}
