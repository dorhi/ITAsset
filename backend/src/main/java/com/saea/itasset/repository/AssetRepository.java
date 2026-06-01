package com.saea.itasset.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class AssetRepository {

    private static final Logger logger = LoggerFactory.getLogger(AssetRepository.class);
    private final JdbcTemplate jdbcTemplate;

    public AssetRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 자산 조회 API
    public Map<String, Object> searchAsset(String eno) {
        String sql = "EXEC ITM_SEARCH_EQ_USER_DATA ?";
        try {
            // queryForList를 사용하여 결과가 0개이거나 1개 이상일 경우에도 500 에러 대신 안전하게 처리
            java.util.List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, eno);
            
            if (results.isEmpty()) {
                logger.warn("No asset found in DB for ENO: [{}]", eno);
                return null;
            }
            
            if (results.size() > 1) {
                logger.warn("Multiple assets found for ENO: [{}] (Count: {}). Returning the first one.", eno, results.size());
            }
            
            return results.get(0);
        } catch (Exception e) {
            logger.error("Error executing ITM_SEARCH_EQ_USER_DATA for ENO: {}. Error: {}", eno, e.getMessage(), e);
            return null; 
        }
    }

    // 자산 회수 API
    public void returnAsset(String eno, String memberId, String currentUserId) {
        String sql = "EXEC REQUEST.DBO.ITM_RETURN_EQ ?, ?, ?";
        jdbcTemplate.update(sql, eno, memberId, currentUserId);
    }

    // UNO 생성 API
    public String createUno() {
        String sql = "EXEC REQUEST.DBO.ITM_UNO_CREATE";
        // 결과가 단일 문자열 값으로 반환된다고 가정
        return jdbcTemplate.queryForObject(sql, String.class);
    }

    // 자산 지급/매핑 API
    public void assignAsset(String eno, String uno, String companyCode, String saeaGCode, 
                            String memberId, String currentUserId) {
        String sql = "EXEC REQUEST.[dbo].[ITTM_SET_EQUIPMENT_USER_DATA_M] ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
        
        // 사용자가 제공한 정확한 파라미터 순서 매핑 (12개):
        // 1: @PROC_TYPE = 'I'
        // 2: @ENO = eno
        // 3: @UNO = uno (ITM_UNO_CREATE 결과물)
        // 4: @COUNTRY = 'KOR'
        // 5: @COMPANY_NO = 대상자의 companyCode
        // 6: @SAEA_GCODE = 대상자의 saeaGCode
        // 7: @USESTARTDATE = null
        // 8: @USEENDDATE = null
        // 9: @MEMBERID = memberId (지급 대상자 사번)
        // 10: @REMARK = null
        // 11: @OLD_ENO = eno
        // 12: @USER = currentUserId (시스템 로그인한 처리자 ID)
        
        logger.info("Calling ITTM_SET_EQUIPMENT_USER_DATA_M with 12 params: " +
                    "1:PROC_TYPE[I], 2:ENO[{}], 3:UNO[{}], 4:COUNTRY[KOR], 5:COMPANY_NO[{}], 6:SAEA_GCODE[{}], " +
                    "7:START[null], 8:END[null], 9:MEMBERID[{}], 10:REMARK[null], 11:OLD_ENO[{}], 12:USER[{}]",
                    eno, uno, companyCode, saeaGCode, memberId, eno, currentUserId);

        jdbcTemplate.update(sql, 
            "I",          // 1: PROC_TYPE
            eno,          // 2: ENO
            uno,          // 3: UNO
            "KOR",        // 4: COUNTRY
            companyCode,  // 5: COMPANY_NO
            saeaGCode,    // 6: SAEA_GCODE
            null,         // 7: USESTARTDATE
            null,         // 8: USEENDDATE
            memberId,     // 9: MEMBERID
            null,         // 10: REMARK
            eno,          // 11: OLD_ENO
            currentUserId // 12: USER
        );
    }
}
