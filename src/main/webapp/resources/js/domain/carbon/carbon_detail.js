// carbon_detail.js
document.addEventListener("DOMContentLoaded", function() {
    const cpId = document.getElementById("targetCpId").value;
    if(cpId) {
        loadCarbonDetail(cpId);
    }
});

async function loadCarbonDetail(cpId) {
    try {
        // 1. localStorage에서 토큰을 가져옵니다.
        const token = localStorage.getItem("accessToken");

        // 2. fetch 요청 시 헤더에 신분증(JWT)을 첨부합니다.
        const response = await fetch(ctx + "/api/carbon/" + cpId, {
            method: "GET",
            headers: {
                "Authorization": token ? "Bearer " + token : "",
                "Content-Type": "application/json"
            }
        });

        if (!response.ok) {
            // 401(만료)이나 403(권한없음) 에러가 나면 팀장님이 만든 강제 로그아웃 실행
            if (response.status === 401 || response.status === 403) {
                console.warn("세션 만료 또는 권한 없음. 강제 로그아웃을 실행합니다.");
                if (typeof AuthManager !== 'undefined') {
                    AuthManager.forceLogout();
                }
                return;
            }
            throw new Error("서버 응답 에러: " + response.status);
        }

        const result = await response.json(); //규격 { message, payload } 처리

        // 1. 결과 데이터가 정상적으로 존재할 때만 렌더링 함수 호출
        if (result && result.payload) {
            renderPage(result.payload); 
        } else {
            console.error("데이터 로드 실패: payload가 없습니다.");
        }
    } catch (error) {
        console.error("데이터 로드 중 오류 발생:", error);
    }
}

function renderPage(data) {
    // 데이터 구조 분해 할당 (데이터가 없을 경우를 대비해 기본값 설정)
    if (!data || !data.carbonInfo) {
        console.error("렌더링할 데이터가 유효하지 않습니다.");
        return;
    }

    const info = data.carbonInfo;
    const benefit = data.userBenefit;

    // 상단 태그 및 제목 바인딩
    document.getElementById("tagText").innerText = info.cpType + " 프로젝트 | " + info.vintageYear + " 빈티지";
    document.getElementById("titleText").innerText = info.cpTitle;
    
    // 위치 조립 (컬럼 사이 공백 추가)
    const locationStr = "위치: " + (data.addressSido || "") + " " + (data.addressSigungu || "") + " " + 
                        (data.addressStreet || "") + " " + (data.addressDetails || "") + " " + (data.farmName || "") + " 일대";
    document.getElementById("locationText").innerText = locationStr;

    // 주요 정보 카드 매핑
    document.getElementById("valCertificate").innerText = info.productCertificate; //인증기관
    document.getElementById("valType").innerText = info.cpType; //상품유형
    document.getElementById("valInitAmount").innerText = Number(info.initAmount || 0).toLocaleString() + " tCO2e";//초기수량(발급수량)
    document.getElementById("valCpAmount").innerText = Number(info.cpAmount || 0).toLocaleString() + " tCO2e";//남은 수량(재고수량)
    document.getElementById("valDetail").innerText = info.cpDetail;//설명

    // 5. 사이드바 가격 정보
    document.getElementById("sideOriginalPrice").innerText = (info.cpPrice || 0).toLocaleString() + " KRW";
    document.getElementById("sideDiscount").innerText = (benefit.discountRate || 0) + "% 할인";
    document.getElementById("sideCurrentPrice").innerText = (benefit.currentPrice || 0).toLocaleString() + "원";
}
// ===== 주문 모달 전역 상태 =====
let __co = {
  cpId: null,
  maxQty: null,   // min(userMaxLimit, remainAmount)
  unitPrice: null,
  quote: null
};

// 숫자 포맷(원)
function fmtKRW(n) {
  const v = Number(n ?? 0);
  return v.toLocaleString() + "원";
}
// 수량 포맷(tCO2e)
function fmtQty(n) {
  const v = Number(n ?? 0);
  return v.toLocaleString();
}

