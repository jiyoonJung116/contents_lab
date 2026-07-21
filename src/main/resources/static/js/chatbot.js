let selectedType = "";
let selectedTags = [];
let currentRoomId = null;        
let currentRoomType = null;
let currentScenarioVersions = []; 
let selectedScenarioVersionIndex = 0;
let currentScenariosForRender = [];

function changeRoom(roomId, roomType) {
    currentRoomId = roomId;
    currentRoomType = roomType;
    checkRoomTypeAndLoadAssets(roomType, roomId);
}

function checkRoomTypeAndLoadAssets(roomType, roomId) {
    const assetSelector = document.getElementById("builderAssetSelector");
    const btnScript = document.getElementById("sendButton");     
    const btnCharacter = document.getElementById("characterButton");   
    const activeRoomId = roomId || (typeof currentRoomId !== "undefined" ? currentRoomId : null);

    if (assetSelector) {
        if (roomType === "builder") {
            assetSelector.classList.remove("hidden");
            loadBuilderAssets();
        } else {
            assetSelector.classList.add("hidden");
        }
    }

    if (activeRoomId == 2) {  // 캐릭터 메이커
        btnCharacter.classList.remove("hidden"); 
        btnScript.classList.add("hidden");   
    } else  {
        btnCharacter.classList.add("hidden");  
        btnScript.classList.remove("hidden");
    }

    const btnText = btnScript.querySelector("span");
    if (activeRoomId == 3) {  // 스토리 메이커
        btnText.innerText = "대본 생성";   
    } else if (activeRoomId == 4) {  // 인스타툰 메이커
        btnText.innerText = "인스타툰 생성";
    } else if (activeRoomId == 6) {  // 아이디어 챗봇
        btnText.innerText = "아이디어 생성";
    }
}

// 캐릭터와 대본을 동시에 불러오는 함수
function loadBuilderAssets() {
    fetch("/api/chat/scripts")
    .then(res => res.json())
    .then(data => {
        const scriptSelect = document.getElementById("selectedScript");
        scriptSelect.innerHTML = '<option value="">-- 매칭할 스토리 대본 선택 --</option>';

        const scripts = data.script_info; 
        scripts.forEach(script => {
            let rawText = script.content || "";
            const roomTitle = script.roomName || script.room_name || "알 수 없는 방";
            if (typeof rawText === 'string') {
                rawText = rawText.replace(/```json|```/g, "").trim();
            }
            
            if (rawText.startsWith("[")) {
                const scenarioVersions = JSON.parse(rawText);
                
                scenarioVersions.forEach(scenario => {
                    const shortVersion = scenario.versionName ? scenario.versionName.split(' ')[0] : "대안";
                    
                    scenario.cuts.forEach(cut => {
                        const option = document.createElement("option");
                        const valueObject = {
                            scriptId: script.id,
                            cutIndex: cut.cutIndex,
                            content: cut.content,
                            sceneDescription: cut.sceneDescription
                        };

                        option.value = JSON.stringify(valueObject);
                        option.textContent = `[${roomTitle}] ${shortVersion} - ${cut.cutIndex}컷: ${cut.content}`;
                        scriptSelect.appendChild(option);
                    });
                });

                return; 
            }

            const shortText = rawText ? (rawText.substring(0, 30) + "...") : "내용이 비어있는 대본";
            const option = document.createElement("option");
            option.value = JSON.stringify({ scriptId: script.id, content: rawText });
            option.textContent = `[${roomTitle}] ${shortText}`;
            scriptSelect.appendChild(option);
        });
    })
    .catch(err => console.error("대본 로드 실패:", err));

    // 캐릭터 목록 불러오기
    fetch("/api/chat/characters")
    .then(res => res.json())
    .then(data => {
        const checklistDiv = document.getElementById("characterChecklist");
        checklistDiv.innerHTML = "";
        
        if (data.status !== "success" || !data.character_list) {
            checklistDiv.innerHTML = '<p class="text-gray-400 text-[11px]">캐릭터 데이터를 불러오지 못했습니다.</p>';
            return;
        }

        const characters = data.character_list;

        if (characters.length === 0) {
            checklistDiv.innerHTML = '<p class="text-gray-400 text-[11px]">생성된 캐릭터가 없습니다. 캐릭터 제작소에서 먼저 만들어주세요!</p>';
            return;
        }

        characters.forEach(char => {
            const label = document.createElement("label");
            label.className = "relative flex flex-col items-center justify-center bg-gray-50 border border-gray-200 p-3 rounded-xl cursor-pointer hover:bg-indigo-50/50 hover:border-indigo-200 transition-all w-20 h-24 shadow-sm";
            
            const safetyPrompt = char.actualPrompt || char.actual_prompt || "A cute chibi character, webtoon style";
            const imgSrc = char.imageUrl || char.image_url || char.filePath || char.file_path;

            label.innerHTML = `
                <input type="checkbox" value="${char.id}" data-prompt="${safetyPrompt.replace(/"/g, '&quot;')}"
                    class="absolute top-1.5 left-1.5 rounded text-indigo-600 focus:ring-indigo-500 character-item w-3.5 h-3.5 cursor-pointer z-10">
                <img src="${imgSrc}" 
                    class="w-14 h-14 rounded-lg object-cover border border-gray-200/80 shadow-inner">
            `;
            checklistDiv.appendChild(label);
        });
    })
    .catch(err => console.error("캐릭터 로드 실패:", err));
}

