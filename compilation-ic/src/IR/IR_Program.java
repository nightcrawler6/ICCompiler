package IR;

import java.io.IOException;

import AST.AST_Method;
import AST.AST_Node;
import CODEGEN.CODEGEN_AssemblyFilePrinter;
import CODEGEN.CODEGEN_Temporary;
import CODEGEN.CODEGEN_Misc;
import CODEGEN.CODEGEN_StringAttacher;
import CODEGEN.CODEGEN_TemporaryFactory;
import SEMANTIC.SEMANTIC_SemanticErrorException;
import SEMANTIC.SEMANTIC_SymbolTable;
import SEMANTIC.SEMANTIC_TempsPastLimitException;

public class IR_Program extends IR_Node {

	private static final String MAIN_WRAPPER_LABEL =String.format("Label_%d_main_wrapper",CODEGEN_AssemblyFilePrinter.addLabelIndex());
	public IR_ClassDeclList classDeclList;
	
	public static final String STRLEN_FUNCTION_LABEL = AST_Method.METHOD_LABEL_PREFIX + "strlen";
	public static final String MEMCPY_FUNCTION_LABEL = AST_Method.METHOD_LABEL_PREFIX + "memcpy";
	public static final String STRCAT_FUNCTION_LABEL = AST_Method.METHOD_LABEL_PREFIX + "strcat";
	public static final String WHILE_START_LABEL_SUFFIX = "_while_start";
	public static final String WHILE_END_LABEL_SUFFIX = "_while_end";
	
	public static final int STATIC_FUNC_FIRST_ARG_OFFSET = AST_Node.FRAME_OFFSET_OF_THE_THIS_ARGUMENT;
	
	public IR_Program(IR_ClassDeclList classDeclList){
		this.classDeclList = classDeclList;
	}

	public void writeMainWrapper(CODEGEN_StringAttacher printed) throws IOException, SEMANTIC_TempsPastLimitException{
		printed.appendNL(String.format("%s:", MAIN_WRAPPER_LABEL));
		CODEGEN_Temporary zeroTemp = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		printed.appendNL(String.format("li %s,0", zeroTemp.getName()));
		CODEGEN_Misc.codeGen_Push(printed,zeroTemp.getName());
		printed.appendNL(String.format("jal %s", SEMANTIC_SymbolTable.mainFunctionLabel));
		printed.appendNL(String.format("j %s", END_LABEL_NAME));
	}
	
	public void writeErrorTreatment(CODEGEN_StringAttacher printed){
		printed.appendNL(String.format("%s:", ERROR_LABEL_NAME));
		printed.appendNL("li $a0,666");
		printed.appendNL("li $v0,1");
		printed.appendNL("syscall");
		printed.appendNL(String.format("j %s", END_LABEL_NAME));
	}
	
	public void writeProgramTermination(CODEGEN_StringAttacher printed)
	{
		printed.appendNL(String.format("%s:", END_LABEL_NAME));
		printed.appendNL("li $v0,10");
		printed.appendNL("syscall");
	}
	
	private void writeStrlenInitialization(CODEGEN_StringAttacher printed,
										   CODEGEN_Temporary zero,
										   CODEGEN_Temporary strAddress,
										   CODEGEN_Temporary strByte,
										   CODEGEN_Temporary index) throws SEMANTIC_TempsPastLimitException{
		CODEGEN_Temporary argAddress = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		
		printed.appendNL(String.format("addi %s, $fp, %d", 
									   argAddress.getName(), 
									   STATIC_FUNC_FIRST_ARG_OFFSET));
		printed.appendNL(String.format("lw %s, 0(%s)", 
									   strAddress.getName(),
									   argAddress.getName()));
		
		printed.appendNL(String.format("lb %s, (%s)", 
									   strByte.getName(), 
									   strAddress.getName()));
		printed.appendNL(String.format("li %s, 0", index.getName()));
		printed.appendNL(String.format("li %s, 0", zero.getName()));
	}
	
