package CODEGEN;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import SEMANTIC.SEMANTIC_Tuple;

public class CODEGEN_StringCollector 
{
	public static List<SEMANTIC_Tuple<String,String>> labelAndStringMappings;

	public static void reset(){
		labelAndStringMappings = new ArrayList<SEMANTIC_Tuple<String,String>>();
	}
	
	public static String addStringAndLabelMapping(String str){
		String stringLabel = String.format("String_%d",CODEGEN_AssemblyFilePrinter.addLabelIndex());
		labelAndStringMappings.add(new SEMANTIC_Tuple<String,String>(stringLabel,str));
		return stringLabel;
	}
	
	public static void printStringsToAssembly() throws IOException{
		if(labelAndStringMappings.size() == 0){
			return;
		}
		CODEGEN_StringAttacher printed = new CODEGEN_StringAttacher();
		printed.appendNL(".data");
		for(int i=0;i<labelAndStringMappings.size();i++){
			SEMANTIC_Tuple<String,String> currPair = labelAndStringMappings.get(i);
			printed.appendNL(String.format("%s: .asciiz \"%s\"", currPair.left,currPair.right));
		}
		CODEGEN_AssemblyFilePrinter.getInstance(null).write(printed.toString());;
	}
}
