<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>회원가입</title>

  <!-- Pretendard -->
  <link rel="stylesheet" as="style" crossorigin href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.8/dist/web/static/pretendard.css">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

  <style>
    :root{
      /* Primary Color (Green) */
      --green-950:#163B16;
      --green-800:#2E7127;
      --green-600:#4A9F2E; /* Main Brand Color */
      --green-500:#6CC32D;
      --green-0:#D3F5BB;

      /* Greyscale */
      --gray-900:#191919;
      --gray-600:#404040;
      --gray-50:#F8FAFB;
      --gray-0:#FCFCFC;
      --gray-500:#565656;
      --gray-400:#9CA3AF;
      --gray-300:#C6C6C6;
      --gray-200:#E1E1E1;
      --gray-100:#F2F2F2;

      --error:#E53935;
      --error-red:#E53935;

      /* Radius */
      --radius-s:4px;
      --radius-m:8px;
      --radius-l:12px;
      --radius-xl:16px;
      --radius-full:999px;

      --container:1200px;
      --header-height:72px;
    }

    *{margin:0;padding:0;box-sizing:border-box;font-family:'Pretendard',sans-serif;letter-spacing:-0.02em;-webkit-font-smoothing:antialiased;}
    body{background-color:var(--gray-50);color:var(--gray-600);line-height:1.4;display:flex;flex-direction:column;min-height:100vh;}

    main{flex:1;display:flex;flex-direction:column;align-items:center; padding-bottom:40px; padding-top:40px;}
    .auth-wrapper{width:100%;max-width:520px;}

    .step-dots{display:flex;justify-content:center;gap:12px;margin-bottom:32px;}
    .dot{width:8px;height:8px;background:var(--gray-300);border-radius:50%;transition:all 0.3s ease;}
    .dot.active{background:var(--green-600);width:24px;border-radius:4px;}

    .auth-card{background:#fff;padding:40px;border-radius:var(--radius-xl);box-shadow:0 4px 24px rgba(0,0,0,0.06);border:1px solid var(--gray-100);}
    .auth-title{font-size:24px;color:var(--gray-900);font-weight:700;margin-bottom:12px;text-align:center;}
    .auth-desc{font-size:14px;color:var(--gray-500);text-align:center;margin-bottom:32px;line-height:1.5;}

    .form-group{margin-bottom:20px;position:relative;}
    .form-label{display:block;font-size:14px;font-weight:700;color:var(--gray-900);margin-bottom:8px;}
    .input-row{display:flex;gap:8px;position:relative;}

    .choice-icon-box{font-size:24px;width:56px;height:56px;background:var(--gray-50);border-radius:50%;display:flex;align-items:center;justify-content:center;}

    input,select{width:100%;height:50px;padding:0 16px;border:1px solid var(--gray-200);border-radius:var(--radius-m);font-size:15px;background:#fff;transition:0.2s;color:var(--gray-900);}
    input:focus,select:focus{outline:none;border-color:var(--green-600);}
    input.input-error{border-color:var(--error) !important;}
    input[readonly]{background:var(--gray-100) !important;color:var(--gray-500);cursor:not-allowed;border:none !important;}

    .btn-main{width:100%;height:54px;background:var(--green-600);color:#fff;border:none;border-radius:var(--radius-m);font-size:16px;font-weight:700;cursor:pointer;margin-top:20px;transition:background 0.2s ease;}
    .btn-main:hover{background:var(--green-800);}
    .btn-sub{white-space:nowrap;padding:0 20px;height:50px;background:var(--gray-900);color:#fff;border:none;border-radius:var(--radius-m);font-weight:600;cursor:pointer;font-size:14px;}

    /* ✅ 이메일 타이머가 input 안쪽(오른쪽)에 안정적으로 보이도록 전용 wrapper 사용 */
    .email-input-wrap{position:relative;flex:1;}
    .email-input-wrap input{padding-right:74px;} /* 타이머 자리 확보 */
    .timer-text{
      position:absolute;
      right:16px;
      top:50%;
      transform:translateY(-50%);
      font-size:14px;
      color:var(--error);
      font-weight:600;
      z-index:5;
      pointer-events:none;
      background:transparent;
    }

    .terms-container{border:1px solid var(--gray-200);border-radius:var(--radius-m);padding:20px;}
    .all-agree{display:flex;align-items:center;gap:10px;margin-bottom:0;padding-bottom:16px;border-bottom:1px solid var(--gray-100);cursor:pointer;}
    .term-row:first-of-type{margin-top:20px;}
    .term-row{display:flex;align-items:center;justify-content:space-between;margin-top:20px;}

    .custom-chk{appearance:none;-webkit-appearance:none;width:16px;height:16px;border:1px solid var(--gray-300);border-radius:var(--radius-s);background:#fff;cursor:pointer;position:relative;display:flex;align-items:center;justify-content:center;margin:0;padding:0;accent-color:var(--green-500);}
    .custom-chk:checked{background:var(--green-500);border-color:var(--green-500);}
    .custom-chk:checked::after{content:'✓';color:#fff;font-size:12px;font-weight:bold;}
    .checkbox-label{display:flex;align-items:center;gap:8px;font-size:14px;color:#374151;cursor:pointer;}

    .footer-info{margin-top:32px;text-align:center;font-size:14px;color:var(--gray-500);}
    .footer-info a{color:var(--green-600);text-decoration:none;font-weight:700;cursor:pointer;}

    .hidden{display:none !important;}

    .status{min-height:18px;margin-top:6px;font-size:13px;font-weight:500;}
    .status.ok{color:var(--green-600);}
    .status.bad{color:var(--error);}
  </style>
</head>

<body>
  <main>
    <div class="auth-wrapper">

      <!-- 가입 유형 선택 -->
      <div id="choice-page" class="auth-card">
        <h2 class="auth-title">회원가입</h2>
        <p class="auth-desc">가입하실 회원 유형을 선택해주세요</p>

        <div id="choicePersonal" style="padding:24px;border:1px solid var(--gray-200);border-radius:var(--radius-l);cursor:pointer;display:flex;align-items:center;gap:20px;margin-bottom:16px;background:#fff;">
          <div class="choice-icon-box"><i class="fa-solid fa-user" style="color:var(--gray-600);"></i></div>
          <div>
            <h4 style="font-size:17px;font-weight:700;color:var(--gray-900);margin:0 0 4px 0;">개인 회원</h4>
            <p style="font-size:14px;color:var(--gray-500);margin:0;">일반 투자 및 서비스를 이용하는 개인</p>
          </div>
        </div>

        <div id="choiceEnterprise" style="padding:24px;border:1px solid var(--gray-200);border-radius:var(--radius-l);cursor:pointer;display:flex;align-items:center;gap:20px;background:#fff;">
          <div class="choice-icon-box"><i class="fa-solid fa-building" style="color:var(--gray-600);"></i></div>
          <div>
            <h4 style="font-size:17px;font-weight:700;color:var(--gray-900);margin:0 0 4px 0;">기업 회원</h4>
            <p style="font-size:14px;color:var(--gray-500);margin:0;">법인 및 사업자 명의 투자 서비스 이용</p>
          </div>
        </div>

        <div class="footer-info" style="text-align:center;margin-top:24px;">
          이미 회원이신가요? <a href="<%=request.getContextPath()%>/auth/login">로그인</a>
        </div>
      </div>

      <!-- 회원가입 플로우 -->
      <div id="signup-flow" class="hidden">
        <div class="step-dots" id="step-indicators"></div>

        <div class="auth-card">

          <!-- STEP 1: 약관 -->
          <div id="step-1" class="signup-step">
            <h2 class="auth-title">약관 동의</h2>
            <p class="auth-desc">원활한 서비스 이용을 위해 약관에 동의해주세요</p>

            <div class="terms-container">
              <label class="all-agree" style="display:flex;align-items:center;gap:8px;padding-bottom:16px;border-bottom:1px solid var(--gray-200);margin-bottom:16px;">
                <input type="checkbox" id="agree-all-chk" class="custom-chk" onclick="handleAllAgree(this)">
                <span style="font-weight:700;color:var(--gray-900);">약관 전체 동의</span>
              </label>

              <div class="term-row">
                <label class="checkbox-label">
                  <input type="checkbox" class="custom-chk sub-chk req-chk" onclick="updateMasterCheckbox()">
                  <span>[필수] 서비스 이용약관 동의</span>
                </label>
              </div>
              <div class="term-row">
                <label class="checkbox-label">
                  <input type="checkbox" class="custom-chk sub-chk req-chk" onclick="updateMasterCheckbox()">
                  <span>[필수] 개인정보 수집 및 이용 동의</span>
                </label>
              </div>
              <div class="term-row">
                <label class="checkbox-label">
                  <input type="checkbox" class="custom-chk sub-chk" onclick="updateMasterCheckbox()">
                  <span>[선택] 푸시 알람 수신 동의</span>
                </label>
              </div>
            </div>

            <div id="termsStatus" class="status"></div>
            <button type="button" class="btn-main" onclick="goToNext()">다음으로</button>
          </div>

          <!-- STEP 2: 사업자 인증 (ENTERPRISE만) -->
          <div id="step-2" class="signup-step hidden">
            <h2 class="auth-title">기업 인증</h2>
            <p class="auth-desc">사업자 등록번호를 확인합니다</p>

            <div class="form-group">
              <label class="form-label">사업자 등록번호</label>
              <div class="input-row">
                <input type="text" id="bNo" placeholder="숫자만 입력 (10자리)" maxlength="10" inputmode="numeric" oninput="onBnoInput()">
                <button class="btn-sub" id="bNoBtn" type="button" onclick="verifyBno()">인증하기</button>
              </div>
              <div id="bNoStatus" class="status"></div>
            </div>

            <button type="button" class="btn-main" onclick="goToNext()">다음으로</button>
          </div>

          <!-- STEP 3: 유의사항 (ENTERPRISE만) -->
          <div id="step-3" class="signup-step hidden">
            <h2 class="auth-title">유의사항 확인</h2>
            <p class="auth-desc">법인회원 거래 유의사항을 확인해주세요</p>

            <div style="background:var(--gray-50);border:1px solid var(--gray-200);padding:20px;border-radius:var(--radius-m);font-size:13px;height:150px;overflow-y:auto;margin-bottom:24px;color:var(--gray-600);line-height:1.6;">
              <h4 style="margin:0 0 12px 0;">[법인회원 고객확인 거래 유의사항]</h4>
              1. 자금세탁방지 의무 준수<br>
              2. 정보 제공의 정확성 보장<br>
              3. 실소유주 정보 제공 의무
            </div>

            <div class="form-group">
              <label class="checkbox-label">
                <input type="checkbox" id="caution-agree-chk" class="custom-chk">
                <span style="font-weight:700;color:var(--gray-900);">위 내용을 모두 확인하였으며 동의합니다.</span>
              </label>
            </div>

            <div id="cautionStatus" class="status"></div>
            <button type="button" class="btn-main" onclick="goToNext()">다음으로</button>
          </div>

          <!-- STEP 4: 본인 인증 (mock) -->
          <div id="step-4" class="signup-step hidden">
            <h2 class="auth-title">본인 확인</h2>
            <p class="auth-desc">휴대폰 본인인증을 진행합니다</p>

            <div class="form-group">
              <label class="form-label">휴대폰 번호</label>
              <input type="text" id="phone" placeholder="01012345678" inputmode="numeric">
              <div id="phoneStatus" class="status"></div>
            </div>

            <div style="margin:24px 0 0;">
              <button class="btn-sub" type="button" style="width:100%;border-color:var(--green-600);color:var(--green-600);" onclick="mockPhoneVerify()">
                <i class="fa-solid fa-mobile-screen"></i> 본인인증 완료하기 (PASS 연동)
              </button>
              <p style="margin-top:10px;font-size:12px;color:var(--gray-500);text-align:center;">* 현재 테스트 모드로 즉시 인증됩니다.</p>
            </div>

            <button type="button" class="btn-main" onclick="goToNext()">다음으로</button>
          </div>

          <!-- STEP 5: 이메일 인증 -->
          <div id="step-5" class="signup-step hidden">
            <h2 class="auth-title">이메일 인증</h2>
            <p class="auth-desc">로그인 아이디로 사용할 이메일을 인증하세요</p>

            <div class="form-group">
              <label class="form-label">이메일 주소 (ID)</label>
              <div class="input-row">
                <!-- ✅ timer가 input에 붙도록 input만 감싸는 wrapper 추가 -->
                <div class="email-input-wrap">
                  <input type="email" id="email" placeholder="example@farm.com"
                    oninput="this.classList.remove('input-error'); clearStatus('emailSendStatus'); clearStatus('emailVerifyStatus'); emailVerified=false;">
                  <span id="emailTimer" class="timer-text hidden">05:00</span>
                </div>
                <button class="btn-sub" type="button" id="emailSendBtn" onclick="sendEmailCode()">인증요청</button>
              </div>
              <div id="emailSendStatus" class="status"></div>
            </div>

            <div class="form-group">
              <label class="form-label">인증번호</label>
              <div class="input-row">
                <input type="text" id="emailCode" placeholder="인증번호 6자리" inputmode="numeric"
                  oninput="this.classList.remove('input-error'); clearStatus('emailVerifyStatus');">
                <button class="btn-sub" type="button" onclick="confirmEmailCode()">확인</button>
              </div>
              <div id="emailVerifyStatus" class="status"></div>
            </div>

            <button type="button" class="btn-main" onclick="goToNext()">다음으로</button>
          </div>

          <!-- STEP 6: 회원정보 입력 -->
          <div id="step-6" class="signup-step hidden">
            <h2 class="auth-title">회원정보 입력</h2>
            <p class="auth-desc">가입 정보를 확인하고 비밀번호를 설정하세요</p>

            <div class="form-group">
              <label class="form-label">이름</label>
              <input type="text" id="userName" placeholder="이름 입력">
            </div>

            <div class="form-group">
              <label class="form-label">이메일 (ID)</label>
              <input type="text" id="finalEmail" readonly style="background:var(--gray-50);">
            </div>

            <div class="form-group hidden" id="finalBnoRow">
              <label class="form-label">사업자 등록번호</label>
              <input type="text" id="finalBno" readonly style="background:var(--gray-50);">
            </div>

            <div class="form-group">
              <label class="form-label">비밀번호</label>
              <input type="password" id="password" placeholder="비밀번호" oninput="checkPw()">
            </div>

            <div class="form-group">
              <label class="form-label">비밀번호 확인</label>
              <input type="password" id="password2" placeholder="비밀번호 재입력" oninput="checkPw()">
              <div id="pwStatus" class="status"></div>
            </div>

            <div id="signupStatus" class="status"></div>
            <button type="button" class="btn-main" onclick="submitSignUp()">가입 완료하기</button>
          </div>

          <!-- STEP 7: 완료 -->
          <div id="step-7" class="signup-step hidden" style="text-align:center;padding:40px 0;">
            <span style="font-size:64px;display:block;margin-bottom:24px;color:var(--green-600);"><i class="fa-solid fa-leaf"></i></span>
            <h2 class="auth-title">가입을 축하드립니다!</h2>
            <p class="auth-desc">로그인 페이지로 이동하여 서비스를 이용해보세요.</p>
            <button class="btn-main" type="button" onclick="location.href='<%=request.getContextPath()%>/auth/login'">로그인하러 가기</button>
          </div>

        </div>
      </div>

    </div>
  </main>

  <script>

    let signUpType = null; // 'PERSONAL' | 'ENTERPRISE'
    let step = 1;
    let emailVerified = false;
    let phoneVerified = false;
    let bnoVerified = false;

    // ✅ 이메일 타이머 상태
    let emailTimerInterval = null;
    let emailRemainSec = 0;
    const EMAIL_EXPIRE_SEC = 300;

    document.addEventListener('DOMContentLoaded', () => {
      const p = document.getElementById('choicePersonal');
      const e = document.getElementById('choiceEnterprise');
      if (p) p.addEventListener('click', () => startSignupFlow('PERSONAL'));
      if (e) e.addEventListener('click', () => startSignupFlow('ENTERPRISE'));
    });

    function startSignupFlow(type){
      signUpType = type;
      step = 1;
      resetForm();
      initDots();

      document.getElementById('choice-page').classList.add('hidden');
      document.getElementById('signup-flow').classList.remove('hidden');
      renderUI();
    }

    function resetForm(){
      emailVerified = false;
      phoneVerified = false;
      bnoVerified = false;
      stopEmailTimer();

      ['bNo','phone','email','emailCode','userName','password','password2'].forEach(id => {
        const el = document.getElementById(id);
        if (el) el.value = '';
      });

      document.querySelectorAll('.status').forEach(el => { el.textContent = ''; el.className='status'; });
      document.querySelectorAll('input[type=checkbox]').forEach(c => c.checked = false);

      const btn = document.getElementById('emailSendBtn');
      if (btn) btn.textContent = '인증요청';
    }

    function renderUI(){
      document.querySelectorAll('.signup-step').forEach(el => el.classList.add('hidden'));
      const cur = document.getElementById('step-' + step);
      if (cur) cur.classList.remove('hidden');

      document.querySelectorAll('.dot').forEach(d => d.classList.remove('active'));
      let dotIdx = step;

      if (signUpType === 'PERSONAL') {
        const map = {1:1, 4:2, 5:3, 6:4, 7:5};
        dotIdx = map[step] || 1;
      }
      const dot = document.getElementById('dot-' + dotIdx);
      if (dot) dot.classList.add('active');

      if (step === 6) {
        const emailEl = document.getElementById('email');
        if (emailEl) document.getElementById('finalEmail').value = emailEl.value || '';

        const row = document.getElementById('finalBnoRow');
        if (signUpType === 'ENTERPRISE') {
          row.classList.remove('hidden');
          document.getElementById('finalBno').value = document.getElementById('bNo').value || '';
        } else {
          row.classList.add('hidden');
        }
      }

      window.scrollTo(0,0);
    }

    function goToNext(){
      if (!validateStep()) return;

      if (signUpType === 'PERSONAL') {
        if (step === 1) step = 4;
        else if (step < 7) step++;
      } else {
        if (step < 7) step++;
      }
      renderUI();
    }

    function validateStep(){
      if (step === 1) {
        const req = Array.from(document.querySelectorAll('.req-chk'));
        if (!req.every(c => c.checked)) {
          setStatus('termsStatus', '필수 약관에 동의해주세요.', false);
          return false;
        }
        return true;
      }

      if (step === 2 && signUpType === 'ENTERPRISE' && !bnoVerified) {
        setStatus('bNoStatus', '사업자 인증이 필요합니다.', false);
        return false;
      }

      if (step === 3 && signUpType === 'ENTERPRISE' && !document.getElementById('caution-agree-chk').checked) {
        setStatus('cautionStatus', '유의사항에 동의해주세요.', false);
        return false;
      }

      if (step === 4 && !phoneVerified) {
        setStatus('phoneStatus', '본인인증을 완료해주세요.', false);
        return false;
      }

      if (step === 5 && !emailVerified) {
        setStatus('emailVerifyStatus', '이메일 인증을 완료해주세요.', false);
        return false;
      }

      return true;
    }

    function handleAllAgree(master){
      document.querySelectorAll('.sub-chk').forEach(c => c.checked = master.checked);
    }
    function updateMasterCheckbox(){
      const all = Array.from(document.querySelectorAll('.sub-chk'));
      document.getElementById('agree-all-chk').checked = all.length > 0 && all.every(c => c.checked);
    }

    function mockPhoneVerify(){
      const phone = (document.getElementById('phone').value || '').replace(/\D/g,'');
      if (phone.length < 10) {
        setStatus('phoneStatus', '휴대폰 번호를 입력해주세요.', false);
        return;
      }
      phoneVerified = true;
      setStatus('phoneStatus', '본인인증이 완료되었습니다.', true);
    }

    function onBnoInput(){
      const el = document.getElementById('bNo');
      if (!el) return;
      el.value = (el.value || '').replace(/\D/g,'').slice(0,10);
      bnoVerified = false;
      clearStatus('bNoStatus');
    }

    async function verifyBno() {
      const rawEl = document.getElementById('bNo');
      const bNo = ((rawEl ? rawEl.value : "") || "").replace(/[^0-9]/g, "");

      if (bNo.length !== 10) {
        setStatus('bNoStatus', '10자리(숫자)만 입력해주세요.', false);
        bnoVerified = false;
        return;
      }

      setStatus('bNoStatus', '조회 중...', null);

      try {
        const res = await fetch(ctx + "/api/auth/enterprise/verification", {
          method: "POST",
          headers: { "Content-Type": "application/json", "Accept": "application/json" },
          body: JSON.stringify({ bNo })
        });

        const ct = (res.headers.get("content-type") || "").toLowerCase();

        if (ct.includes("application/json")) {
          const data = await res.json();
          const ok = (res.ok === true) && (data && data.verified === true);

          bnoVerified = ok;
          if (ok) setStatus('bNoStatus', data.message || '인증되었습니다.', true);
          else setStatus('bNoStatus', (data && (data.message || data.msg)) || '인증 실패', false);
          return;
        }

        bnoVerified = false;
        setStatus('bNoStatus', '인증 실패(응답 형식 오류)', false);

      } catch (e) {
        console.error(e);
        bnoVerified = false;
        setStatus('bNoStatus', '서버 통신 오류', false);
      }
    }

    function isValidEmail(email){
      return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
    }

    function formatMMSS(sec){
      const m = String(Math.floor(sec / 60)).padStart(2,'0');
      const s = String(sec % 60).padStart(2,'0');
      return m + ':' + s;
    }

    function startEmailTimer(sec = EMAIL_EXPIRE_SEC){
      emailRemainSec = sec;

      const timerEl = document.getElementById('emailTimer');
      if (!timerEl) return;

      timerEl.classList.remove('hidden');
      timerEl.textContent = formatMMSS(emailRemainSec);

      if (emailTimerInterval) clearInterval(emailTimerInterval);

      emailTimerInterval = setInterval(() => {
        emailRemainSec--;
        timerEl.textContent = formatMMSS(Math.max(0, emailRemainSec));

        if (emailRemainSec <= 0) {
          clearInterval(emailTimerInterval);
          emailTimerInterval = null;

          setStatus('emailSendStatus', '인증번호가 만료되었습니다. 재전송해주세요.', false);
          const btn = document.getElementById('emailSendBtn');
          if (btn) btn.textContent = '재전송';
          emailVerified = false;
        }
      }, 1000);
    }

    function stopEmailTimer(){
      if (emailTimerInterval) clearInterval(emailTimerInterval);
      emailTimerInterval = null;
      const timerEl = document.getElementById('emailTimer');
      if (timerEl) timerEl.classList.add('hidden');
    }

    async function sendEmailCode(){
      const emailEl = document.getElementById('email');
      const email = (emailEl.value || '').trim();

      emailEl.classList.remove('input-error');
      clearStatus('emailSendStatus');
      clearStatus('emailVerifyStatus');
      emailVerified = false;

      if (!email) {
        emailEl.classList.add('input-error');
        setStatus('emailSendStatus', '이메일을 입력해주세요.', false);
        return;
      }
      if (!isValidEmail(email)) {
        emailEl.classList.add('input-error');
        setStatus('emailSendStatus', '유효한 이메일 주소를 입력해주세요.', false);
        return;
      }

      setStatus('emailSendStatus', '전송 중...', null);

      const btn = document.getElementById('emailSendBtn');
      if (btn) btn.disabled = true;

      try {
        const res = await fetch(ctx + "/api/auth/email/verification", {
          method: "POST",
          headers: {"Content-Type":"application/json"},
          body: JSON.stringify({ email })
        });

        const text = (await res.text()) || '';

        if (text.includes('이미 가입') || text.includes('중복')) {
          emailEl.classList.add('input-error');
          setStatus('emailSendStatus', text, false);
          stopEmailTimer();
          if (btn) btn.textContent = '인증요청';
          return;
        }

        if (!res.ok) {
          emailEl.classList.add('input-error');
          setStatus('emailSendStatus', text || '발송 실패. 다시 시도해주세요.', false);
          return;
        }

        setStatus('emailSendStatus', text || '인증번호가 메일로 발송되었습니다.', true);
        startEmailTimer(EMAIL_EXPIRE_SEC);
        if (btn) btn.textContent = '재전송';

      } catch (e) {
        console.error(e);
        emailEl.classList.add('input-error');
        setStatus('emailSendStatus', '서버 통신 오류', false);
      } finally {
        if (btn) btn.disabled = false;
      }
    }

    async function confirmEmailCode(){
      const emailEl = document.getElementById('email');
      const codeEl = document.getElementById('emailCode');

      const email = (emailEl.value || '').trim();
      const code = (codeEl.value || '').trim();

      codeEl.classList.remove('input-error');
      clearStatus('emailVerifyStatus');

      if (!email) {
        emailEl.classList.add('input-error');
        setStatus('emailVerifyStatus', '이메일을 먼저 입력해주세요.', false);
        return;
      }

      if (!code) {
        codeEl.classList.add('input-error');
        setStatus('emailVerifyStatus', '인증번호를 입력해주세요.', false);
        return;
      }

      try {
        const res = await fetch(ctx + "/api/auth/email/verification/confirmation", {
          method: "POST",
          headers: {"Content-Type":"application/json"},
          body: JSON.stringify({ email, code })
        });

        const text = (await res.text()) || '';
        const ok = res.ok && (text.includes('성공') || text.toLowerCase().includes('success'));

        emailVerified = ok;

        if (ok) {
          setStatus('emailVerifyStatus', '인증되었습니다.', true);
          stopEmailTimer();
        } else {
          codeEl.classList.add('input-error');
          setStatus('emailVerifyStatus', '인증번호가 일치하지 않습니다.', false);
        }

      } catch(e) {
        console.error(e);
        emailVerified = false;
        codeEl.classList.add('input-error');
        setStatus('emailVerifyStatus', '서버 통신 오류', false);
      }
    }

    function checkPw(){
      const p1 = document.getElementById('password').value || '';
      const p2 = document.getElementById('password2').value || '';
      const ok = (p1.length > 0 && p1 === p2);
      if (p1.length === 0 && p2.length === 0) {
        setStatus('pwStatus', '', null);
        return false;
      }
      setStatus('pwStatus', ok ? '비밀번호가 일치합니다.' : '비밀번호가 일치하지 않습니다.', ok);
      return ok;
    }

    async function submitSignUp(){
      if (!emailVerified || !phoneVerified || !checkPw()) return;

      const payload = {
        userName: (document.getElementById('userName').value || '').trim(),
        email: (document.getElementById('email').value || '').trim(),
        password: document.getElementById('password').value || '',
        phoneNumber: (document.getElementById('phone').value || '').replace(/\D/g,'')
      };

      if (!payload.userName) {
        setStatus('signupStatus', '이름을 입력해주세요.', false);
        return;
      }

      if (signUpType === 'ENTERPRISE') {
    	  payload.brn = (document.getElementById('bNo').value || '').replace(/\D/g,'');
      }

      setStatus('signupStatus', '처리 중...', null);

      try {
        const res = await fetch(ctx + "/api/auth/signup", {
          method: "POST",
          headers: {"Content-Type":"application/json"},
          body: JSON.stringify(payload)
        });

        if (res.ok) {
          step = 7;
          renderUI();
        } else {
          const t = await res.text();
          setStatus('signupStatus', t || '회원가입 실패', false);
        }
      } catch(e) {
        console.error(e);
        setStatus('signupStatus', '서버 통신 오류', false);
      }
    }

    function initDots(){
      const c = document.getElementById('step-indicators');
      if (!c) return;
      c.innerHTML = '';
      const total = (signUpType === 'PERSONAL') ? 5 : 7;
      for (let i=1; i<=total; i++) {
        const d = document.createElement('div');
        d.className = 'dot';
        d.id = 'dot-' + i;
        c.appendChild(d);
      }
    }

    function setStatus(id, msg, ok){
      const el = document.getElementById(id);
      if (!el) return;
      el.textContent = msg || '';
      el.className = 'status' + (ok === true ? ' ok' : ok === false ? ' bad' : '');
    }
    function clearStatus(id){ setStatus(id, '', null); }
  </script>
</body>
</html>
