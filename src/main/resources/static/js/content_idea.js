document.addEventListener('DOMContentLoaded', () => {
    document.addEventListener('click', async (e) => {
        const copyBtn = e.target.closest('.btn-copy');
        
        if (copyBtn) {
            const textToCopy = copyBtn.getAttribute('data-prompt');

            if (!textToCopy) {
                alert('복사할 프롬프트 내용이 없습니다.');
                return;
            }

            try {
                // 클립보드 복사
                await navigator.clipboard.writeText(textToCopy);

                const originalHTML = copyBtn.innerHTML;
                copyBtn.innerHTML = '<span class="material-symbols-outlined">check</span> 복사됨!';
                copyBtn.style.backgroundColor = '#10b981'; 

                setTimeout(() => {
                    copyBtn.innerHTML = originalHTML;
                    copyBtn.style.backgroundColor = '';
                }, 1500);

            } catch (err) {
                console.error('복사 실패:', err);
                const textarea = document.createElement('textarea');
                textarea.value = textToCopy;
                document.body.appendChild(textarea);
                textarea.select();
                document.execCommand('copy');
                document.body.removeChild(textarea);
                alert('프롬프트가 복사되었습니다!');
            }
        }
    });

    document.querySelectorAll('.btn-bookmark-view').forEach(button => {
        button.addEventListener('click', async () => {
            const templateId = button.getAttribute('data-template-id');

            if (!templateId) {
                alert('북마크 대상 정보(ID)를 찾을 수 없습니다.');
                return;
            }

            const formData = new URLSearchParams();
            formData.append('targetId', templateId);

            try {
                const response = await fetch('/api/bookmark/save', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: formData
                });

                const result = await response.json();

                if (result.status === 'success') {
                    button.innerHTML = '<span class="material-symbols-outlined" style="color: #4CAF50;">bookmark_added</span> 저장됨';
                    button.style.backgroundColor = '#e8f5e9';
                    alert('북마크에 저장되었습니다!');
                } else {
                    alert(result.message || '북마크 저장 중 오류가 발생했습니다.');
                }

            } catch (error) {
                console.error('북마크 요청 실패:', error);
                alert('서버와 통신 중 문제가 발생했습니다.');
            }
        });
    });

    document.addEventListener('click', function (e) {
        const deleteBtn = e.target.closest('.btn-delete-bookmark');
        
        if (deleteBtn) {
            const bookmarkId = deleteBtn.getAttribute('data-bookmark-id');

            if (!bookmarkId) {
                alert('북마크 ID가 유효하지 않습니다.');
                return;
            }

            if (confirm('정말 이 북마크를 삭제하시겠습니까?')) {
                deleteBookmark(bookmarkId, deleteBtn);
            }
        }
    });
});

function deleteBookmark(bookmarkId, buttonElement) {
    const formData = new URLSearchParams();
    formData.append('bookmarkId', bookmarkId);

    fetch('/api/bookmark/delete', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: formData
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('네트워크 응답에 문제가 있습니다.');
        }
        return response.json();
    })
    .then(data => {
        if (data.status === 'success') {
            alert('북마크가 삭제되었습니다.');
            
            location.reload();

            /* 💡 [방법 2] 새로고침 없이 화면에서 해당 테이블 행(tr)만 지우고 싶다면 위 location.reload() 대신 아래 코드 사용
            const row = buttonElement.closest('tr');
            if (row) {
                row.remove();
            }
            */
        } else {
            alert(data.message || '북마크 삭제에 실패했습니다.');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('북마크 삭제 중 오류가 발생했습니다.');
    });
}