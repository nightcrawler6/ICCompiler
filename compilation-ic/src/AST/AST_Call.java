package AST;

import java.util.List;

import IR.IR_Binop;
import IR.IR_Call;
import IR.IR_EXP;
import IR.IR_ExpBinop;
import IR.IR_ExpList;
import IR.IR_ExpMemory;
import IR.IR_LiteralConstant;
import SEMANTIC.SEMANTIC_NoSuchClassInTableException;
import SEMANTIC.SEMANTIC_NoInitForMethodException;
import SEMANTIC.SEMANTIC_NoSuchMethodInTableException;
import SEMANTIC.SEMANTIC_FunctionSymbolInfo;
import SEMANTIC.SEMANTIC_ICTypeInfo;
import SEMANTIC.SEMANTIC_SemanticErrorException;
import SEMANTIC.SEMANTIC_SymbolInfo;
import SEMANTIC.SEMANTIC_SymbolTable;
import UTILS.DebugPrint;

public class AST_Call extends AST_Node{
	
	public AST_Exp caller;
	public String callerClassName = null;
	public String calledFunctionName;
	public AST_ExpList args;
	
	public AST_Call(AST_Exp exp, String funcName, AST_ExpList args){
		this.caller = exp;
		this.calledFunctionName = funcName;
		this.args = args;
	}
	
	private SEMANTIC_SymbolInfo getObjectSymbolInfo(String className) throws SEMANTIC_SemanticErrorException{
		
		SEMANTIC_ICTypeInfo callerTypeInfo = caller.validate(className);
		if (callerTypeInfo == null){
			DebugPrint.print("AST_CALL.getObjectSymbolInfo: The calling expression isn't a valid expression.");
			return null;
		}
		if (!callerTypeInfo.isICClass()){
			String debugMessage = 
					String.format("AST_CALL.getObjectSymbolInfo: The calling expression isn't an object, exp : %s", 
								  callerTypeInfo); 
			DebugPrint.print(debugMessage);
			return null;
		}
		
		callerClassName = callerTypeInfo.ICType;
		SEMANTIC_SymbolInfo symbolInfo = SEMANTIC_SymbolTable.searchSymbolInfoInClassAndUp(callerClassName, calledFunctionName);
		if (symbolInfo == null){
			String debugMessage = 
					String.format("AST_CALL.getObjectSymbolInfo: The class '%s' doesn't contain the symbol '%s'.", 
								  callerTypeInfo.ICType, calledFunctionName);
			DebugPrint.print(debugMessage);
			return null;
		}
		
		return symbolInfo;
	}
	
	private SEMANTIC_FunctionSymbolInfo getFunctionSymbolInfo(String className) throws SEMANTIC_SemanticErrorException{
		SEMANTIC_SymbolInfo functionSymbolInfo = null;
		
		if (caller == null){
			functionSymbolInfo = 
					SEMANTIC_SymbolTable.searchSymbolInfoLocallyOrInCurrentClassAndUp(className, calledFunctionName);
			if (functionSymbolInfo == null){
				DebugPrint.print("AST_CALL.getFunctionSymbolInfo: The symbol '" + calledFunctionName + "' doesn't exist locally or in the class '" + className + "'.");
				return null;
			}
		}
		else{
			functionSymbolInfo = getObjectSymbolInfo(className);
			if (functionSymbolInfo == null){
				return null;
			}
		}
		
		if (!(functionSymbolInfo instanceof SEMANTIC_FunctionSymbolInfo)){
			DebugPrint.print("AST_CALL.getFunctionSymbolInfo: '" + calledFunctionName + "' is not a function.");
			return null;
		}
		
		return (SEMANTIC_FunctionSymbolInfo)functionSymbolInfo;		
	}
	
	private boolean validateSingleActualArgument(SEMANTIC_ICTypeInfo formalArgumentType, 
			AST_Exp actualArgument, String className) throws SEMANTIC_SemanticErrorException{

		SEMANTIC_ICTypeInfo actualArgumentType = actualArgument.validate(className);
		if (actualArgumentType == null){
			DebugPrint.print("AST_CALL.validateSingleActualArgument: The expression is invalid.");
			return false;
		}
		
		if (!SEMANTIC_SymbolTable.validatePredeccessor(formalArgumentType, actualArgumentType)){
			String debugMessage = String.format("AST_CALL.validateSingleActualArgument: Formal/actual type mismatch, formal : %s, actual : %s", 
												formalArgumentType, actualArgumentType);
			DebugPrint.print(debugMessage);
			return false;
		}
		
		return true;
	}
	
