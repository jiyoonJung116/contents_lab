function openChatHistoryModal(roomId, roomName, roomDesc) {
    const modal = document.getElementById('chatHistoryModal');
    const titleElem = document.getElementById('modalRoomTitle');
    const descElem = document.getElementById('modalRoomDesc');
    const messageContainer = document.getElementById('chatMessageList');

    titleElem.innerText = roomName || '채팅 내역';
    descElem.innerText = roomDesc && roomDesc !== 'null' ? roomDesc : '설명이 없습니다.';
    modal.style.display = 'flex';

    messageContainer.innerHTML = '<div style="text-align:center; color:#94a3b8; padding: 40px 0;">대화 내역을 불러오는 중...</div>';

    fetch(`/admin/chat/messages?roomId=${roomId}`)
        .then(response => response.json())
        .then(messages => {
            renderChatMessages(messages);
        })
        .catch(error => {
            console.error('Error fetching chat messages:', error);
            messageContainer.innerHTML = '<div style="text-align:center; color:#ef4444; padding: 40px 0;">메시지를 불러오는 데 실패했습니다.</div>';
        });
}

function renderChatMessages(messages) {
    const messageContainer = document.getElementById('chatMessageList');
    messageContainer.innerHTML = '';

    if (!messages || messages.length === 0) {
        messageContainer.innerHTML = '<div style="text-align:center; color:#94a3b8; padding: 40px 0;">대화 내역이 없습니다.</div>';
        return;
    }

    messages.forEach(msg => {
        const isUser = msg.sender_type === 'USER' || msg.SENDER_TYPE === 'USER' || msg.sender_type === 'user';
        
        let content = msg.content || msg.CONTENT || '';
        const createDate = msg.createDate || msg.CREATEDATE || msg.CREATE_DATE || '';
        const imageUrl = msg.image_url || msg.IMAGE_URL || null;

        content = content.replace(/(data:image\/[a-zA-Z]+;base64,[^\s]+)/g, '<img src="$1" style="max-width:100%; border-radius:8px; margin-top:6px;" alt="이미지">');

        const imageHtml = imageUrl 
            ? `<div style="margin-top: 8px;"><img src="${imageUrl}" style="max-width: 100%; border-radius: 8px;" alt="생성된 이미지"></div>` 
            : '';

        const bubbleHtml = `
            <div class="chat-bubble-wrap ${isUser ? 'user' : 'ai'}">
                <div class="chat-bubble-row">
                    ${!isUser ? '<div class="ai-avatar"><i class="fa-solid fa-robot"></i></div>' : ''}
                    <div class="chat-message-content">
                        ${content}
                        ${imageHtml}
                    </div>
                    <span class="chat-message-time">${createDate}</span>
                </div>
            </div>
        `;
        messageContainer.insertAdjacentHTML('beforeend', bubbleHtml);
    });

    messageContainer.scrollTop = messageContainer.scrollHeight;
}

function closeChatHistoryModal() {
    document.getElementById('chatHistoryModal').style.display = 'none';
}

window.addEventListener('click', function(event) {
    const modal = document.getElementById('chatHistoryModal');
    if (event.target === modal) {
        closeChatHistoryModal();
    }
});