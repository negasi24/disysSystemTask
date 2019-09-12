package com.disys.systemtask.utility;

import java.util.HashMap;
import java.util.Map;

public interface Credential {

    /*
    There is no api for check the credential so that i hardcode the sample user credntial using interface.
     */

    public static HashMap<String, String>  getCredntials() {

        HashMap<String, String> sampleCredential = new HashMap<String,String>() {{
            put("deepan", "123456");
            put("joe", "123456");
            put("hussain","123456");
            put("prithviraj","123456");
            put("admin","123456");
            put("test","123456");
        }};

        return sampleCredential;
    }

}
