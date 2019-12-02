package at.fhv.dgr1992.exceptions;

public class VelocityLimitException extends Exception{
    public VelocityLimitException(){
        super();
    }

    public VelocityLimitException(String msg){
        super(msg);
    }
}
