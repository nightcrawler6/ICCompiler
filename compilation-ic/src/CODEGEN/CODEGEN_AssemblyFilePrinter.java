package CODEGEN;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import SEMANTIC.SEMANTIC_ClassSymbolInfo;
import SEMANTIC.SEMANTIC_SymbolInfoNode;
import SEMANTIC.SEMANTIC_SymbolTable;

public class CODEGEN_AssemblyFilePrinter{
	private FileWriter fileWriter = null;
	public static CODEGEN_AssemblyFilePrinter instance;
	public static int labelIndex;
	
	public final static String NEW_LINE_STRING = System.lineSeparator();
	
	public static void reset(){
		instance = null;
		labelIndex = 0;
	}
	
	protected CODEGEN_AssemblyFilePrinter(String filePath) throws IOException{
		fileWriter = new FileWriter(filePath);
	}
	
	public static FileWriter getInstance(String filePath) throws IOException{
		if(instance == null){
			instance = new CODEGEN_AssemblyFilePrinter(filePath);
		}
		return instance.fileWriter;
	}
	
	public static int addLabelIndex(){
		labelIndex++;
		return labelIndex;
	}
	
	private static void printVirtualFunctionsTableForClass(SEMANTIC_ClassSymbolInfo classSymbolInfo) throws IOException{
		StringBuilder printed = new StringBuilder();
		if(classSymbolInfo.virtualFunctionsOrder.size()==0){
			return;
		}
		printed.append(String.format("%s: .word ",classSymbolInfo.getVFTableLabel()));
		List<String> virtualFunctionsOrder = classSymbolInfo.virtualFunctionsOrder;
		Hashtable<String,String> virtualFunctionsTable = classSymbolInfo.virtualFunctionsTable;
		for(int i=0;i<virtualFunctionsOrder.size();i++){
			printed.append(virtualFunctionsTable.get(virtualFunctionsOrder.get(i)));
			
			if(i!=virtualFunctionsOrder.size()-1){
				printed.append(",");
			}
		}
		CODEGEN_AssemblyFilePrinter.getInstance(null).write(printed.toString()+System.lineSeparator());
	}
	
	public static void printVirtualFunctionsTablesAndUpdateVFTAdresses() throws IOException{
		List<SEMANTIC_SymbolInfoNode> hashTableValues = new ArrayList<SEMANTIC_SymbolInfoNode>(SEMANTIC_SymbolTable.hashTable.values());
		for(int i=0;i<hashTableValues.size();i++){
			SEMANTIC_SymbolInfoNode temp = hashTableValues.get(i);
			if(temp.symbolInfo instanceof SEMANTIC_ClassSymbolInfo){
				 printVirtualFunctionsTableForClass((SEMANTIC_ClassSymbolInfo)temp.symbolInfo);
			}
		}
	}
}
