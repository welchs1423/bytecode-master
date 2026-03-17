const express = require('express');
const app = express();
const port = 3000;

// 에이전트가 보낼 JSON 데이터를 해석하기 위한 설정
app.use(express.json());

// 에이전트가 데이터를 쏘는 엔드포인트
app.post('/collect', (req, res) => {
    const data = req.body;
    const now = new Date().toLocaleTimeString();

    // 1. 단순 출력
    console.log(`📩 [${now}] 수집된 데이터:`, data);

    // 2. [가장 간단한 분석] 2ms가 넘는 쿼리는 '주의' 표시하기 (H2는 빨라서 2ms로 잡았습니다)
    if (data.log.includes('ms')) {
        const time = parseFloat(data.log.split('시간: ')[1]);
        if (time > 2.0) {
            console.log(`⚠️  [SLOW QUERY DETECTED] 성능 저하 위험! (${time}ms)`);
        }
    }

    res.status(200).send('OK');
});

app.listen(port, () => {
    console.log(`✅ APM 수집 서버가 http://localhost:${port} 에서 실행 중입니다.`);
});