	private void writeStrlenLoop(CODEGEN_StringAttacher printed,
			 					 CODEGEN_Temporary zero,
			 					 CODEGEN_Temporary strAddress,
			 					 CODEGEN_Temporary strByte,
			 					 CODEGEN_Temporary index) throws SEMANTIC_TempsPastLimitException{
		
		CODEGEN_Temporary strByteAddress = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		String whileStartLabel = STRLEN_FUNCTION_LABEL + WHILE_START_LABEL_SUFFIX;
		String whileEndLabel = STRLEN_FUNCTION_LABEL + WHILE_END_LABEL_SUFFIX;
		
		printed.appendNL(String.format("%s:", whileStartLabel));
		printed.appendNL(String.format("beq %s, %s, %s",
									   strByte.getName(),
									   zero.getName(),
									   whileEndLabel));
		
		printed.appendNL(String.format("addi %s, %s, 1", index.getName(), index.getName()));
		printed.appendNL(String.format("add %s, %s, %s", 
									   strByteAddress.getName(),
									   strAddress.getName(),
									   index.getName()));
		printed.appendNL(String.format("lb %s, (%s)", strByte.getName(), strByteAddress.getName()));
		
		printed.appendNL(String.format("j %s", whileStartLabel));
		printed.appendNL(String.format("%s:", whileEndLabel));
	}
	
	private void writeStrlen(CODEGEN_StringAttacher printed) throws SEMANTIC_TempsPastLimitException, IOException{
		CODEGEN_Temporary zero = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		CODEGEN_Temporary strAddress = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		CODEGEN_Temporary strByte = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		CODEGEN_Temporary index = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		
		printed.appendNL(String.format("%s:", STRLEN_FUNCTION_LABEL));
		IR_Method.appendProlog(printed, 0);
		
		writeStrlenInitialization(printed, zero, strAddress, strByte, index);
		writeStrlenLoop(printed, zero, strAddress, strByte, index);
		
		printed.appendNL(String.format("mov $v0, %s", index.getName()));
		IR_Method.appendEpilog(printed);
	}

	private void writeMemcpyArgsLoading(CODEGEN_StringAttacher printed,
										CODEGEN_Temporary dstAddress,
										CODEGEN_Temporary srcAddress,
										CODEGEN_Temporary bytesToCopyNum) throws SEMANTIC_TempsPastLimitException{
		
		CODEGEN_Temporary argAddress = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		
		printed.appendNL(String.format("addi %s, $fp, %d",
									   argAddress.getName(),
									   STATIC_FUNC_FIRST_ARG_OFFSET));
		printed.appendNL(String.format("lw %s, 0(%s)", 
									   dstAddress.getName(),
									   argAddress.getName()));
		
		printed.appendNL(String.format("addi %s, %s, %d", 
									   argAddress.getName(),
									   argAddress.getName(),
									   SEMANTIC_SymbolTable.ADDRESS_SIZE));
		printed.appendNL(String.format("lw %s, 0(%s)", 
				   					   srcAddress.getName(),
				   					   argAddress.getName()));
		
		printed.appendNL(String.format("addi %s, %s, %d", 
				   					   argAddress.getName(),
				   					   argAddress.getName(),
				   					   SEMANTIC_SymbolTable.ADDRESS_SIZE));
		printed.appendNL(String.format("lw %s, 0(%s)", 
				   					   bytesToCopyNum.getName(),
				   					   argAddress.getName()));
	}
	
