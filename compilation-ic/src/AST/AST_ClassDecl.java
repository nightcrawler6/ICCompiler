package AST;
import IR.IR_ClassDecl;
import IR.IR_MethodList;
import SEMANTIC.*;
public class AST_ClassDecl extends AST_Node{
	
	public String extendsClassName;
	public AST_FieldMethodList fieldsOrMethods;
	
	public AST_ClassDecl(String declaredClassName,String extendsClassName,AST_FieldMethodList l){
		this.fieldsOrMethods=l;
		this.currentClassName = declaredClassName;
		this.extendsClassName=extendsClassName;
	}
	
	public SEMANTIC_ICTypeInfo validate(String receivedClassName) throws SEMANTIC_SemanticErrorException{

		if(this.extendsClassName!=null){
			if(SEMANTIC_SymbolTable.doesClassExist(extendsClassName) == false){
				return null;
			}
		}
		
		SEMANTIC_ClassSymbolInfo classSymbolInfo=new SEMANTIC_ClassSymbolInfo(this.currentClassName, this.extendsClassName, null, null);
		if(SEMANTIC_SymbolTable.doesSymbolExistInCurrentScope(currentClassName)){
			return null;
		}
		SEMANTIC_SymbolTable.insertNewSymbol(classSymbolInfo);
		SEMANTIC_SymbolTable.createNewScope();

		if(this.fieldsOrMethods!=null){
			AST_FieldMethodList iterator=this.fieldsOrMethods;
			while(iterator!=null){
				AST_FieldMethod currObj=iterator.head;
				if(currObj instanceof AST_Field){
					SEMANTIC_ICTypeInfo fieldValidation=((AST_Field)currObj).validate(currentClassName);
					if(fieldValidation == null)	{
						return null;
					}
				}
				else if(currObj instanceof AST_Method){
					SEMANTIC_ICTypeInfo methodValidation=((AST_Method)currObj).validate(currentClassName);
					if(methodValidation == null){
						return null;
					}
					
					if(extendsClassName!=null){
						SEMANTIC_FunctionSymbolInfo newInsertedMethod=(SEMANTIC_FunctionSymbolInfo)SEMANTIC_SymbolTable.searchSymbolInfoLocallyOrInCurrentClassAndUp(currentClassName,((AST_Method) currObj).currentFunctionName) ;
						SEMANTIC_FunctionSymbolInfo methodWithTheSameNameInPredesseccor=(SEMANTIC_FunctionSymbolInfo)SEMANTIC_SymbolTable.searchSymbolInfoInClassAndUp(extendsClassName,((AST_Method) currObj).currentFunctionName) ;
						if(methodWithTheSameNameInPredesseccor!=null){
							if(!newInsertedMethod.equals(methodWithTheSameNameInPredesseccor)){
								return null;
							}
						}
					}
				}
				
				iterator=iterator.tail;
			}
		}
		SEMANTIC_SymbolTable.closeCurrentScope();
		return new SEMANTIC_ICTypeInfo();
	}
	
	public IR_ClassDecl createIR() throws SEMANTIC_SemanticErrorException{
		IR_MethodList classMethods=null;
		SEMANTIC_ClassSymbolInfo classSymbolInfo=new SEMANTIC_ClassSymbolInfo(this.currentClassName, this.extendsClassName, null, null);
		SEMANTIC_SymbolTable.insertNewSymbol(classSymbolInfo);
		SEMANTIC_SymbolTable.createNewScope();
		if(this.fieldsOrMethods!=null){
			this.fieldsOrMethods.currentClassName = this.currentClassName;
			classMethods = this.fieldsOrMethods.createIR();
			
		}
		SEMANTIC_SymbolTable.closeCurrentScope();
		
		return new IR_ClassDecl(classMethods);
	}
	
}
