package at.fhv.dgr1992.ePuck.ePuckVRep;

import java.util.LinkedList;

/**
 * Enums representing the VRep return codes of the API
 */
public enum VRepReturnCode {
    UnknownVRepReturnCode (-1),
    simx_return_ok (0),
    simx_return_novalue_flag (1),           /* input buffer doesn't contain the specified command */
    simx_return_timeout_flag (2),           /* command reply not received in time for simx_opmode_blocking operation mode */
    simx_return_illegal_opmode_flag (4),    /* command doesn't support the specified operation mode */
    simx_return_remote_error_flag (8),      /* command caused an error on the server side */
    simx_return_split_progress_flag (16),   /* previous similar command not yet fully processed (applies to simx_opmode_oneshot_split operation modes) */
    simx_return_local_error_flag (32),      /* command caused an error on the client side */
    simx_return_initialize_error_flag (64); /* simxStart was not yet called */

    private final int _returnCode;

    VRepReturnCode(int returnCode) {
        _returnCode = returnCode;
    }

    /**
     * Creates the enum based on the given retrun code value
     * @param returnCode Return code value received from the VRep API
     * @return Enum representation of the integer return code
     */
    public static VRepReturnCode[] defineReturnCode(int returnCode){
        LinkedList<VRepReturnCode> returnCodes = new LinkedList<VRepReturnCode>();

        if(returnCode == 0){
            returnCodes.add(VRepReturnCode.simx_return_ok);
        }

        if((returnCode & 1) == 1){
            returnCodes.add(VRepReturnCode.simx_return_novalue_flag);
        }

        if((returnCode & 2) == 2){
            returnCodes.add(VRepReturnCode.simx_return_timeout_flag);
        }

        if((returnCode & 4) == 4){
            returnCodes.add(VRepReturnCode.simx_return_illegal_opmode_flag);
        }

        if((returnCode & 8) == 8){
            returnCodes.add(VRepReturnCode.simx_return_remote_error_flag);
        }

        if((returnCode & 16) == 16){
            returnCodes.add(VRepReturnCode.simx_return_split_progress_flag);
        }

        if((returnCode & 32) == 32){
            returnCodes.add(VRepReturnCode.simx_return_local_error_flag);
        }

        if((returnCode & 64) == 64){
            returnCodes.add(VRepReturnCode.simx_return_initialize_error_flag);
        }

        if(returnCodes.size() == 0){
            returnCodes.add(VRepReturnCode.UnknownVRepReturnCode);
        }

        VRepReturnCode[] returnArray = new VRepReturnCode[returnCodes.size()];
        int i = 0;
        for (VRepReturnCode code: returnCodes) {
            returnArray[i] = code;
            i++;
        }
        return returnArray;
    }

    /**
     * get the description for the enum
     * @return String with the message for the return code
     */
    public String getDetailDescription(){
        String msg;
        switch(_returnCode){
            case 0:
                msg = "Code 0: OK";
                break;
            case 1:
                msg = "Code 1: Input buffer doesn't contain the specified command.";
                break;
            case 2:
                msg = "Code 2: Command reply not received in time for simx_opmode_blocking operation mode.";
                break;
            case 4:
                msg = "Code 3: Command doesn't support the specified operation mode.";
                break;
            case 8:
                msg = "Code 4: Command caused an error on the server side.";
                break;
            case 16:
                msg = "Code 5: Previous similar command not yet fully processed (applies to simx_opmode_oneshot_split operation modes).";
                break;
            case 32:
                msg = "Code 32: Command caused an error on the client side.";
                break;
            case 64:
                msg = "Code 64: simxStart was not yet called.";
                break;

            default:
                msg = "Given return code unknown. Return code: " + _returnCode;
        }

        return msg;
    }
}