// 모달 열기
async function openOrderModal() {
  const modal = document.getElementById("carbonOrderModal");
  if (!modal) return;

  const cpId = document.getElementById("targetCpId")?.value;
  __co.cpId = cpId ? Number(cpId) : null;

  // UI 초기화
  document.getElementById("coQtyInput").value = 1;
  document.getElementById("coAgree").checked = false;
  document.getElementById("coQtyHint").innerText = "";
  setSubmitEnabled(false);

  modal.classList.add("is-open");
  modal.setAttribute("aria-hidden", "false");

  // 기본 값 1개 기준으로 quote 조회해서 모달 값 채우기
  await refreshQuote();
}

// 모달 닫기
function closeOrderModal() {
  const modal = document.getElementById("carbonOrderModal");
  if (!modal) return;
  modal.classList.remove("is-open");
  modal.setAttribute("aria-hidden", "true");
}

// ESC로 닫기
document.addEventListener("keydown", (e) => {
  if (e.key === "Escape") closeOrderModal();
});

// 수량/동의 변경 시 재계산
document.addEventListener("DOMContentLoaded", () => {
  const qty = document.getElementById("coQtyInput");
  const agree = document.getElementById("coAgree");

  if (qty) {
    qty.addEventListener("input", async () => {
      await refreshQuote();
    });
  }
  if (agree) {
    agree.addEventListener("change", () => {
      validateAndToggle();
    });
  }
});

// quote API 호출 + 화면 바인딩
async function refreshQuote() {
  const cpId = __co.cpId;
  if (!cpId) return;

  const qtyEl = document.getElementById("coQtyInput");
  const amount = Number(qtyEl?.value ?? 1);

  // amount 방어
  const safeAmount = (!amount || amount < 1) ? 1 : Math.floor(amount);
  if (qtyEl && qtyEl.value != safeAmount) qtyEl.value = safeAmount;

  try {
    const token = localStorage.getItem("accessToken");
    const url = `${ctx}/api/carbon/orders/quote?cpId=${cpId}&amount=${safeAmount}`;

    const res = await fetch(url, {
      method: "GET",
      headers: {
        "Authorization": token ? "Bearer " + token : "",
        "Content-Type": "application/json"
      }
    });

    if (!res.ok) {
      if (res.status === 401 || res.status === 403) {
        if (typeof AuthManager !== 'undefined') AuthManager.forceLogout();
        return;
      }
      throw new Error("quote 응답 오류: " + res.status);
    }

    const json = await res.json(); // {message, payload}
    const p = json?.payload;
    if (!p) throw new Error("quote payload 없음");

    // 최대 구매 가능 수량 = min(userMaxLimit, remainAmount)
    const userMax = Number(p.userMaxLimit ?? 0);
    const remain = Number(p.remainAmount ?? 0);
    const maxQty = Math.max(0, Math.min(userMax || remain, remain || userMax)); // 둘 중 하나 null이어도 동작

    __co.maxQty = maxQty;
    __co.unitPrice = Number(p.unitPrice ?? 0);
    __co.quote = p;
	__co.quote.orderAmount = safeAmount;

    // 바인딩
    document.getElementById("coProductName").innerText = p.cpTitle ?? "-";
    document.getElementById("coUnitPrice").innerText = Number(p.unitPrice ?? 0).toLocaleString();
    document.getElementById("coMaxQty").innerText = fmtQty(maxQty);

    document.getElementById("coSupply").innerText = fmtKRW(p.supplyAmount);
    document.getElementById("coVat").innerText = fmtKRW(p.vatAmount);
    document.getElementById("coTotal").innerText = fmtKRW(p.totalAmount);

    validateAndToggle();
  } catch (e) {
    console.error(e);
    document.getElementById("coQtyHint").innerText = "주문 견적을 불러오지 못했습니다.";
    setSubmitEnabled(false);
  }
}

