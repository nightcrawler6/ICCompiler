package SEMANTIC;


public class SEMANTIC_ICTypeInfo {
	public String ICType;
	public int pointerDepth;
	
	public static final String IC_TYPE_INT = "int";
	public static final String IC_TYPE_STRING = "string";
	public static final String IC_TYPE_NULL = "null";
	public static final String IC_TYPE_VOID = "void";
	
	public SEMANTIC_ICTypeInfo(){
		this.ICType=null;
		this.pointerDepth=-1;
	}
	
	public SEMANTIC_ICTypeInfo(String ICType, int pointerDepth){
		this.ICType=ICType;
		this.pointerDepth=pointerDepth;
	}
	
	public boolean equals(Object obj){
		SEMANTIC_ICTypeInfo compared=(SEMANTIC_ICTypeInfo)obj;
		if(this.pointerDepth!=compared.pointerDepth){
			return false;
			
		}
		if(!this.ICType.equals(compared.ICType)){
			return false;
		}
		return true;
	}
	
	public boolean isFlatICType(String otherICType){
		return (ICType.equals(otherICType) &&
				pointerDepth == 0);
	}
	
	public boolean isICClass(){
		
		boolean isSpecialICType = (ICType.equals(IC_TYPE_INT)) 		||
								  (ICType.equals(IC_TYPE_STRING))	||
								  (ICType.equals(IC_TYPE_VOID)) 	||
							      (ICType.equals(IC_TYPE_NULL));
		
		return ((!isSpecialICType) &&
				(pointerDepth == 0));
	}
	
	@Override
	public String toString(){
		String rep = ICType;
		
		int i = pointerDepth;
		while (i > 0){
			rep += "[]";
			i--;
		}
		
		return rep;
	}
}
