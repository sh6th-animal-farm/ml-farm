// 틱 주기 토글
const periodBtns = document.querySelectorAll('.period-btn');

periodBtns.forEach(btn => {
    btn.addEventListener('click', () => {
        // 기존 active 제거
        periodBtns.forEach(b => b.classList.remove('active'));
        // 클릭한 버튼에 active 추가
        btn.classList.add('active');
    });
});

const orderBtns = document.querySelectorAll('.order-btn');

orderBtns.forEach(btn => {
    btn.addEventListener('click', () => {
        // 기존 active 제거
        orderBtns.forEach(b => b.classList.remove('active'));
        // 클릭한 버튼에 active 추가
        btn.classList.add('active');
    });
});

const tradeBtns = document.querySelectorAll('.trade-btn');

tradeBtns.forEach(btn => {
    btn.addEventListener('click', () => {
        // 기존 active 제거
        tradeBtns.forEach(b => b.classList.remove('active'));
        // 클릭한 버튼에 active 추가
        btn.classList.add('active');
    });
});