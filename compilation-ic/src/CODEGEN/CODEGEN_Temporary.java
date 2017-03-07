package CODEGEN;

public class CODEGEN_Temporary {
	public int tempIndex;
	public CODEGEN_Temporary(int tempIndex){
		this.tempIndex=tempIndex;
	}
	
	public String getName(){
		return String.format("Temp_%d", tempIndex);
	}
}
