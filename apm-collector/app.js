const express = require('express');
const app = express();
const port = 3000;

// 에이전트가 보낼 JSON 데이터를 해석하기 위한 설정
app.use(express.json());

// 에이전트가 데이터를 쏘는 엔드포인트
app.post('/collect', (req, res) => {
    const data = req.body;

    // 현재 시간과 함께 받은 데이터를 출력
    const now = new Date().toLocaleTimeString();
    console.log(`📩 [${now}] 수집된 데이터:`, data);

    // 에이전트에게 잘 받았다고 응답 (200 OK)
    res.status(200).send('OK');
});

app.listen(port, () => {
    console.log(`✅ APM 수집 서버가 http://localhost:${port} 에서 실행 중입니다.`);
});