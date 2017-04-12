/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jupyterkernel.json.messages;

import org.json.JSONObject;
import org.jupyterkernel.util.JSONField;

/**
 *
 * @author kay schluehr
 */
public class T_inspect_reply extends T_JSON {
    @JSONField
    public String status = "ok";

    @JSONField
    public Boolean found = false;    
    
    @JSONField
    public Integer detail_level;        
    
    @JSONField
    public JSONObject data;        
    
    @JSONField
    public JSONObject metadata;        

}
