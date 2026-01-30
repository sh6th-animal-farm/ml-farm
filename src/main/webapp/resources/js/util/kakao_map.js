var container = document.getElementById("map"); //지도를 담을 영역의 DOM 레퍼런스
var options = {
  //지도를 생성할 때 필요한 기본 옵션
  center: new kakao.maps.LatLng(36.2683, 127.6358), //지도의 중심좌표.
  level: 13, //지도의 레벨(확대, 축소 정도)
};

var map = new kakao.maps.Map(container, options); //지도 생성 및 객체 리턴

// 마커
var content =
  '<div style="width:24px; height:24px; background:#fff; border:3px solid #4A9F2E; border-radius:50%;"></div>';
var customOverlay = new kakao.maps.CustomOverlay({
  position: new kakao.maps.LatLng(farmLat, farmLng),
  content: content,
});
customOverlay.setMap(map);

// 선택한 지역으로 이동하는 함수
function moveToRegion(lat, lng) {
  var moveLatLon = new kakao.maps.LatLng(lat, lng);
  map.setCenter(moveLatLon);
  map.setLevel(7); // 적절한 확대 레벨
}

// 마커 묶어서 숫자로 보여주기
var clusterer = new kakao.maps.MarkerClusterer({
  map: map,
  averageCenter: true,
  minLevel: 8, // 이 레벨 이상 축소 시 합쳐짐
  styles: [
    {
      // 클러스터 디자인
      width: "40px",
      height: "40px",
      background: "rgba(74, 159, 46, 0.8)", // Green 600 투명도 적용
      borderRadius: "20px",
      color: "#fff",
      textAlign: "center",
      lineHeight: "40px",
    },
  ],
});

// 모달 띄우기
kakao.maps.event.addListener(marker, "click", function () {
  map.setLevel(1); // 최대한 확대
  map.panTo(marker.getPosition());

  // 모달
  var modalContent = `
        <div style="padding:16px; background:#fff; border-radius:16px; box-shadow:0 2px 10px rgba(0,0,0,0.1);">
            <h3 style="color:#191919;">${projectName}</h3>
            <p style="color:#4A9F2E; font-weight:700;">예상 수익률: ${expectedReturn}%</p>
            <button onclick="location.href='/project/detail?id=${projectId}'" 
                    style="background:#4A9F2E; color:#fff; border-radius:8px; padding:8px;">상세보기</button>
        </div>`;
  // 커스텀 오버레이 등으로 표시
});