async function generate() {
    const promptElement = document.getElementById("prompt");
    const prompt = promptElement ? promptElement.value.trim() : "";
    
    if (currentRoomType !== "builder" && !prompt) { 
        alert("주제 또는 명령어를 입력해주세요."); 
        return; 
    }

    const loadingDiv = document.getElementById("loading");
    const resultDiv = document.getElementById("result");
    const carouselContainer = document.getElementById("carouselContainer");

    if (resultDiv) { resultDiv.innerHTML = ""; resultDiv.classList.add("hidden"); }
    if (carouselContainer) carouselContainer.innerHTML = "";

    if (currentRoomType === "idea") {
        loadingDiv.innerText = "입력하신 키워드를 기반으로 트렌디한 인스타툰 아이디어 10가지를 발굴하고 있습니다...";

        try {
            const response = await fetch("/api/chat/idea/generate", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ 
                    roomId: currentRoomId, 
                    userInput: prompt
                })
            });

            const data = await response.json();
            loadingDiv.innerText = "";
            if (promptElement) {
                promptElement.value = "";
            }

            if (data.success && resultDiv) {
                resultDiv.classList.remove("hidden");
                
                let replyContent = data.ai_response;
                
                if (typeof replyContent === 'string') {
                    replyContent = replyContent.replace(/```markdown|```json|```/g, "").trim();
                }
                
                let html = `
                    <div class="p-5 bg-white rounded-2xl border border-amber-100 shadow-sm mb-4">
                        <div class="flex items-center space-x-2 mb-4">
                            <span class="p-1.5 bg-amber-50 rounded-xl text-amber-500 font-bold text-lg">💡</span>
                            <h3 class="font-extrabold text-lg text-gray-800">인스타툰 아이디어</h3>
                        </div>
                        <div class="prose max-w-none text-gray-700 leading-relaxed max-h-[500px] overflow-y-auto p-1">
                            ${typeof marked !== 'undefined' ? marked.parse(replyContent) : `<div class="whitespace-pre-line text-sm">${replyContent}</div>`}
                        </div>
                    </div>
                `;
                resultDiv.innerHTML = html;

            } else {
                alert("아이디어 생성 실패: " + data.error);
            }
        } catch (e) {
            loadingDiv.innerText = "";
            alert("아이디어 생성 중 오류가 발생했습니다.");
        }
    } else if (currentRoomType === "card") {
        loadingDiv.innerText = "스토리메이커가 콘텐츠 아이디어와 대사를 창작하고 있습니다...";

        const cutCountElement = document.getElementById("cutCountInput");
        const cutCount = cutCountElement ? parseInt(cutCountElement.value) : 4;

        try {
            const response = await fetch("/api/chat/story/generate", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ 
                    roomId: currentRoomId, 
                    userInput: prompt,
                    cutCount: cutCount
                })
            });

            const data = await response.json();
            loadingDiv.innerText = "";
            if (promptElement) {
                promptElement.value = "";
            }

            if (data.success && resultDiv) {
                resultDiv.classList.remove("hidden");
                
                try {
                    let replyContent = data.bot_reply;
                    if (typeof replyContent === 'string') {
                        replyContent = replyContent.replace(/```json|```/g, "").trim();
                    }
                    
                    currentScenarioVersions = typeof replyContent === 'string' ? JSON.parse(replyContent) : replyContent;
                    
                    let html = `
                        <div class="p-4 bg-gray-50 rounded-lg mb-4 border border-gray-200">
                            <h3 class="font-bold text-lg mb-3 text-indigo-600">인스타툰 시나리오 대안</h3>
                            <div class="grid grid-cols-3 gap-3 mb-4">
                    `;
                    
                    currentScenarioVersions.forEach((scenario, sIdx) => {
                        html += `
                            <button type="button" 
                                    class="p-3 border rounded-lg bg-white hover:bg-indigo-50 hover:border-indigo-300 transition text-center font-medium shadow-sm"
                                    onclick="renderScenarioDetail(${sIdx})">
                                    ${scenario.versionName || (sIdx + 1) + '안'}
                            </button>
                        `;
                    });
                    
                    html += `
                            </div>
                            <div id="scenarioDetailArea" class="mt-4 p-4 border rounded-lg bg-white shadow-inner hidden"></div>
                        </div>
                    `;
                    resultDiv.innerHTML = html;
                    
                    customSelectedCuts = {};
                    
                    renderScenarioDetail(0);

                } catch (jsonErr) {
                    const rawText = data.botReply || data.content;
                    resultDiv.innerHTML = typeof marked !== 'undefined' ? marked.parse(rawText) : `<div class="p-2 whitespace-pre-line">${rawText}</div>`;
                }
                
            } else {
                alert("생성 실패: " + data.error);
            }
        } catch (e) {
            loadingDiv.innerText = "";
            alert("스토리 생성 중 오류가 발생했습니다.");
        }
    } else if (currentRoomType === "builder") {
        loadingDiv.innerText = "선택하신 컷과 캐릭터를 조합하여 일체감 있는 인스타툰을 빌딩하고 있습니다...";

        const checkedCharacters = Array.from(document.querySelectorAll(".character-item:checked")).map(el => el.value);
        const characterPrompts = Array.from(document.querySelectorAll(".character-item:checked")).map(el => el.getAttribute("data-prompt"));
        
        const selectedScriptValue = document.getElementById("selectedScript").value;
        if (!selectedScriptValue) {
            alert("매칭할 스토리 대본을 선택해 주세요.");
            loadingDiv.innerText = "";
            return;
        }

        let finalScriptId = "";
        let finalCutIndex = 1;
        let finalContent = "";
        let finalSceneDescription = "";

        try {
            const scriptData = JSON.parse(selectedScriptValue);
            
            finalScriptId = scriptData.scriptId;
            finalCutIndex = scriptData.cutIndex;
            finalContent = scriptData.content;
            finalSceneDescription = scriptData.sceneDescription;
        } catch (e) {
            finalScriptId = selectedScriptValue; 
        }

        try {
            const response = await fetch("/api/chat/toon/generate", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    roomId: currentRoomId,
                    scriptId: finalScriptId,
                    characterIds: checkedCharacters,
                    characterPrompts: characterPrompts,
                    additionalPrompt: prompt,
                    cutIndex: finalCutIndex,
                    content: finalContent,
                    sceneDescription: finalSceneDescription
                })
            });

            const data = await response.json();
            loadingDiv.innerText = "";
            if (promptElement) {
                promptElement.value = "";
            }

            if (typeof renderContent === "function") {
                await renderContent(data.content);
            } else if (resultDiv) {
                resultDiv.classList.remove("hidden");
                resultDiv.innerHTML = typeof marked !== 'undefined' ? marked.parse(data.content) : `<div class="p-2 whitespace-pre-line">${data.content}</div>`;
            }

        } catch (e) {
            loadingDiv.innerText = "";
            alert("툰 빌딩 중 오류가 발생했습니다.");
        }
    } else {
        await generateCharacter();
    }
}

let customSelectedCuts = {}; 
function renderScenarioDetail(index) {
    const detailArea = document.getElementById("scenarioDetailArea");
    if (!detailArea) return;

    const scenario = currentScenarioVersions[index];
    if (!scenario) return;

    detailArea.classList.remove("hidden");

    let html = `
        <div class="border-b pb-2 mb-3 flex justify-between items-center">
            <div>
                <span class="inline-block bg-indigo-100 text-indigo-800 text-xs font-semibold px-2.5 py-0.5 rounded">현재 보기</span>
                <h4 class="font-bold text-gray-800 text-base mt-1">🎯 ${scenario.versionName}</h4>
            </div>
            <button type="button" onclick="saveCustomCombination()" class="bg-indigo-600 hover:bg-indigo-700 text-white text-xs font-bold py-2 px-4 rounded shadow shadow-indigo-200 transition">
                이 컷 조합으로 시나리오 확정하기
            </button>
        </div>
        <p class="text-xs text-gray-400 mb-4">각 컷별로 마음에 드는 안의 라디오 버튼을 선택해 나만의 대본을 조립해 보세요!</p>
        <div class="space-y-4">
    `;

    scenario.cuts.forEach(cut => {
        const cutIdx = cut.cutIndex;
        const content = cut.content;
        const sceneDesc = cut.sceneDescription;
        const isChecked = customSelectedCuts[cutIdx] && customSelectedCuts[cutIdx].fromVersion === scenario.versionName;

        html += `
            <div class="p-3 bg-gray-50 rounded-lg border border-gray-200 flex items-start justify-between">
                <div class="text-sm text-gray-700 space-y-1 pr-4">
                    <div class="flex items-center mb-1">
                        <span class="w-6 h-6 bg-indigo-500 text-white rounded-full flex items-center justify-center text-xs font-bold mr-2">${cutIdx}컷</span>
                        <span class="text-xs text-indigo-500 font-semibold">[${scenario.versionName.split(' ')[0]}]</span>
                    </div>
                    <p class="ml-8 text-gray-900 font-medium">대사: "${content}"</p>
                    <p class="text-xs text-gray-500 ml-8">연출: ${sceneDesc}</p>
                </div>
                
                <div class="pt-1">
                    <input type="radio" 
                           name="cut_select_${cutIdx}" 
                           class="w-5 h-5 text-indigo-600 border-gray-300 focus:ring-indigo-500 cursor-pointer"
                           ${isChecked ? 'checked' : ''} 
                           onclick="trackCutSelection(${cutIdx}, '${scenario.versionName}', \`${content.replace(/`/g, '\\`')}\`, \`${sceneDesc.replace(/`/g, '\\`')}\`)">
                </div>
            </div>
        `;
        
        if (!customSelectedCuts[cutIdx]) {
            trackCutSelection(cutIdx, scenario.versionName, content, sceneDesc);
        }
    });

    html += `</div>`;
    detailArea.innerHTML = html;
}

function trackCutSelection(cutIndex, versionName, content, sceneDescription) {
    customSelectedCuts[cutIndex] = {
        cutIndex: cutIndex,
        fromVersion: versionName,
        content: content,
        sceneDescription: sceneDescription
    };
}

function saveCustomCombination() {
    const finalCutsArray = Object.values(customSelectedCuts).sort((a, b) => a.cutIndex - b.cutIndex);
    
    localStorage.setItem("myCustomScript", JSON.stringify(finalCutsArray));
}

function saveCutSelection(cutIndex, versionName, content, sceneDescription) {
    customSelectedCuts[cutIndex] = {
        cutIndex: cutIndex,
        versionName: versionName,
        content: content,
        sceneDescription: sceneDescription
    };
}

async function renderContent(content) {
    const resultDiv = document.getElementById("result");
    if (resultDiv) {
        resultDiv.classList.remove("hidden"); 

        if (typeof marked !== 'undefined') {
            resultDiv.innerHTML = marked.parse(content);
        } else {
            resultDiv.innerText = content;
        }
    }
    await makeCarousel(content);
}

function createImagePrompt(section) {
    let imgDescription = "";
    const match = section.match(/\*\s*이미지\s*:\s*(.*)/);
    
    if (match && match[1]) {
        imgDescription = match[1];
    } else {
        imgDescription = section;
    }
    
    imgDescription = imgDescription.replace(/[\[\]]/g, "").trim();
    return `cute korean instagram comic style, young korean office worker in 20s, ${imgDescription}, flat illustration, pastel colors, kawaii character design, webtoon style, instagram carousel illustration, no text, no words, no letters, no watermark, no logo, high quality`;
}

async function makeCarousel(content) {
    const container = document.getElementById("carouselContainer");
    if (!container) return;
    container.innerHTML = "";

    const splitRegex = /\d+\s*[\.\s]\s*(?:컷|카드|페이지)\s*:/g;
    const sections = content.split(splitRegex).filter(section => section.trim() !== "");
    
    if (sections.length === 0 || (!content.match(splitRegex) && sections.length === 1)) {
        sections[0] = content;
    }

    for (let i = 0; i < sections.length; i++) {
        const section = sections[i].trim();
        const card = document.createElement("div");
        card.className = "carousel-card bg-white border border-gray-200 rounded-xl p-4 shadow-sm flex flex-col justify-between";

        let parsedSection = typeof marked !== 'undefined' ? marked.parse(section) : section;

        card.innerHTML = `
            <div>
                <h3 class="text-xs font-bold text-indigo-600 mb-2">${i + 1}페이지</h3>
                <div class="text-[11px] text-gray-600 leading-relaxed">${parsedSection}</div>
            </div>
            <div class="image-area mt-3 pt-3 border-t border-gray-100 flex flex-col items-center justify-center min-h-[150px] bg-gray-50 rounded-lg">
                <p class="status-text text-[10px] text-gray-400 font-medium">이미지 생성 중...</p>
            </div>
        `;

        container.appendChild(card);

        try {
            const imagePrompt = createImagePrompt(section);
            const response = await fetch("/api/image", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ prompt: imagePrompt })
            });

            const imageBase64 = await response.text();
            const imageArea = card.querySelector(".image-area");
            if (imageArea) {
                imageArea.innerHTML = ""; 
                imageArea.classList.remove("bg-gray-50");
                
                const img = document.createElement("img");
                img.src = "data:image/png;base64," + imageBase64;
                img.className = "w-full aspect-square object-cover rounded-lg shadow-inner";
                imageArea.appendChild(img);
            }
        } catch (e) {
            const statusP = card.querySelector(".status-text");
            if (statusP) {
                statusP.innerText = "이미지 생성 실패";
            }
        }
    }
}

//  캐릭터 기획/생성 실행 
async function generateCharacter() {
    const promptElement = document.getElementById("prompt");
    const userInput = promptElement ? promptElement.value.trim() : "";

    if (!userInput) {
        alert("캐릭터 기획을 위한 요구사항을 입력해주세요. (예: 하늘색 재테크 전문가 곰 캐릭터)");
        return;
    }

    document.getElementById("loading").innerText = "AI가 캐릭터 속성을 분석하고 맞춤형 캐릭터를 드로잉 중입니다...";
    
    const resultDiv = document.getElementById("result");
    const carouselContainer = document.getElementById("carouselContainer");
    
    if (resultDiv) { 
        resultDiv.innerHTML = ""; 
        resultDiv.classList.add("hidden"); 
    }

    if (carouselContainer) {
        carouselContainer.innerHTML = "";
    }

    try {
        const response = await fetch("/api/character/create", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                roomId: currentRoomId, 
                userInput: userInput
            })
        });

        const data = await response.json();
        document.getElementById("loading").innerText = "";
        if (promptElement) promptElement.value = "";

        if (data.status === "success") {
            const pipeline = data.pipeline_result || {};
            const botReply = pipeline.botReply || pipeline.content || "";
            const imageUrl = pipeline.imageUrl || pipeline.image_url || "";
            const characterName = pipeline.characterName || pipeline.name || "";

            renderCharacterResult(botReply, imageUrl, characterName);
            loadBuilderAssets(); 
            
        } else {
            alert("캐릭터 생성 실패: " + (data.error || "알 수 없는 오류"));
        }
    } catch (e) {
        document.getElementById("loading").innerText = "";
        alert("캐릭터 생성 중 오류가 발생했습니다.");
    }
}

function renderCharacterResult(botReply, dataUrl, characterName) {
    const resultDiv = document.getElementById("result");
    const carouselContainer = document.getElementById("carouselContainer");

    if (resultDiv) {
        resultDiv.classList.remove("hidden");
        resultDiv.innerHTML = `<div class="p-2 whitespace-pre-line text-sm text-gray-800">${botReply}</div>`;
    }

    if (carouselContainer) {
        const characterCard = document.createElement("div");
        characterCard.className = "carousel-card bg-white border border-gray-200 rounded-xl p-5 shadow-md flex flex-col items-center justify-between col-span-full max-w-sm mx-auto";

        characterCard.innerHTML = `
            <div class="text-center w-full">
                <span class="inline-block px-2.5 py-1 text-[10px] font-bold text-white bg-indigo-500 rounded-full mb-2">대표 캐릭터 인증</span>
                <h3 class="text-sm font-extrabold text-gray-900 mb-3">✨ 인스타툰 [${characterName}]</h3>
            </div>
            <div class="w-full aspect-square overflow-hidden rounded-lg border border-gray-100 shadow-inner bg-white">
                <img src="${dataUrl}" class="w-full h-full object-cover transition-transform duration-300 hover:scale-105" />
            </div>
            <p class="text-[10px] text-gray-400 mt-3 text-center w-full">※ 이 마스코트 이미지는 고정 낙서 스타일 프롬프트 뼈대를 기반으로 안전하게 생성되었으며 DB 영구 적재가 완료되었습니다.</p>`;
        carouselContainer.appendChild(characterCard);
    }
}

function switchChatRoom(roomId, element) {
    currentRoomId = roomId; 
    currentRoomType = element.getAttribute('data-room_type');   
    currentScenariosForRender = [];

    document.querySelectorAll('.room-item').forEach(rm => {
        rm.classList.remove('border-indigo-200', 'bg-indigo-50/50');
        rm.classList.add('border-gray-200');
    });
    element.classList.remove('border-gray-200');
    element.classList.add('border-indigo-200', 'bg-indigo-50/50');

    const chatContainer = document.getElementById('chatContainer');
    const resultDiv = document.getElementById('result');
    const carouselContainer = document.getElementById('carouselContainer');
    const promptElement = document.getElementById("prompt");
    const subHeaderDesc = document.querySelector("header p");
    
    if (resultDiv) { 
        resultDiv.classList.add('hidden'); 
        resultDiv.innerHTML = ''; 
    }

    if (carouselContainer) {
        carouselContainer.innerHTML = '';
        carouselContainer.classList.add('hidden');
    }

    if (chatContainer) {
        chatContainer.innerHTML = ''; 
    }

    const roomTitle = element.querySelector('.font-bold').innerText;
    document.querySelector('header h1').innerText = roomTitle;
    checkRoomTypeAndLoadAssets(currentRoomType);

    if (currentRoomType === "card") {
        if (subHeaderDesc) subHeaderDesc.innerText = "콘텐츠 아이디어와 카피 및 스토리 대사를 자유롭게 명령해보세요.";
        if (promptElement) promptElement.placeholder = "스토리 주제를 적거나 지시사항을 자유롭게 입력하세요... (예: 직장생활을 주제로 한 대사 써줘)";
    } else if (currentRoomType === "builder") {
        if (subHeaderDesc) subHeaderDesc.innerText = "작성된 대본과 생성된 캐릭터를 조합하여 컷툰을 빌딩합니다.";
        if (promptElement) promptElement.placeholder = "캐릭터 배치나 연출에 대한 추가 요구사항이 있다면 적어주세요...";
    } else {
        if (subHeaderDesc) subHeaderDesc.innerText = "방 설정 없이 자유롭게 대화해보세요.";
        if (promptElement) promptElement.placeholder = "대본 주제를 쓰거나, 생성하고 싶은 캐릭터 특징을 입력하세요... (예: 재테크하는 갈색 곰 캐릭터)";
    }

    fetch(`/api/chat/room/${roomId}`)
        .then(response => response.json())
        .then(data => {
            renderChatHistory(data);
        })
        .catch(error => console.error("대화 내역 로드 실패:", error));
}

function renderChatHistory(messageList) {
    const chatContainer = document.getElementById('chatContainer');
    if (!chatContainer) return;

    chatContainer.innerHTML = '';

    if (!messageList || !Array.isArray(messageList) || messageList.length === 0) {
        return;
    }

    currentScenariosForRender = [];
    messageList.forEach((msg, msgIdx) => {
        const role = msg.sender_type || "";
        
        let text = msg.content || msg.message || "";
        if (typeof text === 'string') {
            text = text.replace(/```markdown|```json|```/g, "").trim();
        }

        const messageRow = document.createElement('div');
        messageRow.className = "message-bubble flex w-full my-4 items-end space-x-2";

        if (role == 'USER') {
            messageRow.innerHTML = "";
            messageRow.classList.add('justify-end');
            messageRow.innerHTML = `
                <span class="text-[10px] text-gray-400 select-none pb-1">오후 6:40</span>
                <div class="max-w-[70%] bg-[#E8E8FF] text-gray-800 text-sm px-4 py-2.5 rounded-2xl rounded-tr-none shadow-sm whitespace-pre-line leading-relaxed">
                    ${text}
                </div>
            `;
            chatContainer.appendChild(messageRow);
        } else if (role == 'BOT') {
            messageRow.classList.add('justify-start', 'items-start');
            
            const isScenarioData = text.startsWith("[");
            let chatbotBubbleHTML = "";

            if (isScenarioData) {
                console.log("here1");
                try {
                    const scenarios = JSON.parse(text);
                    currentScenariosForRender = scenarios; 

                    let buttonsHTML = "";
                    scenarios.forEach((scenario, index) => {
                        buttonsHTML += `
                            <button type="button" 
                                    class="scenario-btn p-2 border rounded-lg bg-white hover:bg-indigo-50 hover:border-indigo-300 transition text-center font-bold text-xs shadow-sm ${index === 0 ? 'border-indigo-500 bg-indigo-50/50 text-indigo-600' : 'text-gray-700'}" 
                                    onclick="switchScenarioInBubble(${index})">
                                    ${scenario.versionName || `${index + 1}안`}
                            </button>
                        `;
                    });

                    const firstScenario = scenarios[0] || { versionName: "1안", cuts: [] };
                    let cutsHTML = "";
                    let chatbotBubbleHTML = "";
                    if (firstScenario.cuts && Array.isArray(firstScenario.cuts)) {
                        firstScenario.cuts.forEach(cut => {
                            cutsHTML += `
                                <div class="p-3 bg-gray-50 rounded-lg border border-gray-200 flex items-start justify-between">
                                    <div class="text-xs text-gray-700 space-y-1 pr-4">
                                        <div class="flex items-center mb-1">
                                            <span class="w-5 h-5 bg-indigo-500 text-white rounded-full flex items-center justify-center text-[10px] font-bold mr-2">${cut.cutIndex}컷</span>
                                            <span class="text-[10px] text-indigo-500 font-semibold">[${firstScenario.versionName.split(' ')[0]}]</span>
                                        </div>
                                        <p class="ml-7 text-gray-950 font-semibold">대사: "${cut.content}"</p>
                                        <p class="text-[11px] text-gray-500 ml-7">연출: ${cut.sceneDescription}</p>
                                    </div>
                                    <div class="pt-0.5">
                                        <input type="radio" 
                                               name="cut_select_${cut.cutIndex}" 
                                               class="w-4 h-4 text-indigo-600 border-gray-300 focus:ring-indigo-500 cursor-pointer" 
                                               onclick="trackCutSelection(${cut.cutIndex}, '${firstScenario.versionName}', \`${cut.content}\`, \`${cut.sceneDescription}\`)">
                                    </div>
                                </div>
                            `;
                        });
                    }

                    chatbotBubbleHTML = `
                        <div class="prose prose-sm max-w-full text-xs text-gray-800 bg-white p-4 rounded-xl border border-gray-200 shadow-sm w-full md:w-[550px] leading-relaxed">
                            <div class="p-4 bg-gray-50 rounded-xl mb-3 border border-gray-150">
                                <h3 class="font-extrabold text-base mb-3 text-indigo-600 flex items-center">
                                    <span class="mr-1.5">🎬</span> 인스타툰 시나리오 대안
                                </h3>
                                <div class="grid grid-cols-3 gap-2 mb-4">
                                    ${buttonsHTML}
                                </div>
                                <div id="scenarioDetailArea" class="mt-4 p-4 border rounded-xl bg-white shadow-inner">
                                    <div class="border-b pb-2 mb-3 flex justify-between items-center">
                                        <div>
                                            <span class="inline-block bg-indigo-100 text-indigo-800 text-[10px] font-semibold px-2 py-0.5 rounded">현재 보기</span>
                                            <h4 id="activeScenarioTitle" class="font-extrabold text-gray-800 text-sm mt-1">🎯 ${firstScenario.versionName}</h4>
                                        </div>
                                        <button type="button" onclick="saveCustomCombination()" class="bg-indigo-600 hover:bg-indigo-700 text-white text-[11px] font-bold py-1.5 px-3 rounded-lg shadow shadow-indigo-150 transition">
                                            이 컷 조합으로 시나리오 확정하기
                                        </button>
                                    </div>
                                    <p class="text-[11px] text-gray-400 mb-4">각 컷별로 마음에 드는 안의 라디오 버튼을 선택해 나만의 대본을 조립해 보세요!</p>
                                    <div id="activeCutsList" class="space-y-3">
                                        ${cutsHTML}
                                    </div>
                                </div>
                            </div>
                        </div>
                    `;
                } catch (e) {
                    console.error("JSON 파싱 에러 - 일반 말풍선으로 대체합니다:", e);
                    chatbotBubbleHTML = `<div class="bg-[#FFEFE0] text-gray-800 text-sm px-4 py-2.5 rounded-2xl rounded-tl-none shadow-sm whitespace-pre-line">${text}</div>`;
                }
            } else {
                console.log("here2");
                const isLongIdeaText = text.includes("##") || text.includes("**") || text.length > 150;
                let chatbotBubbleHTML = "";

                if (isLongIdeaText) {
                    chatbotBubbleHTML = `
                        <div class="prose prose-sm max-w-full text-xs text-gray-800 bg-white p-4 rounded-xl border border-gray-150 shadow-sm w-full md:w-[550px]">
                            <div class="p-5 bg-white rounded-2xl border border-amber-100 shadow-sm">
                                <!-- 상단 헤더 타이틀 -->
                                <div class="flex items-center space-x-2 mb-4">
                                    <span class="p-1.5 bg-amber-50 rounded-xl text-amber-500 font-bold text-lg">💡</span>
                                    <h3 class="font-extrabold text-lg text-gray-800">인스타툰 아이디어 기획</h3>
                                </div>
                                
                                <!-- 내용 스크롤 본문 바디 (마크다운 파싱 지원) -->
                                <div class="prose max-w-none text-gray-700 leading-relaxed max-h-[500px] overflow-y-auto p-1 custom-scrollbar text-xs">
                                    ${typeof marked !== 'undefined' ? marked.parse(text) : `<div class="whitespace-pre-line">${text}</div>`}
                                </div>
                            </div>
                        </div>
                    `;
                } else {
                    console.log("here3");
                    chatbotBubbleHTML = `
                        <div class="bg-[#FFEFE0] text-gray-800 text-sm px-4 py-2.5 rounded-2xl rounded-tl-none shadow-sm whitespace-pre-line leading-relaxed">
                            ${text}
                        </div>
                    `;
                }
            }

            messageRow.innerHTML = `
                <div class="flex-shrink-0 w-9 h-9 rounded-full bg-[#FF4D4D] text-white font-extrabold text-xs flex items-center justify-center shadow-sm">
                    🤖
                </div>
                <div class="flex flex-col space-y-1 max-w-[85%] pl-1">
                    <span class="text-xs font-bold text-gray-600">AI 툰 메이커</span>
                    <div class="flex items-end space-x-2">
                        ${chatbotBubbleHTML}
                        <span class="text-[10px] text-gray-400 select-none pb-1">오후 6:40</span>
                    </div>
                </div>
            `;
            chatContainer.appendChild(messageRow);
        }
    });

    chatContainer.scrollTop = chatContainer.scrollHeight;
}

