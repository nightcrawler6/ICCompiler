package IR;

import CODEGEN.CODEGEN_AssemblyFilePrinter;

public class IR_Node {

	public static final int VAR_DEFAULT_INIT_VALUE = 0;
	public static final String ERROR_LABEL_NAME = String.format("Label_%d_ERROR",CODEGEN_AssemblyFilePrinter.addLabelIndex()); 
	public static final String END_LABEL_NAME = String.format("Label_%d_END",CODEGEN_AssemblyFilePrinter.addLabelIndex());
	public static final String PRINTINT_FUNC_LABEL = "Label_0_PRINT_printInt";

}
