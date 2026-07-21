async function generate() {
    const prompt = document.getElementById("prompt").value;

    if (!prompt) {
        alert("주제를 입력해주세요.");
        return;
    }

    document.getElementById("loading").innerText = "AI가 콘텐츠를 생성 중입니다...";
    document.getElementById("result").innerHTML = "";
    document.getElementById("carouselContainer").innerHTML = "";

    try {
        const response = await fetch("/api/generate", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                prompt: prompt
            })
        });

        const data = await response.json();

        document.getElementById("loading").innerText = "";

        await renderContent(data.content);

    } catch (e) {
        document.getElementById("loading").innerText = "";
        alert("생성 중 오류가 발생했습니다.");
    }
}

async function renderContent(content) {
    document.getElementById("result").innerHTML = marked.parse(content);

    await makeCarousel(content);
}


// 이미지 프롬프트 생성
function createImagePrompt(section) {

    // 캐릭터 추출
    const character = section.match(/캐릭터:\s*(.*)/)?.[1] ?? "";

    // 배경 추출
    const background = section.match(/배경:\s*(.*)/)?.[1] ?? "";

    return `
            cute korean instagram comic style,
            young korean office worker in 20s,
            ${character},
            ${background},

            flat illustration,
            pastel colors,
            kawaii character design,
            webtoon style,
            instagram carousel illustration,

            no text,
            no words,
            no letters,
            no watermark,
            no logo,
            high quality
            `;

}


async function makeCarousel(content) {
    const container = document.getElementById("carouselContainer");

    container.innerHTML = "";

    const sections = content.split(/\(\d+페이지:/).filter(section => section.trim() !== "");

    for (let i = 0; i < sections.length; i++) {

        const section = sections[i];
        const card = document.createElement("div");

        card.className = "carousel-card";

        card.innerHTML = `
            <h3>${i + 1}페이지</h3>
            <div>${marked.parse(section)}</div>
            <p>이미지 생성 중...</p>
        `;

        container.appendChild(card);

        try {
            const imagePrompt = createImagePrompt(section);
            const response = await fetch("/api/image", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    prompt: imagePrompt
                })
            });

            if (!response.ok) {
                throw new Error(
                    "이미지 생성 실패 : " + response.status
                );
            }

            const imageBase64 = await response.text();
            const img = document.createElement("img");

            img.src = "data:image/png;base64," + imageBase64;
            img.style.width = "100%";
            img.style.marginTop = "20px";
            img.style.borderRadius = "12px";

            card.querySelector("p").remove();
            card.appendChild(img);

        } catch (e) {
            card.querySelector("p").innerText = "이미지 생성 실패";
        }
    }
}