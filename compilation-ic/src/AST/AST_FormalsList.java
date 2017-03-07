package AST;

import SEMANTIC.SEMANTIC_NoInitForFormalMethodException;
import SEMANTIC.SEMANTIC_ICTypeInfo;
import SEMANTIC.SEMANTIC_SemanticErrorException;
import SEMANTIC.SEMANTIC_SymbolTable;
import SEMANTIC.SEMANTIC_VariableSymbolInfo;

public class AST_FormalsList extends AST_Node{
	public SEMANTIC_ICTypeInfo formalICType;
	public AST_Type formal_type;
	public String formal_name;
	public AST_FormalsList tail;
	public int formalFrameOffset;
	
	public AST_FormalsList(AST_Type formal_type,String formal_name,AST_FormalsList tail){
		this.tail=tail;
		this.formal_type=formal_type;
		this.formal_name=formal_name;
	}
	
	public SEMANTIC_ICTypeInfo validate(String className) throws SEMANTIC_SemanticErrorException{
		if(this.currentFunctionName==null){
			throw new SEMANTIC_NoInitForFormalMethodException();
		}
		else{
			formalICType=this.formal_type.validate(className);
			if (formalICType == null){
				return null;
			}
			if(SEMANTIC_SymbolTable.doesSymbolExistInCurrentScope(formal_name)){
				return null;
			}
			SEMANTIC_VariableSymbolInfo formalInfo=new SEMANTIC_VariableSymbolInfo(formal_name, formalICType);
			if(SEMANTIC_SymbolTable.addFormalToMethod(className,currentFunctionName, formalInfo)==false){
				return null;
			}
			
			SEMANTIC_SymbolTable.insertNewSymbol(formalInfo);
				
			if (this.tail != null){
				tail.currentFunctionName = this.currentFunctionName;
				if (tail.validate(className) == null){
					return null;
				}
			}
		}
		return new SEMANTIC_ICTypeInfo();
	}
	public boolean isEmpty(){
		return ((this.tail==null)&&(this.formal_name==null));
	}
	
	public void createIR() throws SEMANTIC_SemanticErrorException{
		assertClassAndFunctionNamesInitialized();
		SEMANTIC_VariableSymbolInfo formalInfo=new SEMANTIC_VariableSymbolInfo(formal_name, formalICType,this.formalFrameOffset, false);
		SEMANTIC_SymbolTable.addFormalToMethod(currentClassName,currentFunctionName, formalInfo);
		SEMANTIC_SymbolTable.insertNewSymbol(formalInfo);
		if(this.tail!=null){
			this.tail.currentFunctionName = this.currentFunctionName;
			this.tail.currentClassName = this.currentClassName;
			this.tail.formalFrameOffset=this.formalFrameOffset+SEMANTIC_SymbolTable.ADDRESS_SIZE;
			this.tail.createIR();
		}
	}
}