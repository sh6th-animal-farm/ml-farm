var map;
var clusterer;

function initMap(projectList) {
    kakao.maps.load(function() {
        var container = document.getElementById("map");
        var options = {
            center: new kakao.maps.LatLng(36.2683, 127.6358),
            level: 13,
        };

        map = new kakao.maps.Map(container, options);

        // 클러스터러 설정
        clusterer = new kakao.maps.MarkerClusterer({
            map: map,
            averageCenter: true,
            minLevel: 1,      // 모든 레벨에서 클러스터링 허용
            minClusterSize: 1, // 마커가 1개여도 클러스터 디자인 적용
            styles: [{
                width: "48px", height: "48px",
                background: "rgba(74, 159, 46, 0.9)", // Green 600
                borderRadius: "50%",
                color: "#fff",
                textAlign: "center",
                font:"var(--font-body-03)",
                lineHeight: "48px",
                boxShadow: "0 4px 12px rgba(0,0,0,0.2)",
                cursor: "pointer"
            }]
        });

        // 클러스터 클릭 이벤트 처리
        kakao.maps.event.addListener(clusterer, 'clusterclick', function(cluster) {
            var markers = cluster.getMarkers();
            
            // 뭉쳐있는 마커가 여러 개일 때만 확대 로직 실행
            if (markers.length > 1) {
                var level = map.getLevel();
                map.setLevel(level - 2, {anchor: cluster.getCenter()});
            }
            // 1개일 때는 marker 자체의 click 이벤트가 동작하므로 여기서 처리하지 않아도 됩니다.
        });

        renderMarkers(projectList);
    });
}

function renderMarkers(projectList) {
    if (projectList && projectList.length > 0) {
        var markers = projectList.map(function(project) {
            var position = new kakao.maps.LatLng(project.latitude, project.longitude);
            
            // 1. 투명 마커 생성 (사이즈를 줘서 클릭 영역을 확보합니다)
            var marker = new kakao.maps.Marker({
                position: position,
                opacity: 0,
                // 클릭이 가능하도록 기본 이미지를 아주 작게라도 설정하거나 빈 이미지를 줍니다
                image: new kakao.maps.MarkerImage(
                    'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/markerStar.png',
                    new kakao.maps.Size(48, 48), // 클러스터 원 크기와 비슷하게 잡아야 클릭이 잘 됩니다
                    { offset: new kakao.maps.Point(24, 24) }
                )
            });

            // 2. 마커에 직접 클릭 이벤트 등록 (이게 가장 확실함)
            kakao.maps.event.addListener(marker, 'click', function() {
                displayProjectModal(project, position);
            });

            return marker;
        });

        clusterer.addMarkers(markers);
    }
}

// 모달 표시 함수
var activeOverlay = null;
function displayProjectModal(project, position) {
    if (activeOverlay) activeOverlay.setMap(null);

    map.panTo(position);

    var content = `
        <div style="padding:20px; background:#fff; border-radius:16px; box-shadow:0 4px 20px rgba(0,0,0,0.15); border:1px solid #E1E1E1; min-width:220px;">
            <div style="display:flex; justify-content:space-between; align-items:start; margin-bottom:12px;">
                <h4 style="margin:0; color:#191919; font-size:16px; font-weight:700;">${project.projectName}</h4>
                <span style="cursor:pointer; color:#707070; font-size:20px;" onclick="closeOverlay()">×</span>
            </div>
            <p style="margin:0 0 16px 0; color:#4A9F2E; font-weight:700; font-size:14px;">예상 수익률: ${project.expectedReturn}%</p>
            <button onclick="location.href='/project/${project.projectId}'" 
                    style="width:100%; background:#4A9F2E; color:#fff; border:none; border-radius:8px; padding:10px; font-weight:600; cursor:pointer;">
                상세보기
            </button>
        </div>`;

    activeOverlay = new kakao.maps.CustomOverlay({
        content: content,
        position: position,
        yAnchor: 1.2
    });
    activeOverlay.setMap(map);
}

function closeOverlay() {
    if (activeOverlay) activeOverlay.setMap(null);
}

function moveToRegion(lat, lng, level) {
    if (!map) {
        console.error("지도가 초기화되지 않았습니다.");
        return;
    }
    
    var moveLatLon = new kakao.maps.LatLng(lat, lng);
    
    // 레벨을 먼저 변경 (애니메이션 없이 즉시 변경하여 위치 오차 방지)
    if (level) {
        map.setLevel(level, {animate: false});
    }
    
    // 중심점을 부드럽게 이동
    map.panTo(moveLatLon);
    
    // 현재 이동하는 좌표 확인
    console.log("이동 좌표:", lat, lng, "레벨:", level);
}