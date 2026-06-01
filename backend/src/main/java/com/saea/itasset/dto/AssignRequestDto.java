package com.saea.itasset.dto;

import lombok.Data;

@Data
public class AssignRequestDto {
    private String eno;            // 자산번호 (QR 스캔)
    private String memberId;       // 지급받을 대상 사번
    private String companyCode;    // 대상자의 COMPANY_CODE
    private String saeaGCode;      // 대상자의 SAEA_GCODE
}
