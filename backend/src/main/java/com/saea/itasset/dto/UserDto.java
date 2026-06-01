package com.saea.itasset.dto;

import lombok.Data;

@Data
public class UserDto {
    private String id;          // 사번 (또는 로그인 ID)
    private String name;        // 이름
    private String deptCode;    // 부서코드 (1550 인지 판별용)
    private String deptName;    // 부서명
    private String companyCode; // COMPANY_CODE
    private String saeaGCode;   // SAEA_GCODE
    private String password;    // 비밀번호 (DB 매핑용)
    
    // 권한 검사용 헬퍼 메소드
    public boolean isAdmin() {
        return "1550".equals(this.deptCode);
    }
}