function validateAndToggle() {
  const qty = Number(document.getElementById("coQtyInput")?.value ?? 1);
  const agree = !!document.getElementById("coAgree")?.checked;
  const maxQty = Number(__co.maxQty ?? 0);

  const hint = document.getElementById("coQtyHint");

  // 수량 검증
  if (!qty || qty < 1) {
    hint.innerText = "수량은 1 이상이어야 합니다.";
    setSubmitEnabled(false);
    return;
  }
  if (maxQty > 0 && qty > maxQty) {
    hint.innerText = `최대 구매 가능 수량(${fmtQty(maxQty)} tCO2e)을 초과했습니다.`;
    setSubmitEnabled(false);
    return;
  }

  hint.innerText = "";
  setSubmitEnabled(agree);
}

function setSubmitEnabled(enabled) {
  const btn = document.getElementById("coSubmitBtn");
  if (!btn) return;
  btn.disabled = !enabled;
}

async function submitCarbonOrder() {
  const q = __co.quote;
  if (!q) {
    alert("견적을 먼저 불러와야 합니다.");
    return;
  }

  // 약관 동의 체크 (버튼 enabled 조건이긴 하지만 한번 더 안전)
  const agree = !!document.getElementById("coAgree")?.checked;
  if (!agree) {
    alert("약관에 동의해야 결제가 가능합니다.");
    return;
  }

  // PortOne SDK 로드 확인
  if (typeof IMP === "undefined") {
    alert("결제 모듈(PortOne)을 불러오지 못했습니다. JSP에 iamport.js를 추가했는지 확인하세요.");
    return;
  }

  // 필수 키 확인
  const impCode = window.PORTONE_IMP_CODE;
  const channelKey = window.PORTONE_CHANNEL_KEY;

  if (!impCode || !channelKey) {
    alert("PortOne 설정값(impCode/channelKey)이 없습니다. JSP에서 전역으로 내려주세요.");
    return;
  }

  const payAmount = Number(q.totalAmount ?? 0);
  if (!payAmount || payAmount < 1) {
    alert("결제 금액이 올바르지 않습니다.");
    return;
  }

  const merchantUid = `mlf_carbon_${__co.cpId}_${Date.now()}_${Math.floor(Math.random()*1000)}`;

  IMP.init(impCode);

  IMP.request_pay(
    {
      channelKey: channelKey,
      pay_method: "card",
      merchant_uid: merchantUid,
      name: q.cpTitle ?? "탄소 배출권 구매",
      amount: payAmount,

    },
    async function (rsp) {
      if (rsp.success) {
        try {
          // 너가 만들 API 예: /api/carbon/orders/complete
          const token = localStorage.getItem("accessToken");
          const verifyRes = await fetch(`${ctx}/api/carbon/orders/complete`, {
            method: "POST",
            headers: {
              "Authorization": token ? "Bearer " + token : "",
              "Content-Type": "application/json"
            },
            body: JSON.stringify({
              impUid: rsp.imp_uid,
              merchantUid: rsp.merchant_uid,
              cpId: __co.cpId,
              amount: Number(q.orderAmount ?? document.getElementById("coQtyInput")?.value ?? 1)
            })
          });

          if (!verifyRes.ok) {
            const err = await verifyRes.json().catch(() => ({}));
            alert("결제는 완료됐지만 서버 검증/주문처리에 실패했습니다: " + (err.message || verifyRes.status));
            return;
          }

          alert("결제 완료!");
          closeOrderModal();
          // 필요하면 상세/잔여수량 새로고침
          // loadCarbonDetail(__co.cpId);
        } catch (e) {
          console.error(e);
          alert("결제 완료 후 처리 중 오류가 발생했습니다.");
        }
      } else {
        alert("결제 실패: " + (rsp.error_msg || "알 수 없는 오류"));
      }
    }
  );
}

