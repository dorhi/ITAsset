import React, { useState, lazy, Suspense } from 'react';
import { useAuth } from '../context/AuthContext';
const QRScanner = lazy(() => import('../components/QRScanner'));
import UserSearchModal from '../components/UserSearchModal';
import axios from 'axios';
import { LogOut } from 'lucide-react';

const Dashboard = () => {
  const { user, logout } = useAuth();
  const [mode, setMode] = useState(null); // 'search', 'assign', 'return', 'register'
  const [scanResult, setScanResult] = useState('');
  const [assetInfo, setAssetInfo] = useState(null);
  const [targetUser, setTargetUser] = useState(null);
  
  const [showScanner, setShowScanner] = useState(false);
  const [showUserModal, setShowUserModal] = useState(false);
  const [manualEno, setManualEno] = useState('');
  const [message, setMessage] = useState('');

  // API에서 넘어온 user는 단순 JSON 객체이므로 메서드(isAdmin)가 존재하지 않습니다. 
  // 속성값인 deptCode로 관리자(1550) 여부를 판단합니다.
  const isAdmin = user && user.deptCode === '1550';

  // 유틸리티 함수: 객체에서 대소문자 구분 없이 값을 가져옴
  const getVal = (obj, key) => {
    if (!obj || typeof obj !== 'object') return '-';
    const foundKey = Object.keys(obj).find(k => k.toLowerCase() === key.toLowerCase());
    return foundKey ? (obj[foundKey] || '-') : '-';
  };

  const handleScan = async (eno) => {
    setScanResult(eno);
    setShowScanner(false);
    
    try {
      if (mode === 'search') {
        const res = await axios.get(`/api/assets/search?eno=${eno}`);
        console.log("Asset Search Result:", res.data);
        setAssetInfo(res.data || {}); // null/empty 대응
      } else if (mode === 'return') {
        const res = await axios.get(`/api/assets/search?eno=${eno}`);
        setAssetInfo(res.data || {});
      } else if (mode === 'assign') {
        const res = await axios.get(`/api/assets/search?eno=${eno}`);
        const data = res.data || {};
        const currentMember = getVal(data, 'membernm') !== '-' ? getVal(data, 'membernm') : 
                            (getVal(data, 'membername') !== '-' ? getVal(data, 'membername') : '');
        
        if (currentMember && currentMember.trim() !== '') {
          if (window.confirm(`이미 [${currentMember}]님에게 할당된 자산입니다. 회수(반납) 화면으로 이동하여 먼저 처리하시겠습니까?`)) {
            setMode('return');
            setAssetInfo(data);
            return;
          }
          setScanResult('');
          return;
        }
        setShowUserModal(true);
      } else if (mode === 'register') {
        // 일반 사용자 내 자산 매핑
        handleAssignSubmit(eno, user);
      }
    } catch (err) {
      showMessage(err.response?.data?.message || '자산 정보를 찾을 수 없습니다.');
    }
  };

  const handleError = (err) => {
    console.warn("QR Scan Error:", err);
  };

  const handleUserSelect = (selectedUser) => {
    setTargetUser(selectedUser);
    setShowUserModal(false);
    handleAssignSubmit(scanResult, selectedUser);
  };

  const handleAssignSubmit = async (eno, assignTo) => {
    console.log("Assign Request - ENO:", eno, "Target User:", assignTo); // 지급 대상자 로그 출력
    try {
      await axios.post('/api/assets/assign', {
        eno: eno,
        memberId: assignTo.id,
        companyCode: assignTo.companyCode,
        saeaGCode: assignTo.saeaGCode
      });
      showMessage(`자산 지급 요청 완료: [${eno}] -> [${assignTo.name}(${assignTo.id})]`);
      resetState();
    } catch (err) {
      showMessage('처리 중 오류가 발생했습니다: ' + (err.response?.data?.message || err.message));
    }
  };

  const handleReturnSubmit = async () => {
    const memberId = getVal(assetInfo, 'memberid') !== '-' ? getVal(assetInfo, 'memberid') : getVal(assetInfo, 'MEMBERID');
    if (!assetInfo || memberId === '-') {
      showMessage('회수 대상자 정보(MEMBER_ID)가 없습니다.');
      return;
    }
    try {
      await axios.post('/api/assets/return', {
        eno: scanResult,
        memberId: memberId
      });
      showMessage(`자산 회수 완료: [${scanResult}]`);
      resetState();
    } catch (err) {
      showMessage('처리 중 오류가 발생했습니다: ' + (err.response?.data?.message || err.message));
    }
  };

  const showMessage = (msg) => {
    setMessage(msg);
    // 디버깅을 위해 메시지 노출 시간을 10초로 연장
    setTimeout(() => setMessage(''), 10000);
  };

  const resetState = () => {
    setMode(null);
    setScanResult('');
    setAssetInfo(null);
    setTargetUser(null);
    setShowScanner(false);
    setManualEno('');
  };

  const startMode = (selectedMode) => {
    resetState();
    setMode(selectedMode);
    setShowScanner(true);
  };

  return (
    <div className="dashboard">
      <header className="dash-header">
        <div className="user-info">
          <span className="user-name">{user?.name} 님 ({isAdmin ? 'Admin' : 'User'})</span>
          <span className="dept-name">{user?.deptName}</span>
        </div>
        <button onClick={logout} className="logout-btn">
          <LogOut size={20} /> 로그아웃
        </button>
      </header>
      
      {message && <div className="toast-msg">{message}</div>}

      {!showScanner && !assetInfo && (
        <main className="menu-grid">
          {isAdmin ? (
            <>
              <button className="menu-btn" onClick={() => startMode('search')}>
                자산 조회
              </button>
              <button className="menu-btn" onClick={() => startMode('assign')}>
                자산 지급
              </button>
              <button className="menu-btn" onClick={() => startMode('return')}>
                자산 회수
              </button>
            </>
          ) : (
            <button className="menu-btn" onClick={() => startMode('register')}>
              내 자산 등록
            </button>
          )}
        </main>
      )}

      {showScanner && (
        <div className="scanner-container">
          <button className="back-btn" onClick={resetState}>뒤로 가기</button>
          <h3>QR 코드를 스캔하거나 번호를 입력하세요</h3>
          
          <Suspense fallback={<div className="loading-screen" style={{color: '#fff'}}>스캐너 로딩 중...</div>}>
            <QRScanner onScan={handleScan} onError={handleError} />
          </Suspense>
          
          <div className="manual-input">
            <input 
              type="text" 
              placeholder="자산 번호 직접 입력 (예: E12345)" 
              value={manualEno}
              onChange={(e) => setManualEno(e.target.value)}
              className="eno-input"
            />
            <button 
              className="submit-btn" 
              onClick={() => manualEno.trim() && handleScan(manualEno.trim())}
            >
              확인
            </button>
          </div>
        </div>
      )}

      {assetInfo && !showScanner && mode === 'search' && (
        <div className="result-card">
          <h3>자산 정보 조회 결과</h3>
          <div className="info-row"><span>자산번호:</span> <span>{getVal(assetInfo, 'eno') === '-' ? scanResult : getVal(assetInfo, 'eno')}</span></div>
          <div className="info-row"><span>자산품목:</span> <span>{getVal(assetInfo, 'equipmentnm')}</span></div>
          <div className="info-row"><span>모델번호:</span> <span>{getVal(assetInfo, 'modelnm')}</span></div>
          <div className="info-row"><span>모델내용:</span> <span>{getVal(assetInfo, 'modelinfo')}</span></div>
          <div className="info-row"><span>할당 사용자:</span> <span>{getVal(assetInfo, 'membernm') === '-' ? getVal(assetInfo, 'membername') : getVal(assetInfo, 'membernm')}</span></div>
          <div className="info-row"><span>사용 부서:</span> <span>{getVal(assetInfo, 'deptnm') === '-' ? getVal(assetInfo, 'dept_name') : getVal(assetInfo, 'deptnm')}</span></div>
          <button className="back-btn full-w" onClick={resetState}>돌아가기</button>
        </div>
      )}

      {assetInfo && !showScanner && mode === 'return' && (
        <div className="result-card">
          <h3>회수할 자산 확인</h3>
          <div className="info-row"><span>자산번호:</span> <span>{getVal(assetInfo, 'eno') === '-' ? scanResult : getVal(assetInfo, 'eno')}</span></div>
          <div className="info-row"><span>현재 사용자:</span> <span>{getVal(assetInfo, 'membernm') === '-' ? getVal(assetInfo, 'membername') : getVal(assetInfo, 'membernm')}</span></div>
          <div className="action-buttons">
            <button className="submit-btn" onClick={handleReturnSubmit}>회수 확인</button>
            <button className="cancel-btn" onClick={resetState}>취소</button>
          </div>
        </div>
      )}

      {showUserModal && (
        <UserSearchModal 
          onSelect={handleUserSelect} 
          onClose={() => resetState()} 
        />
      )}
    </div>
  );
};

export default Dashboard;
