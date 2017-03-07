package IR;

import java.io.IOException;

import CODEGEN.CODEGEN_AssemblyFilePrinter;
import CODEGEN.CODEGEN_Temporary;
import CODEGEN.CODEGEN_Utils;
import CODEGEN.CODEGEN_StringAttacher;
import CODEGEN.CODEGEN_TemporaryFactory;
import SEMANTIC.SEMANTIC_ClassSymbolInfo;
import SEMANTIC.SEMANTIC_SymbolTable;
import SEMANTIC.SEMANTIC_TempsPastLimitException;

public class IR_ExpNewClass extends IR_EXP{
	public String newExpClassName;
	
	public IR_ExpNewClass(String newExpClassName){
		this.newExpClassName=newExpClassName;
	}
	
	public void generateNullActionForFields(SEMANTIC_ClassSymbolInfo classInfo,CODEGEN_Temporary addressOnHeap) throws IOException, SEMANTIC_TempsPastLimitException{
		if(classInfo.fields==null){
			return;
		}
		
		CODEGEN_StringAttacher printed = new CODEGEN_StringAttacher();
		CODEGEN_Temporary temp = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		CODEGEN_Temporary zeroTemp = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		printed.appendNL(String.format("li %s,0", zeroTemp.getName()));
		printed.appendNL(String.format("mov %s,%s", temp.getName(), addressOnHeap.getName()));
		for(int i=0;i<classInfo.fields.size();i++){
			printed.appendNL(String.format("addi %s,%s,%d", temp.getName(), temp.getName(),SEMANTIC_SymbolTable.ADDRESS_SIZE));
			printed.appendNL(String.format("sw %s,0(%s)", zeroTemp.getName(),temp.getName()));
		}
		CODEGEN_AssemblyFilePrinter.getInstance(null).write(printed.toString());
	}
	
	private void generateCodeForStoringVFTableAddress(SEMANTIC_ClassSymbolInfo classInfo, 
													  CODEGEN_Temporary addressOnHeap, 
													  CODEGEN_StringAttacher printed) throws SEMANTIC_TempsPastLimitException{
		CODEGEN_Temporary newTemp = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		
		if (classInfo.virtualFunctionsOrder.size() > 0){
			printed.appendNL(String.format("la %s, %s", newTemp.getName(),classInfo.getVFTableLabel()));	
		}
		else{
			printed.appendNL(String.format("li %s, 0", newTemp.getName()));
		}
		
		printed.appendNL(String.format("sw %s,0(%s)", newTemp.getName(),addressOnHeap.getName()));
	}
	
	public CODEGEN_Temporary generateCode() throws IOException, SEMANTIC_TempsPastLimitException{
		SEMANTIC_ClassSymbolInfo classInfo = SEMANTIC_SymbolTable.getClassSymbolInfo(newExpClassName);
		CODEGEN_StringAttacher printed = new CODEGEN_StringAttacher();
		CODEGEN_Temporary addressOnHeap = CODEGEN_Utils.codeGen_malloc(printed,classInfo.size);
		generateCodeForStoringVFTableAddress(classInfo, addressOnHeap, printed);
		CODEGEN_AssemblyFilePrinter.getInstance(null).write(printed.toString());
		generateNullActionForFields(classInfo,addressOnHeap);
		return addressOnHeap;
	}
}
