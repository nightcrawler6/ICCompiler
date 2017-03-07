package SEMANTIC;
import java.util.*;

public class SEMANTIC_SymbolTable{

	public static Hashtable<String,SEMANTIC_SymbolInfoNode> hashTable=new Hashtable<String,SEMANTIC_SymbolInfoNode>();
	public static final String SCOPE_SYMBOL_NAME="<<BSCOPE>>";
	public static final String MAIN_FUNC_SYMBOL_NAME = "main";
	public static final String PRINTINT_FUNC_SYMBOL_NAME = "printInt"; 
	public static final String PRINT_CLASS_SYMBOL_NAME = "PRINT"; 

	public static final int ADDRESS_SIZE=4;
	public static String mainFunctionLabel = null; 

	public static void insertNewSymbol(SEMANTIC_SymbolInfo symbolInfo){
		SEMANTIC_SymbolInfoNode sameNamePointer=hashTable.get(symbolInfo.symbolName);
		SEMANTIC_SymbolInfoNode iteratorForScope=hashTable.get(SCOPE_SYMBOL_NAME);
		while((iteratorForScope!=null)&&(iteratorForScope.nextSymbolInScope!=null)){
			iteratorForScope=iteratorForScope.nextSymbolInScope;
		}
		SEMANTIC_SymbolInfoNode insertedSymbolInfo=new SEMANTIC_SymbolInfoNode(symbolInfo,sameNamePointer,null);
		if(iteratorForScope!=null){
			iteratorForScope.nextSymbolInScope=insertedSymbolInfo;
		}
		hashTable.put(symbolInfo.symbolName,insertedSymbolInfo);
	}
	
	public static boolean doesSymbolExistInCurrentScope(String symbolName){
		SEMANTIC_SymbolInfoNode iterator=hashTable.get(SCOPE_SYMBOL_NAME);
		while(iterator!=null){
			
			if((iterator.symbolInfo!=null)&&(iterator.symbolInfo.symbolName.equals(symbolName))){
				return true;
			}
			iterator=iterator.nextSymbolInScope;
		}
		return false;
	}
	
	public static void createNewScope(){
		SEMANTIC_SymbolInfoNode node=SEMANTIC_SymbolTable.hashTable.get(SCOPE_SYMBOL_NAME);
		SEMANTIC_SymbolInfoNode newNode=new SEMANTIC_SymbolInfoNode(null,null,null);
		if(node!=null){
			newNode.hiddenSymbol=node;
		}
		
		SEMANTIC_SymbolTable.hashTable.put(SCOPE_SYMBOL_NAME,newNode);
	}
	public static void closeCurrentScope(){
		SEMANTIC_SymbolInfoNode node=SEMANTIC_SymbolTable.hashTable.get(SCOPE_SYMBOL_NAME);
		
		SEMANTIC_SymbolInfoNode temp=node.nextSymbolInScope;
		while(temp!=null){
			SEMANTIC_SymbolInfoNode entry=SEMANTIC_SymbolTable.hashTable.get(temp.symbolInfo.symbolName);
			if(entry.hiddenSymbol!=null){
				SEMANTIC_SymbolTable.hashTable.put(temp.symbolInfo.symbolName,entry.hiddenSymbol);
			}
			else{
				SEMANTIC_SymbolTable.hashTable.remove(temp.symbolInfo.symbolName);
			}
			temp=temp.nextSymbolInScope;
		}
		if(node.hiddenSymbol!=null){
			SEMANTIC_SymbolTable.hashTable.put(SCOPE_SYMBOL_NAME, node.hiddenSymbol);
		}
		else{
			SEMANTIC_SymbolTable.hashTable.remove(SCOPE_SYMBOL_NAME);

		}
	}
	
	public static boolean doesClassExist(String className){
		SEMANTIC_SymbolInfoNode symbolInfoNode = hashTable.get(className);
		if (symbolInfoNode != null){
			SEMANTIC_SymbolInfo symbolInfo = symbolInfoNode.symbolInfo;
			if ((symbolInfo != null) && (symbolInfo instanceof SEMANTIC_ClassSymbolInfo)){
				return true;
			} 
		}
		
		return false;
	}
	
