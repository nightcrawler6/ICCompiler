package AST;
import IR.IR_Binop;
import IR.IR_EXP;
import IR.IR_ExpBinop;
import IR.IR_ExpMemory;
import IR.IR_LiteralConstant;
import IR.IR_Temporary;
import IR.IR_TemporaryType;
import SEMANTIC.SEMANTIC_NoInitForClassException;
import SEMANTIC.SEMANTIC_NoInitForMethodException;
import SEMANTIC.SEMANTIC_ICTypeInfo;
import SEMANTIC.SEMANTIC_SemanticErrorException;

public abstract class AST_Node{
	public int SerialNumber;
	
	public String currentClassName = null;
	
	public String currentFunctionName = null; 
	
	public static final int FRAME_OFFSET_OF_FIRST_LOCAL = -4;
	public static final int FRAME_OFFSET_OF_RETURN_ADDRESS = 4;
	public static final int FRAME_OFFSET_OF_THE_THIS_ARGUMENT = 8; 
	public static final int FRAME_OFFSET_OF_FIRST_FORMAL = 12;
	
	public SEMANTIC_ICTypeInfo validate(String className) throws SEMANTIC_SemanticErrorException{
		return null;
	}
	
	public void assertClassNameInitialized() throws SEMANTIC_NoInitForClassException{
		if(this.currentClassName==null){
			throw new SEMANTIC_NoInitForClassException();
		}
	}
	
	public void assertClassAndFunctionNamesInitialized() throws SEMANTIC_NoInitForMethodException{
		if(this.currentClassName==null){
			throw new SEMANTIC_NoInitForMethodException();
		}
		if(currentFunctionName==null){
			throw new SEMANTIC_NoInitForMethodException();
		}
	}
	
	public IR_EXP getThisObjectHeapAddress(){
		IR_EXP fp = new IR_Temporary(IR_TemporaryType.fp);
		IR_EXP firstArgumentOffset = new IR_LiteralConstant(FRAME_OFFSET_OF_THE_THIS_ARGUMENT);
		
		IR_EXP firstArgumentAddress = new IR_ExpBinop(fp, firstArgumentOffset, IR_Binop.PLUS, false);
		IR_EXP firstArgumentContent = new IR_ExpMemory(firstArgumentAddress);
		
		return firstArgumentContent;
	}
	
}