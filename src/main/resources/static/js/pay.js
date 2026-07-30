document.addEventListener("DOMContentLoaded", function () {
    const container = document.getElementById("plan-container");
    const rawList = window.rawSubscribeList;

    if (!container) return;

    if (!rawList || !Array.isArray(rawList) || rawList.length === 0) {
        container.innerHTML = `
            <div class="col-span-full py-12 text-center text-gray-400">
                <i class="fa-solid fa-triangle-exclamation text-2xl mb-2"></i>
                <p class="text-sm">불러올 수 있는 구독 플랜 데이터가 없습니다.</p>
            </div>
        `;
        return;
    }

    // 1. 플랜 데이터 가공
    const groupedPlans = rawList.reduce((acc, item) => {
        const rawType = item.subscribeType || '';
        const rawName = item.subscribeName || '';
        const groupKey = rawType.replace(/_(monthly|yearly)$/i, '').trim(); 
        const displayName = rawName.replace(/\(월간\)|\(연간\)/g, '').trim();
        const cycle = (item.billingCycle || '').toUpperCase();

        if (!acc[groupKey]) {
            acc[groupKey] = {
                groupKey: groupKey,
                displayName: displayName,
                description: item.description || '',
                monthly: null,
                yearly: null
            };
        }

        const isYearly = cycle === 'YEARLY' || rawType.includes('yearly') || rawName.includes('연간');

        if (isYearly) {
            acc[groupKey].yearly = item;
        } else {
            acc[groupKey].monthly = item;
        }

        return acc;
    }, {});

    container.innerHTML = "";

    // 2. 카드리스트 DOM 생성
    Object.values(groupedPlans).forEach(plan => {
        const monthlyItem = plan.monthly;
        const yearlyItem = plan.yearly;
        const monthlyPrice = monthlyItem ? monthlyItem.price : 0;
        const yearlyPrice = yearlyItem ? yearlyItem.price : null;
        
        let discountRate = 20;
        if (yearlyItem && yearlyItem.discountRate) {
            discountRate = yearlyItem.discountRate;
        }

        const defaultPlanId = monthlyItem ? monthlyItem.id : (yearlyItem ? yearlyItem.id : '');
        
        let yearlyPriceHtml = '';
        if (yearlyPrice) {
            yearlyPriceHtml = `
                <div class="flex items-center gap-2 text-xs font-semibold text-indigo-600 mb-6">
                    <span>₩${yearlyPrice.toLocaleString()} / 연</span>
                    <span class="bg-indigo-50 text-indigo-600 font-bold px-2 py-0.5 rounded text-[11px]">${discountRate}% 할인</span>
                </div>
            `;
        } else {
            yearlyPriceHtml = `<div class="mb-6 h-[18px]"></div>`;
        }

        const cardHtml = `
            <div class="bg-white border border-gray-200 hover:border-indigo-500 rounded-2xl p-6 flex flex-col justify-between shadow-sm transition duration-200">
                <div>
                    <h3 class="text-xl font-bold text-gray-900">${plan.displayName}</h3>
                    <p class="text-xs text-gray-400 mt-1 min-h-[32px]">${plan.description || ''}</p>
                    
                    <div class="my-4">
                        <span class="text-3xl font-black text-gray-900">₩${monthlyPrice.toLocaleString()}</span>
                        <span class="text-gray-400 text-sm font-medium"> / 월</span>
                    </div>

                    ${yearlyPriceHtml}

                    <button type="button" 
                            class="btn-select-plan w-full bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-3 rounded-xl text-sm transition shadow-sm"
                            data-plan-id="${defaultPlanId}"
                            data-plan-name="${plan.displayName}"
                            data-plan-price="${monthlyPrice}">
                        ${plan.displayName} 플랜 선택
                    </button>
                </div>
            </div>
        `;

        container.insertAdjacentHTML("beforeend", cardHtml);
    });

    // 3. 플랜 선택
    const selectButtons = document.querySelectorAll(".btn-select-plan");
    const selectedPlanNameElem = document.getElementById("selected-plan-name");
    const selectedPlanPriceElem = document.getElementById("selected-plan-price");
    const btnPaySubmit = document.getElementById("btn-pay-submit");

    selectButtons.forEach(button => {
        button.addEventListener("click", function () {
            const planId = this.getAttribute("data-plan-id");
            const planName = this.getAttribute("data-plan-name");
            const planPrice = parseInt(this.getAttribute("data-plan-price"), 10);

            if (selectedPlanNameElem) {
                selectedPlanNameElem.textContent = `${planName} 플랜`;
            }
            if (selectedPlanPriceElem) {
                selectedPlanPriceElem.textContent = `₩${planPrice.toLocaleString()}`;
            }

            if (btnPaySubmit) {
                btnPaySubmit.setAttribute("data-selected-plan-id", planId);
                btnPaySubmit.setAttribute("data-selected-price", planPrice);
                btnPaySubmit.setAttribute("data-selected-name", planName);
                btnPaySubmit.textContent = `₩${planPrice.toLocaleString()} 결제하기`;
            }

            const checkoutSection = document.getElementById("checkout-section");
            if (checkoutSection) {
                checkoutSection.scrollIntoView({ behavior: 'smooth' });
            }
        });
    });

    // 4. 결제하기
    if (btnPaySubmit) {
        btnPaySubmit.addEventListener("click", async function (e) {
            e.preventDefault();

            const planId = this.getAttribute("data-selected-plan-id");
            const price = this.getAttribute("data-selected-price");
            const planName = this.getAttribute("data-selected-name") || "구독 플랜";

            if (!planId || !price) {
                alert("플랜을 먼저 선택해 주세요.");
                return;
            }

            const nameElem = document.getElementById("user-name");
            const emailElem = document.getElementById("user-email");
            const phoneElem = document.getElementById("user-phone");
            const userName = (nameElem && nameElem.value.trim()) ? nameElem.value.trim() : "홍길동";
            const userEmail = (emailElem && emailElem.value.trim()) ? emailElem.value.trim() : "test@example.com";
            const rawPhone = (phoneElem && phoneElem.value) ? phoneElem.value : "01000000000";
            const phoneNumber = rawPhone.replace(/[^0-9]/g, "") || "01000000000";
            const customerData = {
                fullName: userName,
                email: userEmail,
                phoneNumber: phoneNumber
            };

            console.log("포트원으로 전송할 customer 객체:", customerData);

            try {
                const response = await PortOne.requestIssueBillingKey({
                    storeId: "store-3e62c10f-5b2d-4de3-8ed8-dd1634babe18",
                    channelKey: "channel-key-c18980c7-34f7-41b5-a631-ff8a7584f4a9",
                    billingKeyMethod: "CARD",
                    issueId: `issue_${Date.now()}`,
                    issueName: `${planName} 구독`,
                    customer: customerData
                });

                if (response.code != null) {
                    alert(`결제수단 등록 실패: ${response.message}`);
                    return;
                }

                console.log("발급 성공 빌키:", response.billingKey);

                try {
                    const res = await fetch('/api/subscribe/register', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify({
                            billingKey: response.billingKey,
                            planId: planId,
                            amount: parseInt(price, 10),
                            customerEmail: userEmail
                        })
                    });

                    const result = await res.json();

                    if (res.ok && result.success) {
                        alert("구독 신청 및 결제가 성공적으로 처리되었습니다.");
                        location.reload();
                    } else {
                        alert(`구독 처리 오류: ${result.message || '서버 응답 실패'}`);
                    }
                } catch (backendErr) {
                    console.warn("백엔드 서버 응답 없음:", response.billingKey);
                    alert(`(테스트용 빌키: ${response.billingKey})\n응답하지 않습니다.`);
                }

            } catch (err) {
                console.error("결제 진행 중 에러 발생:", err);
                alert("결제 처리 중 예상치 못한 에러가 발생했습니다.");
            }
        });
    }
});