	public static SEMANTIC_SymbolInfo searchSymbolInfoInClassAndUp(String className, String symbolName) throws SEMANTIC_ClassIsNotInSymbolTableException{
		SEMANTIC_SymbolInfoNode classSymbolInfoNode= hashTable.get(className);
		if(classSymbolInfoNode!=null)
		{
			SEMANTIC_SymbolInfo symbolInfo=classSymbolInfoNode.symbolInfo;
			if(symbolInfo instanceof SEMANTIC_ClassSymbolInfo){
				SEMANTIC_VariableSymbolInfo fieldFound = ((SEMANTIC_ClassSymbolInfo) symbolInfo).searchField(symbolName);
				if(fieldFound!=null){
					return fieldFound;
				}
				else{
					SEMANTIC_FunctionSymbolInfo methodFound = ((SEMANTIC_ClassSymbolInfo) symbolInfo).searchMethod(symbolName);
					if(methodFound!=null){
						return methodFound;
					}
					else{
						String extendedClassName  = ((SEMANTIC_ClassSymbolInfo) symbolInfo).extendedClassName;
						if(extendedClassName!=null){
							return searchSymbolInfoInClassAndUp(extendedClassName,symbolName);
						}
						else{
							return null;
						}
					}
				}
			}
			else{
				throw new SEMANTIC_ClassIsNotInSymbolTableException();
			}
			
		}
		else{
			throw new SEMANTIC_ClassIsNotInSymbolTableException();
		}
	}
	
	public static SEMANTIC_SymbolInfo searchSymbolInfoLocallyOrInCurrentClassAndUp(String currenClassName,String symbolName) throws SEMANTIC_ClassIsNotInSymbolTableException{
		SEMANTIC_SymbolInfoNode classSymbolInfoNode = hashTable.get(symbolName);
		if(classSymbolInfoNode!=null){
			return classSymbolInfoNode.symbolInfo;
		}
		else{
			return searchSymbolInfoInClassAndUp(currenClassName, symbolName);
		}
	}
	
	public static boolean addFormalToMethod(String className,String functionName, SEMANTIC_VariableSymbolInfo formal) throws SEMANTIC_SemanticAnalysisException{
		if(SEMANTIC_SymbolTable.doesSymbolExistInCurrentScope(formal.symbolName)==true){
			return false;
		}
		else{
			SEMANTIC_SymbolInfo currentSymbolInfo=SEMANTIC_SymbolTable.searchSymbolInfoInClassAndUp(className, functionName);
			if(currentSymbolInfo instanceof SEMANTIC_FunctionSymbolInfo){
				SEMANTIC_FunctionSymbolInfo currentMethod=(SEMANTIC_FunctionSymbolInfo)currentSymbolInfo;
				currentMethod.addFormal(formal.variableType);
			}
			else{
				return false;
			}
			currentSymbolInfo=SEMANTIC_SymbolTable.searchSymbolInfoLocallyOrInCurrentClassAndUp(className, functionName);
			if(currentSymbolInfo instanceof SEMANTIC_FunctionSymbolInfo){
				SEMANTIC_FunctionSymbolInfo currentMethod=(SEMANTIC_FunctionSymbolInfo)currentSymbolInfo;
				currentMethod.addFormal(formal.variableType);
			}
			
		}
		return true;
		
	}
	
	public static void addMethodToClass(String className, SEMANTIC_FunctionSymbolInfo methodInfo) throws SEMANTIC_ClassIsNotInSymbolTableException{
		SEMANTIC_SymbolInfoNode classSymbolInfoNode= hashTable.get(className);
		if(classSymbolInfoNode!=null){
			SEMANTIC_SymbolInfo symbolInfo=classSymbolInfoNode.symbolInfo;
			if(symbolInfo instanceof SEMANTIC_ClassSymbolInfo){
				((SEMANTIC_ClassSymbolInfo) symbolInfo).addMethod(methodInfo);
			}
			else{
				throw new SEMANTIC_ClassIsNotInSymbolTableException();
			}
		}
		else{
			throw new SEMANTIC_ClassIsNotInSymbolTableException();
		}
	}
	
	public static void addFieldToClass(String className, SEMANTIC_VariableSymbolInfo fieldInfo) throws SEMANTIC_ClassIsNotInSymbolTableException{
		SEMANTIC_SymbolInfoNode classSymbolInfoNode= hashTable.get(className);
		if(classSymbolInfoNode!=null){
			SEMANTIC_SymbolInfo symbolInfo=classSymbolInfoNode.symbolInfo;
			if(symbolInfo instanceof SEMANTIC_ClassSymbolInfo){
				((SEMANTIC_ClassSymbolInfo) symbolInfo).addField(fieldInfo);
			}
			else{
				throw new SEMANTIC_ClassIsNotInSymbolTableException();
			}
		}
		else{
			throw new SEMANTIC_ClassIsNotInSymbolTableException();
		}
	}
	
