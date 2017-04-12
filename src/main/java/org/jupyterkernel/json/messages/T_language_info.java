/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jupyterkernel.json.messages;

import org.jupyterkernel.util.JSONField;

/**
 *
 * @author kay schluehr
 */
public class T_language_info extends T_JSON {

    @JSONField
    public String name;
    
    @JSONField
    public String version;
    
    @JSONField
    public String mimetype;
    
    @JSONField
    public String file_extension;
    
    @JSONField
    public String pygments_lexer;

    @JSONField
    public String codemirror_mode;

    @JSONField
    public String nbconverter_exporter;    
}
