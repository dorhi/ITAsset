import React, { useEffect, useRef } from 'react';
import { Html5QrcodeScanner } from 'html5-qrcode';

const QRScanner = ({ onScan, onError }) => {
  const scannerRef = useRef(null);
  const isInitialized = useRef(false);

  useEffect(() => {
    // 렌더링 시점에 해당 ID를 가진 요소가 있는지 확인
    const scannerId = "reader";
    const element = document.getElementById(scannerId);
    if (!element) return;

    const html5QrcodeScanner = new Html5QrcodeScanner(scannerId, {
      fps: 20, // 인식률 향상을 위해 FPS 상향
      qrbox: (viewfinderWidth, viewfinderHeight) => {
        // 화면 크기에 맞게 스캔 박스 조정 (인식 범위 확대)
        const minEdgeSize = Math.min(viewfinderWidth, viewfinderHeight);
        const qrboxSize = Math.floor(minEdgeSize * 0.7);
        return { width: qrboxSize, height: qrboxSize };
      },
      aspectRatio: 1.0,
      showTorchButtonIfSupported: true,
      rememberLastUsedCamera: true
    });

    const onScanSuccess = (decodedText) => {
      // isInitialized가 true일 때만 부모 콜백 호출 (컴포넌트가 마운트된 상태 확인)
      if (isInitialized.current) {
        html5QrcodeScanner.pause(true); 
        onScan(decodedText);
      }
    };

    const onScanFailure = (error) => {
      // 스캔 실패(인식 중) 로그는 무시
    };

    html5QrcodeScanner.render(onScanSuccess, onScanFailure);
    isInitialized.current = true; // 중요: 초기화 완료 플래그 설정

    return () => {
      // 정리 시점에 해당 요소가 남아있는지 확인 후 clear 호출
      if (document.getElementById(scannerId)) {
        html5QrcodeScanner.clear().catch(error => {
          console.error("Scanner clear failed:", error);
        });
      }
    };
  }, []); // 의존성 배열을 비워 한 번만 실행되도록 설정

  return (
    <div className="scanner-wrapper">
      <div id="reader" ref={scannerRef}></div>
    </div>
  );
};

export default QRScanner;
