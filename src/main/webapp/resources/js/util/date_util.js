const DateUtil = {
    /**
     * datetime-local 값을 OffsetDateTime 형식으로 변환
     * @param {string} localDateTime (ex: "2024-12-09T11:35")
     * @returns {string} (ex: "2024-12-09T11:35:00+09:00")
     */
    toOffsetDateTime: function(localDateTime) {
        if (!localDateTime) return null;
        // 날짜만 있는 경우(2026-02-07) 뒤에 시간과 타임존을 강제로 붙여줌
        if (localDateTime.length === 10) {
            return `${localDateTime}T00:00:00+09:00`;
        }
        // 초(00)와 한국 시차(+09:00)를 결합
        return `${localDateTime}:00+09:00`;
    },
    /**
     * datetime-local 값을 OffsetDateTime 형식으로 변환
     * @param {string} OffsetDateTime (ex: "2024-12-09T11:35:00+09:002024-12-09T11:35")
     * @returns {string} (ex: "2024-12-09T11:35")
     */
    toLocalDateTime: function (dateStr) {
    if (!dateStr) return '';
    
    // dateStr이 '2024-12-09T11:35:00+09:00' 형태로 올 때
    try {
        // substring(0, 16)을 하면 '2024-12-09T11:35' 까지만 남습니다.
        return dateStr.substring(0, 16);
    } catch (e) {
        console.error("날짜 문자열 파싱 오류:", e);
        return '';
    }
}
};

export {DateUtil}