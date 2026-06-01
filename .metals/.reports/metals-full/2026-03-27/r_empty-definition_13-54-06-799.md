error id: file:///E:/Vibe/ITASSET/backend/src/main/java/com/saea/itasset/repository/AssetRepository.java:local8
file:///E:/Vibe/ITASSET/backend/src/main/java/com/saea/itasset/repository/AssetRepository.java
empty definition using pc, found symbol in pc: 
found definition using semanticdb; symbol local8
empty definition using fallback
non-local guesses:

offset: 1528
uri: file:///E:/Vibe/ITASSET/backend/src/main/java/com/saea/itasset/repository/AssetRepository.java
text:
```scala
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
        String sql@@ = "EXEC REQUEST.DBO.ITM_RETURN_EQ ?, ?, ?";
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
        
        // 파라미터 매핑:
        // @PROC_TYPE = 'I'
        // @ENO = eno
        // @UNO = uno
        // @COUNTRY = 'KOR'
        // @COMPANY_NO = companyCode
        // @SAEA_GCODE = saeaGCode
        // @MEMBERID = memberId
        // @OLD_ENO = eno
        // @USER = currentUserId
        // @USESTARTDATE = NULL
        // @USEENDDATE = NULL
        // @REMARK = NULL
        
        jdbcTemplate.update(sql, 
            "I",          // PROC_TYPE
            eno,          // ENO
            uno,          // UNO
            "KOR",        // COUNTRY
            companyCode,  // COMPANY_NO
            saeaGCode,    // SAEA_GCODE
            memberId,     // MEMBERID
            eno,          // OLD_ENO
            currentUserId,// USER
            null,         // USESTARTDATE
            null,         // USEENDDATE
            null          // REMARK
        );
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: 