function switchScenarioInBubble(scenarioIdx) {
    const buttons = document.querySelectorAll(".scenario-btn");
    buttons.forEach((btn, idx) => {
        if (idx === scenarioIdx) {
            btn.className = "scenario-btn p-2 border rounded-lg bg-indigo-50 border-indigo-500 text-indigo-600 font-bold text-xs shadow-sm";
        } else {
            btn.className = "scenario-btn p-2 border rounded-lg bg-white border-gray-200 text-gray-700 hover:bg-indigo-50/50 hover:border-indigo-300 transition text-center font-bold text-xs shadow-sm";
        }
    });

    const activeScenario = currentScenariosForRender[scenarioIdx];
    if (!activeScenario) return;

    document.getElementById("activeScenarioTitle").innerText = `🎯 ${activeScenario.versionName}`;

    const cutsListDiv = document.getElementById("activeCutsList");
    let cutsHTML = "";
    
    activeScenario.cuts.forEach(cut => {
        cutsHTML += `
            <div class="p-3 bg-gray-50 rounded-lg border border-gray-200 flex items-start justify-between">
                <div class="text-xs text-gray-700 space-y-1 pr-4">
                    <div class="flex items-center mb-1">
                        <span class="w-5 h-5 bg-indigo-500 text-white rounded-full flex items-center justify-center text-[10px] font-bold mr-2">${cut.cutIndex}컷</span>
                        <span class="text-[10px] text-indigo-500 font-semibold">[${activeScenario.versionName.split(' ')[0]}]</span>
                    </div>
                    <p class="ml-7 text-gray-950 font-semibold">대사: "${cut.content}"</p>
                    <p class="text-[11px] text-gray-500 ml-7">연출: ${cut.sceneDescription}</p>
                </div>
                <div class="pt-0.5">
                    <!-- name="cut_select_${cut.cutIndex}"를 통해 컷별 고유 그룹 지정 -->
                    <input type="radio" 
                           name="cut_select_${cut.cutIndex}" 
                           class="w-4 h-4 text-indigo-600 border-gray-300 focus:ring-indigo-500 cursor-pointer" 
                           onclick="trackCutSelection(${cut.cutIndex}, '${activeScenario.versionName}', \`${cut.content}\`, \`${cut.sceneDescription}\`)">
                </div>
            </div>
        `;
    });
    
    cutsListDiv.innerHTML = cutsHTML;
}

document.addEventListener("DOMContentLoaded", function () {
    const roomItems = document.querySelectorAll('.room-item');
    
    if (roomItems.length > 0) {
        const firstRoom = roomItems[0];
        const firstRoomId = firstRoom.getAttribute('data-room-id');
        switchChatRoom(firstRoomId, firstRoom);
    }
    
    roomItems.forEach(item => {
        item.addEventListener('click', function () {
            const roomId = this.getAttribute('data-room-id');
            switchChatRoom(roomId, this);
        });
    });
});