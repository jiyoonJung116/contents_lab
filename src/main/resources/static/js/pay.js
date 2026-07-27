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

    container.addEventListener("click", function (e) {
        const btn = e.target.closest(".btn-select-plan");
        if (!btn) return;

        const name = btn.getAttribute("data-plan-name");
        const price = Number(btn.getAttribute("data-plan-price"));

        document.getElementById("selected-plan-name").textContent = `${name} 플랜`;
        document.getElementById("selected-plan-price").textContent = `₩${price.toLocaleString()}`;
        document.getElementById("btn-pay-submit").textContent = `₩${price.toLocaleString()} 결제하기`;

        document.getElementById("checkout-section").scrollIntoView({ behavior: 'smooth' });
    });
});