	private boolean validateActualArguments(List<SEMANTIC_ICTypeInfo> formalArgumentTypes, 
			AST_ExpList actualArguments, String className) throws SEMANTIC_SemanticErrorException{

		if ((formalArgumentTypes == null) || (formalArgumentTypes.size() == 0)){
			if ((actualArguments == null) || (actualArguments.head == null)){
				return true;
			}
			
			DebugPrint.print("AST_CALL.validateActualArguments: Too many actual arguments.");
			return false;
		}
		if ((actualArguments == null) || (actualArguments.head == null)){
			String debugMessage = String.format("AST_CALL.validateActualArguments: Missing %d actual argument(s).",
												formalArgumentTypes.size());
			DebugPrint.print(debugMessage);
			return false;
		}
		
		if (!validateSingleActualArgument(formalArgumentTypes.get(0), actualArguments.head, className)){
			return false;
		}
		
		List<SEMANTIC_ICTypeInfo> formalArgumentsTail = formalArgumentTypes.subList(1, formalArgumentTypes.size());
		return validateActualArguments(formalArgumentsTail, actualArguments.tail, className);
	}
	
	@Override
	public SEMANTIC_ICTypeInfo validate(String className) throws SEMANTIC_SemanticErrorException{

		SEMANTIC_FunctionSymbolInfo functionSymbolInfo = getFunctionSymbolInfo(className);
		if (functionSymbolInfo == null){
			return null;
		}
		
		if (!validateActualArguments(functionSymbolInfo.argumentsTypes, args, className)){
			return null;
		}
		return functionSymbolInfo.returnType;
	}
	
	private void bequeathClassAndFunctionNamesToChildren() throws SEMANTIC_NoInitForMethodException{
		assertClassAndFunctionNamesInitialized();
		
		if (caller != null){
			caller.currentClassName = this.currentClassName;
			caller.currentFunctionName = this.currentFunctionName;
		}
	
		if (args != null){
			args.currentClassName = this.currentClassName;
			args.currentFunctionName = this.currentFunctionName;
		}
	}
	
	private IR_EXP getCallerAddress() throws SEMANTIC_SemanticErrorException{
		if (caller == null){
			return getThisObjectHeapAddress();
		}
		else{
			return caller.createIR();
		}
	}
	
	private IR_EXP getFunctionOffsetInCallerVirtualTable() throws SEMANTIC_NoSuchMethodInTableException, SEMANTIC_NoSuchClassInTableException{
		if (callerClassName == null){
			callerClassName = currentClassName;
		}
		
		SEMANTIC_SymbolInfo calledFunctionInfo = SEMANTIC_SymbolTable.searchSymbolInfoInClassAndUp(callerClassName, calledFunctionName);
		if ((calledFunctionInfo == null) || (!(calledFunctionInfo instanceof SEMANTIC_FunctionSymbolInfo))){
			throw new SEMANTIC_NoSuchMethodInTableException(callerClassName, calledFunctionName);
		}
		
		return new IR_LiteralConstant(((SEMANTIC_FunctionSymbolInfo)calledFunctionInfo).offset);
	}
	
	private IR_EXP getCalledFunctionAddress(IR_EXP callerAddress) throws SEMANTIC_NoSuchMethodInTableException, SEMANTIC_NoSuchClassInTableException{
		IR_EXP callerVirtualTableAddress = new IR_ExpMemory(callerAddress); 
		
		IR_EXP calledFunctionOffset = getFunctionOffsetInCallerVirtualTable();
		IR_EXP virtualTableEntryAddress = new IR_ExpBinop(callerVirtualTableAddress, 
				   										   calledFunctionOffset, 
				   										   IR_Binop.PLUS,
				   										   false);
		
		IR_EXP virtualTableEntryContent = new IR_ExpMemory(virtualTableEntryAddress);
		return virtualTableEntryContent;
	}
	
	public IR_Call createIR() throws SEMANTIC_SemanticErrorException{
		bequeathClassAndFunctionNamesToChildren();
		
		if(calledFunctionName.equals(SEMANTIC_SymbolTable.PRINTINT_FUNC_SYMBOL_NAME)){
			IR_ExpList argumentsIR = null;
			if (args != null){
				argumentsIR = args.createIR();
			}
			
			return new IR_Call(argumentsIR,true);
		}
		
		IR_EXP callerAddress = getCallerAddress();
		IR_EXP calledFunctionAddress = getCalledFunctionAddress(callerAddress);
		
		IR_ExpList argumentsIR = null;
		if (args != null){
			argumentsIR = args.createIR();
		}
		
		return new IR_Call(calledFunctionAddress, callerAddress, argumentsIR);
	}
}