	private void writeMemcpyLoop(CODEGEN_StringAttacher printed,
								 CODEGEN_Temporary dstAddress,
								 CODEGEN_Temporary srcAddress,
								 CODEGEN_Temporary bytesToCopyNum) throws SEMANTIC_TempsPastLimitException{
		
		CODEGEN_Temporary index = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		CODEGEN_Temporary srcByteAddress = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		CODEGEN_Temporary dstByteAddress = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		CODEGEN_Temporary srcByte = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		
		String whileStartLabel = MEMCPY_FUNCTION_LABEL + WHILE_START_LABEL_SUFFIX;
		String whileEndLabel = MEMCPY_FUNCTION_LABEL + WHILE_END_LABEL_SUFFIX;
		
		printed.appendNL(String.format("li %s, 0", index.getName()));	
		printed.appendNL(String.format("%s:", whileStartLabel));
		
		printed.appendNL(String.format("beq %s, %s, %s", 
									   index.getName(),
									   bytesToCopyNum.getName(),
									   whileEndLabel));
		
		printed.appendNL(String.format("add %s, %s, %s", 
				   					   srcByteAddress.getName(),
				   					   srcAddress.getName(),
				   					   index.getName()));
		printed.appendNL(String.format("add %s, %s, %s", 
				   					   dstByteAddress.getName(),
				   					   dstAddress.getName(),
				   					   index.getName()));
		
		printed.appendNL(String.format("lb %s, (%s)", 
				   					   srcByte.getName(),
				   					   srcByteAddress.getName()));
		printed.appendNL(String.format("sb %s, (%s)", 
				   					   srcByte.getName(),
				   					   dstByteAddress.getName()));
		
		printed.appendNL(String.format("addi %s, %s, 1", 
				   					   index.getName(),
				   					   index.getName()));
		
		printed.appendNL(String.format("j %s", whileStartLabel));
		printed.appendNL(String.format("%s:", whileEndLabel));
	}
	
	private void writeMemcpy(CODEGEN_StringAttacher printed) throws SEMANTIC_TempsPastLimitException, IOException{
		CODEGEN_Temporary dstAddress = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		CODEGEN_Temporary srcAddress = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		CODEGEN_Temporary bytesToCopyNum = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		
		printed.appendNL(String.format("%s:", MEMCPY_FUNCTION_LABEL));
		IR_Method.appendProlog(printed, 0);
		writeMemcpyArgsLoading(printed, dstAddress, srcAddress, bytesToCopyNum);
		writeMemcpyLoop(printed, dstAddress, srcAddress, bytesToCopyNum);
		printed.appendNL("li $v0, 0");
		IR_Method.appendEpilog(printed);
	}

	private void writeStrcatArgsLoading(CODEGEN_StringAttacher printed, 
										CODEGEN_Temporary str1Address, 
										CODEGEN_Temporary str2Address) throws SEMANTIC_TempsPastLimitException
	{
		CODEGEN_Temporary argAddress = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		
		printed.appendNL(String.format("addi %s, $fp, %d",
									   argAddress.getName(),
									   STATIC_FUNC_FIRST_ARG_OFFSET));
		printed.appendNL(String.format("lw %s, 0(%s)", 
									   str1Address.getName(),
									   argAddress.getName()));
		
		printed.appendNL(String.format("addi %s, %s, %d", 
									   argAddress.getName(),
									   argAddress.getName(),
									   SEMANTIC_SymbolTable.ADDRESS_SIZE));
		printed.appendNL(String.format("lw %s, 0(%s)", 
									   str2Address.getName(),
				   					   argAddress.getName()));
	}
	
	private void writeStrLengthCalculation(CODEGEN_StringAttacher printed,
										   CODEGEN_Temporary strAddress,
										   CODEGEN_Temporary strLength) throws IOException
	{
		CODEGEN_Misc.codeGen_Push(printed, strAddress.getName());
		printed.appendNL(String.format("jal %s", STRLEN_FUNCTION_LABEL));
		printed.appendNL(String.format("addi $sp, $sp, %d", SEMANTIC_SymbolTable.ADDRESS_SIZE));
		printed.appendNL(String.format("mov %s, $v0", strLength.getName()));
	}
	
	private CODEGEN_Temporary writeAllocationForConcat(CODEGEN_StringAttacher printed,
										  CODEGEN_Temporary str1Length,
										  CODEGEN_Temporary str2Length) throws SEMANTIC_TempsPastLimitException, IOException{
		
		CODEGEN_Temporary concatLength = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		
		printed.appendNL(String.format("add %s, %s, %s", 
									   concatLength.getName(),
									   str1Length.getName(),
									   str2Length.getName()));
		
		printed.appendNL(String.format("addi %s, %s, 1", 
									   concatLength.getName(), 
									   concatLength.getName()));
		
		return CODEGEN_Misc.codeGen_malloc(printed, concatLength);
	}
	
