const WebSocketManager = (function() {
    let stompClient = null;
    let isConnected = false;
    let subscriptions = {}; // 구독 객체들을 저장할 보관함

    return {
        // 1. 서버 연결
        connect: function(url, onConnectCallback) {
            // 이미 연결 되어 있으면 종료 (중복 방지)
            if (isConnected) {
                if (onConnectCallback) onConnectCallback();
                return;
            }

            const socket = new SockJS(url);
            stompClient = Stomp.over(socket);

            // stompClient.debug = null;

            stompClient.connect({}, function(frame) {
                isConnected = true;
                console.log('WebSocket Connected: ' + frame);
                if (onConnectCallback) onConnectCallback();
            }, function(error) {
                console.error('STOMP Error: ' + error);
                isConnected = false;
            });
        },

        // 2. 구독 신청 (id를 지정하면 나중에 해제하기 편함)
        subscribe: function(id, topic, callback) {
            if (!stompClient || !isConnected) {
                console.warn('연결이 활성화되지 않았습니다.');
                return;
            }

            // 이미 해당 ID로 구독 중이면 기존 구독 해제 (중복 방지)
            this.unsubscribe(id);

            const subscription = stompClient.subscribe(topic, function(response) {
                const data = JSON.parse(response.body);
                callback(data);
            });

            // 보관함에 저장
            subscriptions[id] = subscription;
            console.log(`Subscribed to ${topic} (id: ${id})`);
        },

        // 3. 특정 채널 구독 해제
        unsubscribe: function(id) {
            if (subscriptions[id]) {
                subscriptions[id].unsubscribe();
                delete subscriptions[id];
                console.log(`Unsubscribed: ${id}`);
            }
        },

        // 4. 모든 구독 해제 (페이지 이동 시 유용)
        unsubscribeAll: function() {
            Object.keys(subscriptions).forEach(id => {
                this.unsubscribe(id);
            });
        },

        // 5. 연결 여부 확인
        isConnected: function() {
            return isConnected;
        }
    };
})();