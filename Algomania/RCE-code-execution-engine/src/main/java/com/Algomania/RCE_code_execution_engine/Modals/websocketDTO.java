package com.Algomania.RCE_code_execution_engine.Modals;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class websocketDTO {
    private String uuid;
    private String code;
    private String input_data;
    private String lang;

}

