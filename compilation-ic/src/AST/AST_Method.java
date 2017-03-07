package AST;

import IR.IR_AsmLabel;
import IR.IR_Method;
import IR.IR_StatementList;
import SEMANTIC.SEMANTIC_FunctionSymbolInfo;
import SEMANTIC.SEMANTIC_ICTypeInfo;
import SEMANTIC.SEMANTIC_SemanticErrorException;
import SEMANTIC.SEMANTIC_SymbolInfo;
import SEMANTIC.SEMANTIC_SymbolTable;
import UTILS.DebugPrint;

public class AST_Method extends AST_FieldMethod{
	
	public AST_Type returnArgumentType;
	public AST_FormalsList formalsList;
	public AST_StatementList body;
	public boolean isMainFunc = false;
	public boolean isPrintFunc = false;
	
	public static final String METHOD_LABEL_PREFIX = "Label_0_";
	
	public AST_Method(AST_Type returnArgumentType,String methodName,AST_FormalsList formalsList,AST_StatementList body){
		this.returnArgumentType=returnArgumentType;
		this.currentFunctionName = methodName;
		this.formalsList=formalsList;
		this.body=body;
	}
	
	public SEMANTIC_ICTypeInfo validate(String className) throws SEMANTIC_SemanticErrorException{		

		if(returnArgumentType==null){
			this.body.expectedReturnType=new SEMANTIC_ICTypeInfo(SEMANTIC_ICTypeInfo.IC_TYPE_VOID,0);
		}
		else{
			this.body.expectedReturnType=this.returnArgumentType.validate(className);
			if(this.body.expectedReturnType==null){
				return null;
			}
			
		}
		
		if(SEMANTIC_SymbolTable.doesSymbolExistInCurrentScope(currentFunctionName)){
			return null;
		}
		SEMANTIC_SymbolInfo symbolWithTheSameName=SEMANTIC_SymbolTable.searchSymbolInfoInClassAndUp(className, currentFunctionName);
		if((symbolWithTheSameName!=null)&&(!(symbolWithTheSameName instanceof SEMANTIC_FunctionSymbolInfo))){
			return null;
		}
		SEMANTIC_SymbolTable.addMethodToClass(className, new SEMANTIC_FunctionSymbolInfo(currentFunctionName,this.body.expectedReturnType,null));
		
		SEMANTIC_FunctionSymbolInfo methodSymbolInfo = new SEMANTIC_FunctionSymbolInfo(currentFunctionName,this.body.expectedReturnType,null);
		SEMANTIC_SymbolTable.insertNewSymbol(methodSymbolInfo);
		if(this.formalsList!=null){
			this.formalsList.currentFunctionName=currentFunctionName;
		}
		SEMANTIC_SymbolTable.createNewScope();
		if((this.formalsList!=null)&&(this.formalsList.validate(className)==null)){
			return null;
		}

		if(this.body.validate(className)==null){
			DebugPrint.print("AST_METHOD.validate: The body isn't valid (returns an incompatible type, or invalid in itself)");
			return null;
		}	
		if ((!this.body.expectedReturnType.ICType.equals(SEMANTIC_ICTypeInfo.IC_TYPE_VOID)) &&
			(!this.body.doesAlwaysReturnValue)){
			DebugPrint.print("AST_METHOD.validate: The body doesn't always return a value though it should");
			return null;
		}
		
		
		SEMANTIC_SymbolTable.closeCurrentScope();
		if (currentFunctionName.equals(SEMANTIC_SymbolTable.MAIN_FUNC_SYMBOL_NAME)){
			if (!methodSymbolInfo.validateMainIsValid()){
				DebugPrint.print("AST_METHOD.validate: Invalid main.");
				return null;
			}
			this.isMainFunc = true;
		}
		if(currentFunctionName.equals(SEMANTIC_SymbolTable.PRINTINT_FUNC_SYMBOL_NAME) && (className.equals(SEMANTIC_SymbolTable.PRINT_CLASS_SYMBOL_NAME))){
			this.isPrintFunc = true;
		}
		return new SEMANTIC_ICTypeInfo();
	}
	
	public IR_Method createIR() throws SEMANTIC_SemanticErrorException{
		assertClassAndFunctionNamesInitialized();
		SEMANTIC_FunctionSymbolInfo methodSymbolInfo = new SEMANTIC_FunctionSymbolInfo(currentFunctionName,this.body.expectedReturnType,null);
		methodSymbolInfo.isMainFunc = this.isMainFunc;
		methodSymbolInfo.isPrintFunc = this.isPrintFunc;
		methodSymbolInfo.functionLabel = String.format("%s%s_%s", METHOD_LABEL_PREFIX,currentClassName,currentFunctionName);
		SEMANTIC_SymbolTable.addMethodToClass(currentClassName, methodSymbolInfo);
		SEMANTIC_SymbolTable.insertNewSymbol(methodSymbolInfo);
		
		SEMANTIC_SymbolTable.createNewScope();
		
		if(this.formalsList!=null){
			this.formalsList.currentClassName=this.currentClassName;
			this.formalsList.currentFunctionName=this.currentFunctionName;
			this.formalsList.formalFrameOffset = AST_Node.FRAME_OFFSET_OF_FIRST_FORMAL; 
			this.formalsList.createIR();
		}
		
		IR_StatementList bodyStmtList;
		if(this.body!=null){
			this.body.currentClassName=this.currentClassName;
			this.body.currentFunctionName=this.currentFunctionName;
			bodyStmtList=this.body.createIR();
		}
		else{
			bodyStmtList=null;
		}
		SEMANTIC_SymbolTable.closeCurrentScope();
		
		if(isMainFunc){
			SEMANTIC_SymbolTable.mainFunctionLabel = methodSymbolInfo.functionLabel;
		}
		
		return new IR_Method(new IR_AsmLabel(methodSymbolInfo.functionLabel),
							 bodyStmtList, 
							 methodSymbolInfo.frameSize,
							 isMainFunc,
							 isPrintFunc);
	}
}