	private void writeStrCopyAndPointerPromotion(CODEGEN_StringAttacher printed,
												 CODEGEN_Temporary dstStrPtr,
												 CODEGEN_Temporary srcStrAddress,
												 CODEGEN_Temporary srcStrLength) throws IOException{
		
		CODEGEN_Misc.codeGen_Push(printed, srcStrLength.getName());
		CODEGEN_Misc.codeGen_Push(printed, srcStrAddress.getName());
		CODEGEN_Misc.codeGen_Push(printed, dstStrPtr.getName());
		
		printed.appendNL(String.format("jal %s", MEMCPY_FUNCTION_LABEL));
		printed.appendNL(String.format("addi $sp, $sp, %d", 3 * SEMANTIC_SymbolTable.ADDRESS_SIZE));

		printed.appendNL(String.format("add %s, %s, %s", 
									   dstStrPtr.getName(),
									   dstStrPtr.getName(),
									   srcStrLength.getName()));
	}
	
	private void writeNullTerminatorAddition(CODEGEN_StringAttacher printed,
											 CODEGEN_Temporary nullTerminatorIndex) throws SEMANTIC_TempsPastLimitException{
		CODEGEN_Temporary nullTerminator = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		
		printed.appendNL(String.format("li %s, 0", nullTerminator.getName()));
		printed.appendNL(String.format("sb %s, (%s)", 
									   nullTerminator.getName(), 
									   nullTerminatorIndex.getName()));	
	}
	
	private void writeStrcat(CODEGEN_StringAttacher printed) throws SEMANTIC_TempsPastLimitException, IOException{
		CODEGEN_Temporary str1Address = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		CODEGEN_Temporary str2Address = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		CODEGEN_Temporary str1Length = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		CODEGEN_Temporary str2Length = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		CODEGEN_Temporary concatStr = null;
		CODEGEN_Temporary concatPtr = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		
		printed.appendNL(String.format("%s:", STRCAT_FUNCTION_LABEL));
		IR_Method.appendProlog(printed, 0);
		
		writeStrcatArgsLoading(printed, str1Address, str2Address);
		writeStrLengthCalculation(printed, str1Address, str1Length);
		writeStrLengthCalculation(printed, str2Address, str2Length);
		
		concatStr = writeAllocationForConcat(printed, str1Length, str2Length);
		printed.appendNL(String.format("mov %s, %s", concatPtr.getName(), concatStr.getName()));
		writeStrCopyAndPointerPromotion(printed, concatPtr, str1Address, str1Length);
		writeStrCopyAndPointerPromotion(printed, concatPtr, str2Address, str2Length);
		writeNullTerminatorAddition(printed, concatPtr);
		
		printed.appendNL(String.format("mov $v0, %s", concatStr.getName())); 
		IR_Method.appendEpilog(printed);
	}
	
	private void writeStaticUtilityFunctions(CODEGEN_StringAttacher printed) throws SEMANTIC_TempsPastLimitException, IOException{
		writeStrlen(printed);
		writeMemcpy(printed);
		writeStrcat(printed);
	}
	
	public void generateCode() throws IOException, SEMANTIC_SemanticErrorException{
		CODEGEN_StringAttacher printed = new CODEGEN_StringAttacher();
		printed.appendNL(String.format("j %s", MAIN_WRAPPER_LABEL));
		writeStaticUtilityFunctions(printed);
		CODEGEN_AssemblyFilePrinter.getInstance(null).write(printed.toString());
		
		this.classDeclList.generateCode();
		
		printed = new CODEGEN_StringAttacher();
		writeMainWrapper(printed);
		writeErrorTreatment(printed);
		writeProgramTermination(printed);
		CODEGEN_AssemblyFilePrinter.getInstance(null).write(printed.toString());
	}
	
}