	public static SEMANTIC_ClassSymbolInfo getClassSymbolInfo(String className){
		SEMANTIC_SymbolInfoNode classSymbolInfoNode= hashTable.get(className);
		if(classSymbolInfoNode!=null){
			SEMANTIC_SymbolInfo symbolInfo=classSymbolInfoNode.symbolInfo;
			if(symbolInfo instanceof SEMANTIC_ClassSymbolInfo){
				return (SEMANTIC_ClassSymbolInfo) symbolInfo;
			}
		}
		return null;
	}
	
	public static SEMANTIC_FunctionSymbolInfo getMethodSymbolInfo(String className, String functionName){
		SEMANTIC_SymbolInfoNode classSymbolInfoNode= hashTable.get(className);
		if(classSymbolInfoNode!=null){
			SEMANTIC_SymbolInfo symbolInfo=classSymbolInfoNode.symbolInfo;
			if(symbolInfo instanceof SEMANTIC_ClassSymbolInfo){
				
				SEMANTIC_ClassSymbolInfo currClass = (SEMANTIC_ClassSymbolInfo) symbolInfo;
				return currClass.searchMethod(functionName);
			}
		}
		return null;
	}
	
	public static boolean validatePredeccessor(SEMANTIC_ICTypeInfo predeccessor, SEMANTIC_ICTypeInfo descendent) throws SEMANTIC_SemanticAnalysisException{
		if (predeccessor.ICType.equals(SEMANTIC_ICTypeInfo.IC_TYPE_NULL)){
			return descendent.ICType.equals(SEMANTIC_ICTypeInfo.IC_TYPE_NULL);
		}
		if(descendent.ICType.equals(SEMANTIC_ICTypeInfo.IC_TYPE_NULL)){
			if (predeccessor.isFlatICType(SEMANTIC_ICTypeInfo.IC_TYPE_INT)){
				return false;
			}
			else{
				return true;
			}
		}
		if(predeccessor.pointerDepth!=descendent.pointerDepth){
			return false;
		}
		
		if((!predeccessor.ICType.equals(SEMANTIC_ICTypeInfo.IC_TYPE_INT)) && (!predeccessor.ICType.equals(SEMANTIC_ICTypeInfo.IC_TYPE_STRING))&&(doesClassExist(predeccessor.ICType)==false)){
			return false;
		}
		if((!descendent.ICType.equals(SEMANTIC_ICTypeInfo.IC_TYPE_INT)) && (!descendent.ICType.equals(SEMANTIC_ICTypeInfo.IC_TYPE_STRING))&&(doesClassExist(descendent.ICType)==false)){
			return false;
		}
		if(descendent.pointerDepth>0){
			if(!predeccessor.ICType.equals(descendent.ICType)){
				return false;
			}
			else{
				return true;
			}
		}
		if(predeccessor.ICType.equals(descendent.ICType)){
			return true;
		}
		if ((!predeccessor.isICClass()) || (!descendent.isICClass())){
			return false;
		}
		SEMANTIC_SymbolInfoNode descendentClassNode= hashTable.get(descendent.ICType);
		SEMANTIC_ClassSymbolInfo descendentClass=(SEMANTIC_ClassSymbolInfo) descendentClassNode.symbolInfo;
		if(descendentClass.extendedClassName!=null){
			if(validatePredeccessor(predeccessor, new SEMANTIC_ICTypeInfo(descendentClass.extendedClassName,0))){
				return true;	
			}
		}
		return false;
	}
	
	public static boolean doesOneMainExistInProgram() throws SEMANTIC_ClassIsNotInSymbolTableException{
		int count=0;
		
		SEMANTIC_SymbolInfoNode node=hashTable.get(SCOPE_SYMBOL_NAME);
		
		node=node.nextSymbolInScope;
		while(node!=null){
			if(!(node.symbolInfo instanceof SEMANTIC_ClassSymbolInfo)){
				throw new SEMANTIC_ClassIsNotInSymbolTableException();
			}
			
			SEMANTIC_ClassSymbolInfo temp=(SEMANTIC_ClassSymbolInfo)node.symbolInfo;
			int currClassMainCount=temp.getMainFunctionsCount();
			if(currClassMainCount>1){
				return false;
			}
			else{
				count=count+currClassMainCount;
			}
			node=node.nextSymbolInScope;
		}
		return (count==1);
	}
}
